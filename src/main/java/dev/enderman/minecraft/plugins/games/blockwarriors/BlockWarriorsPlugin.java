package dev.enderman.minecraft.plugins.games.blockwarriors;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Slime;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import dev.enderman.minecraft.plugins.games.blockwarriors.commands.ArenaCommand;
import dev.enderman.minecraft.plugins.games.blockwarriors.event.listeners.ConnectionListener;
import dev.enderman.minecraft.plugins.games.blockwarriors.event.listeners.GameListener;
import dev.enderman.minecraft.plugins.games.blockwarriors.games.avoidtherain.AvoidTheRainGame;
import dev.enderman.minecraft.plugins.games.blockwarriors.games.slimeshooter.SlimeShooterGame;
import dev.enderman.minecraft.plugins.games.blockwarriors.managers.ArenaManager;
import dev.enderman.minecraft.plugins.games.blockwarriors.types.Arena;
import dev.enderman.minecraft.plugins.games.blockwarriors.utility.ConfigurationUtility;

import java.util.UUID;

public final class BlockWarriorsPlugin extends JavaPlugin {
	private ArenaManager arenaManager;

	private NamespacedKey teamNameKey = new NamespacedKey(this, "team_name");

	private NamespacedKey kitNameKey = new NamespacedKey(this, "kit_name");

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

	public NamespacedKey getTeamNameKey() {
		return teamNameKey;
	}

	public NamespacedKey getKitNameKey() {
		return kitNameKey;
	}
}
