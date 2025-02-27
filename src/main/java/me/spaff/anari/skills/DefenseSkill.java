package me.spaff.anari.skills;

import me.spaff.anari.AnariColors;

public class DefenseSkill extends Skill {
    public DefenseSkill(int currentLevel) {
        super(currentLevel);
        init();
    }

    @Override
    public void init() {
        if (!PERKS.isEmpty()) return;
        PERKS.add(new Perk(1, "Iron Skin", "&7Environmental damage is reduced by &a10%&7."));
        PERKS.add(new Perk(1, "Obsidian Skin", "&6Fire &7damage is reduced by &b30%&7."));
        PERKS.add(new Perk(1, "Stone Wall", "&7Arrows deal &b10% &7less damage."));
        PERKS.add(new Perk(1, "Rejuvenation", "&7When healing there is &a5% chance &7to heal an addition &c1❤&7."));
        PERKS.add(new Perk(1, "Magic Barrier", "&7Negative potion effects last only &a80% &7of it their original duration."));
        //PERKS.add(new Perk(1, "Unyielding", "&7Gain " + AnariColors.LIGHT_GRAY + "Resistance I &7for &a5 seconds &7when below &c3❤&7."));
    }

    @Override
    public SkillType getType() {
        return SkillType.DEFENSE;
    }
}
