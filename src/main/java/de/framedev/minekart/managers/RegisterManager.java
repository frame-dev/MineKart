package de.framedev.minekart.managers;

import de.framedev.minekart.commands.CreateGameCMD;
import de.framedev.minekart.commands.PlayerStatsCMD;
import de.framedev.minekart.listeners.*;
import de.framedev.minekart.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;

import java.util.Map;

public class RegisterManager {

    private final SpecialItems specialItems;

    public RegisterManager(Main plugin) {
        /* Commands */
        new CreateGameCMD(plugin);
        new PlayerStatsCMD(plugin);

        /* Listeners */
        new SignChangeEvent(plugin);
        new PlayerMoveEffects(plugin);
        new PlayerClickItem(plugin);

        new GameJoin(plugin);
        new GameStartedListener(plugin);

        specialItems = plugin.getSpecialItemsManager();

        registerCommands(plugin);
        registerTabCompleters(plugin);
        registerListeners(plugin);
        registerSpecialItems();
        plugin.getLobbyManager().registerEvents();

        for(SpecialItem specialItem : specialItems.getSpecialitems()) {
            plugin.log("§6" + specialItem.getDisplayName() + " §awurde Regristriert§4§l!");
        }
        Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + "§aCommands/TabCompleters/Listeners §bRegistered!");
    }

    private void registerListeners(Main plugin) {
        for (Listener listener : plugin.getListeners()) {
            if (listener != null) {
                plugin.getServer().getPluginManager().registerEvents(listener, plugin);
            }
        }
    }

    private void registerCommands(Main plugin) {
        for (Map.Entry<String, CommandExecutor> commands : plugin.getCommands().entrySet()) {
            if (commands.getKey() != null && commands.getValue() != null) {
                plugin.getCommand(commands.getKey()).setExecutor(commands.getValue());
            }
        }
    }

    private void registerTabCompleters(Main plugin) {
        for (Map.Entry<String, TabCompleter> tabCompleter : plugin.getTabCompleters().entrySet()) {
            if (tabCompleter.getKey() != null && tabCompleter.getValue() != null) {
                plugin.getCommand(tabCompleter.getKey()).setTabCompleter(tabCompleter.getValue());
            }
        }
    }

    private void registerSpecialItems() {
        // Mushroom /
        SpecialItem speedMushroom = new SpecialItem(Material.RED_MUSHROOM).setDisplayName("§aSpeeeeed!");
        specialItems.addSpecialItem(speedMushroom);
        // Lighting /
        SpecialItem blitz = new SpecialItem(Material.BLAZE_ROD).setDisplayName("§aBlitz!");
        specialItems.addSpecialItem(blitz);

        // Star /
        specialItems.addSpecialItem(specialItems.createSpecialItem(Material.NETHER_STAR, "§aStern!"));
        // Banana /
        specialItems.addSpecialItem(specialItems.createSpecialItem(Material.YELLOW_DYE,"§aBanana"));
        // Bombe
        specialItems.addSpecialItem(specialItems.createSpecialItem(Material.TNT,"§aBombe!"));

        // Bullet Bill
    }
}
