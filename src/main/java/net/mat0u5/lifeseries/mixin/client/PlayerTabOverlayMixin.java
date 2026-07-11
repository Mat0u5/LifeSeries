package net.mat0u5.lifeseries.mixin.client;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.client.LifeSeriesClient;
import net.mat0u5.lifeseries.seasons.util.LivesManager;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.other.Time;
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

//? if <= 1.20.2 {
/*import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?} else {
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.NumberFormat;
//?}

@Mixin(value = PlayerTabOverlay.class, priority = 1)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public class PlayerTabOverlayMixin {

    //? if <= 1.20.2 {
    /*@Redirect(method = "renderTablistScore",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)I"))
    private int modifyFormattedScore(GuiGraphicsExtractor instance, Font font, String s, int i1, int i2, int i3, Objective objective, int i, String string, int j, int k) {
        if (objective != null && LifeSeries.modFullyDisabled()) {
            int score = objective.getScoreboard().getOrCreatePlayerScore(string, objective).getScore();
            if (objective.getName().equals(LivesManager.SCOREBOARD_NAME)) {
                Component renderOverride = null;
                if (!LifeSeries.isSeason(Seasons.LIMITED_LIFE)) {
                    if (score >= LifeSeriesClient.TAB_LIST_LIVES_CUTOFF && !LifeSeriesClient.TAB_LIST_SHOW_EXACT_LIVES && !LifeSeries.DEBUG) {
                        renderOverride = Component.literal(LifeSeriesClient.TAB_LIST_LIVES_CUTOFF+"+").withStyle(ChatFormatting.YELLOW);
                    }
                }
                else {
                    renderOverride = Component.literal(Time.seconds(score).formatLong()).withStyle(ChatFormatting.YELLOW);
                }
                if (renderOverride != null) {
                    return instance.drawString(font, renderOverride, k - font.width(renderOverride), i2, i3);
                }
            }
        }

        return instance.drawString(font, s, i1, i2, i3);
    }
    *///?} else {

    //? if <= 1.21.11 {
    /*@Redirect(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/scores/ReadOnlyScoreInfo;safeFormatValue(Lnet/minecraft/world/scores/ReadOnlyScoreInfo;Lnet/minecraft/network/chat/numbers/NumberFormat;)Lnet/minecraft/network/chat/MutableComponent;"))
    *///?} else if <= 26.1 {
    /*@Redirect(method = "extractRenderState",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/scores/ReadOnlyScoreInfo;safeFormatValue(Lnet/minecraft/world/scores/ReadOnlyScoreInfo;Lnet/minecraft/network/chat/numbers/NumberFormat;)Lnet/minecraft/network/chat/MutableComponent;"))
    *///?} else {
    @Redirect(method = "extractRenderState",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/scores/ReadOnlyScoreInfo;formatValue(Lnet/minecraft/network/chat/numbers/NumberFormat;)Lnet/minecraft/network/chat/MutableComponent;"))
    //?}
    private MutableComponent modifyFormattedScore(ReadOnlyScoreInfo readableScoreboardScore, NumberFormat numberFormat) {
        //? if <= 26.1 {
        /*MutableComponent originalText = ReadOnlyScoreInfo.safeFormatValue(readableScoreboardScore, numberFormat);
        *///?} else {
        MutableComponent originalText = readableScoreboardScore.formatValue(numberFormat);
        //?}
        Objective objective = ls$getDisplayedObjective();
        if (readableScoreboardScore == null || originalText == null || LifeSeries.modFullyDisabled()) return originalText;

        if (objective != null && objective.getName().equals(LivesManager.SCOREBOARD_NAME)) {
            int score = readableScoreboardScore.value();
            if (!LifeSeries.isSeason(Seasons.LIMITED_LIFE)) {
                if (score >= LifeSeriesClient.TAB_LIST_LIVES_CUTOFF && !LifeSeriesClient.TAB_LIST_SHOW_EXACT_LIVES && !LifeSeries.DEBUG) {
                    return Component.literal(LifeSeriesClient.TAB_LIST_LIVES_CUTOFF+"+").setStyle(originalText.getStyle());
                }
            }
            else {
                return Component.literal(Time.seconds(score).formatLong()).setStyle(originalText.getStyle());
            }
        }

        return originalText;
    }
    //?}

    @Unique
    private Objective ls$getDisplayedObjective() {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) return null;

        Scoreboard scoreboard = client.level.getScoreboard();
        //? if <= 1.20 {
        /*return scoreboard.getDisplayObjective(Scoreboard.DISPLAY_SLOT_LIST);
        *///?} else {
        return scoreboard.getDisplayObjective(DisplaySlot.LIST);
        //?}
    }


    @Inject(method = "getNameForDisplay", at = @At("RETURN"), cancellable = true)
    private void getName(PlayerInfo entry, CallbackInfoReturnable<Component> cir) {
        if (!LifeSeriesClient.COLORBLIND_SUPPORT || LifeSeries.modFullyDisabled()) return;
        Component original = cir.getReturnValue();
        if (entry == null) return;
        PlayerTeam team = entry.getTeam();
        if (team == null) return;
        String name = team.getDisplayName().getString();
        if (name == null || name.isEmpty()) return;
        //~ if >= 26.2 '.withStyle(team.getColor())' -> '.withColor(team.getColor().orElse(TeamColor.WHITE).textColor())' {
        cir.setReturnValue(TextUtils.format("[{}] ", name).withColor(team.getColor().orElse(TeamColor.WHITE).textColor()).append(original));
        //~}
    }
}
