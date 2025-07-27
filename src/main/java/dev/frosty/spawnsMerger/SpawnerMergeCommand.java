package dev.frosty.spawnsMerger;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class SpawnerMergeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Nur Spieler können diesen Befehl nutzen.");
            return true;
        }

        ItemStack main = player.getInventory().getItemInMainHand();
        ItemStack off = player.getInventory().getItemInOffHand();
        if (main.getType() != Material.SPAWNER || off.getType() != Material.SPAWNER) {
            player.sendMessage("§cHalte zwei Spawner in der Hand zum Mergen.");
            return true;
        }

        NBTItem mainNbt = new NBTItem(main);
        NBTItem offNbt = new NBTItem(off);
        if (!mainNbt.hasKey("SpawnerType") || !mainNbt.hasKey("SpawnerValue") ||
            !offNbt.hasKey("SpawnerType") || !offNbt.hasKey("SpawnerValue")) {
            player.sendMessage("§cNur begrenzte Spawner können gemerged werden.");
            return true;
        }

        String typeMain = mainNbt.getString("SpawnerType");
        String typeOff = offNbt.getString("SpawnerType");
        if (!typeMain.equals(typeOff)) {
            player.sendMessage("§cSpawner müssen vom selben Typ sein.");
            return true;
        }

        int valueMain = mainNbt.getInteger("SpawnerValue");
        int valueOff = offNbt.getInteger("SpawnerValue");
        int sum = valueMain + valueOff;

        mainNbt.setInteger("SpawnerValue", sum);
        ItemStack result = mainNbt.getItem();
        ItemMeta meta = result.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + "Spawner: " + ChatColor.YELLOW + typeMain);
            meta.setLore(List.of(ChatColor.GRAY + "Verbleibende Spawns: " + sum));
            result.setItemMeta(meta);
        }

        player.getInventory().setItemInMainHand(result);
        player.getInventory().setItemInOffHand(null);
        player.sendMessage("§aSpawner gemerged. Neuer Wert: §e" + sum);
        return true;
    }
}
