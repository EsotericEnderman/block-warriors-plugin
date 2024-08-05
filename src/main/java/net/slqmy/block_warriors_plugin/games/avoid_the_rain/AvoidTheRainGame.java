package net.slqmy.block_warriors_plugin.games.avoid_the_rain;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import net.slqmy.block_warriors_plugin.BlockWarriorsPlugin;
import net.slqmy.block_warriors_plugin.enums.Team;
import net.slqmy.block_warriors_plugin.types.AbstractGame;
import net.slqmy.block_warriors_plugin.types.Arena;
import net.slqmy.block_warriors_plugin.utility.ConfigurationUtility;
import net.slqmy.block_warriors_plugin.utility.MathsUtility;

import java.util.*;

public final class AvoidTheRainGame extends AbstractGame {
	private static final Random RANDOM = new Random();

	private final BlockWarriorsPlugin plugin;
	private final List<Projectile> projectiles = new ArrayList<>();
	private final HashMap<Team, Integer> points = new HashMap<>();
	private BukkitTask spawnProjectilesTask;

	public AvoidTheRainGame(@NotNull BlockWarriorsPlugin plugin, @NotNull Arena arena) {
		super(plugin, arena);

		this.plugin = plugin;
	}

	public List<Projectile> getProjectiles() {
		return projectiles;
	}

	@Override
	public void onStart() {
		arena.sendTitle(ChatColor.GREEN.toString() + ChatColor.BOLD + "GO!");
		arena.sendMessage(ChatColor.GREEN + "Whichever team is the last one standing wins!");

		for (final UUID uuid : arena.getPlayers()) {
			final Player player = Bukkit.getPlayer(uuid);
			assert player != null;

			points.put(arena.getTeam(player), 0);

			player.closeInventory();
		}

		final Location spawnLocation = arena.getSpawnLocation().clone().add(0, 60, 0);
		final World spawnWorld = spawnLocation.getWorld();

		assert spawnWorld != null;

		spawnProjectilesTask = Bukkit.getScheduler().runTaskTimer(
						plugin,
						() -> {
							for (int i = 1; i < arena.getPlayers().size() * 3; i++) {
								final Location projectileSpawnLocation = spawnLocation.clone().add(RANDOM.nextDouble() * 12 - 6, 0, RANDOM.nextDouble() * 12 - 6);

								final EntityType spawnedEntity = MathsUtility.chooseRandom(
												EntityType.ARROW,
												EntityType.EGG,
												EntityType.ENDER_PEARL,
												EntityType.FIREBALL,
												EntityType.FIREWORK,
												EntityType.SPLASH_POTION,
												EntityType.LLAMA_SPIT,
												EntityType.SHULKER_BULLET,
												EntityType.SNOWBALL,
												EntityType.SPECTRAL_ARROW,
												EntityType.THROWN_EXP_BOTTLE,
												EntityType.TRIDENT
								);

								final Projectile projectile = (Projectile) spawnWorld.spawnEntity(
												projectileSpawnLocation,
												spawnedEntity
								);

								if (projectile instanceof Arrow || projectile instanceof SpectralArrow || projectile instanceof Trident) {
									((AbstractArrow) projectile).setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
								} else if (projectile instanceof final ThrownPotion potionProjectile) {
									final ItemStack potion = new ItemStack(MathsUtility.chooseRandom(Material.SPLASH_POTION, Material.LINGERING_POTION));

									final PotionMeta meta = (PotionMeta) potion.getItemMeta();
									assert meta != null;

									meta.addCustomEffect(
													new PotionEffect(
																	MathsUtility.chooseRandom(PotionEffectType.values()),
																	RANDOM.nextInt(100, 301),
																	RANDOM.nextInt(0, 5),
																	true,
																	true,
																	true
													),
													false
									);

									potion.setItemMeta(meta);

									potionProjectile.setItem(potion);
								} else if (projectile instanceof final Firework fireworkProjectile) {
									final ItemStack firework = new ItemStack(Material.FIREWORK_ROCKET);

									final FireworkMeta fireworkMeta = (FireworkMeta) firework.getItemMeta();
									assert fireworkMeta != null;

									fireworkMeta.setPower(RANDOM.nextInt(5, 15));
									fireworkMeta.addEffects(
													FireworkEffect
																	.builder()
																	.withColor(Color.fromARGB(RANDOM.nextInt(256), RANDOM.nextInt(256), RANDOM.nextInt(256), RANDOM.nextInt(256)))
																	.withColor(Color.fromARGB(RANDOM.nextInt(256), RANDOM.nextInt(256), RANDOM.nextInt(256), RANDOM.nextInt(256)))
																	.with(MathsUtility.chooseRandom(FireworkEffect.Type.values()))
																	.withTrail()
																	.withFlicker()
																	.withFade(Color.fromARGB(RANDOM.nextInt(256), RANDOM.nextInt(256), RANDOM.nextInt(256), RANDOM.nextInt(256)))
																	.build()
									);

									fireworkProjectile.setFireworkMeta(fireworkMeta);
									fireworkProjectile.setShotAtAngle(true);

									fireworkProjectile.setVelocity(new Vector(0, -1.25F, 0));
								} else if (projectile instanceof final Fireball fireballProjectile) {
									fireballProjectile.setDirection(new Vector(0, -1, 0));
								}

								projectiles.add(projectile);
							}
						},
						25,
						16L / arena.getPlayers().size()
		);

		tasks.add(spawnProjectilesTask);
	}

	@EventHandler
	public void onEntityDamageByEntity(@NotNull final EntityDamageByEntityEvent event) {
		if (isArenaPlayer(event.getEntity()) && isArenaPlayer(event.getDamager())) {
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
	public void onProjectileHit(@NotNull final ProjectileHitEvent event) {
		final Projectile projectile = event.getEntity();

		if (projectiles.contains(projectile)) {
			projectiles.remove(projectile);

			Bukkit.getScheduler().runTaskLater(
							plugin,
							projectile::remove,
							15
			);
		}
	}

	@EventHandler
	public void onPlayerEggThrow(@NotNull final PlayerEggThrowEvent event) {
		if (projectiles.contains(event.getEgg())) {
			event.setHatching(false);
		}
	}

	@EventHandler
	public void onPlayerDeath(@NotNull final PlayerDeathEvent event) {
		final Player deadPlayer = event.getEntity();

		if (!isPlaying(deadPlayer)) {
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

			spawnProjectilesTask.cancel();

			arena.reset(true);
		}
	}

	@EventHandler
	public void onPlayerRespawn(@NotNull final PlayerRespawnEvent event) {
		if (isPlaying(event.getPlayer())) {
			event.setRespawnLocation(ConfigurationUtility.getLobbySpawn());
		}
	}
}
