package net.mat0u5.lifeseries.mixin;
//? if < 1.21.9 {
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = MinecraftServer.class)
public interface MannequinEntityAccessor {
    //Empty class to avoid mixin errors
}
//?} else {

/*import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.decoration.MannequinEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = MannequinEntity.class, priority = 1)
public interface MannequinEntityAccessor {

    @Invoker("setMannequinProfile")
    void ls$setMannequinProfile(ProfileComponent profile);

    @Invoker("getMannequinProfile")
    ProfileComponent ls$getMannequinProfile();

    @Invoker("setDescription")
    void ls$setDescription(Text description);

    @Invoker("getDescription")
    Text ls$getDescription();

    @Invoker("setHideDescription")
    void ls$setHideDescription(boolean hideDescription);
}
*///?}