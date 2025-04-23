package com.ycjhub.verDungeonAddon;

import net.playavalon.mythicdungeons.dungeons.functions.FunctionSpawnMythicMob;

public class FunctionRoguelikeSpawnMythicMob extends FunctionSpawnMythicMob {

    public FunctionRoguelikeSpawnMythicMob() {
        super();
    }
    @Override
    public String getLevelString() {
        //get for specific player
        return multiplyRange(super.getLevelString(), VerDungeonAddon.getInstance().getCurrentRoom(instance.getPlayers().getFirst().getPlayer())); // or dynamically calculated
    }
    private static String multiplyRange(String range, int times) {
        String[] parts = range.split("-");
        if (parts.length != 2) {
            return Integer.parseInt(parts[0].trim()) * times + "";
        }
        int min = Integer.parseInt(parts[0].trim());
        int max = Integer.parseInt(parts[1].trim());
        return (min * times) + "-" + (max * times);
    }

}
