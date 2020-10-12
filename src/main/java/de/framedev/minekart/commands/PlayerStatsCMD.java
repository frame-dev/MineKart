package de.framedev.minekart.commands;

import de.framedev.minekart.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 22.07.2020 21:35
 */
public class PlayerStatsCMD implements CommandExecutor {

    private final Main plugin;

    String commandName = "playerstats";
    public PlayerStatsCMD(Main plugin) {
        this.plugin = plugin;
        plugin.getCommands().put(commandName, this);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase(commandName)) {
            if (sender.hasPermission("minekart.stats")) {
                if (args.length == 1) {
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                    sender.sendMessage(plugin.getPrefix() + "§aWins from §6" + target.getName() + " §c: §6" + plugin.getPlayerStats().getWins(target));
                    sender.sendMessage(plugin.getPrefix() + "§aYour won money §6:" + plugin.getPlayerStats().getWinsAmount(target));
                } else {
                    sender.sendMessage(plugin.getPrefix() + "§cPlease use §6/playerstats <PlayerName>§c!");
                }
            } else {
                sender.sendMessage(plugin.getPrefix() + "§cKeine Permissions!");
            }
        }
        return false;
    }
}
