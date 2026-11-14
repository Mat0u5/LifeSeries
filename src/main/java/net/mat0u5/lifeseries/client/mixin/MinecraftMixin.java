package net.mat0u5.lifeseries.client.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.client.LifeSeriesClient;
import net.mat0u5.lifeseries.config.WorldConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.InBetweenManager;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if >= 26.1 {
import net.minecraft.world.level.gamerules.GameRules;
import java.util.Optional;
import net.minecraft.client.multiplayer.chat.ChatAbilities;
import net.minecraft.client.multiplayer.chat.ChatRestriction;
//?}

@Mixin(value = Minecraft.class, priority = 1)
@MixinEnvironment(value = "client", type = MixinEnvironment.Env.CLIENT)
public abstract class MinecraftMixin {

    @Inject(method = "doWorldLoad", at = @At("HEAD"))
    //? if <= 1.20.2 {
    //private void acknowledgeWorldLoad(String string, LevelStorageSource.LevelStorageAccess levelStorageAccess, PackRepository packRepository, WorldStem worldStem, boolean bl, CallbackInfo ci) {
    //?} else if <= 1.21.11 {
    //private void acknowledgeWorldLoad(LevelStorageSource.LevelStorageAccess levelStorageAccess, PackRepository packRepository, WorldStem worldStem, boolean bl, CallbackInfo ci) {
    //?} else {
    private void acknowledgeWorldLoad(LevelStorageSource.LevelStorageAccess levelStorageAccess, PackRepository packRepository, WorldStem worldStem, Optional<GameRules> gameRules, boolean newWorld, CallbackInfo ci) {
    //?}
        if (LifeSeries.modFullyDisabled()) return;
        WorldConfig worldConfig = new WorldConfig(levelStorageAccess);
        if (worldConfig.acknowledged()) return;
        worldConfig.setProperty("acknowledged", "true");
    }

    //? if <= 1.21.11 {
    /*@Inject(method = "getChatStatus", at = @At("HEAD"), cancellable = true)
    private void inBetweenChatIsolation(CallbackInfoReturnable<Minecraft.ChatStatus> cir) {
        if (LifeSeries.modDisabled()) return;
        if (InBetweenManager.shouldIsolateChat(((Minecraft) (Object) this).player) && !LifeSeriesClient.isAdmin) {
            cir.setReturnValue(Minecraft.ChatStatus.DISABLED_BY_OPTIONS);
        }
    }
    *///?} else {
    @Inject(method = "computeChatAbilities", at = @At("RETURN"), cancellable = true)
    private void inBetweenChatIsolation(CallbackInfoReturnable<ChatAbilities> cir) {
        if (LifeSeries.modDisabled()) return;
        if (!InBetweenManager.shouldIsolateChat(((Minecraft) (Object) this).player)) return;
        ChatAbilities.Builder builder = new ChatAbilities.Builder();
        cir.getReturnValue().restrictions().forEach(builder::addRestriction);
        builder.addRestriction(LifeSeriesClient.isAdmin ? ChatRestriction.CHAT_DISABLED_BY_OPTIONS : ChatRestriction.CHAT_AND_COMMANDS_DISABLED_BY_OPTIONS);
        cir.setReturnValue(builder.build());
    }
    //?}
}
