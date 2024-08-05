package net.slqmy.block_warriors_plugin.games.king_of_the_hill;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import net.slqmy.block_warriors_plugin.BlockWarriorsPlugin;
import net.slqmy.block_warriors_plugin.enums.GameState;
import net.slqmy.block_warriors_plugin.enums.Team;
import net.slqmy.block_warriors_plugin.types.AbstractGame;
import net.slqmy.block_warriors_plugin.types.Arena;
import net.slqmy.block_warriors_plugin.utility.DebugUtility;
import net.slqmy.block_warriors_plugin.utility.PacketUtility;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class KingOfTheHillGame extends AbstractGame {
	private final BlockWarriorsPlugin plugin;
	private final Map<Team, Integer> points = new HashMap<>();
	private BukkitTask checkIfKingTask;

	public KingOfTheHillGame(@NotNull BlockWarriorsPlugin plugin, @NotNull Arena arena) {
		super(plugin, arena);

		this.plugin = plugin;
	}

	@Override
	public void onStart() {
		arena.sendTitle(ChatColor.GREEN.toString() + ChatColor.BOLD + "GO!");
		arena.sendMessage(ChatColor.GREEN + "Whichever team reaches 100 points first wins!\n" + ChatColor.DARK_GRAY + "| " + ChatColor.GRAY + "Stand on the gold blocks or kill other players to gain points!");

		for (final UUID uuid : arena.getPlayers()) {
			final Player player = Bukkit.getPlayer(uuid);
			assert player != null;

			final ItemStack slimeBall = new ItemStack(Material.SLIME_BALL);

			final ItemMeta meta = slimeBall.getItemMeta();
			assert meta != null;

			meta.setDisplayName(ChatColor.GREEN + "Slimeball");
			meta.addEnchant(Enchantment.KNOCKBACK, 5, true);

			slimeBall.setItemMeta(meta);

			player.getInventory().addItem(
							slimeBall
			);

			points.put(arena.getTeam(player), 0);

			player.closeInventory();
		}

		checkIfKingTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			for (final UUID uuid : arena.getPlayers()) {
				final Player player = Bukkit.getPlayer(uuid);
				assert player != null;

				if (player.getLocation().subtract(0, 1, 0).getBlock().getType() == Material.GOLD_BLOCK) {
					addPoint(player, 2);
				}
			}
		}, 10, 22);

		tasks.add(checkIfKingTask);
	}

	private void addPoint(@NotNull final Player player, final int earnedPoints) {
		player.sendMessage(ChatColor.YELLOW + "+" + earnedPoints + ChatColor.GREEN + " Point" + (earnedPoints == 1 ? "" : "s"));

		final Team team = arena.getTeam(player);
		final int teamPoints = points.get(team) + earnedPoints;

		if (teamPoints >= 100) {
			arena.sendMessage(
							ChatColor.DARK_GRAY + " \n| " + ChatColor.GOLD + "Team " + team.getDisplayName() + ChatColor.GOLD
											+ " has won the game! Thank you for playing.\n ");

			for (final UUID uuid : arena.getPlayers()) {
				final Player currentPlayer = Bukkit.getPlayer(uuid);
				assert currentPlayer != null;

				final Team currentTeam = arena.getTeam(currentPlayer);

				currentPlayer.sendTitle(currentTeam == team ? ChatColor.GOLD.toString() + ChatColor.BOLD + "VICTORY!"
								: ChatColor.RED.toString() + ChatColor.BOLD + "GAME OVER!", "", 10, 10, 10);
			}

			arena.reset(true);
			return;
		}

		points.replace(team, teamPoints);
	}

	@EventHandler
	public void onEntityPickupItem(@NotNull final EntityPickupItemEvent event) {
		if (event.getEntity() instanceof final Player player && isPlaying(player)) {
			event.getItem().remove();

			event.setCancelled(true);
		}
	}

	@EventHandler
	private void onPlayerDeath(@NotNull final PlayerDeathEvent event) {
		DebugUtility.log("Player death event heard.");
		DebugUtility.log("Is the dead player an arena player? " + isArenaPlayer(event.getEntity()));

		final Player deadPlayer = event.getEntity();

		if (isArenaPlayer(event.getEntity())) {
			PacketUtility.respawnPlayer(deadPlayer);

			DebugUtility.log("Player drops: " + event.getDrops());

			final Player killer = deadPlayer.getKiller();

			if (isPlaying(killer)) {
				assert killer != null;

				addPoint(killer, 15);
			}
		}
	}

	@EventHandler
	public void onPlayerRespawn(@NotNull final PlayerRespawnEvent event) {
		DebugUtility.log("Player respawn event heard.");

		final Player player = event.getPlayer();

		DebugUtility.log("Is respawning player an arena player? " + isArenaPlayer(player));

		if (isArenaPlayer(player)) {
			event.setRespawnLocation(arena.getSpawnLocation());

			final ItemStack slimeBall = new ItemStack(Material.SLIME_BALL);

			final ItemMeta meta = slimeBall.getItemMeta();
			assert meta != null;

			meta.setDisplayName(ChatColor.GREEN + "Slimeball");
			meta.addEnchant(Enchantment.KNOCKBACK, 5, true);

			slimeBall.setItemMeta(meta);

			player.getInventory().addItem(
							slimeBall
			);
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(@NotNull final EntityDamageByEntityEvent event) {
		if (arena.getState() != GameState.PLAYING && isArenaPlayer(event.getEntity()) && isArenaPlayer(event.getDamager())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockBreak(@NotNull final BlockBreakEvent event) {
		if (isArenaPlayer(event.getPlayer())) {
			event.setCancelled(true);
		}
	}
}
