package net.mat0u5.lifeseries.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.seasons.season.finallife.boogeyman.FinalLifeBoogeymanManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.commands.MsgCommand;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(value = MsgCommand.class, priority = 1)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public class MsgCommandMixin {
    @Inject(method = "sendMessage", at = @At("HEAD"), cancellable = true)
    private static void blockBoogeymanMessages(CommandSourceStack source, Collection<ServerPlayer> targets, PlayerChatMessage message, CallbackInfo ci) {
        if (LifeSeries.isClientOrDisabled()) return;
        if (FinalLifeBoogeymanManager.canMessage(source, targets)) return;
        source.sendFailure(Component.translatable("argument.entity.notfound.player"));
        ci.cancel();
    }
}
