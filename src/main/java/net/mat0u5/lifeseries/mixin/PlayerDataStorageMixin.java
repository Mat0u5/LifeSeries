package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.subin.SubInManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.PlayerDataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

//? if >= 1.21.9
import net.minecraft.server.players.NameAndId;

@Mixin(value = PlayerDataStorage.class, priority = 1)
public class PlayerDataStorageMixin {

    @Redirect(method = "save", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getStringUUID()Ljava/lang/String;"))
    public String subInSave(Player instance) {
        return ls$getStringUUIDForPlayer(instance);
    }

    //? if <= 1.20.2 {
    /*@Redirect(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getStringUUID()Ljava/lang/String;"))
    public String subInLoad(Player instance) {
        return ls$getStringUUIDForPlayer(instance);
    }
    *///?} else if <= 1.21.6 {
    /*@Redirect(method = "load(Lnet/minecraft/world/entity/player/Player;Ljava/lang/String;)Ljava/util/Optional;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getStringUUID()Ljava/lang/String;"))
    public String subInLoad(Player instance) {
        return ls$getStringUUIDForPlayer(instance);
    }
    *///?} else {
    @Redirect(method = "load(Lnet/minecraft/server/players/NameAndId;Ljava/lang/String;)Ljava/util/Optional;", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/NameAndId;id()Ljava/util/UUID;"))
    public UUID subInLoad(NameAndId instance) {
        return ls$getStringUUIDForPlayer(instance);
    }

    @Unique
    private UUID ls$getStringUUIDForPlayer(NameAndId instance) {
        if (Main.isLogicalSide() && !Main.modDisabled() && SubInManager.isSubbingIn(instance.id())) {
            UUID resultUUID = SubInManager.getSubstitutedPlayerUUID(instance.id());
            if (resultUUID != null) {
                return resultUUID;
            }
        }
        return instance.id();
    }
    //?}

    @Unique
    private String ls$getStringUUIDForPlayer(Player instance) {
        if (Main.isLogicalSide() && !Main.modDisabled() && SubInManager.isSubbingIn(instance.getUUID())) {
            UUID resultUUID = SubInManager.getSubstitutedPlayerUUID(instance.getUUID());
            if (resultUUID != null) {
                return resultUUID.toString();
            }
        }
        return instance.getStringUUID();
    }
}
