package dev.kouyang07.data.structs;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.kouyang07.data.structs.api.Details;
import lombok.Data;

import java.util.List;

@Data
public class APIWrapper {
    @JsonProperty("assets")
    private List<Object> assets;

    @JsonProperty("descriptions")
    private List<Details> details;

    @JsonProperty("total_inventory_count")
    private int totalInventoryCount;

    @JsonProperty("success")
    private int success;

    @JsonProperty("rwgrsn")
    private int rwgrsn;
}