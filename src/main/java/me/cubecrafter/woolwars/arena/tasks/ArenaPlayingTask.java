package me.cubecrafter.woolwars.arena.tasks;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.arena.PowerUp;
import me.cubecrafter.woolwars.arena.Team;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ArenaPlayingTask implements Runnable {

    @Getter private final BukkitTask task;
    @Getter private final BukkitTask rotatePowerUpsTask;
    private final Arena arena;
    private final Map<Team, Integer> placedBlocks = new HashMap<>();

    public ArenaPlayingTask(Arena arena) {
        this.arena = arena;
        arena.setTimer(60);
        task = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), this, 0L, 20L);
        rotatePowerUpsTask = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), () -> arena.getPowerUps().forEach(PowerUp::rotate), 0L, 1L);
    }

    @Override
    public void run() {
        if (arena.getTimer() > 0) {
            arena.setTimer(arena.getTimer() - 1);
            for (Integer i : Arrays.asList(1,2,3,4,5,10)) {
                if (arena.getTimer() == i) {
                    arena.sendMessage("&c{seconds} &7seconds left!".replace("{seconds}", String.valueOf(arena.getTimer())));
                }
            }
        } else if (arena.getTimer() == 0) {
            Map.Entry<Team, Integer> bestTeam = placedBlocks.entrySet().stream().max(Map.Entry.comparingByValue()).orElse(null);
            // NO PLACED BLOCKS
            if (bestTeam == null) {
                arena.setGameState(GameState.ROUND_OVER);
                // DRAW
            } else if (placedBlocks.entrySet().stream().filter(entry -> Objects.equals(entry.getValue(), bestTeam.getValue())).count() > 1) {
                arena.sendTitle(40, "&c&lDRAW", arena.getTeamPointsFormatted());
                arena.playSound("GHAST_MOAN");
                arena.setGameState(GameState.ROUND_OVER);
                // WINNER TEAM FOUND
            } else {
                Team winner = bestTeam.getKey();
                if (winner.getPoints() == arena.getRequiredPoints()) {
                    for (Team loop : arena.getTeams()) {
                        if (loop.equals(winner)) {
                            loop.sendTitle(40, "&a&lWINNER", arena.getTeamPointsFormatted());
                            loop.playSound("ENTITY_PLAYER_LEVELUP");
                        } else {
                            loop.sendTitle(40, "&c&lLOSER", arena.getTeamPointsFormatted());
                            loop.playSound("GHAST_MOAN");
                        }
                    }
                    arena.setGameState(GameState.RESTARTING);
                } else {
                    for (Team loop : arena.getTeams()) {
                        if (loop.equals(winner)) {
                            loop.sendTitle(40, "&a&lWINNER", arena.getTeamPointsFormatted());
                            loop.playSound("ENTITY_PLAYER_LEVELUP");
                        } else {
                            loop.sendTitle(40, "&c&lLOSER", arena.getTeamPointsFormatted());
                            loop.playSound("GHAST_MOAN");
                        }
                    }
                    if (arena.isLastRound()) {
                        arena.setGameState(GameState.RESTARTING);
                    } else {
                        arena.setGameState(GameState.ROUND_OVER);
                    }
                }
            }
            placedBlocks.clear();
            task.cancel();
            rotatePowerUpsTask.cancel();
        }
    }

    public void addPlacedBlock(Team team) {
        if (team == null) return;
        placedBlocks.merge(team, 1, Integer::sum);
    }

    public void removePlacedBlock(Team team) {
        if (placedBlocks.get(team) == null) return;
        placedBlocks.put(team, placedBlocks.get(team) - 1);
        if (placedBlocks.get(team) == 0) placedBlocks.remove(team);
    }

    public void checkWinners() {
        for (Map.Entry<Team, Integer> entry : placedBlocks.entrySet()) {
            if (entry.getValue() != arena.getBlocksRegion().getTotalBlocks()) continue;
            Team team = entry.getKey();
            team.addPoint();
            placedBlocks.clear();
            for (Team loop : arena.getTeams()) {
                if (loop.equals(team)) {
                    loop.sendTitle(40, "&a&lWINNER", arena.getTeamPointsFormatted());
                    loop.playSound("ENTITY_PLAYER_LEVELUP");
                } else {
                    loop.sendTitle(40, "&c&lLOSER", arena.getTeamPointsFormatted());
                    loop.playSound("GHAST_MOAN");
                }
            }
            if (team.getPoints() == arena.getRequiredPoints() || arena.isLastRound()) {
                task.cancel();
                rotatePowerUpsTask.cancel();
                arena.setGameState(GameState.RESTARTING);
                return;
            }
            task.cancel();
            rotatePowerUpsTask.cancel();
            arena.setGameState(GameState.ROUND_OVER);
        }
    }

}