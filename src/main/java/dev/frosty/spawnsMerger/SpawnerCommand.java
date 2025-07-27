package dev.frosty.spawnsMerger;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;

public class SpawnerCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String  [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Nur Spieler können diesen Befehl nutzen.");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage("§cVerwendung: /givespawner <MobType> <Spawnsanzahl>");
            return true;
        }

        String mobType = args[0].toUpperCase(); // z. B. COW
        int value;

        try {
            value = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("§cBitte gib eine gültige Zahl für die Spawns an.");
            return true;
        }

        ItemStack base = new ItemStack(Material.SPAWNER);
        NBTItem nbt = new NBTItem(base);
        nbt.setString("SpawnerType", mobType);
        nbt.setInteger("SpawnerValue", value);

        ItemStack spawner = nbt.getItem();
        ItemMeta meta = spawner.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + "Spawner: " + ChatColor.YELLOW + mobType);
            meta.setLore(List.of(ChatColor.GRAY + "Verbleibende Spawns: " + value));
            spawner.setItemMeta(meta);
        }

        player.getInventory().addItem(spawner);
        player.sendMessage("§aSpawner für §e" + mobType + "§a mit §e" + value + "§a Spawns erhalten!");

        return true;
    }
}
