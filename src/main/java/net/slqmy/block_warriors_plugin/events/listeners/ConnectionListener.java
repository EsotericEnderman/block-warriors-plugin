package net.slqmy.block_warriors_plugin.events.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import net.slqmy.block_warriors_plugin.BlockWarriorsPlugin;
import net.slqmy.block_warriors_plugin.types.Arena;
import net.slqmy.block_warriors_plugin.utility.ConfigurationUtility;

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
