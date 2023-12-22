package com.yourcompany.app;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private static void showSQLError(SQLException e) {
        System.err.println("SQL Error: " + e.getSQLState() + " : " + e.getMessage());
    }

    private static Connection connectToDatabase(String server, String username, String password) {
        try {
            String connectionString = "jdbc:sqlserver://" + server + ";user=" + username + ";password=" + password;
            return DriverManager.getConnection(connectionString);
        } catch (SQLException e) {
            showSQLError(e);
            return null;
        }
    }

    public static boolean executeNonQuery(String dbName, String query, String server, String username, String password) {
        try (Connection conn = connectToDatabase(server, username, password);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("USE " + dbName);
            stmt.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            showSQLError(e);
            return false;
        }
    }

    public static List<String[]> executeQueryMulti(String dbName, String query, String server, String username, String password) {
        List<String[]> results = new ArrayList<>();
        try (Connection conn = connectToDatabase(server, username, password);
             Statement stmt = conn.createStatement()) {
            stmt.execute("USE " + dbName);
            ResultSet rs = stmt.executeQuery(query);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                String[] row = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getString(i);
                }
                results.add(row);
            }
        } catch (SQLException e) {
            showSQLError(e);
        }
        return results;
    }
    
    public static List<String> executeQuery(String dbName, String query, String server, String username, String password) {
        List<String> results = new ArrayList<>();
        try (Connection conn = connectToDatabase(server, username, password);
             Statement stmt = conn.createStatement()) {
            stmt.execute("USE " + dbName);
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                results.add(rs.getString(1));
            }
        } catch (SQLException e) {
            showSQLError(e);
        }
        return results;
    }
    public static boolean createDatabase(String dbName, String server, String username, String password) {
        String createDBQuery = "CREATE DATABASE " + dbName;
        return executeNonQuery(dbName, createDBQuery, server, username, password);
    }

    public static String readValueFromTable(String dbName, String tableName, String key, String server, String username, String password) {
        String readQuery = "SELECT Value FROM " + tableName + " WHERE [Key] = '" + key + "'";
        List<String> result = executeQuery(dbName, readQuery, server, username, password);
        return result.isEmpty() ? "" : result.get(0);
    }

    public static boolean updateValueInTable(String dbName, String tableName, String key, String newValue, String server, String username, String password) {
        String updateQuery = "UPDATE " + tableName + " SET Value = '" + newValue + "' WHERE [Key] = '" + key + "'";
        return executeNonQuery(dbName, updateQuery, server, username, password);
    }

    public static boolean tableExists(String dbName, String tableName, String server, String username, String password) {
        String query = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = '" + tableName + "'";
        List<String> results = executeQuery(dbName, query, server, username, password);
        return !results.isEmpty();
    }

    public static boolean createTable(String dbName, String tableName, String server, String username, String password) {
        String query = "CREATE TABLE " + tableName + " ([Key] INT PRIMARY KEY, Value NVARCHAR(100));";
        return executeNonQuery(dbName, query, server, username, password);
    }

    public static boolean valueExistsInTable(String dbName, String tableName, String key, String server, String username, String password) {
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE [Key] = '" + key + "'";
        List<String> result = executeQuery(dbName, query, server, username, password);
        return !result.isEmpty() && !result.get(0).equals("0");
    }

    public static boolean insertValueIntoTable(String dbName, String tableName, String key, String value, String server, String username, String password) {
        String query = "INSERT INTO " + tableName + " ([Key], Value) VALUES ('" + key + "', '" + value + "');";
        return executeNonQuery(dbName, query, server, username, password);
    }

    public static List<String> readAllValuesFromTable(String dbName, String tableName, String server, String username, String password) {
        String query = "SELECT Value FROM " + tableName;
        return executeQuery(dbName, query, server, username, password);
    }
}
  
