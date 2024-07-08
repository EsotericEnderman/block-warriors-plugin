package net.slqmy.block_warriors.games.cactus_castle;

import net.slqmy.block_warriors.BlockWarriors;
import net.slqmy.block_warriors.enums.GameState;
import net.slqmy.block_warriors.enums.Team;
import net.slqmy.block_warriors.types.AbstractGame;
import net.slqmy.block_warriors.types.Arena;
import net.slqmy.block_warriors.utility.ConfigurationUtility;
import net.slqmy.block_warriors.utility.DebugUtility;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class CactusCastleGame extends AbstractGame {

	public CactusCastleGame(@NotNull BlockWarriors plugin, @NotNull Arena arena) {
		super(plugin, arena);
	}

	@Override
	public void onStart() {
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

			player.addPotionEffect(
							new PotionEffect(
											PotionEffectType.SPEED,
											PotionEffect.INFINITE_DURATION,
											1,
											true,
											true,
											true
							)
			);

			player.closeInventory();
		}
	}

	@EventHandler
	private void onPlayerDeath(@NotNull final PlayerDeathEvent event) {
		DebugUtility.log("Player death event heard.");
		DebugUtility.log("Is the dead player an arena player? " + isArenaPlayer(event.getEntity()));

		final Player deadPlayer = event.getEntity();

		if (!isArenaPlayer(deadPlayer)) {
			return;
		}

		final Team deadPlayerTeam = arena.getTeam(deadPlayer);

		arena.sendMessage(deadPlayerTeam.getDisplayName() + " " + ChatColor.YELLOW + deadPlayer.getName() + " has been eliminated!");

		arena.getPlayers().remove(deadPlayer.getUniqueId());

		boolean oneTeamRemaining = true;

		for (final UUID uuid : arena.getPlayers()) {
			if (arena.getTeam(uuid) != arena.getTeam(arena.getPlayers().get(0))) {
				oneTeamRemaining = false;
				break;
			}
		}

		if (oneTeamRemaining) {
			final Team winningTeam = arena.getTeam(arena.getPlayers().get(0));

			arena.sendMessage(winningTeam.getDisplayName() + ChatColor.GREEN + " team has won! Thank you for playing.");

			for (final UUID uuid : arena.getPlayers()) {
				final Player currentPlayer = Bukkit.getPlayer(uuid);
				assert currentPlayer != null;

				final Team currentTeam = arena.getTeam(currentPlayer);

				currentPlayer.sendTitle(currentTeam == winningTeam ? ChatColor.GOLD.toString() + ChatColor.BOLD + "VICTORY!"
								: ChatColor.RED.toString() + ChatColor.BOLD + "GAME OVER!", "", 10, 10, 10);
			}

			arena.reset(true);
		}
	}

	@EventHandler
	public void onPlayerRespawn(@NotNull final PlayerRespawnEvent event) {
		DebugUtility.log("Player respawn event heard.");

		final Player player = event.getPlayer();

		DebugUtility.log("Is respawning player an arena player? " + isArenaPlayer(player));

		if (isArenaPlayer(player)) {
			event.setRespawnLocation(ConfigurationUtility.getLobbySpawn());
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

	@EventHandler
	public void onEntityPickupItem(@NotNull final EntityPickupItemEvent event) {
		if (event.getEntity() instanceof final Player player && isPlaying(player)) {
			event.getItem().remove();

			event.setCancelled(true);
		}
	}
}
