package de.gunnablescum.velocityservermanager.commands;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import de.gunnablescum.velocityservermanager.ServerManager;
import de.gunnablescum.velocityservermanager.utils.Messages;

/**
 * Created by Noah Fetz on 17.06.2016.
 * Contributors: GunnableScum
 */
public class WhereAmICommand implements SimpleCommand {


    public WhereAmICommand(ServerManager plugin, CommandManager manager) {
        manager.register(manager.metaBuilder("whereami").aliases("wai").plugin(plugin).build(), this);
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        if (!(source instanceof Player p)) {
            source.sendMessage(Messages.onlyIngameCommand());
            return;
        }

        //noinspection OptionalGetWithoutIsPresent <- The client cannot send commands while not being connected to a server.
        p.sendMessage(Messages.whereAmIServerInfo(p.getCurrentServer().get().getServerInfo().getName()));
    }
}
