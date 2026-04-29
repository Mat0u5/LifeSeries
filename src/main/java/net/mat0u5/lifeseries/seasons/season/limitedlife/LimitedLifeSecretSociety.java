package net.mat0u5.lifeseries.seasons.season.limitedlife;

import net.mat0u5.lifeseries.seasons.secretsociety.SecretSociety;
import net.mat0u5.lifeseries.utils.interfaces.IPlayer;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.DatapackIntegration;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class LimitedLifeSecretSociety extends SecretSociety {

    @Override
    public Component getPunishmentText() {
        return Component.nullToEmpty("§7Type \"/society fail\", and your time will be dropped to the next color.");
    }

    @Override
    public void punishPlayer(ServerPlayer member) {
        ((IPlayer) member).ls$hurt(member.damageSources().playerAttack(member), 0.001f);
        DatapackIntegration.EVENT_SOCIETY_FAIL_REWARD.trigger(new DatapackIntegration.Events.MacroEntry("Player", member.getScoreboardName()));
        if (DatapackIntegration.EVENT_SOCIETY_FAIL_REWARD.isCanceled()) return;
        boolean canChangeLives = ((IPlayer) member).ls$isAlive() && !((IPlayer) member).ls$isOnLastLife(true);
        if (canChangeLives) {
            ((IPlayer) member).ls$setLives(LimitedLife.getNextLivesColorLives(((IPlayer)member).ls$getLives()));
        }
    }
}
