package me.spaff.anari.menu;

import me.spaff.anari.item.AnariItem;
import me.spaff.anari.item.AnariItems;
import me.spaff.anari.player.AnariPlayer;
import me.spaff.anari.player.AnariPlayerManager;
import me.spaff.anari.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class AnariMenu extends PlayerMenu {
    private final int[] ACCESSORY_SLOTS = new int[]{14, 23, 32, 41, 15, 24};

    public AnariMenu(Player player) {
        super(player, "Anari Menu", 6);

        this.fillWith(new ItemBuilder.Builder(Material.BLACK_STAINED_GLASS_PANE)
                .name("&7")
                .build()
                .getItem()
        );

        this.setItem(20, new ItemBuilder.Builder(Material.GOLDEN_PICKAXE)
                .name("&eSkills")
                .flags(ItemFlag.HIDE_ATTRIBUTES)
                .lore("&7Click to view information about your skill progression.")
                .build()
                .getItem()
        );

        int index = 1;
        for (int slot : ACCESSORY_SLOTS) {
            AnariItem accessory = AnariPlayerManager.getPlayer(player).getAccessory(index);
            if (accessory != null) {
                this.setItem(slot, accessory.getItemStack());
            }
            else {
                this.setItem(slot, new ItemBuilder.Builder(Material.LIGHT_GRAY_STAINED_GLASS_PANE)
                        .name("&eAccessory Slot #" + index)
                        .lore("&7Click at any accessory in your inventory to equip it.")
                        .build()
                        .getItem()
                );
            }

            index++;
        }

        this.register();
        this.open();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (e.getClickedInventory() == null) return;

        Inventory clickedInventory = e.getClickedInventory();
        Inventory inventory = this.getInventory();

        AnariPlayer anariPlayer = AnariPlayerManager.getPlayer(player);

        if (clickedInventory.equals(inventory)) {
            e.setCancelled(true);

            // Skills Menu
            if (e.getSlot() == 20) {
                new SkillsMenu(player);
                return;
            }

            // UnEquipping
            int clickedSlot = e.getSlot();
            int index = 1;
            for (int slot : ACCESSORY_SLOTS) {
                if (clickedSlot == slot) {
                    AnariItem item = anariPlayer.unEquipAccessory(index);
                    if (item != null)
                        player.getInventory().addItem(item.getItemStack());
                    // TODO: Check for full inventory
                }
                index++;
            }

            new AnariMenu(player);
        }
        else if (clickedInventory.equals(player.getInventory()) &&
                player.getOpenInventory().getTopInventory().equals(inventory)) {
            e.setCancelled(true);

            // Equipping from inventory
            ItemStack clickedItem = e.getCurrentItem();
            if (!AnariItem.isAnariItem(clickedItem)) return;

            AnariItem item = AnariItems.getInstance(AnariItem.getId(clickedItem));
            if (!anariPlayer.canEquip(item.getId())) return;

            e.setCurrentItem(null);
            anariPlayer.equipAccessory(item);

            new AnariMenu(player);
        }
    }
}