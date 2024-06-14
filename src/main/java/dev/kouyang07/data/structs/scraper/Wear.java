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

    public static Wear toWear(String wear){
        if(wear.equals("Factory New")){
            return Wear.FACTORY_NEW;
        }else if(wear.equals("Minimal Wear")){
            return Wear.MINIMAL_WEAR;
        }else if(wear.equals("Field-Tested")){
            return Wear.FIELD_TESTED;
        }else if(wear.equals("Well-Worn")){
            return Wear.WELL_WORN;
        }else if(wear.equals("Battle-Scarred")){
            return Wear.BATTLE_SCARRED;
        }else{
            return Wear.FACTORY_NEW;
        }
    }
}
