package dev.enderman.minecraft.plugins.games.blockwarriors.event.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import dev.enderman.minecraft.plugins.games.blockwarriors.BlockWarriorsPlugin;
import dev.enderman.minecraft.plugins.games.blockwarriors.types.Arena;
import dev.enderman.minecraft.plugins.games.blockwarriors.utility.ConfigurationUtility;

public final class ConnectionListener implements Listener {
	private final BlockWarriorsPlugin plugin;

	public ConnectionListener(@NotNull final BlockWarriorsPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(@NotNull final PlayerJoinEvent event) {
		event.getPlayer().teleport(ConfigurationUtility.getLobbySpawn());
	}

	@EventHandler
	public void onPlayerQuit(@NotNull final PlayerQuitEvent event) {
		final Player player = event.getPlayer();

		final Arena arena = plugin.getArenaManager().getArena(player);

		if (arena != null) {
			arena.removePlayer(player);
		}
	}
}
