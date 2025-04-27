package me.rioeyu.enhancedGuard;

import me.rioeyu.enhancedGuard.antiexploit.PacketHandler;
import me.rioeyu.enhancedGuard.commands.ReloadConfigCommand;
import me.rioeyu.enhancedGuard.events.EventListener;
import me.rioeyu.enhancedGuard.utils.*;
import org.bukkit.plugin.java.JavaPlugin;

public class EnhancedGuard extends JavaPlugin {
    private ConfigLoader configLoader;
    private Checker checker;
    private EventListener eventListener;
    private BlacklistChecker blacklistChecker;
    private PacketHandler packetHandler;
    private Messages messages;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.configLoader = new ConfigLoader(this);
        this.messages = new Messages(this);
        this.checker = new Checker(this, configLoader);
        this.blacklistChecker = new BlacklistChecker(this);
        this.packetHandler = new PacketHandler(this, configLoader, messages);
        this.eventListener = new EventListener(this, configLoader, checker, blacklistChecker);

        getServer().getPluginManager().registerEvents(eventListener, this);
        getCommand("egreloadconfig").setExecutor(new ReloadConfigCommand(this, configLoader, checker, eventListener));

        new UpdateChecker(getDescription().getVersion(), getLogger()).checkForUpdate();
        getLogger().info("Plugin đã được kích hoạt!");
    }

    @Override
    public void onDisable() {
        if (checker != null) {
            checker.stop();
        }

        if (packetHandler != null && configLoader.isPacketProtectionEnabled()) {
            getServer().getOnlinePlayers().forEach(packetHandler::removePlayer);
        }

        if (getConfig().getBoolean("stop_server") && !getServer().isStopping()) {
            getLogger().warning("Plugin đã bị vô hiệu hoá! Đang dừng server...");
            getServer().shutdown();
        }
    }

    public PacketHandler getPacketHandler() {
        return packetHandler;
    }

    public Messages getMessages() {
        return messages;
    }
}