package net.mat0u5.lifeseries.series.limitedlife;

import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.series.*;
import net.mat0u5.lifeseries.utils.OtherUtils;
import net.mat0u5.lifeseries.utils.PlayerUtils;
import net.mat0u5.lifeseries.utils.ScoreboardUtils;
import net.mat0u5.lifeseries.utils.TeamUtils;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;

import java.util.ArrayList;
import java.util.List;

import static net.mat0u5.lifeseries.Main.currentSeries;
import static net.mat0u5.lifeseries.Main.seriesConfig;

public class LimitedLife extends Series {
    
    private int DEFAULT_TIME = 86400;
    private int YELLOW_TIME = 57600;
    private int RED_TIME = 28800;

    public LimitedLifeBoogeymanManager boogeymanManager = new LimitedLifeBoogeymanManager();

    @Override
    public SeriesList getSeries() {
        return SeriesList.LIMITED_LIFE;
    }

    @Override
    public ConfigManager getConfig() {
        return new LimitedLifeConfig();
    }

    @Override
    public void displayTimers(MinecraftServer server) {
        //This function is called once every second, so we can
        String message = "";
        if (status == SessionStatus.NOT_STARTED) {
            message = "Session has not started";
        }
        else if (status == SessionStatus.STARTED) {
            message = getRemainingLength();
        }
        else if (status == SessionStatus.PAUSED) {
            message = "Session has been paused";
        }
        else if (status == SessionStatus.FINISHED) {
            message = "Session has ended";
        }

        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            if (status == SessionStatus.STARTED && isAlive(player)) {
                // One second has passed - remove a players life
                removePlayerLife(player);
            }

            MutableText fullMessage = Text.literal("");
            if (displayTimer.contains(player.getUuid())) {
                fullMessage.append(Text.literal(message).formatted(Formatting.GRAY));
            }
            if (hasAssignedLives(player)) {
                if (!fullMessage.getString().isEmpty()) fullMessage.append(Text.of("  |  "));
                fullMessage.append(getFormattedLives(getPlayerLives(player)));
            }
            player.sendMessage(fullMessage, true);
        }
    }

    @Override
    public Formatting getColorForLives(Integer lives) {
        if (lives == null) return Formatting.GRAY;
        if (lives >= YELLOW_TIME) return Formatting.GREEN;
        if (lives >= RED_TIME) return Formatting.YELLOW;
        if (lives >= 1) return Formatting.DARK_RED;
        return Formatting.DARK_GRAY;
    }

    @Override
    public Text getFormattedLives(Integer lives) {
        if (lives == null) return Text.empty();
        Formatting color = getColorForLives(lives);
        return Text.literal(OtherUtils.formatTime(lives*20)).formatted(color);
    }

    @Override
    public void reloadPlayerTeamActual(ServerPlayerEntity player) {
        Integer lives = getPlayerLives(player);
        if (lives == null) TeamUtils.addPlayerToTeam("Unassigned",player);
        else if (lives <= 0) TeamUtils.addPlayerToTeam("Dead",player);
        else if (lives >= YELLOW_TIME) TeamUtils.addPlayerToTeam("Green",player);
        else if (lives >= RED_TIME) TeamUtils.addPlayerToTeam("Yellow",player);
        else if (lives >= 1) TeamUtils.addPlayerToTeam("Red",player);
    }

    @Override
    public void setPlayerLives(ServerPlayerEntity player, int lives) {
        Formatting colorBefore = player.getScoreboardTeam().getColor();
        ScoreboardUtils.setScore(ScoreHolder.fromName(player.getNameForScoreboard()), "Lives", lives);
        if (lives <= 0) {
            playerLostAllLives(player);
        }
        Formatting colorNow = getColorForLives(lives);
        if (colorBefore != colorNow) {
            if (player.isSpectator() && lives > 0) {
                player.changeGameMode(GameMode.SURVIVAL);
            }
            reloadPlayerTeam(player);
        }
    }

    @Override
    public Boolean isOnLastLife(ServerPlayerEntity player) {
        if (!isAlive(player)) return null;
        Integer lives = currentSeries.getPlayerLives(player);
        return lives < RED_TIME;
    }

    @Override
    public Boolean isOnSpecificLives(ServerPlayerEntity player, int check) {
        if (!isAlive(player)) return null;
        Integer lives = currentSeries.getPlayerLives(player);
        if (check == 1) return 0 < lives && lives < RED_TIME;
        if (check == 2) return RED_TIME <= lives && lives < YELLOW_TIME;
        if (check == 3) return lives >= YELLOW_TIME;
        return null;
    }

    @Override
    public void onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
        if (source != null) {
            if (source.getAttacker() instanceof ServerPlayerEntity) {
                if (player != source.getAttacker()) {
                    onPlayerKilledByPlayer(player, (ServerPlayerEntity) source.getAttacker());
                    return;
                }
            }
        }
        if (player.getPrimeAdversary() != null) {
            if (player.getPrimeAdversary() instanceof ServerPlayerEntity) {
                if (player != player.getPrimeAdversary()) {
                    onPlayerKilledByPlayer(player, (ServerPlayerEntity) player.getPrimeAdversary());
                    return;
                }
            }
        }
        onPlayerDiedNaturally(player);
        addToPlayerLives(player, -3600);
        if (isAlive(player)) {
            PlayerUtils.sendTitle(player, Text.literal("-1 hour").formatted(Formatting.RED), 20, 80, 20);
        }
    }

    @Override
    public void onClaimKill(ServerPlayerEntity killer, ServerPlayerEntity victim) {
        Boogeyman boogeyman  = boogeymanManager.getBoogeyman(killer);
        if (boogeyman == null || boogeyman.cured) {
            addToPlayerLives(killer, 1800);
            PlayerUtils.sendTitle(killer, Text.literal("+30 minutes").formatted(Formatting.GREEN), 20, 80, 20);
            return;
        }
        boogeymanManager.cure(killer);

        //Victim was killed by boogeyman - remove 2 hours from victim and add 1 hour to boogey
        boolean wasAlive = false;
        if (isAlive(victim)) {
            addToPlayerLives(victim, -3600);
            wasAlive = true;
        }
        addToPlayerLives(killer, 3600);
        if (isAlive(victim)) {
            PlayerUtils.sendTitle(killer, Text.literal("+1 hour").formatted(Formatting.GREEN), 20, 80, 20);
            PlayerUtils.sendTitle(victim, Text.literal("-1 extra hour").formatted(Formatting.RED), 20, 80, 20);
        }
        else if (wasAlive) {
            PlayerUtils.sendTitleWithSubtitle(killer,
                    Text.literal("+1 hour").formatted(Formatting.GREEN),
                    Text.literal("").append(victim.getStyledDisplayName()).append(Text.literal(" ran out of time!")),
                    20, 80, 20);
        }
        else {
            PlayerUtils.sendTitle(killer, Text.literal("+1 hour").formatted(Formatting.GREEN), 20, 80, 20);
        }
    }

    @Override
    public void onPlayerKilledByPlayer(ServerPlayerEntity victim, ServerPlayerEntity killer) {
        Boogeyman boogeyman  = boogeymanManager.getBoogeyman(killer);
        if (boogeyman == null || boogeyman.cured) {
            boolean wasAllowedToAttack = isAllowedToAttack(killer, victim);
            addToPlayerLives(victim, -3600);
            addToPlayerLives(killer, 1800);
            if (isAlive(victim)) {
                PlayerUtils.sendTitle(victim, Text.literal("-1 hour").formatted(Formatting.RED), 20, 80, 20);
                PlayerUtils.sendTitle(killer, Text.literal("+30 minutes").formatted(Formatting.GREEN), 20, 80, 20);
            }
            else {
                PlayerUtils.sendTitleWithSubtitle(killer,
                        Text.literal("+30 minutes").formatted(Formatting.GREEN),
                        Text.literal("").append(victim.getStyledDisplayName()).append(Text.literal(" ran out of time!")),
                        20, 80, 20);
            }
            if (wasAllowedToAttack) return;
            OtherUtils.broadcastMessageToAdmins(Text.of("§c [Unjustified Kill?] §f"+victim.getNameForScoreboard() + "§7 was killed by §f"+killer.getNameForScoreboard() +
                    "§7, who is not §cred name§7, and is not a §cboogeyman§f!"));
            OtherUtils.broadcastMessageToAdmins(Text.of("§7Remember to remove or add time to them (using §f/lives add/remove <player> <time>§7) if this was indeed an unjustified kill."));
            return;
        }
        boogeymanManager.cure(killer);

        //Victim was killed by boogeyman - remove 2 hours from victim and add 1 hour to boogey
        addToPlayerLives(victim, -7200);
        addToPlayerLives(killer, 3600);

        if (isAlive(victim)) {
            PlayerUtils.sendTitle(victim, Text.literal("-2 hours").formatted(Formatting.RED), 20, 80, 20);
            PlayerUtils.sendTitleWithSubtitle(killer,Text.of("§aYou are cured!"), Text.literal("+1 hour").formatted(Formatting.GREEN), 20, 80, 20);
        }
        else {
            PlayerUtils.sendTitleWithSubtitle(killer,Text.of("§aYou are cured, +1 hour"),
                    Text.literal("").append(victim.getStyledDisplayName()).append(Text.literal(" ran out of time!"))
                    , 20, 80, 20);
        }
    }

    @Override
    public boolean isAllowedToAttack(ServerPlayerEntity attacker, ServerPlayerEntity victim) {
        if (isOnLastLife(attacker, false)) return true;
        if (attacker.getPrimeAdversary() == victim && isOnLastLife(victim, false)) return true;
        if (isOnSpecificLives(attacker, 2, false) && isOnSpecificLives(victim, 3, false)) return true;
        if (attacker.getPrimeAdversary() == victim && (isOnSpecificLives(victim, 2, false) && isOnSpecificLives(attacker, 3, false))) return true;
        Boogeyman boogeymanAttacker = boogeymanManager.getBoogeyman(attacker);
        Boogeyman boogeymanVictim = boogeymanManager.getBoogeyman(victim);
        if (boogeymanAttacker != null && !boogeymanAttacker.cured) return true;
        if (attacker.getPrimeAdversary() == victim && (boogeymanVictim != null && !boogeymanVictim.cured)) return true;
        return false;
    }

    @Override
    public void onPlayerJoin(ServerPlayerEntity player) {
        super.onPlayerJoin(player);
        boogeymanManager.onPlayerJoin(player);
        if (!hasAssignedLives(player)) {
            setPlayerLives(player, DEFAULT_TIME);
        }
    }

    @Override
    public void reload() {
        DEFAULT_TIME = seriesConfig.getOrCreateInt("time_default", 86400);
        YELLOW_TIME = seriesConfig.getOrCreateInt("time_yellow", 57600);
        RED_TIME = seriesConfig.getOrCreateInt("time_red", 28800);
    }

    @Override
    public boolean sessionStart() {
        if (super.sessionStart()) {
            boogeymanManager.resetBoogeymen();
            activeActions.addAll(List.of(
                    boogeymanManager.actionBoogeymanWarn1,
                    boogeymanManager.actionBoogeymanWarn2,
                    boogeymanManager.actionBoogeymanChoose
            ));
            return true;
        }
        return false;
    }

    @Override
    public void sessionEnd() {
        super.sessionEnd();
        boogeymanManager.sessionEnd();
    }

    @Override
    public void playerLostAllLives(ServerPlayerEntity player) {
        super.playerLostAllLives(player);
        boogeymanManager.playerLostAllLives(player);
        List<ServerPlayerEntity> players = new ArrayList<>(PlayerUtils.getAllPlayers());
        players.remove(player);
        PlayerUtils.sendTitle(player, Text.literal("You have run out of time!").formatted(Formatting.RED), 20, 160, 20);
        PlayerUtils.sendTitleWithSubtitleToPlayers(players, player.getStyledDisplayName(), Text.literal("ran out of time!"), 20, 80, 20);
        OtherUtils.broadcastMessage(Text.literal("").append(player.getStyledDisplayName()).append(Text.of(" ran out of time")));
    }
}
