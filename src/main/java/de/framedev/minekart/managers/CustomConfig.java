package de.framedev.minekart.managers;

import de.framedev.minekart.main.Main;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/*
 * =============================================
 * = This Plugin was Created by FrameDev       =
 * = Copyrighted by FrameDev                   =
 * = Please don't Change anything              =
 * =============================================
 * This Class was made at 24.05.2020, 01:15
 */

public class CustomConfig implements Serializable {

    private static final long serialVersionUID = -8845539395443716366L;
    String fileName;
    File file = null;
    FileConfiguration cfg = null;

    public CustomConfig(String fileName) {
        this.fileName = fileName;
    }

    public void createConfig() {
        file = new File(Main.getInstance().getDataFolder(), fileName + ".yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            Main.getInstance().saveResource(fileName + ".yml", false);
        }

        cfg = new YamlConfiguration();
        try {
            cfg.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            cfg.save(file = new File(Main.getInstance().getDataFolder(), fileName + ".yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void set(String path, Object object) {
        cfg.set(path, object);
    }

    public String getString(String path) {
        return cfg.getString(path);
    }

    public Integer getInt(String path) {
        return cfg.getInt(path);
    }

    public Double getDouble(String path) {
        return cfg.getDouble(path);
    }

    public Object get(String path) {
        return cfg.get(path);
    }

    public boolean contains(String path) {
        return cfg.contains(path);
    }

    public boolean isString(String path) {
        return cfg.isString(path);
    }

    public boolean isInt(String path) {
        return cfg.isInt(path);
    }

    public boolean isDouble(String path) {
        return cfg.isDouble(path);
    }

    public boolean isLong(String path) {
        return cfg.isLong(path);
    }

    public boolean isBoolean(String path) {
        return cfg.isBoolean(path);
    }

    public List<?> getList(String path) {
        return cfg.getList(path);
    }

    public FileConfiguration getConfiguration() throws NullPointerException {
        return cfg;
    }
}
