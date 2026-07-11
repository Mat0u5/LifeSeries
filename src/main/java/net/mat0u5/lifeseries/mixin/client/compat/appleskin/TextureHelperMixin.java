package net.mat0u5.lifeseries.mixin.client.compat.appleskin;

//? if <= 1.20 {
/*import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import squeek.appleskin.api.event.HUDOverlayEvent;
import squeek.appleskin.client.HUDOverlayHandler;

//? if forge {
/^import org.spongepowered.asm.mixin.injection.Redirect;
^///?} else {
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
//?}

@Pseudo
@Mixin(value = HUDOverlayHandler.class, priority = 1, remap = false)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public class TextureHelperMixin {
    //? if forge {
    /^@Redirect(method = "renderFoodOrHealthOverlay", at = @At(value = "INVOKE", target = "Lsqueek/appleskin/api/event/HUDOverlayEvent$HealthRestored;isCanceled()Z"), remap = false)
    private static boolean cancelHealthPreview(HUDOverlayEvent.HealthRestored instance) {
        return true;
    }
    ^///?} else {
    @WrapOperation(method = "onRender", at = @At(value = "FIELD", target = "Lsqueek/appleskin/api/event/HUDOverlayEvent$HealthRestored;isCanceled:Z"))
    private boolean cancelHealthPreview(HUDOverlayEvent.HealthRestored instance, Operation<Boolean> original) {
        return true;
    }
    //?}
}
*///?} else {
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.client.LifeSeriesClient;
import net.mat0u5.lifeseries.client.render.RenderUtils;
import net.mat0u5.lifeseries.client.utils.ClientUtils;
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
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public class TextureHelperMixin {
    @Inject(method = "getHeartTexture", at = @At("RETURN"), cancellable = true)
    private static void teamHearts(boolean hardcore, TextureHelper.HeartType type, CallbackInfoReturnable<Identifier> cir) {
        Identifier original = cir.getReturnValue();
        String texturePath = original.getPath();
        String playerTeamColor = ClientUtils.getPlayerTeamColor();
        String playerTeamName = ClientUtils.getPlayerTeamName();
        if (!LifeSeriesClient.COLORED_HEARTS || playerTeamColor == null || playerTeamName == null ||
                !RenderUtils.lifeSkinsAllowedColors.contains(playerTeamColor.toLowerCase(Locale.ROOT)) ||
                !RenderUtils.lifeSkinsAllowedHearts.contains(texturePath) || LifeSeries.modFullyDisabled()) {
            return;
        }
        String color = playerTeamColor.toLowerCase(Locale.ROOT);
        String heartType = texturePath.replaceFirst("hud/heart/", "");
        Identifier customHeart = IdentifierHelper.mod(color+"_"+heartType);
        cir.setReturnValue(customHeart);
    }
}
//?}