package me.spaff.anari.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemBuilder {
    private final ItemStack item;
    public ItemBuilder(Builder builder) {
        this.item = builder.item;
    }

    public ItemStack getItem() {
        return item;
    }

    public static class Builder {
        private final ItemStack item;

        public Builder(Material material) {
            this.item = new ItemStack(material);
        }

        public Builder name(String text) {
            ItemUtils.setName(item, text);
            return this;
        }

        public Builder lore(String lore) {
            ItemUtils.setLore(item, lore);
            return this;
        }

        public Builder lore(List<String> lore) {
            ItemUtils.setLore(item, lore);
            return this;
        }

        public Builder flags(ItemFlag flags) {
            ItemUtils.addFlags(item, flags);
            return this;
        }

        public ItemBuilder build() {
            return new ItemBuilder(this);
        }
    }
}