package me.spaff.anari.utils.logger;

public enum ALevel {
    INFO("&a"),
    WARNING("&e"),
    SEVERE("&c");

    private String clr;

    ALevel(String clr) {
        this.clr = clr;
    }

    public String getColor() {
        return clr;
    }
}