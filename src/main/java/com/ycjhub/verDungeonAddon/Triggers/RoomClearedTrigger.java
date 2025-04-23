package com.ycjhub.verDungeonAddon.Triggers;

import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent;
import net.playavalon.mythicdungeons.api.parents.TriggerCategory;
import net.playavalon.mythicdungeons.api.parents.elements.DungeonTrigger;
import net.playavalon.mythicdungeons.dungeons.triggers.TriggerMobDeath;
import net.playavalon.mythicdungeons.menu.MenuButton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;

public class RoomClearedTrigger extends DungeonTrigger {

    public RoomClearedTrigger() {
        super("Room Cleared");
    }

    @Override
    public MenuButton buildMenuButton() {
        return null;
    }

    @Override
    public void buildHotbarMenu() {

    }
}
