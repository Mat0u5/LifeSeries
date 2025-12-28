package net.mat0u5.lifeseries.entity.triviabot.server;

import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class TriviaBotSounds {
    private TriviaBot bot;
    public TriviaBotSounds(TriviaBot bot) {
        this.bot = bot;
    }

    private int introSoundCooldown = 0;
    private boolean playedCountdownSound = false;
    private boolean playedCountdownEndingSound = false;
    public void playSounds() {
        if (introSoundCooldown > 0) introSoundCooldown--;

        if (introSoundCooldown == 0 && !bot.interactedWith()) {
            if (!bot.santaBot()) {
                SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("wildlife_trivia_intro"));
                PlayerUtils.playSoundWithSourceToPlayers(PlayerUtils.getAllPlayers(), bot, sound, SoundSource.NEUTRAL, 1, 1);
                introSoundCooldown = 830;
            }
            else {
                SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_santabot_intro"));
                PlayerUtils.playSoundToPlayer(bot.serverData.getBoundPlayer(), sound, 1, 1);
                introSoundCooldown = 624;
            }
        }

        if (!playedCountdownEndingSound && bot.interactedWith() && !bot.submittedAnswer() && !bot.ranOutOfTime()
                && ((!bot.santaBot() && bot.triviaHandler.getRemainingTicks() <= 676) || (bot.santaBot() && bot.triviaHandler.getRemainingTicks() <= 643))) {
            if (!bot.santaBot()) {
                SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("wildlife_trivia_suspense_end"));
                PlayerUtils.playSoundWithSourceToPlayers(PlayerUtils.getAllPlayers(), bot, sound, SoundSource.NEUTRAL, 0.65f, 1);
            }
            else {
                SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_santabot_suspense_end"));
                PlayerUtils.playSoundToPlayer(bot.serverData.getBoundPlayer(), sound, 0.65f, 1);
            }
            playedCountdownEndingSound = true;
            playedCountdownSound = true;
        }
        else if (!playedCountdownSound && bot.interactedWith() && !bot.submittedAnswer() && !bot.ranOutOfTime()) {
            if (!bot.santaBot()) {
                SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("wildlife_trivia_suspense"));
                PlayerUtils.playSoundWithSourceToPlayers(PlayerUtils.getAllPlayers(), bot, sound, SoundSource.NEUTRAL, 0.65f, 1);
            }
            else  {
                SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_santabot_suspense"));
                PlayerUtils.playSoundToPlayer(bot.serverData.getBoundPlayer(), sound, 0.65f, 1);
            }
            playedCountdownSound = true;
        }
    }
}
