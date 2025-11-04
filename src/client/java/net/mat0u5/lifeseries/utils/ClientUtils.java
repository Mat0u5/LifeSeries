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
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

//? if >= 1.21.2 {
/*import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
*///?}

public class ClientUtils {

    public static boolean shouldPreventGliding() {
        if (!MainClient.preventGliding) return false;
        Minecraft client = Minecraft.getInstance();
        if (client == null) return false;
        if (client.player == null) return false;
        //? if >= 1.21.2 {
        /*if (LivingEntity.canGlideUsing(client.player.getItemBySlot(EquipmentSlot.CHEST), EquipmentSlot.CHEST) ||
                LivingEntity.canGlideUsing(client.player.getItemBySlot(EquipmentSlot.LEGS), EquipmentSlot.LEGS) ||
                LivingEntity.canGlideUsing(client.player.getItemBySlot(EquipmentSlot.FEET), EquipmentSlot.FEET)) {
            return false;
        }
        *///?}
        ItemStack helmet = PlayerUtils.getEquipmentSlot(client.player, 3);
        return ItemStackUtils.hasCustomComponentEntry(helmet, "FlightSuperpower");
    }

    @Nullable
    public static Player getPlayer(UUID uuid) {
        Minecraft client = Minecraft.getInstance();
        if (client == null) return null;
        if (client.level == null) return null;
        return client.level.getPlayerByUUID(uuid);
    }

    @Nullable
    public static PlayerTeam getPlayerTeam() {
        Minecraft client = Minecraft.getInstance();
        if (client == null) return null;
        if (client.player == null) return null;
        return client.player.getTeam();
    }

    public static void runCommand(String command) {
        ClientPacketListener handler = Minecraft.getInstance().getConnection();
        if (handler == null) return;

        if (command.startsWith("/")) {
            command = command.substring(1);
        }
        handler.sendCommand(command);
    }

    public static void disconnect(Component reason) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) return;
        ClientPacketListener handler = client.getConnection();
        if (handler == null) return;
        //? if < 1.21.6 {
        client.level.disconnect();
        //?} else {
        /*client.level.disconnect(reason);
        *///?}
        handler.onDisconnect(new DisconnectionDetails(reason));
    }

    public static boolean handleUpdatedAttribute(ClientLevel world, AttributeInstance instance, double baseValue, ClientboundUpdateAttributesPacket packet) {
        Entity entity = world.getEntity(packet.getEntityId());
        if (entity == null) return false;
        if (!(entity instanceof LocalPlayer player)) return false;
        if (!MainClient.isClientPlayer(player.getUUID())) return false;
        Holder<Attribute> scaleAttribute = Attributes.SCALE;
        if (instance.getAttribute() != scaleAttribute) return false;
        if (MainClient.clientCurrentSeason != Seasons.WILD_LIFE) return false;
        if (!MainClient.clientActiveWildcards.contains(Wildcards.SIZE_SHIFTING)) return false;
        if (!MainClient.FIX_SIZECHANGING_BUGS) return false;

        double oldBaseValue = player.getAttributeBaseValue(scaleAttribute);
        if (oldBaseValue == baseValue) return false;

        EntityDimensions oldEntityDimensions = player.getDefaultDimensions(player.getPose()).scale((float) oldBaseValue);
        AABB oldBoundingBox = oldEntityDimensions.makeBoundingBox(player.ls$getEntityPos());
        double oldHitboxSize = oldEntityDimensions.width();

        EntityDimensions newEntityDimensions = player.getDefaultDimensions(player.getPose()).scale((float) baseValue);
        AABB newBoundingBox = newEntityDimensions.makeBoundingBox(player.ls$getEntityPos());
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

    
    public static boolean isSpaceEmpty(LocalPlayer player, AABB box, double offsetX, double offsetY, double offsetZ) {
        if (player.noPhysics || player.isSpectator()) return true;
        AABB newBox = new AABB(box.minX + offsetX, box.minY +offsetY, box.minZ + offsetZ, box.maxX + offsetX, box.minY, box.maxZ + offsetZ);
        return PlayerUtils.getWorld(player).noCollision(player, newBox);
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

    public static Component getPlayerName(Component text) {
        if (text == null || Main.modFullyDisabled()) return text;
        if (Minecraft.getInstance().getConnection() == null) return text;

        if (MainClient.playerDisguiseNames.containsKey(text.getString())) {
            String name = MainClient.playerDisguiseNames.get(text.getString());
            for (PlayerInfo entry : Minecraft.getInstance().getConnection().getOnlinePlayers()) {
                if (OtherUtils.profileName(entry.getProfile()).equalsIgnoreCase(TextUtils.removeFormattingCodes(name))) {
                    if (entry.getTabListDisplayName() != null) {
                        return applyColorblindToName(entry.getTabListDisplayName(), entry.getTeam());
                    }
                    return applyColorblindToName(Component.literal(name), entry.getTeam());
                }
            }
        }
        else {
            for (PlayerInfo entry : Minecraft.getInstance().getConnection().getOnlinePlayers()) {
                if (OtherUtils.profileName(entry.getProfile()).equalsIgnoreCase(TextUtils.removeFormattingCodes(text.getString()))) {
                    return applyColorblindToName(text, entry.getTeam());
                }
            }
        }
        return text;
    }
    public static Component applyColorblindToName(Component original, PlayerTeam team) {
        if (!MainClient.COLORBLIND_SUPPORT) return original;
        if (original == null) return original;
        if (team == null) return original;
        return TextUtils.format("[{}] ",team.getDisplayName().getString()).withStyle(team.getColor()).append(original);
    }
}
