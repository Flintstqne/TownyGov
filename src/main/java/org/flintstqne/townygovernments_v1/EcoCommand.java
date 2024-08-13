package org.flintstqne.townygovernments_v1;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.sql.SQLException;


public class EcoCommand implements CommandExecutor {
    private TownCreationListener townCreationListener;
    private ChatHandler chatHandler;
    private Main plugin; // Add this line
    private EconomicSystemManager economicSystemManager;


    public EcoCommand(TownCreationListener townCreationListener, Main plugin, EconomicSystemManager economicSystemManager) {
        this.townCreationListener = townCreationListener;
        this.chatHandler = new ChatHandler("TownyGov");
        this.plugin = plugin;
        this.economicSystemManager = economicSystemManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("teco")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.setMetadata("command", new FixedMetadataValue(plugin, "teco"));
                player.setMetadata("tecoCommandUsed", new FixedMetadataValue(plugin, true));
                try {
                    Resident resident = TownyAPI.getInstance().getResident(player.getName());
                    if (resident.hasTown()) {
                        Town town = resident.getTown();
                        if (resident.isMayor() || resident.hasTownRank("assistant")) {
                            if (player.hasMetadata("townCreated")) { // Check if the town has been created
                                // Set firstChange to false when /teco command is used
                                //economicSystemManager.getDatabaseManager().setIsFirstChangeEconomy(player.getName(), false);
                                if (economicSystemManager.chargePlayer(player, plugin.getEcoSystemChange_Amount())) {
                                    townCreationListener.getEconomySystemSelectionMenu(player).show(player);                                    player.removeMetadata("townCreated", plugin); // Remove the townCreated metadata
                                    return true;
                                } else {
                                    player.sendMessage(chatHandler.prefixMessage(chatHandler.formatError("You do not have enough money to change your economic system!")));
                                }
                            } else {
                                townCreationListener.getEconomySystemSelectionMenu(player).show(player);                                return true;
                            }
                        } else {
                            player.sendMessage(chatHandler.prefixMessage(chatHandler.formatError("You must be the mayor or an assistant to change the economic system!")));
                        }
                    } else {
                        player.sendMessage(chatHandler.prefixMessage(chatHandler.formatError("You must be part of a town to change the economic system!")));
                    }
                } catch (NotRegisteredException e) {
                    player.sendMessage(chatHandler.prefixMessage(chatHandler.formatError("You must be part of a town to change the economic system!")));
                }
            }
        }
        return true;
    }
}