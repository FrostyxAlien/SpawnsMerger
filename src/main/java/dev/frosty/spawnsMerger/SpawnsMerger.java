package dev.frosty.spawnsMerger;

import org.bukkit.plugin.java.JavaPlugin;

import dev.frosty.spawnsMerger.SpawnerCommand;
import dev.frosty.spawnsMerger.SpawnerListener;
import dev.frosty.spawnsMerger.SpawnerMergeCommand;

/** Main plugin class. */

public final class SpawnsMerger extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        System.out.println("Plugin gestartet.");
        getCommand("givespawner").setExecutor(new SpawnerCommand());
        getCommand("mergespawner").setExecutor(new SpawnerMergeCommand());
        getServer().getPluginManager().registerEvents(new SpawnerListener(this), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("Plugin gestoppt.");
    }
}
