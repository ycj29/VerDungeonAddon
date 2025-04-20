package com.ycjhub.verDungeonAddon.Triggers;

import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent;
import net.playavalon.mythicdungeons.api.parents.elements.DungeonTrigger;
import net.playavalon.mythicdungeons.menu.MenuButton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;

public class RoomClearedTrigger extends DungeonTrigger implements Listener {
    public RoomClearedTrigger(String displayName, Map<String, Object> config) {
        super(displayName, config);
    }

    @Override
    public MenuButton buildMenuButton() {
        return null;
    }

    @Override
    public void buildHotbarMenu() {

    }
    @EventHandler
    public void onClick(MythicMobSpawnEvent e) {

    }
}
