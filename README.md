# Velocity Server Manager

This is a fork of [BungeeServerManager](https://git.fetz-gr.ch/NoahFetz/bungeeservermanager) by Noah Fetz, edited to work on Velocity Proxies.<br>
This project follows the MIT License as per upstream, it has not been edited.

Check out the original Plugin on SpigotMC here: [BungeeServerManager \[BungeeCord\] \[MySQL\]](https://www.spigotmc.org/resources/bungeeservermanager-bungeecord-mysql.24837/)

## Features
 * Add or Remove Servers from your Velocity Proxy without restarting
 * Restriction who can join specific servers with permissions
 * Jump to any player's server you want
 * Servers are saved in a MySQL Database

## Permissions
These should be self-explanitory. Have fun configuring to your heart's desire.
 * `servermanager.goto`
 * `servermanager.help`
 * `servermanager.notify`
 * `servermanager.servers.add`
 * `servermanager.servers.delete`
 * `servermanager.servers.reload`
 * `servermanager.servers.list`
 * `servermanager.servers.kick`
 * `servermanager.servers.info`
 * `servermanager.servers.enable`
 * `servermanager.servers.disable`
 * `servermanager.servers.setlobby`
 * `servermanager.servers.setrestricted`

For restricted servers, you'll need the `servermanager.server.*` or `servermanager.ignorerestriction` permission.

## Commands
- /hub - Go to a Lobby Server ("lobby" in velocity.toml is counted)
- /goto - Go to a player's current server
- /notify - Enable/Disable VSM Notifications
- /whereami - Find which server you are currently on
- /servermanager - Shows help for ServerManager commands
- /servermanager add - Adds a server to your network
- /servermanager delete - Deletes a server from your network
- /servermanager reload - Reloads the data of a specific server or all servers in the network
- /servermanager list - Lists all servers in your network
- /servermanager info - Shows some information about a specific server
- /servermanager enable - Enables a server, so players are able to connect to it
- /servermanager disable - Disables a server, so players can't connect to it
- /servermanager setlobby <name> <isLobby> - You can add a server to the lobby group or remove it
- /servermanager setrestricted <name> <isrestricted> - You can restrict a server so only people with the permission servermanager.server. can join on it
- /servermanager kick - Kicks all players from the specific server to a random lobby

## TODO
- [x] Migrate to Gradle<br>
- [x] Initial port to Velocity<br>
- [ ] Change Messages to Support Minimessage Format<br>
- [ ] Implement Tab Completion for Commands<br>
- [ ] Release on Velocity Resource Site and Modrinth

## Contribution
I made this as a small side project to learn a tiny bit more about Programming for the Velocity Proxy. I will probably not look at PR Requests anytime soon. If you do feel up to the task, you can fork this repository instead.<br>
Just make sure to follow the license.<br>
Instead, if you'd like to support me financially, you can [Donate on my Ko-Fi Page](https://ko-fi.com/GunnableScum)!

## License

This fork is licensed under the upstream MIT License - see the LICENSE file here or [upstream](https://git.fetz-gr.ch/NoahFetz/bungeeservermanager/-/blob/master/LICENSE?ref_type=heads) for details
