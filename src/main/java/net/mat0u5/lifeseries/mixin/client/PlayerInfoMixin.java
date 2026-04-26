package net.mat0u5.lifeseries.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.mat0u5.lifeseries.features.LifeSkinsClient;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.minecraft.client.multiplayer.PlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.UUID;

//? if <= 1.20 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.world.entity.player.PlayerSkin;
//?}

@Mixin(value = PlayerInfo.class, priority = 1)
public class PlayerInfoMixin {

    //? if <= 1.20 {
    /*@ModifyReturnValue(method = "getSkinLocation", at = @At("RETURN"))
    private Identifier lifeSkinsOverride(Identifier original) {
    *///?} else {
    @ModifyReturnValue(method = "getSkin", at = @At("RETURN"))
    private PlayerSkin lifeSkinsOverride(PlayerSkin original) {
    //?}
        PlayerInfo playerInfo = (PlayerInfo) (Object) this;
        UUID uuid = OtherUtils.profileId(playerInfo.getProfile());

        var lifeSkins = LifeSkinsClient.getTexture(uuid);
            if (lifeSkins != null) {
            return lifeSkins;
        }
        return original;
    }
}
