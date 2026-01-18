package de.gunnablescum.velocityservermanager.commands;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.gunnablescum.velocityservermanager.ServerManager;
import de.gunnablescum.velocityservermanager.utils.BackendServerManager;
import de.gunnablescum.velocityservermanager.utils.DatabaseRegisteredServer;
import de.gunnablescum.velocityservermanager.utils.Messages;
import de.gunnablescum.velocityservermanager.utils.MySQL;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import javax.annotation.Nullable;

/**
 * Created by Noah Fetz on 20.05.2016.
 * Contributors: GunnableScum
 */
public class ServerManagerCommand implements SimpleCommand {

    public ServerManagerCommand(ServerManager plugin, CommandManager manager) {
        manager.register(manager.metaBuilder("servermanager").aliases("sm").plugin(plugin).build(), this);
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        String name = "the network Administrator";
        MiniMessage mm = MiniMessage.miniMessage();
        if(source instanceof Player p) {
            name = p.getUsername();
        }
        if(args.length == 0) {
            sendAvailableCommands(source);
            return;
        }
        String action = args[0];

        if(args.length == 1) {
            if(action.equalsIgnoreCase("list")) {
                printServerList(source);
                return;
            }

            if(action.equalsIgnoreCase("reload")){
                if(!source.hasPermission("servermanager.servers.reload")) {
                    source.sendMessage(Messages.noPermission());
                    return;
                }
                BackendServerManager.clearAllServers();
                BackendServerManager.addAllServers();
                sendPermittedBroadcast(Messages.allServerReloadBroadcast(name));
                return;
            }

            sendAvailableCommands(source);
            return;
        }

        String serverName = args[1];
        DatabaseRegisteredServer target = MySQL.getServer(serverName);
        if(!action.equalsIgnoreCase("add")) {
            if(target == null) {
                source.sendMessage(Messages.serverNotFound());
                return;
            }
            if(target.isProxyManaged()) {
                source.sendMessage(Messages.proxyManagedServer());
                return;
            }
        }

        if(args.length == 2) {
            if(action.equalsIgnoreCase("delete")){
                if(!source.hasPermission("servermanager.servers.delete")) {
                    source.sendMessage(Messages.noPermission());
                    return;
                }
                target.empty(true);
                target.deleteFromDatabase();
                target.removeFromProxy();

                sendPermittedBroadcast(Messages.serverDeletedBroadcast(name, target.displayName()));
                return;
            }

            if(action.equalsIgnoreCase("reload")) {
                if(!source.hasPermission("servermanager.servers.reload")) {
                    source.sendMessage(Messages.noPermission());
                    return;
                }
                target.reloadInProxy();
                sendPermittedBroadcast(Messages.serverReloadedBroadcast(name, target.displayName()));
                return;
            }

            if(action.equalsIgnoreCase("enable")) {
                if(!source.hasPermission("servermanager.servers.enable")) {
                    source.sendMessage(Messages.noPermission());
                    return;
                }
                if (target.active()) {
                    source.sendMessage(Messages.serverAlreadyEnabled());
                    return;
                }

                target.setActive(true);
                sendPermittedBroadcast(Messages.serverEnabledBroadcast(name, target.displayName()));
                return;
            }

            if(action.equalsIgnoreCase("disable")) {
                if(!source.hasPermission("servermanager.servers.disable")) {
                    source.sendMessage(Messages.noPermission());
                    return;
                }
                if (!target.active()) {
                    source.sendMessage(Messages.serverAlreadyDisabled());
                    return;
                }
                if (target.getFromProxy() == null) {
                    source.sendMessage(Messages.serverNotFound());
                    return;
                }

                target.empty(true);
                target.setActive(false);
                target.removeFromProxy();
                sendPermittedBroadcast(Messages.serverDisabledBroadcast(name, target.displayName()));
                return;
            }

            if(action.equalsIgnoreCase("info")) {
                target.sendInfo(source);
                return;
            }

            if(action.equalsIgnoreCase("kick")){
                if (!target.active() || target.getFromProxy() == null) {
                    source.sendMessage(Messages.serverNotActive());
                    return;
                }
                if(!source.hasPermission("servermanager.servers.kick")) {
                    source.sendMessage(Messages.noPermission());
                    return;
                }
                target.empty(false);
                sendPermittedBroadcast(Messages.serverEmptiedBroadcast(name, target.displayName()));
                return;
            }

            sendAvailableCommands(source);
            return;
        }

        if(args.length == 3) {
            Boolean setArg = validateBoolean(args[2]);

            if(setArg == null) {
                source.sendMessage(Messages.invalidArgs("INVALID_BOOL"));
                return;
            }

            if(action.equalsIgnoreCase("setlobby")) {
                if(target.lobby() == setArg) {
                    source.sendMessage(Messages.noActionCommited());
                    return;
                }
                if(!source.hasPermission("servermanager.servers.setlobby")) {
                    source.sendMessage(Messages.noPermission());
                    return;
                }
                target.setLobby(setArg);
                target.reloadInProxy();
                sendPermittedBroadcast(Messages.PREFIX.append(mm.deserialize("The server " + target.displayName() + " has been " + (setArg ? "flagged" : "unflagged") + " as a lobby by " + name)));
                return;
            }

            if(action.equalsIgnoreCase("setrestricted")) {
                if(setArg == target.restricted()) {
                    source.sendMessage(Messages.noActionCommited());
                    return;
                }
                if(!source.hasPermission("servermanager.servers.setrestricted")) {
                    source.sendMessage(Messages.noPermission());
                    return;
                }
                target.setRestricted(setArg);
                sendPermittedBroadcast(Messages.PREFIX.append(mm.deserialize("The server " + target.displayName() + " has been "+ (setArg ? "restricted" : "unrestricted") + " by " + name)));
                return;
            }
            sendAvailableCommands(source);
            return;
        }

        if (args.length < 6 || !action.equalsIgnoreCase("add")) {
            sendAvailableCommands(source);
            return;
        }

        if(!source.hasPermission("servermanager.servers.add")) {
            source.sendMessage(Messages.noPermission());
            return;
        }

        if(target != null) {
            source.sendMessage(Messages.PREFIX.append(mm.deserialize("<gray>This server is already in the database")));
            return;
        }

        String ip = args[2];
        int port;
        Boolean isLobby = validateBoolean(args[4]);
        Boolean isEnabled = validateBoolean(args[5]);
        Boolean isRestricted = validateBoolean(args[6]);

        try {
            port = Integer.parseInt(args[3]);
        } catch (NumberFormatException ex) {
            source.sendMessage(Messages.invalidArgs("NAN_PORT"));
            return;
        }

        if(port < 1 || port > 65535) {
            source.sendMessage(Messages.invalidArgs("PORT_OUT_OF_RANGE"));
            return;
        }

        if(isLobby == null || isEnabled == null || isRestricted == null) {
            source.sendMessage(Messages.invalidArgs("INVALID_BOOL"));
            return;
        }

        StringBuilder displayname = new StringBuilder();
        for (int i = 7; i < args.length; i++) {
            displayname.append(args[i]).append(" ");
        }
        String trimmedDisplayName = displayname.toString().trim();
        BackendServerManager.createServer(serverName, ip, port, trimmedDisplayName, isLobby, isEnabled, isRestricted, false);
        sendPermittedBroadcast(Messages.serverAddedBroadcast(name, trimmedDisplayName.isBlank() ? serverName : trimmedDisplayName));
    }

