package de.framedev.minekart.listeners;

import de.framedev.minekart.main.Main;
import de.framedev.minekart.managers.Game;
import de.framedev.minekart.managers.GameManager;
import de.framedev.minekart.managers.SpecialItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.HashMap;

/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 11.07.2020 10:29
 */
public class PlayerClickItem implements Listener {

    private final Main plugin;
    private final GameManager gameManager;

    public static HashMap<Player, String> commands;
    private Location location1;
    private Location location2;

    public PlayerClickItem(Main plugin) {
        this.plugin = plugin;
        this.gameManager = plugin.getGameManager();
        commands = new HashMap<>();
        plugin.getListeners().add(this);
    }

    public final HashMap<Player, BukkitTask> taskIDs = new HashMap<>();
    public HashMap<Player, String> particleActive1 = new HashMap<>();

    public void spawnParticle(final Player player, final ParticleEffect particle) {
        taskIDs.put(player, Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(current -> {
                    particle.display(player.getLocation(), 1, 1, 1, 0.01f, 10, null, current);
                });
            }
        }, 0, 20));
    }

    private final HashMap<Player, Integer> time = new HashMap<>();

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (plugin.getGameManager().getGames() != null) {
            if (plugin.getGameManager().getGamePlayers() != null) {
                if (plugin.getGameManager().getGamePlayers().containsKey(event.getPlayer())) {
                    if (plugin.getGameManager().getGamePlayers().get(event.getPlayer()).getPlayers().contains(event.getPlayer())) {
                        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                            if (event.getItem() != null) {
                                ItemStack item = event.getItem();
                                if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                                    if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§cReset")) {
                                        event.setCancelled(true);
                                        if(!PlayerMoveEffects.checkPoints.isEmpty() && PlayerMoveEffects.checkPoints.containsKey(event.getPlayer())) {
                                            Game game = plugin.getGameManager().getGames().get(0);
                                            plugin.getGameManager().spawnPig(PlayerMoveEffects.checkPoints.get(event.getPlayer()), game,event.getPlayer());
                                        }
                                        if (plugin.getGameManager().getGames().get(0).getPlayers().contains(event.getPlayer())) {
                                            Game game = plugin.getGameManager().getGames().get(0);
                                            plugin.getGameManager().getGames().get(0).getPigs().remove(event.getPlayer().getPlayer());
                                            Location location = plugin.getGameManager().getPigSpawnLocation(game, 1, game.getActiveWorld());
                                            plugin.getGameManager().spawnPig(location, game,event.getPlayer().getPlayer());
                                        }
                                    }
                                    for (SpecialItem specialItem : plugin.getSpecialItems()) {
                                        if (specialItem != null) {
                                            if (item.getType() == specialItem.getType() && item.getItemMeta().getDisplayName().equalsIgnoreCase(specialItem.getDisplayName())) {
                                                if (item.getItemMeta().getDisplayName().equalsIgnoreCase("§aSpeeeeed!")) {
                                                    if (gameManager.getGamePlayers().get(event.getPlayer()).getPigs() != null) {
                                                        Minecart pig = gameManager.getGamePlayers().get(event.getPlayer()).getPigs().get(event.getPlayer());
                                                        double speed = pig.getMaxSpeed();
                                                        pig.setMaxSpeed(200);
                                                        new BukkitRunnable() {
                                                            @Override
                                                            public void run() {
                                                                pig.setMaxSpeed(speed);
                                                            }
                                                        }.runTaskLater(Main.getInstance(), 200);
                                                        event.getPlayer().getItemInHand().setType(Material.AIR);
                                                        event.setCancelled(true);
                                                        event.getPlayer().sendMessage(plugin.getPrefix() + "§aItem wurde eingesetzt! §6:" + item.getItemMeta().getDisplayName());
                                                    }
                                                } else if (item.getItemMeta().getDisplayName().equalsIgnoreCase("§aStern!")) {
                                                    if (gameManager.getGamePlayers().get(event.getPlayer()).getPigs() != null) {
                                                        Minecart pig = gameManager.getGamePlayers().get(event.getPlayer()).getPigs().get(event.getPlayer());
                                                        spawnParticle(player, ParticleEffect.SPELL_MOB);
                                                        particleActive1.put(player, player.getName());
                                                        pig.setInvulnerable(true);
                                                        player.setInvulnerable(true);
                                                        if (time.containsKey(player)) {
                                                            time.remove(player);
                                                            time.put(player, 15);
                                                        } else {
                                                            time.put(player, 15);
                                                        }
                                                        new BukkitRunnable() {
                                                            @Override
                                                            public void run() {
                                                                if (time.get(player) <= 0) {
                                                                    if (particleActive1.containsKey(player)) {
                                                                        taskIDs.get(player).cancel();
                                                                        taskIDs.remove(player);
                                                                        particleActive1.remove(player);
                                                                    }
                                                                    pig.setInvulnerable(false);
                                                                    player.setInvulnerable(false);
                                                                    time.remove(player);
                                                                    cancel();
                                                                } else {
                                                                    int times = time.get(player);
                                                                    times--;
                                                                    time.remove(player);
                                                                    time.put(player, times);
                                                                }
                                                            }
                                                        }.runTaskTimer(plugin, 0, 20);
                                                        event.getPlayer().getItemInHand().setType(Material.AIR);
                                                        event.setCancelled(true);
                                                        event.getPlayer().sendMessage(plugin.getPrefix() + "§aItem wurde eingesetzt! §6:" + item.getItemMeta().getDisplayName());
                                                    }
                                                } else if (item.getItemMeta().getDisplayName().equalsIgnoreCase("§aBliz!")) {
                                                    if (plugin.getGameManager().getGames().get(0) != null) {
                                                        if (plugin.getGameManager().getGames().get(0).getPlayers() != null) {
                                                            plugin.getGameManager().getGames().get(0).getPlayers().forEach(players -> {
                                                                if (!players.getName().equalsIgnoreCase(player.getName())) {
                                                                    Minecart minecart = plugin.getGameManager().getGames().get(0).getPigs().get(players);
                                                                    double speed = minecart.getMaxSpeed();
                                                                    minecart.setMaxSpeed(speed - 4);
                                                                    new BukkitRunnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            minecart.setMaxSpeed(speed);
                                                                        }
                                                                    }.runTaskLater(plugin, 20);
                                                                    players.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 10, 2, false));
                                                                }
                                                            });
                                                            event.getPlayer().sendMessage(plugin.getPrefix() + "§aItem wurde eingesetzt! §6:" + item.getItemMeta().getDisplayName());
                                                        }
                                                    }
                                                } else if (item.getItemMeta().getDisplayName().equalsIgnoreCase("§aBanana")) {
                                                    if (plugin.getGameManager().getGames().get(0) != null) {
                                                        if (plugin.getGameManager().getGames().get(0).getPlayers() != null) {
                                                            event.setCancelled(true);
                                                            player.getInventory().remove(item);
                                                            player.getWorld().dropItemNaturally(player.getLocation(), item);
                                                            event.getPlayer().sendMessage(plugin.getPrefix() + "§aItem wurde eingesetzt! §6:" + item.getItemMeta().getDisplayName());
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onClickBlock(PlayerInteractEvent event) {
        if (event.getItem() != null) {
            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (event.getItem().getType() == Material.STICK && event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase("§6PositionMarker")) {
                    location1 = event.getClickedBlock().getLocation();
                    if (!commands.get(event.getPlayer()).equalsIgnoreCase("finish")) {
                        commands.put(event.getPlayer(), "finish");
                        event.getPlayer().sendMessage(plugin.getPrefix() + " §cWenn du fertig bist gib bitte" + " §6\"finish\" ein!");
                    }
                    event.setCancelled(true);
                }
            } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (event.getItem().getType() == Material.STICK && event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase("§6PositionMarker")) {
                    location2 = event.getClickedBlock().getLocation();
                    if (!commands.get(event.getPlayer()).equalsIgnoreCase("finish")) {
                        commands.put(event.getPlayer(), "finish");
                        event.getPlayer().sendMessage(plugin.getPrefix() + " §cWenn du fertig bist gib bitte" + " §6\"finish\" ein!");
                    }
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (commands != null) {
            if (commands.containsKey(event.getPlayer())) {
                if (commands.get(event.getPlayer()).equalsIgnoreCase("game")) {
                    event.setCancelled(true);
                    commands.remove(event.getPlayer());
                    commands.put(event.getPlayer(), event.getMessage());
                }
            }
        }
        if (event.getMessage().equalsIgnoreCase("finish")) {
            if (commands.containsKey(event.getPlayer())) {
                if (commands.get(event.getPlayer()).equalsIgnoreCase("finish")) {
                    Game game = plugin.getGameManager().getGames().get(0);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            plugin.setFinishLocationBlocks(game, location1, location2);
                        }
                    }.runTaskLater(plugin, 120);
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPickUp(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        if (plugin.getGameManager().getGames() != null) {
            if (plugin.getGameManager().getGamePlayers() != null) {
                if (plugin.getGameManager().getGamePlayers().containsKey(event.getPlayer())) {
                    if (plugin.getGameManager().getGamePlayers().get(event.getPlayer()).getPlayers().contains(event.getPlayer())) {
                        if (event.getItem() != null) {
                            ItemStack item = event.getItem().getItemStack();
                            if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                                for (SpecialItem specialItem : plugin.getSpecialItems()) {
                                    if (specialItem != null) {
                                        if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§aBanana")) {
                                            if (!player.getName().equalsIgnoreCase(player.getName())) {
                                                Minecart minecart = plugin.getGameManager().getGames().get(0).getPigs().get(player);
                                                double speed = minecart.getMaxSpeed();
                                                minecart.setMaxSpeed(speed - 10);
                                                new BukkitRunnable() {
                                                    @Override
                                                    public void run() {
                                                        minecart.setMaxSpeed(speed);
                                                    }
                                                }.runTaskLater(plugin, 20*10);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
