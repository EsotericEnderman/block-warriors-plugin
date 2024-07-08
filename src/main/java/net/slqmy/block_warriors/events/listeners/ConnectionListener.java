package net.slqmy.block_warriors.events.listeners;

import net.slqmy.block_warriors.BlockWarriors;
import net.slqmy.block_warriors.types.Arena;
import net.slqmy.block_warriors.utility.ConfigurationUtility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public final class ConnectionListener implements Listener {
	private final BlockWarriors plugin;

	public ConnectionListener(@NotNull final BlockWarriors plugin) {
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
