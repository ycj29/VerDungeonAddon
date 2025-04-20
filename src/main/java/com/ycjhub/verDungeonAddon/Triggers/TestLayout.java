package com.ycjhub.verDungeonAddon.Triggers;

import net.playavalon.mythicdungeons.MythicDungeons;
import net.playavalon.mythicdungeons.api.generation.layout.LayoutBranching;
import net.playavalon.mythicdungeons.api.generation.rooms.Connector;
import net.playavalon.mythicdungeons.api.generation.rooms.DungeonRoomContainer;
import net.playavalon.mythicdungeons.api.generation.rooms.InstanceRoom;
import net.playavalon.mythicdungeons.api.generation.rooms.RotatedRoom;
import net.playavalon.mythicdungeons.dungeons.dungeontypes.DungeonProcedural;
import net.playavalon.mythicdungeons.utility.RandomCollection;
import net.playavalon.mythicdungeons.utility.SimpleLocation;
import net.playavalon.mythicdungeons.utility.helpers.MathUtils;
import net.playavalon.mythicdungeons.utility.helpers.Util;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

public class TestLayout extends LayoutBranching {

    public TestLayout(DungeonProcedural dungeon, YamlConfiguration config) {
        super(dungeon, config);
    }

    @Override
    protected boolean selectFirstRoom() {
        System.out.println("1234565432123456765423456");
        System.out.println("1234565432123456765423456");
        System.out.println("1234565432123456765423456");
        System.out.println("1234565432123456765423456");
        System.out.println("1234565432123456765423456");
        System.out.println("1234565432123456765423456");
        System.out.println("1234565432123456765423456");

        if (this.dungeon.getUniqueRooms().isEmpty()) {
            MythicDungeons.inst().getLogger().severe("ERROR :: Dungeon has no rooms!!");
            return false;
        } else {
            List<DungeonRoomContainer> possibleRooms = new ArrayList<>(this.dungeon.getStartRooms().values());
            if (possibleRooms.isEmpty()) {
                possibleRooms = new ArrayList<>(this.dungeon.getUniqueRooms().values());
            }

            if (!this.roomWhitelist.isEmpty()) {
                possibleRooms.removeIf((roomx) -> !this.roomWhitelist.contains(roomx));
            }

            RandomCollection<DungeonRoomContainer> weightedRooms = new RandomCollection<>();

            for (DungeonRoomContainer room : possibleRooms) {
                if (!(room.getDepth().getMin() > 0.0F)) {
                    weightedRooms.add(room.getWeight() * (room.getOccurrences().getMin() + 1.0F), room);
                }
            }

            DungeonRoomContainer first = weightedRooms.next();
            if (first == null || first.getOrientations().isEmpty()) {
                return false;
            } else {
                // 一律用第0個orientation
                System.out.println(first.getOrientations().getFirst().getRotation());
                this.addRoom(new SimpleLocation(0.0, 128.0, 0.0), first.getOrientations().getFirst());
                if (this.DEBUG) {
                    MythicDungeons.inst().getLogger().info(Util.colorize("&dSelected first room: " + first.getNamespace()));
                }
                return true;
            }
        }
    }

    @Override
    protected InstanceRoom findRoom(DungeonRoomContainer from, Connector connector, SimpleLocation position, RandomCollection<DungeonRoomContainer> weightedRooms) {
        System.out.println("SUODHFKJSDHFKSDHFJDSHF");
        System.out.println("SUODHFKJSDHFKSDHFJDSHF");
        System.out.println("SUODHFKJSDHFKSDHFJDSHF");
        System.out.println("SUODHFKJSDHFKSDHFJDSHF");
        System.out.println("SUODHFKJSDHFKSDHFJDSHF");
        System.out.println("SUODHFKJSDHFKSDHFJDSHF");
        System.out.println("SUODHFKJSDHFKSDHFJDSHF");
        System.out.println("SUODHFKJSDHFKSDHFJDSHF");
        System.out.println("SUODHFKJSDHFKSDHFJDSHF");



        List<DungeonRoomContainer> invalidRooms = new ArrayList<>();
        DungeonRoomContainer nextRoom = null;
        InstanceRoom roomInst = null;
        boolean foundRoom = false;

        while (!foundRoom) {
            if (nextRoom != null) invalidRooms.add(nextRoom);
            if (invalidRooms.size() == weightedRooms.size()) break;

            nextRoom = weightedRooms.next();
            // 直接拿第0個orientation，不考慮方向
            if (nextRoom.getOrientations().isEmpty()) continue; // 避免空指標
            RotatedRoom room = nextRoom.getOrientations().getFirst();
            System.out.println( nextRoom.getOrientations().getFirst().getRotation());
            // 隨便挑一個connector即可（只挑能對接的那一個）
            for (Connector nextConnector : room.getConnectors()) {
                if (connector.getLocation().getDirection().isOpposite(nextConnector.getLocation().getDirection())
                        && nextConnector.canGenerate(from)) {
                    roomInst = new InstanceRoom(room, position, nextConnector.getLocation());
                    if (this.canRoomGenerate(roomInst)) {
                        foundRoom = true;
                        break;
                    }
                    roomInst = null;
                }
            }
        }
        return roomInst;
    }
}