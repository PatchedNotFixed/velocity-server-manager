package ch.fetz.ServerManager.Utils;

import ch.fetz.ServerManager.ServerManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created by Noah Fetz on 27.12.2016.
 */
public class PluginMessageManager implements Listener {

    private final ServerManager plugin;
    private final String spacer = "#####";
    public PluginMessageManager(ServerManager serverManager) {
        this.plugin = serverManager;
        plugin.getProxy().getPluginManager().registerListener(serverManager, this);
    }

    public void sendPluginMessage(String message, ServerInfo server){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try{
            out.writeUTF(message);
        }catch(Exception ex){
        }
        server.sendData("bsm:cmd", stream.toByteArray());
    }

    @EventHandler
    public void onReceiveMessage(PluginMessageEvent e){
        if(!e.getTag().equals("bsm:cmd")){
            return;
        }
        if(!(e.getSender() instanceof Server)){
            return;
        }
        Server server = (Server)e.getSender();
        ByteArrayInputStream stream = new ByteArrayInputStream(e.getData());
        DataInputStream input = new DataInputStream(stream);
        try{
            processIncomingMessage(server, input.readUTF().split(spacer));
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void processIncomingMessage(Server server, String[] message){
        if(message[0].equals("playercount")){
            String bungeename = message[1];
            if(plugin.getManager().isActive(bungeename)){
                int playercount = ProxyServer.getInstance().getServerInfo(bungeename).getPlayers().size();
                sendPluginMessage("playercount" + spacer + bungeename + spacer + playercount, server.getInfo());
            }else{
                sendPluginMessage("playercount" + spacer + bungeename + spacer + "0", server.getInfo());
            }
        }
    }
}
