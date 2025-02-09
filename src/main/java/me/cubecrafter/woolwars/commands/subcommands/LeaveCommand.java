/*
 * Wool Wars
 * Copyright (C) 2022 CubeCrafter Development
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.cubecrafter.woolwars.commands.subcommands;

import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.commands.SubCommand;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class LeaveCommand implements SubCommand, CommandExecutor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena == null) return;
        arena.removePlayer(player, true);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public String getLabel() {
        return "leave";
    }

    @Override
    public String getPermission() {
        return "woolwars.leave";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            execute(sender, args);
        }
        return true;
    }

}
