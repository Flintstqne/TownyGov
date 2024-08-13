package org.flintstqne.townygovernments_v1;

import com.palmergames.bukkit.towny.object.Town;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class EconomicSystemManager {
    private DatabaseManager databaseManager;
    private Economy economy;
    boolean isFirstChange = true;

    public EconomicSystemManager(DatabaseManager databaseManager, Economy economy) {
        this.databaseManager = databaseManager;
        this.economy = economy;

    }

    public DatabaseManager getDatabaseManager() {
        return this.databaseManager;
    }

    public boolean chargePlayer(Player player, double amount) {
        try {
            String playerName = player.getName();
            boolean isFirstChange = databaseManager.getIsFirstChangeEconomy(playerName);
            System.out.println("chargePlayer called for player " + playerName + ", isFirstChange: " + isFirstChange); // Debugging statement

            if (isFirstChange) {
                //databaseManager.setIsFirstChangeEconomy(playerName, false);
                System.out.println("Setting isFirstChange to false for player " + playerName); // Debugging statement
                return true;
            }
            if (economy.has(player, amount)) {
                System.out.println("Player has enough money, charging player."); // Debugging statement
                economy.withdrawPlayer(player, amount);
                return true;
            } else {
                System.out.println("Player does not have enough money, not charging player."); // Debugging statement
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setEconomicSystem(Town town, String economicSystem) {
        try {
            databaseManager.setEconomicSystem(town.getName(), economicSystem);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getEconomicSystem(Town town) {
        try {
            return databaseManager.getEconomicSystem(town.getName());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}