package dev.frosty.spawnsMerger;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;

public class SpawnerCommand implements CommandExecutor {

    // NBT
    private static final String NBT_TYPE = "SpawnerType";
    private static final String NBT_VALUE = "SpawnerValue";

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

        EntityType type;
        try {
            type = EntityType.valueOf(args[0].toUpperCase());

            // Spawnbar? z.B. keine ArmorStands, Spieler etc.
            if (!type.isAlive() || !type.isSpawnable()) {
                player.sendMessage("§cDieser Mob-Typ kann nicht als Spawner verwendet werden.");
                return true;
            }
        } catch (IllegalArgumentException e) {
            player.sendMessage("§cUngültiger Mob-Typ.");
            return true;
        }

        int value;
        try {
            value = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("§cBitte gib eine gültige Zahl für die Spawns an.");
            return true;
        }

        // Spawner-Item Beschreibung
        ItemStack base = new ItemStack(Material.SPAWNER);
        NBTItem nbt = new NBTItem(base);
        nbt.setString(NBT_TYPE, type.name());
        nbt.setInteger(NBT_VALUE, value);

        ItemStack spawner = nbt.getItem();
        ItemMeta meta = spawner.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + "Spawner: " + ChatColor.YELLOW + type.name());
            meta.setLore(List.of(ChatColor.GRAY + "Verbleibende Spawns: " + value));
            spawner.setItemMeta(meta);
        }

        player.getInventory().addItem(spawner);
        player.sendMessage("§aSpawner für §e" + type.name() + "§a mit §e" + value + "§a Spawns erhalten!");

        return true;
    }
}
