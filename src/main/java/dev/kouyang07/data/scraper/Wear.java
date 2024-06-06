package dev.kouyang07.data.scraper;

public enum Wear {
    FACTORY_NEW("factory-new"),
    MINIMAL_WEAR("minimal-wear"),
    FIELD_TESTED("field-tested"),
    WELL_WORN("well-worn"),
    BATTLE_SCARRED("battle-scarred");

    private final String name;

    Wear(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
