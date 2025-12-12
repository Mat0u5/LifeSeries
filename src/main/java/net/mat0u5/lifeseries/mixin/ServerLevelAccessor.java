package net.mat0u5.lifeseries.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.SleepStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = ServerLevel.class, priority = 2)
public interface ServerLevelAccessor {
    @Accessor("sleepStatus")
    SleepStatus ls$getSleepStatus();

    @Invoker("wakeUpAllPlayers")
    void ls$wakeUpAllPlayers();
}
