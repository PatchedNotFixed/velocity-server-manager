package de.gunnablescum.velocityservermanager.utils;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import de.gunnablescum.velocityservermanager.ServerManager;
import net.kyori.adventure.text.minimessage.MiniMessage;

import javax.annotation.Nullable;
import java.net.InetSocketAddress;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

public record DatabaseRegisteredServer(String systemName, String displayName, String address, int port, Boolean lobby, boolean restricted, boolean active, boolean online) {

    private final static ProxyServer proxyServer = ServerManager.getInstance().getProxyServer();

    public boolean hasDedicatedDisplayName() {
        return !(displayName == null || displayName.isEmpty() || displayName.isBlank());
    }

    @Override
    public String displayName() {
        if(displayName == null) return systemName;
        return displayName.isEmpty() ? systemName : displayName;
    }

    public void empty(boolean force) {
        if(!active) return;
        Optional<RegisteredServer> server = ServerManager.getInstance().getProxyServer().getServer(systemName);
        if(server.isEmpty()) return;
        for(Player all : server.get().getPlayersConnected()) {
            if(force || !all.hasPermission("servermanager.ignorekick"))
                all.createConnectionRequest(ServerManager.lobbies.get(new SecureRandom().nextInt(ServerManager.lobbies.size()))).connect();
            all.sendMessage(force ? Messages.previousServerDeletedInfo() : Messages.previousServerEmptied());
        }
    }

    public void removeFromProxy() {
        Optional<RegisteredServer> server = ServerManager.getInstance().getProxyServer().getServer(systemName);
        server.ifPresent(registeredServer -> ServerManager.getInstance().getProxyServer().unregisterServer(registeredServer.getServerInfo()));
    }

    public void addToProxy() {
        ServerManager.getInstance().getProxyServer().registerServer(new ServerInfo(systemName, new InetSocketAddress(address, port)));
    }

    public void reloadInProxy() {
        removeFromProxy();
        addToProxy();
    }

    @Nullable
    public RegisteredServer getFromProxy() {
        return proxyServer.getServer(systemName).orElse(null);
    }

    public void deleteFromDatabase() {
        MySQL.deleteServer(systemName);
    }

    public void setActive(boolean isActive) {
        if(isActive) addToProxy();
        else removeFromProxy();

        MySQL.update("UPDATE servermanager_servers SET isactive = ? WHERE systemname = ?", List.of(
                new SQLStatementParameter(SQLStatementParameterType.INT, 1, isActive ? 1 : 0),
                new SQLStatementParameter(SQLStatementParameterType.STRING, 2, systemName)
        ));
    }

    public void setLobby(boolean isLobby) {
        MySQL.update("UPDATE servermanager_servers SET islobby = ? WHERE systemname = ?", List.of(
                new SQLStatementParameter(SQLStatementParameterType.INT, 1, isLobby ? 1 : 0),
                new SQLStatementParameter(SQLStatementParameterType.STRING, 2, systemName)
        ));
    }

    public void setRestricted(boolean isRestricted) {
        MySQL.update("UPDATE servermanager_servers SET isrestricted = ? WHERE systemname = ?", List.of(
                new SQLStatementParameter(SQLStatementParameterType.INT, 1, isRestricted ? 1 : 0),
                new SQLStatementParameter(SQLStatementParameterType.STRING, 2, systemName)
        ));
    }

    public void sendInfo(CommandSource source) {
        if(!source.hasPermission("servermanager.servers.info")) {
            source.sendMessage(Messages.noPermission());
            return;
        }
        MiniMessage mm = MiniMessage.miniMessage();
        source.sendMessage(Messages.PREFIX.append(mm.deserialize("<gray>Info about<dark_gray>: " + displayName())));
        source.sendMessage(mm.deserialize("<gray>Systemname<dark_gray>:<green> " + systemName()));
        source.sendMessage(mm.deserialize("<gray>Status<dark_gray>: " + onlineOfflineString(online())));
        source.sendMessage(mm.deserialize("<gray>Displayname<dark_gray>: <yellow>" + (hasDedicatedDisplayName() ? displayName() : "<red>None")));
        source.sendMessage(mm.deserialize("<gray>Enabled<dark_gray>: " + trueFalseString(active())));
        source.sendMessage(mm.deserialize("<gray>Lobby<dark_gray>: " + trueFalseString(lobby())));
        source.sendMessage(mm.deserialize("<gray>Restricted<dark_gray>: " + trueFalseString(restricted())));
        source.sendMessage(mm.deserialize("<gray>IP<dark_gray>:<green> " + address()));
        source.sendMessage(mm.deserialize("<gray>Port<dark_gray>:<green> " + port()));
        if(!active()) return;

        StringBuilder players = null;
        RegisteredServer registeredServer = getFromProxy();
        if (registeredServer == null) {
            return;
        }

        for (Player all : registeredServer.getPlayersConnected()) {
            if (players == null) {
                players = new StringBuilder("<green>" + all.getUsername());
            } else {
                players.append("<gray>, ").append(all.getUsername());
            }
        }

        if(players == null) players = new StringBuilder("<red>None");

        source.sendMessage(mm.deserialize("<gray>Players<dark_gray>: " + players));
    }

    // Haha, Tri-state boolean get rekt java
    public boolean isProxyManaged() {
        return lobby == null;
    }

    private String trueFalseString(Boolean bool){
        return bool ? "<green>True</green>" : "<red>False</red>";
    }
    private String onlineOfflineString(Boolean bool){
        return bool ? "<green>Online</green>" : "<red>Offline</red>";
    }

}
