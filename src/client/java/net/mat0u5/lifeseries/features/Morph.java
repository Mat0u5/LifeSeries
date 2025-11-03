package net.mat0u5.lifeseries.features;

import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphComponent;
import net.mat0u5.lifeseries.utils.ClientUtils;
import net.mat0u5.lifeseries.utils.interfaces.IMorph;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
//? if >= 1.21.2
/*import net.minecraft.entity.SpawnReason;*/


public class Morph {

    public static boolean showArmor = false;
    public static boolean showHandItems = true;

    public static void clientTick(MorphComponent morphComponent) {
        EntityType<?> morph = morphComponent.morph;
        LivingEntity dummy = morphComponent.dummy;

        if(morphComponent.isMorphed() && morph != null){
            Player player = ClientUtils.getPlayer(morphComponent.playerUUID);
            if (player == null) return;

            boolean isHorse = morph == EntityType.HORSE || morph == EntityType.SKELETON_HORSE || morph == EntityType.ZOMBIE_HORSE;
            boolean fixedHead = isHorse || morph == EntityType.GOAT;
            boolean clampedPitch = isHorse || morph == EntityType.GOAT;
            boolean reversePitch = morph == EntityType.PHANTOM;

            if (dummy == null || dummy.getType() != morph) {
                //? if <= 1.21 {
                Entity entity = morph.create(player.level());
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
            dummy.xo = player.xo;
            dummy.yo = player.yo;
            dummy.zo = player.zo;
            dummy.yBodyRotO = player.yBodyRotO;
            if (!fixedHead) {
                dummy.yHeadRotO = player.yHeadRotO;
            }
            else {
                dummy.yHeadRotO = player.yBodyRotO;
            }

            if (!clampedPitch) {
                dummy.xRotO = player.xRotO;
            }
            else {
                dummy.xRotO = Math.clamp(player.xRotO, -28, 28);
            }
            if (reversePitch) dummy.xRotO *= -1;
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
            float prevPlayerSpeed = (player.walkAnimation.speed(-1)+player.walkAnimation.speed())/2;
            //?} else {
            /*float prevPlayerSpeed = (player.limbAnimator.getAmplitude(-1)+player.limbAnimator.getSpeed())/2;
             *///?}
            dummy.walkAnimation.setSpeed(prevPlayerSpeed);
            //? if <= 1.21 {
            dummy.walkAnimation.update(player.walkAnimation.position() - dummy.walkAnimation.position(), 1);
            //?} else if <= 1.21.4 {
            /*dummy.limbAnimator.updateLimbs(player.limbAnimator.getPos() - dummy.limbAnimator.getPos(), 1, 1);
             *///?} else {
            /*dummy.limbAnimator.updateLimbs(player.limbAnimator.getAnimationProgress() - dummy.limbAnimator.getAnimationProgress(), 1, 1);
             *///?}
            dummy.walkAnimation.setSpeed(player.walkAnimation.speed());

            dummy.oAttackAnim = player.oAttackAnim;
            dummy.attackAnim = player.attackAnim;
            dummy.swinging = player.swinging;
            dummy.swingTime = player.swingTime;

            dummy.xOld = player.xOld;
            dummy.yOld = player.yOld;
            dummy.zOld = player.zOld;

            dummy.setPos(player.ls$getEntityPos());
            dummy.setYBodyRot(player.yBodyRot);
            if (!fixedHead) {
                dummy.setYHeadRot(player.yHeadRot);
            }
            else {
                dummy.setYHeadRot(player.yBodyRot);
            }

            if (!clampedPitch) {
                dummy.setXRot(player.getXRot());
            }
            else {
                dummy.setXRot(Math.clamp(player.getXRot(), -28, 28));
            }
            if (reversePitch) dummy.setXRot(dummy.getXRot() * -1);

            dummy.setShiftKeyDown(player.isShiftKeyDown());
            dummy.tickCount = player.tickCount;
            dummy.setOnGround(player.onGround());

            for (EquipmentSlot slot : EquipmentSlot.values()) {
                dummy.setItemSlot(slot, showArmor ? player.getItemBySlot(slot) : Items.AIR.getDefaultInstance());
            }

            for (InteractionHand hand : InteractionHand.values()) {
                dummy.setItemInHand(hand, showHandItems ? player.getItemInHand(hand) : Items.AIR.getDefaultInstance());
            }

            dummy.setPose(player.getPose());

            morphComponent.morph = morph;
            morphComponent.dummy = dummy;
        }
    }
}
