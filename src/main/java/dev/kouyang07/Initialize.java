package dev.kouyang07;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import dev.kouyang07.data.CSData;
import dev.kouyang07.data.Player;
import dev.kouyang07.data.Scraper;
import dev.kouyang07.data.structs.Item;
import dev.kouyang07.discord.Bot;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Arrays;

public class Initialize {
    public static void init(){
        populateSkinList();
        System.out.println("[INFO] Populated " + CSData.itemSet.size() + "skins");
        //Item(weapon=Driver Gloves, skin=Rezan the Red, wear=FACTORY_NEW, statTrak=false)
        Player player = new Player("76561198824034732");
        player.trackInventory();
        for(Item item : player.getInventory()){
            System.out.println(new Scraper(item).getPrice().getLowestPlatform().getPrice());
        }
        //new Bot();
    }

    private static void populateSkinList(){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(getSkinJson());
            if (rootNode.isArray()) {
                ArrayNode arrayNode = (ArrayNode) rootNode;
                for (JsonNode element : arrayNode) {
                    if (element.has("name")) {
                        String name = element.get("name").asText();
                        if(name.contains("|")) {
                            String[] itemArray = name.replaceAll("弐", "2").split("\\|");
                            CSData.itemSet.add(new Item(itemArray[0].replaceAll("★", "").trim(), itemArray[1].trim()));
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error while populating skin list" + Arrays.toString(e.getStackTrace()));
        }
    }

    private static String getSkinJson() {
        String jsonData = "null";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://bymykel.github.io/CSGO-API/api/en/skins.json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            if(response.body() != null){
                jsonData = response.body().string();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return jsonData;
    }
}
