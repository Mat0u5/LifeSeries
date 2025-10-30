package net.mat0u5.lifeseries.seasons.season.limitedlife;

import net.mat0u5.lifeseries.seasons.secretsociety.SecretSociety;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.mat0u5.lifeseries.Main.livesManager;

public class LimitedLifeSecretSociety extends SecretSociety {

    @Override
    public Text getPunishmentText() {
        return Text.of("§7Type \"/society fail\", and your time will be dropped to the next color.");
    }

    @Override
    public void punishPlayer(ServerPlayerEntity member) {
        PlayerUtils.damage(member, member.getDamageSources().playerAttack(member), 0.001f);
        boolean canChangeLives = member.ls$isAlive() && !member.ls$isOnLastLife(true);
        if (canChangeLives) {
            member.ls$setLives(LimitedLife.getNextLivesColorLives(member.ls$getLives()));
        }
    }
}
