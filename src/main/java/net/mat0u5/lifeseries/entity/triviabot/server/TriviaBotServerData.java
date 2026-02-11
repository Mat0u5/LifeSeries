package net.mat0u5.lifeseries.entity.triviabot.server;

import net.mat0u5.lifeseries.entity.PlayerBoundEntity;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.nicelife.NiceLifeTriviaManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaWildcard;
import net.mat0u5.lifeseries.utils.world.LevelUtils;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

import static net.mat0u5.lifeseries.Main.currentSeason;

public class TriviaBotServerData implements PlayerBoundEntity {
    private TriviaBot bot;
    public TriviaBotServerData(TriviaBot bot) {
        this.bot = bot;
    }

    public int despawnPlayerChecks = 0;

    private UUID _boundPlayerUUID;

    @Override
    public void onSetPlayer(ServerPlayer player) {}

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
        if (bot.level().isClientSide()) return false;
        ServerPlayer player = getBoundPlayer();
        if (player == null) return false;
        if (!player.isAlive()) return false;
        if (getPlayerPos() == null) return false;
        if (player.isSpectator() && !SuperpowersWildcard.hasActivatedPower(player, Superpowers.ASTRAL_PROJECTION)) return false;
        return true;
    }

    public void tick() {
        if (bot.level().isClientSide()) return;
        if (despawnChecks()) return;
        bot.pathfinding.tick();
        bot.triviaHandler.tick();

        chunkLoading();
        bot.removeAllEffects();
        bot.sounds.playSounds();
    }

    public boolean despawnChecks() {
        ServerPlayer player = getBoundPlayer();
        if (player == null || (player.isSpectator() && player.ls$isDead())) {
            despawnPlayerChecks++;
        }
        if (despawnPlayerChecks > 200) {
            despawn();
            return true;
        }
        if (bot.tickCount % 10 == 0) {
            if (currentSeason.getSeason() == Seasons.WILD_LIFE) {
                if (!TriviaWildcard.bots.containsValue(bot)) {
                    despawn();
                    return true;
                }
            }
            else if (currentSeason.getSeason() == Seasons.NICE_LIFE) {
                if (!NiceLifeTriviaManager.bots.containsValue(bot)) {
                    despawn();
                    return true;
                }
            }
            else {
                despawn();
                return true;
            }
        }
        return false;
    }


    public void handleHighVelocity() {
        Vec3 velocity = bot.getDeltaMovement();
        if (velocity.y > 0.15) {
            bot.setDeltaMovement(velocity.x,0.15,velocity.z);
        }
        else if (velocity.y < -0.15) {
            bot.setDeltaMovement(velocity.x,-0.15,velocity.z);
        }
    }

    public void chunkLoading() {
        if (bot.level() instanceof ServerLevel level) {
            //? if <= 1.21.4 {
            /*level.getChunkSource().addRegionTicket(TicketType.PORTAL, new ChunkPos(bot.blockPosition()), 2, bot.blockPosition());
            *///?} else {
            level.getChunkSource().addTicketWithRadius(TicketType.PORTAL, LevelUtils.chunkPosFromBlockPos(bot.blockPosition()), 2);
            //?}
        }
    }

    public void despawn() {
        if (getBoundPlayerUUID() != null) {
            TriviaWildcard.bots.remove(getBoundPlayerUUID());
        }
        if (!bot.level().isClientSide()) {
            //? if <= 1.21 {
            /*bot.kill();
            *///?} else {
            bot.kill((ServerLevel) bot.level());
             //?}
        }
        bot.discard();
    }
}
