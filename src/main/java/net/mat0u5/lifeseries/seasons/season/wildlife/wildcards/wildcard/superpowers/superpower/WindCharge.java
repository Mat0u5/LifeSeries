package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.ToggleableSuperpower;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.server.level.ServerPlayer;

//? if >= 1.21 {
import java.util.Set;
import net.minecraft.core.component.DataComponents;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.player.AttributeUtils;
import net.mat0u5.lifeseries.utils.world.ItemStackUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
//?}
//? if >= 1.21 && <= 1.21.4
//import net.minecraft.world.item.component.Unbreakable;
//? if >= 1.21.5
import net.minecraft.util.Unit;

public class WindCharge extends ToggleableSuperpower {
    public static int MAX_MACE_DAMAGE = 2;

    public WindCharge(ServerPlayer player) {
        super(player);
    }

    @Override
    public Superpowers getSuperpower() {
        return Superpowers.WIND_CHARGE;
    }

    @Override
    public void activate() {
        super.activate();
        //? if >= 1.21 {
        ServerPlayer player = getPlayer();
        if (player == null) return;
        player.ls$playNotifySound(SoundEvents.ARROW_SHOOT, SoundSource.MASTER, 0.3f, 1);
        AttributeUtils.setSafeFallHeight(player, 100000);
        giveMace();
        giveWindCharge();
        NetworkHandlerServer.sendVignette(player, 300);
        //?}
    }

    @Override
    public void deactivate() {
        super.deactivate();
        //? if >= 1.21 {
        ServerPlayer player = getPlayer();
        if (player != null) {
            player.ls$playNotifySound(SoundEvents.WIND_CHARGE_BURST.value(), SoundSource.MASTER, 0.3f, 1);
            TaskScheduler.scheduleTask(1, () -> {
                player.getInventory().setChanged();
                PlayerUtils.updatePlayerInventory(player);
            });
            AttributeUtils.resetSafeFallHeight(player);
        }
        //?}
    }
    //? if >= 1.21 {
    private void giveWindCharge() {
        ServerPlayer player = getPlayer();
        if (player != null && !player.getInventory().hasAnyOf(Set.of(Items.WIND_CHARGE))) {
            ItemStack windCharge = new ItemStack(Items.WIND_CHARGE, 4);
            player.getInventory().add(windCharge);
        }

    }

    private void giveMace() {
        ServerPlayer player = getPlayer();
        if (player != null) {
            ItemStack mace = new ItemStack(Items.MACE);
            mace.enchant(ItemStackUtils.getEnchantmentEntry(Enchantments.VANISHING_CURSE), 1);
            mace.enchant(ItemStackUtils.getEnchantmentEntry(Enchantments.WIND_BURST), 3);
            //? if <= 1.21.4 {
            /*mace.set(DataComponents.UNBREAKABLE, new Unbreakable(true));
            *///?} else {
            mace.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);
             //?}
            mace.set(DataComponents.MAX_DAMAGE, 1);
            mace.set(DataComponents.DAMAGE, 1);
            ItemStackUtils.setCustomComponentBoolean(mace, "IgnoreBlacklist", true);
            ItemStackUtils.setCustomComponentBoolean(mace, "FromSuperpower", true);
            ItemStackUtils.setCustomComponentBoolean(mace, "WindChargeSuperpower", true);
            player.getInventory().add(mace);
        }
    }
    //?}
}
