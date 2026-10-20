package net.mat0u5.lifeseries.entity.fakeplayer;

import com.mojang.authlib.GameProfile;
import net.mat0u5.matlib.entity.fakeplayer.FakeServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

//? if > 1.20 {
import net.minecraft.server.level.ClientInformation;
//?}

//? if <= 1.21.6 {
/*import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.AstralProjection;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
*///?}

@SuppressWarnings("EntityConstructor")
public class FakePlayer extends FakeServerPlayer {
    //? if <= 1.20 {
    /*private FakePlayer(MinecraftServer server, ServerLevel levelIn, GameProfile profile) {
        super(server, levelIn, profile);
    }
    *///?} else {
    private FakePlayer(MinecraftServer server, ServerLevel levelIn, GameProfile profile, ClientInformation cli) {
        super(server, levelIn, profile, cli);
    }
    //?}

    //? if <= 1.21.6 {
    /*public UUID shadow;

    public static CompletableFuture<FakePlayer> createFake(
            String username, MinecraftServer server, Vec3 pos, double yaw, double pitch,
            ResourceKey<Level> dimensionId, GameType gamemode, boolean flying, Inventory inv,
            UUID shadow) {
        //? if <= 1.20 {
        /^Factory<FakePlayer> factory = (server1, level, profile) -> {
            FakePlayer instance = new FakePlayer(server1, level, profile);
            instance.shadow = shadow;
            return instance;
        };
        ^///?} else {
        Factory<FakePlayer> factory = (server1, level, profile, clientInformation) -> {
            FakePlayer instance = new FakePlayer(server1, level, profile, clientInformation);
            instance.shadow = shadow;
            return instance;
        };
        //?}
        return FakeServerPlayer.create(factory, username, server, pos, yaw, pitch, dimensionId, gamemode, flying, inv);
    }

    private AstralProjection getProjection() {
        if (shadow == null) return null;
        ServerPlayer player = PlayerUtils.getPlayer(shadow);
        if (player == null) return null;
        if (!SuperpowersWildcard.hasActivatedPower(player, Superpowers.ASTRAL_PROJECTION)) return null;
        if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof AstralProjection projection) {
            return projection;
        }
        return null;
    }

    @Override
    public boolean shouldStayConnected() {
        AstralProjection projection = getProjection();
        if (projection == null) return false;
        projection.clone = this;
        return true;
    }

    @Override
    //? if <= 1.21 {
    //public boolean hurt(DamageSource source, float amount) {
    //?} else {
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
    //?}
        AstralProjection projection = getProjection();
        if (projection != null) {
            //? if <= 1.21 {
            //projection.onDamageClone(source, amount);
            //?} else {
            projection.onDamageClone(level, source, amount);
            //?}
        }
        //? if <= 1.21 {
        //return super.hurt(source, amount);
        //?} else {
        return super.hurtServer(level, source, amount);
        //?}
    }
    *///?}
}
