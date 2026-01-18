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
                    BackendServerManager.setIsOnline(server.getServerInfo().getName(), true))
                .exceptionally(throwable -> {
                    BackendServerManager.setIsOnline(server.getServerInfo().getName(), false);
                    return null;
                });
        }
    }
}
