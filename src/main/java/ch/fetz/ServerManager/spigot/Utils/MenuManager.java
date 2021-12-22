package ch.fetz.ServerManager.spigot.Utils;

import ch.fetz.ServerManager.spigot.SpigotServerManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

/**
 * Created by Noah Fetz on 05.12.2016.
 */
public class MenuManager {
    private final SpigotServerManager plugin;

    public MenuManager(SpigotServerManager spigotServerManager) {
        this.plugin = spigotServerManager;
    }

    public void openMainMenu(final Player p, final int page){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Inventory mainInventory = Bukkit.createInventory(p, 54, "§aServers §8(§7Page §e" + page + "§8)");
            p.openInventory(mainInventory);
            ArrayList<String> servers = plugin.getSpigotManager().getAllServers();

            int begin = page*45-45;
            int counter = begin;
            int end = begin+45;
            int slotCounter = 0;
            while(counter < end && counter < servers.size()){

                String server = servers.get(begin+slotCounter);

                boolean isonline = plugin.getSpigotManager().isOnline(server);

                ArrayList<String> lore = new ArrayList<>();

                lore.add("§7Bungee Name§8: §e" + server);
                lore.add(" ");
                lore.add("§7Status§8: §e" + isOnlineString(isonline));
                lore.add(" ");
                lore.add("§7IP§8: §e" + plugin.getSpigotManager().getIp(server));
                lore.add("§7Port§8: §e" + plugin.getSpigotManager().getPort(server));
                lore.add(" ");
                lore.add("§7Enabled§8: §e" + getColoredBoolean(plugin.getSpigotManager().isActive(server)));
                lore.add("§7Lobby§8: §e" + getColoredBoolean(plugin.getSpigotManager().isLobby(server)));
                lore.add("§7Restricted§8: §e" + getColoredBoolean(plugin.getSpigotManager().isRestricted(server)));
                lore.add("§7Playercount§8: §e-§7/§e-");

                int color;
                if(isonline){
                    color = 5;
                }else{
                    color = 14;
                }
                ItemStack item = new ItemStack(Material.CLAY, 1, (byte)color);
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(plugin.getSpigotManager().getDisplayName(server));
                itemMeta.setLore(lore);
                item.setItemMeta(itemMeta);

                mainInventory.setItem(slotCounter, item);
                slotCounter++;
                counter++;
                p.updateInventory();
            }
            if(servers.size() > end){
                ItemStack next = new ItemStack(Material.PAPER, 1);
                ItemMeta nextMeta = next.getItemMeta();
                nextMeta.setDisplayName("§7Next Page");
                next.setItemMeta(nextMeta);
                mainInventory.setItem(53, next);
            }

            if(begin >= 45){
                ItemStack previous = new ItemStack(Material.PAPER, 1);
                ItemMeta previousMeta = previous.getItemMeta();
                previousMeta.setDisplayName("§7Previous Page");
                previous.setItemMeta(previousMeta);
                mainInventory.setItem(52, previous);
            }
        });
    }

    private String getColoredBoolean(boolean input){
        if(input){
            return "§atrue";
        }else{
            return "§cfalse";
        }
    }

    private String isOnlineString(boolean input){
        if(input){
            return "§aOnline";
        }else{
            return "§cOffline";
        }
    }
}
