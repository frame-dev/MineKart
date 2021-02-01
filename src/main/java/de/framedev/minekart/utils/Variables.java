package de.framedev.minekart.utils;


/*
 * de.framedev.minekart.utils
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 25.08.2020 22:22
 */

import de.framedev.minekart.main.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Variables {

    private final Main plugin;
    private final String prefix;
    private FileConfiguration langConfig;

    public Variables(Main plugin) {
        this.plugin = plugin;
        this.prefix = plugin.getPrefix();
    }

    public String getPrefix() {
        return prefix;
    }

    public FileConfiguration getLangConfig(Player player) {
        return plugin.getLanguageByPlayerConfig(player);
    }
}
