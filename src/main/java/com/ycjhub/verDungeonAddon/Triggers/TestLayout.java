package com.ycjhub.verDungeonAddon.Triggers;

import com.ycjhub.verDungeonAddon.VerDungeonAddon;
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
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

public class TestLayout extends LayoutBranching {

    public TestLayout(DungeonProcedural dungeon, YamlConfiguration config) {
        super(dungeon, config);
    }
    private Connector bannedConnector;

    @Override
    protected boolean selectFirstRoom() {
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

            for(DungeonRoomContainer room : possibleRooms) {
                if (!(room.getDepth().getMin() > (double)0.0F)) {
                    weightedRooms.add(room.getWeight() * (room.getOccurrences().getMin() + (double)1.0F), room);
                }
            }

            DungeonRoomContainer first = weightedRooms.next();
            if (first == null) {
                return false;
            } else {
                //update
                /*RotatedRoom r = first.getRandomOrientation();
                SimpleLocation offset = new SimpleLocation(0.0F, 0.0F, 0.0F);
                offset.shift(r.getBounds().getMin());
                InstanceRoom ir = new InstanceRoom(r, new SimpleLocation(0.0F, 128.0F, 0.0F), offset);
                ir.addUsedConnector(getEntranceConnector(r));*/
                RotatedRoom picked = first.getRandomOrientation();
                VerDungeonAddon.s.add(picked.getSpawn().toString() + ":| " + picked.getConnectors().size() + "::::" + picked.getConnectors().getFirst().getLocation() + "!" + getEntranceConnector(picked).getLocation());
                this.addRoom(new SimpleLocation(0.0F, 128.0F, 0.0F), picked); //need to get the correct direction
                if (this.DEBUG) {
                    MythicDungeons.inst().getLogger().info(Util.colorize("&dSelected first room: " + first.getNamespace()));
                }

                return true;
            }
        }
    }



    @Override
    protected InstanceRoom findRoom(DungeonRoomContainer from, Connector connector, SimpleLocation position, RandomCollection<DungeonRoomContainer> weightedRooms) {
        ArrayList<RotatedRoom> orientations = new ArrayList<>();
        RotatedRoom to = null;
        List<DungeonRoomContainer> invalidRooms = new ArrayList<>();
        DungeonRoomContainer nextRoom = null;
        InstanceRoom roomInst = null;
        boolean foundRoom = false;

        while(!foundRoom) {
            orientations.remove(to);
            Map<RotatedRoom, List<Connector>> validConnectors = new HashMap<>();
            if (orientations.isEmpty()) {
                if (nextRoom != null) {
                    invalidRooms.add(nextRoom);
                }

                if (invalidRooms.size() == weightedRooms.size()) {
                    break;
                }

                nextRoom = weightedRooms.next();
                orientations = new ArrayList<>();
                validConnectors = new HashMap<>();

                for(RotatedRoom room : nextRoom.getOrientations()) {
                    //random pick a room and loop all rotation
                    boolean foundConnector = false;

                    for(Connector nextConnector : room.getConnectors()) {
                        //loop all connector (mine should only have two)
                        if (getEntranceConnector(room).equals(nextConnector)) { // ADDED condition to check if it's a wanted connector
                            //check if |[A] <-> [B]| or [A]| <-> |[B]                            //canGenerate just check for whitelist
                            if (connector.getLocation().getDirection().isOpposite(nextConnector.getLocation().getDirection()) && nextConnector.canGenerate(from)) {

                                //update
                                foundConnector = true;
                                (validConnectors.computeIfAbsent(room, (k) -> new ArrayList<>())).add(nextConnector);
                            }
                        }
                    }

                    if (validConnectors.get(room) != null) {
                        Collections.shuffle(validConnectors.get(room));
                    }

                    if (foundConnector) {
                        orientations.add(room);
                    }
                }
            }

            if (!orientations.isEmpty()) {
                to = orientations.get(MathUtils.getRandomNumberInRange(0, orientations.size() - 1));
                if (validConnectors.get(to) != null) {
                    for(Connector nextConnector : validConnectors.get(to)) {
                        roomInst = new InstanceRoom(to, position, nextConnector.getLocation());
                        if (this.canRoomGenerate(roomInst)) {
                            foundRoom = true;
                            break;
                        }

                        roomInst = null;
                    }
                }
            }
        }

        return roomInst;
    }

    public Connector getEntranceConnector(RotatedRoom room) {
        double d = Double.MAX_VALUE;
        Connector r = null;
        for (int i = 0; i < room.getConnectors().size(); i++) {
            Location conLoc = room.getConnectors().get(i).getLocation().asLocation();
            double temp = customDistance(room.getSpawn(), conLoc);
            if (d > temp) {
                d = temp;
                r = room.getConnectors().get(i);
            }
        }
        return r;
    }
    private static double customDistance(Location a, Location b) {
        if (a == null || b == null) {
            // 回傳一個極大值，代表這個房間沒有效座標
            return Double.MAX_VALUE;
        }
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        double dz = a.getZ() - b.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}