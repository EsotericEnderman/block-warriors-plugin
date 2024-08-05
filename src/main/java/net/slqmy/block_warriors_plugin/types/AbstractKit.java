package net.slqmy.block_warriors_plugin.types;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import net.slqmy.block_warriors_plugin.BlockWarriorsPlugin;
import net.slqmy.block_warriors_plugin.enums.KitType;

import java.util.UUID;

public abstract class AbstractKit implements Listener {
	protected final KitType kitType;
	protected final UUID kitUser;

	public AbstractKit(@NotNull final BlockWarriorsPlugin plugin, @NotNull final KitType kitType,
	                   @NotNull final Player kitUser) {
		this.kitType = kitType;
		this.kitUser = kitUser.getUniqueId();

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	public void remove() {
		HandlerList.unregisterAll(this);
	}

	public abstract void activateKit(@NotNull final Player player);

	public abstract void giveItems(@NotNull final Player player);

	public KitType getKitType() {
		return kitType;
	}

	public UUID getKitUser() {
		return kitUser;
	}
}
