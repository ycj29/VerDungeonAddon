package com.ycjhub.verDungeonAddon;

import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.modifier.StatModifier;
import io.lumine.mythic.lib.player.modifier.ModifierSource;
import io.lumine.mythic.lib.player.modifier.ModifierType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BuffOption {
    private static HashMap<MMOPlayerData, List<StatModifier>> store = new HashMap<>();
    private ItemStack item;
    private String name;
    private String stat;
    private String rarity;
    private String[] lore;
    private double value;
    private int tier;

    public BuffOption(ItemStack itemStack, String name, String... lore) {
        this(itemStack, name, "attack_damage","COMMON", 10.0, 0, lore);
    }
    public BuffOption(ItemStack i, String n, String s, String r, double v, int t, String... l) {
        item = i;
        name = n;
        rarity = r;
        lore = l;
        stat = s.toUpperCase();
        value = v;
        tier = t;
    }

    public void Apply(Player pl) {
        MMOPlayerData playerData = MMOPlayerData.get(pl);
        pl.sendMessage("Before: " + stat + playerData.getStatMap().getStat(stat));

        StatModifier sm = new StatModifier("VerDunAddon", stat, value, ModifierType.FLAT, EquipmentSlot.OTHER, ModifierSource.OTHER);
        sm.register(playerData);
        List<StatModifier> modifiers = store.getOrDefault(playerData, new ArrayList<>());
        modifiers.add(sm);
        store.put(playerData, modifiers);

    }

    public String getName() {
        return name;
    }
    public String getRarity() {
        return rarity;
    }
    public ItemStack getItem() {
        return item;
    }
    public String[] getLore() {
        return lore;
    }
    public double getValue() {
        return value;
    }
    public ItemStack getTier() {
        switch (tier) {
            case 0:
                ItemStack item0 = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
                ItemMeta meta0 = item0.getItemMeta();
                meta0.setDisplayName(ChatColor.translateAlternateColorCodes('&', rarity));
                meta0.setLore(new ArrayList<>());
                item0.setItemMeta(meta0);
                return item0;
            case 1:
                ItemStack item1 = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
                ItemMeta meta1 = item1.getItemMeta();
                meta1.setDisplayName(ChatColor.translateAlternateColorCodes('&', rarity));
                meta1.setLore(new ArrayList<>());
                item1.setItemMeta(meta1);
                return item1;
            case 2:
                ItemStack item2 = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
                ItemMeta meta2 = item2.getItemMeta();
                meta2.setDisplayName(ChatColor.translateAlternateColorCodes('&', rarity));
                meta2.setLore(new ArrayList<>());
                item2.setItemMeta(meta2);
                return item2;
            case 3:
                ItemStack item3 = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
                ItemMeta meta3 = item3.getItemMeta();
                meta3.setDisplayName(ChatColor.translateAlternateColorCodes('&', rarity));
                meta3.setLore(new ArrayList<>());
                item3.setItemMeta(meta3);
                return item3;
        }
        ItemStack item0 = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        ItemMeta meta0 = item0.getItemMeta();
        meta0.setDisplayName(ChatColor.translateAlternateColorCodes('&', rarity));
        meta0.setLore(new ArrayList<>());
        item0.setItemMeta(meta0);
        return item0;
    }
    public static void destroy(Player pl) {
        store.forEach((k, v) -> {
            if (k.getPlayer().equals(pl)) {
                v.forEach(sm -> {
                    sm.unregister(k);
                });
            }
        });
        store.remove(MMOPlayerData.get(pl));
    }
    @Override
    public String toString() {
        return name + stat + rarity + lore.toString();
    }
}
