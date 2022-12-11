package ch.fetz.ServerManager.Listener;

import ch.fetz.ServerManager.ServerManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by Noah Fetz on 21.05.2016.
 */
public class ServerSwitch_Listener implements Listener {
    private final ServerManager plugin;

    public ServerSwitch_Listener(ServerManager serverManager) {
        this.plugin = serverManager;
        plugin.getProxy().getPluginManager().registerListener(serverManager, this);
    }

    @EventHandler
    public void onSwitch(ServerSwitchEvent e){
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
    }
}
