package net.mat0u5.lifeseries.seasons.season.wildlife.morph;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.SizeShifting;
import net.mat0u5.lifeseries.utils.interfaces.IMorph;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class MorphComponent {
    private static final String MORPH_NBT_KEY = "morph";
    private static final String TYPE_KEY = "type";

    public UUID playerUUID;
    @Nullable
    public EntityType<?> morph = null;
    public LivingEntity dummy = null;

    public MorphComponent(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public void setMorph(EntityType<?> morph) {
        this.morph = morph;
        if (morph == null) this.dummy = null;

        if (Main.isLogicalSide()) {
            ServerPlayer serverPlayer = PlayerUtils.getPlayer(playerUUID);
            if (serverPlayer != null) {
                if (morph != null) {
                    //? if <= 1.21 {
                    /*Entity entity = morph.create(serverPlayer.level());
                     *///?} else {
                    Entity entity = morph.create(serverPlayer.ls$getServerLevel(), EntitySpawnReason.COMMAND);
                    //?}
                    if (entity != null) {
                        ((IMorph) entity).setFromMorph(true);
                    }
                }
                serverPlayer.refreshDimensions();
            }
        }
    }

    public boolean isMorphed() {
        return morph != null;
    }

    @Nullable
    public LivingEntity getDummy() {
        return dummy;
    }

    @Nullable
    public EntityType<?> getType() {
        return morph;
    }

    public String getTypeAsString() {
        if (morph == null) return "null";
        return BuiltInRegistries.ENTITY_TYPE.getKey(morph).toString();
    }
}

