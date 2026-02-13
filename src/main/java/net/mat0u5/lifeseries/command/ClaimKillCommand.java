package net.mat0u5.lifeseries.command;

import com.mojang.brigadier.CommandDispatcher;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static net.mat0u5.lifeseries.Main.*;

public class ClaimKillCommand extends Command {

    @Override
    public boolean isAllowed() {
        return currentSeason.getSeason() != Seasons.UNASSIGNED;
    }

    @Override
    public Component getBannedText() {
        return Component.nullToEmpty("This command is only available when you have selected a Season.");
    }

    public List<String> getAdminCommands() {
        return List.of("claimkill");
    }

    public List<String> getNonAdminCommands() {
        return List.of("claimkill");
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            literal("claimkill")
                .then(argument("player", EntityArgument.player())
                    .suggests((context, builder) -> SharedSuggestionProvider.suggest(getSuggestions(), builder))
                    .executes(context -> claimCredit(
                        context.getSource(), EntityArgument.getPlayer(context, "player")
                    ))
                )
                .then(literal("validate")
                    .requires(PermissionManager::isAdmin)
                    .then(argument("killer", EntityArgument.player())
                        .then(argument("victim", EntityArgument.player())
                            .executes(context -> claimCreditAccept(
                                context.getSource(),
                                EntityArgument.getPlayer(context, "killer"),
                                EntityArgument.getPlayer(context, "victim")
                            ))
                        )
                    )
                )
        );
    }

    public List<String> getSuggestions() {
        if (server == null) return new ArrayList<>();
        List<String> suggestions = new ArrayList<>();
        Set<UUID> recentDeaths = currentSession.playerNaturalDeathLog.keySet();
        for (UUID uuid : recentDeaths) {
            ServerPlayer player = PlayerUtils.getPlayer(uuid);
            if (player == null) continue;
            suggestions.add(player.getScoreboardName());
        }
        return suggestions;
    }

    public int claimCredit(CommandSourceStack source, ServerPlayer victim) {
        if (checkBanned(source)) return -1;
        if (victim == null) return -1;
        Player player = source.getPlayer();
        if (player == null) return -1;

        Set<UUID> recentDeaths = currentSession.playerNaturalDeathLog.keySet();
        UUID victimUUID = victim.getUUID();
        if (!recentDeaths.contains(victimUUID)) {
            source.sendFailure(ModifiableText.CLAIMKILL_ERROR_NODEATH.get(victim));
            return -1;
        }
        if (player == victim) {
            source.sendFailure(ModifiableText.CLAIMKILL_ERROR_SELF.get());
            return -1;
        }
        Component textAll = ModifiableText.CLAIMKILL.get(player, victim);
        PlayerUtils.broadcastMessageToAdmins(textAll, 200);
        String validateCommand = TextUtils.formatString("/claimkill validate {} {}", player, victim);
        Component adminText = ModifiableText.CLAIMKILL_VALIDATE.get(TextUtils.runCommandText(validateCommand));
        PlayerUtils.broadcastMessageToAdmins(adminText, 200);

        return 1;
    }

    public int claimCreditAccept(CommandSourceStack source, ServerPlayer killer, ServerPlayer victim) {
        if (checkBanned(source)) return -1;
        if (killer == null) return -1;
        if (victim == null) return -1;

        Component message = TextUtils.format("{}§7's kill claim on {}§7 was accepted.", killer, victim);
        PlayerUtils.broadcastMessage(message);
        currentSeason.onClaimKill(killer, victim);
        currentSession.playerNaturalDeathLog.remove(victim.getUUID());

        return 1;
    }
}
