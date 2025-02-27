package me.spaff.anari.listeners;

import me.spaff.anari.Main;
import me.spaff.anari.item.AnariID;
import me.spaff.anari.item.AnariItem;
import me.spaff.anari.menu.AnariMenu;
import me.spaff.anari.nms.NPC;
import me.spaff.anari.nms.PacketReader;
import me.spaff.anari.player.AnariPlayerManager;
import me.spaff.anari.utils.ItemUtils;
import net.minecraft.world.inventory.InventoryMenu;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R3.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockReceiveGameEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListeners implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        PacketReader.injectPlayer(player);
        AnariPlayerManager.registerPlayer(player);
        AnariPlayerManager.getPlayer(player).updateInventory();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        PacketReader.uninjectPlayer(player);
        AnariPlayerManager.unregisterPlayer(player);
    }

    /*@EventHandler
    public void onBlockReceiveGameEvent(BlockReceiveGameEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();

        if (e.getEvent().equals(GameEvent.SHRIEK)) {

        }
    }*/

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();
        EntityDamageEvent.DamageCause damageCause = e.getCause();

        // Totem effect
        if (e.getFinalDamage() >= player.getHealth()) {
            GameMode gameMode = player.getGameMode();

            if (!gameMode.equals(GameMode.SURVIVAL) && !gameMode.equals(GameMode.ADVENTURE)) return;
            if (!AnariPlayerManager.getPlayer(player).hasEquipped(AnariID.TOTEM_OF_UNDYING)) return;
            if (damageCause.equals(EntityDamageEvent.DamageCause.VOID) || damageCause.equals(EntityDamageEvent.DamageCause.KILL)) return;

            e.setCancelled(true);
            player.setHealth(0.5D);
            player.damage(0.01D); // Make player take a bit of damage so it looks like they took damage

            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*45, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20*40, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20*5, 1));

            player.playEffect(EntityEffect.TOTEM_RESURRECT);
        }

        ItemStack useItem = player.getItemInUse();

        // Change shield behaviour
        double finalDamage = e.getFinalDamage();
        if (useItem != null && useItem.getType().equals(Material.SHIELD)) {
            // Player blocked with a shield
            if (finalDamage == 0.0D) {
                if (damageCause.equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) ||
                        damageCause.equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
                    // Can't really override the final damage so i just make the shield
                    // take massive damage to durability
                    ItemUtils.setDamage(useItem, (int)(e.getDamage() * 10));
                }

                int cooldownTimeTicks = e.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE) ? 20 : 40;
                player.setCooldown(useItem, cooldownTimeTicks);
                // TODO: Fix item ignoring cooldown when held down
            }
        }
    }

    @EventHandler
    public void onEntityResurrectEvent(EntityResurrectEvent e) {
        LivingEntity entity = e.getEntity();
        Location entityLocation = entity.getLocation();

        e.setCancelled(true);

        // Drop items from hands because for some reason when cancelling the event
        // and dying doesn't do that automatically
        EquipmentSlot[] hands = new EquipmentSlot[]{EquipmentSlot.OFF_HAND, EquipmentSlot.HAND};
        for (EquipmentSlot hand : hands) {
            entityLocation.getWorld().dropItem(entityLocation, entity.getEquipment().getItem(hand));
            entity.getEquipment().setItem(hand, new ItemStack(Material.AIR));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (e.getClickedInventory() == null) return;

        Inventory clickedInventory = e.getClickedInventory();
        InventoryMenu craftingMenu = (((CraftPlayer) player).getHandle()).inventoryMenu;

        //System.out.println("slot: " + e.getSlot());

        if (e.getSlot() == 0 && clickedInventory.getType().equals(InventoryType.CRAFTING)) {
            ItemStack resultItem = CraftItemStack.asBukkitCopy(craftingMenu.getResultSlot().getItem());
            if (!resultItem.getType().equals(Material.AIR)) return;

            e.setCancelled(true);

            // If we don't close it and reopen after 1 tick next time player
            // opens his inventory they will have the menu button on their cursor
            player.closeInventory();
            new BukkitRunnable() {
                @Override
                public void run() {
                    new AnariMenu(player);
                }
            }.runTaskLater(Main.getInstance(), 1);
        }
    }

    @EventHandler
    public void onPlayerItemHeldEvent(PlayerItemHeldEvent e) {
        Player player = e.getPlayer();

        new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack newSlotItem = player.getInventory().getItem(e.getNewSlot());

                if (!ItemUtils.isNull(newSlotItem)) {
                    ItemStack updatedItem = AnariItem.update(newSlotItem, player);

                    if (updatedItem != null)
                        player.getInventory().setItem(e.getNewSlot(), updatedItem);
                }
            }
        }.runTaskLater(Main.getInstance(), 5);
    }

    @EventHandler
    public void onEntityPickupItemEvent(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();

        ItemStack newItem = AnariItem.update(e.getItem().getItemStack(), player);

        if (newItem != null)
            e.getItem().setItemStack(newItem);
    }
}