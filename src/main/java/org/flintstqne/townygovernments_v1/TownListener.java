package org.flintstqne.townygovernments_v1;

import com.palmergames.bukkit.towny.event.TownRemoveResidentEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.SQLException;

public class TownListener<Resident> implements Listener {
    private EconomicSystemManager economicSystemManager;
    private GovernmentSystemManager governmentSystemManager;


    public TownListener(EconomicSystemManager economicSystemManager, GovernmentSystemManager governmentSystemManager) {
        this.economicSystemManager = economicSystemManager;
        this.governmentSystemManager = governmentSystemManager;

    }

    @EventHandler
    public void onPlayerLeaveTown(TownRemoveResidentEvent event) {
        Resident resident = (Resident) event.getResident();
        Player player = event.getResident().getPlayer();
        if (player != null) {
            try {
                economicSystemManager.getDatabaseManager().setIsFirstChangeEconomy(player.getName(), true);
                governmentSystemManager.getDatabaseManager().setIsFirstChangeGovernment(player.getName(), true);
                economicSystemManager.getDatabaseManager().setEconomicSystem(player.getName(), null);
                economicSystemManager.getDatabaseManager().removePlayerData(player.getName());
                governmentSystemManager.getDatabaseManager().setGovernmentSystem(player.getName(), null);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}