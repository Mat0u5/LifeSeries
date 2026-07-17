package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard;

import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;

public class EmptyWildcard extends Wildcard {

	@Override
	public Wildcards getType() {
		return Wildcards.EMPTY;
	}
}
