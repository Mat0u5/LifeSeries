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
import net.minecraft.world.scores.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if <= 1.20 {
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.NumberFormat;
*///?}

@Mixin(value = PlayerTabOverlay.class, priority = 1)
public class PlayerTabOverlayMixin {

    //? if <= 1.20 {
    @Redirect(method = "renderTablistScore",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)I"))
    private int modifyFormattedScore(GuiGraphics instance, Font font, String s, int i1, int i2, int i3, Objective objective, int i, String string, int j, int k) {
        if (objective != null && Main.modFullyDisabled()) {
            int score = objective.getScoreboard().getOrCreatePlayerScore(string, objective).getScore();
            if (objective.getName().equals(LivesManager.SCOREBOARD_NAME)) {
                Component renderOverride = null;
                if (MainClient.clientCurrentSeason != Seasons.LIMITED_LIFE) {
                    if (score >= MainClient.TAB_LIST_LIVES_CUTOFF && !MainClient.TAB_LIST_SHOW_EXACT_LIVES && !Main.DEBUG) {
                        renderOverride = Component.literal(MainClient.TAB_LIST_LIVES_CUTOFF+"+").withStyle(ChatFormatting.YELLOW);
                    }
                }
                else {
                    renderOverride = Component.literal(OtherUtils.formatTime(score*20)).withStyle(ChatFormatting.YELLOW);
                }
                if (renderOverride != null) {
                    return instance.drawString(font, renderOverride, k - font.width(renderOverride), i2, i3);
                }
            }
        }

        return instance.drawString(font, s, i1, i2, i3);
    }
    //?} else {
    /*@Redirect(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/scores/ReadOnlyScoreInfo;safeFormatValue(Lnet/minecraft/world/scores/ReadOnlyScoreInfo;Lnet/minecraft/network/chat/numbers/NumberFormat;)Lnet/minecraft/network/chat/MutableComponent;"))
    private MutableComponent modifyFormattedScore(ReadOnlyScoreInfo readableScoreboardScore, NumberFormat numberFormat) {
        MutableComponent originalText = ReadOnlyScoreInfo.safeFormatValue(readableScoreboardScore, numberFormat);
        Objective objective = ls$getDisplayedObjective();
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
    *///?}

    @Unique
    private Objective ls$getDisplayedObjective() {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) return null;

        Scoreboard scoreboard = client.level.getScoreboard();
        //? if <= 1.20 {
        return scoreboard.getDisplayObjective(Scoreboard.DISPLAY_SLOT_LIST);
        //?} else {
        /*return scoreboard.getDisplayObjective(DisplaySlot.LIST);
        *///?}
    }


    @Inject(method = "getNameForDisplay", at = @At("RETURN"), cancellable = true)
    private void getName(PlayerInfo entry, CallbackInfoReturnable<Component> cir) {
        if (!MainClient.COLORBLIND_SUPPORT || Main.modFullyDisabled()) return;
        Component original = cir.getReturnValue();
        if (entry == null) return;
        PlayerTeam team = entry.getTeam();
        if (team == null) return;
        cir.setReturnValue(TextUtils.format("[{}] ", team.getDisplayName().getString()).withStyle(team.getColor()).append(original));
    }
}
