package de.gunnablescum.velocityservermanager.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import de.gunnablescum.velocityservermanager.ServerManager;

import java.security.SecureRandom;

/**
 * Created by Noah Fetz on 06.09.2016.
 * Contributors: GunnableScum
 */
public class ServerKickListener {

    public ServerKickListener(ServerManager plugin) {
        plugin.getProxyServer().getEventManager().register(plugin, this);
    }

    @Subscribe
    public void onKick(KickedFromServerEvent e){
        if (e.getPlayer().getCurrentServer().isPresent() || ServerManager.lobbies.isEmpty()) return;
        e.getPlayer().createConnectionRequest(ServerManager.lobbies.get(new SecureRandom().nextInt(ServerManager.lobbies.size()))).connect();
    }
}
