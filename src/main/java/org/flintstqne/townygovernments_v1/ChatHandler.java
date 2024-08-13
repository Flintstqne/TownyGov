package org.flintstqne.townygovernments_v1;

import org.bukkit.ChatColor;

public class ChatHandler {
    private String prefix;
    public ChatHandler(String prefix) {
        this.prefix = prefix;
    }

    public String formatMessage(String message, ChatColor color) {
        return color + message;
    }

    public String prefixMessage(String message) {
        return ChatColor.GOLD + "[" + prefix + "] " + ChatColor.RESET + message;
    }

    public String formatError(String message) {
        return formatMessage(message, ChatColor.RED);
    }

    public String formatSuccess(String message) {
        return formatMessage(message, ChatColor.GREEN);
    }

    public String formatInfo(String message) {
        return formatMessage(message, ChatColor.BLUE);
    }

    public String formatWarning(String message) {
        return formatMessage(message, ChatColor.YELLOW);
    }
}