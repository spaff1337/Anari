package me.spaff.anari.item.rarity;

import me.spaff.anari.utils.StringUtils;

public enum AnariRarity {
    COMMON(StringUtils.getHexColor("#b5b5b5"), "Common"),
    UNCOMMON(StringUtils.getHexColor("#72c92e"), "Uncommon"),
    RARE(StringUtils.getHexColor("#2999e3"), "Rare");

    private String color;
    private String name;

    AnariRarity(String color, String name) {
        this.color = color;
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }
}
