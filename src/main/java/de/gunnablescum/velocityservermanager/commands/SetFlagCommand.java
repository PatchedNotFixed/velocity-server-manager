package de.gunnablescum.velocityservermanager.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
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
import net.kyori.adventure.text.minimessage.MiniMessage;

public class SetFlagCommand extends VSMCommand {

    public SetFlagCommand(ServerManager plugin, CommandManager manager) {
        super(plugin, manager, "setflag", "setserverflag");
    }

    @Override
    protected BrigadierCommand createBrigadierCommand(ProxyServer proxyServer) {
        LiteralCommandNode<CommandSource> enableServerNode = BrigadierCommand.literalArgumentBuilder("setflag")
            .requires(source -> source.hasPermission("servermanager.servers.setflag"))
            .then(BrigadierCommand.requiredArgumentBuilder("server", StringArgumentType.word())
            .then(BrigadierCommand.requiredArgumentBuilder("flag", StringArgumentType.word())
            .then(BrigadierCommand.requiredArgumentBuilder("state", BoolArgumentType.bool())
                .suggests((ctx, builder) -> BoolArgumentType.bool().listSuggestions(ctx, builder))
                .executes(context -> {
                    MiniMessage mm = MiniMessage.miniMessage();
                    String server = StringArgumentType.getString(context, "server");
                    DatabaseRegisteredServer target = MySQL.getServer(server);
                    if (target == null) {
                        context.getSource().sendMessage(Messages.serverNotFound());
                        return Command.SINGLE_SUCCESS;
                    }
                    String flag = StringArgumentType.getString(context, "flag").toLowerCase();
                    boolean state = BoolArgumentType.getBool(context, "state");
                    switch (flag) {
                        case "lobby" -> {
                            if (target.lobby() == state) {
                                context.getSource().sendMessage(Messages.noActionCommited());
                                return Command.SINGLE_SUCCESS;
                            }
                            target.setLobby(state);
                            sendPermittedBroadcast(Messages.PREFIX.append(mm.deserialize("The server " + target.displayName() + " has been " + (state ? "" : "un") + "flagged as a lobby by " + getResponsible(context))));
                        }
                        case "restricted" -> {
                            if (target.restricted() == state) {
                                context.getSource().sendMessage(Messages.noActionCommited());
                                return Command.SINGLE_SUCCESS;
                            }
                            target.setRestricted(state);
                            sendPermittedBroadcast(Messages.PREFIX.append(mm.deserialize("The server " + target.displayName() + " has been " + (state ? "" : "un") + "restricted by " + getResponsible(context))));
                        }
                        default -> context.getSource().sendMessage(Messages.invalidArgs("NO_SUCH_FLAG"));
                    }
                    return Command.SINGLE_SUCCESS;
                })))).build();

        return new BrigadierCommand(enableServerNode);
    }
}
