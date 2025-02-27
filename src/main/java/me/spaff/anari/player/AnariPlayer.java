package me.spaff.anari.player;

import me.spaff.anari.item.AnariID;
import me.spaff.anari.item.AnariItem;
import me.spaff.anari.item.AnariItems;
import me.spaff.anari.skills.*;
import me.spaff.anari.utils.ItemUtils;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class AnariPlayer {
    private final Player player;
    private final UUID uuid;

    private Map<Integer, AnariItem> accessories = new HashMap<>();
    private List<Skill> skills = new ArrayList<>();

    public AnariPlayer(UUID uuid) {
        this.player = Bukkit.getPlayer(uuid);
        this.uuid = uuid;

        init();
    }

    public AnariPlayer(Player player) {
        this(player.getUniqueId());
    }

    private void init() {
        //clear();

        skills.add(new CombatSkill(0));
        skills.add(new MiningSkill(0));
        skills.add(new FarmingSkill(0));
        skills.add(new AgilitySkill(0));
        skills.add(new DefenseSkill(0));

        // TODO: Load data from database
    }

    /*private void clear() {
        player.setExp(0.f);
        player.setLevel(0);
        player.setTotalExperience(0);

        if (!player.getGameMode().equals(GameMode.CREATIVE) &&
                !player.getGameMode().equals(GameMode.SPECTATOR))
        {
            player.setAllowFlight(false);
            player.setFlying(false);
        }
    }*/

    public void updateInventory() {
        for (int slot = 0; slot < player.getInventory().getContents().length; slot++) {
            ItemStack item = player.getInventory().getItem(slot);
            if (ItemUtils.isNull(item)) continue;

            ItemStack updatedItem = AnariItem.update(item);
            if (updatedItem != null)
                player.getInventory().setItem(slot, updatedItem);
        }
    }

    // Setters

    // Accessories
    public void setAccessory(int slot, AnariItem item) {
        Validate.isTrue(item.isAccessory(), "Cannot equip " + item.getId() + " because it's not an accessory!");
        Validate.inclusiveBetween(1, 6, slot, "Accessory slot " + slot + " is out of bounds!");
        accessories.put(slot, item);
    }

    public void equipAccessory(AnariItem item) {
        if (accessories.size() >= 6) return;
        setAccessory(accessories.size() + 1, item);

        // TODO: Make so 2 or more of the same accessory
        //  can't be equipped
    }

    public AnariItem unEquipAccessory(int slot) {
        AnariItem item = accessories.get(slot);
        accessories.remove(slot);
        return item;
    }

    // Skills
    public Skill getSkill(SkillType type) {
        for (Skill skill : skills) {
            if (skill.getType().equals(type))
                return skill;
        }
        return null;
    }

    // Getters

    public Map<Integer, AnariItem> getAccessories() {
        return accessories;
    }

    public AnariItem getAccessory(int slot) {
        return accessories.get(slot);
    }

    public boolean hasEquipped(AnariID id) {
        AnariItem item = AnariItems.getInstance(id);
        if (!item.isAccessory()) return false;

        for (var accessory : accessories.entrySet()) {
            AnariItem equippedAccessory = accessory.getValue();

            if (equippedAccessory.getId().equals(item.getId()))
                return true;
        }

        return false;
    }

    public boolean canEquip(AnariID id) {
        if (hasEquipped(id)) return false;
        if (accessories.size() >= 6) return false;
        return true;
    }
}