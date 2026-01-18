package de.gunnablescum.velocityservermanager.commands;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import de.gunnablescum.velocityservermanager.ServerManager;
import de.gunnablescum.velocityservermanager.utils.BackendServerManager;
import de.gunnablescum.velocityservermanager.utils.DatabaseRegisteredServer;
import de.gunnablescum.velocityservermanager.utils.Messages;
import de.gunnablescum.velocityservermanager.utils.MySQL;
import net.kyori.adventure.text.Component;

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
                    source.sendMessage(Component.text(Messages.PREFIX + Messages.NO_PERMISSION));
                    return;
                }
                BackendServerManager.clearAllServers();
                BackendServerManager.addAllServers();
                sendPermittedBroadcast(Messages.PREFIX + String.format(Messages.ALL_SERVER_RELOAD_BROADCAST, name));
                return;
            }

            sendAvailableCommands(source);
            return;
        }

        String serverName = args[1];
        DatabaseRegisteredServer target = MySQL.getServer(serverName);
        if(!action.equalsIgnoreCase("add")) {
            if(target == null) {
                source.sendMessage(Component.text(Messages.PREFIX + Messages.SERVER_NOT_FOUND));
                return;
            }
            if(target.isProxyManaged()) {
                source.sendMessage(Component.text(Messages.PREFIX + Messages.SERVER_PROXY_MANAGED));
                return;
            }
        }

        if(args.length == 2) {
            if(action.equalsIgnoreCase("delete")){
                if(!source.hasPermission("servermanager.servers.delete")) {
                    source.sendMessage(Component.text(Messages.PREFIX + Messages.NO_PERMISSION));
                    return;
                }
                target.empty(true);
                target.deleteFromDatabase();
                target.removeFromProxy();

                sendPermittedBroadcast(Messages.PREFIX + String.format(Messages.SERVER_DELETED_BROADCAST,
                        target.displayName(),
                        name
                ));
                return;
            }

            if(action.equalsIgnoreCase("reload")) {
                if(!source.hasPermission("servermanager.servers.reload")) {
                    source.sendMessage(Component.text(Messages.PREFIX + Messages.NO_PERMISSION));
                    return;
                }
                target.reloadInProxy();
                sendPermittedBroadcast(Messages.PREFIX + String.format(Messages.SERVER_RELOADED_BROADCAST,
                        target.displayName(),
                        name)
                );
                return;
            }

            if(action.equalsIgnoreCase("enable")) {
                if(!source.hasPermission("servermanager.servers.enable")) {
                    source.sendMessage(Component.text(Messages.PREFIX + Messages.NO_PERMISSION));
                    return;
                }
                if (target.active()) {
                    source.sendMessage(Component.text(Messages.PREFIX + Messages.SERVER_ALREADY_ENABLED));
                    return;
                }

                target.setActive(true);
                sendPermittedBroadcast(Messages.PREFIX + Messages.SERVER_ENABLED_BROADCAST);
                return;
            }

            if(action.equalsIgnoreCase("disable")) {
                if(!source.hasPermission("servermanager.servers.disable")) {
                    source.sendMessage(Component.text(Messages.PREFIX + Messages.NO_PERMISSION));
                    return;
                }
                if (target.getFromProxy() == null) {
                    source.sendMessage(Component.text(Messages.PREFIX + Messages.SERVER_NOT_FOUND));
                    return;
                }
                if (!target.active()) {
                    source.sendMessage(Component.text(Messages.PREFIX + Messages.SERVER_ALREADY_DISABLED));
                    return;
                }

                target.setActive(false);
                target.empty(true);
                target.removeFromProxy();
                sendPermittedBroadcast(Messages.PREFIX + Messages.SERVER_DISABLED_BROADCAST);
                return;
            }

            if(action.equalsIgnoreCase("info")) {
                target.sendInfo(source);
                return;
            }

            if(action.equalsIgnoreCase("kick")){
                if (!target.active() || target.getFromProxy() == null) {
                    source.sendMessage(Component.text(Messages.PREFIX + Messages.SERVER_NOT_ACTIVE));
                    return;
                }
                if(!source.hasPermission("servermanager.servers.kick")) {
                    source.sendMessage(Component.text(Messages.PREFIX + Messages.NO_PERMISSION));
                    return;
                }
                target.empty(false);
                sendPermittedBroadcast(Messages.PREFIX + String.format(Messages.SERVER_EMPTIED_BROADCAST,
                        target.displayName(),
                        name
                ));
                return;
            }

            sendAvailableCommands(source);
            return;
        }

        if(args.length == 3) {
            Boolean setArg = validateBoolean(args[2]);

            if(setArg == null) {
                source.sendMessage(Component.text(Messages.PREFIX + String.format(Messages.INVALID_ARGS, "INVALID_BOOL")));
                return;
            }

            if(action.equalsIgnoreCase("setlobby")) {
                if(target.lobby() == setArg) {
                    source.sendMessage(Component.text(Messages.PREFIX + Messages.NO_ACTION_COMMITED));
                    return;
                }
                if(!source.hasPermission("servermanager.servers.setlobby")) {
                    source.sendMessage(Component.text(Messages.PREFIX + Messages.NO_PERMISSION));
                    return;
                }
                target.setLobby(setArg);
                target.reloadInProxy();
                sendPermittedBroadcast(Messages.PREFIX + "§7The server " + target.displayName() + " §7has been " + (setArg ? "flagged" : "unflagged") + " as a lobby by " + name);
                return;
            }

            if(action.equalsIgnoreCase("setrestricted")) {
                if(setArg == target.restricted()) {
                    source.sendMessage(Component.text(Messages.PREFIX + Messages.NO_ACTION_COMMITED));
                    return;
                }
                if(!source.hasPermission("servermanager.servers.setrestricted")) {
                    source.sendMessage(Component.text(Messages.PREFIX + Messages.NO_PERMISSION));
                    return;
                }
                target.setRestricted(setArg);
                sendPermittedBroadcast(Messages.PREFIX + "§7The server " + target.displayName() + " §7has been "+ (setArg ? "restricted" : "unrestricted") + " by " + name);
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
            source.sendMessage(Component.text(Messages.PREFIX + Messages.NO_PERMISSION));
            return;
        }

        if(target != null) {
            source.sendMessage(Component.text(Messages.PREFIX + "§7This server is already in the database"));
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
            source.sendMessage(Component.text(Messages.PREFIX + String.format(Messages.INVALID_ARGS, "NAN_PORT")));
            return;
        }

        if(port < 1 || port > 65535) {
            source.sendMessage(Component.text(Messages.PREFIX + String.format(Messages.INVALID_ARGS, "PORT_OUT_OF_RANGE")));
            return;
        }

        if(isLobby == null || isEnabled == null || isRestricted == null) {
            source.sendMessage(Component.text(Messages.PREFIX + String.format(Messages.INVALID_ARGS, "INVALID_BOOL")));
            return;
        }

        StringBuilder displayname = new StringBuilder();
        for (int i = 7; i < args.length; i++) {
            displayname.append(args[i]).append(" ");
        }
        String trimmedDisplayName = displayname.toString().trim();
        BackendServerManager.createServer(serverName, ip, port, trimmedDisplayName, isLobby, isEnabled, isRestricted, false);
        sendPermittedBroadcast(Messages.PREFIX + String.format(Messages.SERVER_ADDED_BROADCAST,
                trimmedDisplayName,
                name
        ));
    }

    private void printServerList(CommandSource source) {
        if(!source.hasPermission("servermanager.servers.list")) {
            source.sendMessage(Component.text(Messages.PREFIX + Messages.NO_PERMISSION));
            return;
        }
        source.sendMessage(Component.text(Messages.PREFIX + Messages.SERVERS_LIST_HEADER));
        int i = 1;
        for (DatabaseRegisteredServer server : BackendServerManager.getAllServers()) {
            source.sendMessage(Component.text(String.format(Messages.SERVER_LIST_INFO,
                    i,
                    server.systemName(),
                    onlineOfflineString(server.online()),
                    server.isProxyManaged() ? "§eProxy-Managed" : trueFalseString(server.active()),
                    trueFalseString(server.lobby()),
                    server.displayName(),
                    trueFalseString(server.restricted()),
                    server.address(),
                    server.port()
            )));
            i++;
        }
    }

    private void sendAvailableCommands(CommandSource source) {
        if(!source.hasPermission("servermanager.help")) {
            source.sendMessage(Component.text(Messages.PREFIX + Messages.NO_PERMISSION));
            return;
        }
        source.sendMessage(Component.text(Messages.PREFIX + "§7The following commands are available to you§8: §e[optional] <needed>"));
        source.sendMessage(Component.text("§e/servermanager §8- §7Shows this help site"));
        source.sendMessage(Component.text("§e/servermanager add <servername> <ip> <port> <islobby> <isactive> <isrestricted> <displayname> §8- §7Adds a server to your network")); //Done
        source.sendMessage(Component.text("§e/servermanager delete <servername> §8- §7Deletes a server from your network")); //Done
        source.sendMessage(Component.text("§e/servermanager reload [servername] §8- §7Reloads the data of a specific server or all servers in the network")); //Done
        source.sendMessage(Component.text("§e/servermanager list §8- §7Lists all servers in your network")); //Done
        source.sendMessage(Component.text("§e/servermanager info <servername> §8- §7Shows some information about a specific server")); //Done
        source.sendMessage(Component.text("§e/servermanager enable <servername> §8- §7Enables a server, so players are able to connect to it")); //Done
        source.sendMessage(Component.text("§e/servermanager disable <servername> §8- §7Disables a server, so players can't connect to it")); //Done
        source.sendMessage(Component.text("§e/servermanager setlobby <name> <isLobby> §8- §7You can add a server to the lobby group or remove is")); //Done
        source.sendMessage(Component.text("§e/servermanager setrestricted <name> <isrestricted> §8- §7You can restrict a server so only people with the permission servermanager.server.<servername> can join on it")); //Done
        source.sendMessage(Component.text("§e/servermanager kick <servername> §8- §7Kicks all players from the specific server to a random lobby")); //Done
    }

    private String trueFalseString(Boolean bool){
        return bool ? "§aTrue" : "§cFalse";
    }
    private String onlineOfflineString(Boolean bool){
        return bool ? "§aOnline" : "§cOffline";
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

    private void sendPermittedBroadcast(String message){
        ServerManager instance = ServerManager.getInstance();
        instance.getLogger().info(message);
        instance.getProxyServer().getAllPlayers().stream().filter(all -> all.hasPermission("servermanager.notify") && BackendServerManager.getNotificationStatus(all)).forEach(all -> all.sendMessage(Component.text(message)));
    }
}
