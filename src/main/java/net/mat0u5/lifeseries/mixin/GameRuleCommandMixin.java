package net.mat0u5.lifeseries.mixin;

import com.mojang.brigadier.context.CommandContext;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.GameRuleCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.mat0u5.lifeseries.Main.seasonConfig;
//? if <= 1.21.9 {
import net.minecraft.world.level.GameRules;
//?} else {
/*import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.gamerules.GameRule;
*///?}

@Mixin(value = GameRuleCommand.class, priority = 1)
public class GameRuleCommandMixin {
    //? if <= 1.21.9 {
    @Inject(method = "setRule", at = @At("TAIL"))
    private static <T extends GameRules.Value<T>> void onChanged(CommandContext<CommandSourceStack> commandContext, GameRules.Key<T> key, CallbackInfoReturnable<Integer> cir) {
        MinecraftServer server = commandContext.getSource().getServer();
        boolean reload = false;
        if (key.equals(GameRules.RULE_KEEPINVENTORY)) {
            seasonConfig.setProperty(seasonConfig.KEEP_INVENTORY.key, String.valueOf(OtherUtils.getBooleanGameRule(server.overworld(), GameRules.RULE_KEEPINVENTORY)));
            reload = true;
        }
        if (key.equals(GameRules.RULE_ANNOUNCE_ADVANCEMENTS)) {
            seasonConfig.setProperty(seasonConfig.SHOW_ADVANCEMENTS.key, String.valueOf(OtherUtils.getBooleanGameRule(server.overworld(), GameRules.RULE_ANNOUNCE_ADVANCEMENTS)));
            reload = true;
        }
        //? if >= 1.21.6 {
        /*if (key.equals(GameRules.RULE_LOCATOR_BAR)) {
            seasonConfig.setProperty(seasonConfig.LOCATOR_BAR.key, String.valueOf(OtherUtils.getBooleanGameRule(server.overworld(), GameRules.RULE_LOCATOR_BAR)));
            reload = true;
        }
        *///?}
        if (reload && !Main.modDisabled()) {
            Main.softReloadStart();
        }
    }
    //?} else {
    /*@Inject(method = "setRule", at = @At("TAIL"))
    private static <T> void onChanged(CommandContext<CommandSourceStack> commandContext, GameRule<T> key, CallbackInfoReturnable<Integer> cir) {
        MinecraftServer server = commandContext.getSource().getServer();
        boolean reload = false;
        if (key.equals(GameRules.KEEP_INVENTORY)) {
            seasonConfig.setProperty(seasonConfig.KEEP_INVENTORY.key, String.valueOf(OtherUtils.getBooleanGameRule(server.overworld(), GameRules.KEEP_INVENTORY)));
            reload = true;
        }
        if (key.equals(GameRules.SHOW_ADVANCEMENT_MESSAGES)) {
            seasonConfig.setProperty(seasonConfig.SHOW_ADVANCEMENTS.key, String.valueOf(OtherUtils.getBooleanGameRule(server.overworld(), GameRules.SHOW_ADVANCEMENT_MESSAGES)));
            reload = true;
        }
        if (key.equals(GameRules.LOCATOR_BAR)) {
            seasonConfig.setProperty(seasonConfig.LOCATOR_BAR.key, String.valueOf(OtherUtils.getBooleanGameRule(server.overworld(), GameRules.LOCATOR_BAR)));
            reload = true;
        }
        if (reload && !Main.modDisabled()) {
            Main.softReloadStart();
        }
    }
    *///?}
}
