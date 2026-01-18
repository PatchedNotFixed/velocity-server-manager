package de.gunnablescum.velocityservermanager.commands;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import de.gunnablescum.velocityservermanager.ServerManager;
import de.gunnablescum.velocityservermanager.utils.DatabaseRegisteredServer;
import de.gunnablescum.velocityservermanager.utils.Messages;
import de.gunnablescum.velocityservermanager.utils.MySQL;

/**
 * Created by Noah Fetz on 17.06.2016.
 * Contributors: GunnableScum
 */
public class WhereAmICommand implements SimpleCommand {


    public WhereAmICommand(ServerManager plugin, CommandManager manager) {
        manager.register(manager.metaBuilder("whereami").aliases("serverinfo").plugin(plugin).build(), this);
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        if (!(source instanceof Player p)) {
            source.sendMessage(Messages.onlyIngameCommand());
            return;
        }

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        String fallbackName = p.getCurrentServer().get().getServerInfo().getName();
        DatabaseRegisteredServer server = MySQL.getServer(fallbackName);

        p.sendMessage(Messages.whereAmIServerInfo(server != null ? server.displayName() : fallbackName));
    }
}
