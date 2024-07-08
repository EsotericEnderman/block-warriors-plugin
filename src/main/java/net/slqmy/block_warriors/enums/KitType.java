package net.slqmy.block_warriors.enums;

import net.slqmy.block_warriors.utility.ItemUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public enum KitType {
	MOLE(
					ChatColor.GOLD + "Mole",
					ItemUtility.createCustomItem(
									Material.RABBIT_SPAWN_EGG,
									ChatColor.GOLD + "Mole",
									ChatColor.GRAY + "Moles are small mammals adapted to a subterranean lifestyle.\n" + ChatColor.GRAY
													+ "They have cylindrical bodies, velvety fur, very small, inconspicuous\n" + ChatColor.GRAY
													+ "eyes and ears, reduced hind limbs, and short, powerful forelimbs with\n" + ChatColor.GRAY
													+ "large paws adapted for digging.\n\n"
													+ ChatColor.GRAY + "- " + ChatColor.YELLOW + "Golden Pickaxe\n"
													+ ChatColor.GRAY + "- " + ChatColor.WHITE + "Speed " + ChatColor.BOLD + "III " + ChatColor.GRAY + "(" + ChatColor.YELLOW + "12.5 " + ChatColor.GRAY + "s)"
					)
	),
	VOLE(
					ChatColor.DARK_GRAY + "Vole",
					ItemUtility.createCustomItem(
									Material.SILVERFISH_SPAWN_EGG,
									ChatColor.DARK_GRAY + "Vole",
									ChatColor.GRAY + "Voles are small rodents that are relatives of lemmings and hamsters,\n" + ChatColor.GRAY
													+ "but with a stouter body; a longer, hairy tail; a slightly rounder head;\n" + ChatColor.GRAY
													+ "smaller eyes and ears; and differently formed molars. They are sometimes\n" + ChatColor.GRAY
													+ "known as meadow mice or field mice in North America.\n\n"
													+ ChatColor.GRAY + "- " + ChatColor.WHITE + "Iron Shovel\n"
													+ ChatColor.GRAY + "- " + ChatColor.YELLOW + "Haste " + ChatColor.BOLD + "I " + ChatColor.GRAY + "("
													+ ChatColor.YELLOW + "12.5 " + ChatColor.GRAY + "s)"
					)
	),
	RETIARIUS(
					ChatColor.GOLD + "Retiarius",
					ItemUtility.createCustomItem(
									Material.TRIDENT,
									ChatColor.GOLD + "Retiarius",
									ChatColor.RED + "A fierce, strong and brave warrior from the coliseums of Ancient Rome.\n\n"
													+ ChatColor.GRAY + "- " + ChatColor.AQUA + "Trident\n"
													+ ChatColor.GRAY + "- " + ChatColor.YELLOW + "Gold Chestplate\n"
													+ ChatColor.GRAY + "- " + ChatColor.WHITE + "Iron Boots\n"
													+ ChatColor.GRAY + "- " + ChatColor.RED + "Strength " + ChatColor.BOLD + "I " + ChatColor.GRAY + "(" + ChatColor.YELLOW + "3 " + ChatColor.GRAY + "s)\n"
													+ ChatColor.GRAY + "- Slowness " + ChatColor.BOLD + "II " + ChatColor.GRAY + "(" + ChatColor.YELLOW + "10 " + ChatColor.GRAY + "s)\n"
					)
	),
	MURMILLO(
					ChatColor.RED + "Murmillo",
					ItemUtility.createCustomItem(
									Material.IRON_SWORD,
									ChatColor.RED + "Murmillo",
									ChatColor.GRAY + "An offense-oriented gladiator, impaired in vision by his protective golden helmet.\n\n"
													+ ChatColor.GRAY + "- " + ChatColor.WHITE + "Iron Sword\n"
													+ ChatColor.GRAY + "- " + ChatColor.YELLOW + "Gold Helmet\n"
													+ ChatColor.GRAY + "- " + ChatColor.YELLOW + "Gold Boots\n"
													+ ChatColor.GRAY + "- " + ChatColor.DARK_AQUA + "Shield\n"
													+ ChatColor.GRAY + "- " + ChatColor.BLACK + "Blindness " + ChatColor.BOLD + "I " + ChatColor.GRAY + "(" + ChatColor.YELLOW + "10 " + ChatColor.GRAY + "s)\n"
													+ ChatColor.GRAY + "- " + ChatColor.WHITE + "Speed " + ChatColor.BOLD + "II " + ChatColor.GRAY + "(" + ChatColor.YELLOW + "10 " + ChatColor.GRAY + "s)"
					)
	);

	private final String name;
	private final ItemStack icon;

	KitType(@NotNull final String displayName, @NotNull final ItemStack icon) {
		this.name = displayName;
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public ItemStack getIcon() {
		return icon;
	}
}
