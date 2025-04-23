package com.ycjhub.verDungeonAddon;

import net.playavalon.mythicdungeons.api.events.dungeon.DungeonEndEvent;
import net.playavalon.mythicdungeons.api.events.dungeon.DungeonStartEvent;
import net.playavalon.mythicdungeons.api.generation.rooms.InstanceRoom;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class BuffMenuListener implements Listener {

    private VerDungeonAddon plugin;
    public BuffMenuListener(VerDungeonAddon plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (plugin.getCurrentMenu((Player) e.getWhoClicked()) == null)
            return;
        BuffMenu buffMenu = plugin.getCurrentMenu((Player) e.getWhoClicked());
        BuffOption option1 = buffMenu.option1;
        BuffOption option2 = buffMenu.option2;
        BuffOption option3 = buffMenu.option3;
        if (!e.getView().getTitle().equals("升級！")) return;
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();

        int slot = e.getRawSlot();
        e.setCancelled(true);

        boolean chosen = false;

        if (slot == 19) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7你獲得了 " + option1.getRarity() +" buff：" + option1.getName() + " + " + option1.getValue() + "！"));
            option1.Apply(((Player) e.getWhoClicked()).getPlayer());
            chosen = true;
        } else if (slot == 22) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7你獲得了 " + option2.getRarity() +" buff：" + option2.getName() + " + " + option2.getValue() + "！"));
            option2.Apply(((Player) e.getWhoClicked()).getPlayer());
            chosen = true;
        } else if (slot == 25) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7你獲得了 " + option3.getRarity() +" buff：" + option3.getName() + " + " + option3.getValue() + "！"));
            option3.Apply(((Player) e.getWhoClicked()).getPlayer());
            chosen = true;
        }
        if (chosen) {
            buffMenu.refresh();
            buffMenu.forceUpgrade = false;
            p.closeInventory();
            List<InstanceRoom> list = VerDungeonAddon.sortRoomsAche.get(p);
            Integer idx = VerDungeonAddon.currentRoom.get(p);
            if (list != null && idx != null && idx < list.size()) {
                //System.out.println(list + "" + idx + ":" + list.size());
                Location targetLoc = list.get(idx).getSpawn().clone();
                targetLoc.add(0, 1, 0);
                targetLoc.setWorld(p.getWorld());
                p.teleport(targetLoc);
                //currentRoom.put(p, idx+1); // 更新房間快取
            }
        }
    }


    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (plugin.getCurrentMenu((Player) e.getPlayer()) == null)
            return;
        BuffMenu buffMenu = plugin.getCurrentMenu((Player) e.getPlayer());
        if (e.getView().getTitle().equals("升級！")) {
            Player p = (Player) e.getPlayer();
            if (buffMenu.forceUpgrade) {
                // 延遲一tick再開，避免事件衝突
                Bukkit.getScheduler().runTaskLater(VerDungeonAddon.getInstance(), () -> buffMenu.open(), 1L);
                p.sendMessage(ChatColor.RED + "你必須選擇一個升級才能離開！");
            }
        }
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (plugin.getCurrentMenu(e.getPlayer()) == null)
            return;
        BuffMenu buffMenu = plugin.getCurrentMenu(e.getPlayer());
        Player p = e.getPlayer();
        //if (!p.isOp()) return;

        int rn = VerDungeonAddon.getInstance().getCurrentRoom(p);
        if (rn == -1) return;
        VerDungeonAddon.currentRoom.putIfAbsent(p, rn);

        if (rn == 0) { // at connector
            if (!buffMenu.forceUpgrade) {
                buffMenu.forceUpgrade = true;
                //currentRoom.put(p, 0); // 同步設為0
                if (buffMenu.lastRoom) {
                    p.performCommand("leave");
                    p.sendTitle("恭喜完成第一章", "快去挑戰下一章", 10, 10, 10);
                    return;
                }
                buffMenu.open();
            }
        } else if (rn > 0) {
            if (VerDungeonAddon.currentRoom.get(p) < rn) {
                //p.sendMessage(rn + ":" + VerDungeonAddon.currentRoom.get(p));
                p.sendTitle("關卡 1-" + rn, "開始挑戰！");
                VerDungeonAddon.currentRoom.put(p, rn);
                //p.sendMessage(rn + ":" + VerDungeonAddon.currentRoom.get(p));
                //System.out.println("ROOMM SORTED SIZE = " + VerDungeonAddon.sortRoomsAche.get(p).size());
            }
            if (rn == VerDungeonAddon.sortRoomsAche.get(p).size()) {
                buffMenu.lastRoom = true;
            }

        }
    }
    @EventHandler
    public void onDungeonEnd(DungeonEndEvent e) {
        e.getPlayers().forEach(player -> {
            BuffOption.destroy(player);
            VerDungeonAddon.currentMenu.remove(player.getUniqueId());
            VerDungeonAddon.sortRoomsAche.remove(player);
            VerDungeonAddon.currentRoom.remove(player);
        });
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        VerDungeonAddon.sortRoomsAche.remove(player);
        VerDungeonAddon.currentRoom.remove(player);
        VerDungeonAddon.currentMenu.remove(player.getUniqueId());
        BuffOption.destroy(player);
    }
}
