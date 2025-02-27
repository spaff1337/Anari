package me.spaff.anari.item.component;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;

public class ItemAttributeComponent {
    private final Attribute attribute;
    private final double amount;
    private final AttributeModifier.Operation operation;
    private final EquipmentSlotGroup slot;

    // TODO: Make this class a record
    public ItemAttributeComponent(Attribute attribute, double amount,
                                  AttributeModifier.Operation operation, EquipmentSlotGroup slot) {
        this.attribute = attribute;
        this.amount = amount;
        this.operation = operation;
        this.slot = slot;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public double getAmount() {
        return amount;
    }

    public AttributeModifier.Operation getOperation() {
        return operation;
    }

    public EquipmentSlotGroup getSlot() {
        return slot;
    }
}
