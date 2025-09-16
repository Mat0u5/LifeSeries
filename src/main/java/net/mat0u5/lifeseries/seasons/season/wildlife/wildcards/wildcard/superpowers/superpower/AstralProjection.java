package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.ToggleableSuperpower;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;


import static net.mat0u5.lifeseries.Main.livesManager;
//? if <= 1.21.6 {
import net.mat0u5.lifeseries.entity.fakeplayer.FakePlayer;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.text.Text;
import java.util.EnumSet;
import static net.mat0u5.lifeseries.Main.server;
//?}
//? if >= 1.21.9 {
/*import net.minecraft.entity.decoration.MannequinEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
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

        startedPos = player.getPos();
        startedLooking[0] = player.getYaw();
        startedLooking[1] = player.getPitch();
        startedWorld = PlayerUtils.getServerWorld(player);
        startedGameMode = player.interactionManager.getGameMode();
        player.changeGameMode(GameMode.SPECTATOR);
        PlayerInventory inv = player.getInventory();

        //? if <= 1.21.6 {
        FakePlayer.createFake(fakePlayerName, server, startedPos, startedLooking[0], startedLooking[1], server.getOverworld().getRegistryKey(),
                startedGameMode, false, inv, player.getUuid()).thenAccept((fakePlayer) -> {
            clone = fakePlayer;
            String name = TextUtils.textToLegacyString(player.getStyledDisplayName());
            NetworkHandlerServer.sendPlayerDisguise(clone.getUuid().toString(), clone.getName().getString(), player.getUuid().toString(), name);
        });
        //?} else {
        /*clone = EntityType.MANNEQUIN.create(startedWorld, SpawnReason.COMMAND);
        clone.setBodyYaw(player.getBodyYaw());
        clone.setHeadYaw(player.getHeadYaw());
        clone.setPitch(player.getPitch());
        clone.setPos(player.getX(), player.getY(), player.getZ());
        clone.setCustomName(player.getStyledDisplayName());
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            clone.equipStack(slot, player.getEquippedStack(slot));
        }
        startedWorld.spawnEntity(clone);
        *///?}
    }

    public void cancelProjection() {
        ServerPlayerEntity player = getPlayer();
        if (player == null) return;

        Vec3d toBackPos = startedPos;
        if (clone != null) {
            toBackPos = clone.getPos();
            //? if <= 1.21.6 {
            clone.networkHandler.onDisconnected(new DisconnectionInfo(Text.empty()));
            NetworkHandlerServer.sendPlayerDisguise(clone.getUuid().toString(), clone.getName().getString(), "", "");
            //?} else {
            /*clone.discard();
            *///?}
        }

        if (!livesManager.isAlive(player)) return;

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
