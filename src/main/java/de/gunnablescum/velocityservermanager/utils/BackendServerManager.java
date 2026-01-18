package de.gunnablescum.velocityservermanager.utils;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import de.gunnablescum.velocityservermanager.ServerManager;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Noah Fetz on 20.05.2016.
 * Contributors: GunnableScum
 */
public class BackendServerManager {

    private static final ProxyServer proxyServer = ServerManager.getInstance().getProxyServer();

    /**
     * Adds a server to the database
     *
     * @param name The system name of the Server
     * @param ip The IP Address of the Server
     * @param port The Port of the Server
     * @param displayname The Display Name of the Server
     * @param isLobby Whether the Server is a Lobby or not
     * @param isActive Whether the Server is active or not
     * @param isRestricted Whether the Server needs permission to join
     */
    public static void createServer(String name, String ip, Integer port, String displayname, Boolean isLobby, Boolean isActive, Boolean isRestricted, Boolean isOnline){
        if(MySQL.isInDatabase(name)) return;
        ArrayList<SQLStatementParameter> parameters = new ArrayList<>();
        parameters.add(new SQLStatementParameter(SQLStatementParameterType.STRING, 1, name));
        parameters.add(new SQLStatementParameter(SQLStatementParameterType.STRING, 2, ip));
        parameters.add(new SQLStatementParameter(SQLStatementParameterType.INT, 3, port));
        parameters.add(new SQLStatementParameter(SQLStatementParameterType.STRING, 4, displayname));
        parameters.add(new SQLStatementParameter(SQLStatementParameterType.BOOL, 5, isActive));
        parameters.add(new SQLStatementParameter(SQLStatementParameterType.BOOL, 6, isLobby));
        parameters.add(new SQLStatementParameter(SQLStatementParameterType.BOOL, 7, isRestricted));
        parameters.add(new SQLStatementParameter(SQLStatementParameterType.BOOL, 8, isOnline));

        MySQL.update("INSERT INTO servermanager_servers (systemname, ip, port, displayname, isactive, islobby, isrestricted, isonline) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", parameters);
        if(isActive) addServerToVelocity(name);
    }

    /**
     * Adds a player to the database
     *
     * @param p The player to add to the database
     */
    public static void createPlayer(Player p){
        if(MySQL.getPossiblyOutdatedPlayerName(p.getUniqueId().toString()) != null) return;
        ArrayList<SQLStatementParameter> parameters = new ArrayList<>();
        parameters.add(new SQLStatementParameter(SQLStatementParameterType.STRING, 1, p.getUniqueId().toString()));
        parameters.add(new SQLStatementParameter(SQLStatementParameterType.STRING, 2, p.getUsername()));
        parameters.add(new SQLStatementParameter(SQLStatementParameterType.BOOL, 3, false));

        MySQL.update("INSERT INTO servermanager_players (uuid, name, notify) VALUES (?, ?, ?)", parameters);
    }

    /**
     * Checks if the current player name is up-to-date
     *
     * @param p The player to check the name for
     */
    public static void checkPlayerName(Player p){
        if(MySQL.getPossiblyOutdatedPlayerName(p.getUniqueId().toString()).equals(p.getUsername())){
            ArrayList<SQLStatementParameter> parameters = new ArrayList<>();
            parameters.add(new SQLStatementParameter(SQLStatementParameterType.STRING, 1, p.getUsername()));
            parameters.add(new SQLStatementParameter(SQLStatementParameterType.STRING, 2, p.getUniqueId().toString()));

            MySQL.update("UPDATE servermanager_players SET name = ? WHERE uuid = ?", parameters);
        }
    }

    /**
     * Sets the notification status for a player
     *
     * @param p The player to set the notification status for
     * @param notify Whether the player should receive notifications or not
     */
    public static void setNotificationStatus(Player p, boolean notify){
        ArrayList<SQLStatementParameter> parameters = new ArrayList<>();
        parameters.add(new SQLStatementParameter(SQLStatementParameterType.BOOL, 1, notify));
        parameters.add(new SQLStatementParameter(SQLStatementParameterType.STRING, 2, p.getUniqueId().toString()));

        MySQL.update("UPDATE servermanager_players SET notify = ? WHERE uuid = ?", parameters);
    }

    /**
     * Returns the notification status of a player
     *
     * @param p The player to get the notification status for
     * @return Whether the player is receiving notifications or not
     */
    public static boolean getNotificationStatus(Player p){
        return MySQL.getNotificationStatus(p.getUniqueId().toString());
    }

    /**
     * Returns all servers in an ArrayList
     *
     * @return List<DatabaseRegisteredServer> of all server system names
     */
    public static List<DatabaseRegisteredServer> getAllServers(){
        return MySQL.getAllServers();
    }

    /**
     * Sets the online status in the Database
     *
     * @param name The system name of the Server
     * @param isOnline The online status of the Server
     */
    public static void setIsOnline(String name, Boolean isOnline){
        ArrayList<SQLStatementParameter> parameters = new ArrayList<>();
        parameters.add(new SQLStatementParameter(SQLStatementParameterType.BOOL, 1, isOnline));
        parameters.add(new SQLStatementParameter(SQLStatementParameterType.STRING, 2, name));

        MySQL.update("UPDATE servermanager_servers SET isonline = ? WHERE systemname = ?", parameters);
    }

    /**
     * Adds all servers to Velocity
     */
    public static void addAllServers(){
        for(DatabaseRegisteredServer server : getAllServers()){
            if (!server.active()) continue;
            addServerToVelocity(server);
        }
    }

    /**
     * Adds a server to Velocity
     *
     * @param name The system name of the Server
     */
    public static void addServerToVelocity(String name){
        DatabaseRegisteredServer server = MySQL.getServer(name);
        if(server == null) throw new IllegalArgumentException("Server " + name + " not found in database.");
        ServerInfo serverInfo = new ServerInfo(name, new InetSocketAddress(server.address(), server.port()));
        proxyServer.registerServer(serverInfo);
        (server.lobby() ? ServerManager.lobbies : ServerManager.nonlobbies).add(proxyServer.getServer(name).orElseThrow());
    }

    /**
     * Adds a server to Velocity
     *
     * @param server The DatabaseRegisteredServer to add
     */
    public static void addServerToVelocity(DatabaseRegisteredServer server){
        ServerInfo serverInfo = new ServerInfo(server.systemName(), new InetSocketAddress(server.address(), server.port()));
        proxyServer.registerServer(serverInfo);
        (server.lobby() ? ServerManager.lobbies : ServerManager.nonlobbies).add(proxyServer.getServer(server.systemName()).orElseThrow());
    }

    /**
     * Removes all servers from Velocity
     */
    public static void clearAllServers(){
        proxyServer.getAllServers().forEach(server -> proxyServer.unregisterServer(server.getServerInfo()));
        ServerManager.lobbies.clear();
        ServerManager.nonlobbies.clear();
    }
}
