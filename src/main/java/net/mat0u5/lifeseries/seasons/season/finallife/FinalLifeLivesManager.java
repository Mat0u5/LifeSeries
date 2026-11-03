package net.mat0u5.lifeseries.seasons.season.finallife;

import net.mat0u5.lifeseries.seasons.util.LivesManager;
import net.minecraft.world.scores.TeamColor;

import java.util.List;

public class FinalLifeLivesManager extends LivesManager {
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
}
