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

public class DisableServerCommand extends VSMCommand {

    public DisableServerCommand(ServerManager plugin, CommandManager manager) {
        super(plugin, manager, "disableserver");
    }

    @Override
    protected BrigadierCommand createBrigadierCommand(ProxyServer proxyServer) {
        LiteralCommandNode<CommandSource> enableServerNode = BrigadierCommand.literalArgumentBuilder("disableserver")
            .requires(source -> source.hasPermission("servermanager.servers.disable"))
            .then(BrigadierCommand.requiredArgumentBuilder("server", StringArgumentType.word())
                .suggests((ctx, builder) -> {
                    proxyServer.getAllServers().forEach(server -> builder.suggest(server.getServerInfo().getName()));
                    return builder.buildFuture();
                })
                .executes(context -> {
                    String server = StringArgumentType.getString(context, "server");
                    DatabaseRegisteredServer target = MySQL.getServer(server);
                    if (checkIfServerProxyManagedOrNull(context.getSource(), target)) return Command.SINGLE_SUCCESS;
                    //noinspection DataFlowIssue <- This is checked above, probably an IntelliJ bug.
                    if (!target.active()) {
                        context.getSource().sendMessage(Messages.noActionCommited());
                        return Command.SINGLE_SUCCESS;
                    }
                    target.empty(true);
                    target.setActive(false);
                    sendPermittedBroadcast(Messages.serverDisabledBroadcast(getResponsible(context), server));
                    return Command.SINGLE_SUCCESS;
                })).build();

        return new BrigadierCommand(enableServerNode);
    }
}
