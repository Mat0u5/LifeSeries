package net.mat0u5.lifeseries.mixin.client;

//? if <= 1.20.3 {
/*import com.mojang.blaze3d.vertex.PoseStack;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphComponent;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Inject(method = "renderShadow", at = @At("HEAD"), cancellable = true)
    private static void stopShadow(PoseStack poseStack, MultiBufferSource multiBufferSource, Entity entity, float f, float g, LevelReader levelReader, float h, CallbackInfo ci){
        if (Main.modFullyDisabled()) return;
        if (entity instanceof Player player) {
            MorphComponent morphComponent = MorphManager.getComponent(player);
            if (morphComponent != null && morphComponent.isMorphed()) {
                ci.cancel();
            }
        }
    }
}
*///?} else if <= 1.21 {
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = MinecraftServer.class)
public class EntityRenderDispatcherMixin {
    //Empty class to avoid mixin errors
}
//?} else {
/*import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphComponent;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityRenderDispatcher.class, priority = 1)
public class EntityRenderDispatcherMixin {
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    public <E extends Entity> void render(E entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof Player playerEntity) {
            if (MainClient.invisiblePlayers.containsKey(playerEntity.getUUID())) {
                long time = MainClient.invisiblePlayers.get(playerEntity.getUUID());
                if (time > System.currentTimeMillis() || time == -1) {
                    cir.setReturnValue(false);
                }
            }

            MorphComponent morphComponent = MorphManager.getOrCreateComponent(playerEntity);
            LivingEntity dummy = morphComponent.getDummy();
            if(morphComponent.isMorphed() && dummy != null) {
                cir.setReturnValue(false);
            }
        }
    }
}
*///?}
