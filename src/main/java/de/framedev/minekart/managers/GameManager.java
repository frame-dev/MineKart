package de.framedev.minekart.managers;

import de.framedev.minekart.listeners.PlayerMoveEffects;
import de.framedev.minekart.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.*;

public class GameManager implements Serializable {

    private static final long serialVersionUID = -7392664852687908177L;
    /* games ArrayList */
    private final ArrayList<Game> games;

    /* HashMap of Player and the Saves Games */
    private final HashMap<Player, Game> gamePlayers;
    private final HashMap<Game, World> playedMaps;

    /**
     * This Constructor use for Creating the Hashes
     */
    public GameManager() {
        games = new ArrayList<>();
        gamePlayers = new HashMap<>();
        playedMaps = new HashMap<>();
    }

    /* get Games */
    public ArrayList<Game> getGames() {
        return games;
    }

    /* get HashMap of Player and Game */
    public HashMap<Player, Game> getGamePlayers() {
        return gamePlayers;
    }

    @SuppressWarnings("unchecked")
    /* add a new Map */
    public boolean addMap(Game game, World world) {
        ArrayList<String> maps = (ArrayList<String>) Main.getInstance().getGameManagerConfig().getList("Game." + game.getCupName() + ".Maps");
        if (maps != null) {
            for (String map : maps) {
                if (map.equalsIgnoreCase(world.getName())) {
                    Main.getInstance().getGameManagerConfig().set("Game." + game.getCupName() + ".Maps", maps);
                    Main.getInstance().getGameManagerConfig().saveConfig();
                    return false;
                } else {
                    maps.add(world.getName());
                    Main.getInstance().getGameManagerConfig().set("Game." + game.getCupName() + ".Maps", maps);
                    Main.getInstance().getGameManagerConfig().saveConfig();
                    return true;
                }
            }
        } else {
            maps = new ArrayList<>();
            maps.add(world.getName());
            Main.getInstance().getGameManagerConfig().set("Game." + game.getCupName() + ".Maps", maps);
            Main.getInstance().getGameManagerConfig().saveConfig();
            return true;
        }
        return false;
    }

    /**
     * @param game Game
     * @return all Maps that are saved on this game
     */
    public List<String> getMaps(Game game) throws NullPointerException {
        if (Main.getInstance().getGameManagerConfig().getList("Game." + game.getCupName() + ".Maps") != null) {
            return (ArrayList<String>) Main.getInstance().getGameManagerConfig().getList("Game." + game.getCupName() + ".Maps");
        }
        throw new NullPointerException("Es konnten keine Maps gefunden werden!");
    }

    public boolean setPigSpawnLocation(Game game, Location location, int number) {
        World world = location.getWorld();
        if (game != null) {
            if (Main.getInstance().getGameManagerConfig().contains("Game." + game.getCupName() + "." + world.getName() + ".Pig." + number)) {
                return false;
            } else {
                FileConfiguration cfg = Main.getInstance().getGameManagerConfig().cfg;
                cfg.set("Game." + game.getCupName() + "." + world.getName() + ".Pig." + number + ".world", location.getWorld().getName());
                cfg.set("Game." + game.getCupName() + "." + world.getName() + ".Pig." + number + ".x", location.getBlockX());
                cfg.set("Game." + game.getCupName() + "." + world.getName() + ".Pig." + number + ".y", location.getBlockY());
                cfg.set("Game." + game.getCupName() + "." + world.getName() + ".Pig." + number + ".z", location.getBlockZ());
                cfg.set("Game." + game.getCupName() + "." + world.getName() + ".Pig." + number + ".yaw", location.getYaw());
                cfg.set("Game." + game.getCupName() + "." + world.getName() + ".Pig." + number + ".pitch", location.getPitch());
                Main.getInstance().getGameManagerConfig().saveConfig();
                return true;
            }
        }
        return false;
    }

