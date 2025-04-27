package me.rioeyu.enhancedGuard.events;

import me.rioeyu.enhancedGuard.EnhancedGuard;
import me.rioeyu.enhancedGuard.antiexploit.PacketHandler;
import me.rioeyu.enhancedGuard.utils.BlacklistChecker;
import me.rioeyu.enhancedGuard.utils.Checker;
import me.rioeyu.enhancedGuard.utils.ConfigLoader;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Locale;

public class EventListener implements Listener {
    private final EnhancedGuard plugin;
    private final ConfigLoader configLoader;
    private final Checker checker;
    private final BlacklistChecker blacklistChecker;
    private final PacketHandler packetHandler;

    public EventListener(EnhancedGuard plugin, ConfigLoader configLoader, Checker checker, BlacklistChecker blacklistChecker) {
        this.plugin = plugin;
        this.configLoader = configLoader;
        this.checker = checker;
        this.blacklistChecker = blacklistChecker;
        this.packetHandler = new PacketHandler(plugin, configLoader, plugin.getMessages());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (configLoader.isPacketProtectionEnabled()) {
            packetHandler.injectPlayer(player);
        }

        blacklistChecker.handleBlacklistedPlayer(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                checker.run();
            }
        }.runTaskLater(plugin, 20L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (configLoader.isPacketProtectionEnabled()) {
            packetHandler.removePlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        if (configLoader.isBlockGameModeCreative() && event.getNewGameMode() == GameMode.CREATIVE) {
            Player player = event.getPlayer();
            if (!configLoader.getOpList().contains(player.getName())) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Bạn không có quyền sử dụng chế độ sáng tạo!");
            }
        }
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        if (configLoader.isDisableOpCommand() && event.getCommand().toLowerCase(Locale.ROOT).startsWith("op ")) {
            event.setCancelled(true);
            event.getSender().sendMessage(ChatColor.RED + "Lệnh này đã bị vô hiệu hoá!");
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (configLoader.isDisableOpCommand() && event.getMessage().toLowerCase(Locale.ROOT).startsWith("/op ")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Lệnh này đã bị vô hiệu hoá!");
        }
    }

    public void reloadConfig() {
        configLoader.reloadConfig();
        checker.reloadConfigData();
        blacklistChecker.reloadConfig();
    }
}