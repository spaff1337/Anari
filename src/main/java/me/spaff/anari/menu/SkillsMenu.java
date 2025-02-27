package me.spaff.anari.menu;

import me.spaff.anari.AnariColors;
import me.spaff.anari.player.AnariPlayer;
import me.spaff.anari.player.AnariPlayerManager;
import me.spaff.anari.skills.*;
import me.spaff.anari.utils.ItemBuilder;
import me.spaff.anari.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;

import java.util.List;

public class SkillsMenu extends PlayerMenu {
    private final int[] SKILL_SLOTS = new int[]{11, 13, 15, 30, 32};
    private final SkillType[] SKILL_TYPES = new SkillType[]{
            SkillType.COMBAT,
            SkillType.MINING,
            SkillType.FARMING,
            SkillType.AGILITY,
            SkillType.DEFENSE
    };

    public SkillsMenu(Player player) {
        super(player, "Skills", 6);

        this.fillWith(new ItemBuilder.Builder(Material.BLACK_STAINED_GLASS_PANE)
                .name("&7")
                .build()
                .getItem()
        );

        this.setItem(49, new ItemBuilder.Builder(Material.ARROW)
                .name("&aGo Back")
                .build()
                .getItem()
        );

        AnariPlayer anariPlayer = AnariPlayerManager.getPlayer(player);

        Material[] SKILL_ICONS = new Material[]{
                Material.IRON_SWORD,
                Material.STONE_PICKAXE,
                Material.GOLDEN_HOE,
                Material.FEATHER,
                Material.SHIELD
        };
        String[] SKILL_NAMES = new String[]{
                "Combat",
                "Mining",
                "Farming",
                "Agility",
                "Defense"
        };
        Skill[] SKILLS = new Skill[]{
                anariPlayer.getSkill(SkillType.COMBAT),
                anariPlayer.getSkill(SkillType.MINING),
                anariPlayer.getSkill(SkillType.FARMING),
                anariPlayer.getSkill(SkillType.AGILITY),
                anariPlayer.getSkill(SkillType.DEFENSE)
        };

        int index = 0;
        for (int slot : SKILL_SLOTS) {
            Material icon = SKILL_ICONS[index];
            String name = SKILL_NAMES[index];
            Skill skill = SKILLS[index];

            this.setItem(slot, new ItemBuilder.Builder(icon)
                    .name("&b" + name + " " + skill.getCurrentLevel())
                    .lore(List.of(
                            "&7Unlock Cost: " + AnariColors.EXP + "⏺" + skill.getNextLevelCost() + " Levels",
                            "",
                            "&6" + skill.getNextPerkName() + ":",
                            skill.getNextPerkDescription()
                    ))
                    .flags(ItemFlag.HIDE_ATTRIBUTES)
                    .build()
                    .getItem()
            );

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

            // Go back
            if (e.getSlot() == 49)
                new AnariMenu(player);

            int i = 0;
            for (SkillType type : SKILL_TYPES) {
                if (e.getSlot() == SKILL_SLOTS[i]) {
                    Skill skill = anariPlayer.getSkill(type);

                    int currentLevel = skill.getCurrentLevel();
                    int requiredLevel = skill.getNextLevelCost();

                    if (currentLevel == skill.getMaxLevel()) {
                        StringUtils.sendMessage(player, "&cThis perk is already maxed out!");
                        return;
                    }

                    if (player.getLevel() >= requiredLevel) {
                        player.setLevel(player.getLevel() - requiredLevel);
                        anariPlayer.getSkill(type).setLevel(currentLevel + 1);

                        StringUtils.sendMessage(player, "&aLeveled up " + type.toString() + " to level: " + anariPlayer.getSkill(type).getCurrentLevel());
                        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);

                        new SkillsMenu(player);
                    }
                    else {
                        StringUtils.sendMessage(player, "&cYou need to be level " + requiredLevel + " to unlock this perk.");
                    }
                }
                i++;
            }
        }
        else if (clickedInventory.equals(player.getInventory()) &&
                player.getOpenInventory().getTopInventory().equals(inventory)) {
            e.setCancelled(true);
        }
    }
}
