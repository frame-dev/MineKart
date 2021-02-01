package de.framedev.minekart.managers;

import de.framedev.minekart.listeners.GameStartedListener;
import de.framedev.minekart.listeners.PlayerMoveEffects;
import de.framedev.minekart.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 23.06.2020 22:15
 */
public class LobbyManager implements Listener {

    private final ArrayList<Lobby> lobbies;
    private final HashMap<Player, Lobby> lobbyPlayers;
    private final HashMap<Game, Lobby> lobbyGames;

    public LobbyManager() {
        this.lobbies = new ArrayList<>();
        this.lobbyPlayers = new HashMap<>();
        this.lobbyGames = new HashMap<>();
        Main.getInstance().getListeners().add(this);
    }

    public HashMap<Game, Lobby> getLobbyGames() {
        return lobbyGames;
    }

    public ArrayList<Lobby> getLobbies() {
        return lobbies;
    }

    private final HashMap<Game, BukkitTask> tasks = new HashMap<>();

    public HashMap<Player, Lobby> getLobbyPlayers() {
        return lobbyPlayers;
    }

    public List<String> getMaps(Game game) {
        return Main.getInstance().getGameManager().getMaps(game);
    }

    public void addPlayerToGame(Game game) {
        lobbies.get(0).getPlayers().forEach(game::addPlayer);
    }

    int time = 45;

    public void startLobby(Game game) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (time <= 0) {
                    if (hasGameEnoughPlayers(game)) {
                        addPlayerToGame(game);
                        for (Player player : game.getPlayers()) {
                            Main.getInstance().getGameManager().getGames().get(0).getPlayerRounds().put(player, 0);
                        }
                        /*game.getPlayers().forEach(player -> {
                            Main.getInstance().getScoreBoardManager().setScoreBoard();
                            Main.getInstance().getScoreBoardManager().setPlayerScoreboard(player);
                            Main.getInstance().getScoreBoardManager().scoreBoardUpdater(player);
                            Main.getInstance().log(Main.getInstance().getPrefix() + "§aScoreboard is Now Updating!");
                            // Debugging Scoreboard$$
                            Main.getInstance().debug(Main.getInstance().getScoreBoardManager().getScoreBoard());
                        });*/
                        PlayerMoveEffects.setStarted(true);
                        GameStartedListener.startGameScheduler(game);
                        cancel();
                        time = 60;
                        Bukkit.getScheduler().cancelTask(getTaskId());
                    } else {
                        Main.getInstance().updateGame();
                        Bukkit.getScheduler().cancelTask(getTaskId());
                        cancel();
                        time = 60;
                    }
                } else {
                    if (!hasGameEnoughPlayers(game)) {
                        Main.getInstance().updateGame();
                        Bukkit.getScheduler().cancelTask(getTaskId());
                        cancel();
                        time = 60;
                    } else {
                        time--;
                    }
                    if (time == 30) {
                        if (!hasGameEnoughPlayers(game)) {
                            Main.getInstance().updateGame();
                            Bukkit.getScheduler().cancelTask(getTaskId());
                            cancel();
                            time = 60;
                        } else {
                            game.setActiveWorld(Main.getInstance().getGameManager().pickRandomMap(game));
                            if (Main.getInstance().getGameManager().hasMapName(game, game.getActiveWorld())) {
                                lobbies.get(0).sendMessageToLobby("§6Game » §aDie §bMap §6" + Main.getInstance().getGameManager().getMapName(game, game.getActiveWorld()) + " §awurde gewählt!");
                            } else {
                                lobbies.get(0).sendMessageToLobby("§6Game » §aDie §bMap §6" + game.getActiveWorld().getName() + " §awurde gewählt!");
                            }
                        }
                    }
                    lobbies.get(0).getPlayers().forEach(current -> {
                        current.setLevel(time);
                    });
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

    public boolean hasGameEnoughPlayers(Game game) {
        return lobbies.get(0).getPlayers().size() >= game.getMinPlayers();
    }

    // Wait time before the new Check is Running if it has enough Player
    private int waitTime = 60;

    public void checkEnoughPlayer(Game game) {
        tasks.put(game, new BukkitRunnable() {
            @Override
            public void run() {
                if (waitTime <= 0) {
                    if (hasGameEnoughPlayers(game)) {
                        startLobby(game);
                        tasks.get(game).cancel();
                        tasks.remove(game);
                        cancel();
                    }
                } else {
                    waitTime--;
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20));
    }

    /*public boolean waitForLobby(Game game) {
        if (tasks.containsKey(game)) {
            return tasks.get(game) != null;
        }
        return false;
    }*/

    public void registerEvents() {
        Main.getInstance().getListeners().add(this);
    }

    @EventHandler
    public void onPlayerBlockBreak(BlockBreakEvent event) {
        if (!lobbies.isEmpty()) {
            if (lobbies.get(0).getPlayers().contains(event.getPlayer())) {
                event.setCancelled(true);
            }
        }
        if (!Main.getInstance().getGameManager().getGames().isEmpty()) {
            if (Main.getInstance().getGameManager().getGames().get(0).getPlayers().contains(event.getPlayer())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (!lobbies.isEmpty()) {
                if (lobbies.get(0).getPlayers().contains(player)) {
                    event.setCancelled(true);
                }
            }
            if (!Main.getInstance().getGameManager().getGames().isEmpty()) {
                if (Main.getInstance().getGameManager().getGames().get(0).getPlayers().contains(player)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerPickup(EntityPickupItemEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (!lobbies.isEmpty()) {
                if (lobbies.get(0).getPlayers().contains(player)) {
                    event.setCancelled(true);
                }
            }
            if (!Main.getInstance().getGameManager().getGames().isEmpty()) {
                if (Main.getInstance().getGameManager().getGames().get(0).getPlayers().contains(player)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!lobbies.isEmpty()) {
            if (lobbies.get(0).getPlayers().contains(event.getPlayer())) {
                event.setCancelled(true);
            }
        }
        if (!Main.getInstance().getGameManager().getGames().isEmpty()) {
            if (Main.getInstance().getGameManager().getGames().get(0).getPlayers().contains(event.getPlayer())) {
                event.setCancelled(true);
            }
        }
    }
}
