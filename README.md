# Velocity Server Manager

This project is a fork of [BungeeServerManager](https://git.fetz-gr.ch/NoahFetz/bungeeservermanager) by Noah Fetz, edited to work on Velocity Proxies.<br>
This project follows the MIT License as per upstream, it has not been edited.

Check out the original Plugin on SpigotMC here: [BungeeServerManager \[BungeeCord\] \[MySQL\]](https://www.spigotmc.org/resources/bungeeservermanager-bungeecord-mysql.24837/)

## TODO
- [x] Migrate to Gradle
- [x] Initial port to Velocity
- [x] Change Messages to Support Minimessage Format
- [x] Make every Message be configurable
- [x] Implement Tab Completion for Commands
- [ ] Rework setlobby and setrestricted to be "flags" (Like, flags, you know what I mean.)
- [ ] Make a single, unified command for everything
- [ ] Release on Velocity Resource Site and ModrinthFIX

## Features
 * Add or Remove Servers from your Velocity Proxy without restarting
 * Restriction who can join specific servers with permissions
 * Jump to any player's server you want
 * Servers are saved in a MySQL Database

## Permissions
Have fun configuring to your heart's desire.
 * `servermanager.goto` - Be able to use /goto and /jumpto.
 * `servermanager.help` // TODO
 * `servermanager.notify` - Receive notifications when somebody manages servers.
 * `servermanager.servers.add` - Add servers to your network.
 * `servermanager.servers.delete` - Delete servers from your network.
 * `servermanager.servers.reload` - Reload server data from the database and re-add them to the proxy.
 * `servermanager.servers.list` - List all servers in your network.
 * `servermanager.servers.kick` - Kick all players from a specific server to a random lobby.
 * `servermanager.ignorekick` - Be exempt from being kicked when a server is cleared.
 * `servermanager.servers.info` - View information about a specific server.
 * `servermanager.servers.setlobby` // TO BE DELETED
 * `servermanager.servers.setrestricted` // TO BE DELETED
 * `servermanager.servers.flags` - Manage Server flags. Check [here](#flags) for more info. // TODO

For restricted servers, you'll need the `servermanager.server.*` or `servermanager.ignorerestriction` permission.

## Commands
- `[hub, lobby]` - Go to a Lobby Server ("lobby" in velocity.toml is counted)
- `[goto, jumpto]` - Go to a player's current server
- `[whereami, wai]` - Find which server you are currently on
- `[servermanager help]` - Shows help for ServerManager commands // TO BE DELETED
- `[addserver]` - Adds a server to your network
- `[delserver]` - Deletes a server from your network
- `[reloadserver]` - Reloads the data of a specific server or all servers in the network
- `[servers, listservers]` - Lists all servers in your network
- `[serverinfo, si]` - Shows some information about a specific server
- `[enableserver]` - Enables a server, so players are able to connect to it
- `[disableserver]` - Disables a server, so players can't connect to it
- `[setflag, setserverflag]` - You can add a server to the lobby group or remove it
- `[clearserver, kickserver]` - Kicks all players from the specific server to a random lobby

## Flags
TODO

## Contribution
I made this as a small side project to learn a tiny bit more about Programming for the Velocity Proxy. I will probably not look at PR Requests anytime soon. If you do feel up to the task, you can fork this repository instead.<br>
Just make sure to follow the license.<br>
Instead, if you'd like to support me financially, you can [Donate on my Ko-Fi Page](https://ko-fi.com/GunnableScum)!

## License

This fork is licensed under the upstream MIT License - see the LICENSE file here or [upstream](https://git.fetz-gr.ch/NoahFetz/bungeeservermanager/-/blob/master/LICENSE?ref_type=heads) for details
