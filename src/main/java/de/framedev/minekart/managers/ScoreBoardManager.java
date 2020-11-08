package de.framedev.minekart.managers;

import de.framedev.minekart.listeners.PlayerMoveEffects;
import de.framedev.minekart.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Objects;

/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 11.07.2020 16:40
 */
public class ScoreBoardManager {

    private final Main plugin;
    private Scoreboard scoreBoard;

    public ScoreBoardManager(Main plugin) {
        this.plugin = plugin;
    }

    public void setScoreBoard() {
        this.scoreBoard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();
        Objective objective = scoreBoard.registerNewObjective("abc", "dummy", "abc");

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(plugin.getPrefix());

        // Erster Platz
        Team x1 = scoreBoard.registerNewTeam("x1");
        x1.setSuffix("");
        x1.setPrefix(" §7");
        x1.addEntry("§a");

        // Zweiter Platz
        Team x2 = scoreBoard.registerNewTeam("x2");
        x2.setSuffix("");
        x2.setPrefix(" §7");
        x2.addEntry("§b");
        // Dritter Platz
        Team x3 = scoreBoard.registerNewTeam("x3");
        x3.setSuffix("");
        x3.setPrefix(" §7");
        x3.addEntry("§c");

        // Scoreboard Daten Eintragung
        objective.getScore("§6== Platz Nummer §b1 ==").setScore(10);
        objective.getScore("§a").setScore(9);
        objective.getScore("§6== Platz Nummer §b2 ==").setScore(8);
        objective.getScore("§b").setScore(7);
        objective.getScore("§6== Platz Nummer §b3 ==").setScore(6);
        objective.getScore("§a").setScore(5);
    }

    public int getPlace(Player player) {
        Game game = plugin.getGameManager().getGames().get(0);
        Player nearest = getNearestPlayerFromLocation(player, player.getLocation());
        if (game.getPlayerRounds().get(nearest) < PlayerMoveEffects.getPlayerRounds().get(player)) {
            return 2;
        } else if (game.getPlayerRounds().get(nearest) > PlayerMoveEffects.getPlayerRounds().get(player)) {
            return 1;
        } else if (game.getPlayerRounds().get(nearest).equals(PlayerMoveEffects.getPlayerRounds().get(player))) {
            return 3;
        }
        return 8;
    }

    public static Player getNearestPlayerFromLocation(Player player, Location location) {
        Game game = Main.getInstance().getGameManager().getGames().get(0);
        double distance = 9999D;

        for (Player players : game.getPlayers()) {
            if (location.getWorld() != players.getWorld())
                continue;
            double dist = location.distance(players.getLocation());
            if (dist < distance) {
                distance = dist;
                player = players;
            }
        }
        player.sendMessage("Test");
        return player;
    }

    public void setPlayerScoreboard(Player player) {
        player.setScoreboard(scoreBoard);
    }
    public void scoreBoardUpdater(Player player) {
        plugin.getGameManager().getGames().get(0).getPlayers().forEach(current -> {
            for (int i = 3; i <= getPlace(player); i--) {
                if (i == 1) {
                    Scoreboard scoreboard = current.getScoreboard();
                    Team x1 = scoreboard.getTeam("x1");
                    if(x1 != null) {
                        x1.addEntry(ChatColor.AQUA.toString());
                        x1.setSuffix(" §a" + player.getName());
                    }
                } else if (i == 2) {
                        Scoreboard scoreboard = current.getScoreboard();
                        Team x2 = scoreboard.getTeam("x2");
                    if(x2 != null) {
                        x2.addEntry(ChatColor.YELLOW.toString());
                        x2.setSuffix(" §a" + player.getName());
                    }
                } else if (i == 3) {
                    Scoreboard scoreboard = current.getScoreboard();
                    Team x3 = scoreboard.getTeam("x3");
                    if(x3 != null) {
                        x3.addEntry(ChatColor.BLUE.toString());
                        x3.setSuffix(" §a" + player.getName());
                    }
                }
            }
        });
    }

    /**
     * Get the Scoreboard
     * @return Returns the Scoreboard
     */
    public Scoreboard getScoreBoard() {
        return scoreBoard;
    }
}
