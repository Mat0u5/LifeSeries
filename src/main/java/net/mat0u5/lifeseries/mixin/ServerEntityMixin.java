package net.mat0u5.lifeseries.mixin;

import com.mojang.datafixers.util.Pair;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

import static net.mat0u5.lifeseries.LifeSeries.currentSeason;

@Mixin(value = ServerEntity.class, priority = 1)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public class ServerEntityMixin {
    @Shadow
    @Final
    private Entity entity;

    @ModifyArg(method = "sendPairingData", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ClientboundSetEquipmentPacket;<init>(ILjava/util/List;)V"), index = 1)
    private List<Pair<EquipmentSlot, ItemStack>> hidePairingEquipment(List<Pair<EquipmentSlot, ItemStack>> equipment) {
        return currentSeason.filterVisibleEquipment(entity, equipment);
    }
}
