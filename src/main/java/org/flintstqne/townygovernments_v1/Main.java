package org.flintstqne.townygovernments_v1;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownyUniverse;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class Main extends JavaPlugin {

    private Towny towny;
    private EconomicSystemManager economicSystemManager;
    private GovernmentSystemManager governmentSystemManager;
    private Economy economy;
    private double ecoSystemChange_Amount;
    private double govSystemChange_Amount;


    @Override
    public void onEnable() {
        try {
            DatabaseManager databaseManager = new DatabaseManager("towns.db");
            databaseManager.initializeDatabase();


            towny = (Towny) getServer().getPluginManager().getPlugin("Towny");
            if (towny == null) {
                getLogger().severe("[TownyGov] Towny not found, disabling plugin...");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
            if (!setupEconomy()) {
                getLogger().severe("[TownyGov] Disabled due to no Vault dependency found!");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
            else {
                getLogger().info("[TownyGov] Vault found, enabling plugin...");
            }
            governmentSystemManager = new GovernmentSystemManager(databaseManager, economy);
            economicSystemManager = new EconomicSystemManager(databaseManager, economy);
            getLogger().info("Towny found, enabling plugin...");
            TownyUniverse townyUniverse = TownyUniverse.getInstance();

            // Create an instance of TownCreationListener
            this.saveDefaultConfig();
            ecoSystemChange_Amount = this.getConfig().getDouble("ecoSystemChange_Amount");

            TownCreationListener townCreationListener = new TownCreationListener(economicSystemManager, economy, this, governmentSystemManager);

            // Register the TownCreationListener as an event listener
            getServer().getPluginManager().registerEvents(townCreationListener, this);
            getServer().getPluginManager().registerEvents(new TownListener(economicSystemManager, governmentSystemManager), this);

            // Command Registration
            this.getCommand("tgov").setExecutor(new GovCommand(townCreationListener, this, governmentSystemManager));
            this.getCommand("teco").setExecutor(new EcoCommand(townCreationListener, this, economicSystemManager));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public double getEcoSystemChange_Amount() {
        return ecoSystemChange_Amount;

    }

    public double getGovSystemChange_Amount() {
        return govSystemChange_Amount;

    }
    @Override
    public void onDisable() {
        // Any cleanup you need to do when your plugin is disabled
    }
}