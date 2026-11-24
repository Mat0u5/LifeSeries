package net.mat0u5.lifeseries.mixin;

//? if <= 1.21.6 {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.InBetweenManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = MinecraftServer.class)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public class MinecraftServerMixin {
    @WrapOperation(method = "createLevels", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getWorldBorder()Lnet/minecraft/world/level/border/WorldBorder;"))
    private WorldBorder keepInBetweenBorder(ServerLevel level, Operation<WorldBorder> original) {
        return level.dimension() == InBetweenManager.DIMENSION ? new WorldBorder() : original.call(level);
    }
}
*///?} else {
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = MinecraftServer.class)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public class MinecraftServerMixin {
    //Empty class to avoid mixin errors
}
//?}
