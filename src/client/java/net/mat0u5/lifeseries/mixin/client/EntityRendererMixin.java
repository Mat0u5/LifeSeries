package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = EntityRenderer.class, priority = 1)
public class EntityRendererMixin<T extends Entity> {

    //? if <= 1.21 {
    @ModifyArg(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IF)V"),
            index = 1
    )
    public Text render(Text text) {
    //?} else if <= 1.21.6 {
    /*@ModifyArg(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;renderLabelIfPresent(Lnet/minecraft/client/render/entity/state/EntityRenderState;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"),
            index = 1
    )
    public Text render(Text text) {
    *///?} else {
    /*@ModifyArg(
            method = "renderLabelIfPresent",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitLabel(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Vec3d;ILnet/minecraft/text/Text;ZIDLnet/minecraft/client/render/state/CameraRenderState;)V"),
            index = 3
    )
    public Text render(Text text) {
    *///?}
        if (text == null || Main.modFullyDisabled()) return text;
        if (MinecraftClient.getInstance().getNetworkHandler() == null) return text;

        if (MainClient.playerDisguiseNames.containsKey(text.getString())) {
            String name = MainClient.playerDisguiseNames.get(text.getString());
            for (PlayerListEntry entry : MinecraftClient.getInstance().getNetworkHandler().getPlayerList()) {
                if (OtherUtils.profileName(entry.getProfile()).equalsIgnoreCase(TextUtils.removeFormattingCodes(name))) {
                    if (entry.getDisplayName() != null) {
                        return ls$applyColorblind(entry.getDisplayName(), entry.getScoreboardTeam());
                    }
                    return ls$applyColorblind(Text.literal(name), entry.getScoreboardTeam());
                }
            }
        }
        else {
            for (PlayerListEntry entry : MinecraftClient.getInstance().getNetworkHandler().getPlayerList()) {
                if (OtherUtils.profileName(entry.getProfile()).equalsIgnoreCase(TextUtils.removeFormattingCodes(text.getString()))) {
                    return ls$applyColorblind(text, entry.getScoreboardTeam());
                }
            }
        }
        return text;
    }

    @Unique
    public Text ls$applyColorblind(Text original, Team team) {
        if (!MainClient.COLORBLIND_SUPPORT) return original;
        if (original == null) return original;
        if (team == null) return original;
        return TextUtils.format("[{}] ",team.getDisplayName().getString()).formatted(team.getColor()).append(original);
    }
}
