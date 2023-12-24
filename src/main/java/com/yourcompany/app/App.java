package com.yourcompany.app;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {

    public static boolean unittest() {
        String server = MConfig.getDBServer();
        String username = MConfig.getDBUsername();
        String password = MConfig.getDBPassword();
        String dbName = "TestDB";
        String tableName = "TestDB";

        boolean success = true;
        
        if (MConfig.getFakeDatabase()) {
            System.out.println("unittest passed (FAKEDATABASE is true)");
            return true;
        }

        if (!Database.tableExists(dbName, tableName, server, username, password)) {
            if (!Database.createTable(dbName, tableName, server, username, password)) {
                success = false;
            }
        }

        if (!Database.valueExistsInTable(dbName, tableName, "1", server, username, password)) {
            if (!Database.insertValueIntoTable(dbName, tableName, "1", "Value1", server, username, password)) {
                success = false;
            }
        }

        List<String> data = Database.readAllValuesFromTable(dbName, tableName, server, username, password);
        if (data.isEmpty() || !data.get(0).equals("Value1")) {
            success = false;
        }

        if (success) {
            System.out.println("unittest passed");
            return true;
        } else {
            System.err.println("unittest failed");
            return false;
        }
    }

        private static Map<String, String> parseArguments(String[] args) {
        Map<String, String> argMap = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            if (args[i].startsWith("--") && i + 1 < args.length) {
                argMap.put(args[i].substring(2), args[i + 1]);
            }
        }
        return argMap;
    }
    public static void main(String[] args) {
            Map<String, String> argMap = parseArguments(args);

            String dbServer = argMap.getOrDefault("dbServer", "defaultServer");
            String dbUsername = argMap.getOrDefault("dbUsername", "defaultUsername");
            String dbPassword = argMap.getOrDefault("dbPassword", "defaultPassword");

            // Initialize MConfig with parsed arguments
            MConfig.initialize(dbServer, dbUsername, dbPassword);
        if (!unittest()) {
            System.exit(1); // Exit with error code if unittest fails
        }
        Monitor.monitor();
        System.exit(0); // Exit with success code
    }
}
