package me.spaff.anari.skills;

public class AgilitySkill extends Skill {
    public AgilitySkill(int currentLevel) {
        super(currentLevel);
        init();
    }

    @Override
    public void init() {
        if (!PERKS.isEmpty()) return;
        PERKS.add(new Perk(1, "Acrobat", "&7Decreases hunger loss from running."));
        PERKS.add(new Perk(1, "Surefooted", "&7Increased fall damage safe distance to 4 blocks."));
        PERKS.add(new Perk(1, "Strong Feet", "&7Decreased fall damage by &a15%&7."));
        PERKS.add(new Perk(1, "Path Walker", "&7Gain &bSpeed I &7when walking on paths."));
        PERKS.add(new Perk(1, "Cliff Climber", "&7Ability to walk up full blocks."));
    }

    @Override
    public SkillType getType() {
        return SkillType.AGILITY;
    }
}
