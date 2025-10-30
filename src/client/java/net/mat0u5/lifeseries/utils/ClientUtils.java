package net.mat0u5.lifeseries.utils;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.utils.enums.Direction;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.ItemStackUtils;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ClientUtils {

    public static boolean shouldPreventGliding() {
        if (!MainClient.preventGliding) return false;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return false;
        if (client.player == null) return false;
        //? if >= 1.21.2 {
         /*if (LivingEntity.canGlideWith(client.player.getEquippedStack(EquipmentSlot.CHEST), EquipmentSlot.CHEST) ||
            LivingEntity.canGlideWith(client.player.getEquippedStack(EquipmentSlot.LEGS), EquipmentSlot.LEGS) ||
            LivingEntity.canGlideWith(client.player.getEquippedStack(EquipmentSlot.FEET), EquipmentSlot.FEET)) {
            return false;
        }
        *///?}
        ItemStack helmet = PlayerUtils.getEquipmentSlot(client.player, 3);
        return ItemStackUtils.hasCustomComponentEntry(helmet, "FlightSuperpower");
    }

    @Nullable
    public static PlayerEntity getPlayer(UUID uuid) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return null;
        if (client.world == null) return null;
        return client.world.getPlayerByUuid(uuid);
    }

    @Nullable
    public static Team getPlayerTeam() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return null;
        if (client.player == null) return null;
        return client.player.getScoreboardTeam();
    }

    public static void runCommand(String command) {
        ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();
        if (handler == null) return;

        if (command.startsWith("/")) {
            command = command.substring(1);
        }
        handler.sendChatCommand(command);
    }

    public static void disconnect(Text reason) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;
        ClientPlayNetworkHandler handler = client.getNetworkHandler();
        if (handler == null) return;
        //? if < 1.21.6 {
        client.world.disconnect();
        //?} else {
        /*client.world.disconnect(reason);
        *///?}
        handler.onDisconnected(new DisconnectionInfo(reason));
    }

    public static boolean handleUpdatedAttribute(ClientWorld world, EntityAttributeInstance instance, double baseValue, EntityAttributesS2CPacket packet) {
        Entity entity = world.getEntityById(packet.getEntityId());
        if (entity == null) return false;
        if (!(entity instanceof ClientPlayerEntity player)) return false;
        if (!MainClient.isClientPlayer(player.getUuid())) return false;
        //? if <= 1.21 {
        RegistryEntry<EntityAttribute> scaleAttribute = EntityAttributes.GENERIC_SCALE;
        //?} else {
        /*RegistryEntry<EntityAttribute> scaleAttribute = EntityAttributes.SCALE;
         *///?}
        if (instance.getAttribute() != scaleAttribute) return false;
        if (MainClient.clientCurrentSeason != Seasons.WILD_LIFE) return false;
        if (!MainClient.clientActiveWildcards.contains(Wildcards.SIZE_SHIFTING)) return false;
        if (!MainClient.FIX_SIZECHANGING_BUGS) return false;

        double oldBaseValue = player.getAttributeBaseValue(scaleAttribute);
        if (oldBaseValue == baseValue) return false;

        EntityDimensions oldEntityDimensions = player.getBaseDimensions(player.getPose()).scaled((float) oldBaseValue);
        Box oldBoundingBox = oldEntityDimensions.getBoxAt(player.ls$getEntityPos());
        double oldHitboxSize = oldEntityDimensions.width();

        EntityDimensions newEntityDimensions = player.getBaseDimensions(player.getPose()).scaled((float) baseValue);
        Box newBoundingBox = newEntityDimensions.getBoxAt(player.ls$getEntityPos());
        double newHitboxSize = newEntityDimensions.width();

        double changedBy = newHitboxSize - oldHitboxSize;

        Vec3d move = null;
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
                    move = move.multiply(5);
                }
            }
            if (!oldSpaceEmpty && !newSpaceEmpty) {
                move = recursivelyFindDesiredCollission(player, newBoundingBox, 1.0E-5, true);
            }

        }

        if (move != null) {
            if (changedBy > 0) {
                Vec3d playerVelocity = player.getVelocity();
                double speedX = playerVelocity.x;
                double speedZ = playerVelocity.z;

                if (move.x != 0) speedX = 0;
                if (move.z != 0) speedZ = 0;

                player.setVelocity(speedX, playerVelocity.y, speedZ);
            }

            player.setPos(player.getX() + move.x, player.getY(), player.getZ() + move.z);
            instance.setBaseValue(baseValue);
            player.calculateDimensions();
            return true;
        }
        if (changedBy > 0) {
            instance.setBaseValue(baseValue);
            player.calculateDimensions();
            return true;
        }
        return false;
    }

    
    public static boolean isSpaceEmpty(ClientPlayerEntity player, Box box, double offsetX, double offsetY, double offsetZ) {
        if (player.noClip || player.isSpectator()) return true;
        Box newBox = new Box(box.minX + offsetX, box.minY +offsetY, box.minZ + offsetZ, box.maxX + offsetX, box.minY, box.maxZ + offsetZ);
        return PlayerUtils.getWorld(player).isSpaceEmpty(player, newBox);
    }

    public static Vec3d recursivelyFindDesiredCollission(ClientPlayerEntity player, Box newBoundingBox, double offsetY, boolean desiredSpaceEmpty) {
        for (double changedBy = 0.05; changedBy <= 0.4; changedBy += 0.05) {
            Vec3d found = findDesiredCollission(player, newBoundingBox, changedBy, offsetY, desiredSpaceEmpty, true);
            if (found != null) return found;
        }
        return null;
    }

    public static Vec3d findDesiredCollission(ClientPlayerEntity player, Box newBoundingBox, double changedBy, double offsetY, boolean desiredSpaceEmpty, boolean onlyCardinal) {
        Direction[] directions = onlyCardinal ? Direction.getCardinalDirections() : Direction.values();
        for (Direction direction : directions) {
            double offsetX = changedBy * direction.x;
            double offsetZ = changedBy * direction.z;

            boolean movedSpaceEmpty = isSpaceEmpty(player, newBoundingBox, offsetX, offsetY, offsetZ);
            if (movedSpaceEmpty == desiredSpaceEmpty) {
                return new Vec3d(offsetX, 0, offsetZ);
            }
        }
        return null;
    }

    public static Text getPlayerName(Text text) {
        if (text == null || Main.modFullyDisabled()) return text;
        if (MinecraftClient.getInstance().getNetworkHandler() == null) return text;

        if (MainClient.playerDisguiseNames.containsKey(text.getString())) {
            String name = MainClient.playerDisguiseNames.get(text.getString());
            for (PlayerListEntry entry : MinecraftClient.getInstance().getNetworkHandler().getPlayerList()) {
                if (OtherUtils.profileName(entry.getProfile()).equalsIgnoreCase(TextUtils.removeFormattingCodes(name))) {
                    if (entry.getDisplayName() != null) {
                        return applyColorblindToName(entry.getDisplayName(), entry.getScoreboardTeam());
                    }
                    return applyColorblindToName(Text.literal(name), entry.getScoreboardTeam());
                }
            }
        }
        else {
            for (PlayerListEntry entry : MinecraftClient.getInstance().getNetworkHandler().getPlayerList()) {
                if (OtherUtils.profileName(entry.getProfile()).equalsIgnoreCase(TextUtils.removeFormattingCodes(text.getString()))) {
                    return applyColorblindToName(text, entry.getScoreboardTeam());
                }
            }
        }
        return text;
    }
    public static Text applyColorblindToName(Text original, Team team) {
        if (!MainClient.COLORBLIND_SUPPORT) return original;
        if (original == null) return original;
        if (team == null) return original;
        return TextUtils.format("[{}] ",team.getDisplayName().getString()).formatted(team.getColor()).append(original);
    }
}
