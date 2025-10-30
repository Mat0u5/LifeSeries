package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.ToggleableSuperpower;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;


import static net.mat0u5.lifeseries.Main.livesManager;
//? if <= 1.21.6 {
import net.mat0u5.lifeseries.entity.fakeplayer.FakePlayer;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.minecraft.network.DisconnectionInfo;

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
    private Vec3d startedPos;
    @Nullable
    private ServerWorld startedWorld;
    private float[] startedLooking = new float[2];
    private GameMode startedGameMode = GameMode.SURVIVAL;

    public AstralProjection(ServerPlayerEntity player) {
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
        ServerPlayerEntity player = getPlayer();
        if (player == null) return;
        if (player.isSpectator()) return;
        player.playSoundToPlayer(SoundEvents.BLOCK_TRIAL_SPAWNER_OMINOUS_ACTIVATE, SoundCategory.MASTER, 0.3f, 1);

        String fakePlayerName = "`"+player.getNameForScoreboard();

        startedPos = player.ls$getEntityPos();
        startedLooking[0] = player.getYaw();
        startedLooking[1] = player.getPitch();
        startedWorld = PlayerUtils.getServerWorld(player);
        if (startedWorld == null) return;
        startedGameMode = player.interactionManager.getGameMode();
        Vec3d velocity = player.getVelocity();
        player.changeGameMode(GameMode.SPECTATOR);
        PlayerInventory inv = player.getInventory();

        //? if <= 1.21.6 {
        FakePlayer.createFake(fakePlayerName, server, startedPos, startedLooking[0], startedLooking[1], PlayerUtils.getServerWorld(player).getRegistryKey(),
                startedGameMode, false, inv, player.getUuid()).thenAccept((fakePlayer) -> {
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
        ServerPlayerEntity player = getPlayer();
        if (player == null) return;
        String name = TextUtils.textToLegacyString(player.getStyledDisplayName());
        NetworkHandlerServer.sendPlayerDisguise(clone.getUuid().toString(), clone.getName().getString(), player.getUuid().toString(), name);
        //?}
    }

    public void cancelProjection() {
        ServerPlayerEntity player = getPlayer();
        if (player == null) return;

        Vec3d toBackPos = startedPos;
        if (clone != null) {
            toBackPos = clone.ls$getEntityPos();
            //? if <= 1.21.6 {
            clone.networkHandler.onDisconnected(new DisconnectionInfo(Text.empty()));
            NetworkHandlerServer.sendPlayerDisguise(clone.getUuid().toString(), clone.getName().getString(), "", "");
            //?} else {
            /*clone.discard();
            *///?}
        }

        if (player.ls$isDead()) return;

        if (startedWorld != null && toBackPos != null) {
            PlayerUtils.teleport(player, startedWorld, toBackPos, startedLooking[0], startedLooking[1]);
        }
        player.changeGameMode(startedGameMode);
        player.playSoundToPlayer(SoundEvents.ENTITY_EVOKER_DEATH, SoundCategory.MASTER, 0.3f, 1);
    }


    //? if <= 1.21 {
    public void onDamageClone(DamageSource source, float amount) {
     //?} else {
    /*public void onDamageClone(ServerWorld world, DamageSource source, float amount) {
    *///?}
        deactivate();
        ServerPlayerEntity player = getPlayer();
        if (player == null) return;
        //? if <= 1.21 {
        PlayerUtils.damage(player, source, amount);
         //?} else {
        /*PlayerUtils.damage(player, world, source, amount);
        *///?}
    }
}
