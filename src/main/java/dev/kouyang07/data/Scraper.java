package dev.kouyang07.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import dev.kouyang07.data.structs.scraper.*;
import lombok.Data;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.text.ParseException;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.List;
@Data
public class Scraper {
    private final String item;
    private final Wear wear;
    private final boolean statTrak;
    private Document doc;

    public Scraper(String item, Wear wear, boolean statTrak) {
        this.item = item;
        this.wear = wear;
        this.statTrak = statTrak;
        try {
            doc = Jsoup.connect(URLConstructor(item, wear, statTrak)).get();
        } catch (Exception e) {
            doc = null;
        }
    }


    public Scraper(String item, Wear wear) {
        this.item = item;
        this.wear = wear;
        this.statTrak = false;
        try {
            doc = Jsoup.connect(URLConstructor(item, wear)).get();
        } catch (Exception e) {
            doc = null;
        }
    }

    public Scraper(String item) {
        this.item = item;
        this.wear = null;
        this.statTrak = false;
        try {
            doc = Jsoup.connect(URLConstructor(item)).get();
        } catch (Exception e) {
            doc = null;
        }
    }

    public Scraper(String item, boolean statTrak) {
        this.item = item;
        this.wear = null;
        this.statTrak = statTrak;
        try {
            doc = Jsoup.connect(URLConstructor(item, statTrak)).get();
        } catch (Exception e) {
            doc = null;
        }
    }

    public byte[] generateChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy"); // The format of your date strings
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("MM/dd"); // Desired format for the chart
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -14); // Get date 30 days ago
        Date thirtyDaysAgo = calendar.getTime();

        Product product = scrapeHistoricalData();

        for (PriceHistory entry : product.getPriceHistory()) {
            try {
                // Parse the date using the correct format
                Date date = inputDateFormat.parse(String.valueOf(entry.getDay()));
                if (date.after(thirtyDaysAgo)) { // Filter for the last 30 days
                    String formattedDate = outputDateFormat.format(date);
                    dataset.addValue(entry.getPrice(), "Price", formattedDate);
                }
            } catch (ParseException e) {
                System.err.println("Error parsing date: " + entry.getDay());
                e.printStackTrace(); // Print stack trace to identify the issue
            } catch (Exception e) {
                System.err.println("Unexpected error while adding value to dataset for date: " + entry.getDay());
                e.printStackTrace(); // Print stack trace to identify the issue
            }
        }

        JFreeChart lineChart = ChartFactory.createLineChart(
                "Price History for " + product.getName(),
                "Date",
                "Price",
                dataset,
                PlotOrientation.VERTICAL, // Changed to vertical for better visualization
                true,
                true,
                false
        );
        lineChart.getPlot().setBackgroundPaint(new Color(31, 41, 55));

        try {
            return ChartUtils.encodeAsPNG(lineChart.createBufferedImage(1300, 600));
        } catch (Exception e) {
            System.err.println("Error while encoding as PNG");
            e.printStackTrace(); // Print stack trace to identify the issue
        }

        return new byte[]{};
    }

    public String URLConstructor(String item) {
        return "https://csgoskins.gg/items/" + item.replace("★", "").replaceAll("\\|", "").replaceAll(" {2}", " ").trim().replaceAll(" ", "-").toLowerCase();
    }

    public String URLConstructor(String item, Wear wear, boolean statTrack) {
        String base = "https://csgoskins.gg/items/" + item.replace("★", "").replaceAll("\\|", "").replaceAll(" {2}", " ").trim().replaceAll(" ", "-").toLowerCase();
        if (statTrack) {
            return base + "/stattrak-" + wear.getName().toLowerCase();
        } else {
            return base + "/" + wear.getName().toLowerCase();
        }
    }

    public String URLConstructor(String item, boolean statTrack) {
        return "https://csgoskins.gg/items/" + item.replace("★", "").replaceAll("\\|", "").replaceAll(" {2}", " ").trim().replaceAll(" ", "-").toLowerCase() + "/stattrak-factory-new";
    }

    public String URLConstructor(String item, Wear wear) {
        return "https://csgoskins.gg/items/" + item.replace("★", "").replaceAll("\\|", "").replaceAll(" {2}", " ").trim().replaceAll(" ", "-").toLowerCase() + "/" + wear.getName().toLowerCase();
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

    public Platforms[] filterPlatforms() {
        Platforms[] platforms = scrapePlatforms();
        int lIndex = 0;
        double minPrice = Double.MAX_VALUE;
        int hIndex = 0;
        double maxPrice = Double.MIN_VALUE;
        for (int i = 0; i < platforms.length; i++) {
            if(Objects.isNull(platforms[i])){
                continue;
            }
            if (Objects.nonNull(platforms[i]) && platforms[i].getPrice() < minPrice) {
                minPrice = platforms[i].getPrice();
                lIndex = i;
            }
            if (Objects.nonNull(platforms[i]) && platforms[i].getPrice() > maxPrice && (platforms[i].getName().equals("Skinport") || platforms[i].getName().equals("Dmarket") || platforms[i].getName().equals("GamerPay"))) {
                maxPrice = platforms[i].getPrice();
                hIndex = i;
            }
        }

        Platforms[] prices = new Platforms[]{platforms[lIndex], platforms[hIndex]};

        if(prices[1] != null) {

            if (prices[1].getName().equals("Skinport")) {
                prices[1].setPrice(prices[1].getPrice() - (prices[1].getPrice() * 0.12));
            } else if (prices[1].getName().equals("Dmarket")) {
                prices[1].setPrice(prices[1].getPrice() - (prices[1].getPrice() * 0.05));
            }
        }
        return prices;
    }

    public PriceStatistics scrapePriceStatistics() {
        List<Double> values = new ArrayList<>();

        Element element = doc.select("div[class=\"shadow-md bg-gray-800 rounded mt-4\"]").getLast();
        Elements fields = element.select("div[class=\"flex px-4 py-2\"]");
        for (Element field : fields) {
            String[] tokens = field.text().split(" ");
            if (tokens[2].contains("Change")) {
                values.add(Double.parseDouble(tokens[tokens.length - 2].substring(2).replaceAll(",", "")));
            } else {
                values.add(Double.parseDouble(tokens[tokens.length - 1].substring(1).replaceAll(",", "")));
            }
        }
        return new PriceStatistics(values);
    }

    public Product scrapeHistoricalData() {
        Elements scripts = doc.select("script");
        Product product = null;
        try {

            for (Element script : scripts) {
                // Check if the script contains the 'priceHistory' variable
                if (script.data().contains("const priceHistory =")) {
                    // Extract the JSON data
                    String scriptData = script.data();
                    String jsonData = scriptData.split("const priceHistory =")[1].split(";")[0].trim();

                    // Parse the JSON data
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.setDateFormat(new StdDateFormat());

                    try {
                        List<PriceHistory> priceHistoryList = objectMapper.readValue(jsonData, new TypeReference<>() {
                        });

                        product = new Product();
                        product.setName(item);
                        product.setPriceHistory(priceHistoryList);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error while parsing historical Data");
        }
        return product;
    }

    public String scrapeImage(){
        Element img = doc.select("img#main-image").first();
        if (img != null) {
            return img.attr("src");
        } else {
            return "";
        }
    }

    public boolean isValid() {
        return doc != null;
    }

    public boolean getStatTrak(){
        return statTrak;
    }
}