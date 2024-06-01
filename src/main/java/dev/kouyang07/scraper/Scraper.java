package dev.kouyang07.scraper;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Objects;

public class Scraper {
    private final String item;
    public Scraper (String item) {
        this.item = item;
    }
    public MessageEmbed scrape() {
        Platforms[] platforms = filteredPlatforms(item);
        return new EmbedBuilder()
                .setTitle("Skin data for " + item.replace("-", " ") + " | " + (platforms[1].getPrice() - platforms[0].getPrice()))
                .addBlankField(false)
                .addField("---- Flipping Data ----", "", true)
                .addField("Lowest Seller", platforms[0].getName() + " for " + platforms[0].getPrice() + " at " + platforms[0].getUrl(), false)
                .addField("Highest Buyer", platforms[1].getName() + " for " + platforms[1].getPrice() + " at " + platforms[1].getUrl(), false)
                .build();
    }

    private Platforms[] scrapePlatforms(String item) {
        Platforms[] platforms = new Platforms[17];
        try {
            Document doc = Jsoup.connect(URLConstructor(item)).get();

            Elements skinPortElement = doc.selectXpath("/html/body/main/div[2]/div[2]/div[1]/div[2]");
            Element skinPort = skinPortElement.first();
            String[] skinPortTokens = skinPort.text().substring(267).split(" ");
            String linkString;
            Element link = skinPortElement.select("div.bg-gray-800 a:contains(View Offer)").first();
            if (link != null) {
                linkString = (link.attr("href"));
            } else {
                linkString = ("Link not found");
            }
            platforms[0] = new Platforms(skinPortTokens[0], Integer.parseInt(skinPortTokens[7]), Double.parseDouble(skinPortTokens[9].substring(1)), linkString);

            for (int i = 3; i <= 18; i++) {
                Elements platformElements = doc.selectXpath("/html/body/main/div[2]/div[2]/div[1]/div[" + i + "]");
                Element platform = platformElements.first();
                if (platform != null) {
                    platforms[i - 2] = scrapePlatform(platform);
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return platforms;
    }

    private Platforms scrapePlatform(Element platform) {
        String[] tokens = platform.text().split(" ");
        String linkString;
        Element link = platform.select("div.bg-gray-800 a:contains(View Offer)").first();
        if (link != null) {
            linkString = (link.attr("href"));
        } else {
            linkString = ("Link not found");
        }
        if (tokens.length == 12) {
            return new Platforms(tokens[0], Integer.parseInt(tokens[7]), Double.parseDouble(tokens[9].substring(1)), linkString);
        } else {
            return new Platforms(tokens[0] + " " + tokens[1], Integer.parseInt(tokens[8]), Double.parseDouble(tokens[10].substring(1)), linkString);
        }
    }

    public String URLConstructor(String item) {
        return "https://csgoskins.gg/items/" + item.replace(" ", "-");
    }

    /**
     * Filters the platforms to only include the platform with the lowest price and the platform with the highest price
     * @param item the item to filter
     * @return an array of the lowest(Indexed at 0) and highest(Indexed at 1) priced platforms
     */

    private Platforms[] filteredPlatforms(String item) {
        Platforms[] platforms = scrapePlatforms(item);
        int lIndex = 0;
        double minPrice = Double.MAX_VALUE;
        int hIndex = 0;
        double maxPrice = Double.MIN_VALUE;
        for(int i = 0; i < platforms.length; i++) {
            if (Objects.nonNull(platforms[i]) && platforms[i].getPrice() < minPrice) {
                minPrice = platforms[i].getPrice();
                lIndex = i;
            }
            if(Objects.nonNull(platforms[i]) && platforms[i].getPrice() > maxPrice && (platforms[i].getName().equals("Skinport") || platforms[i].getName().equals("Dmarket"))) {
                maxPrice = platforms[i].getPrice();
                hIndex = i;
            }
        }
        return new Platforms[] {platforms[lIndex], platforms[hIndex]};
    }
}
