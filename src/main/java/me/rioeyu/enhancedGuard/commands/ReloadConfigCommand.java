package me.rioeyu.enhancedGuard.commands;

import me.rioeyu.enhancedGuard.EnhancedGuard;
import me.rioeyu.enhancedGuard.events.EventListener;
import me.rioeyu.enhancedGuard.utils.Checker;
import me.rioeyu.enhancedGuard.utils.ConfigLoader;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ReloadConfigCommand implements CommandExecutor {
    private final EnhancedGuard plugin;
    private final ConfigLoader configLoader;
    private final Checker checker;
    private final EventListener eventListener;
    private final Set<String> cachedOpList;

    public ReloadConfigCommand(EnhancedGuard plugin, ConfigLoader configLoader, Checker checker, EventListener eventListener) {
        this.plugin = plugin;
        this.configLoader = configLoader;
        this.checker = checker;
        this.eventListener = eventListener;
        this.cachedOpList = new HashSet<>(configLoader.getOpList());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender) && sender instanceof Player player) {
            if (!cachedOpList.contains(player.getName())) {
                player.sendMessage(ChatColor.RED + "Bạn không có quyền thực hiện lệnh này!");
                return true;
            }
        }

        sender.sendMessage(ChatColor.YELLOW + "Đang reload config của EnhancedGuard...");

        CompletableFuture.runAsync(() -> {
            try {
                configLoader.reloadConfig();
                eventListener.reloadConfig();
                checker.reloadConfigData();

                sender.sendMessage(ChatColor.GREEN + "Đã reload config của EnhancedGuard thành công!");
                plugin.getLogger().info("Đã reload config của EnhancedGuard!");
            } catch (Exception e) {
                plugin.getLogger().severe("Đã xảy ra lỗi khi reload config của EnhancedGuard: " + e.getMessage());
                sender.sendMessage(ChatColor.RED + "Reload config của EnhancedGuard thất bại! Hãy kiểm tra console để biết thêm chi tiết.");
            }
        });

        return true;
    }
}