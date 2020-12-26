package xyz.acrylicstyle.mentions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import util.ICollectionList;
import xyz.acrylicstyle.tomeito_api.TomeitoAPI;
import xyz.acrylicstyle.tomeito_api.sounds.Sound;

public class Mentions extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        ICollectionList.asList(e.getMessage().split("\\s+")).forEach(s -> {
            if (s.equals("@everyone")) { // @everyone, mentions everyone (aqua color)
                if (!e.getPlayer().hasPermission("mentions.everyone")) return;
                e.setMessage(e.getMessage().replaceAll("@everyone", "" + ChatColor.AQUA + ChatColor.UNDERLINE + ChatColor.BOLD + "@everyone" + ChatColor.RESET + ChatColor.WHITE));
                TomeitoAPI.run(() -> TomeitoAPI.broadcastSound(Sound.BLOCK_NOTE_PLING, 1));
            } else if (s.equals("@admin")) { // @admin, mentions OPs (yellow color)
                if (!e.getPlayer().hasPermission("mentions.admin")) return;
                e.setMessage(e.getMessage().replaceAll("@admin", ChatColor.GOLD + "@admin" + ChatColor.RESET + ChatColor.WHITE));
                TomeitoAPI.run(() -> TomeitoAPI.getOnlinePlayers().filter(Player::isOp).forEach(p -> {
                    e.getRecipients().remove(p);
                    String message = String.format(e.getFormat(), e.getPlayer().getName(), e.getMessage()).replaceAll("@admin", "" + ChatColor.GOLD + ChatColor.UNDERLINE + ChatColor.BOLD + "@admin" + ChatColor.RESET + ChatColor.WHITE);
                    p.sendMessage(message);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 1);
                }));
            } else if (s.startsWith("@")) { // @player (aqua color)
                if (!e.getPlayer().hasPermission("mentions.mention")) return;
                String p1 = s.replaceFirst("@", "");
                Player p = Bukkit.getPlayerExact(p1);
                if (p != null) {
                    e.getRecipients().remove(p);
                    e.setMessage(e.getMessage().replaceAll("@" + p1, ChatColor.AQUA + "@" + p1 + ChatColor.RESET + ChatColor.WHITE));
                    String message = String.format(e.getFormat(), e.getPlayer().getName(), e.getMessage()).replaceAll("@" + p1, "" + ChatColor.AQUA + ChatColor.UNDERLINE + ChatColor.BOLD + "@" + p1 + ChatColor.RESET + ChatColor.WHITE);
                    p.sendMessage(message);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 1);
                }
            }
        });
    }
}
