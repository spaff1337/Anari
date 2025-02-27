package me.spaff.anari.item;

public enum AnariItemType {
    NONE(""),
    ACCESSORY("Accessory"),
    SWORD("Sword"),
    BOW("Bow"),
    AMOR("Armor"),
    OTHER("Other");

    private String typeString;

    AnariItemType(String typeString) {
        this.typeString = typeString;
    }

    public String getTypeName() {
        return this.typeString;
    }
}
