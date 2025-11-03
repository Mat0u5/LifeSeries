package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.scoreboard.*;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerTabOverlay.class, priority = 1)
public class PlayerListHudMixin {

    @Redirect(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/scores/ReadOnlyScoreInfo;safeFormatValue(Lnet/minecraft/world/scores/ReadOnlyScoreInfo;Lnet/minecraft/network/chat/numbers/NumberFormat;)Lnet/minecraft/network/chat/MutableComponent;"))
    private MutableComponent modifyFormattedScore(ReadOnlyScoreInfo readableScoreboardScore, NumberFormat numberFormat) {
        Objective objective = ls$getDisplayedObjective();
        MutableComponent originalText = ReadOnlyScoreInfo.safeFormatValue(readableScoreboardScore, numberFormat);
        if (readableScoreboardScore == null || originalText == null || Main.modFullyDisabled()) return originalText;

        if (objective != null && objective.getName().equals(LivesManager.SCOREBOARD_NAME)) {
            int score = readableScoreboardScore.value();
            if (MainClient.clientCurrentSeason != Seasons.LIMITED_LIFE) {
                if (score >= MainClient.TAB_LIST_LIVES_CUTOFF && !MainClient.TAB_LIST_SHOW_EXACT_LIVES && !Main.DEBUG) {
                    return Component.literal(MainClient.TAB_LIST_LIVES_CUTOFF+"+").setStyle(originalText.getStyle());
                }
            }
            else {
                return Component.literal(OtherUtils.formatTime(score*20)).setStyle(originalText.getStyle());
            }
        }

        return originalText;
    }

    @Unique
    private Objective ls$getDisplayedObjective() {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) return null;

        Scoreboard scoreboard = client.level.getScoreboard();
        return scoreboard.getDisplayObjective(DisplaySlot.LIST);
    }


    @Inject(method = "getNameForDisplay", at = @At("RETURN"), cancellable = true)
    private void getName(PlayerInfo entry, CallbackInfoReturnable<Component> cir) {
        if (!MainClient.COLORBLIND_SUPPORT || Main.modFullyDisabled()) return;
        Component original = cir.getReturnValue();
        if (entry == null) return;
        PlayerTeam team = entry.getTeam();
        if (team == null) return;
        cir.setReturnValue(TextUtils.format("[{}] ", team.getDisplayName().getString()).formatted(team.getColor()).append(original));
    }
}
