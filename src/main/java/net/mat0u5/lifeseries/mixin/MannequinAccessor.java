package net.mat0u5.lifeseries.mixin;
//? if < 1.21.9 {
/*import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;

@Mixin(value = MinecraftServer.class)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public interface MannequinAccessor {
    //Empty class to avoid mixin errors
}
*///?} else {

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.decoration.Mannequin;
import net.minecraft.world.item.component.ResolvableProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = Mannequin.class, priority = 1)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public interface MannequinAccessor {

    @Invoker("setProfile")
    void ls$setMannequinProfile(ResolvableProfile profile);

    @Invoker("getProfile")
    ResolvableProfile ls$getMannequinProfile();

    @Invoker("setDescription")
    void ls$setDescription(Component description);

    @Invoker("getDescription")
    Component ls$getDescription();

    @Invoker("setHideDescription")
    void ls$setHideDescription(boolean hideDescription);
}
//?}