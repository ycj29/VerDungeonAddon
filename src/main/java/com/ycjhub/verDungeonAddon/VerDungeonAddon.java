package com.ycjhub.verDungeonAddon;

import com.ycjhub.verDungeonAddon.Triggers.TestLayout;
import net.playavalon.mythicdungeons.MythicDungeons;
import net.playavalon.mythicdungeons.api.MythicDungeonsService;
import net.playavalon.mythicdungeons.api.events.dungeon.DungeonStartEvent;
import net.playavalon.mythicdungeons.api.generation.rooms.InstanceRoom;
import net.playavalon.mythicdungeons.dungeons.instancetypes.play.InstanceProcedural;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public final class VerDungeonAddon extends JavaPlugin implements Listener {
    static final HashMap<Player, List<InstanceRoom>> sortRoomsAche = new HashMap<>();
    static final WeakHashMap<Player, Integer> currentRoom = new WeakHashMap<>();
    public final Set<UUID> forceUpgrade = new HashSet<>();
    public final Set<UUID> lastRoom = new HashSet<>();
    public static List<String> s = new ArrayList<>();
    private static VerDungeonAddon instance;

    @Override
    public void onEnable() {
        instance = this;
        getAllBuffOptions();
        Bukkit.getPluginManager().registerEvents(new BuffMenu(), this);
        Bukkit.getPluginManager().registerEvents(this, this);
        MythicDungeons.inst().registerLayout(TestLayout.class, "test", "testlayout");
    }
    @EventHandler
    public void onPlayerChat(PlayerChatEvent e) {
        if (e.getPlayer().isOp()) {
            new BuffMenu().open(e.getPlayer());
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
    public void onDungeonStart(DungeonStartEvent e) {
        e.getPlayers().forEach(player -> {
            BuffOption.destroy(player);
            sortRoomsAche.remove(player);
            currentRoom.remove(player);
            forceUpgrade.remove(player.getUniqueId());
            lastRoom.remove(player.getUniqueId());
            new BuffMenu().open(player);
        });
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
    public static VerDungeonAddon getInstance() {
        return instance;
    }
    public List<BuffOption> getAllBuffOptions() {
        List<BuffOption> result = new ArrayList<>();
        File file = new File(getDataFolder(), "buffs.yml");
        if (!file.exists()) {
            saveResource("buffs.yml", false);
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.getKeys(false).forEach(key -> {
            String matName = config.getString(key + ".display", "STONE");
            Material material;
            try {
                material = Material.valueOf(matName);
            } catch (Exception ex) {
                material = Material.STONE;
            }
            ItemStack itemStack = new ItemStack(material);
            String name = config.getString(key + ".name", "未知BUFF");
            String stats = config.getString(key + ".stats", "");
            Integer tier = config.getInt(key + ".tier", 1);
            String rarity = config.getString(key + ".rarity", "common");
            Double value = config.getDouble(key + ".value", 0.0);
            List<String> loreList = config.getStringList(key + ".lore");
            String[] lore = loreList.toArray(new String[0]);
            result.add(new BuffOption(itemStack, name, stats, rarity, value, tier, lore));
        });
        result.forEach(s -> {
            System.out.println("Loaded " + s.toString());
        });
        return result;
    }

}
