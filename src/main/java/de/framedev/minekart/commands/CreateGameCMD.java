package de.framedev.minekart.commands;

import de.framedev.minekart.listeners.PlayerClickItem;
import de.framedev.minekart.main.Main;
import de.framedev.minekart.managers.Game;
import de.framedev.minekart.managers.LocationsManager;
import de.framedev.minekart.managers.ServerSwitcher;
import de.framedev.minekart.managers.SpecialItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CreateGameCMD implements CommandExecutor, Listener, TabCompleter {

    private final HashMap<Player, String> commands = new HashMap<>();
    private final Main plugin;

    public CreateGameCMD(Main plugin) {
        this.plugin = plugin;
        plugin.getCommands().put("minekart", this);
        plugin.getTabCompleters().put("minekart", this);
        plugin.getListeners().add(this);
    }

    Game game;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("create")) {
                    if (sender.hasPermission("minekart.admin.create")) {
                        game = new Game();
                        if (game.createGame(args[1])) {
                            commands.put((Player) sender, "maxplayers");
                            sender.sendMessage("§aBitte gebe in den Chat ein wie viele Spieler Maximal Spielen dürfen!");
                        } else {
                            sender.sendMessage("§cDieses Spiel existiert Bereits!");
                        }
                    } else {
                        sender.sendMessage(plugin.getPrefix() + "§cKeine Permissions!");
                    }
                }
                if (args[0].equalsIgnoreCase("addmap")) {
                    if (sender.hasPermission("minekart.admin.addmap")) {
                        Game games = Main.getInstance().getGameManager().getGames().get(0);
                        if (Main.getInstance().getGameManager().addMap(games, ((Player) sender).getWorld())) {
                            sender.sendMessage("§aZum Game §6" + games.getCupName() + " §awurde die Karte §6" + ((Player) sender).getWorld().getName() + " §ahinzugefügt!");
                        } else {
                            sender.sendMessage("§cEs ist bereits eine Map mit diesem Namen gespeichert!");
                        }
                    } else {
                        sender.sendMessage(plugin.getPrefix() + "§cKeine Permissions!");
                    }
                }
                if (args[0].equalsIgnoreCase("quickstart")) {
                    if (sender.hasPermission("minekart.quickstart")) {
                        Game games = Main.getInstance().getGameManager().getGames().get(0);
                        Main.getInstance().getLobbyManager().getLobbies().get(0).addPlayer((Player) sender);
                        Main.getInstance().getLobbyManager().startLobby(games);
                        games.getOldItems().put((Player) sender, ((Player) sender).getInventory().getContents());
                        ((Player) sender).getInventory().clear();
                        ((Player) sender).teleport(new LocationsManager(games.getCupName() + ".lobby").getLocation());
                        Player player = (Player) sender;
                        if (player.hasPermission("minekart.quickstart")) {
                            player.getInventory().setItem(0, Main.getInstance().getInventory().getItem(0));
                        }
                        player.getInventory().setItem(8, Main.getInstance().getInventory().getItem(1));
                    } else {
                        sender.sendMessage(plugin.getPrefix() + "§cKeine Permissions!");
                    }
                }
                if (args[0].equalsIgnoreCase("setlobby")) {
                    Player player = (Player) sender;
                    if (player.hasPermission("minekart.admin.setlobby")) {
                        Game games = Main.getInstance().getGameManager().getGames().get(0);
                        new LocationsManager(games.getCupName() + ".lobby").setLocation(player.getLocation());
                        player.sendMessage(Main.getInstance().getPrefix() + "§aLobby Spawn wurde gesetzt§c!");
                    } else {
                        player.sendMessage(plugin.getPrefix() + "§cKeine Permissions!");
                    }
                }
                if (args[0].equalsIgnoreCase("join")) {
                    Game games = Main.getInstance().getGameManager().getGames().get(0);
                    Main.getInstance().getLobbyManager().getLobbies().get(0).addPlayer((Player) sender);
                    games.getOldItems().put((Player) sender, ((Player) sender).getInventory().getContents());
                    ((Player) sender).getInventory().clear();
                    ((Player) sender).teleport(new LocationsManager(games.getCupName() + ".lobby").getLocation());
                    if (games.getGameLobby().getPlayers().size() >= games.getMinPlayers()) {
                        Main.getInstance().getLobbyManager().startLobby(games);
                    }
                    Player player = (Player) sender;
                    if (player.hasPermission("minekart.quickstart")) {
                        player.getInventory().setItem(0, Main.getInstance().getInventory().getItem(0));
                    }
                    player.getInventory().setItem(8, Main.getInstance().getInventory().getItem(1));
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("setcart")) {
                    if (sender.hasPermission("minekart.admin.setcart")) {
                        Game games = Main.getInstance().getGameManager().getGames().get(0);
                        if (Main.getInstance().getGameManager().setPigSpawnLocation(games, ((Player) sender).getLocation(), Integer.parseInt(args[2]))) {
                            sender.sendMessage(Main.getInstance().getPrefix() + "§aLocation pig §6" + args[2] + " §awurde gesetzt für den Cup §6" + games.getCupName() + "§c!");
                        } else {
                            sender.sendMessage("§cEin Fehler beim setzen von den Pigs");
                        }
                    } else {
                        sender.sendMessage(plugin.getPrefix() + "§cKeine Permissions!");
                    }
                }
                if (args[0].equalsIgnoreCase("checkpoint")) {
                    if (sender.hasPermission("minekart.admin.checkpoint")) {
                        Game game = Main.getInstance().getGameManager().getGames().get(0);
                        if (Main.getInstance().getGameManager().createCheckPoint(((Player) sender).getWorld(), game, ((Player) sender).getLocation(), Integer.parseInt(args[1]))) {
                            sender.sendMessage(Main.getInstance().getPrefix() + "§aCheckpoint wurde gesetzt Nummer §6" + Integer.parseInt(args[1]) + " §ain der Welt §6" + ((Player) sender).getWorld().getName() + "§4§l!");
                        } else {
                            sender.sendMessage(Main.getInstance().getPrefix() + "§cCheckpoint konnte nicht gesetzt werden§4§l!");
                        }
                    } else {
                        sender.sendMessage(plugin.getPrefix() + "§cKeine Permissions!");
                    }
                }
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("marker")) {
                    if (sender.hasPermission("minekart.admin.marker")) {
                        PlayerClickItem.commands.put((Player) sender, "game");
                        ((Player) sender).getInventory().addItem(new SpecialItem(Material.STICK).setDisplayName("§6PositionMarker").build());
                        sender.sendMessage(Main.getInstance().getPrefix() + "§aBitte gib den Cup Name ein!");
                    } else {
                        sender.sendMessage(plugin.getPrefix() + "§cKeine Permissions!");
                    }
                }
                if (args[0].equalsIgnoreCase("hologram")) {
                    Player player = (Player) sender;
                    if (player.hasPermission("minekart.admin.hologram")) {
                        Location location = player.getLocation();
                        new LocationsManager("hologram").setLocation(location);
                        player.sendMessage(Main.getInstance().getPrefix() + "§aHologram Location gesetzt!");
                        player.sendMessage(Main.getInstance().getPrefix() + "§aBei einem MineKart Join werden dort die Stats von den Spielern angezeigt!");
                    } else {
                        player.sendMessage(plugin.getPrefix() + "§cKeine Permissions!");
                    }
                }
                if (args[0].equalsIgnoreCase("leave")) {
                    Main.getInstance().getGameManager().getGames().get(0).removePlayer((Player) sender);
                    Main.getInstance().getGameManager().getGames().get(0).getGameLobby().removePlayer((Player) sender);
                    ((Player) sender).getInventory().clear();
                    if (!plugin.getGameManager().getGames().get(0).getOldItems().isEmpty()) {
                        if (plugin.getGameManager().getGames().get(0).getOldItems().containsKey((Player) sender))
                            ((Player) sender).getInventory().setContents(Main.getInstance().getGameManager().getGames().get(0).getOldItems().get(sender));
                    }
                    if (Main.getInstance().isCloudNet()) {
                        Main.getInstance().connectToCloudLobbyServer((Player) sender);
                    } else if (!Main.getInstance().isBungeecord()) {
                        ((Player) sender).teleport(new LocationsManager(Main.getInstance().getGameManager().getGames().get(0).getCupName() + ".lobby").getLocation());
                    } else {
                        new ServerSwitcher().connect((Player) sender, Main.getInstance().getLobbyServer());
                    }
                }
            } else if (args.length == 4) {
                if (args[0].equalsIgnoreCase("renamemap")) {
                    if (sender.hasPermission("minekart.admin.renamemap")) {
                        Game games = Main.getInstance().getGameManager().getGames().get(0);
                        String worldName = args[3];
                        World world = Bukkit.getWorld(args[2]);
                        if (world != null) {
                            if (!Main.getInstance().getGameManager().hasMapName(games, world)) {
                                if (Main.getInstance().getGameManager().setMapName(games, worldName, world)) {
                                    sender.sendMessage(Main.getInstance().getPrefix() + "§aDer WeltName von §6" + world.getName() + " §awurde geändert auf §6" + worldName + "§c!");
                                } else {
                                    sender.sendMessage(Main.getInstance().getPrefix() + "§cEs konnte keine Welt umbennant werden!");
                                }
                            } else {
                                sender.sendMessage(Main.getInstance().getPrefix() + "§cDiese Map hat bereits einen Namen!");
                            }
                        } else {
                            sender.sendMessage(Main.getInstance().getPrefix() + "§cDiese Welt existiert nicht!");
                        }
                    } else {
                        sender.sendMessage(plugin.getPrefix() + "§cKeine Permissions!");
                    }
                }
            } else {
                sender.sendMessage("");
            }
        } else {
            sender.sendMessage(plugin.getPrefix() + "§cDieser Befehl duerfen nur Spieler ausfuehren!");
        }
        return false;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (!commands.isEmpty()) {
            if (commands.containsKey(event.getPlayer())) {
                if (commands.get(event.getPlayer()).equalsIgnoreCase("maxplayers")) {
                    try {
                        game.setMaxPlayers(Integer.parseInt(event.getMessage()));
                        commands.remove(event.getPlayer());
                        event.setCancelled(true);
                        commands.put(event.getPlayer(), "minplayers");
                        event.getPlayer().sendMessage("§aBitte gebe die Zahl an Minimal Spieler!");
                    } catch (Exception ex) {
                        game.setMaxPlayers(Main.getInstance().getConfig().getInt("MaxPlayers"));
                        commands.remove(event.getPlayer());
                        event.setCancelled(true);
                        commands.put(event.getPlayer(), "minplayers");
                        event.getPlayer().sendMessage("§aBitte gebe die Zahl an Minimal Spieler!");
                    }
                } else if (commands.get(event.getPlayer()).equalsIgnoreCase("minplayers")) {
                    game.setMinPlayers(Integer.parseInt(event.getMessage()));
                    commands.remove(event.getPlayer());
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("§aSpiel wurde erstellt! Mit dem Namen §6" + game.getCupName());
                    game.saveGame(game.getCupName());
                }
            }
        }
        if (!plugin.getGameManager().getGames().isEmpty()) {
            if (!plugin.getGameManager().isStarted()) {
                if(plugin.getLobbyManager().getLobbies().get(0).getPlayers().contains(event.getPlayer())) {
                    plugin.getLobbyManager().getLobbies().get(0).sendMessageToLobby("§6" + event.getPlayer().getName() + " §a" +
                            event.getMessage());
                    event.setCancelled(true);
                }
            } else {
                Game game = plugin.getGameManager().getGames().get(0);
                if(game.getPlayers().contains(event.getPlayer())) {
                    String message = event.getMessage();
                    game.sendMessageToGame("§6" + event.getPlayer().getName() + " §a" + message);
                    event.setCancelled(true);
                }
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            ArrayList<String> commands = new ArrayList<>();
            ArrayList<String> empty = new ArrayList<>();
            if (sender.hasPermission("minekart.admin.create"))
                commands.add("create");
            if (sender.hasPermission("minekart.admin.addmap"))
                commands.add("addmap");
            if (sender.hasPermission("minekart.quickstart"))
                commands.add("quickstart");
            if (sender.hasPermission("minekart.admin.setlobby"))
                commands.add("setlobby");
            commands.add("join");
            if (sender.hasPermission("minekart.admin.setcart"))
                commands.add("setcart");
            if (sender.hasPermission("minekart.admin.marker"))
                commands.add("marker");
            if (sender.hasPermission("minekart.admin.hologram"))
                commands.add("hologram");
            commands.add("leave");
            if (sender.hasPermission("minekart.admin.renamemap"))
                commands.add("renamemap");
            for (String s : commands) {
                if (s.toLowerCase().startsWith(args[0])) {
                    empty.add(s);
                }
            }
            Collections.sort(empty);
            return empty;
        }
        return null;
    }
}
