package net.mat0u5.lifeseries.utils.player;

import java.util.UUID;

public class RealUUID {
	private UUID uuid;
	private RealUUID(UUID uuid) {
		this.uuid = uuid;
	}

	public static RealUUID of(UUID uuid) {
		return new RealUUID(uuid);
	}

	public UUID get() {
		return uuid;
	}
}
