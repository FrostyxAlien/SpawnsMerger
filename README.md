// Welche Versionen wurden benutzt?

Minecraft Version 1.21.5
JavaJDK 21 - auf Paper

APIs
- NBTApi

  
// Was kann das Plugin?

-Mob-Spawner mit begrenzten Spawnanzahl der Mobs
-Mob-Spawner zusammenführen zu einem mit rechte und linke Hand (Spawner mit 5 Spawns + Spawner mit 20 Spawns = 25 Spawns Spawner des gleichen Mobtyps)

// Wird noch hinzugefügt oder geändert
- GUI (hinzugefügt - wird noch überarbeitet)
- Hologram über platziertem Spawner
- Code wird überarbeitet

// Konzept Stand 16.08.2025
- GUI für Spawner-Verwaltung
   Spawner fusionieren
   Spawner einfrieren
   Spawnrate anpassen
   Spawner Statistik
   Spawner Level Upgrade
  
- Spawner einfrieren
   Visueller Effekt: Eisblaue Partikel & Eisblock-Hülle um den Spawner
   Mit Redstone einfrieren oder mit einem Schneeball
  
- Spawner zusammenführen / fusionieren (Merge-System)
   Zwei gleiche Spawner im gleichen Level im GUI kombinieren für mehr Spawns
  
- Spawn-Raten-Anpassung (In der Config die maximale Rate einstellen um Laggs zu vermeiden)

- Spawner Upgrades (vorerst bis Level 5)
   Level 1: Standard (1 Mob / 20s)
   Level 2: 2 Mobs / 18s
   Level 3: +5% Dropchance auf seltene Items
   Level 4: Mobs mit Custom Effekt + 8% Dropchance auf seltene Items
   Level 5: 3 Mobs / 15s + 10% Dropchance auf seltene Items + 5% XP-Multiplikator (mehr XP pro Mob)

  - Custom Mobs Spawner (kann in der Config deaktiviert werden)

// Permissions 
  - frostyspawner.use - Grundlegende Nutzung - mit GUI
  - frostyspawner.admin - Administrative Funktionen
