package de.gunnablescum.velocityservermanager;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.gunnablescum.velocityservermanager.commands.*;
import de.gunnablescum.velocityservermanager.listener.ConnectionListener;
import de.gunnablescum.velocityservermanager.listener.ServerKickListener;
import de.gunnablescum.velocityservermanager.listener.ServerSwitchListener;
import de.gunnablescum.velocityservermanager.utils.DatabaseRegisteredServer;
import de.gunnablescum.velocityservermanager.utils.Messages;
import de.gunnablescum.velocityservermanager.utils.MySQL;
import de.gunnablescum.velocityservermanager.utils.ServerPinger;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Noah Fetz on 20.05.2016.
 * Contributors: GunnableScum
 */
@Plugin(
        id = "velocityservermanager",
        name = "VelocityServerManager",
        version = "1.0",
        description = "Plugin for Dynamic Server Management for the Velocity Proxy.",
        authors = {"Noah Fetz", "GunnableScum"}
)
public class ServerManager {

    @Inject
    private ProxyServer proxyServer;

    @Inject
    private Logger logger;

    @Inject
    @DataDirectory
    private Path dataDirectory;

    public static final List<RegisteredServer> lobbies = new ArrayList<>();
    public static final Map<String, Boolean> serverStatusCache = new HashMap<>();

    private static ServerManager instance;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        instance = this;
        logger.info("Initializing VelocityServerManager...");
        MySQL.init();
        if(!MySQL.isConnected()) return; // Huh?

        // In case of an ungraceful shutdown, delete all Fallback Servers from Database
        MySQL.deleteFallbackServers();

        Messages.loadMessages();

        registerCommands();
        registerListener();
        startServerPinging();

        addFallbackServersToLobbies();
        MySQL.insertFallbackServers(lobbies);

        DatabaseRegisteredServer.addAllServers();
        logger.info("VelocityServerManager has been successfully initialized!");
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent e) {
        // Delete all Fallback Servers from Database
        MySQL.deleteFallbackServers();
    }

    private void addFallbackServersToLobbies() {
        lobbies.addAll(proxyServer.getAllServers());
    }

    @Subscribe
    public void onProxyPing(ProxyPingEvent event){
        if(MySQL.isConnected()) return;
        proxyServer.shutdown(Component.text("VelocityServerManager couldn't connect to the Database. Shutting down Proxy."));
    }

    private void registerCommands(){
        CommandManager manager = proxyServer.getCommandManager();
        new AddServerCommand(this, manager);
        new ClearServerCommand(this, manager);
        new DeleteServerCommand(this, manager);
        new DisableServerCommand(this, manager);
        new EnableServerCommand(this, manager);
        new GotoCommand(this, manager);
        new HubCommand(this, manager);
        new ReloadServerCommand(this, manager);
        new ServerInfoCommand(this, manager);
        new ServersCommand(manager);
        new ServerListCommand(this, manager);
        new FlagServerCommand(this, manager);
        new UnflagServerCommand(this, manager);
        new WhereAmICommand(this, manager);
    }

    private void registerListener(){
        new ServerKickListener(this);
        new ConnectionListener(this);
        new ServerSwitchListener(this);
    }

    public static ServerManager getInstance() {
        return instance;
    }

    public Logger getLogger() {
        return logger;
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

    public ProxyServer getProxyServer() {
        return proxyServer;
    }

    private void startServerPinging(){
        int checkDelay = 10;
        proxyServer.getScheduler().buildTask(this, ServerPinger::checkAllServers).delay(checkDelay, TimeUnit.SECONDS).repeat(checkDelay, TimeUnit.SECONDS).schedule();
    }
}
