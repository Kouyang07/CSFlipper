package dev.kouyang07.data.api.steamcommunity;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kouyang07.data.api.steamcommunity.struct.Description;
import dev.kouyang07.data.api.steamcommunity.struct.RootWrapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayerInventory {
    //https://steamcommunity.com/inventory/76561198824034732/730/2?l=english
    private String steamID;

    public PlayerInventory(String steamID) {
        this.steamID = steamID;
    }

    public String constructURL() {
        return "https://steamcommunity.com/inventory/" + steamID + "/730/2?l=english";
    }

    public ArrayList<String> getInventory() {
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
            RootWrapper root = objectMapper.readValue(jsonData, RootWrapper.class);
            ArrayList<String> marketNames = new ArrayList<>();
            for(Description description : root.getDescriptions()) {
                marketNames.add(description.getMarketHashName());
            }
            return marketNames;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }
}
