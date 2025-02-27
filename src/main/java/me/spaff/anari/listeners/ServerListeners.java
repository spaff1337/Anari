package me.spaff.anari.listeners;

import me.spaff.anari.Main;
import me.spaff.anari.indicator.DamageIndicator;
import org.bukkit.ExplosionResult;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Iterator;
import java.util.Random;

public class ServerListeners implements Listener {
    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent e) {
        Entity entity = e.getEntity();

        if (entity instanceof LivingEntity) {
            new DamageIndicator(e.getFinalDamage(), entity.getLocation().add(0, 0.5, 0)).spawn();
        }
        else if (entity instanceof Item) {
            if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) ||
                    e.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
        Entity entity = e.getEntity();

        if (entity instanceof LivingEntity) {
            new DamageIndicator(e.getFinalDamage(), entity.getLocation().add(0, 1, 0)).spawn();
            
            if (e.getDamager().getType().equals(EntityType.PLAYER)) {
                Player player = (Player) e.getDamager();
                ItemStack useItem = player.getInventory().getItemInMainHand();

                if (useItem != null && useItem.getType().equals(Material.MACE)) {
                    // I don't know whose idea was to not implement a damage cap
                    // for this, and just allow players to deal thousands of damage
                    e.setDamage(Math.clamp(e.getDamage(), 0, 51)); // 51 so player can get the achievement
                }
            }
        }
        else if (entity instanceof Item) {
            if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) ||
                    e.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent e) {
        LivingEntity entity = e.getEntity();

        if (e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER)) {
            entity.setMetadata("noExpDrop", new FixedMetadataValue(Main.getInstance(), true));
        }
    }

    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();

        if (!entity.getMetadata("noExpDrop").isEmpty() &&
                entity.getMetadata("noExpDrop").getFirst().asBoolean()) {
            e.setDroppedExp(0);
        }

        if (entity instanceof Evoker) {
            e.getDrops().remove(new ItemStack(Material.TOTEM_OF_UNDYING));
            if (new Random().nextInt(20) == 0) // 5%
                e.getDrops().add(new ItemStack(Material.TOTEM_OF_UNDYING));
        }
    }

    @EventHandler
    public void onEntityExplodeEvent(EntityExplodeEvent e) {
        if (e.blockList().isEmpty()) return;
        if (e.getEntity().getType().equals(EntityType.CREEPER)) {
            e.blockList().clear();
            return;
        }

        Iterator<Block> blocksIterator = e.blockList().iterator();
        while (blocksIterator.hasNext()) {
            Block block = blocksIterator.next();
            if (!block.getType().equals(Material.CHEST)) continue;

            Chest chest = (Chest) block.getState();
            if (!chest.getInventory().isEmpty())
                blocksIterator.remove();
        }
    }
}