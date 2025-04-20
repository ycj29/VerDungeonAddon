package com.ycjhub.verDungeonAddon.Triggers;

import net.playavalon.mythicdungeons.api.events.dungeon.TriggerFireEvent;
import net.playavalon.mythicdungeons.api.parents.elements.DungeonFunction;
import net.playavalon.mythicdungeons.menu.MenuButton;
import net.playavalon.mythicdungeons.player.MythicPlayer;

import java.util.List;
import java.util.Map;

public class RoguelikeMenuFunction extends DungeonFunction {
    public RoguelikeMenuFunction(String namespace, Map<String, Object> config) {
        super(namespace, config);
    }

    @Override
    public void runFunction(TriggerFireEvent triggerFireEvent, List<MythicPlayer> list) {
        this.instance.getDungeon().asProcedural().getLayout();
    }

    @Override
    public MenuButton buildMenuButton() {
        return null;
    }

    @Override
    public void buildHotbarMenu() {

    }
}
