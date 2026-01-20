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

public class ReloadServerCommand extends VSMCommand {

    public ReloadServerCommand(ServerManager plugin, CommandManager manager) {
        super(plugin, manager, "reloadserver", "rls");
    }

    @Override
    protected BrigadierCommand createBrigadierCommand(ProxyServer proxyServer) {
        LiteralCommandNode<CommandSource> enableServerNode = BrigadierCommand.literalArgumentBuilder("reloadserver")
            .requires(source -> source.hasPermission("servermanager.servers.reload"))
                .executes(context -> {
                    sendPermittedBroadcast(Messages.allServerReloadBroadcast(getResponsible(context)));
                    return Command.SINGLE_SUCCESS;
                })
            .then(BrigadierCommand.requiredArgumentBuilder("server", StringArgumentType.word())
                .suggests((ctx, builder) -> {
                    proxyServer.getAllServers().forEach(server -> builder.suggest(server.getServerInfo().getName()));
                    return builder.buildFuture();
                })
                .executes(context -> {
                    String server = StringArgumentType.getString(context, "server");
                    DatabaseRegisteredServer target = MySQL.getServer(server);
                    if(target == null) {
                        context.getSource().sendMessage(Messages.serverNotFound());
                        return Command.SINGLE_SUCCESS;
                    }
                    target.reloadInProxy();
                    sendPermittedBroadcast(Messages.serverReloadedBroadcast(getResponsible(context), target.displayName()));
                    return Command.SINGLE_SUCCESS;
                }))
            .build();

        return new BrigadierCommand(enableServerNode);
    }
}
