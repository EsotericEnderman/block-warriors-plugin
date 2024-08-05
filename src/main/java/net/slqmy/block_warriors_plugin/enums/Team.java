package net.slqmy.block_warriors_plugin.enums;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public enum Team {
	RED(ChatColor.RED + "Red", Material.RED_WOOL),
	BLUE(ChatColor.BLUE + "Blue", Material.BLUE_WOOL),
	GREEN(ChatColor.GREEN + "Green", Material.LIME_WOOL);

	private final String displayName;
	private final Material icon;

	Team(@NotNull final String displayName, @NotNull final Material icon) {
		this.displayName = displayName;
		this.icon = icon;
	}

	public String getDisplayName() {
		return displayName;
	}

	public Material getIconMaterial() {
		return icon;
	}
}
