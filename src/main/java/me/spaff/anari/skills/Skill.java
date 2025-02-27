package me.spaff.anari.skills;

import java.util.ArrayList;
import java.util.List;

public abstract class Skill {
    protected final List<Perk> PERKS = new ArrayList<>();
    protected int currentLevel;

    public Skill(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public abstract void init();

    // Setters

    public void setLevel(int level) {
        this.currentLevel = level;
    }

    // Getters

    public abstract SkillType getType();

    public String getNextPerkName() {
        if (!isWithingBounds(currentLevel)) return "COMING SOON";
        return PERKS.get(currentLevel).name();
    }

    public String getNextPerkDescription() {
        if (!isWithingBounds(currentLevel)) return "&cCOMING SOON";
        return PERKS.get(currentLevel).description();
    }

    public int getNextLevelCost() {
        if (!isWithingBounds(currentLevel)) return -1;
        return PERKS.get(currentLevel).unlockLevelCost();
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getMaxLevel() {
        return PERKS.size();
    }

    protected boolean isWithingBounds(int level) {
        if (level >= 0 && level < PERKS.size())
            return true;
        return false;
    }
}
