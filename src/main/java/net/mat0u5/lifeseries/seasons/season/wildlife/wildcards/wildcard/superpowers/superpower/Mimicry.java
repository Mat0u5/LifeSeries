package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpower;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

//? if >= 1.21.9 {
import net.mat0u5.lifeseries.mixin.MannequinAccessor;
import net.minecraft.world.entity.decoration.Mannequin;
//?}

public class Mimicry extends Superpower {

    private Superpower mimic = null;

    public Mimicry(ServerPlayer player) {
        super(player);
    }

    @Override
    public Superpowers getSuperpower() {
        return Superpowers.MIMICRY;
    }

    @Override
    public int getCooldownMillis() {
        return 300000;
    }

    @Override
    public void activate() {
        ServerPlayer player = getPlayer();
        if (player == null) return;
        Entity lookingAt = PlayerUtils.getEntityLookingAt(player, 50);
        boolean isLookingAtPlayer = false;
        boolean successfullyMimicked = false;
        if (lookingAt != null)  {
            //? if >= 1.21.9 {
            if (lookingAt instanceof Mannequin mannequin && mannequin instanceof MannequinAccessor mannequinAccessor && mannequin.tickCount < 0) {
                ServerPlayer lookingAtPlayer = PlayerUtils.getPlayer(mannequinAccessor.ls$getMannequinProfile().partialProfile().id());
                if (lookingAtPlayer != null) {
                    lookingAt = lookingAtPlayer;
                }
            }
            //?}
            if (lookingAt instanceof ServerPlayer lookingAtPlayer) {
                lookingAtPlayer = PlayerUtils.getPlayerOrProjection(lookingAtPlayer);
                isLookingAtPlayer = true;
                Superpowers mimicPower = SuperpowersWildcard.getSuperpower(lookingAtPlayer);
                if (!PlayerUtils.isFakePlayer(lookingAtPlayer) && mimicPower != null) {
                    if (mimicPower != Superpowers.NULL && mimicPower != Superpowers.MIMICRY) {
                        mimic = mimicPower.getInstance(player);
                        successfullyMimicked = true;
                        PlayerUtils.displayMessageToPlayer(player, TextUtils.format("Mimicked superpower of {}", lookingAtPlayer), 65);
                        player.ls$playNotifySound(SoundEvents.CHICKEN_EGG, SoundSource.MASTER, 0.3f, 1);
                    }
                    if (mimicPower == Superpowers.MIMICRY) {
                        PlayerUtils.displayMessageToPlayer(player, Component.literal("You cannot mimic that power."), 65);
                        return;
                    }
                }
            }
        }

        if (!isLookingAtPlayer) {
            PlayerUtils.displayMessageToPlayer(player, Component.nullToEmpty("You are not looking at a player."), 65);
            return;
        }
        if (!successfullyMimicked) {
            PlayerUtils.displayMessageToPlayer(player, Component.nullToEmpty("That player does not have a superpower."), 65);
            return;
        }
        super.activate();
        sendCooldownPacket();
    }

    @Override
    public void deactivate() {
        if (mimic != null) {
            mimic.deactivate();
        }
        super.deactivate();
    }

    @Override
    public void onKeyPressed() {
        if (mimic != null) {
            mimic.onKeyPressed();
        }
        super.onKeyPressed();
    }

    @Override
    public void tick() {
        if (mimic == null) return;
        if (System.currentTimeMillis() >= cooldown) {
            mimic.turnOff();
            SimplePackets.MIMICRY_COOLDOWN.target(getPlayer()).sendToClient(System.currentTimeMillis()-1000);
            mimic = null;
        }
        if (mimic == null) return;
        mimic.tick();
    }

    @Override
    public void turnOff() {
        super.turnOff();
        SimplePackets.MIMICRY_COOLDOWN.target(getPlayer()).sendToClient(System.currentTimeMillis()-1000);
    }

    public Superpower getMimickedPower() {
        if (mimic == null) return this;
        return mimic;
    }

    @Override
    public void sendCooldownPacket() {
        SimplePackets.MIMICRY_COOLDOWN.target(getPlayer()).sendToClient(cooldown);
    }
}
