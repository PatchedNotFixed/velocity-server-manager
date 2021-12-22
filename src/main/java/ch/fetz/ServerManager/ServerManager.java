package ch.fetz.ServerManager;

import ch.fetz.ServerManager.Commands.*;
import ch.fetz.ServerManager.Listener.*;
import ch.fetz.ServerManager.Utils.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Noah Fetz on 20.05.2016.
 */
public class ServerManager extends Plugin {
    //=========[Don't copy my code :c]=========
    private ServerManager instance;
    private Manager manager;
    private MySQL mySQL;
    private Metrics metrics;
    private Messages messages;
    private Updater updater;
    private ServerPinger serverPinger;
    public String prefix = "§f[§4ServerManager§f] ";
    public Configuration config;
    public String mysqlHost;
    public Integer mysqlPort;
    public String mysqlUser;
    public String mysqlDatabase;
    public String mysqlPassword;
    private Connection con;
    public boolean setHeader;
    public String headerText;
    public boolean setFooter;
    public String footerText;
    public String motd;
    public String hubCommands;
    public boolean enableMOTD = true;
    public boolean onemoreplayer = false;
    public boolean activateUpdater = true;
    public boolean forcedServers = false;
    public ArrayList<ServerInfo> lobbies = new ArrayList<>();
    public ArrayList<ServerInfo> nonlobbies = new ArrayList<>();
    public ArrayList<ProxiedPlayer> receiver = new ArrayList<>();
    private PluginMessageManager pluginMessageManager;
    private final int configVersion = 3;
    private ScheduledTask serverPingTask;
    public boolean enableOnlineCheck = true;
    private int checkDelay = 10;

    @Override
    public void onEnable(){
        instance = this;
        manager = new Manager(this);
        mySQL = new MySQL(this);
        messages = new Messages(this);
        updater = new Updater(this);
        serverPinger = new ServerPinger(this);
        pluginMessageManager = new PluginMessageManager(this);

        getProxy().registerChannel("bsm:cmd");

        createConfig();
        registerCommands();
        registerListener();
        messages.loadMessages();
        try{
            if(activateUpdater){
                updater.checkForUpdate();
            }
        }catch(Exception ex){
        }

        mySQL.connect();
        mySQL.createTable();

        manager.addAllServers();

        if(this.enableOnlineCheck){
            startServerPinging();
        }

        ProxyServer.getInstance().getConsole().sendMessages(prefix + "§7All servers were §asuccessfully §7added");

        metrics = new Metrics(this);

        ProxyServer.getInstance().getConsole().sendMessage("§8-----------===== " + prefix + "§8=====-----------");
        ProxyServer.getInstance().getConsole().sendMessage("§7The plugin was §asuccessfully §7activated");
        ProxyServer.getInstance().getConsole().sendMessage("§7Plugin by§8: §eNojo | Noah");
        ProxyServer.getInstance().getConsole().sendMessage("§7Plugin Version§8: §e" + this.getDescription().getVersion());
        ProxyServer.getInstance().getConsole().sendMessage("§8-----------===== " + prefix + "§8=====-----------");

    }

    @Override
    public void onDisable(){
        try{
            serverPingTask.cancel();
            for(ServerInfo server : lobbies){
                getManager().setIsOnline(server.getName() ,false);
            }
            for(ServerInfo server : nonlobbies){
                getManager().setIsOnline(server.getName() ,false);
            }
            mySQL.close();
        }catch (Exception e){
        }
        ProxyServer.getInstance().getConsole().sendMessage("§8-----------===== " + prefix + "§8=====-----------");
        ProxyServer.getInstance().getConsole().sendMessage("§7The plugin was §asuccessfully §7deactivated");
        ProxyServer.getInstance().getConsole().sendMessage("§7Plugin by§8: §eNojo | Noah");
        ProxyServer.getInstance().getConsole().sendMessage("§8-----------===== " + prefix + "§8=====-----------");
    }

