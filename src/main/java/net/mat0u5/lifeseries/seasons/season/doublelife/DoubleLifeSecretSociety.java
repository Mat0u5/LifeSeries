package net.mat0u5.lifeseries.seasons.season.doublelife;

import net.mat0u5.lifeseries.seasons.secretsociety.SecretSociety;
import net.mat0u5.lifeseries.utils.interfaces.IPlayer;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

import static net.mat0u5.lifeseries.LifeSeries.currentSeason;

public class DoubleLifeSecretSociety extends SecretSociety {

	@Override
	public int addMember(List<ServerPlayer> memberPlayers, ServerPlayer player) {
		if (!DoubleLife.SOULBOUND_SECRET_SOCIETY) {
			return super.addMember(memberPlayers, player);
		}
		memberPlayers.add(player);
		if (currentSeason instanceof DoubleLife doubleLife) {
			ServerPlayer soulmate = doubleLife.getSoulmate(player);
			if (soulmate != null && ((IPlayer) soulmate).ls$isAlive()) {
				memberPlayers.add(soulmate);
				return 2;
			}
		}
		return 1;
	}
}
