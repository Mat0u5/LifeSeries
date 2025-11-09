package net.mat0u5.lifeseries.mixin;

import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.Path;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = PathNavigation.class, priority = 1)
public interface PathNavigationAccessor {
    @Accessor
    @Mutable
    void setPath(Path path);
}
