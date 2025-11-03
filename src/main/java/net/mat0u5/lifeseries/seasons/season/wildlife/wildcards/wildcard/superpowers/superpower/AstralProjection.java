package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.ToggleableSuperpower;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;


import static net.mat0u5.lifeseries.Main.livesManager;
//? if <= 1.21.6 {
import net.mat0u5.lifeseries.entity.fakeplayer.FakePlayer;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.utils.other.TextUtils;

import static net.mat0u5.lifeseries.Main.server;
//?}
//? if >= 1.21.9 {
/*import net.minecraft.entity.decoration.MannequinEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.mat0u5.lifeseries.mixin.MannequinEntityAccessor;
import net.minecraft.component.type.ProfileComponent;
*///?}

public class AstralProjection extends ToggleableSuperpower {
    //? if <= 1.21.6 {
    @Nullable
    public FakePlayer clone;
    //?} else {
    /*@Nullable
    public MannequinEntity clone;
    *///?}
    @Nullable
    private Vec3 startedPos;
    @Nullable
    private ServerLevel startedWorld;
    private float[] startedLooking = new float[2];
    private GameType startedGameMode = GameType.SURVIVAL;

    public AstralProjection(ServerPlayer player) {
        super(player);
    }

    @Override
    public Superpowers getSuperpower() {
        return Superpowers.ASTRAL_PROJECTION;
    }

    @Override
    public void activate() {
        super.activate();
        resetParams();
        startProjection();
    }

    @Override
    public void deactivate() {
        super.deactivate();
        cancelProjection();
        resetParams();
    }

    @Override
    public int deactivateCooldownMillis() {
        return 5000;
    }

    public void resetParams() {
        clone = null;
        startedPos = null;
        startedLooking = new float[2];
        startedWorld = null;
    }

    public void startProjection() {
        ServerPlayer player = getPlayer();
        if (player == null) return;
        if (player.isSpectator()) return;
        player.playNotifySound(SoundEvents.TRIAL_SPAWNER_OMINOUS_ACTIVATE, SoundSource.MASTER, 0.3f, 1);

        String fakePlayerName = "`"+player.getScoreboardName();

        startedPos = player.ls$getEntityPos();
        startedLooking[0] = player.getYRot();
        startedLooking[1] = player.getXRot();
        startedWorld = PlayerUtils.getServerWorld(player);
        if (startedWorld == null) return;
        startedGameMode = player.gameMode.getGameModeForPlayer();
        Vec3 velocity = player.getDeltaMovement();
        player.setGameMode(GameType.SPECTATOR);
        Inventory inv = player.getInventory();

        //? if <= 1.21.6 {
        FakePlayer.createFake(fakePlayerName, server, startedPos, startedLooking[0], startedLooking[1], PlayerUtils.getServerWorld(player).dimension(),
                startedGameMode, false, inv, player.getUUID()).thenAccept((fakePlayer) -> {
            clone = fakePlayer;
            sendDisguisePacket();
        });
        //?} else {
        /*clone = EntityType.MANNEQUIN.create(startedWorld, SpawnReason.COMMAND);
        if (clone == null) return;

        clone.setPos(player.getX(), player.getY(), player.getZ());
        clone.setCustomName(player.getStyledDisplayName());
        clone.setCustomNameVisible(true);
        if (clone instanceof MannequinEntityAccessor mannequinAccessor) {
            mannequinAccessor.ls$setMannequinProfile(ProfileComponent.ofStatic(player.getGameProfile()));
            mannequinAccessor.ls$setDescription(Text.of("Astral Projection"));
            mannequinAccessor.ls$setHideDescription(true);
        }
        clone.age = -2_000_000;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            clone.equipStack(slot, player.getEquippedStack(slot));
        }
        for (Hand hand : Hand.values()) {
            clone.setStackInHand(hand, player.getStackInHand(hand));
        }

        startedWorld.spawnEntity(clone);

        TaskScheduler.scheduleTask(1, () -> {
            clone.headYaw = player.headYaw;
            clone.lastHeadYaw = player.lastHeadYaw;
            clone.bodyYaw = player.bodyYaw;
            clone.lastBodyYaw = player.lastBodyYaw;
            clone.lastYaw = player.lastYaw;
            clone.setPitch(player.getPitch());
            clone.setVelocity(velocity);
            clone.velocityModified = true;
            clone.velocityDirty = true;
            clone.refreshPositionAndAngles(player.ls$getEntityPos(), player.getYaw(), player.getPitch());
        });
        *///?}
    }

    public void sendDisguisePacket() {
        //? if <= 1.21.6 {
        if (!this.active) return;
        if (clone == null) return;
        ServerPlayer player = getPlayer();
        if (player == null) return;
        String name = TextUtils.textToLegacyString(player.getFeedbackDisplayName());
        NetworkHandlerServer.sendPlayerDisguise(clone.getUUID().toString(), clone.getName().getString(), player.getUUID().toString(), name);
        //?}
    }

    public void cancelProjection() {
        ServerPlayer player = getPlayer();
        if (player == null) return;

        Vec3 toBackPos = startedPos;
        if (clone != null) {
            toBackPos = clone.ls$getEntityPos();
            //? if <= 1.21.6 {
            clone.connection.onDisconnect(new DisconnectionDetails(Component.empty()));
            NetworkHandlerServer.sendPlayerDisguise(clone.getUUID().toString(), clone.getName().getString(), "", "");
            //?} else {
            /*clone.discard();
            *///?}
        }

        if (player.ls$isDead()) return;

        if (startedWorld != null && toBackPos != null) {
            PlayerUtils.teleport(player, startedWorld, toBackPos, startedLooking[0], startedLooking[1]);
        }
        player.setGameMode(startedGameMode);
        player.playNotifySound(SoundEvents.EVOKER_DEATH, SoundSource.MASTER, 0.3f, 1);
    }


    //? if <= 1.21 {
    public void onDamageClone(DamageSource source, float amount) {
     //?} else {
    /*public void onDamageClone(ServerWorld world, DamageSource source, float amount) {
    *///?}
        deactivate();
        ServerPlayer player = getPlayer();
        if (player == null) return;
        //? if <= 1.21 {
        PlayerUtils.damage(player, source, amount);
         //?} else {
        /*PlayerUtils.damage(player, world, source, amount);
        *///?}
    }
}
