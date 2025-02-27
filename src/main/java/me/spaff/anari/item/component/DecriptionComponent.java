package me.spaff.anari.item.component;

import java.util.List;

public class DecriptionComponent {
    private final List<String> description;

    // TODO: Make this class a record
    public DecriptionComponent(List<String> description) {
        this.description = description;
    }

    public List<String> getDescription() {
        return description;
    }
}
