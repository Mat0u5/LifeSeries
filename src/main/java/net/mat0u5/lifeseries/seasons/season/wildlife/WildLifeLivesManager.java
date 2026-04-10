package net.mat0u5.lifeseries.seasons.season.wildlife;

import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpower;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.AstralProjection;
import net.minecraft.server.level.ServerPlayer;

public class WildLifeLivesManager extends LivesManager {
    @Override
    public void onPlayerLivesChanged(ServerPlayer player) {
        super.onPlayerLivesChanged(player);
        Superpower power = SuperpowersWildcard.getSuperpowerInstance(player);
        if (power instanceof AstralProjection astralPower && SuperpowersWildcard.hasActivatedPower(player, Superpowers.ASTRAL_PROJECTION)) {
            astralPower.onChangeLives();
        }
    }
}
