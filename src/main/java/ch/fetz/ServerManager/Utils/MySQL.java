package ch.fetz.ServerManager.Utils;

import ch.fetz.ServerManager.ServerManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.md_5.bungee.api.ProxyServer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Noah Fetz on 20.05.2016.
 */
public class MySQL {
    private final ServerManager plugin;

    private final HikariDataSource dataSource;

    public MySQL(ServerManager serverManager) {
        this.plugin = serverManager;

        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mariadb://" + plugin.mysqlHost + ":" + plugin.mysqlPort + "/" + plugin.mysqlDatabase);
        config.setUsername(plugin.mysqlUser);
        config.setPassword(plugin.mysqlPassword);
        config.setPoolName("BSM-Pool");

        this.dataSource = new HikariDataSource(config);

        this.createTable();
    }

    public void createTable(){
        try {
            this.update("CREATE TABLE IF NOT EXISTS servermanager_servers(id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, systemname TEXT, ip TEXT, port INT, displayname TEXT, motd TEXT, islobby BOOLEAN, isactive BOOLEAN, isrestricted BOOLEAN, isonline BOOLEAN)", new ArrayList<>());
            this.update("CREATE TABLE IF NOT EXISTS servermanager_players(id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, uuid TEXT, name TEXT, notify BOOLEAN)", new ArrayList<>());
        } catch (Exception ex) {
            ProxyServer.getInstance().getConsole().sendMessage(plugin.prefix + "§cCould not create the MySQL Table!");
        }
    }

    public void update(String qry, ArrayList<SQLStatementParameter> parameters){
        try {
            Connection connection = this.dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(qry);

            for(SQLStatementParameter parameter : parameters) {
                switch (parameter.type) {
                    case STRING:
                        ps.setString(parameter.index, (String)parameter.value);
                        break;

                    case INT:
                        ps.setInt(parameter.index, (int)parameter.value);
                        break;

                    case DOUBLE:
                        ps.setDouble(parameter.index, (double)parameter.value);
                        break;

                    case BOOL:
                        ps.setBoolean(parameter.index, (boolean)parameter.value);
                        break;

                    case LONG:
                        ps.setLong(parameter.index, (long)parameter.value);
                }
            }

            ps.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getResult(String qry, ArrayList<SQLStatementParameter> parameters){
        try {
            Connection connection = this.dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(qry);

            for(SQLStatementParameter parameter : parameters) {
                switch (parameter.type) {
                    case STRING:
                        ps.setString(parameter.index, (String)parameter.value);
                        break;

                    case INT:
                        ps.setInt(parameter.index, (int)parameter.value);
                        break;

                    case DOUBLE:
                        ps.setDouble(parameter.index, (double)parameter.value);
                        break;

                    case BOOL:
                        ps.setBoolean(parameter.index, (boolean)parameter.value);
                        break;

                    case LONG:
                        ps.setLong(parameter.index, (long)parameter.value);
                }
            }

            ResultSet rs = ps.executeQuery();
            connection.close();
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();

            return null;
        }
    }
}
