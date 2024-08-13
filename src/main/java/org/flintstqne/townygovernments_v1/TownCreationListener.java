package org.flintstqne.townygovernments_v1;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.event.NewTownEvent;
import com.palmergames.bukkit.towny.event.statusscreen.TownStatusScreenEvent;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TownCreationListener implements Listener {
    private Main plugin;
    private EconomicSystemManager economicSystemManager;
    private Economy economy;
    private ChatHandler chatHandler;
    private GovernmentSystemManager governmentSystemManager;


    public TownCreationListener(EconomicSystemManager economicSystemManager, Economy economy, Main plugin, GovernmentSystemManager governmentSystemManager){
        this.economicSystemManager = economicSystemManager;
        this.plugin = plugin;
        this.economy = economy;
        this.chatHandler = new ChatHandler("TownyGov");
        this.governmentSystemManager = governmentSystemManager;

    }

    public ChestGui getEconomySystemSelectionMenu(Player player) {
        ChestGui gui = new ChestGui(1, ChatColor.DARK_GREEN + "Select Economy System");
        StaticPane pane = new StaticPane(9, 1);

        String[] systems = {"Capitalism", "Communism", "Socialism"};
        Material[] materials = {Material.DIAMOND, Material.REDSTONE, Material.GOLD_INGOT};
        ChatColor[] colors = {ChatColor.AQUA, ChatColor.RED, ChatColor.GOLD};

        try {
            Resident resident = TownyAPI.getInstance().getResident(player.getName());
            Town town = resident.getTown();
            String currentEconomicSystem = economicSystemManager.getEconomicSystem(town);

            for (int i = 0; i < systems.length; i++) {
                final int index = i;
                ItemStack item = new ItemStack(materials[i]);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(colors[i] + systems[i]);
                List<String> lore = new ArrayList<>();
                boolean isFirstChange = economicSystemManager.getDatabaseManager().getIsFirstChangeEconomy(player.getName());
                if (systems[i].equals(currentEconomicSystem)) {
                    lore.add(ChatColor.WHITE + "Current Selection");
                } else if (!isFirstChange) {
                    lore.add(ChatColor.WHITE + "" + ChatColor.BOLD + "Change your economic system for: " + plugin.getEcoSystemChange_Amount());
                }

                meta.setLore(lore);
                item.setItemMeta(meta);

                GuiItem guiItem = new GuiItem(item, event -> {
                    event.setCancelled(true);
                    Player clicker = (Player) event.getWhoClicked();
                    try {
                        String newEconomicSystem = systems[index];

                        if (newEconomicSystem.equals(currentEconomicSystem)) {
                            clicker.sendMessage(chatHandler.prefixMessage(chatHandler.formatError("You are already " + currentEconomicSystem + "!")));
                            return;
                        }

                        if (currentEconomicSystem == null || economicSystemManager.chargePlayer(clicker, plugin.getEcoSystemChange_Amount())) {
                            economicSystemManager.setEconomicSystem(town, newEconomicSystem);
                            clicker.sendMessage(chatHandler.prefixMessage(chatHandler.formatSuccess("You have chosen " + newEconomicSystem + " as your economic system!")));
                            if (!isFirstChange) {
                                double balance = economy.getBalance(clicker);
                                clicker.sendMessage(chatHandler.prefixMessage(chatHandler.formatInfo("Your new balance is: " + balance)));
                            }
                            clicker.closeInventory();
                            clicker.setMetadata("economySelected", new FixedMetadataValue(plugin, true));
                        } else {
                            clicker.sendMessage(chatHandler.prefixMessage(chatHandler.formatError("You do not have enough money to change your economic system!")));
                        }
                    } catch (Exception e) {
                        clicker.sendMessage(chatHandler.prefixMessage(chatHandler.formatError("You are not part of a town!")));
                    }
                });

                pane.addItem(guiItem, i, 0);
            }
        } catch (NotRegisteredException | SQLException e) {
            e.printStackTrace();
        }

        gui.addPane(pane);
        return gui;
    }


    public ChestGui getGovernmentSystemSelectionMenu(Player player) {
        ChestGui gui = new ChestGui(1, "Select Government System");
        StaticPane pane = new StaticPane(9, 1);

        try {
            Resident resident = TownyAPI.getInstance().getResident(player.getName());
            Town town = resident.getTown();
            String currentGovernmentSystem = governmentSystemManager.getGovernmentSystem(town);
            if (resident.hasTown()) {
                String currentSystem = governmentSystemManager.getGovernmentSystem(town);

                // Create items for each system
                String[] systems = {"Democracy", "Autocracy", "Buerocracy"};
                Material[] materials = {Material.PAPER, Material.NETHERITE_AXE, Material.SPYGLASS};
                ChatColor[] colors = {ChatColor.AQUA, ChatColor.RED, ChatColor.GOLD};

                for (int i = 0; i < systems.length; i++) {
                    final int index = i;
                    ItemStack item = new ItemStack(materials[i]);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(colors[i] + systems[i]);

                    List<String> lore = new ArrayList<>();
                    boolean isFirstChange = governmentSystemManager.getDatabaseManager().getIsFirstChangeGovernment(player.getName());
                    if (systems[i].equals(currentSystem)) {
                        lore.add(ChatColor.WHITE + "Current Selection");
                    } else if (!isFirstChange) {
                        lore.add(ChatColor.WHITE + "" + ChatColor.BOLD + "Change your government system for: " + plugin.getGovSystemChange_Amount());
                    }

                    meta.setLore(lore);
                    item.setItemMeta(meta);
                    GuiItem guiItem = new GuiItem(item, event -> {
                        event.setCancelled(true);
                        Player clicker = (Player) event.getWhoClicked();
                        try {
                            String newGovernmentSystem = systems[index];

                            if (newGovernmentSystem.equals(currentGovernmentSystem)) {
                                clicker.sendMessage(chatHandler.prefixMessage(chatHandler.formatError("You are already " + currentGovernmentSystem + "!")));
                                return;
                            }

                            if (currentGovernmentSystem == null || economicSystemManager.chargePlayer(clicker, plugin.getEcoSystemChange_Amount())) {
                                economicSystemManager.setEconomicSystem(town, newGovernmentSystem);
                                clicker.sendMessage(chatHandler.prefixMessage(chatHandler.formatSuccess("You have chosen " + newGovernmentSystem + " as your economic system!")));
                                if (!isFirstChange) {
                                    double balance = economy.getBalance(clicker);
                                    clicker.sendMessage(chatHandler.prefixMessage(chatHandler.formatInfo("Your new balance is: " + balance)));
                                }
                                clicker.closeInventory();
                                clicker.setMetadata("economySelected", new FixedMetadataValue(plugin, true));
                            } else {
                                clicker.sendMessage(chatHandler.prefixMessage(chatHandler.formatError("You do not have enough money to change your economic system!")));
                            }
                        } catch (Exception e) {
                            clicker.sendMessage(chatHandler.prefixMessage(chatHandler.formatError("You are not part of a town!")));
                        }
                    });
                    pane.addItem(guiItem, i, 0);
                }
            }
        } catch (NotRegisteredException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        gui.addPane(pane);
        return gui;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTownScreen(TownStatusScreenEvent event) {
        Town town = event.getTown();
        String economicSystem = economicSystemManager.getEconomicSystem(town);
        String governmentSystem = governmentSystemManager.getGovernmentSystem(town);
        event.addLine(ChatColor.GOLD + "Economic System: " + ChatColor.WHITE + economicSystem);
        event.addLine(ChatColor.GOLD + "Government System: " + ChatColor.WHITE + governmentSystem);

    }

    @EventHandler
    public void onNewTown(NewTownEvent event) {
        Town town = event.getTown();
        try {
            Resident mayor = town.getMayor();
            Player player = Bukkit.getPlayer(mayor.getName());
            if (player != null) {
                getEconomySystemSelectionMenu(player).show(player);

                // Create a new Bukkit Runnable
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        // Check if the player has made a selection in the economy menu
                        if (player.hasMetadata("economySelected")) {
                            // If a selection has been made, open the government menu
                            getGovernmentSystemSelectionMenu(player).show(player);
                            // Remove the metadata value
                            player.removeMetadata("economySelected", plugin);
                            // Cancel the runnable
                            this.cancel();
                        }
                    }
                }.runTaskTimer(plugin, 0L, 20L); // Run the task every second (20 ticks = 1 second)
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
// TODO: Make sure that the database is added to the config folder, not the root folder


}