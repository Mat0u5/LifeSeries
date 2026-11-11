package net.mat0u5.lifeseries.client.mixin;

import com.mojang.authlib.GameProfile;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.player.ProfileManager;
import net.minecraft.client.server.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

//? if > 1.21.6 {
import net.minecraft.server.players.NameAndId;
//?}

@Mixin(value = IntegratedServer.class)
@MixinEnvironment(value = "client", type = MixinEnvironment.Env.CLIENT)
public abstract class IntegratedServerMixin {
    @Inject(method = "isSingleplayerOwner", at = @At("HEAD"), cancellable = true)
    //? if <= 1.21.6 {
    /*private void ls$ownerIgnoresDisguise(GameProfile profile, CallbackInfoReturnable<Boolean> cir) {
        UUID uuid = OtherUtils.profileId(profile);
        String name = OtherUtils.profileName(profile);
    *///?} else {
    private void ls$ownerIgnoresDisguise(NameAndId nameAndId, CallbackInfoReturnable<Boolean> cir) {
        UUID uuid = nameAndId.id();
        String name = nameAndId.name();
    //?}
        if (uuid == null) return;
        String originalName = ProfileManager.getOriginalName(uuid);
        if (originalName == null || originalName.equalsIgnoreCase(name)) return;
        GameProfile owner = ((IntegratedServer) (Object) this).getSingleplayerProfile();
        cir.setReturnValue(owner != null && originalName.equalsIgnoreCase(OtherUtils.profileName(owner)));
    }
}
