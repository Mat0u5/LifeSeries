package net.mat0u5.lifeseries.features;

import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphComponent;
import net.mat0u5.lifeseries.utils.ClientUtils;
import net.mat0u5.lifeseries.utils.interfaces.IMorph;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;
//? if >= 1.21.2
/*import net.minecraft.entity.SpawnReason;*/

public class Morph {

    public static void clientTick(MorphComponent morphComponent) {
        EntityType<?> morph = morphComponent.morph;
        LivingEntity dummy = morphComponent.dummy;

        if(morphComponent.isMorphed() && morph != null){
            PlayerEntity player = ClientUtils.getPlayer(morphComponent.playerUUID);
            if (player == null) return;

            boolean isHorse = morph == EntityType.HORSE || morph == EntityType.SKELETON_HORSE || morph == EntityType.ZOMBIE_HORSE;
            boolean fixedHead = isHorse || morph == EntityType.GOAT;
            boolean clampedPitch = isHorse || morph == EntityType.GOAT;
            boolean reversePitch = morph == EntityType.PHANTOM;

            if (dummy == null || dummy.getType() != morph) {
                //? if <= 1.21 {
                Entity entity = morph.create(player.getWorld());
                //?} else if <= 1.21.6 {
                /*Entity entity = morph.create(player.getWorld(), SpawnReason.COMMAND);
                *///?} else {
                /*Entity entity = morph.create(player.getEntityWorld(), SpawnReason.COMMAND);
                *///?}
                if (entity != null) ((IMorph) entity).setFromMorph(true);
                if(!(entity instanceof LivingEntity)){
                    morph = null;
                    return;
                }
                dummy = (LivingEntity) entity;
            }
            //? if <= 1.21.4 {
            dummy.prevX = player.prevX;
            dummy.prevY = player.prevY;
            dummy.prevZ = player.prevZ;
            dummy.prevBodyYaw = player.prevBodyYaw;
            if (!fixedHead) {
                dummy.prevHeadYaw = player.prevHeadYaw;
            }
            else {
                dummy.prevHeadYaw = player.prevBodyYaw;
            }

            if (!clampedPitch) {
                dummy.prevPitch = player.prevPitch;
            }
            else {
                dummy.prevPitch = Math.clamp(player.prevPitch, -28, 28);
            }
            if (reversePitch) dummy.prevPitch *= -1;
            //?} else {
            /*dummy.lastX = player.lastX;
            dummy.lastY = player.lastY;
            dummy.lastZ = player.lastZ;
            dummy.lastBodyYaw = player.lastBodyYaw;
            if (!fixedHead) {
                dummy.lastHeadYaw = player.lastHeadYaw;
            }
            else {
                dummy.lastHeadYaw = player.lastBodyYaw;
            }

            if (!clampedPitch) {
                dummy.lastPitch = player.lastPitch;
            }
            else {
                dummy.lastPitch = Math.clamp(player.lastPitch, -28, 28);
            }
            if (reversePitch) dummy.lastPitch *= -1;
            *///?}

            //Some math to synchronize the morph limbs with the player limbs
            //? if <= 1.21.4 {
            float prevPlayerSpeed = (player.limbAnimator.getSpeed(-1)+player.limbAnimator.getSpeed())/2;
            //?} else {
            /*float prevPlayerSpeed = (player.limbAnimator.getAmplitude(-1)+player.limbAnimator.getSpeed())/2;
             *///?}
            dummy.limbAnimator.setSpeed(prevPlayerSpeed);
            //? if <= 1.21 {
            dummy.limbAnimator.updateLimbs(player.limbAnimator.getPos() - dummy.limbAnimator.getPos(), 1);
            //?} else if <= 1.21.4 {
            /*dummy.limbAnimator.updateLimbs(player.limbAnimator.getPos() - dummy.limbAnimator.getPos(), 1, 1);
             *///?} else {
            /*dummy.limbAnimator.updateLimbs(player.limbAnimator.getAnimationProgress() - dummy.limbAnimator.getAnimationProgress(), 1, 1);
             *///?}
            dummy.limbAnimator.setSpeed(player.limbAnimator.getSpeed());

            dummy.lastHandSwingProgress = player.lastHandSwingProgress;
            dummy.handSwingProgress = player.handSwingProgress;
            dummy.handSwinging = player.handSwinging;
            dummy.handSwingTicks = player.handSwingTicks;

            dummy.lastRenderX = player.lastRenderX;
            dummy.lastRenderY = player.lastRenderY;
            dummy.lastRenderZ = player.lastRenderZ;

            dummy.setPosition(WorldUtils.getEntityPos(player));
            dummy.setBodyYaw(player.bodyYaw);
            if (!fixedHead) {
                dummy.setHeadYaw(player.headYaw);
            }
            else {
                dummy.setHeadYaw(player.bodyYaw);
            }

            if (!clampedPitch) {
                dummy.setPitch(player.getPitch());
            }
            else {
                dummy.setPitch(Math.clamp(player.getPitch(), -28, 28));
            }
            if (reversePitch) dummy.setPitch(dummy.getPitch() * -1);

            dummy.setSneaking(player.isSneaking());
            dummy.age = player.age;
            dummy.setOnGround(player.isOnGround());

            morphComponent.morph = morph;
            morphComponent.dummy = dummy;
        }
    }
}
