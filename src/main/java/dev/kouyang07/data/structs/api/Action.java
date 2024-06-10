package dev.kouyang07.data.structs.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Action {
    @JsonProperty("link")
    private String link;

    @JsonProperty("name")
    private String name;

    // Getters and Setters
}
