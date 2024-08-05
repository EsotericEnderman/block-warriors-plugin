package net.slqmy.block_warriors_plugin.utility.types;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SignLocation extends Location {
	private final BlockFace facingDirection;

	public SignLocation(@Nullable World world, double x, double y, double z, @NotNull final BlockFace direction) {
		super(world, x, y, z);
		this.facingDirection = direction;
	}

	public BlockFace getFacingDirection() {
		return facingDirection;
	}
}
