package com.yourcompany.app;

import org.json.JSONObject;

public class MConfig {

    private static JSONObject configData = new JSONObject();

    public static void initialize(String dbServer, String dbUsername, String dbPassword) {
        configData.put("DB_SERVER", dbServer);
        configData.put("DB_USERNAME", dbUsername);
        configData.put("DB_PASSWORD", dbPassword);
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
