package de.framedev.minekart.managers;

import de.framedev.minekart.main.Main;
import org.bukkit.World;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Game implements Serializable {

    private static final long serialVersionUID = -3535718090983081675L;

    /* PlayerList in Game */
    private final ArrayList<Player> players;

    /* Saved Pigs */
    private final HashMap<Player, Minecart> pigs;

    private final GameManager gameManager;

    private final ArrayList<World> finishedWorlds;

    private final HashMap<Player, Integer> playerRounds;

    private final HashMap<Player, ItemStack[]> oldItems;

    /*get Active World */
    private World activeWorld;
    private final Lobby lobby;

    public Game() {
        this.oldItems = new HashMap<>();
        this.finishedWorlds = new ArrayList<>();
        this.playerRounds = new HashMap<>();
        this.players = new ArrayList<>();
        this.pigs = new HashMap<>();
        this.lobby = new Lobby();
        gameManager = Main.getInstance().getGameManager();
        gameManager.getGames().add(this);

    }

    public HashMap<Player, ItemStack[]> getOldItems() {
        return oldItems;
    }

    public ArrayList<World> getFinishedWorlds() {
        return finishedWorlds;
    }

    public HashMap<Player, Integer> getPlayerRounds() {
        return playerRounds;
    }

    public World getActiveWorld() {
        return activeWorld;
    }

    public void setActiveWorld(World activeWorld) {
        this.activeWorld = activeWorld;
    }

    public HashMap<Player, Minecart> getPigs() {
        return pigs;
    }

    public boolean setGame(String cupName) {
        if (Main.getInstance().getGameManagerConfig().contains("Game." + cupName)) {
            this.cupName = cupName;
            return true;
        }
        return false;
    }

    public Game getGame() {
        return this;
    }

    private String cupName;

    public boolean createGame(String cupName) {
        if (!Main.getInstance().getGameManagerConfig().contains("Game." + cupName)) {
            this.cupName = cupName;
            this.lobby.saveLobby(cupName);
            return true;
        }
        return false;
    }

    public boolean setMaxPlayers(int maxPlayers) {
        if (!Main.getInstance().getGameManagerConfig().contains("Game." + cupName + ".MaxPlayers")) {
            Main.getInstance().getGameManagerConfig().set("Game." + cupName + ".MaxPlayers", maxPlayers);
            Main.getInstance().getGameManagerConfig().saveConfig();
            return true;
        }
        return false;
    }

    public boolean setMinPlayers(int minPlayers) {
        if (!Main.getInstance().getGameManagerConfig().contains("Game." + cupName + ".MinPlayers")) {
            Main.getInstance().getGameManagerConfig().set("Game." + cupName + ".MinPlayers", minPlayers);
            Main.getInstance().getGameManagerConfig().saveConfig();
            return true;
        }
        return false;
    }

    public int getMinPlayers() {
        if (Main.getInstance().getGameManagerConfig().contains("Game." + cupName + ".MinPlayers")) {
            return Main.getInstance().getGameManagerConfig().getInt("Game." + cupName + ".MinPlayers");
        }
        return 0;
    }

    public int getMaxPlayers() {
        if (Main.getInstance().getGameManagerConfig().contains("Game." + cupName + ".MaxPlayers")) {
            return Main.getInstance().getGameManagerConfig().getInt("Game." + cupName + ".MaxPlayers");
        }
        return 0;
    }

    public Lobby getGameLobby() {
        return Lobby.getLobbyFromFile(cupName);
    }

    public String getCupName() {
        if (cupName != null) {
            return cupName;
        }
        return "";
    }

    public void addPlayer(Player player) {
        if (!players.contains(player)) {
            players.add(player);
            gameManager.getGamePlayers().put(player, this);
        }
    }

    public void removePlayer(Player player) {
        if (players.contains(player)) {
            players.remove(player);
            if(Main.getInstance().getConfig().getBoolean("BungeeCord")) {
                new ServerSwitcher().connect(player,Main.getInstance().getConfig().getString("LobbyServer"));
            }
            gameManager.getGamePlayers().remove(player);
            sendMessageToGame("§6Game » §b" + player.getName() + " §ahat das Spiel verlassen!");
        }
    }

    public void removeGame() {
        if (players.size() == 1) {
            gameManager.getGames().remove(this);
        }
    }

    public void sendMessageToGame(String message) {
        for (Player player : players) {
            player.sendMessage(message);
        }
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void deleteFile(String cupName) {
        File f = new File(Main.getInstance().getDataFolder() + "/" + cupName, "minekart.game");
        if (f.exists()) {
            f.delete();
        }
    }

    public void saveGame(String cupName) {

        File f = new File(Main.getInstance().getDataFolder() + "/" + cupName, "minekart.game");
        f.getParentFile().mkdirs();
        try {
            FileOutputStream outputStream = new FileOutputStream(f);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(this);
            objectOutputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Game getGameFromFile(String cupName) throws NullPointerException {
        File f = new File(Main.getInstance().getDataFolder() + "/" + cupName, "minekart.game");
        Game game = null;
        FileInputStream fileInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            fileInputStream = new FileInputStream(f);
            objectInputStream = new ObjectInputStream(fileInputStream);
            game = (Game) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (game == null) {
            throw new NullPointerException("Game is Null");
        }
        return game;

    }
}
