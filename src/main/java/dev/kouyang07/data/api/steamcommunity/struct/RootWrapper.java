package dev.kouyang07.data.api.steamcommunity.struct;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RootWrapper {
    @JsonProperty("assets")
    private List<Object> assets;

    @JsonProperty("descriptions")
    private List<Description> descriptions;

    @JsonProperty("total_inventory_count")
    private int totalInventoryCount;

    @JsonProperty("success")
    private int success;

    @JsonProperty("rwgrsn")
    private int rwgrsn;
}