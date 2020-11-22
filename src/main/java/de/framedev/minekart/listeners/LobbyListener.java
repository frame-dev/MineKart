package de.framedev.minekart.listeners;

import de.framedev.minekart.main.Main;
import de.framedev.minekart.managers.ServerSwitcher;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * This Plugin was Created by FrameDev
 * Package : de.framedev.minekart.listeners
 * Date: 22.11.2020
 * Project: MineKart
 * Copyrighted by FrameDev
 */
public class LobbyListener implements Listener {

    private final Main plugin;

    public LobbyListener(Main plugin) {
        this.plugin = plugin;
        plugin.getListeners().add(this);
    }

    @EventHandler
    public void onClickItems(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(event.getItem() == null) return;
            if(event.getItem().hasItemMeta()) {
                ItemStack item = event.getItem();
                if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§aLeave Game")) {
                    if(plugin.getGameManager().getGames().get(0).getGameLobby().getPlayers().contains(event.getPlayer())) {
                        plugin.getGameManager().getGames().get(0).getGameLobby().removePlayer(event.getPlayer());
                        new ServerSwitcher().connect(event.getPlayer(),plugin.getLobbyServer());
                    }
                    event.setCancelled(true);
                }
            }
        }
    }
}
