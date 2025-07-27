package dev.frosty.spawnsMerger;

import org.bukkit.plugin.java.JavaPlugin;

import dev.frosty.spawnsMerger.SpawnerCommand;
import dev.frosty.spawnsMerger.SpawnerListener;
import dev.frosty.spawnsMerger.SpawnerMergeCommand;

// MAIN

public final class SpawnsMerger extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        System.out.println("Plugin gestartet.");

        SpawnerMergeGUI mergeGUI = new SpawnerMergeGUI();
        getServer().getPluginManager().registerEvents(new SpawnerListener(this), this);
        getServer().getPluginManager().registerEvents(mergeGUI, this);

        getCommand("givespawner").setExecutor(new SpawnerCommand());
        getCommand("mergespawner").setExecutor(new SpawnerMergeCommand());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("Plugin gestoppt.");
    }
}
