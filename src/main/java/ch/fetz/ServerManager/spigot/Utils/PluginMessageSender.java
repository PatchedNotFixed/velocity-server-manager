package ch.fetz.ServerManager.spigot.Utils;

import ch.fetz.ServerManager.spigot.SpigotServerManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created by Noah Fetz on 27.12.2016.
 */
public class PluginMessageSender implements PluginMessageListener {

    private final SpigotServerManager plugin;

    public PluginMessageSender(SpigotServerManager spigotServerManager) {
        this.plugin = spigotServerManager;
    }

    public void sendPluginMessage(Player p, String message){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try {
            out.writeUTF(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        p.sendPluginMessage(plugin, "bsm:cmd", stream.toByteArray());
    }

    @Override
    public void onPluginMessageReceived(String channel, Player p, byte[] bytes) {
        if(!channel.equalsIgnoreCase("bsm:cmd")){
            return;
        }
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        DataInputStream in = new DataInputStream(stream);
        try {
            in.readUTF();
        } catch (Exception e) {
        }
    }
}
