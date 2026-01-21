package de.gunnablescum.velocityservermanager.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import de.gunnablescum.velocityservermanager.ServerManager;

import java.security.SecureRandom;

/**
 * Created by Noah Fetz on 21.05.2016.
 * Contributors: GunnableScum
 */
public class ConnectionListener {

    public ConnectionListener(ServerManager plugin) {
        plugin.getProxyServer().getEventManager().register(plugin, this);
    }

    @Subscribe
    public void onJoin(ServerPreConnectEvent e) {
        if(e.getPreviousServer() == null) {
            e.setResult(ServerPreConnectEvent.ServerResult.allowed(ServerManager.lobbies.get(new SecureRandom().nextInt(ServerManager.lobbies.size()))));
        }
    }
}
