package me.spaff.anari.item;

import me.spaff.anari.Constants;
import me.spaff.anari.Main;
import me.spaff.anari.item.component.DecriptionComponent;
import me.spaff.anari.item.component.DurabilityComponent;
import me.spaff.anari.item.component.ItemAttributeComponent;
import me.spaff.anari.item.rarity.AnariRarity;
import me.spaff.anari.utils.ItemUtils;
import me.spaff.anari.utils.StringUtils;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AnariItem {
    private Builder builder;

    private UUID playerUUID;

    private final ItemStack icon;
    private final AnariID id;

    private final String name;
    private final List<String> lore = new ArrayList<>();
    private final AnariRarity rarity;

    private final AnariItemType itemType;

    // Components
    private DecriptionComponent decription;
    private ItemAttributeComponent[] itemAttribute;
    private DurabilityComponent durability;

    private AnariItem(Builder builder) {
        this.builder = builder;
        this.icon = builder.icon;
        this.id = builder.id;
        this.name = builder.name;
        this.rarity = builder.rarity;
        this.itemType = builder.itemType;
        this.playerUUID = builder.playerUUID;

        // Components
        this.decription = builder.description; // TODO: Update description component to utilize wrapped text
        this.itemAttribute = builder.itemAttribute;
        this.durability = builder.durability;

        // Initialize
        init();
    }

    // Initializers
    private void init() {
        // NOTE: Make sure they are initialized in a correct order,
        // so the buildLore() function can build it properly.
        lore.clear();

        initId();
        initName();

        initItemType();
        initDescription();

        applyDurability();
        applyAttributes();

        buildLore();
    }

    private void initId() {
        ItemUtils.setPersistentData(icon, Constants.NAMESPACE, Constants.ID_NAMESPACE_KEY, PersistentDataType.STRING, id.toString());
    }

    private void initName() {
        ItemUtils.setName(icon, rarity.getColor() + name);
    }

    private void initItemType() {
        lore.add(StringUtils.getHexColor("#707070") + itemType.getTypeName());
        //if (decription != null)
        //    lore.add("");
    }

    private void initDescription() {
        if (decription == null) return;
        lore.addAll(decription.getDescription());

        // TODO: Add automatic line breaker when line is too long
    }

    private void applyAttributes() {
        if (itemAttribute == null) return;

        ItemMeta meta = this.icon.getItemMeta();
        meta.setAttributeModifiers(null);

        for (ItemAttributeComponent itemAttributeComponent : itemAttribute) {
            AttributeModifier attributeModifier = new AttributeModifier(
                    new NamespacedKey(Main.getInstance(), UUID.randomUUID().toString()),
                    itemAttributeComponent.getAmount(),
                    itemAttributeComponent.getOperation(),
                    itemAttributeComponent.getSlot()
            );

            meta.addAttributeModifier(itemAttributeComponent.getAttribute(), attributeModifier);
        }

        this.icon.setItemMeta(meta);
    }

    private void applyDurability() {
        if (durability == null) return;

        ItemMeta meta = this.icon.getItemMeta();
        Damageable damageable = (Damageable) meta;
        if (damageable == null) return;

        damageable.setMaxDamage(durability.getMaxDurability());
        damageable.setDamage(durability.getDamage());

        this.icon.setItemMeta(meta);
    }

    private void buildLore() {
        ItemUtils.setLore(icon, lore);
    }

    // Setters

    public void setPlayerUUID(Player player) {
        if (player == null) return;
        this.playerUUID = player.getUniqueId();
    }

    // Getters

    public AnariID getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public AnariRarity getRarity() {
        return this.rarity;
    }

    public AnariItemType getItemType() {
        return this.itemType;
    }

    public boolean isAccessory() {
        if (itemType.equals(AnariItemType.ACCESSORY))
            return true;
        return false;
    }

    //

    // This function is used to update anari items, so that they don't
    // display outdated information like names or description when they are changed.

    // TODO: When updating item carry over data like progress or kill count
    //  to a new updated item
    public static ItemStack update(ItemStack item, Player player) {
        if (ItemUtils.isNull(item)) return null;
        if (!isAnariItem(item) && !shouldOverrideVanilla(item)) return null;

        boolean isOverridingVanilla = false;
        if (shouldOverrideVanilla(item))
            isOverridingVanilla = true;

        if (hasInvalidId(item))
            return new ItemStack(Material.AIR);

        ItemMeta itemMeta = item.getItemMeta();
        Damageable damageable = (Damageable) itemMeta;

        int amount = item.getAmount();
        int durability = -1;

        if (damageable != null)
            durability = damageable.getDamage();

        AnariID itemId = isOverridingVanilla ? AnariID.valueOf(item.getType().toString()) : getId(item);

        AnariItem newInstance = AnariItems.getInstance(itemId);
        newInstance.setPlayerUUID(player);
        newInstance.init();

        ItemStack updatedItem = newInstance.getItemStack().clone();
        ItemMeta updatedItemMeta = updatedItem.getItemMeta();
        Damageable updatedItemDamageable = (Damageable) updatedItemMeta;

        if (updatedItemDamageable != null && durability != 0) {
            updatedItemDamageable.setDamage(durability);
            updatedItem.setItemMeta(updatedItemMeta);
        }

        updatedItem.setAmount(amount);

        return updatedItem;
    }

    public static ItemStack update(ItemStack item) {
        return update(item, null);
    }

    public static boolean shouldOverrideVanilla(ItemStack item) {
        String vanillaId = item.getType().toString();
        if (EnumUtils.isValidEnum(AnariID.class, vanillaId))
            return true;

        return false;
    }

    public static boolean isAnariItem(ItemStack item) {
        if (ItemUtils.isNull(item)) return false;
        return getId(item) != null;
    }

    public static AnariID getId(ItemStack item) {
        if (ItemUtils.isNull(item)) return null;

        String itemId = (String) ItemUtils.getPersistentData(item, Constants.NAMESPACE, Constants.ID_NAMESPACE_KEY, PersistentDataType.STRING);
        if (itemId == null || itemId.isEmpty()) return null;
        if (!EnumUtils.isValidEnum(AnariID.class, itemId)) return null;

        return AnariID.valueOf(itemId);
    }

    public static boolean hasInvalidId(ItemStack item) {
        if (ItemUtils.isNull(item)) return false;
        if (!isAnariItem(item)) return false;

        if (getId(item) == null)
            return true;
        return false;
    }

    public ItemStack getItemStack() {
        return icon;
    }

    public AnariItem clone() {
        return new AnariItem(builder);
    }

    public static class Builder {
        private final UUID playerUUID;

        private final ItemStack icon;
        private final AnariID id;

        private final String name;
        private final AnariRarity rarity;
        private final AnariItemType itemType;

        // Components
        private DecriptionComponent description;
        private ItemAttributeComponent[] itemAttribute;
        private DurabilityComponent durability;

        public Builder(ItemStack icon, AnariID id, AnariItemType type, String name, AnariRarity rarity, UUID playerUUID) {
            this.playerUUID = playerUUID;
            this.icon = icon;
            this.id = id;
            this.name = name;
            this.rarity = rarity;
            this.itemType = type;
        }

        public Builder decription(List<String> description) {
            this.description = new DecriptionComponent(description);
            return this;
        }

        public Builder attribute(ItemAttributeComponent[] itemAttributes) {
            this.itemAttribute = itemAttributes;
            return this;
        }

        public Builder durability(int damage, int maxDurability) {
            this.durability = new DurabilityComponent(damage, maxDurability);
            return this;
        }

        public Builder durability(int maxDurability) {
            this.durability = new DurabilityComponent(0, maxDurability);
            return this;
        }

        public AnariItem build() {
            return new AnariItem(this);
        }
    }
}
