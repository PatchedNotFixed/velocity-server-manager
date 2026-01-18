package de.gunnablescum.velocityservermanager.utils;

import de.gunnablescum.velocityservermanager.ServerManager;

import java.io.IOException;

/**
 * Created by Noah Fetz on 03.08.2016.
 * Contributors: GunnableScum
 */
public class Messages {
    public static String PREFIX;

    public static String NO_PERMISSION;
    public static String SERVERS_LIST_HEADER;
    public static String ALL_SERVER_RELOAD_BROADCAST;
    public static String PREVIOUS_SERVER_DELETED_INFO;
    public static String SERVER_DELETED_BROADCAST;
    public static String SERVER_NOT_FOUND;
    public static String SERVER_LIST_INFO;
    public static String SERVER_RELOADED_BROADCAST;
    public static String SERVER_ENABLED_BROADCAST;
    public static String SERVER_DISABLED_BROADCAST;
    public static String SERVER_ALREADY_ENABLED;
    public static String SERVER_ALREADY_DISABLED;
    public static String SERVER_EMPTIED_BROADCAST;
    public static String SERVER_NOT_ACTIVE;
    public static String PREVIOUS_SERVER_EMPTIED;
    public static String GOTO_DESCRIPTION;
    public static String GOTO_PLAYER_NOT_ONLINE;
    public static String GOTO_CONNECTED;
    public static String GOTO_ALREADY_ON_SERVER;
    public static String ONLY_INGAME_COMMAND;
    public static String LOBBY_ALREADY_ON_LOBBY;
    public static String WHEREAMI_SERVER_INFO;
    public static String NO_ACTION_COMMITED;
    public static String INVALID_ARGS;
    public static String SERVER_PROXY_MANAGED;
    public static String SERVER_ADDED_BROADCAST;

    public static void loadMessages(){
        Config config;
        try {
            config = new Config(ServerManager.getInstance().getDataDirectory(), "messages.yml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        PREFIX = config.getString("Messages.PREFIX", "");
        NO_PERMISSION = config.getString("Messages.NO_PERMISSION", "");
        SERVERS_LIST_HEADER = config.getString("Messages.SERVERS_LIST_HEADER", "");
        ALL_SERVER_RELOAD_BROADCAST = config.getString("Messages.ALL_SERVER_RELOAD_BROADCAST", "");
        PREVIOUS_SERVER_DELETED_INFO = config.getString("Messages.PREVIOUS_SERVER_DELETED_INFO", "");
        SERVER_DELETED_BROADCAST = config.getString("Messages.SERVER_DELETED_BROADCAST", "");
        SERVER_NOT_FOUND = config.getString("Messages.SERVER_NOT_FOUND", "");
        SERVER_LIST_INFO = config.getString("Messages.SERVER_LIST_INFO", "");
        SERVER_RELOADED_BROADCAST = config.getString("Messages.SERVER_RELOADED_BROADCAST", "");
        SERVER_ENABLED_BROADCAST = config.getString("Messages.SERVER_ENABLED_BROADCAST", "");
        SERVER_ALREADY_ENABLED = config.getString("Messages.SERVER_ALREADY_ENABLED", "");
        SERVER_DISABLED_BROADCAST = config.getString("Messages.SERVER_DISABLED_BROADCAST", "");
        SERVER_ALREADY_DISABLED = config.getString("Messages.SERVER_ALREADY_DISABLED", "");
        SERVER_EMPTIED_BROADCAST = config.getString("Messages.SERVER_EMPTIED_BROADCAST", "");
        SERVER_ADDED_BROADCAST = config.getString("Messages.SERVER_ADDED_BROADCAST", "");
        SERVER_NOT_ACTIVE = config.getString("Messages.SERVER_NOT_ACTIVE", "");
        PREVIOUS_SERVER_EMPTIED = config.getString("Messages.PREVIOUS_SERVER_EMPTIED", "");
        GOTO_DESCRIPTION = config.getString("Messages.GOTO_DESCRIPTION", "");
        GOTO_PLAYER_NOT_ONLINE = config.getString("Messages.GOTO_PLAYER_NOT_ONLINE", "");
        GOTO_CONNECTED = config.getString("Messages.GOTO_CONNECTED", "");
        GOTO_ALREADY_ON_SERVER = config.getString("Messages.GOTO_ALREADY_ON_SERVER", "");
        ONLY_INGAME_COMMAND = config.getString("Messages.ONLY_INGAME_COMMAND", "");
        LOBBY_ALREADY_ON_LOBBY = config.getString("Messages.LOBBY_ALREADY_ON_LOBBY", "");
        WHEREAMI_SERVER_INFO = config.getString("Messages.WHEREAMI_SERVER_INFO", "");
        NO_ACTION_COMMITED = config.getString("Messages.NO_ACTION_COMMITED", "");
        INVALID_ARGS = config.getString("Messages.INVALID_ARGS", "");
        SERVER_PROXY_MANAGED = config.getString("Messages.SERVER_PROXY_MANAGED", "");
    }
}
