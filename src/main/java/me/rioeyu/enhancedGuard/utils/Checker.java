package me.rioeyu.enhancedGuard.utils;

import me.rioeyu.enhancedGuard.EnhancedGuard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class Checker implements Runnable {
    private final EnhancedGuard plugin;
    private final ConfigLoader configLoader;

    private final Set<String> opList = ConcurrentHashMap.newKeySet();
    private final Set<String> sensitivePermissions = ConcurrentHashMap.newKeySet();
    private final List<String> punishCommands = new CopyOnWriteArrayList<>();

    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public Checker(EnhancedGuard plugin, ConfigLoader configLoader) {
        this.plugin = plugin;
        this.configLoader = configLoader;
        reloadConfigData();
        startChecking();
    }

    public void startChecking() {
        if (executor.isShutdown() || executor.isTerminated()) {
            executor = Executors.newSingleThreadScheduledExecutor();
        }
        executor.scheduleAtFixedRate(this, 0, 150, TimeUnit.MILLISECONDS);
    }

    public void reloadConfigData() {
        opList.clear();
        sensitivePermissions.clear();
        punishCommands.clear();

        opList.addAll(configLoader.getOpList());
        sensitivePermissions.addAll(configLoader.getSensitivePermissions());
        punishCommands.addAll(configLoader.getPunishCommands());
    }

    @Override
    public void run() {
        if (Bukkit.getOnlinePlayers().isEmpty()) return;

        Bukkit.getOnlinePlayers().parallelStream().forEach(player -> {
            if (!opList.contains(player.getName()) && (player.isOp() || hasSensitivePermission(player))) {
                executePunishment(player);
            }
        });
    }

    private boolean hasSensitivePermission(Player player) {
        return player.getEffectivePermissions().stream()
                .map(PermissionAttachmentInfo::getPermission)
                .anyMatch(sensitivePermissions::contains);
    }

    private void executePunishment(Player player) {
        String playerName = player.getName();
        for (String command : punishCommands) {
            Bukkit.getScheduler().runTask(plugin, () ->
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", playerName))
            );
        }
    }

    public void stop() {
        executor.shutdown();
    }
}