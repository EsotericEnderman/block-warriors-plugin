package dev.enderman.minecraft.plugins.games.blockwarriors.games.gladiators.kits;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import dev.enderman.minecraft.plugins.games.blockwarriors.BlockWarriorsPlugin;
import dev.enderman.minecraft.plugins.games.blockwarriors.enums.KitType;
import dev.enderman.minecraft.plugins.games.blockwarriors.types.AbstractKit;
import dev.enderman.minecraft.plugins.games.blockwarriors.utility.ItemUtility;

public final class RetiariusKit extends AbstractKit {
	public RetiariusKit(@NotNull final BlockWarriorsPlugin plugin, @NotNull final Player kitUser) {
		super(
						plugin,
						KitType.RETIARIUS,
						kitUser
		);
	}

	@Override
	public void activateKit(@NotNull final Player player) {
		player.addPotionEffect(
						new PotionEffect(
										PotionEffectType.STRENGTH, 60, 0, true, true, true
						)
		);

		player.addPotionEffect(
						new PotionEffect(
										PotionEffectType.SLOWNESS, 200, 1, true, true, true
						)
		);
	}

	@Override
	public void giveItems(@NotNull Player player) {
		final PlayerInventory playerInventory = player.getInventory();

		playerInventory.addItem(
						ItemUtility.createCustomItem(
										Material.TRIDENT,
										ChatColor.DARK_AQUA + "Retiarius' Trident"
						)
		);

		playerInventory.setChestplate(
						ItemUtility.createCustomItem(
										Material.GOLDEN_CHESTPLATE,
										ChatColor.YELLOW + "Retiarius' Chestplate"
						)
		);

		playerInventory.setBoots(
						ItemUtility.createCustomItem(
										Material.IRON_BOOTS,
										ChatColor.WHITE + "Retiarius' Boots"
						)
		);
	}
}
