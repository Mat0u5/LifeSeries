package net.mat0u5.lifeseries.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Display.TextDisplay.class)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public interface TextDisplayAccessor {
    @Invoker("setText")
    void ls$setText(Component text);

    @Invoker("setBackgroundColor")
    void ls$setBackgroundColor(int color);

    @Invoker("setLineWidth")
    void ls$setLineWidth(int width);

    @Invoker("setFlags")
    void ls$setFlags(byte flags);
}
