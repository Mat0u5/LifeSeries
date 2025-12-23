package net.mat0u5.lifeseries.mixin.client;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Player.class, priority = 3)
public interface PlayerAccessor {
    @Mutable
    @Accessor("sleepCounter")
    void ls$setSleepCounter(int sleepCounter);
}
