package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpower;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

//? if >= 1.21.9 {
/*import net.minecraft.entity.decoration.MannequinEntity;
import net.mat0u5.lifeseries.mixin.MannequinEntityAccessor;
*///?}

public class Mimicry extends Superpower {

    private Superpower mimic = null;

    public Mimicry(ServerPlayerEntity player) {
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
        ServerPlayerEntity player = getPlayer();
        if (player == null) return;
        Entity lookingAt = PlayerUtils.getEntityLookingAt(player, 50);
        boolean isLookingAtPlayer = false;
        boolean successfullyMimicked = false;
        if (lookingAt != null)  {
            //? if >= 1.21.9 {
            /*if (lookingAt instanceof MannequinEntity mannequin && mannequin instanceof MannequinEntityAccessor mannequinAccessor && mannequin.age < 0) {
                ServerPlayerEntity lookingAtPlayer = PlayerUtils.getPlayer(mannequinAccessor.ls$getMannequinProfile().getGameProfile().id());
                if (lookingAtPlayer != null) {
                    lookingAt = lookingAtPlayer;
                }
            }
            *///?}
            if (lookingAt instanceof ServerPlayerEntity lookingAtPlayer) {
                lookingAtPlayer = PlayerUtils.getPlayerOrProjection(lookingAtPlayer);
                isLookingAtPlayer = true;
                Superpowers mimicPower = SuperpowersWildcard.getSuperpower(lookingAtPlayer);
                if (!PlayerUtils.isFakePlayer(lookingAtPlayer) && mimicPower != null) {
                    if (mimicPower != Superpowers.NULL && mimicPower != Superpowers.MIMICRY) {
                        mimic = mimicPower.getInstance(player);
                        successfullyMimicked = true;
                        PlayerUtils.displayMessageToPlayer(player, TextUtils.format("Mimicked superpower of {}", lookingAtPlayer), 65);
                        player.playSoundToPlayer(SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.MASTER, 0.3f, 1);
                    }
                    if (mimicPower == Superpowers.MIMICRY) {
                        PlayerUtils.displayMessageToPlayer(player, Text.literal("You cannot mimic that power."), 65);
                        return;
                    }
                }
            }
        }

        if (!isLookingAtPlayer) {
            PlayerUtils.displayMessageToPlayer(player, Text.of("You are not looking at a player."), 65);
            return;
        }
        if (!successfullyMimicked) {
            PlayerUtils.displayMessageToPlayer(player, Text.of("That player does not have a superpower."), 65);
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
            NetworkHandlerServer.sendLongPacket(getPlayer(), PacketNames.SUPERPOWER_COOLDOWN, System.currentTimeMillis()-1000);
            mimic = null;
        }
        if (mimic == null) return;
        mimic.tick();
    }

    @Override
    public void turnOff() {
        super.turnOff();
        NetworkHandlerServer.sendLongPacket(getPlayer(), PacketNames.MIMICRY_COOLDOWN, System.currentTimeMillis()-1000);
    }

    public Superpower getMimickedPower() {
        if (mimic == null) return this;
        return mimic;
    }

    @Override
    public void sendCooldownPacket() {
        NetworkHandlerServer.sendLongPacket(getPlayer(), PacketNames.MIMICRY_COOLDOWN, cooldown);
    }
}
