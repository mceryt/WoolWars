package me.cubecrafter.woolwars.core.tasks;

import lombok.Getter;
import lombok.Setter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.core.Arena;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class ArenaTimerTask implements Runnable {

    @Getter private final BukkitTask task;
    private final Arena arena;
    @Setter private int timer = 120;

    public ArenaTimerTask(Arena arena) {
        this.arena = arena;
        task = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), this, 0L, 20L);
    }

    @Override
    public void run() {
        if (timer > 0) {
            timer--;
        }
        arena.broadcast(getTimerFormatted());
    }

    public String getTimerFormatted() {
        int minutes = (timer / 60) % 60;
        int seconds = (timer) % 60;
        return minutes + ":" + (seconds > 9 ? seconds : "0" + seconds);
    }

}
