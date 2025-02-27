package me.spaff.anari.skills;

import me.spaff.anari.AnariColors;

public class CombatSkill extends Skill {
    public CombatSkill(int currentLevel) {
        super(currentLevel);
        init();
    }

    @Override
    public void init() {
        if (!PERKS.isEmpty()) return;
        PERKS.add(new Perk(1, "Adrenaline Rush", "&7Killing a hostile mob grants &bSpeed I &7for &a5 seconds&7."));
        PERKS.add(new Perk(1, "Sharpshooter", "&7Shooting a bow has a &b10% chance &7to fire additional arrow."));
        PERKS.add(new Perk(1, "Combo", "&7Every &a5th &7melee hit on a single target will deal extra &c2⚔ damage&7."));
        PERKS.add(new Perk(1, "Axe Master", "&7Axes have faster attack speed."));
        PERKS.add(new Perk(1, "Final Stand", "&7When on &c0.5❤&7 gain &6Strength I &7and &bSpeed II&7."));
    }

    @Override
    public SkillType getType() {
        return SkillType.COMBAT;
    }
}
