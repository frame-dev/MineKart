package de.framedev.minekart.managers;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ProcessSnapshot;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.driver.service.ServiceLifeCycle;
import de.dytanic.cloudnet.ext.bridge.ServiceInfoSnapshotUtil;
import de.dytanic.cloudnet.ext.bridge.bukkit.BukkitCloudNetHelper;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import de.dytanic.cloudnet.ext.signs.node.CloudNetSignsModule;
import org.bukkit.entity.Player;

/**
 * This Plugin was Created by FrameDev
 * Package : de.framedev.minekart.managers
 * Date: 02.02.21
 * Project: MineKart
 * Copyrighted by FrameDev
 */

public class CloudNetManager {

    public ICloudPlayer getCloudPlayer(Player player) {
        ICloudPlayer cloudPlayer = null;
        IPlayerManager playerManager = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class);
        cloudPlayer = playerManager.getOnlinePlayer(player.getUniqueId());
        return cloudPlayer;
    }

    public void changeToIngame() {
        BukkitCloudNetHelper.changeToIngame();
    }

    public void changeToInLobby() {
        BukkitCloudNetHelper.setState("INLOBBY");
    }
}
