package net.mat0u5.lifeseries.entity.triviabot.server;

import net.mat0u5.lifeseries.entity.PlayerBoundEntity;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaWildcard;
import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

import static net.mat0u5.lifeseries.Main.livesManager;

public class TriviaBotServerData implements PlayerBoundEntity {
    private TriviaBot bot;
    public TriviaBotServerData(TriviaBot bot) {
        this.bot = bot;
    }

    public int snailTransformation = 0;
    public int despawnPlayerChecks = 0;

    private UUID _boundPlayerUUID;

    @Override
    public void onSetPlayer(ServerPlayerEntity player) {}

    @Override
    public UUID getBoundPlayerUUID() {
        return _boundPlayerUUID;
    }

    @Override
    public void setBoundPlayerUUID(UUID uuid) {
        _boundPlayerUUID = uuid;
    }

    @Override
    public boolean shouldPathfind() {
        if (bot.getBotWorld().isClient()) return false;
        ServerPlayerEntity player = getBoundPlayer();
        if (player == null) return false;
        if (!player.isAlive()) return false;
        if (getPlayerPos() == null) return false;
        if (player.isSpectator() && !SuperpowersWildcard.hasActivatedPower(player, Superpowers.ASTRAL_PROJECTION)) return false;
        return true;
    }

    public void tick() {
        if (bot.getBotWorld().isClient()) return;
        if (despawnChecks()) return;
        bot.pathfinding.tick();

        if (bot.ranOutOfTime()) {
            snailTransformation++;
        }
        if (bot.age % 2 == 0 && bot.submittedAnswer()) {
            bot.setAnalyzingTime(bot.getAnalyzingTime()-1);
        }

        if (bot.interactedWith()) {
            bot.triviaHandler.sendTimeUpdatePacket();
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
            if (bot.interactedWith() && bot.triviaHandler.getRemainingTicks() <= 0) {
                if (!bot.ranOutOfTime()) {
                    ServerPlayerEntity boundPlayer = getBoundPlayer();
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

        chunkLoading();
        bot.clearStatusEffects();
        bot.sounds.playSounds();
    }

    public boolean despawnChecks() {
        ServerPlayerEntity player = getBoundPlayer();
        if (player == null || (player.isSpectator() && livesManager.isDead(player))) {
            despawnPlayerChecks++;
        }
        if (despawnPlayerChecks > 200) {
            despawn();
            return true;
        }
        if (bot.age % 10 == 0) {
            if (!TriviaWildcard.bots.containsValue(bot) || !WildcardManager.isActiveWildcard(Wildcards.TRIVIA)) {
                despawn();
                return true;
            }
        }
        return false;
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
        if (bot.getBotWorld() instanceof ServerWorld world) {
            //? if <= 1.21.4 {
            world.getChunkManager().addTicket(ChunkTicketType.PORTAL, new ChunkPos(bot.getBlockPos()), 2, bot.getBlockPos());
            //?} else {
            /*world.getChunkManager().addTicket(ChunkTicketType.PORTAL, new ChunkPos(bot.getBlockPos()), 2);
             *///?}
        }
    }

    public void despawn() {
        if (getBoundPlayerUUID() != null) {
            TriviaWildcard.bots.remove(getBoundPlayerUUID());
        }
        if (!bot.getBotWorld().isClient()) {
            //? if <= 1.21 {
            bot.kill();
            //?} else {
            /*bot.kill((ServerWorld) bot.getBotWorld());
             *///?}
        }
        bot.discard();
    }
}
