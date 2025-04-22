package com.ycjhub.verDungeonAddon;

import io.lumine.mythic.core.skills.stats.Stats;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.modifier.StatModifier;
import io.lumine.mythic.lib.api.stat.modifier.TemporaryStatModifier;
import io.lumine.mythic.lib.player.modifier.ModifierSource;
import io.lumine.mythic.lib.player.modifier.ModifierType;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.playavalon.mythicdungeons.api.events.dungeon.DungeonEndEvent;
import net.playavalon.mythicdungeons.api.generation.rooms.InstanceRoom;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BuffMenu implements Listener {
    //private boolean forceUpgrade = false; //for listenr
    private BuffOption option1;
    private BuffOption option2;
    private BuffOption option3;
    VerDungeonAddon vd = VerDungeonAddon.getInstance();
    //private boolean lastRoom;

    public BuffMenu(BuffOption opt1, BuffOption opt2, BuffOption opt3) {
        this.option1 = opt1;
        this.option2 = opt2;
        this.option3 = opt3;
    }

    public BuffMenu() {
        // Done in main class VerDungeonAddon.getInstance().forceUpgrade.remove();
        refresh();
    }

    private void refresh() {
        List<BuffOption> all = new ArrayList<>(VerDungeonAddon.getInstance().getAllBuffOptions());
        if (all.size() < 3) {
            throw new IllegalStateException("Buff options 少於三個，無法隨機選三個不重複的！");
        }
        Collections.shuffle(all);
        this.option1 = all.get(0);
        this.option2 = all.get(1);
        this.option3 = all.get(2);
    }

    public void open(Player pl) {
        vd.forceUpgrade.add(pl.getUniqueId());
        Inventory inv = Bukkit.createInventory(pl, 45, "升級！");

        //

        ItemStack item = option1.getItem();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(option1.getName());
        List<String> coloredLore1 = Arrays.stream(option1.getLore())
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .collect(Collectors.toList());
        meta.setLore(coloredLore1);
        item.setItemMeta(meta);

        //

        ItemStack item2 = option2.getItem();
        ItemMeta meta2 = item2.getItemMeta();
        meta2.setDisplayName(option2.getName());
        List<String> coloredLore2 = Arrays.stream(option2.getLore())
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .collect(Collectors.toList());
        meta2.setLore(coloredLore2);
        item2.setItemMeta(meta2);

        //

        ItemStack item3 = option3.getItem();
        ItemMeta meta3 = item3.getItemMeta();
        meta3.setDisplayName(option3.getName());
        List<String> coloredLore3 = Arrays.stream(option3.getLore())
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .collect(Collectors.toList());
        meta3.setLore(coloredLore3);
        item3.setItemMeta(meta3);

        //

        List<Integer> glass1 = Arrays.asList(0, 1, 2, 9, 10, 11, 18, 20, 27, 28, 29, 36, 37, 38);
        List<Integer> glass2 = Arrays.asList(3, 4, 5, 12, 13, 14, 21, 23, 30, 31, 32, 39, 40, 41);
        List<Integer> glass3 = Arrays.asList(6, 7, 8, 15, 16, 17, 24, 26, 33, 34, 35, 42, 43, 44);

        int bo1 = 19;
        inv.setItem(bo1, item);

        int bo2 = 22;
        inv.setItem(bo2, item2);

        int bo3 = 25;
        inv.setItem(bo3, item3);


        glass1.forEach(i -> {
            inv.setItem(i, option1.getTier());
        });

        glass2.forEach(i -> {
            inv.setItem(i, option2.getTier());
        });

        glass3.forEach(i -> {
            inv.setItem(i, option3.getTier());
        });
        pl.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
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
            refresh();
            vd.forceUpgrade.remove(e.getWhoClicked().getUniqueId());
            p.closeInventory();
            List<InstanceRoom> list = VerDungeonAddon.sortRoomsAche.get(p);
            Integer idx = VerDungeonAddon.currentRoom.get(p);
            if (list != null && idx != null && idx < list.size()) {
                System.out.println(list + "" + idx + ":" + list.size());
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
        if (e.getView().getTitle().equals("升級！")) {
            Player p = (Player) e.getPlayer();
            if (vd.forceUpgrade.contains(p.getUniqueId())) {
                // 延遲一tick再開，避免事件衝突
                Bukkit.getScheduler().runTaskLater(VerDungeonAddon.getInstance(), () -> open(p), 1L);
                p.sendMessage(ChatColor.RED + "你必須選擇一個升級才能離開！");
            }
        }
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (!p.isOp()) return;

        int rn = VerDungeonAddon.getInstance().getCurrentRoom(p);
        if (rn == -1) return;
        VerDungeonAddon.currentRoom.putIfAbsent(p, rn);

        if (rn == 0) { // at connector
            if (!vd.forceUpgrade.contains(p.getUniqueId())) {
                vd.forceUpgrade.add(p.getUniqueId());
                //currentRoom.put(p, 0); // 同步設為0
                if (vd.lastRoom.contains(p.getUniqueId())) {
                    p.performCommand("leave");
                    p.sendTitle("恭喜完成第一章", "快去挑戰下一章", 10, 10, 10);
                    return;
                }
                open(p);
            }
        } else if (rn > 0) {
            if (VerDungeonAddon.currentRoom.get(p) < rn) {
                p.sendMessage(rn + ":" + VerDungeonAddon.currentRoom.get(p));
                p.sendTitle("關卡 1-" + rn, "開始挑戰！");
                VerDungeonAddon.currentRoom.put(p, rn);
                p.sendMessage(rn + ":" + VerDungeonAddon.currentRoom.get(p));
                System.out.println("ROOMM SORTED SIZE = " + VerDungeonAddon.sortRoomsAche.size());
            }
            if (rn == 20) {
                System.out.println("ROOMM SORTED SIZE = " + VerDungeonAddon.sortRoomsAche.size());
                vd.lastRoom.add(p.getUniqueId());
            }

        }
    }
    @EventHandler
    public void onDungeonEnd(DungeonEndEvent e) {
        e.getPlayers().forEach(player -> {
            VerDungeonAddon.sortRoomsAche.remove(player);
            VerDungeonAddon.currentRoom.remove(player);
            vd.forceUpgrade.remove(player.getUniqueId());
            BuffOption.destroy(player);
        });
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        VerDungeonAddon.sortRoomsAche.remove(player);
        VerDungeonAddon.currentRoom.remove(player);
        vd.forceUpgrade.remove(player.getUniqueId());
        BuffOption.destroy(player);
    }

}
