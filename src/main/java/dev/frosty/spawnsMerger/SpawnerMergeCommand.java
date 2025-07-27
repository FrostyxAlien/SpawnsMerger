package dev.frosty.spawnsMerger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

// Edit - Öffnet ein GUI zum Zusammenführen anstatt über Hand

public class SpawnerMergeCommand implements CommandExecutor {

    private final SpawnerMergeGUI gui;

    public SpawnerMergeCommand(SpawnerMergeGUI gui) {
        this.gui = gui;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Nur Spieler können diesen Befehl nutzen.");
            return true;
        }

        gui.open(player);
        return true;
    }
}
