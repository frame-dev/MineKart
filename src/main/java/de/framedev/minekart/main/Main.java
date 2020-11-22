package de.framedev.minekart.main;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.FlagUtil;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import de.framedev.minekart.api.MineKartAPI;
import de.framedev.minekart.commands.CreateGameCMD;
import de.framedev.minekart.commands.PlayerStatsCMD;
import de.framedev.minekart.listeners.*;
import de.framedev.minekart.managers.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

public class Main extends JavaPlugin {

    /* Classes */
    private GameManager gameManager;
    private LobbyManager lobbyManager;
    private PlayerStats playerStats;
    private ScoreBoardManager scoreBoardManager;
    private LocationsManager locationsManager;
    private SpecialItems specialItemsManager;

    /* *.yml Files */
    private CustomConfig langConfigDE;
    private CustomConfig langConfigEN;
    private CustomConfig gameManagerConfig;
    private CustomConfig configuration;

    /* Instance of this Main */
    private static Main instance;

    /* Variables */
    private String prefix;
    private ItemStack[] lobbyInventory;

    //Not used
    private String noPermission;
    private boolean bungeecord;
    private String lobbyServer;
    private boolean cloudNet;
    private String lobbyGroup;

    /* Lists */
    private HashMap<Game, Cuboid> cuboids;
    private ArrayList<SpecialItem> specialItems;
    private ArrayList<Listener> listeners;
    private HashMap<String, CommandExecutor> commands;
    private HashMap<String, TabCompleter> tabCompleters;

    /* WorldGuard */
    public WorldGuardPlugin worldGuardPlugin;

    /* Economy /Vault */
    private Economy economy;

