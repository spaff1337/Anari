package me.spaff.anari.skills;

import me.spaff.anari.AnariColors;

public class MiningSkill extends Skill {
    public MiningSkill(int currentLevel) {
        super(currentLevel);
        init();
    }

    @Override
    public void init() {
        if (!PERKS.isEmpty()) return;
        PERKS.add(new Perk(1, "Prospector", "&a5% chance &7to double ore drops."));
        PERKS.add(new Perk(1, "Endless Quiver", "&7Mining gravel has a small chance to drop arrows."));
        PERKS.add(new Perk(1, "Rockstar", "&7Mining stone has a chance to drop " + AnariColors.EXP + "EXP&7."));
        PERKS.add(new Perk(1, "Efficient", "&7Increased mining speed by &b10%&7."));
        PERKS.add(new Perk(1, "Lucky Strike", "&a5% chance &7to instantly mine any ore."));
    }

    @Override
    public SkillType getType() {
        return SkillType.MINING;
    }
}
