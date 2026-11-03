package net.mat0u5.lifeseries.seasons.season.finallife;

import net.mat0u5.lifeseries.seasons.util.LivesManager;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.mat0u5.lifeseries.LifeSeries.currentSeason;

//? if <= 26.1 {
//import net.minecraft.ChatFormatting;
//?} else {
import net.minecraft.world.scores.TeamColor;
//?}

public class FinalLifeLivesManager extends LivesManager {
	//~ if >= 26.2 'ChatFormatting' -> 'TeamColor' {
	@Override
	public List<LivesTeam> getDefaultTeams() {
		return List.of(
				new LivesTeam("lives_null", "Unassigned", TeamColor.GRAY),
				new LivesTeam("lives_0", "Dead", TeamColor.DARK_GRAY),
				new LivesTeam("lives_1", "Dark Red", TeamColor.DARK_RED),
				new LivesTeam("lives_2", "Red", TeamColor.RED),
				new LivesTeam("lives_3", "Orange", TeamColor.GOLD),
				new LivesTeam("lives_4", "Yellow", TeamColor.YELLOW),
				new LivesTeam("lives_5", "Green", TeamColor.GREEN),
				new LivesTeam("lives_6", "Dark Green", TeamColor.DARK_GREEN)
		);
	}
	//~}

	@Override
	@Nullable
	public Integer getPlayerLives(ServerPlayer player) {
		if (currentSeason instanceof FinalLife finalLife && finalLife.finalLifeBoogeymanManager.isBoogeyman(player)) return 1;
		return super.getPlayerLives(player);
	}
	@Override
	public boolean isExcludedFromLivesSystem(ServerPlayer player) {
		if (currentSeason instanceof FinalLife finalLife && finalLife.finalLifeBoogeymanManager.isBoogeyman(player)) {
			return true;
		}
		super.isExcludedFromLivesSystem(player);
	}
}
