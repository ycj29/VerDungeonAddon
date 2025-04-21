package com.ycjhub.verDungeonAddon;

import com.ycjhub.verDungeonAddon.Triggers.TestLayout;
import net.playavalon.mythicdungeons.MythicDungeons;
import net.playavalon.mythicdungeons.api.MythicDungeonsService;
import net.playavalon.mythicdungeons.api.events.dungeon.DungeonEndEvent;
import net.playavalon.mythicdungeons.api.events.dungeon.DungeonStartEvent;
import net.playavalon.mythicdungeons.api.generation.layout.LayoutBranching;
import net.playavalon.mythicdungeons.api.generation.rooms.InstanceRoom;
import net.playavalon.mythicdungeons.dungeons.instancetypes.play.InstanceProcedural;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.List;

public final class VerDungeonAddon extends JavaPlugin implements Listener {
    private static final HashMap<Player, List<InstanceRoom>> sortRoomsAche = new HashMap<>();
    private static final WeakHashMap<Player, Integer> currentRoom = new WeakHashMap<>();
    private final Set<UUID> forceUpgrade = new HashSet<>();
    public static List<String> s = new ArrayList<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        MythicDungeons.inst().registerLayout(TestLayout.class, "test", "testlayout");
    }
    @EventHandler
    public void onWorldLoad(PlayerChatEvent e) {
        if (e.getPlayer().isOp()) {
            new BuffOption().Apply(e.getPlayer());
            System.out.println(s.toString());
            e.getPlayer().sendMessage(s.toString());
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    public static MythicDungeonsService mythicDungeonsAPI() {
        return Bukkit.getServer().getServicesManager().load(MythicDungeonsService.class);
    }
    @EventHandler
    public void onDungeonEnd(DungeonEndEvent e) {
        e.getPlayers().forEach(player -> {
            sortRoomsAche.remove(player);
            currentRoom.remove(player);
            forceUpgrade.remove(player.getUniqueId());
        });
    }
    @EventHandler
    public void onDungeonStart(DungeonStartEvent e) {
        e.getPlayers().forEach(player -> {
            sortRoomsAche.remove(player);
            currentRoom.remove(player);
            forceUpgrade.remove(player.getUniqueId());
        });
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (!p.isOp()) return;

        int rn = getCurrentRoom(p);
        if (rn == -1) return;
        currentRoom.putIfAbsent(p, rn);

        if (rn == 0) { // at connector
            if (!forceUpgrade.contains(p.getUniqueId())) {
                forceUpgrade.add(p.getUniqueId());
                //currentRoom.put(p, 0); // 同步設為0
                openMenu(p);
            }
        } else if (rn > 0) {
            if (currentRoom.get(p) < rn) {
                p.sendMessage(rn + ":" + currentRoom.get(p));
                p.sendTitle("關卡 1-" + rn, "開始挑戰！");
                currentRoom.put(p, rn);
                p.sendMessage(rn + ":" + currentRoom.get(p));
            }
        }
    }



    private List<InstanceRoom> sortRooms(Collection<InstanceRoom> rooms, Location start) {
        ArrayList<InstanceRoom> result = new ArrayList<>(rooms);
        result.sort(Comparator.comparingDouble(r -> customDistance(start, r.getSpawn())));
        return result;
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

    public int getCurrentRoom(Player p) {
        //0 = in connector, -1 = not in procedural dungeon else = room number
        //p.sendMessage("You're moving!"); //debug message
        if (mythicDungeonsAPI().isPlayerInDungeon(p)) {
            if (mythicDungeonsAPI().getDungeonInstance(p).getDungeon().asProcedural() == null) return -1;
            List<InstanceRoom> sortedRooms;
            if (sortRoomsAche.containsKey(p)) {
                sortedRooms = sortRoomsAche.get(p);
            } else {
                sortedRooms = sortRooms(
                        ((InstanceProcedural) mythicDungeonsAPI().getDungeonInstance(p))
                                .getRoomsByUUID().values(), (mythicDungeonsAPI().getDungeonInstance(p)).getStartLoc()
                );
                sortRoomsAche.put(p, sortedRooms);
            }
            for (int i = 0; i < sortedRooms.size(); i++) {
                InstanceRoom room = sortedRooms.get(i);
                if (room.getBounds().expand(0, 1, 0).contains(p.getX(), p.getY(), p.getZ())) {
                    //p.sendMessage(ChatColor.RED + "Found you in room: " + room.getUuid()
                            //+ " this is room #" + (i + 1));
                    return i+1;
                }
            }
            //p.sendMessage(ChatColor.RED + "Found you in connector");
            return 0;

            /*Note: the getDungeon() is getting the template of specific dungeon instance not instance it self, and getRoomsByUUID() gets all rooms in instance
            //e.getPlayer().sendMessage("In dungeon: " + mythicDungeonsAPI().getDungeonInstance(e.getPlayer()).getDungeon().getDisplayName());
            e.getPlayer().sendMessage("In dungeon: " + mythicDungeonsAPI().getDungeonInstance(e.getPlayer()).getDungeon().asProcedural().getLayout().getAllRooms().size());
            */
        }
        return -1;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getView().getTitle().equals("升級！")) {
            Player p = (Player) e.getPlayer();
            if (forceUpgrade.contains(p.getUniqueId())) {
                // 延遲一tick再開，避免事件衝突
                Bukkit.getScheduler().runTaskLater(this, () -> openMenu(p), 1L);
                p.sendMessage(ChatColor.RED + "你必須選擇一個升級才能離開！");
            }
        }
    }


}
