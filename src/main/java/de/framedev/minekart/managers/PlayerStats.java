package de.framedev.minekart.managers;

/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 19.06.2020 18:12
 */

import de.framedev.minekart.main.Main;
import de.framedev.mysqlapi.api.SQL;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class PlayerStats {

    private final String databaseName = Main.getInstance().getConfiguration().getString("DatabaseName");
    private final File file = new File(Main.getInstance().getDataFolder(), "playerstats.yml");
    private final FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

    /* Main */
    private final Main plugin;

    public PlayerStats() {
        this.plugin = Main.getInstance();
    }

    public void createTable() {
        SQL.createTable(databaseName, "UUID VARCHAR(255)", "Wins INT", "WinsAmount INT");
    }

    public void setWinsAmount(OfflinePlayer player, int amount) {
        if (plugin.isMysql()) {
            if (SQL.isTableExists(databaseName)) {
                if (SQL.exists(databaseName, "UUID", player.getUniqueId().toString())) {
                    SQL.updateData(databaseName, "WinsAmount", "'" + amount + "'", "UUID = '" + player.getUniqueId().toString() + "'");
                } else {
                    SQL.insertData(databaseName, "'" + player.getUniqueId().toString() + "','" + amount + "'", "UUID,WinsAmount");
                }
            } else {
                createTable();
                SQL.insertData(databaseName, "'" + player.getUniqueId().toString() + "','" + amount + "'", "UUID,WinsAmount");
            }
        } else {
            cfg.set("Player." + player.getName() + ".WinsAmount", amount);
            try {
                cfg.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public int getWinsAmount(OfflinePlayer player) {
        if (plugin.isMysql()) {
            if (SQL.isTableExists(databaseName)) {
                if (SQL.exists(databaseName, "UUID", player.getUniqueId().toString())) {
                    if (SQL.get(databaseName, "WinsAmount", "UUID", player.getUniqueId().toString()) != null) {
                        int wins = (int) SQL.get(databaseName, "WinsAmount", "UUID", player.getUniqueId().toString());
                        return wins;
                    } else {
                        return 0;
                    }
                } else {
                    return 0;
                }
            } else {
                createTable();
                return 0;
            }
        } else {
            return cfg.getInt("Player." + player.getName() + ".WinsAmount");
        }
    }

    public void addWin(OfflinePlayer player) {
        if (plugin.isMysql()) {
            if (SQL.isTableExists(databaseName)) {
                if (SQL.exists(databaseName, "UUID", player.getUniqueId().toString())) {
                    if (SQL.get(databaseName, "Wins", "UUID", player.getUniqueId().toString()) != null) {
                        int wins = (int) SQL.get(databaseName, "Wins", "UUID", player.getUniqueId().toString());
                        wins++;
                        SQL.updateData(databaseName, "Wins", "'" + wins + "'", "UUID = '" + player.getUniqueId().toString() + "'");
                    } else {
                        SQL.updateData(databaseName, "Wins", "'" + 1 + "'", "UUID = '" + player.getUniqueId().toString() + "'");
                    }
                } else {
                    SQL.insertData(databaseName, "'" + player.getUniqueId().toString() + "','" + 1 + "'", "UUID,Wins");
                }
            } else {
                createTable();
                SQL.insertData(databaseName, "'" + player.getUniqueId().toString() + "','" + 1 + "'", "UUID,Wins");
            }
        } else {
            if (cfg.contains("Player." + player.getName() + ".Wins")) {
                int wins = cfg.getInt("Player." + player.getName() + ".Wins");
                wins++;
                cfg.set("Player." + player.getName() + ".Wins", wins);
            } else {
                cfg.set("Player." + player.getName() + ".Wins", 1);
            }
            try {
                cfg.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int getWins(OfflinePlayer player) {
        if (plugin.isMysql()) {
            if (SQL.isTableExists(databaseName)) {
                if (SQL.exists(databaseName, "UUID", player.getUniqueId().toString())) {
                    if (SQL.get(databaseName, "Wins", "UUID", player.getUniqueId().toString()) != null) {
                        int wins = (int) SQL.get(databaseName, "Wins", "UUID", player.getUniqueId().toString());
                        return wins;
                    } else {
                        return 0;
                    }
                } else {
                    return 0;
                }
            } else {
                createTable();
                return 0;
            }
        } else {
            return cfg.getInt("Player." + player.getName() + ".Wins");
        }
    }
}
