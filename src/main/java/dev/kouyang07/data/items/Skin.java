package dev.kouyang07.data.items;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.kouyang07.data.items.structure.*;
import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Skin {

    public static HashMap<String, Skin> allSkins = new HashMap<>();

    private String id;
    private String name;
    private String description;
    private Weapon weapon;
    private Category category;
    private Pattern pattern;
    @JsonProperty("min_float")
    private double minFloat;
    @JsonProperty("max_float")
    private double maxFloat;
    private Rarity rarity;
    private boolean stattrak;
    private boolean souvenir;
    @JsonProperty("paint_index")
    private String paintIndex;
    private List<Wear> wears;
    private List<Collection> collections;
    private List<Crate> crates;
    private Team team;
    private String image;
}
