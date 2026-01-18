package de.gunnablescum.velocityservermanager.commands;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import de.gunnablescum.velocityservermanager.ServerManager;
import de.gunnablescum.velocityservermanager.utils.Messages;

import java.security.SecureRandom;

/**
 * Created by Noah Fetz on 09.06.2016.
 * Contributors: GunnableScum
 */
public class HubCommand implements SimpleCommand {

    public HubCommand(ServerManager plugin, CommandManager manager) {
        manager.register(manager.metaBuilder("hub").aliases("lobby").plugin(plugin).build(), this);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        if (!(source instanceof Player p)) {
            source.sendMessage(Messages.onlyIngameCommand());
            return;
        }

        if(!ServerManager.lobbies.contains(p.getCurrentServer().get().getServer())){
            p.createConnectionRequest(ServerManager.lobbies.get(new SecureRandom().nextInt(ServerManager.lobbies.size()))).connect();
            return;
        }

        p.sendMessage(Messages.alreadyOnLobby());
    }
}
