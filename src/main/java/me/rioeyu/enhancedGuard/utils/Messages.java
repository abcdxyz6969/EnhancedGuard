// Messages.java
package me.rioeyu.enhancedGuard.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Messages {
    private final Plugin plugin;
    private FileConfiguration messages;
    private static final List<String> SUPPORTED_LANGUAGES = Arrays.asList("en", "vi");

    public Messages(Plugin plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    public void loadMessages() {
        File languageFolder = new File(plugin.getDataFolder(), "language");
        if (!languageFolder.exists()) {
            languageFolder.mkdirs();
        }

        // Get language from config, default to "en"
        String configLang = plugin.getConfig().getString("language", "en");

        // Setup base language file (English)
        String baseFile = "language/messages_en.yml";
        plugin.saveResource(baseFile, false);

        if (!configLang.equals("en")) {
            String langFile = "language/messages_" + configLang + ".yml";
            File customLangFile = new File(plugin.getDataFolder(), langFile);

            if (SUPPORTED_LANGUAGES.contains(configLang)) {
                // Built-in language
                plugin.saveResource(langFile, false);
                messages = YamlConfiguration.loadConfiguration(customLangFile);
            } else {
                // Custom language - create from English template if doesn't exist
                if (!customLangFile.exists()) {
                    File enFile = new File(plugin.getDataFolder(), baseFile);
                    try {
                        YamlConfiguration.loadConfiguration(enFile).save(customLangFile);
                    } catch (IOException e) {
                        plugin.getLogger().warning("Could not create custom language file: " + e.getMessage());
                        // Fallback to English
                        messages = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), baseFile));
                        return;
                    }
                }
                messages = YamlConfiguration.loadConfiguration(customLangFile);
            }
        } else {
            messages = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), baseFile));
        }
    }

    public String get(String path) {
        return messages.getString(path, "Missing message: " + path);
    }

    public FileConfiguration getMessages() {
        return messages;
    }
}