package dev.kouyang07.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kouyang07.data.structs.Item;
import dev.kouyang07.data.structs.Price;
import dev.kouyang07.data.structs.api.Details;
import dev.kouyang07.data.structs.APIWrapper;
import dev.kouyang07.data.structs.scraper.Wear;
import lombok.Data;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
@Data
public class Player {
    //https://steamcommunity.com/inventory/76561198824034732/730/2?l=english
    private String steamID;
    private ArrayList<Item> inventory = new ArrayList<>();

    public Player(String steamID) {
        this.steamID = steamID;
    }

    public String constructURL() {
        return "https://steamcommunity.com/inventory/" + steamID + "/730/2?l=english";
    }

    private ArrayList<String> listInventory() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(constructURL())
                .build();
        String jsonData;
        try {
            Response response = client.newCall(request).execute();
            assert response.body() != null;
            jsonData = response.body().string();
        } catch (Exception e) {
            return new ArrayList<>();
        }

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            APIWrapper root = objectMapper.readValue(jsonData, APIWrapper.class);
            ArrayList<String> marketNames = new ArrayList<>();
            for(Details details : root.getDetails()) {
                marketNames.add(details.getMarketHashName());
            }
            return marketNames;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public void trackInventory(){
        ArrayList<String> itemString = listInventory();
        for(String item : itemString){
            String[] itemTokens = item.split("\\|");
            if(itemTokens.length == 1){
                continue;
            }
            String weapon = itemTokens[0].trim();
            String weaponData = itemTokens[1].trim();
            String skin;
            String wear;
            String[] skinTokens = weaponData.split("\\(");
            if(skinTokens.length == 2){
                skin = skinTokens[0].trim();
                wear = skinTokens[1];
            }else{
                continue;
            }
            inventory.add(new Item(weapon, skin, Wear.toWear(wear)));
        }
    }
}
