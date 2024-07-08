package net.slqmy.block_warriors.guis;

import net.slqmy.block_warriors.enums.KitType;
import net.slqmy.block_warriors.types.Arena;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class KitSelectionGUI {
	public KitSelectionGUI(@NotNull final Arena arena, @NotNull final Player player) {
		final Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Kit Selection");

		final List<KitType> gameKits = arena.getGame().getKits();

		for (final KitType kit : KitType.values()) {
			if (gameKits.contains(kit)) {
				final ItemStack icon = kit.getIcon();

				final ItemMeta meta = icon.getItemMeta();
				assert meta != null;
				meta.setLocalizedName(kit.name());

				icon.setItemMeta(meta);

				inventory.addItem(icon);
			}
		}

		player.openInventory(inventory);
	}
}
