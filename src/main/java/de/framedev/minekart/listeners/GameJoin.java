package de.framedev.minekart.listeners;

import de.framedev.minekart.main.Main;
import de.framedev.minekart.managers.Holograms;
import de.framedev.minekart.managers.LocationsManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;

/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 22.07.2020 20:30
 */
public class GameJoin implements Listener {

    private final Main plugin;
    private Holograms holograms;

    public GameJoin(Main plugin) {
        this.plugin = plugin;
        plugin.getListeners().add(this);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (plugin.getConfig().getBoolean("BungeeCord") || plugin.isCloudNet()) {
            if(plugin.getGameManager().isStarted())
                event.getPlayer().kickPlayer("§cGame Gestartet");
            plugin.getLobbyManager().getLobbies().get(0).addPlayer(event.getPlayer());
            if(player.hasPermission("minekart.quickstart")) {
                player.getInventory().setItem(0,plugin.getInventory().getItem(0));
            }
            player.getInventory().setItem(8,plugin.getInventory().getItem(1));
        }
        ArrayList<String> holoText = new ArrayList<>();
        if (event.getPlayer().getLocale().equalsIgnoreCase("de_DE")) {
            holoText.add("§aDeine Stats");
            holoText.add("§aDeine Wins §6:" + plugin.getPlayerStats().getWins(event.getPlayer()));
            holoText.add("§aDein gewonnenes Geld §6:" + plugin.getPlayerStats().getWinsAmount(event.getPlayer()));
        } else if (event.getPlayer().getLocale().equalsIgnoreCase("en_EN")) {
            holoText.add("§aYour Stats");
            holoText.add("§aYour Wins §6:" + plugin.getPlayerStats().getWins(event.getPlayer()));
            holoText.add("§aYour won money §6:" + plugin.getPlayerStats().getWinsAmount(event.getPlayer()));
        }
        if (new LocationsManager().getCfg().contains("hologram")) {
            holograms = new Holograms(holoText, new LocationsManager("hologram").getLocation());
            holograms.showPlayer(event.getPlayer());
        }
    }
}
