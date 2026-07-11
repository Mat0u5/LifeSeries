package net.mat0u5.lifeseries.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.server.level.ChunkMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkMap.class)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public interface ChunkMapAccessor {
    @Accessor("entityMap")
    Int2ObjectMap<TrackedEntityAccessor> getEntityTrackers();
}