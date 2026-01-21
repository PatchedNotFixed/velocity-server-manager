package de.gunnablescum.velocityservermanager.utils;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.gunnablescum.velocityservermanager.ServerManager;

/**
 * Created by Noah Fetz on 24.01.2017.
 * Contributors: GunnableScum
 */
public class ServerPinger {

    public static void checkAllServers(){
        for (RegisteredServer server : ServerManager.getInstance().getProxyServer().getAllServers().stream().toList()) {
            server.ping().thenAccept(serverPing ->
                    update(server.getServerInfo().getName(), true)
                )
                .exceptionally(throwable -> {
                    update(server.getServerInfo().getName(), false);
                    return null;
                });
        }
    }

    private static void update(String name, boolean online) {
        if(ServerManager.serverStatusCache.getOrDefault(name, false) != online) {
            VSMCommand.sendPermittedBroadcast(online ? Messages.serverOnlineBroadcast(name) : Messages.serverOfflineBroadcast(name));
        }
        ServerManager.serverStatusCache.put(name, online);
    }
}
