package dev.kouyang07.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kouyang07.data.structs.api.Details;
import dev.kouyang07.data.structs.APIWrapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;

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
}
