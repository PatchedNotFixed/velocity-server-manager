package de.gunnablescum.velocityservermanager;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.gunnablescum.velocityservermanager.commands.*;
import de.gunnablescum.velocityservermanager.listener.ConnectionListener;
import de.gunnablescum.velocityservermanager.listener.ServerKickListener;
import de.gunnablescum.velocityservermanager.listener.ServerSwitchListener;
import de.gunnablescum.velocityservermanager.utils.BackendServerManager;
import de.gunnablescum.velocityservermanager.utils.Messages;
import de.gunnablescum.velocityservermanager.utils.MySQL;
import de.gunnablescum.velocityservermanager.utils.ServerPinger;
import net.kyori.adventure.text.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by Noah Fetz on 20.05.2016.
 * Contributors: GunnableScum
 */
@Plugin(
        id = "velocityservermanager",
        name = "VelocityServerManager",
        version = "1.0-SNAPSHOT",
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

    public static ArrayList<RegisteredServer> lobbies = new ArrayList<>();
    public static ArrayList<RegisteredServer> nonlobbies = new ArrayList<>();
    private final int checkDelay = 10;

    private static ServerManager instance;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        instance = this;
        logger.info("Initializing VelocityServerManager...");
        MySQL.init();
        if(!MySQL.isConnected()) return;
        Messages.loadMessages();
        BackendServerManager.addAllServers();

        registerCommands();
        registerListener();
        startServerPinging();

        if(proxyServer.getServer("lobby").isEmpty()) {
            logger.severe("No server with the name 'lobby' found! Please make sure to have at least one server registered as 'lobby', otherwise velocity will not boot.");
            logger.info("This is so that there is always at least one server to connect to.");
            proxyServer.shutdown(Component.text("VelocityServerManager couldn't find a server named 'lobby'. Shutting down Proxy."));
            return;
        }

        RegisteredServer fallback = proxyServer.getServer("lobby").get();

        lobbies.add(fallback);

        if(!MySQL.isInDatabase("lobby")) MySQL.insertFallbackServer(fallback);

        logger.info("VelocityServerManager has been successfully initialized!");
    }

    @Subscribe
    public void onProxyPing(ProxyPingEvent event){
        if(MySQL.isConnected()) return;
        proxyServer.shutdown(Component.text("VelocityServerManager couldn't connect to the Database. Shutting down Proxy."));
    }

    private void registerCommands(){
        CommandManager manager = proxyServer.getCommandManager();
        new GotoCommand(this, manager);
        new HubCommand(this, manager);
        new NotifyCommand(this, manager);
        new WhereAmICommand(this, manager);
        new ServerManagerCommand(this, manager);
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
        proxyServer.getScheduler().buildTask(this, ServerPinger::checkAllServers).delay(checkDelay, TimeUnit.SECONDS).repeat(checkDelay, TimeUnit.SECONDS).schedule();
    }
}
