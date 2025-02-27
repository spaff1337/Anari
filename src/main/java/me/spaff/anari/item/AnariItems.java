package me.spaff.anari.item;

import me.spaff.anari.AnariColors;
import me.spaff.anari.item.component.ItemAttributeComponent;
import me.spaff.anari.item.rarity.AnariRarity;
import me.spaff.anari.utils.logger.ALevel;
import me.spaff.anari.utils.logger.ALogger;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnariItems {
    private static final Map<AnariID, AnariItem> registeredItems = new HashMap<>();

    private static void registerItem(AnariID id, AnariItem item) {
        if (registeredItems.containsKey(id) || registeredItems.containsValue(item)) return;
        registeredItems.put(id, item);
    }

    // TODO: Instead of registering new instances do something else

    public static void registerItems() {
        // Items
        ALogger.log(ALevel.INFO, "Registering items...");

        registerItem(AnariID.TEST, new AnariItem.
                Builder(new ItemStack(Material.DIAMOND), AnariID.TEST, AnariItemType.ACCESSORY,
                "Test", AnariRarity.UNCOMMON, null)
                .decription(Arrays.asList("&7DXXXXXDXDDXXDDX", "&eDanielDXXDXDXDXD"))
                .build());

        registerItem(AnariID.TEST_WEAPON, new AnariItem.
                Builder(new ItemStack(Material.WOODEN_SHOVEL), AnariID.TEST_WEAPON, AnariItemType.SWORD,
                "Test Weapon XDDXDX", AnariRarity.RARE, null)
                .attribute(new ItemAttributeComponent[]{
                        new ItemAttributeComponent(Attribute.ATTACK_DAMAGE, 420, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND),
                        new ItemAttributeComponent(Attribute.ATTACK_SPEED, 69, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND),
                        new ItemAttributeComponent(Attribute.ATTACK_KNOCKBACK, 666, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND)
                })
                .build());

        registerItem(AnariID.TEST_HELMET, new AnariItem.
                Builder(new ItemStack(Material.NETHERITE_HELMET), AnariID.TEST_HELMET, AnariItemType.AMOR,
                "Netherite Helmet", AnariRarity.RARE, null)
                .attribute(new ItemAttributeComponent[]{
                        new ItemAttributeComponent(Attribute.ARMOR, 420, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD),
                        new ItemAttributeComponent(Attribute.ARMOR_TOUGHNESS, 69, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD),
                        new ItemAttributeComponent(Attribute.KNOCKBACK_RESISTANCE, 20, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD)
                })
                .build());

        // Vanilla items

        registerItem(AnariID.TOTEM_OF_UNDYING, new AnariItem.
                Builder(new ItemStack(Material.TOTEM_OF_UNDYING), AnariID.TOTEM_OF_UNDYING, AnariItemType.ACCESSORY,
                "Totem of Undying", AnariRarity.RARE, null)
                .decription(List.of(
                        "&7Prevents from dying when taking",
                        "&7fatal damage.",
                        AnariColors.LIGHT_GRAY + "2 minute cooldown."
                ))
                .build());

        registerItem(AnariID.SHIELD, new AnariItem.
                Builder(new ItemStack(Material.SHIELD), AnariID.SHIELD, AnariItemType.OTHER,
                "Shield", AnariRarity.UNCOMMON, null)
                .durability(150)
                .build());

        registerItem(AnariID.ELYTRA, new AnariItem.
                Builder(new ItemStack(Material.ELYTRA), AnariID.ELYTRA, AnariItemType.OTHER,
                "Elytra", AnariRarity.RARE, null)
                .durability(200)
                .build());
    }

    public static AnariItem getInstance(AnariID id) {
        return registeredItems.get(id).clone();
    }

    public static ItemStack getItem(AnariID id) {
        return registeredItems.get(id).getItemStack().clone();
    }
}
