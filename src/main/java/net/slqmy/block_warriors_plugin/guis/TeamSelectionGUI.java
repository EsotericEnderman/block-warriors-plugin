package net.slqmy.block_warriors_plugin.guis;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import net.slqmy.block_warriors_plugin.BlockWarriorsPlugin;
import net.slqmy.block_warriors_plugin.enums.Team;
import net.slqmy.block_warriors_plugin.types.Arena;

public final class TeamSelectionGUI {
	public TeamSelectionGUI(@NotNull final BlockWarriorsPlugin plugin, @NotNull final Arena arena, @NotNull final Player player) {
		final Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Team Selection");

		for (final Team team : Team.values()) {
			final ItemStack icon = new ItemStack(team.getIconMaterial());

			final ItemMeta meta = icon.getItemMeta();
			assert meta != null;
			PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
			dataContainer.set(plugin.getTeamNameKey(), PersistentDataType.STRING, team.name());

			meta.setDisplayName(team.getDisplayName() + ChatColor.RESET + ChatColor.GRAY + " (" + ChatColor.YELLOW
							+ arena.getTeamPlayerCount(team) + ChatColor.GRAY + " players)");

			icon.setItemMeta(meta);

			inventory.addItem(icon);
		}

		player.openInventory(inventory);
	}
}
