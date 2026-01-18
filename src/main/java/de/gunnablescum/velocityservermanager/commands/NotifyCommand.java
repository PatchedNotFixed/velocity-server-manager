package de.gunnablescum.velocityservermanager.commands;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import de.gunnablescum.velocityservermanager.ServerManager;
import de.gunnablescum.velocityservermanager.utils.BackendServerManager;
import de.gunnablescum.velocityservermanager.utils.Messages;
import net.kyori.adventure.text.Component;

/**
 * Created by Noah Fetz on 15.11.2016.
 * Contributors: GunnableScum
 */
public class NotifyCommand implements SimpleCommand {

    public NotifyCommand(ServerManager plugin, CommandManager manager) {
        manager.register(manager.metaBuilder("notify").plugin(plugin).build(), this);
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();

        if (!(source instanceof Player)) {
            source.sendMessage(Component.text(Messages.PREFIX + Messages.ONLY_INGAME_COMMAND));
            return;
        }
        if (!source.hasPermission("servermanager.notify")) {
            source.sendMessage(Component.text(Messages.PREFIX + Messages.NO_PERMISSION));
            return;
        }

        boolean toggle = !BackendServerManager.getNotificationStatus((Player) source);

        BackendServerManager.setNotificationStatus((Player) source, toggle);
        source.sendMessage(Component.text(Messages.PREFIX + (toggle ? "§7You're now going to receive notifications" : "§7You won't receive any notifications from now on")));
    }

}
