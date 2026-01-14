package net.mat0u5.lifeseries.entity.angrysnowman;

import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;import net.minecraft.world.entity.ai.attributes.AttributeSupplier;import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

//? if <= 1.21.9 {
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.SnowGolem;

//?} else {
/*import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.golem.SnowGolem;
 *///?}

import static net.mat0u5.lifeseries.Main.livesManager;

public class AngrySnowman extends SnowGolem {
    public static final SoundEvent HURT_SOUND = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_snowman_hit"));
    public static final SoundEvent GROWL = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_snowman_growl"));
    //? if <= 1.21.9 {
    public static final ResourceLocation ID = IdentifierHelper.mod("angrysnowman");
    //?} else {
    /*public static final Identifier ID = IdentifierHelper.mod("angrysnowman");
    *///?}


    public AngrySnowman(EntityType<? extends SnowGolem> entityType, Level level) {
        super(entityType, level);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, (double)1.25F, 80, 20.0F));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, (double)1.0F, 1.0000001E-5F));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, (double)20.0F)
                .add(Attributes.MOVEMENT_SPEED, (double)0.2F)
                .add(Attributes.ATTACK_KNOCKBACK, (double)1F)
                .add(Attributes.ATTACK_DAMAGE, 1);
    }

    private long ticks = 0;
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) return;
        this.setPumpkin(false);
        ticks++;
        if (ticks % 20 == 0) {
            ServerPlayer closestPlayer = null;
            double closestDistance = 100000;
            for (ServerPlayer player : livesManager.getAlivePlayers()) {
                if (player.isSpectator()) continue;
                if (player.isCreative()) continue;

                double distance = this.distanceTo(player);
                if (distance < closestDistance && distance < 30) {
                    closestPlayer = player;
                    closestDistance = distance;
                }
            }
            this.setTarget(closestPlayer);
        }
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return GROWL;
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return HURT_SOUND;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return HURT_SOUND;
    }
}
