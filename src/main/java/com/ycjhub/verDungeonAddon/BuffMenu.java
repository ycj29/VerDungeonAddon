package com.ycjhub.verDungeonAddon;

import net.playavalon.mythicdungeons.api.generation.rooms.InstanceRoom;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BuffMenu {



    public BuffMenu(BuffOption opt1, BuffOption opt2, BuffOption opt3) {

    }
    private void openMenu(Player p) {
        forceUpgrade.add(p.getUniqueId());
        Inventory inv = Bukkit.createInventory(p, 45, "升級！");
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY + "普通");
        meta.setLore(new ArrayList<>());
        item.setItemMeta(meta);
        ItemStack item2 = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
        ItemMeta meta2 = item2.getItemMeta();
        meta2.setDisplayName(ChatColor.GOLD + "傳奇");
        meta2.setLore(new ArrayList<>());
        item2.setItemMeta(meta2);
        List<Integer> i1 = Arrays.asList(0, 1, 2, 9, 10, 11, 18, 20, 27, 28, 29, 36, 37, 38);
        List<Integer> i2 = Arrays.asList(3, 4, 5, 12, 13, 14, 21, 23, 30, 31, 32, 39, 40, 41);
        List<Integer> i3 = Arrays.asList(6, 7, 8, 15, 16, 17, 24, 26, 33, 34, 35, 42, 43, 44);
        int ab1 = 19;
        int ab2 = 22;
        int ab3 = 25;
        ItemStack ab1i = new ItemStack(Material.IRON_SWORD);
        ItemMeta mb1 = ab1i.getItemMeta();
        mb1.setDisplayName(ChatColor.GRAY + "普通 - 傷害 + 5");
        mb1.setLore(Arrays.asList(ChatColor.WHITE + "普攻傷害 + 5", ChatColor.WHITE + "這是一個常見的卡牌！"));
        ab1i.setItemMeta(mb1);
        ItemStack ab2i = new ItemStack(Material.GOLDEN_SWORD);
        ItemMeta mb2 = ab2i.getItemMeta();
        mb2.setDisplayName(ChatColor.GOLD + "傳說 - 爆擊傷害 + 20");
        mb2.setLore(Arrays.asList(ChatColor.WHITE + "爆擊傷害 + 20", ChatColor.WHITE + "這是一個" + ChatColor.GOLD +"傳奇" + ChatColor.WHITE +"的卡牌！"));
        ab2i.setItemMeta(mb2);
        i1.forEach(i -> {
            inv.setItem(i, item);
        });
        inv.setItem(ab1, ab1i);
        i2.forEach(i -> {
            inv.setItem(i, item2);
        });
        inv.setItem(ab2, ab2i);
        i3.forEach(i -> {
            inv.setItem(i, item);
        });
        inv.setItem(ab3, ab1i);
        p.openInventory(inv);
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
            p.sendMessage(ChatColor.GRAY + "你獲得了 普通 buff：傷害 +5！");
            chosen = true;
        } else if (slot == 22) {
            p.sendMessage(ChatColor.GOLD + "你獲得了 傳奇 buff：爆擊傷害 +20！");
            chosen = true;
        } else if (slot == 25) {
            p.sendMessage(ChatColor.AQUA + "你獲得了 隱藏 buff：防禦 +10！");
            chosen = true;
        }
        if (chosen) {
            forceUpgrade.remove(p.getUniqueId());
            p.closeInventory();
            List<InstanceRoom> list = sortRoomsAche.get(p);
            Integer idx = currentRoom.get(p);
            if (list != null && idx != null && idx < list.size()) {
                Location targetLoc = list.get(idx).getSpawn().clone();
                targetLoc.add(0, 1, 0);
                targetLoc.setWorld(p.getWorld());
                p.teleport(targetLoc);
                //currentRoom.put(p, idx+1); // 更新房間快取
            } else {
                p.performCommand("leave");
                p.sendTitle("恭喜完成第一章", "快去挑戰下一章", 10, 10, 10);
            }
        }
    }
}
