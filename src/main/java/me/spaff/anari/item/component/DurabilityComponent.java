package me.spaff.anari.item.component;

public class DurabilityComponent {
    private final int damage;
    private final int maxDurability;

    // TODO: Make this class a record
    public DurabilityComponent(int damage, int maxDurability) {
        this.damage = damage;
        this.maxDurability = maxDurability;
    }

    public int getDamage() {
        return damage;
    }

    public int getMaxDurability() {
        return maxDurability;
    }
}
