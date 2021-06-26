package com.shanebeestudios.api.task;

import com.shanebeestudios.api.BrewingManager;
import com.shanebeestudios.api.FurnaceManager;
import com.shanebeestudios.api.VirtualFurnaceAPI;
import com.shanebeestudios.api.machine.BrewingStand;
import com.shanebeestudios.api.machine.Furnace;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * Timer for ticking furnaces
 */
public class FurnaceTick extends BukkitRunnable {

    private final FurnaceManager furnaceManager;
    private final BrewingManager brewingManager;
    private final VirtualFurnaceAPI virtualFurnaceAPI;
    private int tick;
    private int id;
    private boolean running;

    public FurnaceTick(VirtualFurnaceAPI virtualFurnaceAPI) {
        this.virtualFurnaceAPI = virtualFurnaceAPI;
        this.furnaceManager = virtualFurnaceAPI.getFurnaceManager();
        this.brewingManager = virtualFurnaceAPI.getBrewingManager();
        this.tick = 0;
    }

    public void start() {
        BukkitTask task = this.runTaskTimerAsynchronously(virtualFurnaceAPI.getJavaPlugin(), 15, 1L);
        id = task.getTaskId();
    }

    @Override
    public void run() {
        running = true;
        try {
            for (Furnace furnace : furnaceManager.getAllFurnaces()) {
                if (!running) {
                    return;
                }
                furnace.tick();
            }

            for (BrewingStand stand : brewingManager.getAllStands()) {
                if (!running) {
                    return;
                }
                stand.tick();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        tick++;
        if (tick >= 6000) {
            this.furnaceManager.saveAll();
            this.brewingManager.saveAll();
            this.tick = 0;
        }
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        this.running = false;
        Bukkit.getScheduler().cancelTask(id);
    }

}
