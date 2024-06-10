package dev.kouyang07.data.api.steamcommunity.struct;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Description {
    @JsonProperty("appid")
    private int appId;

    @JsonProperty("classid")
    private String classId;

    @JsonProperty("instanceid")
    private String instanceId;

    @JsonProperty("currency")
    private int currency;

    @JsonProperty("background_color")
    private String backgroundColor;

    @JsonProperty("icon_url")
    private String iconUrl;

    @JsonProperty("descriptions")
    private List<DescriptionDetail> descriptions;

    @JsonProperty("tradable")
    private int tradable;

    @JsonProperty("actions")
    private List<Action> actions;

    @JsonProperty("name")
    private String name;

    @JsonProperty("name_color")
    private String nameColor;

    @JsonProperty("type")
    private String type;

    @JsonProperty("market_name")
    private String marketName;

    @JsonProperty("market_hash_name")
    private String marketHashName;

    @JsonProperty("market_actions")
    private List<MarketAction> marketActions;

    @JsonProperty("commodity")
    private int commodity;

    @JsonProperty("market_tradable_restriction")
    private int marketTradableRestriction;

    @JsonProperty("marketable")
    private int marketable;

    @JsonProperty("tags")
    private List<Tag> tags;

    // Getters and Setters
    // (Omitted for brevity; generate these in your IDE)
}