package net.mat0u5.lifeseries.mixin;
//? if <= 1.21.11 {
/*import net.mat0u5.lifeseries.events.Events;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

@Mixin(targets = "net.minecraft.server.network.ServerGamePacketListenerImpl$1")
public abstract class ServerPlayNetworkHandlerInteractEntityHandlerMixin implements ServerboundInteractPacket.Handler {
//? if fabric {
    /^@Shadow
    @Final
    ServerGamePacketListenerImpl field_28963;

    @Shadow
    @Final
    Entity val$target;
^///?} else {
    //? if <= 1.20 {
    /^private static java.lang.reflect.Field lifeseries$handlerField;
    private static java.lang.reflect.Field lifeseries$entityField;

    private ServerGamePacketListenerImpl ls$getHandler() {
        try {
            if (lifeseries$handlerField == null) {
                for (java.lang.reflect.Field f : this.getClass().getDeclaredFields()) {
                    if (f.getType() == ServerGamePacketListenerImpl.class) {
                        f.setAccessible(true);
                        lifeseries$handlerField = f;
                        break;
                    }
                }
            }
            return (ServerGamePacketListenerImpl) lifeseries$handlerField.get(this);
        } catch (Exception e) { throw new RuntimeException("Failed to find handler field", e); }
    }

    private Entity ls$getTarget() {
        try {
            if (lifeseries$entityField == null) {
                for (java.lang.reflect.Field f : this.getClass().getDeclaredFields()) {
                    if (Entity.class.isAssignableFrom(f.getType())) {
                        f.setAccessible(true);
                        lifeseries$entityField = f;
                        break;
                    }
                }
            }
            return (Entity) lifeseries$entityField.get(this);
        } catch (Exception e) { throw new RuntimeException("Failed to find entity field", e); }
    }
    ^///?} else {
    @Shadow(aliases = {"this$0"})
    @Final
    ServerGamePacketListenerImpl field_28963;

    @Shadow(aliases = {"val$entity"})
    @Final
    Entity val$target;
    //?}
//?}

    //~ if !fabric && <= 1.20 'field_28963' -> 'ls$getHandler()' {
    //~ if !fabric && <= 1.20 'val$target' -> 'ls$getTarget()' {
    @Inject(method = "onInteraction(Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/Vec3;)V", at = @At(value = "HEAD"), cancellable = true)
    public void onPlayerInteractEntity(InteractionHand hand, Vec3 hitPosition, CallbackInfo info) {
        Player player = this.field_28963.player;
        Level world = player.level();

        EntityHitResult hitResult = new EntityHitResult(val$target, hitPosition.add(val$target.getX(), val$target.getY(), val$target.getZ()));
        InteractionResult result = Events.onRightClickEntity(player, world, hand, val$target, hitResult);

        if (result != InteractionResult.PASS) {
            info.cancel();
        }
    }

    @Inject(method = "onInteraction(Lnet/minecraft/world/InteractionHand;)V", at = @At(value = "HEAD"), cancellable = true)
    public void onPlayerInteractEntity(InteractionHand hand, CallbackInfo info) {
        Player player = this.field_28963.player;
        Level world = player.level();

        InteractionResult result = Events.onRightClickEntity(player, world, hand, val$target, null);

        if (result != InteractionResult.PASS) {
            info.cancel();
        }
    }
    //~}
    //~}
}
*///?} else {
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = MinecraftServer.class)
public interface ServerPlayNetworkHandlerInteractEntityHandlerMixin {
}
//?}

