package dev.kouyang07.data.structs.scraper;

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

    public static dev.kouyang07.data.structs.skin.Wear toWearClass(Wear wear){
        return new dev.kouyang07.data.structs.skin.Wear(wear.getName().toLowerCase(), wear.getName());
    }
}
