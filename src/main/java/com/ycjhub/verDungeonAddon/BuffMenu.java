package com.ycjhub.verDungeonAddon;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class BuffMenu implements Listener {
    //private boolean forceUpgrade = false; //for listenr
    protected BuffOption option1;
    protected BuffOption option2;
    protected BuffOption option3;
    private Player pl;
    public boolean forceUpgrade = false;
    public boolean lastRoom = false;
    //private boolean lastRoom;

    public BuffMenu(Player player) {
        // Done in main class VerDungeonAddon.getInstance().forceUpgrade.remove();
        pl = player;
        refresh();
    }

    protected void refresh() {
        List<BuffOption> all = new ArrayList<>(VerDungeonAddon.getInstance().getAllBuffOptions(pl));
        if (all.size() < 3) {
            throw new IllegalStateException("Buff options 少於三個，無法隨機選三個不重複的！");
        }
        Collections.shuffle(all);
        this.option1 = all.get(0);
        this.option2 = all.get(1);
        this.option3 = all.get(2);
    }

    public void open() {
        if (!forceUpgrade)
            return;
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


}
