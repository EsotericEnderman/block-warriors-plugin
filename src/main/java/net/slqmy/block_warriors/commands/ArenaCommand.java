package net.slqmy.block_warriors.commands;

import net.slqmy.block_warriors.BlockWarriors;
import net.slqmy.block_warriors.enums.GameState;
import net.slqmy.block_warriors.guis.KitSelectionGUI;
import net.slqmy.block_warriors.guis.TeamSelectionGUI;
import net.slqmy.block_warriors.managers.ArenaManager;
import net.slqmy.block_warriors.types.AbstractCommand;
import net.slqmy.block_warriors.types.Arena;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class ArenaCommand extends AbstractCommand {
	private final BlockWarriors plugin;

	public ArenaCommand(@NotNull final BlockWarriors plugin) {
		super(
						"arena",
						"Join or leave a block muncher arena and see all the active arenas.",
						"/arena <join | leave | list | teams | kits> (arena)",
						new Integer[]{1, 2},
						new String[]{"game"},
						"block_muncher.arena",
						true);

		this.plugin = plugin;
	}

	@Override
	public boolean execute(@NotNull final CommandSender sender, final String @NotNull [] args) {
		final Player player = (Player) sender;

		final ArenaManager arenaManager = plugin.getArenaManager();

		if (args.length == 1) {
			if ("list".equalsIgnoreCase(args[0])) {
				player.sendMessage(" \n" + ChatColor.UNDERLINE + "Active Arenas:\n" + ChatColor.RESET + " ");

				for (final Arena arena : arenaManager.getArenas()) {
					player.sendMessage(
									ChatColor.DARK_GRAY + "• " + ChatColor.RESET + "Arena " + ChatColor.UNDERLINE + arena.getID() + "\n"
													+ ChatColor.GRAY + " - " + ChatColor.RESET + "State: " + arena.getState().getName() + "\n"
													+ ChatColor.GRAY + " - " + ChatColor.RESET + "Players: " + ChatColor.YELLOW + ChatColor.UNDERLINE
													+ arena.getPlayers().size() + "\n" + ChatColor.RESET + " ");
				}
			} else if ("leave".equalsIgnoreCase(args[0])) {
				final Arena arena = arenaManager.getArena(player);

				if (arena == null) {
					player.sendMessage(ChatColor.RED + "You are not in an arena!");
					return true;
				}

				player.sendMessage(ChatColor.GREEN + "You have left arena " + ChatColor.YELLOW + ChatColor.UNDERLINE + "#"
								+ arena.getID() + ChatColor.GREEN + ".");

				arena.removePlayer(player);
			} else if ("kits".equalsIgnoreCase(args[0])) {
				final Arena arena = arenaManager.getArena(player);

				if (arena == null) {
					player.sendMessage(ChatColor.RED + "You are not in an arena!");
					return true;
				}

				if (arena.getState() == GameState.PLAYING) {
					player.sendMessage(ChatColor.RED + "You may not select a kit right now, sorry!");
					return true;
				}

				new KitSelectionGUI(arena, player);
			} else if ("teams".equalsIgnoreCase(args[0])) {
				final Arena arena = arenaManager.getArena(player);

				if (arena == null) {
					player.sendMessage(ChatColor.RED + "You are not in an arena!");
					return true;
				}

				if (arena.getState() == GameState.PLAYING) {
					player.sendMessage(ChatColor.RED + "You may not change your team right now, sorry!");
					return true;
				}

				new TeamSelectionGUI(arena, player);
			} else {
				return false;
			}
		} else if (args.length == 2) {
			if ("join".equalsIgnoreCase(args[0])) {
				final Arena playerArena = arenaManager.getArena(player);

				if (playerArena == null) {
					final int id;

					try {
						id = Integer.parseInt(args[1]);
					} catch (final NumberFormatException exception) {
						player.sendMessage(ChatColor.RED + "Invalid arena ID! The ID Must be " + ChatColor.UNDERLINE
										+ " an integer above 0" + ChatColor.RED + "!");
						return false;
					}

					if (id <= 0 || id > arenaManager.getArenas().size()) {
						player.sendMessage(ChatColor.RED + "That arena does not exist!");
						return false;
					}

					final Arena arena = arenaManager.getArena(id);
					assert arena != null;

					if (arena.getState() == GameState.PLAYING) {
						player.sendMessage(ChatColor.RED + "There is already an active game in that arena!");
						return true;
					}

					arena.addPlayer(player);
					player.sendMessage(ChatColor.GREEN + "You have successfully been added to arena " + ChatColor.YELLOW
									+ ChatColor.UNDERLINE + "#" + id + ChatColor.GREEN + "!");

					return true;
				}

				player.sendMessage(ChatColor.RED + "You are already in an arena! Use " + ChatColor.UNDERLINE + "/arena leave"
								+ ChatColor.RED + " to leave.");
			} else {
				return false;
			}
		}

		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull final CommandSender sender, final String @NotNull [] args) {
		if (args.length == 1 && "join".equalsIgnoreCase(args[0].trim())) {
			final List<String> results = new ArrayList<>();

			for (final Arena arena : plugin.getArenaManager().getArenas()) {
				results.add(String.valueOf(arena.getID()));
			}

			return results;
		}

		return null;
	}
}
