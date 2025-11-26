package net.mat0u5.lifeseries.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Mixin(value = SkullBlockEntity.class, priority = 1)
public interface SkullBlockEntityAccessor {
    //? if > 1.20 && <= 1.20.3 {
    /*@Invoker("fetchGameProfile")
    static CompletableFuture<Optional<GameProfile>> ls$fetchGameProfile(String string) {
        throw new AssertionError();
    }
    *///?}
}
