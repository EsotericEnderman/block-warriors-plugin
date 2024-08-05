package net.slqmy.block_warriors_plugin.games.one_in_the_chamber;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;

import net.slqmy.block_warriors_plugin.BlockWarriorsPlugin;
import net.slqmy.block_warriors_plugin.enums.GameState;
import net.slqmy.block_warriors_plugin.enums.Team;
import net.slqmy.block_warriors_plugin.types.AbstractGame;
import net.slqmy.block_warriors_plugin.types.Arena;
import net.slqmy.block_warriors_plugin.utility.DebugUtility;
import net.slqmy.block_warriors_plugin.utility.PacketUtility;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public final class OneInTheChamberGame extends AbstractGame {
	private final BlockWarriorsPlugin plugin;
	private final HashMap<Team, Integer> kills = new HashMap<>();

	public OneInTheChamberGame(
					@NotNull BlockWarriorsPlugin plugin,
					@NotNull Arena arena
	) {
		super(plugin, arena);

		this.plugin = plugin;
	}

	@Override
	public void onStart() {
		for (final UUID uuid : arena.getPlayers()) {
			final Player player = Bukkit.getPlayer(uuid);
			assert player != null;

			player.sendMessage(ChatColor.YELLOW + "Go! First team to " + ChatColor.UNDERLINE + "25" + ChatColor.YELLOW + " kills wins!");

			final PlayerInventory inventory = player.getInventory();

			inventory.addItem(new ItemStack(Material.IRON_SWORD));
			inventory.addItem(new ItemStack(Material.BOW));
			inventory.setItem(9, new ItemStack(Material.ARROW));

			kills.put(arena.getTeam(player), 0);
		}
	}

	private void givePoint(@NotNull final Player player) {
		final Team playerTeam = arena.getTeam(player);
		final int killCount = kills.get(playerTeam) + 1;

		if (killCount == 25) {
			arena.sendMessage(
							ChatColor.DARK_GRAY + " \n| " + ChatColor.GOLD + "Team " + playerTeam.getDisplayName() + ChatColor.GOLD
											+ " has won the game! Thank you for playing.\n "
			);

			for (final UUID uuid : arena.getPlayers()) {
				final Player currentPlayer = Bukkit.getPlayer(uuid);
				assert currentPlayer != null;

				currentPlayer.sendTitle(
								arena.getTeam(currentPlayer) == playerTeam ? ChatColor.GOLD.toString() + ChatColor.BOLD + "VICTORY!" : ChatColor.RED.toString() + ChatColor.BOLD + "GAME OVER!", "", 10, 10, 10
				);

				arena.reset(true);
				return;
			}
		}

		player.sendMessage(ChatColor.YELLOW + "+1" + ChatColor.GREEN + " Kill");
		kills.replace(playerTeam, killCount);
	}

	@EventHandler
	public void onPlayerInteractEntity(@NotNull final PlayerInteractEntityEvent event) {
		DebugUtility.log("Interact event heard");

		DebugUtility.log("Is arena player? " + isArenaPlayer(event.getPlayer()));
		DebugUtility.log("Game state? " + arena.getState());

		if (isArenaPlayer(event.getPlayer()) && arena.getState() != GameState.PLAYING) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockBreak(@NotNull final BlockBreakEvent event) {
		if (isArenaPlayer(event.getPlayer())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(@NotNull final EntityDamageByEntityEvent event) {
		DebugUtility.log("Entity damage event heard");

		DebugUtility.log("Is the hurt entity an arena player? " + isArenaPlayer(event.getEntity()));
		DebugUtility.log("Is the attacking player an arena player? " + isArenaPlayer(event.getDamager()));

		if (arena.getState() != GameState.PLAYING && isArenaPlayer(event.getEntity()) && isArenaPlayer(event.getDamager())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onProjectileHit(@NotNull final ProjectileHitEvent event) {
		final Entity hit = event.getHitEntity();

		DebugUtility.log("Projectile hit event heard");
		DebugUtility.log("Is hit player an arena player? " + isArenaPlayer(hit));

		if (!isArenaPlayer(hit)) {
			return;
		}

		assert hit != null;

		final Projectile projectile = event.getEntity();
		final ProjectileSource attacker = projectile.getShooter();

		DebugUtility.log("Is the attacker playing? " + isPlaying(attacker));

		if (isPlaying(attacker)) {
			assert attacker != null;
			if (!attacker.equals(hit)) {
				final Player hitPlayer = (Player) hit;
				final Player attackingPlayer = (Player) attacker;

				Bukkit.getScheduler().runTaskLater(plugin, () -> {
					hitPlayer.setHealth(0);
				}, 1);

				onPlayerDeath(
								new PlayerDeathEvent(
												hitPlayer,
												Arrays.stream(hitPlayer.getInventory().getContents()).toList(),
												0,
												arena.getTeam(hitPlayer).getDisplayName() + ChatColor.GRAY + " "
																+ hitPlayer.getName() + " was killed by " + arena.getTeam(attackingPlayer).getDisplayName()
																+ attackingPlayer.getName() + ChatColor.GRAY + "!"
								)
				);

				projectile.remove();
			} else {
				event.setCancelled(true);
			}
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

				givePoint(killer);

				final Inventory killerInventory = killer.getInventory();

				if (killerInventory.contains(Material.ARROW)) {
					killerInventory.addItem(new ItemStack(Material.ARROW));
				} else {
					killerInventory.setItem(9, new ItemStack(Material.ARROW));
				}
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

			final Inventory playerInventory = player.getInventory();

			playerInventory.setItem(9, new ItemStack(Material.ARROW));
			playerInventory.addItem(
							new ItemStack(Material.IRON_SWORD),
							new ItemStack(Material.BOW)
			);
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
