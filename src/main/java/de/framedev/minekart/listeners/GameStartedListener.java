package de.framedev.minekart.listeners;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import de.dytanic.cloudnet.ext.signs.node.CloudNetSignsModule;
import de.dytanic.cloudnet.service.ICloudService;
import de.framedev.minekart.main.Main;
import de.framedev.minekart.managers.CloudNetManager;
import de.framedev.minekart.managers.Game;
import de.framedev.minekart.managers.SpecialItem;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
        game.getPlayers().forEach(player -> Main.getInstance().getGameManager().spawnPigs(game, player));
        new BukkitRunnable() {
            @Override
            public void run() {
                if (startTime <= 0) {
                    startTime = 12;
                    if (Main.getInstance().getGameManager().startGame(game)) {
                        game.getPlayers().forEach(players -> {
                            players.sendMessage("§aStart");
                            for(ItemStack stack : players.getInventory().getContents()) {
                                if(stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()) {
                                    if(stack.getItemMeta().getDisplayName().equalsIgnoreCase("§aQuickStart")) {
                                        players.getInventory().remove(stack);
                                    } else if(stack.getItemMeta().getDisplayName().equalsIgnoreCase("§cLeave")) {
                                        ItemMeta meta = stack.getItemMeta();
                                        meta.setDisplayName("§cLeave Game");
                                        stack.setItemMeta(meta);
                                    }
                                }
                            }
                            players.getInventory().setItem(0,new SpecialItem(Material.BONE).setDisplayName("§cReset").build());
                        });
                        if(Main.getInstance().isCloudNet()) {
                            new CloudNetManager().changeToIngame();
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
