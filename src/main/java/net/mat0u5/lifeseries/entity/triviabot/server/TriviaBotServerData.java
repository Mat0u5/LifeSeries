package net.mat0u5.lifeseries.entity.triviabot.server;

import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaWildcard;
import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static net.mat0u5.lifeseries.Main.server;

public class TriviaBotServerData {
    private TriviaBot bot;
    public TriviaBotServerData(TriviaBot bot) {
        this.bot = bot;
    }

    public int snailTransformation = 0;
    public int nullPlayerChecks = 0;
    public UUID boundPlayerUUID;

    public void tick() {
        if (bot.getWorldEntity().isClient()) return;

        if (bot.ranOutOfTime()) {
            snailTransformation++;
        }
        if (bot.age % 2 == 0 && bot.submittedAnswer()) {
            bot.setAnalyzingTime(bot.getAnalyzingTime()-1);
        }

        if (bot.age % 100 == 0) {
            if (!TriviaWildcard.bots.containsValue(bot) || !WildcardManager.isActiveWildcard(Wildcards.TRIVIA)) {
                despawn();
            }
        }

        if (bot.submittedAnswer()) {
            if (bot.answeredRight()) {
                if (bot.getAnalyzingTime() < -80) {
                    if (bot.hasVehicle()) bot.dismountVehicle();
                    bot.noClip = true;
                    float velocity = Math.min(0.5f, 0.25f * Math.abs((bot.getAnalyzingTime()+80) / (20.0f)));
                    bot.setVelocity(0,velocity,0);
                    if (bot.getAnalyzingTime() < -200) despawn();
                }
            }
            else {
                if (bot.getAnalyzingTime() < -100) {
                    if (bot.hasVehicle()) bot.dismountVehicle();
                    bot.noClip = true;
                    float velocity = Math.min(0.5f, 0.25f * Math.abs((bot.getAnalyzingTime()+100) / (20.0f)));
                    bot.setVelocity(0,velocity,0);
                    if (bot.getAnalyzingTime() < -200) despawn();
                }
            }
        }
        else {
            handleHighVelocity();
            ServerPlayerEntity boundPlayer = getBoundServerPlayer();
            if (boundPlayer != null) {
                if (bot.age % 5 == 0) {
                    bot.pathfinding.updateNavigationTarget();
                }
            }
            if (bot.interactedWith() && bot.triviaHandler.getRemainingTime() <= 0) {
                if (!bot.ranOutOfTime()) {
                    if (boundPlayer != null) {
                        NetworkHandlerServer.sendStringPacket(boundPlayer, PacketNames.RESET_TRIVIA, "true");
                    }
                }
                bot.setRanOutOfTime(true);
            }
            if (snailTransformation > 66) {
                bot.triviaHandler.transformIntoSnail();
            }
        }

        if (nullPlayerChecks > 1000) {
            despawn();
        }

        if (bot.age % 100 == 0 || !bot.pathfinding.navigationInit) {
            bot.pathfinding.navigationInit = true;
            bot.pathfinding.updateNavigation();
        }

        chunkLoading();
        bot.clearStatusEffects();
        bot.sounds.playSounds();
    }

    @Nullable
    public ServerPlayerEntity getBoundServerPlayer() {
        if (bot.getWorldEntity().isClient()) return null;
        if (server == null) return null;
        ServerPlayerEntity player = PlayerUtils.getPlayer(boundPlayerUUID);
        if (player == null || (player.isSpectator() && !player.isAlive())) {
            nullPlayerChecks++;
            return null;
        }
        nullPlayerChecks = 0;
        if (player.isSpectator()) return null;
        if (!player.isAlive()) return null;
        return player;
    }
    @Nullable
    public PlayerEntity getBoundPlayer() {
        return getBoundServerPlayer();
    }

    @Nullable
    public ServerPlayerEntity getActualBoundPlayer() {
        if (server == null) return null;
        return PlayerUtils.getPlayer(boundPlayerUUID);
    }

    public void setBoundPlayer(PlayerEntity player) {
        if (player == null) return;
        boundPlayerUUID = player.getUuid();
    }


    public void handleHighVelocity() {
        Vec3d velocity = bot.getVelocity();
        if (velocity.y > 0.15) {
            bot.setVelocity(velocity.x,0.15,velocity.z);
        }
        else if (velocity.y < -0.15) {
            bot.setVelocity(velocity.x,-0.15,velocity.z);
        }
    }

    public void chunkLoading() {
        if (bot.getWorldEntity() instanceof ServerWorld world) {
            //? if <= 1.21.4 {
            world.getChunkManager().addTicket(ChunkTicketType.PORTAL, new ChunkPos(bot.getBlockPos()), 2, bot.getBlockPos());
            //?} else {
            /*world.getChunkManager().addTicket(ChunkTicketType.PORTAL, new ChunkPos(bot.getBlockPos()), 2);
             *///?}
        }
    }

    public void despawn() {
        if (boundPlayerUUID != null) {
            TriviaWildcard.bots.remove(boundPlayerUUID);
        }
        if (bot.getWorldEntity().isClient()) {
            //? if <= 1.21 {
            bot.kill();
            //?} else {
            /*bot.kill((ServerWorld) bot.getWorldEntity());
             *///?}
        }
        bot.discard();
    }
}
