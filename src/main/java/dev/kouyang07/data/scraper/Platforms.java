package dev.kouyang07.data.scraper;

import lombok.Data;

@Data
public class Platforms {
    private String name;
    private int activeOffers;
    private double price;
    private String url;

    public Platforms(String name, int activeOffers, double price, String url) {
        this.name = name;
        this.activeOffers = activeOffers;
        this.price = price;
        this.url = url;
    }
}
