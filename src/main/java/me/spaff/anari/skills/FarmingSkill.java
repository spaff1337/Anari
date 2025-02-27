package me.spaff.anari.skills;

public class FarmingSkill extends Skill {
    public FarmingSkill(int currentLevel) {
        super(currentLevel);
        init();
    }

    @Override
    public void init() {
        if (!PERKS.isEmpty()) return;
        PERKS.add(new Perk(1, "Green Thumb", "&7Crops grow faster around you."));
        PERKS.add(new Perk(1, "Master Harvester", "&a10% chance &7to get double drops when harvesting crops."));
        PERKS.add(new Perk(1, "Better Bones", "&7Using &fBone Meal &7on crops will have a &a20% chance &7to grow it to their max age."));
        PERKS.add(new Perk(1, "Feather Feet", "&7You are unable to trample farmland with crops."));
        PERKS.add(new Perk(1, "Re-planter", "&7Automatically replants fully grown crops when harvested."));
    }

    @Override
    public SkillType getType() {
        return SkillType.FARMING;
    }
}
