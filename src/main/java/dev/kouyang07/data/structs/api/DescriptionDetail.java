package dev.kouyang07.data.structs.api;

import com.fasterxml.jackson.annotation.JsonProperty;

class DescriptionDetail {
    @JsonProperty("type")
    private String type;

    @JsonProperty("value")
    private String value;

    @JsonProperty("color")
    private String color;

    // Getters and Setters
    // (Omitted for brevity; generate these in your IDE)
}
