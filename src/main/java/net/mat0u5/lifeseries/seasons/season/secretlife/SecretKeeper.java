package net.mat0u5.lifeseries.seasons.season.secretlife;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.interfaces.IPlayer;
import net.mat0u5.lifeseries.utils.other.*;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.AnimationUtils;
import net.mat0u5.lifeseries.utils.world.DatapackIntegration;
import net.mat0u5.lifeseries.utils.world.ItemSpawner;
import net.mat0u5.lifeseries.utils.world.ItemStackUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static net.mat0u5.lifeseries.LifeSeries.*;
import static net.mat0u5.lifeseries.seasons.season.secretlife.TaskManager.*;

public class SecretKeeper {
	public static BlockPos successButtonPos;
	public static BlockPos rerollButtonPos;
	public static BlockPos failButtonPos;
	public static BlockPos itemSpawnerPos;
	public static boolean secretKeeperBeingUsed = false;
	public static int secretKeeperBeingUsedFor = 0;
	public static List<UUID> pendingConfirmationTasks = new ArrayList<>();

	public static void rewardHealth(ServerPlayer player, int addHealth, TaskTypes taskType) {
		if (server == null) return;
		if (addHealth == 0) {
			secretKeeperBeingUsed = false;
			return;
		}
		secretKeeperBeingUsed = true;
		SecretLife season = (SecretLife) currentSeason;
		double currentHealth = season.getPlayerHealth(player);
		if (currentHealth > SecretLife.MAX_HEALTH) currentHealth = SecretLife.MAX_HEALTH;
		int rounded = (int) Math.floor(currentHealth);
		int remainderToMax = (int) SecretLife.MAX_HEALTH - rounded;

		if (addHealth <= remainderToMax && remainderToMax != 0) {
			season.addPlayerHealth(player, addHealth);
			secretKeeperBeingUsed = false;
		}
		else {
			rewardHealthAndItems(player, addHealth, taskType, remainderToMax);
		}
	}

