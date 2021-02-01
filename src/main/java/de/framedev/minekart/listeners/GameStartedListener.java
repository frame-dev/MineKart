package de.framedev.minekart.listeners;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudProxy;
import de.dytanic.cloudnet.bridge.CloudServer;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.server.ServerState;
import de.dytanic.cloudnetcore.CloudNet;
import de.framedev.minekart.main.Main;
import de.framedev.minekart.managers.Game;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.scheduler.BukkitRunnable;

/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 25.07.2020 22:15
 */
public class GameStartedListener implements Listener {

    private final Main plugin;

    public GameStartedListener(Main plugin) {
        this.plugin = plugin;
        plugin.getListeners().add(this);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!plugin.getGameManager().getGames().isEmpty()) {
            if (plugin.getGameManager().getGames().get(0).getPlayers().contains(event.getPlayer())) {
                if(event.getPlayer().isOnGround()) {
                    if (!plugin.getGameManager().isStarted()) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSwitchItem(PlayerSwapHandItemsEvent event) {
        if (!plugin.getGameManager().getGames().isEmpty()) {
            if (plugin.getGameManager().getGames().get(0).getPlayers().contains(event.getPlayer())) {
                event.setCancelled(true);
            }
        }
    }

    private static int startTime = 12;

    @SuppressWarnings("deprecation")
    public static void startGameScheduler(Game game) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (startTime <= 0) {
                    startTime = 12;
                    if (Main.getInstance().getGameManager().startGame(game)) {
                        game.getPlayers().forEach(players -> {
                            players.sendMessage("§aStart");
                        });
                        if(Main.getInstance().isCloudNet()) {
                            CloudServer server = CloudServer.getInstance();
                            server.setServerState(ServerState.INGAME);
                        }
                        cancel();
                    } else {
                        System.err.println("error");
                    }
                } else {
                    game.getPlayers().forEach(player -> {
                        player.sendTitle("§aSpiel startet in", "§6" + startTime);
                    });
                    startTime--;
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }
}
