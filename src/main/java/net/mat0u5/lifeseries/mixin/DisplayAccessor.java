package net.mat0u5.lifeseries.mixin;

import com.mojang.math.Transformation;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.world.entity.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Display.class)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public interface DisplayAccessor {
    @Invoker("setTransformation")
    void ls$setTransformation(Transformation transformation);

    //? if <= 1.20 {
    /*@Invoker("setInterpolationDuration")
    void ls$setTransformationInterpolationDuration(int duration);

    @Invoker("setInterpolationDelay")
    void ls$setTransformationInterpolationDelay(int delay);
    *///?} else {
    @Invoker("setTransformationInterpolationDuration")
    void ls$setTransformationInterpolationDuration(int duration);

    @Invoker("setTransformationInterpolationDelay")
    void ls$setTransformationInterpolationDelay(int delay);

    @Invoker("setPosRotInterpolationDuration")
    void ls$setPosRotInterpolationDuration(int duration);
    //?}
}
