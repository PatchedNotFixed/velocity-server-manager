package de.gunnablescum.velocityservermanager.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import de.gunnablescum.velocityservermanager.ServerManager;
import de.gunnablescum.velocityservermanager.utils.DatabaseRegisteredServer;
import de.gunnablescum.velocityservermanager.utils.Messages;
import de.gunnablescum.velocityservermanager.utils.MySQL;
import net.kyori.adventure.text.minimessage.MiniMessage;

/**
 * Created by Noah Fetz on 27.05.2016.
 * Contributors: GunnableScum
 */
public class ServerSwitchListener {

    public ServerSwitchListener(ServerManager plugin) {
        plugin.getProxyServer().getEventManager().register(plugin, this);
    }

    @Subscribe
    public void onConnect(ServerPreConnectEvent e) {
        String serverTarget = e.getOriginalServer().getServerInfo().getName();

        DatabaseRegisteredServer server = MySQL.getServer(serverTarget);
        if(server == null) return; // Shouldn't happen but IntelliJ pisses me off about this

        if(server.restricted()) {
            if(!e.getPlayer().hasPermission("servermanager.server." + serverTarget) && !e.getPlayer().hasPermission("servermanager.ignorerestricion")) {
                MiniMessage mm = MiniMessage.miniMessage();
                e.getPlayer().sendMessage(Messages.PREFIX.append(mm.deserialize("<red>You're not allowed to join this server.")));
                e.setResult(ServerPreConnectEvent.ServerResult.denied());
            }
        }
    }
}
