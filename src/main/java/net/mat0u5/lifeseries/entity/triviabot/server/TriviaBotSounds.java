package net.mat0u5.lifeseries.entity.triviabot.server;

import net.mat0u5.lifeseries.config.modifiable.ModifiableSound;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

import java.util.List;

public class TriviaBotSounds {
    private TriviaBot bot;
    public TriviaBotSounds(TriviaBot bot) {
        this.bot = bot;
    }

    private int introSoundCooldown = 0;
    private boolean playedCountdownSound = false;
    private boolean playedCountdownEndingSound = false;
    public int delay = 0;
    public void playSounds() {
        if (delay > 0) {
            delay--;
            return;
        }
        if (introSoundCooldown > 0) introSoundCooldown--;

        if (introSoundCooldown == 0 && !bot.interactedWith()) {
            if (!bot.santaBot()) {
                ModifiableSound.WILDLIFE_TRIVIA_INTRO.playWithSource(PlayerUtils.getAllPlayers(), bot, SoundSource.NEUTRAL, 1, 1);
                introSoundCooldown = 830;
            }
            else {
                ModifiableSound.NICELIFE_SANTABOT_INTRO.play(bot.serverData.getBoundPlayer(), 0.65f, 1);
                introSoundCooldown = 624;
            }
        }

        if (!playedCountdownEndingSound && bot.interactedWith() && !bot.submittedAnswer() && !bot.ranOutOfTime()
                && ((!bot.santaBot() && bot.triviaHandler.getRemainingTicks() <= 676) || (bot.santaBot() && bot.triviaHandler.getRemainingTicks() <= 643))) {
            if (!bot.santaBot()) {
                List<ServerPlayer> otherPlayers = PlayerUtils.getAllPlayers();
                ServerPlayer boundPlayer = bot.serverData.getBoundPlayer();
                if (boundPlayer != null) {
                    otherPlayers.remove(boundPlayer);
                    ModifiableSound.WILDLIFE_TRIVIA_SUSPENSE_END.playWithSource(List.of(boundPlayer), bot, SoundSource.MASTER, 0.65f, 1);
                }
                ModifiableSound.WILDLIFE_TRIVIA_SUSPENSE_END.playWithSource(otherPlayers, bot, SoundSource.NEUTRAL, 0.65f, 1);

            }
            else {
                ModifiableSound.NICELIFE_SANTABOT_SUSPENSE_END.play(bot.serverData.getBoundPlayer(), 0.65f, 1);
            }
            playedCountdownEndingSound = true;
            playedCountdownSound = true;
        }
        else if (!playedCountdownSound && bot.interactedWith() && !bot.submittedAnswer() && !bot.ranOutOfTime()) {
            if (!bot.santaBot()) {
                ModifiableSound.WILDLIFE_TRIVIA_SUSPENSE.playWithSource(PlayerUtils.getAllPlayers(), bot, SoundSource.NEUTRAL, 0.65f, 1);
            }
            else  {
                ModifiableSound.NICELIFE_SANTABOT_SUSPENSE.play(bot.serverData.getBoundPlayer(), 0.65f, 1);
            }
            playedCountdownSound = true;
        }
    }
}
