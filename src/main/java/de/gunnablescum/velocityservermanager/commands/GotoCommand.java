package de.gunnablescum.velocityservermanager.commands;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.gunnablescum.velocityservermanager.ServerManager;
import de.gunnablescum.velocityservermanager.utils.Messages;

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
            source.sendMessage(Messages.onlyIngameCommand());
            return;
        }

        if (!p.hasPermission("servermanager.goto")) {
            p.sendMessage(Messages.noPermission());
            return;
        }

        if(args.length != 1){
            p.sendMessage(Messages.gotoDescription());
            return;
        }

        String pname = args[0];
        Optional<Player> p2 = ServerManager.getInstance().getProxyServer().getPlayer(args[0]);
        if(p2.isEmpty()){
            p.sendMessage(Messages.gotoPlayerOffline(pname));
            return;
        }

        RegisteredServer target = p2.get().getCurrentServer().get().getServer();
        if(p.getCurrentServer().get().getServer() != target){
            p.createConnectionRequest(target).connect().thenAccept(result -> {
                if(result.isSuccessful()) {
                    p.sendMessage(Messages.gotoConnected(pname));
                } else {
                    // TODO: Handle connection failure
                }
            });
            return;
        }
        p.sendMessage(Messages.gotoAlreadyOnServer(pname));
    }
}
