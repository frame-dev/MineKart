package de.framedev.minekart.listeners;

import de.framedev.minekart.main.Main;
import de.framedev.minekart.managers.Game;
import de.framedev.minekart.managers.LocationsManager;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 03.07.2020 19:59
 */
public class SignChangeEvent implements Listener {

    private final Main plugin;
    private Sign sign;

    public SignChangeEvent(Main plugin) {
        this.plugin = plugin;
        plugin.getListeners().add(this);
    }

    @EventHandler
    public void onSignChange(org.bukkit.event.block.SignChangeEvent event) {
        if (event.getLine(0) != null) {
            if (event.getLine(0).equalsIgnoreCase("[mk]")) {
                if (event.getPlayer().hasPermission("minekart.admin.create")) {
                    for (Game game : plugin.getGameManager().getGames()) {
                        if (game != null) {
                            if (event.getLine(1).equalsIgnoreCase(game.getCupName())) {
                                event.setLine(0, "§6[MineKart]");
                                event.setLine(1, "§a" + game.getCupName());
                                event.setLine(2, "§6" + game.getMinPlayers() + "/" + game.getMaxPlayers());
                                new LocationsManager("Sign").setLocation(event.getBlock().getLocation());
                                this.sign = (Sign) event.getBlock().getState();
                            }
                        }
                    }
                }
            }
        }
    }

    public void setSign(Sign sign) {
        this.sign = sign;
    }

    public Sign getSign() {
        return sign;
    }
}