    private void createConfig(){
        try{
            if (!getDataFolder().exists()){
                getDataFolder().mkdir();
            }
            File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
                try (InputStream in = getResourceAsStream("config.yml")) {
                    Files.copy(in, file.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            if(config.getInt("ConfigVersion") < configVersion){
                File backup = new File(getDataFolder(), "config_BACKUP.yml");
                file.renameTo(backup);
                File file2 = new File(getDataFolder(), "config.yml");
                if (!file2.exists()) {
                    try (InputStream in = getResourceAsStream("config.yml")) {
                        Files.copy(in, file2.toPath());
                        config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file2);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            this.mysqlHost = config.getString("MySQL.Host");
            this.mysqlPort = config.getInt("MySQL.Port");
            this.mysqlUser = config.getString("MySQL.User");
            this.mysqlDatabase = config.getString("MySQL.Database");
            this.mysqlPassword = config.getString("MySQL.Password");
            this.setHeader = config.getBoolean("Header.SetHeader");
            this.headerText = config.getString("Header.HeaderText");
            this.setFooter = config.getBoolean("Footer.SetFooter");
            this.footerText = config.getString("Footer.FooterText");
            this.motd = config.getString("ServerPing.MOTDText");
            this.enableMOTD = config.getBoolean("ServerPing.SetMOTD");
            this.onemoreplayer = config.getBoolean("ServerPing.OneMorePlayer");
            this.hubCommands = config.getString("General.HubCommands");
            this.activateUpdater = config.getBoolean("General.AutoUpdater");
            this.forcedServers = config.getBoolean("General.ForceServers");
            this.enableOnlineCheck = config.getBoolean("General.EnableOnlineOfflineCheck");
            this.checkDelay = config.getInt("General.OnlineOfflineCheckDelay");

            this.prefix = ChatColor.translateAlternateColorCodes('&', config.getString("Language.prefix") + " ");
        }catch (Exception ex) {
            ProxyServer.getInstance().getConsole().sendMessages(prefix + "§cThe config could not be created! Please PM NojoLP with the following StackTrace on the Spigot-Forums!");
            ProxyServer.getInstance().getConsole().sendMessages(prefix + "§7================================§8[§4COPY STACK TRACE FROM HERE§8]§7================================");
            ex.printStackTrace();
            ProxyServer.getInstance().getConsole().sendMessages(prefix + "§7================================§8[§4COPY STACK TRACE UP TO HERE§8]§7================================");
        }
    }

    private void registerCommands(){
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new command_servers("servermanager", this));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new command_servers("sm", this));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new command_whereami("whereami", this));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new command_goto("goto", this));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new command_greload("greload", this));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new command_notify("notify", this));
        for(String command : hubCommands.split(";")){
            if(!command.equalsIgnoreCase("null")){
                ProxyServer.getInstance().getPluginManager().registerCommand(this, new command_hub(command, this));
            }
        }
    }

    private void registerListener(){
        new JoinEvent_Listener(this);
        new ServerSwitch_Listener(this);
        new ServerConnectEvent_Listener(this);
        new Ping_Listener(this);
        new ServerKickEvent_Listener(this);
        //new ProxyReload_Listener(this);
    }

    public ServerManager getInstance(){
        return this.instance;
    }

    public Manager getManager(){
        return this.manager;
    }

    public Connection getCon(){
        return this.con;
    }

    public void setCon(Connection setcon){
        this.con = setcon;
    }

    public MySQL getMySQL(){
        return this.mySQL;
    }

    public Messages getMessages(){
        return this.messages;
    }

    private void startServerPinging(){
        try{
            serverPingTask = ProxyServer.getInstance().getScheduler().schedule(this, () -> serverPinger.checkAllServers(), checkDelay, checkDelay, TimeUnit.SECONDS);
        }catch (Exception ex){
        }
    }
}
