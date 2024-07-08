package net.slqmy.block_warriors;

import net.slqmy.block_warriors.commands.ArenaCommand;
import net.slqmy.block_warriors.events.listeners.ConnectionListener;
import net.slqmy.block_warriors.events.listeners.GameListener;
import net.slqmy.block_warriors.games.avoid_the_rain.AvoidTheRainGame;
import net.slqmy.block_warriors.games.slime_shooter.SlimeShooterGame;
import net.slqmy.block_warriors.managers.ArenaManager;
import net.slqmy.block_warriors.types.Arena;
import net.slqmy.block_warriors.utility.ConfigurationUtility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Slime;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public final class BlockWarriors extends JavaPlugin {
	private ArenaManager arenaManager;

	@Override
	public void onEnable() {
		// Setting up the configuration must happen before setting up the arena manager,
		// as the arena manager relies heavily on the config.yml file.
		ConfigurationUtility.setUpConfig(this);

		arenaManager = new ArenaManager(this);

		final PluginManager pluginManager = Bukkit.getPluginManager();

		pluginManager.registerEvents(new ConnectionListener(this), this);
		pluginManager.registerEvents(new GameListener(this), this);

		new ArenaCommand(this);
	}

	@Override
	public void onDisable() {
		if (arenaManager == null) {
			return;
		}

		for (final Arena arena : arenaManager.getArenas()) {
			for (final UUID uuid : arena.getPlayers()) {
				final Player player = Bukkit.getPlayer(uuid);
				assert player != null;

				player.getInventory().clear();
			}

			if (arena.getGameName().equals("slime-shooter")) {
				for (final Slime slime : ((SlimeShooterGame) arena.getGame()).getSlimes()) {
					slime.remove();
				}
			} else if (arena.getGameName().equals("avoid-the-rain")) {
				for (final Projectile projectile : ((AvoidTheRainGame) arena.getGame()).getProjectiles()) {
					projectile.remove();
				}
			}
		}
	}

	public ArenaManager getArenaManager() {
		return arenaManager;
	}
}
