package dev.kouyang07.data.structs.scraper;

import lombok.Data;

@Data
public class Platform {
    private String name;
    private int activeOffers;
    private double price;
    private String url;

    public Platform(String name, int activeOffers, double price, String url) {
        this.name = name;
        this.activeOffers = activeOffers;
        this.price = price;
        this.url = url;
    }
}
