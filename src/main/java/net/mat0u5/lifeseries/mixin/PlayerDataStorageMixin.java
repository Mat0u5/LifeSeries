package net.mat0u5.lifeseries.mixin;

import com.mojang.authlib.GameProfile;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.seasons.subin.SubInManager;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.PlayerDataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Mixin(value = PlayerDataStorage.class, priority = 1)
public class PlayerDataStorageMixin {

    @Redirect(method = "save", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getStringUUID()Ljava/lang/String;"))
    public String subInSave(Player instance) {
        return ls$getStringUUIDForPlayer(instance);
    }

    //? if <= 1.20.3 {
    /*@Redirect(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getStringUUID()Ljava/lang/String;"))
    public String subInLoad(Player instance) {
        return ls$getStringUUIDForPlayer(instance);
    }
    *///?} else if <= 1.21.6 {
    /*@Redirect(method = "load(Lnet/minecraft/world/entity/player/Player;Ljava/lang/String;)Ljava/util/Optional;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getStringUUID()Ljava/lang/String;"))
    public String subInLoad(Player instance) {
        return ls$getStringUUIDForPlayer(instance);
    }
    *///?}

    @Unique
    private String ls$getStringUUIDForPlayer(Player instance) {
        if (LifeSeries.isLogicalNonDisabled() && SubInManager.isSubbingIn(instance)) {
            GameProfile gameProfile = SubInManager.getTargetPlayer(instance);
            if (gameProfile != null) {
                UUID resultUUID = OtherUtils.profileId(gameProfile);
                if (resultUUID != null) {
                    return resultUUID.toString();
                }
            }
        }
        return instance.getStringUUID();
    }
}
