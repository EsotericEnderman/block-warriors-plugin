package net.slqmy.block_muncher.managers;

import net.slqmy.block_muncher.BlockMuncher;
import net.slqmy.block_muncher.types.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ArenaManager {
	private final List<Arena> arenas = new ArrayList<>();

	public ArenaManager(@NotNull final BlockMuncher plugin) {
		final FileConfiguration config = plugin.getConfig();
		final ConfigurationSection arenasList = config.getConfigurationSection("arenas");
		assert arenasList != null;

		for (final String arenaKey : arenasList.getKeys(false)) {
			final String worldName = config.getString("arenas." + arenaKey + ".world-name");
			assert worldName != null;

			arenas.add(
					new Arena(
							plugin,
							Integer.parseInt(arenaKey),
							new Location(
									Bukkit.getWorld(worldName),
									config.getDouble("arenas." + arenaKey + ".x"),
									config.getDouble("arenas." + arenaKey + ".y"),
									config.getDouble("arenas." + arenaKey + ".z"),
									(float) config.getDouble("arenas." + arenaKey + ".yaw"),
									(float) config.getDouble("arenas." + arenaKey + ".pitch")

							)));
		}
	}

	public List<Arena> getArenas() {
		return arenas;
	}

	public @Nullable Arena getArena(@NotNull final UUID uuid) {
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
}
