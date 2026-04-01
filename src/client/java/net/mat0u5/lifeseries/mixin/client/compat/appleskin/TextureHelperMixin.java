package net.mat0u5.lifeseries.mixin.client.compat.appleskin;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.render.RenderUtils;
import net.mat0u5.lifeseries.utils.ClientUtils;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import squeek.appleskin.helpers.TextureHelper;

import java.util.Locale;

@Pseudo
@Mixin(value = TextureHelper.class, priority = 1, remap = false)
public class TextureHelperMixin {
    @Inject(method = "getHeartTexture", at = @At("RETURN"), cancellable = true)
    private static void lifeSkins(boolean hardcore, TextureHelper.HeartType type, CallbackInfoReturnable<Identifier> cir) {
        Identifier original = cir.getReturnValue();
        String texturePath = original.getPath();
        String playerTeamColor = ClientUtils.getPlayerTeamColor();
        String playerTeamName = ClientUtils.getPlayerTeamName();
        if (!MainClient.COLORED_HEARTS || playerTeamColor == null || playerTeamName == null ||
                !RenderUtils.lifeSkinsAllowedColors.contains(playerTeamColor.toLowerCase(Locale.ROOT)) ||
                !RenderUtils.lifeSkinsAllowedHearts.contains(texturePath) || Main.modFullyDisabled()) {
            return;
        }
        String color = playerTeamColor.toLowerCase(Locale.ROOT);
        String heartType = texturePath.replaceFirst("hud/heart/", "");
        Identifier customHeart = IdentifierHelper.mod(color+"_"+heartType);
        cir.setReturnValue(customHeart);
    }
}