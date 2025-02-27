package me.spaff.anari.menu;

import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Menu {
    private final String title;
    private final int rows;

    private final Inventory inventory;

    public Menu(String title, int rows) {
        this.title = title;
        this.rows = Math.clamp(rows, 1, 6);

        this.inventory = Bukkit.createInventory(null, rows * 9, title);
    }

    public void setItem(int slot, ItemStack item) {
        Validate.notNull(item, "Item cannot be null!");
        slot = Math.max(0, Math.min(slot, 54));
        this.inventory.setItem(slot, item);
    }

    public ItemStack getItem(int slot) {
        slot = Math.max(0, Math.min(slot, 54));
        return this.inventory.getItem(slot);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getRows() {
        return rows;
    }

    public String getTitle() {
        return title;
    }
}
