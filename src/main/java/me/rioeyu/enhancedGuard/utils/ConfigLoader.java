package me.rioeyu.enhancedGuard.utils;

import me.rioeyu.enhancedGuard.EnhancedGuard;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigLoader {
    private final EnhancedGuard plugin;
    private List<String> opList;
    private List<String> sensitivePermissions;
    private List<String> punishCommands;
    private List<String> blacklistedPrefixes;
    private List<String> blacklistedTerms;
    private List<String> blacklistCommands;
    private List<String> blockedChannels;
    private boolean disableOpCommand;
    private boolean blockGameModeCreative;
    private boolean stopServer;
    private boolean packetProtectionEnabled;
    private String language;

    public ConfigLoader(EnhancedGuard plugin) {
        this.plugin = plugin;
        reloadConfig();
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        // Load lists
        opList = config.getStringList("op_list");
        sensitivePermissions = config.getStringList("sensitive_permissions");
        punishCommands = config.getStringList("punish_command");
        blacklistedPrefixes = config.getStringList("blacklisted_prefixes");
        blacklistedTerms = config.getStringList("blacklisted_terms");
        blacklistCommands = config.getStringList("blacklist_commands");
        blockedChannels = config.getStringList("packet_protection.blocked_channels");

        // Load boolean values
        disableOpCommand = config.getBoolean("disable_op_command");
        blockGameModeCreative = config.getBoolean("block_gamemode_creative");
        stopServer = config.getBoolean("stop_server");
        packetProtectionEnabled = config.getBoolean("packet_protection.enabled", true);

        // Load language
        language = config.getString("language", "en");
    }

    // Existing getters
    public List<String> getOpList() {
        return opList;
    }

    public List<String> getSensitivePermissions() {
        return sensitivePermissions;
    }

    public List<String> getPunishCommands() {
        return punishCommands;
    }

    public List<String> getBlacklistedPrefixes() {
        return blacklistedPrefixes;
    }

    public List<String> getBlacklistedTerms() {
        return blacklistedTerms;
    }

    public List<String> getBlacklistCommands() {
        return blacklistCommands;
    }

    public List<String> getBlockedChannels() {
        return blockedChannels;
    }

    public boolean isDisableOpCommand() {
        return disableOpCommand;
    }

    public boolean isBlockGameModeCreative() {
        return blockGameModeCreative;
    }

    public boolean isStopServer() {
        return stopServer;
    }

    public boolean isPacketProtectionEnabled() {
        return packetProtectionEnabled;
    }

    // New language getter
    public String getLanguage() {
        return language;
    }
}