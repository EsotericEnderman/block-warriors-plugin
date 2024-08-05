package net.slqmy.block_warriors_plugin.utility;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import net.slqmy.block_warriors_plugin.utility.types.ApplicableEnchantment;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public final class ItemUtility {
	/**
	 * Creates a custom item with the specified properties.
	 *
	 * @param material     The material of the item.
	 * @param count        The quantity of the item in the stack.
	 * @param displayName  The display name of the item.
	 * @param lore         The lore of the item (each line should be separated by
	 *                     '\n').
	 * @param enchantments The applicable enchantments for the item.
	 * @param modifiers    The attribute modifiers for the item.
	 * @return The created custom item.
	 */
	public static @NotNull ItemStack createCustomItem(@NotNull final Material material, final int count,
	                                                  @NotNull final String displayName, @Nullable final String lore,
	                                                  final ApplicableEnchantment @NotNull [] enchantments,
	                                                  @NotNull final Multimap<Attribute, AttributeModifier> modifiers) {
		final ItemStack item = new ItemStack(material, count);

		final ItemMeta meta = item.getItemMeta();
		assert meta != null;

		meta.setDisplayName(ChatColor.RESET + displayName);

		meta.setLore(lore == null ? null
						: Arrays.stream(lore.split("\n")).map((final String line) -> ChatColor.RESET + line).toList());

		meta.setAttributeModifiers(modifiers);

		for (final ApplicableEnchantment enchantment : enchantments) {
			meta.addEnchant(enchantment.getEnchantment(), enchantment.getLevel(), enchantment.ignoresLevelRestriction());
		}

		item.setItemMeta(meta);

		return item;
	}

	/**
	 * Creates a custom item with the specified properties and no attribute
	 * modifiers.
	 *
	 * @param material     The material of the item.
	 * @param count        The quantity of the item in the stack.
	 * @param displayName  The display name of the item.
	 * @param lore         The lore of the item (each line should be separated by
	 *                     '\n').
	 * @param enchantments The applicable enchantments for the item.
	 * @return The created custom item.
	 */
	public static @NotNull ItemStack createCustomItem(@NotNull final Material material, final int count,
	                                                  @NotNull final String displayName, @Nullable final String lore,
	                                                  final ApplicableEnchantment @NotNull [] enchantments) {
		return createCustomItem(material, count, displayName, lore, enchantments, ArrayListMultimap.create());
	}

	/**
	 * Creates a custom item with the specified properties, no attribute modifiers,
	 * and no enchantments.
	 *
	 * @param material    The material of the item.
	 * @param count       The quantity of the item in the stack.
	 * @param displayName The display name of the item.
	 * @param lore        The lore of the item (each line should be separated by
	 *                    '\n').
	 * @return The created custom item.
	 */
	public static @NotNull ItemStack createCustomItem(@NotNull final Material material, final int count,
	                                                  @NotNull final String displayName, @Nullable final String lore) {
		return createCustomItem(material, count, displayName, lore, new ApplicableEnchantment[]{});
	}

	/**
	 * Creates a custom item with the specified properties, no attribute modifiers,
	 * no lore, and no enchantments.
	 *
	 * @param material    The material of the item.
	 * @param count       The quantity of the item in the stack.
	 * @param displayName The display name of the item.
	 * @return The created custom item.
	 */
	public static @NotNull ItemStack createCustomItem(@NotNull final Material material, final int count,
	                                                  @NotNull final String displayName) {
		return createCustomItem(material, count, displayName, null);
	}

	/**
	 * Creates one of a custom item with the specified properties, no lore, no
	 * attribute modifiers, and no enchantments.
	 *
	 * @param material    The material of the item.
	 * @param displayName The display name of the item.
	 * @return The created custom item.
	 */
	public static @NotNull ItemStack createCustomItem(@NotNull final Material material,
	                                                  @NotNull final String displayName) {
		return createCustomItem(material, 1, displayName);
	}

	/**
	 * Creates one of a custom item with the specified properties, no attribute
	 * modifiers, and no enchantments.
	 *
	 * @param material    The material of the item.
	 * @param displayName The display name of the item.
	 * @param lore        The lore of the item (each line should be separated by
	 *                    '\n').
	 * @return The created custom item.
	 */
	public static @NotNull ItemStack createCustomItem(@NotNull final Material material, @NotNull final String displayName,
	                                                  @Nullable final String lore) {
		return createCustomItem(material, 1, displayName, lore);
	}
}
