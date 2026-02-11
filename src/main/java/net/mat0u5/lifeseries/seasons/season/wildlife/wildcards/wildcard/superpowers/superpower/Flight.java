package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpower;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.Time;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import static net.mat0u5.lifeseries.Main.currentSeason;

//? if >= 1.21.2 {
import net.minecraft.world.item.equipment.Equippable;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.ItemStackUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Unit;
import java.util.Optional;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
//?}
//? if >=1.21.2 && <= 1.21.4
/*import net.minecraft.world.item.component.Unbreakable;*/
//? if >= 1.21.5
import net.minecraft.world.item.component.TooltipDisplay;
//? if >= 1.21.6
import net.minecraft.core.registries.BuiltInRegistries;

public class Flight extends Superpower {
    public boolean isLaunchedUp = false;
    private int onGroundTicks = 0;
    private Time timer = Time.zero();

    public Flight(ServerPlayer player) {
        super(player);
    }

    @Override
    public Superpowers getSuperpower() {
        return Superpowers.FLIGHT;
    }

    @Override
    public int getCooldownMillis() {
        return 45000;
    }

    @Override
    public void tick() {
        timer.tick();
        ServerPlayer player = getPlayer();
        if (player == null) {
            onGroundTicks = 0;
            return;
        }
        if (!isLaunchedUp) {
            onGroundTicks = 0;
            if (timer.isMultipleOf(Time.ticks(5))) SimplePackets.PREVENT_GLIDING.target(player).sendToClient(true);
            return;
        }

        if (player.onGround()) {
            onGroundTicks++;
            if (timer.isMultipleOf(Time.ticks(5))) SimplePackets.PREVENT_GLIDING.target(player).sendToClient(true);
        }

        else {
            onGroundTicks = 0;
        }

        if (onGroundTicks >= 10) {
            isLaunchedUp = false;
            onGroundTicks = 0;
        }
    }

    @Override
    public void activate() {
        super.activate();
        ServerPlayer player = getPlayer();
        if (player == null) return;
        giveHelmet();

        player.ls$getServerLevel().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.MASTER, 1, 1);
        player.ls$playNotifySound(SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.MASTER, 1, 1);

        //? if <= 1.21.4 {
        /*MobEffectInstance effect = new MobEffectInstance(MobEffects.JUMP, 20, 54, false, false, false);
        *///?} else {
        MobEffectInstance effect = new MobEffectInstance(MobEffects.JUMP_BOOST, 20, 54, false, false, false);
        //?}
        player.addEffect(effect);
        SimplePackets.JUMP.target(player).sendToClient();

        isLaunchedUp = true;
        SimplePackets.PREVENT_GLIDING.target(player).sendToClient(false);
    }

    @Override
    public void deactivate() {
        super.deactivate();
        ServerPlayer player = getPlayer();
        if (player == null) return;
        TaskScheduler.scheduleTask(1, () -> {
            player.getInventory().setChanged();
            PlayerUtils.updatePlayerInventory(player);
        });
        SimplePackets.PREVENT_GLIDING.target(player).sendToClient(false);
    }

    private void giveHelmet() {
        //? if >= 1.21.2 {
        ServerPlayer player = getPlayer();
        if (player != null) {
            if (ItemStackUtils.hasCustomComponentEntry(PlayerUtils.getEquipmentSlot(player, 3), "FlightSuperpower")) return;

            ItemStack helmet = new ItemStack(Items.IRON_NUGGET);
            helmet.enchant(ItemStackUtils.getEnchantmentEntry(Enchantments.BINDING_CURSE), 1);
            helmet.enchant(ItemStackUtils.getEnchantmentEntry(Enchantments.VANISHING_CURSE), 1);
            ItemEnchantments enchantmentsComponent = helmet.get(DataComponents.ENCHANTMENTS);
            //? if <= 1.21.4 {
            /*if (enchantmentsComponent != null) {
                helmet.set(DataComponents.ENCHANTMENTS, enchantmentsComponent.withTooltip(false));
            }
            helmet.set(DataComponents.UNBREAKABLE, new Unbreakable(false));
            helmet.set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);
            *///?} else {
            helmet.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);
            helmet.set(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT
                    .withHidden(DataComponents.ENCHANTMENTS, true)
                    .withHidden(DataComponents.UNBREAKABLE, true)
            );
            //?}

            helmet.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, false);
            helmet.set(DataComponents.ITEM_NAME, Component.nullToEmpty("Winged Helmet"));
            //? if >= 1.21.2 {
            helmet.set(DataComponents.ITEM_MODEL, IdentifierHelper.mod("winged_helmet"));
            helmet.set(DataComponents.GLIDER, Unit.INSTANCE);
                //? if <= 1.21.4 {
            /*helmet.set(DataComponents.EQUIPPABLE, new Equippable(EquipmentSlot.HEAD, SoundEvents.ARMOR_EQUIP_GENERIC, Optional.empty(), Optional.empty(), Optional.empty(), false, false, false));
                *///?} else if <= 1.21.5 {
                /*helmet.set(DataComponents.EQUIPPABLE, new Equippable(EquipmentSlot.HEAD, SoundEvents.ARMOR_EQUIP_GENERIC, Optional.empty(), Optional.empty(), Optional.empty(), false, false, false, false));
                *///?} else {
                helmet.set(DataComponents.EQUIPPABLE, new Equippable(EquipmentSlot.HEAD, SoundEvents.ARMOR_EQUIP_GENERIC, Optional.empty(), Optional.empty(), Optional.empty(), false, false, false, false, false, BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.SHEARS_SNIP)));
                //?}
            //?}
            ItemStackUtils.setCustomComponentBoolean(helmet, "IgnoreBlacklist", true);
            ItemStackUtils.setCustomComponentBoolean(helmet, "FromSuperpower", true);
            ItemStackUtils.setCustomComponentBoolean(helmet, "FlightSuperpower", true);

            ItemStackUtils.spawnItemForPlayer(player.ls$getServerLevel(), player.position(), PlayerUtils.getEquipmentSlot(player, 3).copy(), player);
            player.setItemSlot(EquipmentSlot.HEAD, helmet);
        }
        //?}
    }
}