    @Override
    public void onEnable() {
        /* Instance */
        instance = this;

        this.lobbyInventory = new ItemStack[3*9];
        ItemStack leaveItem = new ItemStack(Material.BLUE_BED);
        ItemMeta leaveMeta = leaveItem.getItemMeta();
        leaveMeta.setDisplayName("§aLeave Game");
        leaveItem.setItemMeta(leaveMeta);
        this.lobbyInventory[0] = leaveItem;

        /* WorldGuard */
        this.worldGuardPlugin = getWorldGuard();

        /* Cuboids */
        this.cuboids = new HashMap<>();

        /* Special Items */
        this.specialItems = new ArrayList<>();
        this.specialItemsManager = new SpecialItems();

        /* cfg  Config.yml */
        this.configuration = new CustomConfig("config");
        configuration.createConfig();

        /* Register Things */
        listeners = new ArrayList<>();
        commands = new HashMap<>();
        tabCompleters = new HashMap<>();

        /* ScoreBoard */
        this.scoreBoardManager = new ScoreBoardManager(this);

        /* Lobby */
        this.lobbyManager = new LobbyManager();

        /* Game Config games.yml */
        this.gameManagerConfig = new CustomConfig("games");
        gameManagerConfig.createConfig();

        /* PlayerStats */
        this.playerStats = new PlayerStats();

        /* GameManager */
        this.gameManager = new GameManager();

        /* Location Manager */
        this.locationsManager = new LocationsManager();

        /* prefix */
        this.prefix = configuration.getString("Prefix");
        prefix = prefix.replace('&', '§');
        prefix = prefix.replace(">>", "»");

        /* Register */
        new RegisterManager(this);

        /* Load Games */
        loadGames();
        Bukkit.getConsoleSender().sendMessage(getPrefix() + "§6Games Loaded!");

        /* Load Lobbies */
        loadLobbies();
        Bukkit.getConsoleSender().sendMessage(getPrefix() + "§6Lobbies Loaded!");

        /* Update Games */
        updateGame();

        /* Load Cuboids */
        loadCuboids();
        Bukkit.getConsoleSender().sendMessage(getPrefix() + "§6Cuboids Loaded!");

        Bukkit.getConsoleSender().sendMessage(getPrefix() + "§aPlugin Enabled!");
        sendPluginInfo();

        /* Create DataBaseTable */
        if (isMysql()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (getServer().getPluginManager().getPlugin("MySQLAPI") != null) {
                        playerStats.createTable();
                    }
                }
            }.runTaskLater(this, 300);
        }

        /* Check Languages */
        this.langConfigDE = new CustomConfig("lang_de_DE");
        this.langConfigEN = new CustomConfig("lang_en_EN");
        this.langConfigDE.createConfig();
        this.langConfigEN.createConfig();

        /* Check if WorldEdit is Null */
        if (Bukkit.getPluginManager().getPlugin("WorldEdit") == null) {
            Bukkit.getPluginManager().disablePlugin(this);
            getLogger().log(Level.SEVERE, "Please install WorldEdit!");
        }

        /* Check if WorldGuard is Null */
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) {
            Bukkit.getPluginManager().disablePlugin(this);
            getLogger().log(Level.SEVERE, "Please install WorldGuard!");
        }

        /* Setup Vault */
        if (!setupEconomy()) {
            System.err.println(getPrefix() + "§cVault not loaded or Installed!");
        }
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new ServerSwitcher());

        this.bungeecord = getConfig().getBoolean("BungeeCord");
        //this.cloudNet = getConfig().getBoolean("CloudNet");

        if (isBungeecord() || isCloudNet()) {
            this.lobbyServer = getConfig().getString("LobbyServer");
            /*if(getConfig().contains("CloudNetLobbyGroup")) {
                this.lobbyGroup = getConfig().getString("CloudNetLobbyGroup");
            }
        */
        }

        getLogger().log(Level.WARNING, "This Plugin is Work in Progress");
    }

    public ItemStack[] getLobbyInventory() {
        return lobbyInventory;
    }

    public boolean isCloudNet() {
        return cloudNet;
    }

    public boolean isBungeecord() {
        return bungeecord;
    }

    public LocationsManager getLocationsManager() {
        return locationsManager;
    }

    public String getLobbyServer() {
        return lobbyServer;
    }

    public String getLobbyGroup() {
        return lobbyGroup;
    }

    public WorldGuardPlugin getWorldGuard() {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("WorldGuard");
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null;
        }
        return (WorldGuardPlugin) plugin;
    }

    public HashMap<String, TabCompleter> getTabCompleters() {
        return tabCompleters;
    }

    /**
     * @return isMySQL enabled (true/false)
     */
    public boolean isMysql() {
        return getConfig().getBoolean("MySQL");
    }

    public void sendPluginInfo() {
        Bukkit.getConsoleSender().sendMessage(getPrefix() + "§6Plugin Name : §b" + getDescription().getName());
        Bukkit.getConsoleSender().sendMessage(getPrefix() + "§6Author : §b" + getDescription().getAuthors().get(0));
        Bukkit.getConsoleSender().sendMessage(getPrefix() + "§6Version : §b" + getDescription().getVersion());
        Bukkit.getConsoleSender().sendMessage(getPrefix() + "§6API Version : §b" + getDescription().getAPIVersion());
    }

    public HashMap<Game, Cuboid> getCuboids() {
        return cuboids;
    }

    private void loadCuboids() {
        if (!gameManager.getGames().isEmpty()) {
            if (new LocationsManager("location1").getCfg().contains("location1") && new LocationsManager("location2").getCfg().contains("location2")) {
                cuboids.put(gameManager.getGames().get(0), new Cuboid(new LocationsManager("location1").getLocation(), new LocationsManager("location2").getLocation()));
            }
        }
    }

    public FileConfiguration getLanguageByPlayerConfig(Player player) {
        CraftPlayer entityPlayer = ((CraftPlayer) player);
        if (entityPlayer.getLocale().equalsIgnoreCase("de_DE")) {
            return this.langConfigDE.getConfiguration();
        } else {
            return this.langConfigEN.getConfiguration();
        }
    }

    public SpecialItems getSpecialItemsManager() {
        return specialItemsManager;
    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }

    public Economy getApi() {
        return economy;
    }

    public ArrayList<SpecialItem> getSpecialItems() {
        return specialItems;
    }

    public PlayerStats getPlayerStats() {
        return playerStats;
    }

    public void updateGame() {
        if (gameManager.getGames() != null) {
            if (!gameManager.getGames().isEmpty()) {
                if (gameManager.getGames().get(0) != null) {
                    lobbyManager.checkEnoughPlayer(gameManager.getGames().get(0));
                }
            }
        }
    }

    public ScoreBoardManager getScoreBoardManager() {
        return scoreBoardManager;
    }


    public void loadGames() {
        List<File> files = listDir(Main.getInstance().getDataFolder());
        if (files != null) {
            for (File file : files) {
                if (file != null) {
                    if (file.isDirectory()) {
                        Game game = new Game();
                        game.setGame(file.getName());
                        if (game != null) {
                            gameManager.getGames().add(Game.getGameFromFile(game.getCupName()));
                        } else {
                            throw new NullPointerException("Cannot find Game");
                        }
                    }
                }
            }
        }

        for (Game games : gameManager.getGames()) {
            if (games != null) {
                games.getPlayers().forEach(player -> {
                    if (player != null) {
                        gameManager.getGamePlayers().put(player, games);
                    }
                });
            }
        }
    }

    public void loadLobbies() {
        List<File> files = listDir(Main.getInstance().getDataFolder());
        if (files != null) {
            for (File file : files) {
                if (file != null) {
                    if (file.isDirectory()) {
                        for (Game games : gameManager.getGames()) {
                            if (games.getCupName().equalsIgnoreCase(file.getName())) {
                                lobbyManager.getLobbies().add(games.getGameLobby());
                            }
                        }
                    }
                }
            }
        }
    }

    public CustomConfig getConfiguration() {
        return configuration;
    }

    public String getPrefix() {
        return prefix;
    }

    public HashMap<String, CommandExecutor> getCommands() {
        return commands;
    }

    public ArrayList<Listener> getListeners() {
        return listeners;
    }

    public CustomConfig getGameManagerConfig() {
        return gameManagerConfig;
    }

    public static Main getInstance() {
        return instance;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public LobbyManager getLobbyManager() {
        return lobbyManager;
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(getPrefix() + "§cPlugin Disabled!");
    }

    public List<File> listDir(File dir) {

        File[] files = dir.listFiles();
        if (files != null) { // Erforderliche Berechtigungen etc. sind vorhanden
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    return Arrays.asList(files);
                }
            }
        }
        return null;
    }

    public void setFinishLocationBlocks(Game game, Location location1, Location location2) {
        Cuboid cuboid = new Cuboid(location1, location2);
        for (Block block : cuboid) {
            if (block.getType() == Material.AIR) {
            } else {
                Material material = Material.getMaterial(Objects.requireNonNull(Main.getInstance().getConfig().getString("StartBlock")));
                if (material != null) {
                    block.setType(material);
                } else {
                    getLogger().log(Level.SEVERE, "Cannot found the Material in the Config.yml!");
                }
            }
        }
        // WorldGuard erstelle Region!
        BlockVector3 min = BlockVector3.at(location1.getX(), location1.getY(), location1.getZ());
        BlockVector3 max = BlockVector3.at(location2.getX(), location2.getY(), location2.getZ());
        ProtectedRegion region = new ProtectedCuboidRegion(location1.getWorld().getName() + "minekart", min, max);
        RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(new BukkitWorld(location1.getWorld()));
        regions.addRegion(region);
        regions.getRegion("__global__").setFlag(Flags.TNT, StateFlag.State.DENY);
        Main.getInstance().getCuboids().put(game, cuboid);
        new LocationsManager(game.getCupName() + "location1").setLocation(location1);
        new LocationsManager(game.getCupName() + "location2").setLocation(location2);
        game.deleteFile(game.getCupName());
        game.saveGame(game.getCupName());
    }

    /**
     * Diese Mthode wird verwenden von Debuggin von Objekten
     *
     * @param obj das Object zu Debuggen
     */
    public void debug(Object obj) {
        // Debuggen von einem Objekt
        System.out.println(getPrefix() + obj);
    }

    /**
     * @param text Text to send
     */
    public void log(String text) {
        Bukkit.getConsoleSender().sendMessage(Main.getInstance().getPrefix() + text);
    }
}
