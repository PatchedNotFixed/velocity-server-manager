package ch.fetz.ServerManager.spigot;

import ch.fetz.ServerManager.spigot.Commands.command_spigotservermanager;
import ch.fetz.ServerManager.spigot.Listener.InventoryClickEvent_Listener;
import ch.fetz.ServerManager.spigot.Utils.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Noah Fetz on 04.12.2016.
 */
public class SpigotServerManager extends JavaPlugin{

    public String prefix = "§f[§4SpigotServerManager§f] ";
    private MySQL mySQL;
    public String mysqlHost;
    public Integer mysqlPort;
    public String mysqlUser;
    public String mysqlDatabase;
    public String mysqlPassword;
    public SpigotManager spigotManager;
    private MenuManager menuManager;
    private SpigotMetrics spigotMetrics;
    private PluginMessageSender pluginMessageSender;

    @Override
    public void onEnable(){
        createConfig();

        this.mySQL = new MySQL(this);
        this.spigotManager = new SpigotManager(this);
        this.menuManager = new MenuManager(this);
        this.pluginMessageSender = new PluginMessageSender(this);

        this.spigotMetrics = new SpigotMetrics(this);

        Bukkit.getMessenger().registerIncomingPluginChannel(this, "bsm:cmd", pluginMessageSender);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "bsm:cmd");
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        registerCommands();
        registerListener();

        getServer().getConsoleSender().sendMessage("§8-----------===== " + prefix + "§8=====-----------");
        getServer().getConsoleSender().sendMessage("§7The plugin was §asuccessfully §7activated");
        getServer().getConsoleSender().sendMessage("§7Plugin by§8: §eNojo | Noah");
        getServer().getConsoleSender().sendMessage("§7Plugin Version§8: §e" + this.getDescription().getVersion());
        getServer().getConsoleSender().sendMessage("§8-----------===== " + prefix + "§8=====-----------");
    }

    @Override
    public void onDisable(){
    }

    private void createConfig(){
        if(!getConfig().contains("MySQL_Host")){
            getConfig().set("MySQL_Host", "Hostname");
        }
        if(!getConfig().contains("MySQL_Port")){
            getConfig().set("MySQL_Port", 3306);
        }
        if(!getConfig().contains("MySQL_User")){
            getConfig().set("MySQL_User", "Username");
        }
        if(!getConfig().contains("MySQL_Database")){
            getConfig().set("MySQL_Database", "Database");
        }
        if(!getConfig().contains("MySQL_Password")){
            getConfig().set("MySQL_Password", "Password");
        }
        try{
            saveConfig();
        }catch(Exception ex){
        }

        this.mysqlHost = getConfig().getString("MySQL_Host");
        this.mysqlPort = getConfig().getInt("MySQL_Port");
        this.mysqlUser = getConfig().getString("MySQL_User");
        this.mysqlDatabase = getConfig().getString("MySQL_Database");
        this.mysqlPassword = getConfig().getString("MySQL_Password");

    }

    private void registerCommands(){
        getCommand("servermanagerclient").setExecutor(new command_spigotservermanager(this));
        getCommand("smc").setExecutor(new command_spigotservermanager(this));
    }

    private void registerListener(){
        new InventoryClickEvent_Listener(this);
    }

    public MySQL getMySQL(){
        return this.mySQL;
    }

    public MenuManager getMenuManager(){
        return this.menuManager;
    }

    public SpigotManager getSpigotManager(){
        return this.spigotManager;
    }
}
