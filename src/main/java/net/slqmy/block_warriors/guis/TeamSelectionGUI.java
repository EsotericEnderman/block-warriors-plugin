package net.slqmy.block_warriors.guis;

import net.slqmy.block_warriors.enums.Team;
import net.slqmy.block_warriors.types.Arena;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public final class TeamSelectionGUI {
	public TeamSelectionGUI(@NotNull final Arena arena, @NotNull final Player player) {
		final Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Team Selection");

		for (final Team team : Team.values()) {
			final ItemStack icon = new ItemStack(team.getIconMaterial());

			final ItemMeta meta = icon.getItemMeta();
			assert meta != null;
			meta.setLocalizedName(team.name());

			meta.setDisplayName(team.getDisplayName() + ChatColor.RESET + ChatColor.GRAY + " (" + ChatColor.YELLOW
							+ arena.getTeamPlayerCount(team) + ChatColor.GRAY + " players)");

			icon.setItemMeta(meta);

			inventory.addItem(icon);
		}

		player.openInventory(inventory);
	}
}
