package net.mat0u5.lifeseries.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Level.class, priority = 1)
public class LevelMixin {
    @ModifyReturnValue(method = "getRainLevel", at = @At(value = "RETURN"))
    public float getRainLevel(float original) {
        if (!Main.modDisabled() && MainClient.clientCurrentSeason == Seasons.NICE_LIFE) {
            return 0;
        }
        return original;
    }
}
