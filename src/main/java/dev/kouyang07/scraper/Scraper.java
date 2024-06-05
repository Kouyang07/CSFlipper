package dev.kouyang07.scraper;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Scraper {
    private final String item;
    private final Document doc;

    public Scraper(String item) {
        this.item = item;
        try {
            doc = Jsoup.connect(URLConstructor(item)).get();
        } catch (Exception e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    public MessageEmbed scrape() {
        Platforms[] platforms = filterPlatforms();
        HistoricalData historicalData = scrapeHistoricalData();
        return new EmbedBuilder()
                .setTitle("Skin data for " + item.replace("-", " ") + " | ~ $" + (int)(platforms[1].getPrice() - platforms[0].getPrice()))
                .addField("Recommended action",
                        "Buy from " + platforms[0].getName() + "@" + platforms[0].getPrice() + " , sell to " + platforms[1].getName() + "@" + platforms[1].getPrice(), false)

                .addField("Historical Data", "Current Price: $" + historicalData.getCurrentPrice() + "\n" +
                        "24h Price Change: $" + historicalData.getPriceChange() + "\n" +
                        "24h Trading Volume: $" + historicalData.getTradingVolume() + "\n" +
                        "Market Cap: $" + historicalData.getMarketCap() + "\n" +
                        "Volume / Market Cap: $" + historicalData.getVolumeMarketCap() + "\n" +
                        "30d High: $" + historicalData.getHigh() + "\n" +
                        "30d Low: $" + historicalData.getLow() + "\n" +
                        "30d Average $" + (historicalData.getHigh() + historicalData.getLow())/2 + "\n" +
                        "All Time High: $" + historicalData.getAllTimeHigh() + "\n" +
                        "All Time Low: $" + historicalData.getAllTimeLow(), false)
                .addField("Flipping Data",
                        "Lowest: " + platforms[0].getName() + " at $" + platforms[0].getPrice() + " at " + platforms[0].getUrl() + "\n" +
                                "Highest: " + platforms[1].getName() + " at $" + platforms[1].getPrice() + " at " + platforms[1].getUrl(), true)
                .build();
    }

    public String URLConstructor(String item) {
        return "https://csgoskins.gg/items/" + item.replace(" ", "-");
    }

    private Platforms[] scrapePlatforms() {
        Platforms[] platforms = new Platforms[25];
        try {
            Elements skinPortElement = doc.selectXpath("/html/body/main/div[2]/div[2]/div[1]/div[2]");
            Element skinPort = skinPortElement.first();
            assert skinPort != null;
            String[] skinPortTokens = skinPort.text().substring(267).split(" ");
            String linkString;
            Element link = skinPortElement.select("div.bg-gray-800 a:contains(View Offer)").first();
            if (link != null) {
                linkString = (link.attr("href"));
            } else {
                linkString = ("Link not found");
            }
            platforms[0] = new Platforms(skinPortTokens[0], Integer.parseInt(skinPortTokens[7]), Double.parseDouble(skinPortTokens[9].substring(1)), linkString);

            for (int i = 3; i <= platforms.length + 3; i++) {
                Elements platformElements = doc.selectXpath("/html/body/main/div[2]/div[2]/div[1]/div[" + i + "]");
                Element platform = platformElements.first();
                if (platform != null) {
                    platforms[i - 2] = parsePlatform(platform);
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return platforms;
    }

    private Platforms parsePlatform(Element platform) {
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

    /**
     * Filters the platforms to only include the platform with the lowest price and the platform with the highest price
     *
     * @return an array of the lowest(Indexed at 0) and highest(Indexed at 1) priced platforms
     */

    private Platforms[] filterPlatforms() {
        Platforms[] platforms = scrapePlatforms();
        int lIndex = 0;
        double minPrice = Double.MAX_VALUE;
        int hIndex = 0;
        double maxPrice = Double.MIN_VALUE;
        for (int i = 0; i < platforms.length; i++) {
            if (Objects.nonNull(platforms[i]) && platforms[i].getPrice() < minPrice) {
                minPrice = platforms[i].getPrice();
                lIndex = i;
            }
            if (Objects.nonNull(platforms[i]) && platforms[i].getPrice() > maxPrice && (platforms[i].getName().equals("Skinport") || platforms[i].getName().equals("Dmarket") || platforms[i].getName().equals("GamerPay"))) {
                maxPrice = platforms[i].getPrice();
                hIndex = i;
            }
        }
        return new Platforms[]{platforms[lIndex], platforms[hIndex]};
    }

    public HistoricalData scrapeHistoricalData() {
        List<Double> values = new ArrayList<>();

        Element element = doc.select("div[class=\"shadow-md bg-gray-800 rounded mt-4\"]").getLast();
        Elements fields = element.select("div[class=\"flex px-4 py-2\"]");
        for(Element field : fields){
            String[] tokens = field.text().split(" ");
            if(tokens[2].contains("Change")){
                values.add(Double.parseDouble(tokens[tokens.length - 2].substring(2).replaceAll(",", "")));
            }else{
                values.add(Double.parseDouble(tokens[tokens.length - 1].substring(1).replaceAll(",", "")));
            }
        }
        return new HistoricalData(values);
    }
}
