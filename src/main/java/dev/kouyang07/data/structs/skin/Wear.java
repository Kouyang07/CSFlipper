package dev.kouyang07.data.structs.skin;

import lombok.Data;

@Data
public class Wear {
    private String id;
    private String name;

    public Wear(String id, String name){
        this.id = id;
        this.name = name;
    }

    public static dev.kouyang07.data.structs.scraper.Wear toWearEnum(String name){
        return dev.kouyang07.data.structs.scraper.Wear.valueOf(name.toUpperCase().trim().replaceAll(" ", "_").replaceAll("-", "_"));
    }
}
