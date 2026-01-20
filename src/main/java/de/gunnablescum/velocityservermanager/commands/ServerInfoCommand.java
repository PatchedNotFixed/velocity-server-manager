package de.gunnablescum.velocityservermanager.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import de.gunnablescum.velocityservermanager.ServerManager;
import de.gunnablescum.velocityservermanager.utils.DatabaseRegisteredServer;
import de.gunnablescum.velocityservermanager.utils.Messages;
import de.gunnablescum.velocityservermanager.utils.MySQL;
import de.gunnablescum.velocityservermanager.utils.VSMCommand;

public class ServerInfoCommand extends VSMCommand {

    public ServerInfoCommand(ServerManager plugin, CommandManager manager) {
        super(plugin, manager, "serverinfo", "si");
    }

    @Override
    protected BrigadierCommand createBrigadierCommand(final ProxyServer proxy) {
        LiteralCommandNode<CommandSource> serverInfoNode = BrigadierCommand.literalArgumentBuilder("serverinfo")
            .requires(source -> source.hasPermission("servermanager.servers.info"))
            .then(BrigadierCommand.requiredArgumentBuilder("server", StringArgumentType.word())
                .suggests((ctx, builder) -> {
                    proxy.getAllServers().forEach(server -> builder.suggest(server.getServerInfo().getName()));
                    return builder.buildFuture();
                })
                .executes(context -> {
                    String serverName;
                    try {
                        serverName = StringArgumentType.getString(context, "server");
                    } catch (IllegalArgumentException e) {
                        return 0;
                    }
                    DatabaseRegisteredServer server = MySQL.getServer(serverName);
                    if (server == null) {
                        context.getSource().sendMessage(Messages.serverNotFound());
                        return Command.SINGLE_SUCCESS;
                    }
                    server.sendInfo(context.getSource());
                    return Command.SINGLE_SUCCESS;
            })).build();

        return new BrigadierCommand(serverInfoNode);
    }

}
