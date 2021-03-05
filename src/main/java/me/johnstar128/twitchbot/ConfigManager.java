package me.johnstar128.twitchbot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import static me.johnstar128.twitchbot.TwitchBot.commands;
import static me.johnstar128.twitchbot.TwitchBot.gson;
import static me.johnstar128.twitchbot.TwitchBot.userDir;

public class ConfigManager {

    TwitchBot tb;

    public ConfigManager(TwitchBot tb) {
        this.tb = tb;
    }

    public static void generateConfigs() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File mainConfig = new File(userDir + "/config.json");
        File cmdConfig = new File(userDir + "/commands.json");
        String oauthKey = "oauth";
        String channelKey = "channel";
        String tokenVal = "token-goes-here";
        String channelVal = "#channel-goes-here";
        Map<String, String> cmdDefault = new HashMap<>();
        Map<String, String> authMap = new HashMap<>();
        BufferedWriter writer = null;
        // If command config file doesn't already exist in working directory, create it
        // Else, do nothing
        if (!(cmdConfig.exists())) {
            try {
                Files.createFile(Path.of(String.valueOf(cmdConfig)));
                try {
                    writer = Files.newBufferedWriter(Path.of(userDir + "/commands.json"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                gson.toJson(cmdDefault, writer);

                if (writer != null) {
                    writer.close();
                }
                Logger.getLogger("TwitchBot").info("Created commands config");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Logger.getLogger("TwitchBot").info("Commands file already exists");
        }

        // If main config file doesn't already exist in working directory, create it
        // Else, do nothing
        if (!(mainConfig.exists())) {
            try {
                Files.createFile(Path.of((String.valueOf(mainConfig))));
                authMap.put(oauthKey, tokenVal);
                authMap.put(channelKey, channelVal);
                try {
                    writer = Files.newBufferedWriter(Path.of(userDir + "/config.json"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                gson.toJson(authMap, writer);

                if (writer != null) {
                    writer.close();
                }

                Logger.getLogger("TwitchBot").info("Created main config");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Logger.getLogger("TwitchBot").info("Main configuration file already exists");
        }
    }

    // Load the oAuth token from config.json
    public String getOAuth() { return getValueFromConfig("config.json", "oauth"); }

    // Load channel name from config.json
    public static String getChannelName() { return getValueFromConfig("config.json", "channel"); }

    // Called whenever a command is added, add that command to commands.json
    @SuppressWarnings("deprecation")
    public static void writeValuesToConfig(String fileOut) {
        JsonParser parser = new JsonParser();
        InputStream in = null;
        try {
            in = new FileInputStream(userDir + "/" + fileOut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Reader reader = new InputStreamReader(in);
        JsonElement jsonElement = parser.parse(reader);
        JsonObject obj = jsonElement.getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();
        for (Map.Entry<String, JsonElement> entry : entries) {
            String title = entry.getKey();
            String returns = entry.getValue().getAsString();
            commands.put(title, returns);
        }
        BufferedWriter writer = null;
        try {
            writer = Files.newBufferedWriter(Path.of(userDir + "/" + fileOut));
        } catch (IOException e) {
            e.printStackTrace();
        }
        gson.toJson(commands, writer);

        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Called on bot startup to load oAuth token and channel name from config.json
    @SuppressWarnings("deprecation")
    public static String getValueFromConfig(String fileIn, String entryKey) {
        JsonParser parser = new JsonParser();
        InputStream in = null;
        try {
            in = new FileInputStream(userDir + "/" + fileIn);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Reader reader = new InputStreamReader(in);
        JsonElement jsonElement = parser.parse(reader);
        JsonObject obj = jsonElement.getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();
        for (Map.Entry<String, JsonElement> entry : entries) {
            if (entry.getKey().equalsIgnoreCase(entryKey)) {
                return entry.getValue().getAsString();
            }
        }
        return jsonElement.toString();
    }
}