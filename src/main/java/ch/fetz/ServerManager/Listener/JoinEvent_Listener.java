package ch.fetz.ServerManager.Listener;

import ch.fetz.ServerManager.ServerManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by Noah Fetz on 21.05.2016.
 */
public class JoinEvent_Listener implements Listener {
    private final ServerManager plugin;

    public JoinEvent_Listener(ServerManager serverManager) {
        this.plugin = serverManager;
        plugin.getProxy().getPluginManager().registerListener(serverManager, this);
    }

    @EventHandler
    public void onJoin(PostLoginEvent e){
        try{
            if(plugin.setHeader || plugin.setFooter) {
                String header = plugin.headerText.replace("%player%", e.getPlayer().getName()).replace("%server%", plugin.getManager().getDisplayName(e.getPlayer().getServer().getInfo().getName()));
                String footer = plugin.footerText.replace("%player%", e.getPlayer().getName()).replace("%server%", plugin.getManager().getDisplayName(e.getPlayer().getServer().getInfo().getName()));
                header = ChatColor.translateAlternateColorCodes('&', header);
                footer = ChatColor.translateAlternateColorCodes('&', footer);
                if (!plugin.setHeader) {
                    header = " ";
                }
                if (!plugin.setFooter) {
                    footer = " ";
                }
                e.getPlayer().setTabHeader(new TextComponent(header), new TextComponent(footer));
            }
        }catch (Exception ex){
        }

        plugin.getManager().createPlayer(e.getPlayer());
        plugin.getManager().checkPlayerName(e.getPlayer());

        if(e.getPlayer().hasPermission("servermanager.receiveinfo") && plugin.getManager().getNotificationStatus(e.getPlayer())){
            plugin.receiver.add(e.getPlayer());
        }
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent e) {
        plugin.receiver.remove(e.getPlayer());
    }
}
