package de.gunnablescum.velocityservermanager.commands;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.gunnablescum.velocityservermanager.ServerManager;
import de.gunnablescum.velocityservermanager.utils.Messages;
import net.kyori.adventure.text.Component;

import java.util.Optional;

/**
 * Created by Noah Fetz on 03.08.2016.
 * Contributors: GunnableScum
 */
public class GotoCommand implements SimpleCommand {

    public GotoCommand(ServerManager plugin, CommandManager manager) {
        manager.register(manager.metaBuilder("goto").aliases("jumpto").plugin(plugin).build(), this);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        if (!(source instanceof Player p)) {
            source.sendMessage(Component.text(Messages.PREFIX + Messages.ONLY_INGAME_COMMAND));
            return;
        }

        if (!p.hasPermission("servermanager.goto")) {
            p.sendMessage(Component.text(Messages.PREFIX + Messages.NO_PERMISSION));
            return;
        }

        if(args.length != 1){
            p.sendMessage(Component.text(Messages.PREFIX + Messages.GOTO_DESCRIPTION));
            return;
        }

        String pname = args[0];
        Optional<Player> p2 = ServerManager.getInstance().getProxyServer().getPlayer(args[0]);
        if(p2.isEmpty()){
            p.sendMessage(Component.text(Messages.PREFIX + Messages.GOTO_PLAYER_NOT_ONLINE.replace("%PLAYER%", pname)));
            return;
        }

        RegisteredServer target = p2.get().getCurrentServer().get().getServer();
        if(p.getCurrentServer().get().getServer() != target){
            p.createConnectionRequest(target).connect().thenAccept(result -> {
                if(result.isSuccessful()) {
                    p.sendMessage(Component.text(Messages.PREFIX + Messages.GOTO_CONNECTED.replace("%PLAYER%", pname)));
                } else {
                    // TODO: Handle connection failure
                }
            });
            return;
        }
        p.sendMessage(Component.text(Messages.PREFIX + Messages.GOTO_ALREADY_ON_SERVER.replace("%PLAYER%", pname)));
    }
}