    private void printServerList(CommandSource source) {
        if(!source.hasPermission("servermanager.servers.list")) {
            source.sendMessage(Messages.noPermission());
            return;
        }
        source.sendMessage(Messages.serversListHeader());
        int i = 1;
        for (DatabaseRegisteredServer server : BackendServerManager.getAllServers()) {
            source.sendMessage(Messages.serverListInfo(
                    String.valueOf(i),
                    server.systemName(),
                    onlineOfflineString(server.online()),
                    server.isProxyManaged() ? "<yellow>Proxy-Managed</yellow>" : trueFalseString(server.active()),
                    trueFalseString(server.lobby()),
                    trueFalseString(server.restricted()),
                    server.displayName(),
                    server.address(),
                    String.valueOf(server.port())
            ));
            i++;
        }
    }

    private void sendAvailableCommands(CommandSource source) {
        MiniMessage mm = MiniMessage.miniMessage();
        if(!source.hasPermission("servermanager.help")) {
            source.sendMessage(Messages.noPermission());
            return;
        }
        source.sendMessage(Messages.PREFIX.append(mm.deserialize("<gray>The following commands are available to you<dark_gray>: <yellow>[optional] <needed>")));
        source.sendMessage(mm.deserialize("<yellow>/servermanager <dark_gray>- <gray>Shows this help site"));
        source.sendMessage(mm.deserialize("<yellow>/servermanager add <servername> <ip> <port> <islobby> <isactive> <isrestricted> <displayname> <dark_gray>- <gray>Adds a server to your network"));
        source.sendMessage(mm.deserialize("<yellow>/servermanager delete <servername> <dark_gray>- <gray>Deletes a server from your network"));
        source.sendMessage(mm.deserialize("<yellow>/servermanager reload [servername] <dark_gray>- <gray>Reloads the data of a specific server or all servers in the network"));
        source.sendMessage(mm.deserialize("<yellow>/servermanager list <dark_gray>- <gray>Lists all servers in your network"));
        source.sendMessage(mm.deserialize("<yellow>/servermanager info <servername> <dark_gray>- <gray>Shows some information about a specific server"));
        source.sendMessage(mm.deserialize("<yellow>/servermanager enable <servername> <dark_gray>- <gray>Enables a server, so players are able to connect to it"));
        source.sendMessage(mm.deserialize("<yellow>/servermanager disable <servername> <dark_gray>- <gray>Disables a server, so players can't connect to it"));
        source.sendMessage(mm.deserialize("<yellow>/servermanager setlobby <name> <isLobby> <dark_gray>- <gray>You can add a server to the lobby group or remove is"));
        source.sendMessage(mm.deserialize("<yellow>/servermanager setrestricted <name> <isrestricted> <dark_gray>- <gray>You can restrict a server so only people with the permission servermanager.server.<servername> can join on it"));
        source.sendMessage(mm.deserialize("<yellow>/servermanager kick <servername> <dark_gray>- <gray>Kicks all players from the specific server to a random lobby"));
    }

