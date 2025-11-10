package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers;

import net.mat0u5.lifeseries.compatibilities.CompatibilityManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.*;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public enum Superpowers {
    NULL,

    TIME_CONTROL,
    CREAKING,
    WIND_CHARGE,
    ASTRAL_PROJECTION,
    SUPER_PUNCH,
    MIMICRY,
    TELEPORTATION,
    LISTENING,
    SHADOW_PLAY,
    FLIGHT,
    PLAYER_DISGUISE,
    ANIMAL_DISGUISE,
    TRIPLE_JUMP,
    INVISIBILITY,
    SUPERSPEED,
    NECROMANCY;

    @Nullable
    public Superpower getInstance(ServerPlayer player) {
        if (this == TIME_CONTROL) return new TimeControl(player);
        if (this == WIND_CHARGE) return new WindCharge(player);
        if (this == ASTRAL_PROJECTION) return new AstralProjection(player);
        if (this == PLAYER_DISGUISE) return new PlayerDisguise(player);
        if (this == ANIMAL_DISGUISE) return new AnimalDisguise(player);
        if (this == SUPER_PUNCH) return new SuperPunch(player);
        if (this == MIMICRY) return new Mimicry(player);
        if (this == TELEPORTATION) return new Teleportation(player);
        if (this == SHADOW_PLAY) return new ShadowPlay(player);
        if (this == INVISIBILITY) return new Invisibility(player);
        if (this == TRIPLE_JUMP) return new TripleJump(player);
        if (this == SUPERSPEED) return new Superspeed(player);
        if (this == NECROMANCY) return new Necromancy(player);
        //? if >= 1.21.2 {
        /*if (this == CREAKING) return new CreakingPower(player);
        if (this == FLIGHT) return new Flight(player);
        *///?}
        if (CompatibilityManager.voicechatLoaded()) {
            if (this == LISTENING) return new Listening(player);
        }
        return null;
    }

    public String getString() {
        return this.toString().toLowerCase(Locale.ROOT);
    }

    public static List<Superpowers> getImplemented() {
        List<Superpowers> result = new ArrayList<>(List.of(Superpowers.values()));
        result.remove(NULL);
        //? if <= 1.21 {
        result.remove(CREAKING);
        result.remove(FLIGHT);
        //?}
        if (!CompatibilityManager.voicechatLoaded()) {
            result.remove(LISTENING);
        }
        return result;
    }

    public static List<String> getImplementedStr() {
        List<String> result = new ArrayList<>();
        for (Superpowers superpower : getImplemented()) {
            result.add(superpower.getString());
        }
        return result;
    }

    public static List<Superpowers> getAll() {
        List<Superpowers> result = new ArrayList<>(List.of(Superpowers.values()));
        result.remove(NULL);
        return result;
    }

    public static List<String> getAllStr() {
        List<String> result = new ArrayList<>();
        for (Superpowers superpower : getAll()) {
            result.add(superpower.getString());
        }
        return result;
    }

    public static Superpowers fromString(String superpower) {
        try {
            return Enum.valueOf(Superpowers.class, superpower.toUpperCase(Locale.ROOT));
        } catch(Exception e) {}
        return Superpowers.NULL;
    }
}
