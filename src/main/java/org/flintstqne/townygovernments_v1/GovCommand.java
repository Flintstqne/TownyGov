package org.flintstqne.townygovernments_v1;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.sql.SQLException;


public class GovCommand implements CommandExecutor {
    private TownCreationListener townCreationListener;
    private ChatHandler chatHandler;
    private Main plugin; // Add this line
    private GovernmentSystemManager governmentSystemManager;


    public GovCommand(TownCreationListener townCreationListener, Main plugin, GovernmentSystemManager governmentSystemManager) {
        this.townCreationListener = townCreationListener;
        this.chatHandler = new ChatHandler("TownyGov");
        this.plugin = plugin;
        this.governmentSystemManager = governmentSystemManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("tgov")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.setMetadata("command", new FixedMetadataValue(plugin, "tgov"));
                try {
                    Resident resident = TownyAPI.getInstance().getResident(player.getName());
                    if (resident.hasTown()) {
                        Town town = resident.getTown();
                        if (resident.isMayor() || resident.hasTownRank("assistant")) {
                            if (player.hasMetadata("townCreated")) { // Check if the town has been created
                                // Set firstChange to false when /tgov command is used
                                //governmentSystemManager.getDatabaseManager().setIsFirstChangeGovernment(player.getName(), false);
                                if (governmentSystemManager.chargePlayer(player, plugin.getGovSystemChange_Amount())) {
                                    townCreationListener.getGovernmentSystemSelectionMenu(player).show(player);
                                    player.removeMetadata("townCreated", plugin); // Remove the townCreated metadata
                                    return true;
                                } else {
                                    player.sendMessage(chatHandler.prefixMessage(chatHandler.formatError("You do not have enough money to change your government system!")));
                                }
                            } else {
                                townCreationListener.getGovernmentSystemSelectionMenu(player).show(player);                                return true;
                            }
                        } else {
                            player.sendMessage(chatHandler.prefixMessage(chatHandler.formatError("You must be the mayor or an assistant to change the government system!")));
                        }
                    } else {
                        player.sendMessage(chatHandler.prefixMessage(chatHandler.formatError("You must be part of a town to change the government system!")));
                    }
                } catch (NotRegisteredException e) {
                    player.sendMessage(chatHandler.prefixMessage(chatHandler.formatError("You must be part of a town to change the government system!")));
                }
            }
        }
        return true;
    }}