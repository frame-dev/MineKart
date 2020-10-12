package de.framedev.minekart.managers;

import de.framedev.minekart.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 03.07.2020 20:00
 */
public class LocationsManager {

    private String name;
    private final File file = new File(Main.getInstance().getDataFolder(), "locations.yml");
    private final FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

    public LocationsManager(String name) {
        this.name = name;
    }

    public LocationsManager() {
    }

    private void saveCfg() {
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setLocation(Location location) {
        cfg.set(name + ".world", location.getWorld().getName());
        cfg.set(name + ".x", location.getX());
        cfg.set(name + ".y", location.getY());
        cfg.set(name + ".z", location.getZ());
        cfg.set(name + ".yaw", location.getYaw());
        cfg.set(name + ".pitch", location.getPitch());
        saveCfg();
    }

    public Location getLocation() throws NullPointerException {
        World world = Bukkit.getWorld(cfg.getString(name + ".world"));
        double x = cfg.getDouble(name + ".x");
        double y = cfg.getDouble(name + ".y");
        double z = cfg.getDouble(name + ".z");
        float yaw = cfg.getInt(name + ".yaw");
        float pitch = cfg.getInt(name + ".pitch");
        if (world == null) {
            throw new NullPointerException("World is Null");
        }
        Location location = new Location(world, x, y, z, yaw, pitch);
        if (location != null) {
            return location;
        } else {
            throw new NullPointerException("Location is Null");
        }
    }

    public Location getLocation(String name) throws NullPointerException {
        World world = Bukkit.getWorld(cfg.getString(name + ".world"));
        double x = cfg.getDouble(name + ".x");
        double y = cfg.getDouble(name + ".y");
        double z = cfg.getDouble(name + ".z");
        if (world != null) {

        } else {
            throw new NullPointerException("World is Null");
        }
        Location location = new Location(world, x, y, z);
        if (location != null) {
            return location;
        } else {
            throw new NullPointerException("Location is Null");
        }
    }

    public FileConfiguration getCfg() {
        return cfg;
    }
}
