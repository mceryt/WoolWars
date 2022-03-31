package me.cubecrafter.woolwars.core.tasks;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.core.Arena;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class ArenaPlayingTask implements Runnable {

    @Getter private final BukkitTask task;
    private final Arena arena;

    public ArenaPlayingTask(Arena arena) {
        this.arena = arena;
        task = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), this, 0L, 20L);
    }

    @Override
    public void run() {

    }

}
