package de.gunnablescum.velocityservermanager.utils;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import de.gunnablescum.velocityservermanager.ServerManager;
import net.kyori.adventure.text.Component;

import javax.annotation.Nullable;
import java.net.InetSocketAddress;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

public record DatabaseRegisteredServer(String systemName, String displayName, String address, int port, Boolean lobby, boolean restricted, boolean active, boolean online) {

    private final static ProxyServer proxyServer = ServerManager.getInstance().getProxyServer();

    public boolean hasDedicatedDisplayName() {
        return !(displayName.isEmpty() || displayName.isBlank());
    }

    @Override
    public String displayName() {
        return displayName.isEmpty() ? systemName : displayName;
    }

    public void empty(boolean force) {
        if(!active) return;
        Optional<RegisteredServer> server = ServerManager.getInstance().getProxyServer().getServer(systemName);
        if(server.isEmpty()) return;
        for(Player all : server.get().getPlayersConnected()) {
            if(force || !all.hasPermission("servermanager.ignorekick"))
                all.createConnectionRequest(ServerManager.lobbies.get(new SecureRandom().nextInt(ServerManager.lobbies.size()))).connect();
            all.sendMessage(Component.text(Messages.PREFIX + (force ? Messages.PREVIOUS_SERVER_DELETED_INFO : Messages.PREVIOUS_SERVER_EMPTIED)));
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
            source.sendMessage(Component.text(Messages.PREFIX + Messages.NO_PERMISSION));
            return;
        }
        source.sendMessage(Component.text(Messages.PREFIX + "§7Info about§8: " + displayName()));
        source.sendMessage(Component.text("§7Systemname§8:§a " + systemName()));
        source.sendMessage(Component.text("§7Status§8: " + onlineOfflineString(online())));
        source.sendMessage(Component.text("§7Displayname§8: " + (hasDedicatedDisplayName() ? displayName() : "§cNone")));
        source.sendMessage(Component.text("§7Enabled§8: " + trueFalseString(active())));
        source.sendMessage(Component.text("§7Lobby§8: " + trueFalseString(lobby())));
        source.sendMessage(Component.text("§7Restricted§8: " + trueFalseString(restricted())));
        source.sendMessage(Component.text("§7IP§8:§a " + address()));
        source.sendMessage(Component.text("§7Port§8:§a " + port()));
        if(!active()) return;

        StringBuilder players = null;
        RegisteredServer registeredServer = getFromProxy();
        if (registeredServer == null) {
            return;
        }

        for (Player all : registeredServer.getPlayersConnected()) {
            if (players == null) {
                players = new StringBuilder("§a" + all.getUsername());
            } else {
                players.append("§7, ").append(all.getUsername());
            }
        }

        if(players == null) players = new StringBuilder("§cNone");

        source.sendMessage(Component.text("§7Players§8: " + players));
    }

    // Haha, another tri-state boolean
    public boolean isProxyManaged() {
        return lobby == null;
    }

    private String trueFalseString(Boolean bool){
        return bool ? "§aTrue" : "§cFalse";
    }
    private String onlineOfflineString(Boolean bool){
        return bool ? "§aOnline" : "§cOffline";
    }

}
