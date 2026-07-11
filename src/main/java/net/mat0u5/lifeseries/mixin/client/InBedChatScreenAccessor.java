package net.mat0u5.lifeseries.mixin.client;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.InBedChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = InBedChatScreen.class, priority = 2)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public interface InBedChatScreenAccessor {
    @Accessor("leaveBedButton")
    abstract Button ls$leaveBedButton();
}
