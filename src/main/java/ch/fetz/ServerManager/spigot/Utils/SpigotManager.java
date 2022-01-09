package ch.fetz.ServerManager.spigot.Utils;

import ch.fetz.ServerManager.Utils.SQLStatementParameter;
import ch.fetz.ServerManager.Utils.SQLStatementParameterType;
import ch.fetz.ServerManager.spigot.SpigotServerManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Noah Fetz on 04.12.2016.
 */
public class SpigotManager {

    private final SpigotServerManager plugin;

    public SpigotManager(SpigotServerManager spigotServerManager) {
        this.plugin = spigotServerManager;
    }

    /**
     * Returns all servers in an ArrayList
     *
     * @return
     */
    public ArrayList<String> getAllServers(){
        ArrayList<String> servers = new ArrayList<>();
        ResultSet rs = plugin.getMySQL().getResult("SELECT * FROM servermanager_servers", new ArrayList<>());
        try {
            while(rs.next()){
                servers.add(rs.getString("systemname"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return servers;
    }

    /**
     * Gets the ip of a server
     *
     * @param name
     * @return
     */
    public String getIp(String name){
        ResultSet rs = plugin.getMySQL().getResult("SELECT * FROM servermanager_servers WHERE systemname = ?", new ArrayList<SQLStatementParameter>() {{add(new SQLStatementParameter(SQLStatementParameterType.STRING, 1, name));}});
        try {
            while(rs.next()){
                return rs.getString("ip");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns the port of a server
     *
     * @param name
     * @return
     */
    public Integer getPort(String name){
        ResultSet rs = plugin.getMySQL().getResult("SELECT * FROM servermanager_servers WHERE systemname = ?", new ArrayList<SQLStatementParameter>() {{add(new SQLStatementParameter(SQLStatementParameterType.STRING, 1, name));}});
        try {
            while(rs.next()){
                return rs.getInt("port");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets the displayname of a server
     *
     * @param name
     * @return
     */
    public String getDisplayName(String name){
        ResultSet rs = plugin.getMySQL().getResult("SELECT * FROM servermanager_servers WHERE systemname = ?", new ArrayList<SQLStatementParameter>() {{add(new SQLStatementParameter(SQLStatementParameterType.STRING, 1, name));}});
        try {
            while(rs.next()){
                return rs.getString("displayname");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns the MOTD of a server
     *
     * @param name
     * @return
     */
    public String getMOTD(String name){
        ResultSet rs = plugin.getMySQL().getResult("SELECT * FROM servermanager_servers WHERE systemname = ?", new ArrayList<SQLStatementParameter>() {{add(new SQLStatementParameter(SQLStatementParameterType.STRING, 1, name));}});
        try {
            while(rs.next()){
                return rs.getString("motd");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns wheter a server is a lobby or not
     *
     * @param name
     * @return
     */
    public Boolean isLobby(String name){
        ResultSet rs = plugin.getMySQL().getResult("SELECT * FROM servermanager_servers WHERE systemname = ?", new ArrayList<SQLStatementParameter>() {{add(new SQLStatementParameter(SQLStatementParameterType.STRING, 1, name));}});
        try {
            while(rs.next()){
                return rs.getBoolean("islobby");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Returns wheter a server is active or not
     *
     * @param name
     * @return
     */
    public Boolean isActive(String name){
        ResultSet rs = plugin.getMySQL().getResult("SELECT * FROM servermanager_servers WHERE systemname = ?", new ArrayList<SQLStatementParameter>() {{add(new SQLStatementParameter(SQLStatementParameterType.STRING, 1, name));}});
        try {
            while(rs.next()){
                return rs.getBoolean("isactive");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Returns wheter a server is restricted or not
     *
     * @param name
     * @return
     */
    public Boolean isRestricted(String name){
        ResultSet rs = plugin.getMySQL().getResult("SELECT * FROM servermanager_servers WHERE systemname = ?", new ArrayList<SQLStatementParameter>() {{add(new SQLStatementParameter(SQLStatementParameterType.STRING, 1, name));}});
        try {
            while(rs.next()){
                return rs.getBoolean("isrestricted");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Returns the online status from the database
     *
     * @param name
     * @return
     */
    public Boolean isOnline(String name){
        ResultSet rs = plugin.getMySQL().getResult("SELECT * FROM servermanager_servers WHERE systemname = ?", new ArrayList<SQLStatementParameter>() {{add(new SQLStatementParameter(SQLStatementParameterType.STRING, 1, name));}});
        try {
            while(rs.next()){
                return rs.getBoolean("isonline");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
