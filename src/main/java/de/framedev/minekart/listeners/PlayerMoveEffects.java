package de.framedev.minekart.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import de.framedev.minekart.main.Main;
import de.framedev.minekart.managers.Game;
import de.framedev.minekart.managers.LocationsManager;
import de.framedev.minekart.managers.ServerSwitcher;
import net.minecraft.server.v1_16_R1.PacketPlayInClientCommand;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 11.07.2020 10:22
 */
public class PlayerMoveEffects implements Listener {

    private final Main plugin;
    private static boolean started;
    private static final HashMap<Player, Integer> playerRounds = new HashMap<>();

    public static void setStarted(boolean start) {
        started = start;
    }

    public static boolean isStarted() {
        return started;
    }

    public PlayerMoveEffects(Main plugin) {
        this.plugin = plugin;
        plugin.getListeners().add(this);
    }

    private final HashMap<Player, Boolean> isFinish = new HashMap<>();
    private final HashMap<Player, Boolean> roundings = new HashMap<>();
    private final ArrayList<Player> finishedPlayers = new ArrayList<>();
    int time1 = 15;
    int time2 = 30;

    public static HashMap<Player, Integer> getPlayerRounds() {
        return playerRounds;
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getGameManager().getGames().isEmpty()) {
            if (plugin.getGameManager().getGames().get(0) != null) {
                Game game = plugin.getGameManager().getGames().get(0);
                if (plugin.getGameManager().getGames().get(0).getPlayers().contains(player)) {
                    if (event.getPlayer().getLocation().subtract(0, 0.5, 0).getBlock().getType() == Material.BARRIER) {
                        if (!checkPoints.isEmpty() && checkPoints.containsKey(event.getPlayer())) {
                            plugin.getGameManager().spawnPig(checkPoints.get(event.getPlayer()), game, event.getPlayer());
                        }
                        if (plugin.getGameManager().getGames().get(0).getPlayers().contains(event.getPlayer())) {
                            plugin.getGameManager().getGames().get(0).getPigs().remove(event.getPlayer());
                            Location location = plugin.getGameManager().getPigSpawnLocation(game, 1, game.getActiveWorld());
                            plugin.getGameManager().spawnPig(location, game, event.getPlayer());
                        }
                    }
                    if (plugin.getConfig().contains("ItemBlock")) {
                        Material material = Material.getMaterial(plugin.getConfig().getString("ItemBlock"));
                        if (material != null) {
                            if (event.getPlayer().getLocation().subtract(0, 0.5, 0).getBlock().getType() == material) {
                                if (time1 == 15) {
                                    event.getPlayer().getInventory().addItem(plugin.getGameManager().getRandomItemStack());
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            if (time1 <= 0) {
                                                time1 = 15;
                                                cancel();
                                            } else {
                                                time1--;
                                            }
                                        }
                                    }.runTaskTimerAsynchronously(plugin, 0, 20);
                                }
                            }
                        } else {
                            Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + "§cDieser Block existiert nicht! §6" + material.name());
                        }
                    } else {
                        Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + "§cKann kein ItemBlock in der Config gefunden werden!");
                    }
                    BlockVector3 blockVector3 = BlockVector3.at(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
                    RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
                    RegionQuery query = container.createQuery();
                    ApplicableRegionSet applicableRegionSet = query.getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));
                    for (ProtectedRegion regions : applicableRegionSet) {
                        if (regions.getId().equalsIgnoreCase(player.getWorld().getName() + "minekart")) {
                            if (regions.contains(blockVector3)) {
                                if (!playerRounds.containsKey(player)) {
                                    roundings.put(player, true);
                                    playerRounds.put(player, 0);
                                    if (time2 == 30) {
                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                if (time2 <= 0) {
                                                    time2 = 30;
                                                    cancel();
                                                    roundings.remove(player);
                                                    roundings.put(player, false);
                                                } else {
                                                    player.sendMessage(String.valueOf(time2));
                                                    time2--;
                                                }
                                            }
                                        }.runTaskTimerAsynchronously(plugin, 0, 20);
                                    }
                                }
                                if (!roundings.get(player)) {
                                    if (playerRounds.containsKey(player)) {
                                        if (playerRounds.get(player) == 0) {
                                            roundings.put(player, true);
                                            if (time2 == 30) {
                                                playerRounds.remove(player);
                                                playerRounds.put(player, 1);
                                                String rounding = plugin.getLanguageByPlayerConfig(player).getString("Rounding");
                                                rounding = rounding.replace('&', '§');
                                                rounding = rounding.replace("%player%", player.getName());
                                                rounding = rounding.replace("%round%", "" + playerRounds.get(player));
                                                plugin.getGameManager().getGames().get(0).sendMessageToGame(rounding);
                                                if (time2 == 30) {
                                                    new BukkitRunnable() {
                                                        @Override
                                                        public void run() {
                                                            if (time2 <= 0) {
                                                                time2 = 30;
                                                                cancel();
                                                                roundings.remove(player);
                                                                roundings.put(player, false);
                                                            } else {
                                                                time2--;
                                                            }
                                                        }
                                                    }.runTaskTimerAsynchronously(plugin, 0, 20);
                                                }
                                            }
                                        } else if (playerRounds.get(player) == 1) {
                                            roundings.put(player, true);
                                            playerRounds.remove(player);
                                            playerRounds.put(player, 2);
                                            String rounding = plugin.getLanguageByPlayerConfig(player).getString("Rounding");
                                            rounding = rounding.replace('&', '§');
                                            rounding = rounding.replace("%player%", player.getName());
                                            rounding = rounding.replace("%round%", "" + playerRounds.get(player));
                                            plugin.getGameManager().getGames().get(0).sendMessageToGame(rounding);
                                            if (time2 == 30) {
                                                new BukkitRunnable() {
                                                    @Override
                                                    public void run() {
                                                        if (time2 <= 0) {
                                                            time2 = 30;
                                                            cancel();
                                                            roundings.remove(player);
                                                            roundings.put(player, false);
                                                        } else {
                                                            time2--;
                                                        }
                                                    }
                                                }.runTaskTimerAsynchronously(plugin, 0, 20);
                                            }
                                        } else if (playerRounds.get(player) == 2) {
                                            roundings.put(player, true);
                                            playerRounds.remove(player);
                                            playerRounds.put(player, 3);
                                            String rounding = plugin.getLanguageByPlayerConfig(player).getString("Rounding");
                                            rounding = rounding.replace('&', '§');
                                            rounding = rounding.replace("%player%", player.getName());
                                            rounding = rounding.replace("%round%", "" + playerRounds.get(player));
                                            plugin.getGameManager().getGames().get(0).sendMessageToGame(rounding);

                                            if (time2 == 30) {
                                                new BukkitRunnable() {
                                                    @Override
                                                    public void run() {
                                                        if (time2 <= 0) {
                                                            time2 = 30;
                                                            cancel();
                                                            roundings.remove(player);
                                                            roundings.put(player, false);
                                                        } else {
                                                            time2--;
                                                        }
                                                    }
                                                }.runTaskTimerAsynchronously(plugin, 0, 20);
                                            }
                                        } else if (playerRounds.get(player) == 3) {
                                            roundings.put(player, true);
                                            playerRounds.remove(player);
                                            playerRounds.put(player, 0);
                                            finishedPlayers.add(player);
                                            if (finishedPlayers.size() == 0) {
                                                plugin.getGameManager().getGames().get(0).getPlayers().forEach(players -> {
                                                    String winMessage = plugin.getLanguageByPlayerConfig(players).getString("WinMessage");
                                                    winMessage = winMessage.replace('&', '§');
                                                    winMessage = winMessage.replace("%player%", player.getName());
                                                    players.sendTitle(winMessage, "");
                                                });
                                                plugin.getPlayerStats().addWin(player);
                                                int win = plugin.getConfig().getInt("Win");
                                                plugin.getApi().depositPlayer(player, win);
                                                int wonMoney = plugin.getPlayerStats().getWinsAmount(player);
                                                wonMoney = wonMoney + win;
                                                plugin.getPlayerStats().setWinsAmount(player, wonMoney);
                                            }
                                            String zielErreicht = plugin.getLanguageByPlayerConfig(player).getString("DetectFinish");
                                            zielErreicht = zielErreicht.replace('&', '§');
                                            zielErreicht = zielErreicht.replace("%player%", player.getName());
                                            plugin.getGameManager().getGames().get(0).sendMessageToGame(zielErreicht);
                                            isFinish.put(player, true);
                                            player.getVehicle().removePassenger(player);
                                            ((Pig) player.getVehicle()).setSaddle(false);
                                            ((Pig) player.getVehicle()).setHealth(0);
                                            player.setGameMode(GameMode.SPECTATOR);
                                            if (finishedPlayers.size() == plugin.getGameManager().getGames().get(0).getPlayers().size()) {
                                                if (plugin.getGameManager().getGames().get(0).getFinishedWorlds().size() == plugin.getGameManager().getMaps(plugin.getGameManager().getGames().get(0)).size()) {
                                                    finishedPlayers.forEach(players -> {
                                                        if(plugin.isCloudNet()) {
                                                            Bukkit.getServer().shutdown();
                                                            plugin.connectToCloudLobbyServer(player);
                                                        }
                                                        if (plugin.isBungeecord()) {
                                                            new ServerSwitcher().connect(players, plugin.getLobbyServer());
                                                        } else {
                                                            Location location = new LocationsManager(plugin.getGameManager().getGames().get(0).getCupName() + ".lobby").getLocation();
                                                            players.teleport(location);
                                                            ItemStack[] items = plugin.getGameManager().getGames().get(0).getOldItems().get(player);
                                                            plugin.getGameManager().getGames().get(0).getOldItems().remove(player);
                                                            player.getInventory().setContents(items);
                                                        }
                                                    });
                                                    finishedPlayers.clear();
                                                    plugin.getGameManager().getGames().get(0).getGameLobby().getPlayers().clear();
                                                    plugin.getGameManager().getGames().get(0).getPlayers().clear();
                                                } else {
                                                    game.getFinishedWorlds().add(player.getWorld());
                                                    ArrayList<String> nextMapsWorlds = new ArrayList<>();
                                                    for (String world : plugin.getGameManager().getMaps(game)) {
                                                        if (Bukkit.getWorld(world) != null) {
                                                            if (!world.equalsIgnoreCase(player.getWorld().getName())) {
                                                                if (!game.getFinishedWorlds().contains(Bukkit.getWorld(world))) {
                                                                    nextMapsWorlds.add(world);
                                                                }
                                                            }
                                                        }
                                                    }
                                                    game.setActiveWorld(plugin.getGameManager().pickRandomMap(game, nextMapsWorlds));
                                                    game.getPlayers().forEach(players -> plugin.getGameManager().spawnPigs(game,players));
                                                }
                                            }
                                            if (time2 == 30) {
                                                new BukkitRunnable() {
                                                    @Override
                                                    public void run() {
                                                        if (time2 <= 0) {
                                                            time2 = 30;
                                                            cancel();
                                                            roundings.remove(player);
                                                            roundings.put(player, false);
                                                        } else {
                                                            time2--;
                                                        }
                                                    }
                                                }.runTaskTimerAsynchronously(plugin, 0, 20);
                                            }
                                            plugin.getGameManager().setStarted(false);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(!checkPoints.isEmpty()) {
                        for (Location location : plugin.getGameManager().getCheckPoints(player.getWorld(), plugin.getGameManager().getGames().get(0))) {
                            if (plugin.getGameManager().isPlayerInCheckPoint(player, location)) {
                                checkPoints.remove(player);
                                checkPoints.put(player, location);
                            }
                        }
                    }
                }
            }
        }
    }

    /* CheckPoints ------------------------------------------- CheckPoints */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Respawn(event.getEntity().getPlayer(),60);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!plugin.getGameManager().getGames().isEmpty()) {
                    if (plugin.getGameManager().getGames().get(0) != null) {
                        if(!checkPoints.isEmpty() && checkPoints.containsKey(event.getEntity())) {
                            Game game = plugin.getGameManager().getGames().get(0);
                            plugin.getGameManager().spawnPig(checkPoints.get(event.getEntity()), game,event.getEntity().getPlayer());
                        }
                        if (plugin.getGameManager().getGames().get(0).getPlayers().contains(event.getEntity())) {
                            Game game = plugin.getGameManager().getGames().get(0);
                            plugin.getGameManager().getGames().get(0).getPigs().remove(event.getEntity().getPlayer());
                            Location location = plugin.getGameManager().getPigSpawnLocation(game, 1, game.getActiveWorld());
                            plugin.getGameManager().spawnPig(location, game,event.getEntity().getPlayer());
                        }
                    }
                }
            }
        }.runTaskLater(plugin,60);
    }

    public void Respawn(final Player player,int Time){
        Bukkit.getScheduler().runTaskLater(this.plugin, new Runnable() {

            @Override
            public void run() {
                ((CraftPlayer)player).getHandle().playerConnection.a(new PacketPlayInClientCommand(PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN));
            }
        },Time);
    }

    public static HashMap<Player, Location> checkPoints = new HashMap<>();

}
