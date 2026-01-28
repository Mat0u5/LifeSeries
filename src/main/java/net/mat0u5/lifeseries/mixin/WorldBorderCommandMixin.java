package net.mat0u5.lifeseries.mixin;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.WorldBorderCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.mat0u5.lifeseries.Main.seasonConfig;

@Mixin(value = WorldBorderCommand.class, priority = 1)
public class WorldBorderCommandMixin {
    @Inject(method = "setSize", at = @At("TAIL"))
    private static void onSizeUpdated(CommandSourceStack commandSourceStack, double d, long l, CallbackInfoReturnable<Integer> cir) {
        MinecraftServer server = commandSourceStack.getServer();
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        ServerLevel nether = server.getLevel(Level.NETHER);
        ServerLevel end = server.getLevel(Level.END);
        if (overworld != null) seasonConfig.setProperty(seasonConfig.WORLDBORDER_SIZE.key, String.valueOf((int)overworld.getWorldBorder().getLerpTarget()));
        //? if >= 1.21.9 {
        if (nether != null) seasonConfig.setProperty(seasonConfig.WORLDBORDER_NETHER_SIZE.key, String.valueOf((int)nether.getWorldBorder().getLerpTarget()));
        if (end != null) seasonConfig.setProperty(seasonConfig.WORLDBORDER_END_SIZE.key, String.valueOf((int)end.getWorldBorder().getLerpTarget()));
        //?}
    }
}