    private String trueFalseString(Boolean bool) {
        return bool == null || bool ? "<green>True</green>" : "<red>False</red>";
    }
    private String onlineOfflineString(Boolean bool) {
        return bool ? "<green>Online</green>" : "<red>Offline</red>";
    }

//    public Iterable<String> onTabComplete(CommandSender sender, String[] args){
//        if ((args.length > 3) || (args.length == 0)){
//            return ImmutableSet.of();
//        }
//        Set matches = new HashSet();
//        String search;
//        if (args.length == 2){
//            search = args[0].toLowerCase();
//            for (String server : ProxyServer.getInstance().getServers().keySet()){
//                if (server.toLowerCase().startsWith(search)){
//                    matches.add(server);
//                }
//            }
//        }
//        return matches;
//    }

    // Get ready for the Tri-state boolean
    @Nullable
    private Boolean validateBoolean(String s) {
        if(s.equalsIgnoreCase("true") || s.equalsIgnoreCase("1") || s.equalsIgnoreCase("enabled")) {
            return true;
        }
        if(s.equalsIgnoreCase("false") || s.equalsIgnoreCase("0") || s.equalsIgnoreCase("disabled")) {
            return false;
        }
        return null;
    }

    private void sendPermittedBroadcast(Component component){
        ProxyServer proxyServer = ServerManager.getInstance().getProxyServer();
        proxyServer.getConsoleCommandSource().sendMessage(component);
        proxyServer.getAllPlayers().stream().filter(all -> all.hasPermission("servermanager.notify") && BackendServerManager.getNotificationStatus(all)).forEach(all -> all.sendMessage(component));
    }
}
