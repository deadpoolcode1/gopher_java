package com.yourcompany.app;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Monitor {

    private static void processEntry(String entry, String action, String server, String username, String password, String dbName) {
        System.out.println("Action " + action + " was requested, table row is: " + entry);
    
        // Extract the 'id' from the entry, assuming it's the first value in a comma-separated entry
        String id = entry.split(",")[0];
    
        // Update query to set request_pending to 0 and timestamp_pending_process to the current timestamp
        String updateQuery1 = "UPDATE " + (action.equals("read") ? "read_data" : "write_data") +
                              " SET request_pending = 0, timestamp_pending_process = CURRENT_TIMESTAMP WHERE id = " + id;
    
        Database.executeNonQuery(dbName, updateQuery1, server, username, password);
    
        System.out.println("Processing LMDS start ID: " + id);
    
        if (action.equals("read")) {
            // Update query to set reply_pending to 1
            String updateQuery2 = "UPDATE read_data SET reply_pending = 1 WHERE id = " + id;
            Database.executeNonQuery(dbName, updateQuery2, server, username, password);
    
            boolean stopRequested = false;
            while (!stopRequested) {
                // Query to check if the stop request is made
                String checkStopQuery = "SELECT request_to_stop FROM read_data WHERE id = " + id;
                List<String> result = Database.executeQuery(dbName, checkStopQuery, server, username, password);
                
    
                // Check if the stop request is made
                if (!result.isEmpty() && "1".equals(result.get(0))) {
                    stopRequested = true;
                } else {
                    // Insert 'hello' into read_data_info and sleep for 1 second
                    String insertQuery = "INSERT INTO read_data_info (read_data_id, data) VALUES (" + id + ", 'hello')";
                    Database.executeNonQuery(dbName, insertQuery, server, username, password);
                    try {
                        Thread.sleep(1000); // Sleep for 1 second
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }
    
        System.out.println("Processing LMDS end ID: " + id);
    }
    
    

    public static boolean monitor() {
        String server = Config.getDBServer();
        String username = Config.getDBUsername();
        String password = Config.getDBPassword();
        String dbName = "my_data"; // Database name

        if (!Database.tableExists(dbName, "read_data", server, username, password) ||
            !Database.tableExists(dbName, "write_data", server, username, password)) {
            System.err.println("Error: Required tables do not exist in the database.");
            return false;
        }

        System.out.println("Monitoring started now.");

        ExecutorService executor = Executors.newCachedThreadPool();

        while (true) {
            List<String> readDataEntries = Database.executeQuery(dbName, "SELECT * FROM read_data WHERE request_pending = 1", server, username, password);
            for (String entry : readDataEntries) {
                executor.submit(() -> processEntry(entry, "read", server, username, password, dbName));
            }

            List<String> writeDataEntries = Database.executeQuery(dbName, "SELECT * FROM write_data WHERE request_pending = 1", server, username, password);
            for (String entry : writeDataEntries) {
                executor.submit(() -> processEntry(entry, "write", server, username, password, dbName));
            }

            try {
                Thread.sleep(5000); // Adjust the sleep duration as needed
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
    }
}
