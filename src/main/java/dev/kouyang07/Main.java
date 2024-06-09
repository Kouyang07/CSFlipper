package dev.kouyang07;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kouyang07.data.items.Skin;
import dev.kouyang07.data.scraper.Scraper;
import dev.kouyang07.discord.Bot;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        //Once every 1.5 seconds
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Read JSON file and parse into Skin object
            List<Skin> skins = mapper.readValue(getSkins(), mapper.getTypeFactory().constructCollectionType(List.class, Skin.class));
            for (Skin skin : skins) {
                Skin.allSkins.putIfAbsent(skin.getName(), skin);
            }
        } catch (IOException ignored) {}
        System.out.println("-- Finished initializing --");
        new Bot();
    }

    private static String getSkins() {
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