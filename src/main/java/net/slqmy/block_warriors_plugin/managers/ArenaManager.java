package net.slqmy.block_warriors_plugin.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.slqmy.block_warriors_plugin.BlockWarriorsPlugin;
import net.slqmy.block_warriors_plugin.types.Arena;
import net.slqmy.block_warriors_plugin.utility.types.SignLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ArenaManager {

	private final List<Arena> arenas = new ArrayList<>();

	public ArenaManager(@NotNull final BlockWarriorsPlugin plugin) {
		final FileConfiguration config = plugin.getConfig();
		final ConfigurationSection arenasList = config.getConfigurationSection("arenas");
		assert arenasList != null;

		for (final String arenaKey : arenasList.getKeys(false).stream().toList()) {
			final String spawnLocationWorldName = config.getString("arenas." + arenaKey + ".spawn-location.world-name");
			assert spawnLocationWorldName != null;

			final String signLocationWorldName = config.getString("arenas." + arenaKey + ".sign-location.world-name");
			assert signLocationWorldName != null;

			final String signFacingDirection = config.getString("arenas." + arenaKey + ".sign-location.facing");
			assert signFacingDirection != null;

			final String gameName = config.getString("arenas." + arenaKey + ".game");
			assert gameName != null;

			arenas.add(
							new Arena(
											plugin,
											Integer.parseInt(arenaKey),
											new Location(
															Bukkit.getWorld(spawnLocationWorldName),
															config.getDouble("arenas." + arenaKey + ".spawn-location.x"),
															config.getDouble("arenas." + arenaKey + ".spawn-location.y"),
															config.getDouble("arenas." + arenaKey + ".spawn-location.z"),
															(float) config.getDouble("arenas." + arenaKey + ".spawn-location.yaw"),
															(float) config.getDouble("arenas." + arenaKey + ".spawn-location.pitch")

											),
											new SignLocation(
															Bukkit.getWorld(signLocationWorldName),
															config.getDouble("arenas." + arenaKey + ".sign-location.x"),
															config.getDouble("arenas." + arenaKey + ".sign-location.y"),
															config.getDouble("arenas." + arenaKey + ".sign-location.z"),
															BlockFace.valueOf(signFacingDirection.toUpperCase())
											),
											gameName
							)
			);
		}
	}

	public List<Arena> getArenas() {
		return arenas;
	}

	public @Nullable Arena getArena(@NotNull final Player player) {
		final UUID uuid = player.getUniqueId();

		for (final Arena arena : arenas) {
			if (arena.getPlayers().contains(uuid)) {
				return arena;
			}
		}

		return null;
	}

	public @Nullable Arena getArena(final int id) {
		for (final Arena arena : arenas) {
			if (arena.getID() == id) {
				return arena;
			}
		}

		return null;
	}

	public @Nullable Arena getArena(@NotNull final SignLocation signLocation) {
		for (final Arena arena : arenas) {
			if (arena.getSignLocation().equals(signLocation)) {
				return arena;
			}
		}

		return null;
	}
}
