package me.rioeyu.enhancedGuard.utils;

import me.rioeyu.enhancedGuard.EnhancedGuard;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

public class BlacklistChecker {
    private final EnhancedGuard plugin;
    private List<String> blacklistedPrefixes;
    private List<String> blacklistedTerms;
    private List<String> blacklistCommands;

    public BlacklistChecker(EnhancedGuard plugin) {
        this.plugin = plugin;
        reloadConfig();
    }

    public void reloadConfig() {
        FileConfiguration config = plugin.getConfig();
        blacklistedPrefixes = config.getStringList("blacklisted_prefixes");
        blacklistedTerms = config.getStringList("blacklisted_terms");
        blacklistCommands = config.getStringList("blacklist_commands");
    }

    public boolean isBlacklisted(String username) {
        String lowercaseUsername = username.toLowerCase(Locale.ROOT);

        for (String prefix : blacklistedPrefixes) {
            if (lowercaseUsername.startsWith(prefix.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }

        for (String term : blacklistedTerms) {
            if (lowercaseUsername.contains(term.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }

        return false;
    }

    public void handleBlacklistedPlayer(Player player) {
        if (isBlacklisted(player.getName())) {
            for (String command : blacklistCommands) {
                String finalCommand = command.replace("%player%", player.getName());
                Bukkit.getScheduler().runTask(plugin, () ->
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand)
                );
            }
        }
    }
}