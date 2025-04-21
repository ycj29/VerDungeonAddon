package com.ycjhub.verDungeonAddon;

import io.lumine.mythic.core.skills.stats.Stats;
import io.lumine.mythic.lib.api.stat.modifier.StatModifier;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BuffOption {



    public BuffOption(ItemStack itemStack, String name,String... lore) {

    }
    public void Apply(Player pl) {
        PlayerData data = PlayerData.get(pl);
        String id = "my_plugin_buff";
        StatModifier modifier = new StatModifier(id, "JUMP_STRENGTH", 10.0);

        modifier.register(data.getMMOPlayerData());
    }
}
