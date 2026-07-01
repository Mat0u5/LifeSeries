package net.mat0u5.lifeseries.utils.player;

import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.UUID;

public class PlayerReference {
	private UUID uuid;

	private PlayerReference(UUID uuid) {
		this.uuid = uuid;
	}

	public static PlayerReference of(UUID uuid) {
		return new PlayerReference(uuid);
	}

	public static PlayerReference of(ServerPlayer player) {
		return new PlayerReference(player == null ? null : player.getUUID());
	}

	public @Nullable ServerPlayer get() {
		return PlayerUtils.getPlayer(uuid);
	}
}
