package net.slqmy.block_warriors_plugin.games.block_muncher;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;

import net.slqmy.block_warriors_plugin.BlockWarriorsPlugin;
import net.slqmy.block_warriors_plugin.enums.GameState;
import net.slqmy.block_warriors_plugin.enums.KitType;
import net.slqmy.block_warriors_plugin.enums.Team;
import net.slqmy.block_warriors_plugin.types.AbstractGame;
import net.slqmy.block_warriors_plugin.types.AbstractKit;
import net.slqmy.block_warriors_plugin.types.Arena;
import net.slqmy.block_warriors_plugin.utility.PacketUtility;

import java.util.HashMap;
import java.util.UUID;

public final class BlockMuncherGame extends AbstractGame {

	private final Arena arena;
	private final HashMap<Team, Integer> points = new HashMap<>();

	public BlockMuncherGame(@NotNull final BlockWarriorsPlugin plugin, @NotNull final Arena arena) {
		super(
						plugin,
						arena,
						new KitType[]{KitType.MOLE, KitType.VOLE}
		);

		this.arena = arena;
	}

	@Override
	public void onStart() {
		arena.sendTitle(ChatColor.GREEN.toString() + ChatColor.BOLD + "GO!");
		arena.sendMessage(ChatColor.GREEN + "Eat (break) " + ChatColor.YELLOW + ChatColor.UNDERLINE + "20" + ChatColor.GREEN
						+ " blocks as fast as possible to win!");

		final HashMap<UUID, AbstractKit> kits = arena.getKits();

		for (final UUID uuid : kits.keySet()) {
			final Player player = Bukkit.getPlayer(uuid);
			assert player != null;

			final AbstractKit playerKit = kits.get(uuid);

			if (playerKit != null) {
				playerKit.giveItems(player);
				playerKit.activateKit(player);
			}
		}

		for (final UUID uuid : arena.getPlayers()) {
			final Player player = Bukkit.getPlayer(uuid);
			assert player != null;

			points.put(arena.getTeam(player), 0);

			player.closeInventory();
		}
	}

	private void addPoint(@NotNull final Player player) {
		player.sendMessage(ChatColor.YELLOW + "+1" + ChatColor.GREEN + " Point");

		final Team team = arena.getTeam(player);
		final int teamPoints = points.get(team) + 1;

		if (teamPoints == 20) {
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
	public void onBlockBreak(@NotNull final BlockBreakEvent event) {
		final Player player = event.getPlayer();

		if (!isArenaPlayer(player)) {
			return;
		}

		if (arena.getState() == GameState.PLAYING) {
			addPoint(player);
		} else {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerInteractEntity(@NotNull final PlayerInteractEntityEvent event) {
		if (isArenaPlayer(event.getPlayer()) && arena.getState() != GameState.PLAYING) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(@NotNull final EntityDamageByEntityEvent event) {
		if (arena.getState() != GameState.PLAYING && isArenaPlayer(event.getEntity()) && isArenaPlayer(event.getDamager())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDeath(@NotNull final PlayerDeathEvent event) {
		final Player deadPlayer = event.getEntity();

		if (isArenaPlayer(deadPlayer)) {
			PacketUtility.respawnPlayer(deadPlayer);
		}
	}

	@EventHandler
	public void onPlayerRespawn(@NotNull final PlayerRespawnEvent event) {
		final Player respawningPlayer = event.getPlayer();

		if (isArenaPlayer(respawningPlayer)) {
			event.setRespawnLocation(arena.getSpawnLocation());

			final AbstractKit playerKit = arena.getKits().get(respawningPlayer.getUniqueId());

			if (playerKit != null) {
				playerKit.giveItems(respawningPlayer);
				playerKit.activateKit(respawningPlayer);
			}
		}
	}

	@EventHandler
	public void onItemPickup(@NotNull final EntityPickupItemEvent event) {
		if (isArenaPlayer(event.getEntity())) {
			event.setCancelled(true);
			event.getItem().remove();
		}
	}
}