    public Location getPigSpawnLocation(Game game, int number, World worlds) throws NullPointerException {
        if (game != null) {
            if (!Main.getInstance().getGameManagerConfig().contains("Game." + game.getCupName() + "." + worlds.getName() + ".Pig." + number)) {
                throw new NullPointerException("Location is Null");
            } else {
                FileConfiguration cfg = Main.getInstance().getGameManagerConfig().cfg;
                if (cfg.contains("Game." + game.getCupName() + "." + worlds.getName() + ".Pig." + number + ".world")) {
                    World world = Bukkit.getWorld(cfg.getString("Game." + game.getCupName() + "." + worlds.getName() + ".Pig." + number + ".world"));
                    int x = cfg.getInt("Game." + game.getCupName() + "." + worlds.getName() + ".Pig." + number + ".x");
                    int y = cfg.getInt("Game." + game.getCupName() + "." + worlds.getName() + ".Pig." + number + ".y");
                    int z = cfg.getInt("Game." + game.getCupName() + "." + worlds.getName() + ".Pig." + number + ".z");
                    float yaw = cfg.getInt("Game." + game.getCupName() + "." + worlds.getName() + ".Pig." + number + ".yaw");
                    float pitch = cfg.getInt("Game." + game.getCupName() + "." + worlds.getName() + ".Pig." + number + ".pitch");
                    return new Location(world, x, y, z, yaw, pitch);
                } else {
                    throw new NullPointerException("World is Null!");
                }
            }
        } else {
            throw new NullPointerException("Location is Null");
        }
    }

    /**
     * @param game the Game was created to start the Game
     */
    @SuppressWarnings("deprecation")
    public void spawnPigs(Game game, Player player) {
        if (game.getActiveWorld() != null) {
            ConfigurationSection cs = Main.getInstance().getGameManagerConfig().cfg.getConfigurationSection("Game." + game.getCupName() + "." + game.getActiveWorld().getName() + ".Pig");
            ArrayList<Location> locations = new ArrayList<>();
            if (cs != null) {
                for (int i = 1; i < game.getPlayers().size(); i++) {
                    locations.add(getPigSpawnLocation(game, i, game.getActiveWorld()));
                }
            }
            locations.forEach(l -> {
                if (player.getLocation() != l) {
                    player.teleport(l);
                    player.getInventory().setItem(0, new SpecialItem(Material.NETHER_STAR).setDisplayName("§cReset").build());
                    Location spawn = l.add(0, 2, 0);
                    PlayerMoveEffects.checkPoints.put(player, l);
                    Entity cart = player.getWorld().spawnEntity(spawn,
                            EntityType.MINECART);
                    game.getPigs().put(player, (Minecart) cart);
                    cart.setPassenger(player);
                }
            });
        }
    }

