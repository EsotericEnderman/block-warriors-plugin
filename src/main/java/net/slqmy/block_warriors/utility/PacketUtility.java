package net.slqmy.block_warriors.utility;

import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import java.lang.reflect.Field;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class PacketUtility {
	public static void respawnPlayer(@NotNull final Player player) {
		Field connectionField;

		try {
			connectionField = player.getClass().getDeclaredField("h");
		} catch (NoSuchFieldException | SecurityException exception) {
			exception.printStackTrace();
			return;
		}

		ServerGamePacketListenerImpl connection;

		try {
			connection = (ServerGamePacketListenerImpl) connectionField.get(player);
		} catch (IllegalArgumentException | IllegalAccessException exception) {
			exception.printStackTrace();
			return;
		}

		connection.send(
						new ClientboundGameEventPacket(
										ClientboundGameEventPacket.IMMEDIATE_RESPAWN,
										1
						)
		);
	}
}
