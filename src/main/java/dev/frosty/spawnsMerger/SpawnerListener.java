package dev.frosty.spawnsMerger;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import java.util.List;

public class SpawnerListener implements Listener {

    private final SpawnsMerger plugin;
    private final NamespacedKey remainingKey;

    public SpawnerListener(SpawnsMerger plugin) {
        this.plugin = plugin;
        this.remainingKey = new NamespacedKey(plugin, "remaining_spawns");
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
        spawner.getPersistentDataContainer().set(remainingKey, PersistentDataType.INTEGER, count);
        spawner.update(true);
    }

    @EventHandler
    public void onSpawnerSpawn(SpawnerSpawnEvent event) {
        CreatureSpawner spawner = event.getSpawner();
        PersistentDataContainer container = spawner.getPersistentDataContainer();
        Integer remaining = container.get(remainingKey, PersistentDataType.INTEGER);
        if (remaining == null) {
            return;
        }

        remaining -= 1;
        if (remaining <= 0) {
            spawner.getBlock().setType(Material.AIR);
        } else {
            container.set(remainingKey, PersistentDataType.INTEGER, remaining);
            spawner.update();
        }
    }

    @EventHandler
    public void onSpawnerBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.SPAWNER) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (tool == null || !tool.containsEnchantment(Enchantment.SILK_TOUCH)) {
            return;
        }

        CreatureSpawner spawner = (CreatureSpawner) event.getBlock().getState();
        PersistentDataContainer container = spawner.getPersistentDataContainer();
        Integer remaining = container.get(remainingKey, PersistentDataType.INTEGER);
        if (remaining == null) {
            return;
        }

        EntityType type = spawner.getSpawnedType();
        ItemStack drop = new ItemStack(Material.SPAWNER);
        NBTItem nbt = new NBTItem(drop);
        nbt.setString("SpawnerType", type.name());
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
    }
}
