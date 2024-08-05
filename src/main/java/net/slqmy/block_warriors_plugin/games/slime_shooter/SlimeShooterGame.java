package net.slqmy.block_warriors_plugin.games.slime_shooter;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import net.slqmy.block_warriors_plugin.BlockWarriorsPlugin;
import net.slqmy.block_warriors_plugin.enums.Team;
import net.slqmy.block_warriors_plugin.types.AbstractGame;
import net.slqmy.block_warriors_plugin.types.Arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class SlimeShooterGame extends AbstractGame {
	private final BlockWarriorsPlugin plugin;
	private final List<Slime> slimes = new ArrayList<>();
	private final HashMap<Team, Integer> points = new HashMap<>();
	private BukkitTask slimeSpawnerTask;

	public SlimeShooterGame(@NotNull BlockWarriorsPlugin plugin, @NotNull Arena arena) {
		super(
						plugin,
						arena
		);

		this.plugin = plugin;
	}

	public List<Slime> getSlimes() {
		return slimes;
	}

	@Override
	public void onStart() {
		arena.sendTitle(ChatColor.GREEN.toString() + ChatColor.BOLD + "GO!");
		arena.sendMessage(ChatColor.GREEN + "Whichever team reaches 100 points first wins!\n" + ChatColor.DARK_GRAY + "| " + ChatColor.GRAY + "Shooting large slimes gives more points!");

		for (final UUID uuid : arena.getPlayers()) {
			final Player player = Bukkit.getPlayer(uuid);
			assert player != null;

			player.getInventory().addItem(
							new ItemStack(Material.BOW),
							new ItemStack(Material.ARROW, 64)
			);

			points.put(arena.getTeam(player), 0);

			player.closeInventory();
		}

		final Location spawnLocation = arena.getSpawnLocation().clone().add(0, 6, 0);
		final World spawnWorld = spawnLocation.getWorld();

		assert spawnWorld != null;

		slimeSpawnerTask = Bukkit.getScheduler().runTaskTimer(
						plugin,
						() -> {
							final Location slimeSpawnLocation = spawnLocation.clone().add(Math.random() * 12 - 6, 0, Math.random() * 12 - 6);

							final Slime slime = (Slime) spawnWorld.spawnEntity(
											slimeSpawnLocation,
											EntityType.SLIME
							);

							slime.setAI(false);

							slimes.add(slime);
						},
						25,
						22L / arena.getPlayers().size()
		);

		tasks.add(slimeSpawnerTask);
	}

	private void givePoints(@NotNull final Player player, final int pointsEarned) {
		final Team team = arena.getTeam(player);
		final int newPoints = points.get(team) + pointsEarned;

		if (newPoints >= 100) {
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

			for (final Slime slime : slimes) {
				slime.remove();
			}

			slimeSpawnerTask.cancel();
			arena.reset(true);
		}

		player.sendMessage(ChatColor.YELLOW + "+" + pointsEarned + ChatColor.GREEN + " Point" + (pointsEarned == 1 ? "" : "s"));
		points.replace(team, newPoints);
	}

	@EventHandler
	public void onEntityDamageByEntity(@NotNull final EntityDamageByEntityEvent event) {
		if (isArenaPlayer(event.getEntity()) && isArenaPlayer(event.getDamager())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onProjectileHit(@NotNull final ProjectileHitEvent event) {
		final Projectile projectile = event.getEntity();

		if (projectile.getShooter() instanceof final Player shooter && isPlaying(shooter)) {
			if (event.getHitEntity() instanceof final Slime slime) {
				final int pointsEarned = slime.getSize() * 2;

				slime.setHealth(0);
				slime.setSize(1);

				givePoints(shooter, pointsEarned);
			} else {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityPickupItem(@NotNull final EntityPickupItemEvent event) {
		if (event.getEntity() instanceof final Player player && isPlaying(player)) {
			event.getItem().remove();

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
