package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.ToggleableSuperpower;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.LevelUtils;
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
import static net.mat0u5.lifeseries.Main.server;
//? if <= 1.21.6 {
/*import net.mat0u5.lifeseries.entity.fakeplayer.FakePlayer;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.utils.other.TextUtils;
*///?}
//? if > 1.20.5 && <= 1.21.6
//import net.minecraft.network.DisconnectionDetails;

//? if >= 1.21.9 {
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.minecraft.world.entity.decoration.Mannequin;
import net.mat0u5.lifeseries.mixin.MannequinAccessor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.component.ResolvableProfile;
//?}

public class AstralProjection extends ToggleableSuperpower {
    //? if <= 1.21.6 {
    /*@Nullable
    public FakePlayer clone;
    *///?} else {
    @Nullable
    public Mannequin clone;
    //?}
    @Nullable
    private Vec3 startedPos;
    @Nullable
    private ServerLevel startedLevel;
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
        startedLevel = null;
    }

    public void startProjection() {
        ServerPlayer player = getPlayer();
        if (player == null) return;
        if (player.isSpectator()) return;
        //? if <= 1.21 {
        /*player.ls$playNotifySound(SoundEvents.EVOKER_PREPARE_ATTACK, SoundSource.MASTER, 0.3f, 1);
        *///?} else {
        player.ls$playNotifySound(SoundEvents.TRIAL_SPAWNER_OMINOUS_ACTIVATE, SoundSource.MASTER, 0.3f, 1);
        //?}

        String fakePlayerName = "`"+player.getScoreboardName();

        startedPos = player.position();
        startedLooking[0] = player.getYRot();
        startedLooking[1] = player.getXRot();
        startedLevel = player.ls$getServerLevel();
        if (startedLevel == null) return;
        startedGameMode = player.gameMode.getGameModeForPlayer();
        Vec3 velocity = player.getDeltaMovement();
        player.setGameMode(GameType.SPECTATOR);
        Inventory inv = player.getInventory();

        //? if <= 1.21.6 {
        /*FakePlayer.createFake(fakePlayerName, server, startedPos, startedLooking[0], startedLooking[1], player.ls$getServerLevel().dimension(),
                startedGameMode, false, inv, player.getUUID()).thenAccept((fakePlayer) -> {
            clone = fakePlayer;
            sendDisguisePacket();
        });
        *///?} else {
        clone = EntityType.MANNEQUIN.create(startedLevel, EntitySpawnReason.COMMAND);
        if (clone == null) return;

        clone.setPosRaw(player.getX(), player.getY(), player.getZ());
        clone.setCustomName(player.getFeedbackDisplayName());
        clone.setCustomNameVisible(true);
        if (clone instanceof MannequinAccessor mannequinAccessor) {
            mannequinAccessor.ls$setMannequinProfile(ResolvableProfile.createResolved(player.getGameProfile()));
            mannequinAccessor.ls$setDescription(Component.nullToEmpty("Astral Projection"));
            mannequinAccessor.ls$setHideDescription(true);
        }
        clone.tickCount = -2_000_000;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            clone.setItemSlot(slot, player.getItemBySlot(slot));
        }
        for (InteractionHand hand : InteractionHand.values()) {
            clone.setItemInHand(hand, player.getItemInHand(hand));
        }

        startedLevel.addFreshEntity(clone);

        TaskScheduler.scheduleTask(1, () -> {
            clone.yHeadRot = player.yHeadRot;
            clone.yHeadRotO = player.yHeadRotO;
            clone.yBodyRot = player.yBodyRot;
            clone.yBodyRotO = player.yBodyRotO;
            clone.yRotO = player.yRotO;
            clone.setXRot(player.getXRot());
            clone.setDeltaMovement(velocity);
            clone.hurtMarked = true;
            //? if <= 1.21.9 {
            /*clone.hasImpulse = true;
            *///?} else {
            clone.needsSync = true;
            //?}
            clone.snapTo(player.position(), player.getYRot(), player.getXRot());
        });
        //?}
    }

    public void sendDisguisePacket() {
        //? if <= 1.21.6 {
        /*if (!this.active) return;
        if (clone == null) return;
        ServerPlayer player = getPlayer();
        if (player == null) return;
        String name = TextUtils.textToLegacyString(player.getDisplayName());
        NetworkHandlerServer.sendPlayerDisguise(clone.getUUID().toString(), clone.getName().getString(), player.getUUID().toString(), name);
        *///?}
    }

    public void cancelProjection() {
        ServerPlayer player = getPlayer();
        if (player == null) return;

        Vec3 toBackPos = startedPos;
        if (clone != null) {
            toBackPos = clone.position();
            //? if <= 1.21.6 {

            /*//? if <= 1.20.5 {
            /^clone.connection.onDisconnect(Component.empty());
            ^///?} else {
            clone.connection.onDisconnect(new DisconnectionDetails(Component.empty()));
            //?}
            NetworkHandlerServer.sendPlayerDisguise(clone.getUUID().toString(), clone.getName().getString(), "", "");
            *///?} else {
            clone.discard();
            //?}
        }

        if (player.ls$isDead()) return;

        if (startedLevel != null && toBackPos != null) {
            LevelUtils.teleport(player, startedLevel, toBackPos, startedLooking[0], startedLooking[1]);
        }
        player.setGameMode(startedGameMode);
        player.ls$playNotifySound(SoundEvents.EVOKER_DEATH, SoundSource.MASTER, 0.3f, 1);
    }


    //? if <= 1.21 {
    /*public void onDamageClone(DamageSource source, float amount) {
     *///?} else {
    public void onDamageClone(ServerLevel level, DamageSource source, float amount) {
    //?}
        deactivate();
        ServerPlayer player = getPlayer();
        if (player == null) return;
        //? if <= 1.21 {
        /*player.ls$hurt(source, amount);
         *///?} else {
        player.ls$hurt(level, source, amount);
        //?}
    }
}
