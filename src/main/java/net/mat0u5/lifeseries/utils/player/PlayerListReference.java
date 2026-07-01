package net.mat0u5.lifeseries.utils.player;

import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerListReference {
	private List<UUID> uuidList;

	private PlayerListReference(List<UUID> uuidList) {
		this.uuidList = uuidList;
	}

	public static PlayerListReference of(List<ServerPlayer> players) {
		List<UUID> uuidList = new ArrayList<>();
		for (ServerPlayer player : players) {
			if (player == null) continue;
			uuidList.add(player.getUUID());
		}
		return new PlayerListReference(uuidList);
	}

	public List<ServerPlayer> get() {
		List<ServerPlayer> result = new ArrayList<>();
		for (UUID uuid : uuidList) {
			ServerPlayer player = PlayerUtils.getPlayer(uuid);
			if (player != null) {
				result.add(player);
			}
		}
		return result;
	}
}
