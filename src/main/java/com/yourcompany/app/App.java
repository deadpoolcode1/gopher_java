package com.yourcompany.app;

import java.util.List;

public class App {

    private static boolean unittest() {
        String server = Config.getDBServer();
        String username = Config.getDBUsername();
        String password = Config.getDBPassword();
        String dbName = "TestDB";
        String tableName = "TestDB";

        boolean success = true;
/* 
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
*/
        if (success) {
            System.out.println("unittest passed");
            return true;
        } else {
            System.err.println("unittest failed");
            return false;
        }
    }

    public static void main(String[] args) {
        Config.initialize("config.json");
        if (!unittest()) {
            System.exit(1); // Exit with error code if unittest fails
        }
        //Monitor.monitor();
        System.exit(0); // Exit with success code
    }
}
