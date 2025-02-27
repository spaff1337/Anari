package me.spaff.anari.utils;

import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_21_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class ItemUtils {
    public static ItemStack setName(ItemStack item, String text) {
        Validate.notNull(item, "Item cannot be null!");
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName(StringUtils.getColoredText(text));
        item.setItemMeta(itemMeta);

        return item;
    }

    public static ItemStack setLore(ItemStack item, String lore) {
        return setLore(item, List.of(lore));
    }

    public static ItemStack setLore(ItemStack item, List<String> lore) {
        Validate.notNull(item, "Item cannot be null!");
        Validate.notNull(item.getItemMeta(), "ItemMeta cannot be null!");
        ItemMeta itemMeta = item.getItemMeta();

        List<String> finalLore = new ArrayList<>();
        for (String text : lore) {
            text = StringUtils.wrapString(text);

            if (text.contains("\n")) {
                String[] split = text.split("\n");

                int index = 0;
                for (String s : split) {
                    if (!s.startsWith("&") && index > 0) {
                        // Get previous line
                        String previousLine = split[index - 1];

                        // Leave just color codes and get the last one
                        String[] words = previousLine.split(" ");
                        List<String> colors = new ArrayList<>();
                        for (String word : words) {
                            if (word.startsWith("&"))
                                colors.add(word.substring(0, 2));
                        }

                        if (!colors.isEmpty()) {
                            String previousColor = colors.getLast();
                            s = previousColor + s;
                        }
                    }

                    finalLore.add(StringUtils.getColoredText(s));
                    index++;
                }
            }
            else
                finalLore.add(StringUtils.getColoredText(text));
        }

        itemMeta.setLore(finalLore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public static ItemStack addFlags(ItemStack item, ItemFlag... flags) {
        Validate.notNull(item, "Item cannot be null!");
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.addItemFlags(flags);
        item.setItemMeta(itemMeta);

        return item;
    }

    public static ItemStack setDamage(ItemStack item, int damage) {
        Validate.notNull(item, "Item cannot be null!");
        ItemMeta itemMeta = item.getItemMeta();
        Damageable damageable = (Damageable) itemMeta;
        if (damageable == null) return item;

        damageable.setDamage(damageable.getDamage() + damage);
        item.setItemMeta(itemMeta);

        return item;
    }

    // Persistent Data

    public static void setPersistentData(ItemStack item, String namespace, String key, PersistentDataType dataType, Object value) {
        Validate.notNull(item, "Item cannot be null!");
        ItemMeta meta = item.getItemMeta();

        NamespacedKey k = new NamespacedKey(namespace, key);
        meta.getPersistentDataContainer().set(k, dataType, value);

        item.setItemMeta(meta);
    }

    public static Object getPersistentData(ItemStack item, String namespace, String key, PersistentDataType dataType) {
        Validate.notNull(item, "Item cannot be null!");
        ItemMeta meta = item.getItemMeta();

        NamespacedKey k = new NamespacedKey(namespace, key);

        return meta.getPersistentDataContainer().get(k, dataType);
    }

    // Other

    public static boolean isNull(ItemStack item) {
        if (item == null || item.getType().equals(Material.AIR))
            return true;
        return false;
    }

    public static String getItemDisplayName(ItemStack item) {
        if (isNull(item)) return "";

        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName())
            return meta.getDisplayName();

        return CraftItemStack.asNMSCopy(item).getItem().getName().getString();
    }

    // Base64

    public static int getItemSizeInBytes(ItemStack item) {
        String base64 = itemStackToBase64(item);
        byte[] decodedBytes = Base64.getDecoder().decode(base64);

        return decodedBytes.length;
    }

    public static ItemStack itemStackFromBase64(String base64) {
        try {
            FastByteArrayInputStream inputStream = new FastByteArrayInputStream(Base64Coder.decodeLines(base64));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack item = (ItemStack)dataInput.readObject();
            dataInput.close();
            return item;
        }
        catch (final Exception exception) {
            throw new IllegalArgumentException(exception);
        }
    }

    public static String itemStackToBase64(ItemStack item) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(item);
            dataOutput.close();
            return new String(Base64Coder.encode(outputStream.toByteArray()));
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    // Skull

    public static ItemStack setSkin(ItemStack head, String base64) {
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        PlayerProfile profile = getProfileBase64(base64);
        meta.setOwnerProfile(profile);

        head.setItemMeta(meta);
        return head;
    }

    public static ItemStack getPlayerHead(String base64) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        PlayerProfile profile = getProfileBase64(base64);
        meta.setOwnerProfile(profile);

        head.setItemMeta(meta);
        return head;
    }

    private static PlayerProfile getProfileBase64(String base64) {
        PlayerProfile profile = Bukkit.createPlayerProfile("6a676a6a6a7an756");
        PlayerTextures textures = profile.getTextures();

        URL urlObject;
        try {
            urlObject = getUrlFromBase64(base64);
        } catch (MalformedURLException exception) {
            throw new RuntimeException("Invalid URL", exception);
        }

        textures.setSkin(urlObject);
        profile.setTextures(textures);
        return profile;
    }

    public static URL getUrlFromBase64(String base64) throws MalformedURLException {
        try {
            String decoded = new String(Base64.getDecoder().decode(base64));
            return new URL(decoded.substring("{\"textures\":{\"SKIN\":{\"url\":\"".length(), decoded.length() - "\"}}}".length()));
        }
        catch (Throwable t) {
            throw new MalformedURLException("Invalid base64 string: " + base64);
        }
    }
}