package org.flintstqne.townygovernments_v1;


import com.palmergames.bukkit.towny.object.Town;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class GovernmentSystemManager {
    private DatabaseManager databaseManager;
    private Economy economy;

    public GovernmentSystemManager(DatabaseManager databaseManager, Economy economy) {
        this.databaseManager = databaseManager;
        this.economy = economy;
    }

    public DatabaseManager getDatabaseManager() {
        return this.databaseManager;
    }

    public boolean chargePlayer(Player player, double amount) {
        try {
            String playerName = player.getName();
            boolean isFirstChange = databaseManager.getIsFirstChangeGovernment(playerName);

            if (isFirstChange) {
                //databaseManager.setIsFirstChangeGovernment(playerName, false);
                System.out.println("Setting isFirstChange to false for player " + playerName); // Debugging statement
                return true;
            }
            if (economy.has(player, amount)) {
                economy.withdrawPlayer(player, amount);
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setGovernmentSystem(Town town, String governmentSystem) {
        try {
            databaseManager.setGovernmentSystem(town.getName(), governmentSystem);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getGovernmentSystem(Town town) {
        try {
            return databaseManager.getGovernmentSystem(town.getName());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}