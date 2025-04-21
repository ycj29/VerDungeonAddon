package com.ycjhub.verDungeonAddon;

import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.modifier.StatModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class BuffOption {
    private static HashMap<MMOPlayerData, StatModifier> store = new HashMap<>();
    private ItemStack item;
    private String name;
    private String stat;
    private String rarity;
    private String[] lore;
    private double value;

    public BuffOption() {

    }
    public BuffOption(ItemStack itemStack, String name, String... lore) {
        this(itemStack, name, "attack_damage","COMMON", 10.0, lore);
    }
    public BuffOption(ItemStack i, String n, String s, String r, double v, String... l) {
        item = i;
        name = n;
        rarity = r;
        lore = l;
        stat = s;
        value = v;
    }

    public void Apply(Player pl) {
        MMOPlayerData playerData = MMOPlayerData.get(pl);
        double value = 40;
        pl.sendMessage("BeforeClear Atk: " + playerData.getStatMap().getStat(stat));
        store.forEach((k, v) -> {
            if (k.getPlayer().equals(pl)) {
                v.unregister(k);
            }
        });
        pl.sendMessage("Before Atk: " + playerData.getStatMap().getStat(stat));


        StatModifier sm = new StatModifier("VerDunAddon", stat, value);
        sm.register(playerData);
        store.put(playerData, sm);
        //TemporaryStatModifier tempStat = new TemporaryStatModifier("VerDunAddon", stat, value, ModifierType.FLAT, EquipmentSlot.OTHER, ModifierSource.OTHER);
        //tempStat.register(playerData, duration);

// 強制刷新 Stat（根據你用的API版本選擇正確的方法）
        //playerData.getStatMap().bufferUpdates();

// 印出數值驗證
        pl.sendMessage("Atk: " + playerData.getStatMap().getStat(stat));
        pl.sendMessage("HELLO");
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
}
