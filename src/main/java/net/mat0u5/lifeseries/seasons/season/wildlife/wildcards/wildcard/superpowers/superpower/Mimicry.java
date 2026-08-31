package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import net.mat0u5.lifeseries.config.modifiable.ModifiableSound;
import net.mat0u5.lifeseries.config.modifiable.ModifiableText;
import net.mat0u5.lifeseries.mixin.MannequinAccessor;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpower;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.utils.interfaces.IPlayer;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.Time;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

//? if >= 1.21.9 {
import net.minecraft.world.entity.decoration.Mannequin;
//?}

public class Mimicry extends Superpower {
    public static int COOLDOWN_MILLIS = 300000;
    public static boolean DISABLE_OTHER_POWER = false;

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
        return COOLDOWN_MILLIS;
    }

    @Override
    public void activate() {
        ServerPlayer player = getPlayer();
        if (player == null) return;
        Entity lookingAt = PlayerUtils.getEntityLookingAt(player, 50);
        boolean isLookingAtPlayer = false;
        boolean successfullyMimicked = false;
        Superpower mimicPowerInstance = null;
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
                mimicPowerInstance = SuperpowersWildcard.getSuperpowerInstance(lookingAtPlayer);
                Superpowers mimicPower = SuperpowersWildcard.getSuperpower(lookingAtPlayer);
                if (!PlayerUtils.isFakePlayer(lookingAtPlayer) && mimicPower != null) {
                    if (mimicPower == Superpowers.MIMICRY) {
                        PlayerUtils.displayMessageToPlayer(player, ModifiableText.WILDLIFE_POWER_MIMIC_ERROR.get(), 65);
                        return;
                    }
                    if (mimicPower != Superpowers.NULL) {
                        mimic = mimicPower.getInstance(player);
                        successfullyMimicked = true;
                        PlayerUtils.displayMessageToPlayer(player, ModifiableText.WILDLIFE_POWER_MIMIC.get(lookingAtPlayer), 65);
                        ModifiableSound.WILDLIFE_SUPERPOWERS_MIMICRY.play(player, 0.3f, 1);
                    }
                }
            }
        }

        if (!isLookingAtPlayer) {
            PlayerUtils.displayMessageToPlayer(player, ModifiableText.WILDLIFE_POWER_MIMIC_NOPLAYER.get(), 65);
            return;
        }
        if (!successfullyMimicked) {
            PlayerUtils.displayMessageToPlayer(player, ModifiableText.WILDLIFE_POWER_MIMIC_NOPOWER.get(), 65);
            return;
        }
        super.activate();
        sendCooldownPacket();
        if (DISABLE_OTHER_POWER && mimicPowerInstance != null) {
            mimicPowerInstance.stolenUntil = System.currentTimeMillis() + COOLDOWN_MILLIS;
        }
    }

    @Override
    public void deactivate() {
        if (mimic != null) {
            mimic.deactivate();
        }
        super.deactivate();
    }

    @Override
    public KeyPressResult onKeyPressed() {
        KeyPressResult mimicResult = null;
        if (mimic != null) {
            mimicResult = mimic.onKeyPressed();
        }
        KeyPressResult thisResult = super.onKeyPressed();
        return mimicResult != null ? mimicResult : thisResult;
    }

    @Override
    public void tick() {
        if (mimic == null) return;
        if (System.currentTimeMillis() >= cooldown) {
            mimic.turnOff();
            SimplePackets.MIMICRY_COOLDOWN.sendToClient(System.currentTimeMillis()-1000, getPlayer());
            mimic = null;
        }
        if (mimic == null) return;
        mimic.tick();
    }

    @Override
    public void turnOff() {
        super.turnOff();
        SimplePackets.MIMICRY_COOLDOWN.sendToClient(System.currentTimeMillis()-1000, getPlayer());
    }

    public Superpower getMimickedPower() {
        if (mimic == null) return this;
        return mimic;
    }

    @Override
    public void sendCooldownPacket() {
        SimplePackets.MIMICRY_COOLDOWN.sendToClient(cooldown, getPlayer());
    }

    public static void displayStolenPowerActionbar(Superpower power, ServerPlayer player) {
        Time time = Time.millis(power.stolenUntil - System.currentTimeMillis());
        int showFor = Math.min(60, time.getTicks());
        PlayerUtils.displayMessageToPlayer(player, ModifiableText.WILDLIFE_SUPERPOWERS_STOLEN.get(time.format()), showFor);
        if (showFor > 20) {
            TaskScheduler.schedulePriorityTask(20, () -> {
                PlayerUtils.displayMessageToPlayer(player, ModifiableText.WILDLIFE_SUPERPOWERS_STOLEN.get(time.diff(Time.seconds(1)).format()), showFor-20);
            });
        }
        if (showFor > 40) {
            TaskScheduler.schedulePriorityTask(40, () -> {
                PlayerUtils.displayMessageToPlayer(player, ModifiableText.WILDLIFE_SUPERPOWERS_STOLEN.get(time.diff(Time.seconds(2)).format()), showFor-40);
            });
        }
    }
}
