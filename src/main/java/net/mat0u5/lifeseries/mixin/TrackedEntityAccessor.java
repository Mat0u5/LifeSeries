package net.mat0u5.lifeseries.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.network.ServerPlayerConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(targets = "net.minecraft.server.level.ChunkMap$TrackedEntity")
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public interface TrackedEntityAccessor {
    @Accessor("serverEntity")
    ServerEntity getServerEntity();

    @Accessor("seenBy")
    Set<ServerPlayerConnection> getSeenBy();
}
