package net.slqmy.block_warriors.games.block_muncher.kits;

import net.slqmy.block_warriors.BlockWarriors;
import net.slqmy.block_warriors.enums.KitType;
import net.slqmy.block_warriors.types.AbstractKit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public final class VoleKit extends AbstractKit {
	public VoleKit(@NotNull final BlockWarriors plugin, @NotNull final Player kitUser) {
		super(plugin, KitType.VOLE, kitUser);
	}

	@Override
	public void activateKit(@NotNull final Player player) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 250, 0, true, true, true));
	}

	@Override
	public void giveItems(@NotNull Player player) {
		player.getInventory().addItem(new ItemStack(Material.IRON_SHOVEL));
	}

	@EventHandler
	public void onBlockBreak(@NotNull final BlockBreakEvent event) {
		final Player player = event.getPlayer();

		if (kitUser.equals(player.getUniqueId())) {
			player.playSound(player, Sound.ENTITY_PLAYER_BURP, 1, 2);
			player.playSound(player, Sound.BLOCK_GRASS_BREAK, 1, 2);
		}
	}
}
