package net.mat0u5.lifeseries.client.utils;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.client.LifeSeriesClient;
import net.mat0u5.matlib.events.OptionalEventReturn;
import net.mat0u5.matlib.utils.enums.Direction;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.matlib.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.matlib.utils.world.ItemStackUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.entity.Entity;

//? if > 1.20 {
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
//?}
//? if >= 1.21.2 {
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
//?}
//? if >= 26.2
import net.minecraft.world.scores.TeamColor;

public class ClientUtils extends net.mat0u5.matlib.client.utils.ClientUtils {

    public static boolean shouldPreventGliding() {
        if (!LifeSeriesClient.preventGliding) return false;
        Minecraft client = Minecraft.getInstance();
        if (client == null) return false;
        if (client.player == null) return false;
        //? if >= 1.21.2 {
        if (LivingEntity.canGlideUsing(client.player.getItemBySlot(EquipmentSlot.CHEST), EquipmentSlot.CHEST) ||
                LivingEntity.canGlideUsing(client.player.getItemBySlot(EquipmentSlot.LEGS), EquipmentSlot.LEGS) ||
                LivingEntity.canGlideUsing(client.player.getItemBySlot(EquipmentSlot.FEET), EquipmentSlot.FEET)) {
            return false;
        }
        //?}
        ItemStack helmet = PlayerUtils.getEquipmentSlot(client.player, 3);
        return ItemStackUtils.hasCustomComponentEntry(helmet, "FlightSuperpower");
    }

    @Nullable
    public static String getPlayerTeamColorOrPacket() {
        if (LifeSeriesClient.teamColor != null && !LifeSeriesClient.teamColor.isEmpty()) return LifeSeriesClient.teamColor;
        return getPlayerTeamColor();
    }

    @Nullable
    public static String getPlayerTeamNameOrPacket() {
        if (LifeSeriesClient.teamName != null && !LifeSeriesClient.teamName.isEmpty()) return LifeSeriesClient.teamName;
        return getPlayerTeamName();
    }

    //? if > 1.20.3 {
    public static boolean handleUpdatedAttribute(ClientLevel level, AttributeInstance instance, double baseValue, ClientboundUpdateAttributesPacket packet) {
        Entity entity = level.getEntity(packet.getEntityId());
        if (entity == null) return false;
        if (!(entity instanceof LocalPlayer player)) return false;
        if (!LifeSeriesClient.isClientPlayer(player.getUUID())) return false;
        Holder<Attribute> scaleAttribute = Attributes.SCALE;
        if (instance.getAttribute() != scaleAttribute) return false;
        if (!LifeSeries.isSeason(Seasons.WILD_LIFE)) return false;
        if (!LifeSeriesClient.clientActiveWildcards.contains(Wildcards.SIZE_SHIFTING)) return false;
        if (!LifeSeriesClient.FIX_SIZECHANGING_BUGS) return false;

        double oldBaseValue = player.getAttributeBaseValue(scaleAttribute);
        if (oldBaseValue == baseValue) return false;

        EntityDimensions oldEntityDimensions = player.getDefaultDimensions(player.getPose()).scale((float) oldBaseValue);
        AABB oldBoundingBox = oldEntityDimensions.makeBoundingBox(player.position());
        double oldHitboxSize = oldEntityDimensions.width();

        EntityDimensions newEntityDimensions = player.getDefaultDimensions(player.getPose()).scale((float) baseValue);
        AABB newBoundingBox = newEntityDimensions.makeBoundingBox(player.position());
        double newHitboxSize = newEntityDimensions.width();

        double changedBy = newHitboxSize - oldHitboxSize;

        Vec3 move = null;
        if (changedBy < 0) {
            boolean oldSpaceBelowEmpty = isSpaceEmpty(player, oldBoundingBox, 0, -1.0E-5, 0);
            boolean newSpaceBelowEmpty = isSpaceEmpty(player, newBoundingBox, 0, -1.0E-5, 0);
            if (!oldSpaceBelowEmpty && newSpaceBelowEmpty) {
                // The shrinking causes the player to fall when on the edge of blocks
                move = findDesiredCollission(player, newBoundingBox, changedBy, - 1.0E-5, false, false);
            }
        }
        else {

            boolean oldSpaceEmpty = isSpaceEmpty(player, oldBoundingBox, 0, 1.0E-5, 0);
            boolean newSpaceEmpty = isSpaceEmpty(player, newBoundingBox, 0, 1.0E-5, 0);
            if (oldSpaceEmpty && !newSpaceEmpty) {
                // Growing causes the player to clip into blocks
                move = findDesiredCollission(player, newBoundingBox, changedBy, 1.0E-5, true, false);
                if (move != null) {
                    move = move.scale(5);
                }
            }
            if (!oldSpaceEmpty && !newSpaceEmpty) {
                move = recursivelyFindDesiredCollission(player, newBoundingBox, 1.0E-5, true);
            }

        }

        if (move != null) {
            if (changedBy > 0) {
                Vec3 playerVelocity = player.getDeltaMovement();
                double speedX = playerVelocity.x;
                double speedZ = playerVelocity.z;

                if (move.x != 0) speedX = 0;
                if (move.z != 0) speedZ = 0;

                player.setDeltaMovement(speedX, playerVelocity.y, speedZ);
            }

            player.setPosRaw(player.getX() + move.x, player.getY(), player.getZ() + move.z);
            instance.setBaseValue(baseValue);
            player.refreshDimensions();
            return true;
        }
        if (changedBy > 0) {
            instance.setBaseValue(baseValue);
            player.refreshDimensions();
            return true;
        }
        return false;
    }
    //?}

    
    public static boolean isSpaceEmpty(LocalPlayer player, AABB box, double offsetX, double offsetY, double offsetZ) {
        if (player.noPhysics || player.isSpectator()) return true;
        AABB newBox = new AABB(box.minX + offsetX, box.minY +offsetY, box.minZ + offsetZ, box.maxX + offsetX, box.minY, box.maxZ + offsetZ);
        return player.level().noCollision(player, newBox);
    }

