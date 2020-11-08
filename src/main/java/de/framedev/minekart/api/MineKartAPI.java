package de.framedev.minekart.api;

import de.framedev.minekart.listeners.PlayerMoveEffects;
import de.framedev.minekart.main.Main;
import de.framedev.minekart.managers.PlayerStats;
import de.framedev.minekart.managers.SpecialItem;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;

/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 30.07.2020 21:40
 */

public class MineKartAPI {

    private static MineKartAPI instance;
    private final Main plugin;

    public MineKartAPI() {
        instance = this;
        plugin = Main.getInstance();
    }

    public PlayerStats getPlayerStats() {
        return plugin.getPlayerStats();
    }

    public static MineKartAPI getInstance() {
        return instance;
    }

    public int getWins(OfflinePlayer player) {
        return plugin.getPlayerStats().getWins(player);
    }

    public int getWinAmount(OfflinePlayer player) {
        return plugin.getPlayerStats().getWinsAmount(player);
    }

    public boolean isPlayerInGame(Player player) {
        return plugin.getGameManager().getGames().get(0).getPlayers().contains(player);
    }

    public boolean isPlayerInLobby(Player player) {
        return plugin.getLobbyManager().getLobbies().get(0).getPlayers().contains(player);
    }

    public int getPlaceFromPlayer(Player player) {
        return plugin.getScoreBoardManager().getPlace(player);
    }

    public int getPlayerRounds(Player player) {
        if (isPlayerInGame(player)) {
            return PlayerMoveEffects.getPlayerRounds().get(player);
        }
        return 0;
    }
    public FileConfiguration getLanguageConfigByPlayer(Player player) {
        return plugin.getLanguageByPlayerConfig(player);
    }

    public int getGamePlayerSize() {
        if(plugin.getGameManager().getGames() != null && !plugin.getGameManager().getGames().isEmpty()) {
            return plugin.getGameManager().getGames().get(0).getPlayers().size();
        }
        return 0;
    }

    public boolean disableSpecialItem(String itemName) {
        for (SpecialItem specialItem : plugin.getSpecialItemsManager().getSpecialitems()) {
            if (specialItem.getDisplayName().equalsIgnoreCase(itemName)) {
                plugin.getSpecialItemsManager().removeSpecialItem(specialItem);
                switch (itemName) {
                    case "§aSpeeeeed!":
                        plugin.getConfig().set("SpecialItems.Mushroom", false);
                        plugin.saveConfig();
                        break;
                    case "§aBlitz!":
                        plugin.getConfig().set("SpecialItems.Thunder", false);
                        plugin.saveConfig();
                        break;
                    case "§aStern!":
                        plugin.getConfig().set("SpecialItems.Star", false);
                        plugin.saveConfig();
                        break;
                    case "§aBanana":
                        plugin.getConfig().set("SpecialItems.Banana", false);
                        plugin.saveConfig();
                        break;
                    case "§aBombe!":
                        plugin.getConfig().set("SpecialItems.Bomb", false);
                        plugin.saveConfig();
                        break;
                }
                return true;
            }
        }
        return false;
    }
    public HashMap<Player, Location> getPlayersCheckPoint() {
        return PlayerMoveEffects.checkPoints;
    }
}
