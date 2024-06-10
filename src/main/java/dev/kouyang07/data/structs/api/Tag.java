package dev.kouyang07.data.structs.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tag {
    @JsonProperty("category")
    private String category;

    @JsonProperty("internal_name")
    private String internalName;

    @JsonProperty("localized_category_name")
    private String localizedCategoryName;

    @JsonProperty("localized_tag_name")
    private String localizedTagName;

    @JsonProperty("color")
    private String color;

    // Getters and Setters
}