    public static Vec3 recursivelyFindDesiredCollission(LocalPlayer player, AABB newBoundingBox, double offsetY, boolean desiredSpaceEmpty) {
        for (double changedBy = 0.05; changedBy <= 0.4; changedBy += 0.05) {
            Vec3 found = findDesiredCollission(player, newBoundingBox, changedBy, offsetY, desiredSpaceEmpty, true);
            if (found != null) return found;
        }
        return null;
    }

    public static Vec3 findDesiredCollission(LocalPlayer player, AABB newBoundingBox, double changedBy, double offsetY, boolean desiredSpaceEmpty, boolean onlyCardinal) {
        Direction[] directions = onlyCardinal ? Direction.getCardinalDirections() : Direction.values();
        for (Direction direction : directions) {
            double offsetX = changedBy * direction.x;
            double offsetZ = changedBy * direction.z;

            boolean movedSpaceEmpty = isSpaceEmpty(player, newBoundingBox, offsetX, offsetY, offsetZ);
            if (movedSpaceEmpty == desiredSpaceEmpty) {
                return new Vec3(offsetX, 0, offsetZ);
            }
        }
        return null;
    }

    public static OptionalEventReturn<Component> getEntityName(Entity entity, Component text) {
        if (text == null || LifeSeries.modFullyDisabled() || !(entity instanceof Player player)) return OptionalEventReturn.pass();
        if (Minecraft.getInstance().getConnection() == null) return OptionalEventReturn.pass();

        if (LifeSeriesClient.playerDisguiseNames.containsKey(text.getString())) {
            String name = LifeSeriesClient.playerDisguiseNames.get(text.getString());
            for (PlayerInfo entry : Minecraft.getInstance().getConnection().getOnlinePlayers()) {
                if (OtherUtils.profileName(entry.getProfile()).equalsIgnoreCase(TextUtils.removeFormattingCodes(name))) {
                    if (entry.getTabListDisplayName() != null) {
                        return OptionalEventReturn.of(applyColorblindToName(entry.getTabListDisplayName(), entry.getTeam()));
                    }
                    return OptionalEventReturn.of(applyColorblindToName(Component.literal(name), entry.getTeam()));
                }
            }
        }
        else {
            for (PlayerInfo entry : Minecraft.getInstance().getConnection().getOnlinePlayers()) {
                if (OtherUtils.profileName(entry.getProfile()).equalsIgnoreCase(TextUtils.removeFormattingCodes(text.getString()))) {
                    return OptionalEventReturn.of(applyColorblindToName(text, entry.getTeam()));
                }
            }
        }
        return OptionalEventReturn.pass();
    }

    public static Component applyColorblindToName(Component original, PlayerTeam team) {
        if (!LifeSeriesClient.COLORBLIND_SUPPORT) return original;
        if (original == null) return original;
        if (team == null) return original;
        String name = team.getDisplayName().getString();
        if (name == null || name.isEmpty()) return original;
        //~ if >= 26.2 '.withStyle(team.getColor())' -> '.withColor(team.getColor().orElse(TeamColor.WHITE).textColor())' {
        return TextUtils.format("[{}] ", name).withColor(team.getColor().orElse(TeamColor.WHITE).textColor()).append(original);
        //~}
    }
}