    public void spawnPig(Location location, Game game, Player player) {
        player.teleport(location);
        Location spawn = location.add(0, 2, 0);
        Entity cart = player.getWorld().spawnEntity(spawn,
                EntityType.MINECART);
        game.getPigs().put(player, (Minecart) cart);
        cart.setPassenger(player);
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public static final class IntRandomNumberGenerator {

        private final PrimitiveIterator.OfInt randomIterator;

        /**
         * Initialize a new random number generator that generates
         * random numbers in the range [min, max]
         *
         * @param min - the min value (inclusive)
         * @param max - the max value (inclusive)
         */
        public IntRandomNumberGenerator(int min, int max) {
            randomIterator = new Random().ints(min, max + 1).iterator();
        }

        /**
         * @return a random number in the range (min, max)
         */
        public int nextInt() {
            return randomIterator.nextInt();
        }
    }

    /**
     * @param game the Game was created
     * @return a Random Picked World
     */
    public World pickRandomMap(Game game) {
        if (getMaps(game).size() == 1) {
            return Bukkit.getWorld(getMaps(game).get(0));
        }
        IntRandomNumberGenerator random = new IntRandomNumberGenerator(0, getMaps(game).size());
        return Bukkit.getWorld(getMaps(game).get(random.nextInt() - 1));
    }

    public World pickRandomMap(Game game, ArrayList<String> worlds) {
        if (getMaps(game).size() == 1) {
            return Bukkit.getWorld(getMaps(game).get(0));
        }
        IntRandomNumberGenerator random = new IntRandomNumberGenerator(0, worlds.size());
        return Bukkit.getWorld(worlds.get(random.nextInt() - 1));
    }

    public ItemStack getRandomItemStack() {
        if (Main.getInstance().getSpecialItems().size() == 1) {
            return Main.getInstance().getSpecialItems().get(0).build();
        }
        IntRandomNumberGenerator random = new IntRandomNumberGenerator(0, Main.getInstance().getSpecialItems().size());
        return Main.getInstance().getSpecialItems().get(random.nextInt() - 1).build();
    }

    private boolean started;

    /**
     * @param game the Game do you won't Start
     * @return boolean if is Starded
     */
    public boolean startGame(Game game) {
        if (getMaps(game) != null) {
            game.getPlayers().forEach(player -> spawnPigs(game, player));
            started = true;
            return true;
        }
        System.err.println("Cannot Start Game!");
        started = false;
        return false;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean setMapName(Game game, String mapName, World world) {
        if (!world.getName().equalsIgnoreCase(mapName)) {
            Main.getInstance().getGameManagerConfig().set("Game." + game.getCupName() + ".MapName." + world.getName(), mapName);
            Main.getInstance().getGameManagerConfig().saveConfig();
            return true;
        }
        return false;
    }

    public boolean hasMapName(Game game, World world) {
        return Main.getInstance().getGameManagerConfig().contains("Game." + game.getCupName() + ".MapName." + world.getName());
    }

    public String getMapName(Game game, World world) {
        if (hasMapName(game, world)) {
            return Main.getInstance().getGameManagerConfig().getString("Game." + game.getCupName() + ".MapName." + world.getName());
        }
        return world.getName();
    }

    public boolean createCheckPoint(World world, Game game, Location location, int i) {
        if (getMaps(game).contains(world.getName())) {
            if (!Main.getInstance().getGameManagerConfig().contains("Game." + game.getCupName() + ".CheckPoints.world." + world.getName() + "." + i)) {
                Main.getInstance().getGameManagerConfig().set("Game." + game.getCupName() + ".CheckPoints.world", world.getName() + "." + i);
                Main.getInstance().getGameManagerConfig().set("Game." + game.getCupName() + ".CheckPoints." + world.getName() + "." + i + ".x", location.getBlockX());
                Main.getInstance().getGameManagerConfig().set("Game." + game.getCupName() + ".CheckPoints." + world.getName() + "." + i + ".y", location.getBlockY());
                Main.getInstance().getGameManagerConfig().set("Game." + game.getCupName() + ".CheckPoints." + world.getName() + "." + i + ".z", location.getBlockZ());
                Main.getInstance().getGameManagerConfig().saveConfig();
                return true;
            }
        }
        return false;
    }

    public List<Location> getCheckPoints(World world, Game game) {
        ArrayList<Location> locations = new ArrayList<>();
        if (Main.getInstance().getGameManagerConfig().contains("Game." + game.getCupName() + ".CheckPoints.world")) {
            if (Main.getInstance().getGameManagerConfig().getString("Game." + game.getCupName() + ".CheckPoints.world").equalsIgnoreCase(world.getName())) {
                ConfigurationSection cs = Main.getInstance().getGameManagerConfig().getConfiguration().getConfigurationSection("Game." + game.getCupName() + ".CheckPoints.world." + world.getName());
                if (cs != null) {
                    for (String s : cs.getKeys(false)) {
                        locations.add(new Location(world, Main.getInstance().getGameManagerConfig().getInt("Game." + game.getCupName() + ".CheckPoints." + world.getName() + "." + s + ".x"),
                                Main.getInstance().getGameManagerConfig().getInt("Game." + game.getCupName() + ".CheckPoints." + world.getName() + "." + s + ".y"), Main.getInstance().getGameManagerConfig().getInt("Game." + game.getCupName() + ".CheckPoints." + world.getName() + "." + s + ".z")));
                    }
                    return locations;
                }
            }
        }
        return null;
    }

    public boolean isPlayerInCheckPoint(Player player, Location checkpoint) {
        if (player.getWorld().equals(checkpoint.getWorld())) {
            return player.getLocation().distance(checkpoint) <= 10;
        }
        return false;
    }
}
