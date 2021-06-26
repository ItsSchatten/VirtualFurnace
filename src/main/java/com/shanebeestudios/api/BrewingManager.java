package com.shanebeestudios.api;

import com.shanebeestudios.api.machine.BrewingStand;
import com.shanebeestudios.api.property.BrewingProperties;
import com.shanebeestudios.api.util.Util;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BrewingManager {
    private final VirtualFurnaceAPI virtualFurnaceAPI;
    private final Map<UUID, BrewingStand> brewingMap;
    private final NamespacedKey key;
    private File brewingFile;
    private FileConfiguration brewingConfig;

    public BrewingManager(VirtualFurnaceAPI api) {
        this.virtualFurnaceAPI = api;
        this.brewingMap = new HashMap<>();
        this.key = Util.getKey("brewingId");
        loadBrewingConfig();
    }

    public BrewingStand getByID(@NotNull UUID uuid) {
        return this.brewingMap.get(uuid);
    }

    public Collection<BrewingStand> getAllStands() {
        return Collections.unmodifiableCollection(this.brewingMap.values());
    }

    public BrewingStand createBrewingStand(String name) {
        BrewingStand stand = new BrewingStand(name, BrewingProperties.NORMAL);
        this.brewingMap.put(stand.getUniqueID(), stand);
        saveBrewing(stand, true);
        return stand;
    }

    private void loadBrewingConfig() {
        if (this.brewingFile == null) {
            this.brewingFile = new File(this.virtualFurnaceAPI.getJavaPlugin().getDataFolder(), "brewing-stands.yml");
        }

        if (!brewingFile.exists())
            this.virtualFurnaceAPI.getJavaPlugin().saveResource("brewing-stands.yml", false);

        this.brewingConfig = YamlConfiguration.loadConfiguration(this.brewingFile);
        loadBrewingStands();
    }

    void loadBrewingStands() {
        ConfigurationSection section = this.brewingConfig.getConfigurationSection("brewing");
        if (section != null) {
            for (String string : section.getKeys(true)) {
                if (section.get(string) instanceof BrewingStand) {
                    BrewingStand brewingStand = ((BrewingStand) section.get(string));
                    if (brewingStand == null) return;

                    this.brewingMap.put(UUID.fromString(string), brewingStand);
                }
            }
        }
        if (!virtualFurnaceAPI.isSilentStart())
            Util.log("Loaded: &b" + this.brewingMap.size() + "&7 furnaces");
    }

    public void removeBrewingFromConfig(BrewingStand stand, boolean save) {
        this.brewingConfig.set("brewing." + stand.getUniqueID(), null);
        if (save)
            saveConfig();
    }

    public void saveBrewing(BrewingStand stand, boolean saveToFile) {
        this.brewingConfig.set("brewing." + stand.getUniqueID(), stand);
        if (saveToFile)
            saveConfig();
    }

    private void saveConfig() {
        try {
            brewingConfig.save(brewingFile);
        } catch (ConcurrentModificationException ignored) {
            // Don't know if this would be called but hey, we'll have it here just because.
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void shutdown() {
        saveAll();
        brewingMap.clear();
    }

    public void saveAll() {
        for (BrewingStand stand : this.brewingMap.values()) {
            saveBrewing(stand, false);
        }
        saveConfig();
    }

}
