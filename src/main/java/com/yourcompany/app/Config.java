package com.yourcompany.app;

import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;
import java.io.File;

public class Config {

    private static JSONObject configData = new JSONObject();

    public static void initialize(String configFilePath) {
        try {
            File configFile = new File(configFilePath);
            if (!configFile.exists()) {
                createDefaultConfig(configFilePath);
            }
            String content = new String(Files.readAllBytes(Paths.get(configFilePath)));
            configData = new JSONObject(content);
        } catch (IOException e) {
            System.err.println("Error reading config file: " + e.getMessage());
        }
    }

    private static void createDefaultConfig(String configFilePath) throws IOException {
        JSONObject defaultConfig = new JSONObject();
        defaultConfig.put("DB_SERVER", "localhost");
        defaultConfig.put("DB_USERNAME", "SA");
        defaultConfig.put("DB_PASSWORD", "Ss6399812");

        Files.write(Paths.get(configFilePath), defaultConfig.toString(4).getBytes(), StandardOpenOption.CREATE);
        System.out.println("Created default config file.");
    }

    public static String getDBServer() {
        return configData.optString("DB_SERVER", "localhost");
    }

    public static String getDBUsername() {
        return configData.optString("DB_USERNAME", "SA");
    }

    public static String getDBPassword() {
        return configData.optString("DB_PASSWORD", "default_password");
    }
}
