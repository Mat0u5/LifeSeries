package net.mat0u5.lifeseries.seasons.season.wildlife.morph;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.SizeShifting;
import net.mat0u5.lifeseries.utils.interfaces.IMorph;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
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

        if (Main.isLogicalSide()) {
            ServerPlayerEntity serverPlayer = PlayerUtils.getPlayer(playerUUID);
            if (serverPlayer != null) {
                if (morph == null) {
                    SizeShifting.setPlayerSizeUnchecked(serverPlayer, 1);
                }
                else {
                    //? if <= 1.21 {
                    Entity entity = morph.create(serverPlayer.getWorld());
                    //?} else {
                    /*Entity entity = morph.create(PlayerUtils.getServerWorld(serverPlayer), SpawnReason.COMMAND);
                     *///?}
                    if (entity != null) {
                        ((IMorph) entity).setFromMorph(true);
                        EntityDimensions dimensions = entity.getDimensions(EntityPose.STANDING);
                        double scaleY = dimensions.height() / serverPlayer.getDimensions(EntityPose.STANDING).height();
                        double scaleX = dimensions.width() / serverPlayer.getDimensions(EntityPose.STANDING).width();
                        double scale = Math.clamp(Math.min(scaleX, scaleY), 0.1, 1.0);
                        if (scale != serverPlayer.getScale()) SizeShifting.setPlayerSizeUnchecked(serverPlayer, 0.1);
                    }
                }
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
        return Registries.ENTITY_TYPE.getId(morph).toString();
    }
}

