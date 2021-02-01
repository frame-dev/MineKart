package de.framedev.minekart.managers;

import de.framedev.minekart.main.Main;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;

/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 23.06.2020 22:15
 */

public class Lobby implements Serializable {

    private static final long serialVersionUID = -7046907400600886158L;
    private final ArrayList<Player> players;
    private static Lobby lobby;

    public Lobby() {
        lobby = this;
        this.players = new ArrayList<>();
        Main.getInstance().getLobbyManager().getLobbies().add(this);
    }

    public void addPlayer(Player player) {
        if (!players.contains(player)) {
            players.add(player);
            Main.getInstance().getLobbyManager().getLobbyPlayers().put(player, this);
            sendMessageToLobby("§6Lobby » §b" + player.getName() + " §aist der Lobby beigetreten");
        }
    }

    public void removePlayer(Player player) {
        if (players.contains(player)) {
            removeLobby();
            players.remove(player);
            Main.getInstance().getLobbyManager().getLobbyPlayers().remove(player);
            sendMessageToLobby("§6Lobby » §b" + player.getName() + " §ahat die Lobby verlassen!");
        }
    }

    public void removeLobby() {
        if (players.size() == 1) {
            players.clear();
            Main.getInstance().getLobbyManager().getLobbies().remove(this);
        }
    }

    public void sendMessageToLobby(String message) {
        for (Player player : players) {
            player.sendMessage(message);
        }
    }

    public void saveLobby(String cupName) {

        File f = new File(Main.getInstance().getDataFolder() + "/" + cupName, "minekart.lobby");
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

    public static Lobby getLobbyFromFile(String cupName) throws NullPointerException {
        File f = new File(Main.getInstance().getDataFolder() + "/" + cupName, "minekart.lobby");
        Lobby lobby = null;
        FileInputStream fileInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            fileInputStream = new FileInputStream(f);
            objectInputStream = new ObjectInputStream(fileInputStream);
            lobby = (Lobby) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (lobby == null) {
            throw new NullPointerException("Lobby is Null");
        }
        return lobby;

    }

    public ArrayList<Player> getPlayers() {
        return players;
    }
}
