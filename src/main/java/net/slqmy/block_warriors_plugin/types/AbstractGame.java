package net.slqmy.block_warriors_plugin.types;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.slqmy.block_warriors_plugin.BlockWarriorsPlugin;
import net.slqmy.block_warriors_plugin.enums.GameState;
import net.slqmy.block_warriors_plugin.enums.KitType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public abstract class AbstractGame implements Listener {
	protected final Arena arena;
	protected final List<KitType> kits;

	protected final List<BukkitTask> tasks = new ArrayList<>();

	public AbstractGame(@NotNull final BlockWarriorsPlugin plugin, @NotNull final Arena arena, final KitType @NotNull [] kits) {
		this.arena = arena;
		this.kits = Arrays.asList(kits);

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	public AbstractGame(@NotNull final BlockWarriorsPlugin plugin, @NotNull final Arena arena) {
		this(plugin, arena, new KitType[]{});
	}

	public void start() {
		arena.setState(GameState.PLAYING);

		for (final UUID uuid : arena.getPlayers()) {
			final Player player = Bukkit.getPlayer(uuid);
			assert player != null;

			player.closeInventory();
		}

		onStart();
	}

	public abstract void onStart();

	public void end() {
		HandlerList.unregisterAll(this);

		for (final BukkitTask task : tasks) {
			task.cancel();
		}
	}

	protected boolean isArenaPlayer(@Nullable final Object entity) {
		return entity instanceof Player && arena.getPlayers().contains(((Player) entity).getUniqueId());
	}

	protected boolean isPlaying(@Nullable final Object entity) {
		return isArenaPlayer(entity) && arena.getState() == GameState.PLAYING;
	}

	public List<KitType> getKits() {
		return kits;
	}
}
