package com.shalev.tutorial;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.*;

public class MyListener implements Listener, CommandExecutor {



    public static List<UUID> players = new ArrayList<>();
    public static HashMap<UUID, Integer> map = new HashMap<>();

    public static ScheduledTask st = new ScheduledTask();

    public static void pluginRefresh(){
        players.clear();
        for (Player p : Bukkit.getOnlinePlayers())
            players.add(p.getUniqueId());

        st.stop();
        st.startTasks();

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


        if (label.equalsIgnoreCase("cRefresh")){
            if(sender.hasPermission("cRefresh.use")) {
                pluginRefresh();
                sender.sendMessage("Players on the compass list: " + players.size());



                return true;
            }
            else{
                sender.sendMessage(ChatColor.RED+"You do not have the permission to perform this command");
            }
        }

        if(label.equalsIgnoreCase("taskStop")){
            if(sender.hasPermission("taskStop.use"))
                st.stop();
            else
                sender.sendMessage(ChatColor.RED+"You do not have the permission to perform this command");
            return true;
        }

        if(label.equalsIgnoreCase("taskStopAll")){
            if(sender.hasPermission("taskStopAll.use"))
                st.stopAll();
            else
                sender.sendMessage(ChatColor.RED+"You do not have the permission to perform this command");
        }

        if(label.equalsIgnoreCase("taskDebug")){
            if (sender instanceof Player){
                if(sender.hasPermission("taskDebug.use")) {
                    Player p = (Player) sender;

                    if (ScheduledTask.uuid == p.getUniqueId())
                        ScheduledTask.uuid = null;
                    else
                        ScheduledTask.uuid = p.getUniqueId();
                }
                else
                    sender.sendMessage(ChatColor.RED+"You do not have the permission to perform this command");
            }
            else
                sender.sendMessage(ChatColor.RED+"This command can only be performed by a player");
        }
        return true;
    }



    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        players.add(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID id = event.getPlayer().getUniqueId();

        players.remove(id);

        if (map.get(id)!= null)
            map.remove(id);

    }

    private void setTarget(Player player, Player target){
        //Special message + distance here
        displayTarget(player,target);


        Location targetLoc = target.getLocation();
        player.setCompassTarget(targetLoc);
    }

    private void displayTarget(Player player,Player target){
        double distance = player.getLocation().distance(target.getLocation());
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(ChatColor.GREEN+ target.getDisplayName()+ChatColor.WHITE+" - "+(int)distance +"m"));
    }

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if(player.getInventory().getItemInMainHand().getType() == Material.COMPASS  &&  event.getHand() == EquipmentSlot.HAND && ( event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)){
            if(players.size()>=1) {
                UUID id = player.getUniqueId();

                Integer result = map.get(id);

                if (result == null) {
                    result = 0;
                    map.put(id, result);
                } else if (result >= players.size() - 1) {
                    result = 0;
                    map.put(id, result);
                } else {
                    result = result + 1;
                    map.put(id, result);
                }


                Player target = Bukkit.getPlayer(players.get(result));
                Objects.requireNonNull(target);

                if (target.getUniqueId() != player.getUniqueId()) {
                    setTarget(player, target);
                } else if (players.size() > 1) {
                    if (result >= players.size() - 1)
                        result = 0;
                    else
                        result += 1;

                    map.put(id, result);
                    target = Bukkit.getPlayer(players.get(result));
                    Objects.requireNonNull(target);
                    setTarget(player, target);

                } else {
                    map.remove(id);
                    player.sendMessage(ChatColor.RED + "You are the only player online!");
                }
            }
            else{
                player.sendMessage(ChatColor.RED+"Online players list isn't updated! type /cRefresh to update it");
            }

        }
    }

}