	public static void rewardHealthAndItems(ServerPlayer player, int addHealth, TaskTypes taskType, int remainderToMax) {
		SecretLife season = (SecretLife) currentSeason;
		if (remainderToMax != 0) season.setPlayerHealth(player, SecretLife.MAX_HEALTH);
		int itemsNum = (addHealth - remainderToMax)/2;
		if (itemsNum == 0) {
			secretKeeperBeingUsed = false;
			return;
		}
		Vec3 spawnPos = OtherUtils.getCenter(itemSpawnerPos);
		for (int i = 0; i <= itemsNum; i++) {
			if (i == 0) continue;
			TaskScheduler.scheduleTask(3*i, () -> {
				server.overworld().playSound(null, spawnPos.x(), spawnPos.y(), spawnPos.z(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1.0F, 1.0F);

				List<ItemStack> lootTableItems = new ArrayList<>();

				if (taskType == TaskTypes.HARD) {
					lootTableItems = ItemSpawner.getRandomItemsFromLootTable(server, server.overworld(), player, IdentifierHelper.of("lifeseriesdynamic", "task_reward_loottable_hard"), true);
				}
				else if (taskType == TaskTypes.RED) {
					lootTableItems = ItemSpawner.getRandomItemsFromLootTable(server, server.overworld(), player, IdentifierHelper.of("lifeseriesdynamic", "task_reward_loottable_red"), true);
				}

				if (taskType == TaskTypes.EASY || lootTableItems.isEmpty()) {
					lootTableItems = ItemSpawner.getRandomItemsFromLootTable(server, server.overworld(), player, IdentifierHelper.of("lifeseriesdynamic", "task_reward_loottable"), false);
				}

				if (!lootTableItems.isEmpty()) {
					for (ItemStack item : lootTableItems) {
						ItemStackUtils.spawnItemForPlayer(server.overworld(), spawnPos, item, player);
					}
				}
				else {
					ItemStack randomItem = season.itemSpawner.getRandomItem();
					ItemStackUtils.spawnItemForPlayer(server.overworld(), spawnPos, randomItem, player);
				}

			});
		}
		TaskScheduler.scheduleTask(3*itemsNum+20, () -> secretKeeperBeingUsed = false);
	}

	public static boolean isBeingUsed(ServerPlayer player) {
		if (!secretKeeperBeingUsed) return false;
		((IPlayer) player).ls$message(ModifiableText.SECRETLIFE_SECRETKEEPER_INUSE.get());
		return true;
	}


	public static boolean hasSessionStarted(ServerPlayer player) {
		if (currentSession.statusNotStarted()) {
			((IPlayer) player).ls$message(ModifiableText.SESSION_ERROR_START.get());
			return false;
		}
		return true;
	}

	public static boolean hasTaskBookCheck(ServerPlayer player, boolean sendMessage) {
		TaskTypes type = getPlayersTaskType(player);
		if (type != null) return true;
		if (sendMessage) {
			((IPlayer) player).ls$message(ModifiableText.SECRETLIFE_TASK_MISSING.get());
		}
		return false;
	}

	public static void clickSucceed(ServerPlayer player, boolean fromCommand) {
		if (server == null) return;
		if (!fromCommand) {
			if (!hasSessionStarted(player)) return;
			if (isBeingUsed(player)) return;
		}
		TaskTypes type = getPlayersTaskType(player);
		if (!hasTaskBookCheck(player, !fromCommand)) return;
		if (!fromCommand) {
			if (TASKS_NEED_CONFIRMATION) {
				if (!pendingConfirmationTasks.contains(player.getUUID())) {
					pendingConfirmationTasks.add(player.getUUID());
					PlayerUtils.broadcastMessageToAdmins(ModifiableText.SECRETLIFE_TASK_PENDING.get(player));
					PlayerUtils.broadcastMessageToAdmins(getShowTaskMessage(player));
					PlayerUtils.broadcastMessageToAdmins(ModifiableText.SECRETLIFE_TASK_PENDING_ACCEPT.get(TextUtils.runCommandText("/task succeed "+player.getScoreboardName())));
				}
				((IPlayer) player).ls$message(ModifiableText.SECRETLIFE_TASK_PENDING_NOTIFICATION.get());
				return;
			}
		}
		pendingConfirmationTasks.remove(player.getUUID());
		if (BROADCAST_SECRET_KEEPER) {
			PlayerUtils.broadcastMessage(ModifiableText.SECRETLIFE_TASK_SUCCEED.get(player));
		}
		if (PUBLIC_TASKS_ON_SUBMIT) {
			PlayerUtils.broadcastMessage(getShowTaskMessage(player));
		}
		succeedTask(player, type);
	}

	public static void succeedTask(ServerPlayer player, TaskTypes type) {
		SessionTranscript.successTask(player);
		removePlayersTaskBook(player);
		submittedOrFailed.add(player.getUUID());
		secretKeeperBeingUsed = true;

		Vec3 centerPos = OtherUtils.getCenter(itemSpawnerPos);
		server.overworld().playSound(null, centerPos.x(), centerPos.y(), centerPos.z(), SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("secretlife_task_succeed")), SoundSource.PLAYERS, 1.0F, 1.0F);
		TaskScheduler.scheduleTask(60, () -> {
			AnimationUtils.createGlyphAnimation(server.overworld(), centerPos, 45);
		});
		TaskScheduler.scheduleTask(130, () -> {
			//? if < 1.21 {
			/*server.overworld().playSound(null, centerPos.x(), centerPos.y(), centerPos.z(), SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.PLAYERS, 1.0F, 1.0F);
			 *///?} else {
			server.overworld().playSound(null, centerPos.x(), centerPos.y(), centerPos.z(), SoundEvents.TRIAL_SPAWNER_EJECT_ITEM, SoundSource.PLAYERS, 1.0F, 1.0F);
			//?}
			AnimationUtils.spawnFireworkBall(server.overworld(), centerPos, 40, 0.3, new Vector3f(0, 1, 0));
			if (type == TaskTypes.EASY) {
				showHeartTitle(player, EASY_SUCCESS);
				rewardHealth(player, EASY_SUCCESS, type);
			}
			if (type == TaskTypes.HARD) {
				showHeartTitle(player, HARD_SUCCESS);
				rewardHealth(player, HARD_SUCCESS, type);
			}
			if (type == TaskTypes.RED) {
				showHeartTitle(player, RED_SUCCESS);
				rewardHealth(player, RED_SUCCESS, type);
			}
		});
		DatapackIntegration.EVENT_TASK_SUCCEED.trigger(new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName()));
		chooseNewTaskForPlayerIfNecessary(player);
	}

	public static void clickReroll(ServerPlayer player, boolean fromCommand) {
		if (!fromCommand) {
			if (!hasSessionStarted(player)) return;
			if (isBeingUsed(player)) return;
		}
		TaskTypes type = getPlayersTaskType(player);
		if (!hasTaskBookCheck(player, !fromCommand)) return;
		if (type == TaskTypes.RED) {
			clickFail(player, false);
			return;
		}
		if (type == TaskTypes.EASY) {
			rerollTask(player);
		}
		if (type == TaskTypes.HARD) {
			if (!((IPlayer) player).ls$isOnLastLife(true)) {
				((IPlayer) player).ls$message(ModifiableText.SECRETLIFE_TASK_REROLL_HARD_FAIL.get());
			}
			else {
				((IPlayer) player).ls$message(ModifiableText.SECRETLIFE_TASK_REROLL_HARD_FAIL_RED.get());
			}
		}
	}

	public static void rerollTask(ServerPlayer player) {
		removePlayersTaskBook(player);
		if (BROADCAST_SECRET_KEEPER) {
			PlayerUtils.broadcastMessage(ModifiableText.SECRETLIFE_TASK_REROLL.get(player));
		}
		if (PUBLIC_TASKS_ON_SUBMIT) {
			PlayerUtils.broadcastMessage(getShowTaskMessage(player));
		}
		SessionTranscript.rerollTask(player);
		secretKeeperBeingUsed = true;
		TaskTypes newType = TaskTypes.HARD;
		if (((IPlayer) player).ls$isOnLastLife(false)) {
			chooseTasks(List.of(player), TaskTypes.RED);
			return;
		}

		PlayerUtils.playSoundToPlayer(player, SoundEvent.createVariableRangeEvent(IdentifierHelper.parse("secretlife_task_reroll")));
		PlayerUtils.playSoundToPlayer(player, SoundEvents.UI_BUTTON_CLICK.value());
		PlayerUtils.sendTitle(player, ModifiableText.SECRETLIFE_TASK_REROLL_PT1.get(),20,35,0);

		TaskScheduler.scheduleTask(35, () -> {
			PlayerUtils.playSoundToPlayer(player, SoundEvents.UI_BUTTON_CLICK.value());
			PlayerUtils.sendTitle(player, ModifiableText.SECRETLIFE_TASK_REROLL_PT2.get(),20,35,0);
		});
		TaskScheduler.scheduleTask(70, () -> {
			PlayerUtils.playSoundToPlayer(player, SoundEvents.UI_BUTTON_CLICK.value());
			PlayerUtils.sendTitle(player, ModifiableText.SECRETLIFE_TASK_REROLL_PT3.get(),20,35,0);
		});
		TaskScheduler.scheduleTask(105, () -> {
			PlayerUtils.playSoundToPlayer(player, SoundEvents.UI_BUTTON_CLICK.value());
			PlayerUtils.sendTitle(player, ModifiableText.SECRETLIFE_TASK_REROLL_PT4.get(),20,30,0);
		});
		TaskScheduler.scheduleTask(140, () -> {
			AnimationUtils.playSecretLifeTotemAnimation(player, false);
		});
		TaskScheduler.scheduleTask(175, () -> {
			assignRandomTaskToPlayer(player, newType);
			secretKeeperBeingUsed = false;
		});
		DatapackIntegration.EVENT_TASK_REROLL.trigger(new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName()));
		return;
	}

	public static void clickFail(ServerPlayer player, boolean fromCommand) {
		if (server == null) return;
		if (!fromCommand) {
			if (!hasSessionStarted(player)) return;
			if (isBeingUsed(player)) return;
		}
		SecretLife season = (SecretLife) currentSeason;
		TaskTypes type = getPlayersTaskType(player);
		if (!hasTaskBookCheck(player, !fromCommand)) return;
		if (BROADCAST_SECRET_KEEPER) {
			PlayerUtils.broadcastMessage(ModifiableText.SECRETLIFE_TASK_FAIL.get(player));
		}
		if (PUBLIC_TASKS_ON_SUBMIT) {
			PlayerUtils.broadcastMessage(getShowTaskMessage(player));
		}
		failTask(player, type);
	}

	public static void failTask(ServerPlayer player, TaskTypes type) {
		SecretLife season = (SecretLife) currentSeason;
		SessionTranscript.failTask(player);
		removePlayersTaskBook(player);
		submittedOrFailed.add(player.getUUID());
		secretKeeperBeingUsed = true;

		Vec3 centerPos = OtherUtils.getCenter(itemSpawnerPos);

		server.overworld().playSound(null, centerPos.x(), centerPos.y(), centerPos.z(), SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("secretlife_task_fail")), SoundSource.PLAYERS, 1.0F, 1.0F);
		TaskScheduler.scheduleTask(60, () -> {
			AnimationUtils.createGlyphAnimation(server.overworld(), centerPos, 45);
		});
		TaskScheduler.scheduleTask(140, () -> {
			AnimationUtils.spawnFireworkBall(server.overworld(), centerPos, 40, 0.3, new Vector3f(1, 0, 0));
			if (type == TaskTypes.EASY) {
				showHeartTitle(player, EASY_FAIL);
				season.removePlayerHealth(player, -EASY_FAIL);
			}
			if (type == TaskTypes.HARD) {
				showHeartTitle(player, HARD_FAIL);
				season.removePlayerHealth(player, -HARD_FAIL);
			}
			if (type == TaskTypes.RED) {
				showHeartTitle(player, RED_FAIL);
				season.removePlayerHealth(player, -RED_FAIL);
			}
			if (!((IPlayer) player).ls$isOnLastLife(false)) {
				secretKeeperBeingUsed = false;
			}
		});
		DatapackIntegration.EVENT_TASK_FAIL.trigger(new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName()));
		chooseNewTaskForPlayerIfNecessary(player);
	}

	public static Component getShowTaskMessage(ServerPlayer player) {
		String rawTask = "";

		Task task = null;

		if (hasTaskBookCheck(player, false) && assignedTasks.containsKey(player.getUUID())) {
			task = assignedTasks.get(player.getUUID());
		}
		else if (preAssignedTasks.containsKey(player.getUUID())) {
			task = preAssignedTasks.get(player.getUUID());
		}

		if (task == null) return Component.empty();

		if (!task.formattedTask.isEmpty()) {
			rawTask = task.formattedTask;
		}
		else {
			rawTask = task.rawTask;
		}

		return ModifiableText.SECRETLIFE_TASK_SHOW_PAST.get(TextUtils.selfMessageText(rawTask), player);
	}


	public static void chooseNewTaskForPlayerIfNecessary(ServerPlayer player) {
		if (currentSession.statusFinished()) return;
		if (((IPlayer) player).ls$isOnLastLife(false) || CONSTANT_TASKS) {
			TaskScheduler.scheduleTask(Time.seconds(6), () -> {
				TaskTypes newType = ((IPlayer) player).ls$isOnLastLife(false) ? TaskTypes.RED : TaskTypes.EASY;
				chooseTasks(List.of(player), newType);
			});
		}
	}

	public static void showHeartTitle(ServerPlayer player, int amount) {
		if (amount == 0) return;
		SecretLife season = (SecretLife) currentSeason;
		if (amount > 0 && season.getPlayerHealth(player) >= SecretLife.MAX_HEALTH) return;
		int healthBefore = Mth.ceil(season.getPlayerHealth(player));
		int finalAmount = amount;
		if (healthBefore + amount <= 0) {
			finalAmount = -healthBefore+1;
			if (healthBefore == 1) finalAmount = 0;
		}
		if (healthBefore + amount > SecretLife.MAX_HEALTH) {
			if (amount > 0) finalAmount = (int) (SecretLife.MAX_HEALTH-healthBefore);
			else finalAmount = amount;
		}
		double finalHearts = Math.abs(finalAmount) / 2.0;
		if (finalHearts == 0) return;

		String finalStr = String.valueOf(finalHearts);
		if (finalAmount%2==0) finalStr = String.valueOf((int)finalHearts);

		if (finalAmount >= 0) {
			PlayerUtils.sendTitle(player, ModifiableText.SECRETLIFE_HEART_ADD.get(finalStr, TextUtils.pluralize("Heart", finalHearts)), 20, 40, 20);
		}
		else {
			PlayerUtils.sendTitle(player, ModifiableText.SECRETLIFE_HEART_REMOVE.get(finalStr, TextUtils.pluralize("Heart", finalHearts)), 20, 40, 20);
		}
	}

	public static boolean alreadyHasPos(BlockPos pos) {
		if (successButtonPos != null && successButtonPos.equals(pos)) return true;
		if (rerollButtonPos != null && rerollButtonPos.equals(pos)) return true;
		if (failButtonPos != null && failButtonPos.equals(pos)) return true;
		return itemSpawnerPos != null && itemSpawnerPos.equals(pos);
	}

	public static void positionFound(BlockPos pos, boolean fromButton) {
		if (pos == null) return;
		if (alreadyHasPos(pos)) {
			PlayerUtils.broadcastMessage(Component.literal("§c[SecretLife setup] This location is already being used."), 20);
			return;
		}
		if (successButtonPos == null && fromButton) {
			successButtonPos = pos;
			PlayerUtils.broadcastMessage(Component.literal("§a[SecretLife setup 1/4] Location set.\n"));
		}
		else if (rerollButtonPos == null && fromButton) {
			rerollButtonPos = pos;
			PlayerUtils.broadcastMessage(Component.literal("§a[SecretLife setup 2/4] Location set.\n"));
		}
		else if (failButtonPos == null && fromButton) {
			failButtonPos = pos;
			PlayerUtils.broadcastMessage(Component.literal("§a[SecretLife setup 3/4] Location set.\n"));
		}
		if (itemSpawnerPos == null && !fromButton) {
			if (successButtonPos != null && rerollButtonPos != null && failButtonPos != null) {
				itemSpawnerPos = pos;
				PlayerUtils.broadcastMessage(Component.literal("§a[SecretLife] All locations have been set. If you wish to change them in the future, use §2'/task changeLocations'\n"));
				PlayerUtils.broadcastMessage(ModifiableText.SESSION_START_PROMPT.get());
			}
		}
		locationsConfig.saveLocations();
		checkSecretLifePositions();
	}

	public static boolean searchingForLocations = false;
	public static boolean checkSecretLifePositions() {
		if (successButtonPos == null) {
			PlayerUtils.broadcastMessageToAdmins(Component.literal("§c[SecretLife setup 1/4] Location for the secret keeper task §6§lSUCCESS BUTTON§r§c was not found. §nThe next button you click will be set as the location."));
			searchingForLocations = true;
			return false;
		}
		if (rerollButtonPos == null) {
			PlayerUtils.broadcastMessageToAdmins(Component.literal("§c[SecretLife setup 2/4] Location for the secret keeper task §6§lRE-ROLL BUTTON§r§c was not found. §nThe next button you click will be set as the location."));
			searchingForLocations = true;
			return false;
		}
		if (failButtonPos == null) {
			PlayerUtils.broadcastMessageToAdmins(Component.literal("§c[SecretLife setup 3/4] Location for the secret keeper task §6§lFAIL BUTTON§r§c was not found. §nThe next button you click will be set as the location."));
			searchingForLocations = true;
			return false;
		}
		if (itemSpawnerPos == null) {
			PlayerUtils.broadcastMessageToAdmins(Component.literal("§c[SecretLife setup 4/4] Location for the secret keeper task §6§lITEM SPAWN BLOCK§r§c was not found. §nPlease place a bedrock block at the desired spot to mark it."));
			searchingForLocations = true;
			return false;
		}
		searchingForLocations = false;
		return true;
	}

	public static void onBlockUse(ServerPlayer player, ServerLevel level, BlockHitResult hitResult) {
		BlockPos pos = hitResult.getBlockPos();
		String name = level.getBlockState(pos).getBlock().getName().getString().toLowerCase(Locale.ROOT);
		if (name.contains("button")) {
			if (searchingForLocations) {
				positionFound(pos, true);
			}
			else {
				if (pos.equals(successButtonPos)) {
					clickSucceed(player, false);
				}
				else if (pos.equals(rerollButtonPos)) {
					clickReroll(player, false);
				}
				else if (pos.equals(failButtonPos)) {
					clickFail(player, false);
				}
			}
		}
		if (!searchingForLocations) return;
		if (successButtonPos == null || rerollButtonPos == null || failButtonPos == null) return;
		BlockPos placePos = pos.relative(hitResult.getDirection());
		TaskScheduler.scheduleTask(1, () -> {
			if (level.getBlockState(placePos).getBlock() == Blocks.BEDROCK) {
				positionFound(placePos, false);
				level.destroyBlock(placePos, false);
			}
		});
	}

	public static void tick() {
		if (secretKeeperBeingUsed) {
			secretKeeperBeingUsedFor++;
		}
		else {
			secretKeeperBeingUsedFor = 0;
		}
		if (secretKeeperBeingUsedFor > 500) {
			secretKeeperBeingUsed = false;
			secretKeeperBeingUsedFor = 0;
			LifeSeries.LOGGER.error("Resetting Secret Keeper.");
		}
	}
}
