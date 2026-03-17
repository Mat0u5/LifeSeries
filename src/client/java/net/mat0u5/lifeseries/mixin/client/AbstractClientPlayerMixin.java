package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;
//? if <= 1.20
//import net.minecraft.resources.Identifier;
//? if > 1.20
import net.minecraft.world.entity.player.PlayerSkin;

@Mixin(value = AbstractClientPlayer.class, priority = 1)
public class AbstractClientPlayerMixin {
    //? if <= 1.20 {
    /*@Inject(method = "getSkinTextureLocation", at = @At("HEAD"), cancellable = true)
    public void getSkinTextures(CallbackInfoReturnable<Identifier> cir) {
    *///?} else {
    @Inject(method = "getSkin", at = @At("HEAD"), cancellable = true)
    public void getSkinTextures(CallbackInfoReturnable<PlayerSkin> cir) {
    //?}
        if (Main.modFullyDisabled()) return;
        AbstractClientPlayer abstrPlayer = (AbstractClientPlayer) (Object) this;
        UUID uuid = abstrPlayer.getUUID();
        if (uuid == null) return;
        if (!MainClient.playerDisguiseUUIDs.containsKey(uuid)) return;
        
        UUID disguisedUUID = MainClient.playerDisguiseUUIDs.get(uuid);
        if (Minecraft.getInstance().getConnection() == null) {
            return;
        }
        for (PlayerInfo entry : Minecraft.getInstance().getConnection().getOnlinePlayers()) {
            if (OtherUtils.profileId(entry.getProfile()).equals(disguisedUUID)) {
                //~ if > 1.20 '.getSkinLocation()' -> '.getSkin()' {
                cir.setReturnValue(entry.getSkin());
                //~}
                return;
            }
        }
    }
}
