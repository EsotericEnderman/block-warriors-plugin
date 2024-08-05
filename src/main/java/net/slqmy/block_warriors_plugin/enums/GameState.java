package net.slqmy.block_warriors_plugin.enums;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

public enum GameState {
	WAITING(ChatColor.GREEN + "Waiting for players...", ChatColor.GREEN + "Waiting..."),
	COUNTDOWN(ChatColor.YELLOW + "Game starting!", ChatColor.YELLOW + "Starting!"),
	PLAYING(ChatColor.RED + "Game active.", ChatColor.RED + "Game active.");

	private final String name;
	private final String displayName;

	GameState(@NotNull final String name, @NotNull final String displayName) {
		this.name = name;
		this.displayName = displayName;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}
}
