package org.flintstqne.townygovernments_v1;

import java.sql.*;
import java.util.logging.Logger;

public class DatabaseManager {
    private Connection connection;
    private Logger logger = Logger.getLogger(DatabaseManager.class.getName());


    public DatabaseManager(String url) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + url);
    }

    public void initializeDatabase() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS towns (name TEXT PRIMARY KEY, economicSystem TEXT, governmentSystem TEXT)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.execute();
        } catch (SQLException e) {
            throw e;
        }

        sql = "CREATE TABLE IF NOT EXISTS playerData (playerName TEXT PRIMARY KEY, isFirstChangeEconomy BOOLEAN, isFirstChangeGovernment BOOLEAN)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.execute();
        } catch (SQLException e) {
            throw e;
        }

        // Check if the isFirstChangeEconomy column exists
        sql = "PRAGMA table_info(playerData)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            boolean isFirstChangeEconomyExists = false;
            while (rs.next()) {
                if ("isFirstChangeEconomy".equals(rs.getString("name"))) {
                    isFirstChangeEconomyExists = true;
                    break;
                }
            }

            // If the isFirstChangeEconomy column does not exist, add it
            if (!isFirstChangeEconomyExists) {
                sql = "ALTER TABLE playerData ADD COLUMN isFirstChangeEconomy BOOLEAN DEFAULT 1";
                try (PreparedStatement alterStmt = connection.prepareStatement(sql)) {
                    alterStmt.execute();
                }
            }
        } catch (SQLException e) {
            throw e;
        }


        // Check if the isFirstChangeGovernment column exists
        sql = "PRAGMA table_info(playerData)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            boolean isFirstChangeGovernmentExists = false;
            while (rs.next()) {
                if ("isFirstChangeEconomy".equals(rs.getString("name"))) {
                    isFirstChangeGovernmentExists = true;
                    break;
                }
            }

            // If the isFirstChangeEconomy column does not exist, add it
            if (!isFirstChangeGovernmentExists) {
                sql = "ALTER TABLE playerData ADD COLUMN isFirstChangeGovernment BOOLEAN DEFAULT 1";
                try (PreparedStatement alterStmt = connection.prepareStatement(sql)) {
                    alterStmt.execute();
                }
            }
        } catch (SQLException e) {
            throw e;
        }
    }


    public void setEconomicSystem(String townName, String economicSystem) throws SQLException {
        String sql = "INSERT OR REPLACE INTO towns(name, economicSystem) VALUES(?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, townName);
            stmt.setString(2, economicSystem);
            stmt.execute();
        }
    }

    public String getEconomicSystem(String townName) throws SQLException {
        String sql = "SELECT economicSystem FROM towns WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, townName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("economicSystem");
            } else {
                return null;
            }
        }
    }

    public boolean getIsFirstChangeEconomy(String playerName) throws SQLException {
        String sql = "SELECT isFirstChangeEconomy FROM playerData WHERE playerName = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("isFirstChangeEconomy");
            } else {
                return true; // Default to true if the player is not in the database
            }
        }
    }

    public boolean getIsFirstChangeGovernment(String playerName) throws SQLException {
        String sql = "SELECT isFirstChangeGovernment FROM playerData WHERE playerName = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("isFirstChangeGovernment");
            } else {
                return true; // Default to true if the player is not in the database
            }
        }
    }

    public void removePlayerData(String playerName) throws SQLException {
        String sql = "DELETE FROM playerData WHERE playerName = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerName);
            stmt.execute();
        }
    }

    public void setGovernmentSystem(String townName, String governmentSystem) throws SQLException {
        String sql = "UPDATE towns SET governmentSystem = ? WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, governmentSystem);
            stmt.setString(2, townName);
            stmt.execute();
        }
    }

    public String getGovernmentSystem(String townName) throws SQLException {
        String sql = "SELECT governmentSystem FROM towns WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, townName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("governmentSystem");
            } else {
                return null;
            }
        }
    }

    public void setIsFirstChangeEconomy(String playerName, boolean isFirstChangeEconomy) throws SQLException {
        String sql = "INSERT OR REPLACE INTO playerData (playerName, isFirstChangeEconomy) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerName);
            stmt.setBoolean(2, isFirstChangeEconomy);
            stmt.execute();
        }
    }

    public void setIsFirstChangeGovernment(String playerName, boolean isFirstChangeGovernment) throws SQLException {
        String sql = "INSERT OR REPLACE INTO playerData (playerName, isFirstChangeGovernment) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerName);
            stmt.setBoolean(2, isFirstChangeGovernment);
            stmt.execute();
        }
    }

    public boolean isSystemSelected(String townName, String systemType) throws SQLException {
        String sql = "SELECT " + systemType + " FROM towns WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, townName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString(systemType) != null;
            } else {
                return false;
            }
        }
    }

    public void dumpData() throws SQLException {
        // Log and delete data from towns table
        String sql = "SELECT * FROM towns";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String townName = rs.getString("name");
                String economicSystem = rs.getString("economicSystem");
                String governmentSystem = rs.getString("governmentSystem");
                System.out.println("Deleting town: " + townName + ", Economic System: " + economicSystem + ", Government System: " + governmentSystem);
            }
        }

        sql = "DELETE FROM towns";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }

        // Log and delete data from playerData table
        sql = "SELECT * FROM playerData";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String playerName = rs.getString("playerName");
                boolean isFirstChangeEconomy = rs.getBoolean("isFirstChangeEconomy");
                boolean isFirstChangeGovernment = rs.getBoolean("isFirstChangeGovernment");
            }
        }
    }
}