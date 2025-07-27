package dev.frosty.spawnsMerger;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class SpawnerMergeGUI implements Listener {

    private static final String TITLE = "\u00a78Spawner Zusammenführen"; //

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(player, 27, TITLE);

        ItemStack pane = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        ItemMeta paneMeta = pane.getItemMeta();
        if (paneMeta != null) {
            paneMeta.setDisplayName(" ");
            pane.setItemMeta(paneMeta);
        }

        for (int i = 0; i < inv.getSize(); i++) {
            if (i == 11 || i == 13 || i == 15) {
                continue;
            }
            inv.setItem(i, pane);
        }

        ItemStack button = new ItemStack(Material.ANVIL);
        ItemMeta meta = button.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + "Zusammenführen");
            button.setItemMeta(meta);
        }
        inv.setItem(13, button);

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (!event.getView().getTitle().equals(TITLE)) {
            return;
        }

        int slot = event.getRawSlot();

        // Click on merge button
        if (slot == 13) {
            event.setCancelled(true);
            merge(player, event.getInventory());
            return;
        }

        if (slot < event.getView().getTopInventory().getSize()) {
            // inside our GUI
            if (slot != 11 && slot != 15) {
                event.setCancelled(true);
                return;
            }

            ItemStack item = event.isShiftClick() ? event.getCurrentItem() : event.getCursor();
            if (item != null && item.getType() != Material.AIR && item.getType() != Material.SPAWNER) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getView().getTitle().equals(TITLE)) {
            return;
        }

        Inventory inv = event.getInventory();
        Player player = (Player) event.getPlayer();

        for (int slot : new int[]{11, 15}) {
            ItemStack item = inv.getItem(slot);
            if (item != null && item.getType() != Material.AIR) {
                player.getInventory().addItem(item);
            }
        }
    }

    private void merge(Player player, Inventory inv) {
        ItemStack left = inv.getItem(11);
        ItemStack right = inv.getItem(15);

        if (left == null || right == null) {
            player.sendMessage("§cBitte lege zwei Spawner ein.");
            return;
        }

        if (left.getType() != Material.SPAWNER || right.getType() != Material.SPAWNER) {
            player.sendMessage("§cNur Spawner können zusammengeführt werden.");
            return;
        }

        NBTItem nbtLeft = new NBTItem(left);
        NBTItem nbtRight = new NBTItem(right);
        if (!nbtLeft.hasKey("SpawnerType") || !nbtLeft.hasKey("SpawnerValue") ||
            !nbtRight.hasKey("SpawnerType") || !nbtRight.hasKey("SpawnerValue")) {
            player.sendMessage("§cNur begrenzte Spawner können zusammengeführt werden.");
            return;
        }

        String typeLeft = nbtLeft.getString("SpawnerType");
        String typeRight = nbtRight.getString("SpawnerType");
        if (!typeLeft.equals(typeRight)) {
            player.sendMessage("§cSpawner müssen vom selben Typ sein.");
            return;
        }

        int valueLeft = nbtLeft.getInteger("SpawnerValue");
        int valueRight = nbtRight.getInteger("SpawnerValue");
        int sum = valueLeft + valueRight;

        nbtLeft.setInteger("SpawnerValue", sum);
        ItemStack result = nbtLeft.getItem();
        ItemMeta meta = result.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + "Spawner: " + ChatColor.YELLOW + typeLeft);
            meta.setLore(List.of(ChatColor.GRAY + "Verbleibende Spawns: " + sum));
            result.setItemMeta(meta);
        }

        // Clear input slots
        inv.setItem(11, null);
        inv.setItem(15, null);

        player.getInventory().addItem(result);
        player.sendMessage("§aSpawner zusammengeführt. Neue Spawnsanzahl: §e" + sum);
    }
}
