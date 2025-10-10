package net.mat0u5.lifeseries.entity.triviabot.server;

import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

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
            SoundEvent sound = SoundEvent.of(Identifier.ofVanilla("wildlife_trivia_intro"));
            PlayerUtils.playSoundWithSourceToPlayers(PlayerUtils.getAllPlayers(), bot, sound, SoundCategory.NEUTRAL, 1, 1);
            introSoundCooldown = 830;
        }

        if (!playedCountdownEndingSound && bot.interactedWith() && !bot.submittedAnswer() && !bot.ranOutOfTime() && bot.triviaHandler.getRemainingTimeMs() <= 33800) {
            PlayerUtils.playSoundWithSourceToPlayers(
                    PlayerUtils.getAllPlayers(), bot,
                    SoundEvent.of(Identifier.ofVanilla("wildlife_trivia_suspense_end")),
                    SoundCategory.NEUTRAL, 0.65f, 1);
            playedCountdownEndingSound = true;
            playedCountdownSound = true;
        }
        else if (!playedCountdownSound && bot.interactedWith() && !bot.submittedAnswer() && !bot.ranOutOfTime()) {
            PlayerUtils.playSoundWithSourceToPlayers(
                    PlayerUtils.getAllPlayers(), bot,
                    SoundEvent.of(Identifier.ofVanilla("wildlife_trivia_suspense")),
                    SoundCategory.NEUTRAL, 0.65f, 1);
            playedCountdownSound = true;
        }
    }
}
