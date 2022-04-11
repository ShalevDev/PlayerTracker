
package com.shalev.tutorial;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static org.bukkit.Bukkit.getServer;


public class ScheduledTask {

    Main plugin = Main.getPlugin(Main.class);
    List<Integer> ids = new ArrayList<>();
    public static UUID uuid;
    public void startTasks(){
        Runnable runnerTargetUpdate = new BukkitRunnable() {
            @Override
            public void run() {
                if (uuid!=null){
                    Objects.requireNonNull(Bukkit.getPlayer(uuid)).sendMessage("Running target update");
                    updateTargets();

                }
                else
                    updateTargets();
            }
        };

        int id = getServer().getScheduler().scheduleSyncRepeatingTask(plugin,runnerTargetUpdate,0,20);
        ids.add(id);

        Runnable runnerTargetDisplay = new BukkitRunnable() {
            @Override
            public void run() {
                if (uuid!=null){
                    Objects.requireNonNull(Bukkit.getPlayer(uuid)).sendMessage("Running target display");
                    targetDisplay();

                }
                else
                    targetDisplay();
            }
        };


        id = getServer().getScheduler().scheduleSyncRepeatingTask(plugin,runnerTargetDisplay,5,10);
        ids.add(id);

    }

    private void targetDisplay(){
        for(Player p:Bukkit.getOnlinePlayers()){
            Integer result = MyListener.map.get(p.getUniqueId());
            //MyListener.players.size()>1 check could cause issues
            if(p.getInventory().getItemInMainHand().getType() == Material.COMPASS && result!=null && MyListener.players.size()>1){

                Player target = Bukkit.getPlayer(MyListener.players.get(result));
                Objects.requireNonNull(target);
                double distance = p.getLocation().distance(target.getLocation());
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(ChatColor.GREEN+ target.getDisplayName()+ChatColor.WHITE+" - "+(int)distance +"m"));
            }
        }
    }


    public void stop(){
        for (int id:ids) {
            getServer().getScheduler().cancelTask(id);
        }
    }

    public void stopAll(){
        getServer().getScheduler().cancelTasks(plugin);
    }


    public void updateTargets(){
        List<UUID> players = MyListener.players;
        HashMap<UUID, Integer> map = MyListener.map;

        for(UUID id : players){

            Player player = Bukkit.getPlayer(id);
            Objects.requireNonNull(player);

            Integer result = map.get(id);

            if (result!=null) {

                Player target = Bukkit.getPlayer(players.get(result));
                Objects.requireNonNull(target);

                Location targetLoc = target.getLocation();
                player.setCompassTarget(targetLoc);
            }

        }


    }
}
