package net.slqmy.block_warriors.games.gladiators;

import net.slqmy.block_warriors.BlockWarriors;
import net.slqmy.block_warriors.enums.GameState;
import net.slqmy.block_warriors.enums.KitType;
import net.slqmy.block_warriors.enums.Team;
import net.slqmy.block_warriors.types.AbstractGame;
import net.slqmy.block_warriors.types.AbstractKit;
import net.slqmy.block_warriors.types.Arena;
import net.slqmy.block_warriors.utility.PacketUtility;
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

import java.util.HashMap;
import java.util.UUID;

public final class GladiatorsGame extends AbstractGame {
	private final Arena arena;
	private final HashMap<Team, Integer> kills = new HashMap<>();

	public GladiatorsGame(@NotNull final BlockWarriors plugin, @NotNull Arena arena) {
		super(
						plugin,
						arena,
						new KitType[]{KitType.RETIARIUS, KitType.MURMILLO}
		);

		this.arena = arena;
	}

	@Override
	public void onStart() {
		arena.sendTitle(ChatColor.GREEN.toString() + ChatColor.BOLD + "GO!");
		arena.sendMessage(ChatColor.GREEN + "The first team to get " + ChatColor.YELLOW + ChatColor.UNDERLINE + "5" + ChatColor.GREEN + " kills wins the game!");

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

			kills.put(arena.getTeam(player), 0);
		}
	}

	public void giveKill(@NotNull final Player player) {
		final Team team = arena.getTeam(player);
		final int teamKills = kills.get(team) + 1;

		if (teamKills == 5) {
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

		player.sendMessage(ChatColor.YELLOW + "+1" + ChatColor.GREEN + " Kill!");
		kills.replace(team, teamKills);
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

			final Player murderer = deadPlayer.getKiller();

			if (isArenaPlayer(murderer)) {
				assert murderer != null;

				giveKill(murderer);
			}
		}
	}

	@EventHandler
	public void onBlockBreak(@NotNull final BlockBreakEvent event) {
		if (isArenaPlayer(event.getPlayer())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerRespawn(@NotNull final PlayerRespawnEvent event) {
		final Player respawningPlayer = event.getPlayer();

		if (isPlaying(respawningPlayer)) {
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
