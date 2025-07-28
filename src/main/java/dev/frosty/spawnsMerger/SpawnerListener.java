package dev.frosty.spawnsMerger;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.bukkit.Location;
import net.kyori.adventure.text.Component;

public class SpawnerListener implements Listener {

    private final SpawnsMerger plugin;
    private final NamespacedKey remainingKey;
    private final NamespacedKey typeKey;
    private final Map<Location, ArmorStand> holograms = new HashMap<>();
    private final Map<Location, Integer> hologramTasks = new HashMap<>();

    public SpawnerListener(SpawnsMerger plugin) {
        this.plugin = plugin;
        this.remainingKey = new NamespacedKey(plugin, "remaining_spawns");
        this.typeKey = new NamespacedKey(plugin, "spawner_type");
    }

    @EventHandler
    public void onSpawnerPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() != Material.SPAWNER) {
            return;
        }

        ItemStack item = event.getItemInHand();
        NBTItem nbt = new NBTItem(item);
        if (!nbt.hasKey("SpawnerType") || !nbt.hasKey("SpawnerValue")) {
            return;
        }

        String typeName = nbt.getString("SpawnerType");
        int count = nbt.getInteger("SpawnerValue");

        EntityType entityType;
        try {
            entityType = EntityType.valueOf(typeName);
        } catch (IllegalArgumentException e) {
            return;
        }

        CreatureSpawner spawner = (CreatureSpawner) event.getBlockPlaced().getState();
        spawner.setSpawnedType(entityType);
        PersistentDataContainer container = spawner.getPersistentDataContainer();
        container.set(remainingKey, PersistentDataType.INTEGER, count);
        container.set(typeKey, PersistentDataType.STRING, typeName);
        spawner.update(true);
    }

    // Rechtsklick zum Anzeigen der restlichen Spawns
    @EventHandler
    public void onSpawnerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.SPAWNER) {
            return;
        }

        CreatureSpawner spawner = (CreatureSpawner) event.getClickedBlock().getState();
        PersistentDataContainer container = spawner.getPersistentDataContainer();
        Integer remaining = container.get(remainingKey, PersistentDataType.INTEGER);
        if (remaining == null) {
            return;
        }

        String typeName = container.get(typeKey, PersistentDataType.STRING);
        createHologram(spawner.getLocation(), typeName, remaining);
    }

    @EventHandler
    public void onSpawnerSpawn(SpawnerSpawnEvent event) {
        CreatureSpawner spawner = event.getSpawner();
        PersistentDataContainer container = spawner.getPersistentDataContainer();
        Integer remaining = container.get(remainingKey, PersistentDataType.INTEGER);
        if (remaining == null) {
            return;
        }

        String typeName = container.get(typeKey, PersistentDataType.STRING);

        remaining -= 1;
        if (remaining <= 0) {
            spawner.getBlock().setType(Material.AIR);
            removeHologram(spawner.getLocation());
        } else {
            container.set(remainingKey, PersistentDataType.INTEGER, remaining);
            spawner.update();
            updateHologram(spawner.getLocation(), typeName, remaining);
        }
    }

    // Beim zerstören
    @EventHandler
    public void onSpawnerBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.SPAWNER) {
            return;
        }

        removeHologram(event.getBlock().getLocation());

        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();
        boolean silk = tool != null && tool.containsEnchantment(Enchantment.SILK_TOUCH);
        if (!silk) {
            return;
        }

        CreatureSpawner spawner = (CreatureSpawner) event.getBlock().getState();
        PersistentDataContainer container = spawner.getPersistentDataContainer();
        Integer remaining = container.get(remainingKey, PersistentDataType.INTEGER);
        if (remaining == null) {
            return;
        }

        String typeName = container.get(typeKey, PersistentDataType.STRING);

        EntityType type = spawner.getSpawnedType();
        ItemStack drop = new ItemStack(Material.SPAWNER);
        NBTItem nbt = new NBTItem(drop);
        nbt.setString("SpawnerType", typeName != null ? typeName : type.name());
        nbt.setInteger("SpawnerValue", remaining);

        ItemStack result = nbt.getItem();
        ItemMeta meta = result.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§aSpawner: §e" + type.name());
            meta.setLore(List.of("§7Verbleibende Spawns: " + remaining));
            result.setItemMeta(meta);
        }

        event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), result);
        event.setExpToDrop(0);

        removeHologram(event.getBlock().getLocation());
    }

    // Hologram
    private void createHologram(Location loc, String type, int remaining) {
        removeHologram(loc);

        Location holoLoc = loc.clone().add(0.5, 1.5, 0.5);
        ArmorStand stand = loc.getWorld().spawn(holoLoc, ArmorStand.class, as -> {
            as.setInvisible(true);
            as.setMarker(true);
            as.setGravity(false);
            as.customName(Component.text(ChatColor.YELLOW + type + ChatColor.GRAY + " (" + remaining + ")"));
            as.setCustomNameVisible(true);
        });
        holograms.put(loc, stand);

        int taskId = plugin.getServer().getScheduler().runTaskLater(plugin,
                () -> removeHologram(loc), 100L).getTaskId();
        hologramTasks.put(loc, taskId);
    }

    private void updateHologram(Location loc, String type, int remaining) {
        ArmorStand stand = holograms.get(loc);
        if (stand != null && !stand.isDead()) {
            stand.customName(Component.text(ChatColor.YELLOW + type + ChatColor.GRAY + " (" + remaining + ")"));
        }
    }

    private void removeHologram(Location loc) {
        ArmorStand stand = holograms.remove(loc);
        Integer taskId = hologramTasks.remove(loc);
        if (taskId != null) {
            plugin.getServer().getScheduler().cancelTask(taskId);
        }
        if (stand != null && !stand.isDead()) {
            stand.remove();
        }
    }
}
