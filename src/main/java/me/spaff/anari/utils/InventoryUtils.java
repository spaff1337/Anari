package me.spaff.anari.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {
    public static boolean isInventoryFull(Inventory inv) {
        int inventorySize = inv.getSize();
        ItemStack[] contents = inv.getContents();
        ItemStack[] var3 = contents;
        int var4 = contents.length;

        int var5;
        ItemStack item;
        for (var5 = 0; var5 < var4; ++var5) {
            item = var3[var5];
            if (item == null || item.getAmount() == 0) {
                return false;
            }
        }

        var3 = contents;
        var4 = contents.length;

        for (var5 = 0; var5 < var4; ++var5) {
            item = var3[var5];
            if (item != null && item.getAmount() < item.getMaxStackSize()) {
                return false;
            }
        }

        return true;
    }

    public static boolean canFitItem(Inventory inv, ItemStack item) {
        ItemStack itemClone = item.clone();
        ItemStack[] var3 = inv.getContents();
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            ItemStack stack = var3[var5];
            if (ItemUtils.isNull(item)) {
                return true;
            }

            if (stack.isSimilar(itemClone) && stack.getAmount() + itemClone.getAmount() <= stack.getMaxStackSize()) {
                return true;
            }
        }

        return false;
    }

    public static boolean canFitItem(Player player, ItemStack item) {
        if (player.getInventory().firstEmpty() != -1) {
            return true;
        }
        else {
            for(int i = 0; i < player.getInventory().getSize(); ++i) {
                ItemStack stack = player.getInventory().getItem(i);
                if (!ItemUtils.isNull(stack) && stack.isSimilar(item)) {
                    int stackAmount = stack.getAmount();
                    int itemAmount = item.getAmount();
                    if (itemAmount + stackAmount <= item.getMaxStackSize()) {
                        return true;
                    }
                }
            }

            return false;
        }
    }
}
