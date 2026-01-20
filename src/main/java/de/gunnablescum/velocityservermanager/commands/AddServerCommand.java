package de.gunnablescum.velocityservermanager.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import de.gunnablescum.velocityservermanager.ServerManager;
import de.gunnablescum.velocityservermanager.utils.BackendServerManager;
import de.gunnablescum.velocityservermanager.utils.Messages;
import de.gunnablescum.velocityservermanager.utils.MySQL;
import de.gunnablescum.velocityservermanager.utils.VSMCommand;

import javax.annotation.Nullable;

public class AddServerCommand extends VSMCommand {

    public AddServerCommand(ServerManager plugin, CommandManager manager) {
        super(plugin, manager, "addserver");
    }

    @Override
    protected BrigadierCommand createBrigadierCommand(ProxyServer proxyServer) {
        LiteralCommandNode<CommandSource> addServerNode = BrigadierCommand.literalArgumentBuilder("addserver")
        .requires(source -> source.hasPermission("servermanager.servers.add"))
        .then(BrigadierCommand.requiredArgumentBuilder("server", StringArgumentType.word())
        .then(BrigadierCommand.requiredArgumentBuilder("host", StringArgumentType.word())
                .suggests((ctx,builder) -> {
                    builder.suggest("127.0.0.1");
                    builder.suggest("localhost");
                    return builder.buildFuture();
                })
        .then(BrigadierCommand.requiredArgumentBuilder("port", IntegerArgumentType.integer(20000, 65535))
            .executes(context -> {
                addServer(context,
                    StringArgumentType.getString(context, "server"),
                    StringArgumentType.getString(context, "host"),
                    IntegerArgumentType.getInteger(context, "port"),
                    null,
                    false,
                    true,
                    false);
                return Command.SINGLE_SUCCESS;
            })
            .then(BrigadierCommand.requiredArgumentBuilder("active", BoolArgumentType.bool())
                .suggests((ctx, builder) -> BoolArgumentType.bool().listSuggestions(ctx, builder))
                .executes(context -> {
                    addServer(context,
                        StringArgumentType.getString(context, "server"),
                        StringArgumentType.getString(context, "host"),
                        IntegerArgumentType.getInteger(context, "port"),
                        null,
                        false,
                        BoolArgumentType.getBool(context, "active"),
                        false);
                    return Command.SINGLE_SUCCESS;
                })
                .then(BrigadierCommand.requiredArgumentBuilder("lobby", BoolArgumentType.bool())
                    .suggests((ctx, builder) -> BoolArgumentType.bool().listSuggestions(ctx, builder))
                    .executes(context -> {
                        addServer(context,
                            StringArgumentType.getString(context, "server"),
                            StringArgumentType.getString(context, "host"),
                            IntegerArgumentType.getInteger(context, "port"),
                            null,
                            BoolArgumentType.getBool(context, "lobby"),
                            BoolArgumentType.getBool(context, "active"),
                            false);
                        return Command.SINGLE_SUCCESS;
                    })
                    .then(BrigadierCommand.requiredArgumentBuilder("restricted", BoolArgumentType.bool())
                        .suggests((ctx, builder) -> BoolArgumentType.bool().listSuggestions(ctx, builder))
                        .executes(context -> {
                            addServer(context,
                                StringArgumentType.getString(context, "server"),
                                StringArgumentType.getString(context, "host"),
                                IntegerArgumentType.getInteger(context, "port"),
                                null,
                                BoolArgumentType.getBool(context, "lobby"),
                                BoolArgumentType.getBool(context, "active"),
                                BoolArgumentType.getBool(context, "restricted"));
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(BrigadierCommand.requiredArgumentBuilder("displayname", StringArgumentType.greedyString())
                            .executes(context -> {
                                addServer(context,
                                    StringArgumentType.getString(context, "server"),
                                    StringArgumentType.getString(context, "host"),
                                    IntegerArgumentType.getInteger(context, "port"),
                                    StringArgumentType.getString(context, "displayname"),
                                    BoolArgumentType.getBool(context, "lobby"),
                                    BoolArgumentType.getBool(context, "active"),
                                    BoolArgumentType.getBool(context, "restricted"));
                                return Command.SINGLE_SUCCESS;
                            })
        ))))))).build();

        return new BrigadierCommand(addServerNode);
    }

    private void addServer(CommandContext<CommandSource> context, String targetName, String host, int port, @Nullable String displayname, boolean isLobby, boolean isActive, boolean isRestricted) {
        if(MySQL.getServer(targetName) != null) {
            context.getSource().sendMessage(Messages.serverAlreadyExists());
            return;
        }
        sendPermittedBroadcast(Messages.serverAddedBroadcast(getResponsible(context), displayname == null || displayname.isBlank() ? targetName : displayname));
        BackendServerManager.createServer(targetName, host, port, displayname, isLobby, isActive, isRestricted, false);
    }
}
