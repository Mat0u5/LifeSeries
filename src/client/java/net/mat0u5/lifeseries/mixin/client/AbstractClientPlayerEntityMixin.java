package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;
//? if <= 1.21.6
import net.minecraft.client.util.SkinTextures;
//? if >= 1.21.9
/*import net.minecraft.entity.player.SkinTextures;*/

@Mixin(value = AbstractClientPlayerEntity.class, priority = 1)
public class AbstractClientPlayerEntityMixin {
    //? if <= 1.21.6 {
    @Inject(method = "getSkinTextures", at = @At("HEAD"), cancellable = true)
    //?} else {
    /*@Inject(method = "getSkin", at = @At("HEAD"), cancellable = true)
    *///?}
    public void getSkinTextures(CallbackInfoReturnable<SkinTextures> cir) {
        if (Main.modFullyDisabled()) return;
        AbstractClientPlayerEntity abstrPlayer = (AbstractClientPlayerEntity) (Object) this;
        UUID uuid = abstrPlayer.getUuid();
        if (uuid == null) return;
        if (!MainClient.playerDisguiseUUIDs.containsKey(uuid)) return;
        
        UUID disguisedUUID = MainClient.playerDisguiseUUIDs.get(uuid);
        if (MinecraftClient.getInstance().getNetworkHandler() == null) {
            return;
        }
        for (PlayerListEntry entry : MinecraftClient.getInstance().getNetworkHandler().getPlayerList()) {
            if (OtherUtils.profileId(entry.getProfile()).equals(disguisedUUID)) {
                cir.setReturnValue(entry.getSkinTextures());
                return;
            }
        }
    }
}
