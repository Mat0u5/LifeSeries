package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.trivia;

import net.mat0u5.lifeseries.config.modifiable.ModifiableSound;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.entity.triviabot.server.trivia.TriviaHandler;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaQuestion;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.DatapackIntegration;
import net.mat0u5.lifeseries.utils.world.ItemSpawner;
import net.mat0u5.matlib.utils.other.Time;
import net.mat0u5.matlib.utils.other.Tuple;
import net.mat0u5.matlib.utils.world.ItemStackUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static net.mat0u5.matlib.MatLib.server;

//? if >= 1.20.5 {
import net.minecraft.core.component.DataComponents;
//?}

public class InBetweenTriviaHandler extends TriviaHandler {
	public static int AUTO_OPEN_TIME = Time.seconds(48).getTicks();
	public static final int LANDED_LIGHTS = 30;
	public static final int LANDED_FADE = 10;
	public static final int LANDED_WAIT = 15;
	private long ticks = 0;
	private int landedTicks = -1;
	public static ItemSpawner itemSpawner;
	public InBetweenTriviaHandler(TriviaBot bot) {
		super(bot);
	}

	public void tick() {
		super.tick();
		tickLanding();
		if (bot.tickCount % 2 == 0 && bot.submittedAnswer()) {
			bot.setAnalyzingTime(bot.getAnalyzingTime()-1);
		}

		if (bot.submittedAnswer()) {
			if (bot.answeredRight()) {
				if (bot.getAnalyzingTime() < -80) {
					bot.setLeaving(true);
					if (bot.isPassenger()) bot.removeVehicle();
					bot.noPhysics = true;
					float velocity = Math.min(0.5f, 0.25f * Math.abs((bot.getAnalyzingTime()+80) / (20.0f)));
					bot.setDeltaMovement(0,velocity,0);
					if (bot.getAnalyzingTime() < -200) bot.serverData.despawn();
				}
			}
			else {
				if (bot.getAnalyzingTime() < -100) {
					if (bot.isPassenger()) bot.removeVehicle();
					bot.noPhysics = true;
					float velocity = Math.min(0.5f, 0.25f * Math.abs((bot.getAnalyzingTime()+100) / (20.0f)));
					bot.setDeltaMovement(0,velocity,0);
					if (bot.getAnalyzingTime() < -200) bot.serverData.despawn();
				}
			}
		}
		else {
			bot.serverData.handleHighVelocity();
			if (bot.interactedWith() && getRemainingTicks() <= 0) {
				if (!bot.ranOutOfTime()) {
					ServerPlayer boundPlayer = bot.serverData.getBoundPlayer();
					if (boundPlayer != null) {
						SimplePackets.RESET_TRIVIA.sendToClient(boundPlayer);
						DatapackIntegration.EVENT_TRIVIA_FAIL_TIME.trigger(List.of(
								new DatapackIntegration.Events.MacroEntry("Player", boundPlayer.getScoreboardName()),
								new DatapackIntegration.Events.MacroEntry("TriviaBot", bot.getStringUUID())
						));
					}

					// Fake incorrect answer
					bot.setSubmittedAnswer(true);
					answeredIncorrect();
				}
				//bot.setRanOutOfTime(true);
			}
		}


		float distanceToPlayer = 1000;
		ServerPlayer player = bot.serverData.getBoundPlayer();
		if (player != null) {
			distanceToPlayer = bot.distanceTo(player);
		}

		ticks++;
		if (player != null && !bot.interactedWith()) {
			if (ticks > AUTO_OPEN_TIME) {
				if (distanceToPlayer <= 4) {
					startTrivia(player);
				}
				else {
					bot.pathfinding.fakeTeleportToPlayer();
					startTrivia(player);
				}
			}
		}
	}

	private void tickLanding() {
		if (!isLanding() || (landedTicks < 0 && (!bot.onGround() || bot.isBotGliding()))) return;
		landedTicks++;
		ServerPlayer player = bot.serverData.getBoundPlayer();
		if (landedTicks == LANDED_LIGHTS && player != null) InBetweenTriviaManager.sendPacketInfo(player, "fade");
	}

	public boolean isLanding() {
		return landedTicks < LANDED_LIGHTS + LANDED_FADE + LANDED_WAIT;
	}

	public Tuple<Integer, TriviaQuestion> generateTrivia(ServerPlayer boundPlayer) {
		return InBetweenTriviaManager.getTriviaQuestion(boundPlayer);
	}

	public void setTimeBasedOnDifficulty(int difficulty) {
		timeToComplete = InBetweenTriviaManager.QUESTION_TIME.get();
	}

	@Override
	public void startTrivia(ServerPlayer boundPlayer) {
		if (!bot.interactedWith()) {
			SimplePackets.STOP_TRIVIA_SOUNDS.sendToClient(boundPlayer);
		}
		super.startTrivia(boundPlayer);
	}

	public InteractionResult interactMob(Player player, InteractionHand hand) {
		if (bot.level().isClientSide()) return InteractionResult.SUCCESS;
		ServerPlayer boundPlayer = bot.serverData.getBoundPlayer();
		if (boundPlayer == null) return InteractionResult.PASS;
		if (boundPlayer.getUUID() != player.getUUID()) return InteractionResult.PASS;
		if (bot.submittedAnswer()) return InteractionResult.PASS;
		if (bot.interactedWith() && getRemainingTicks() <= 0) return InteractionResult.PASS;

		startTrivia(boundPlayer);

		return InteractionResult.SUCCESS;
	}

	public boolean handleAnswer(int answer) {
		if (super.handleAnswer(answer)) {
			bot.setAnalyzingTime(0); // No analyzing
			return true;
		}
		return false;
	}

	public void answeredCorrect() {
		super.answeredCorrect();
		TaskScheduler.scheduleTask(74, this::spawnItemForPlayer);
		TaskScheduler.scheduleTask(99, this::spawnItemForPlayer);
		TaskScheduler.scheduleTask(129, () -> {
			for (int i = 0; i < 6; i++) {
				spawnItemForPlayer(true);
			}
		});
		ModifiableSound.FINALLIFE_TRIVIA_CORRECT.playWithSource(PlayerUtils.getAllPlayers(), bot, SoundSource.NEUTRAL, 1f, 1);
	}

	public void answeredIncorrect() {
		super.answeredIncorrect();
		//TODO unknown if anything happens.
		ModifiableSound.FINALLIFE_TRIVIA_INCORRECT.playWithSource(PlayerUtils.getAllPlayers(), bot, SoundSource.NEUTRAL, 1f, 1);
	}

	public void spawnItemForPlayer() {
		spawnItemForPlayer(false);
	}
	public void spawnItemForPlayer(boolean inAir) {
		if (bot.level().isClientSide()) return;
		if (itemSpawner == null) return;
		if (bot.serverData.getBoundPlayer() == null) return;
		Vec3 playerPos = bot.serverData.getBoundPlayer().position();
		Vec3 pos = bot.position().add(0,0.7,0);
		Vec3 relativeTargetPos = new Vec3(
				playerPos.x() - pos.x(),
				0,
				playerPos.z() - pos.z()
		);
		Vec3 vector = Vec3.ZERO;
		if (relativeTargetPos.lengthSqr() > 0.0001) {
			if (inAir) {
				vector = relativeTargetPos.normalize().scale(0.04).add(0,0.33,0);
			}
			else {
				vector = relativeTargetPos.normalize().scale(0.2).add(0,0.1,0);
			}
		}

		List<ItemStack> lootTableItems = ItemSpawner.getRandomItemsFromLootTable(server, (ServerLevel) bot.level(), bot.serverData.getBoundPlayer(), IdentifierHelper.of("lifeseriesdynamic", "finallife_trivia_reward_loottable"), false);
		if (!lootTableItems.isEmpty()) {
			for (ItemStack item : lootTableItems) {
				ItemStackUtils.spawnItemForPlayerWithVelocity((ServerLevel) bot.level(), pos, item, bot.serverData.getBoundPlayer(), vector);
			}
		}
		else {
			ItemStack randomItem = itemSpawner.getRandomItem();
			ItemStackUtils.spawnItemForPlayerWithVelocity((ServerLevel) bot.level(), pos, randomItem, bot.serverData.getBoundPlayer(), vector);
		}
	}

	public static void initializeItemSpawner() {
		//TODO items probably do not match
		itemSpawner = new ItemSpawner();
		itemSpawner.addItem(new ItemStack(Items.GOLDEN_APPLE, 2), 20);
		itemSpawner.addItem(new ItemStack(Items.ENDER_PEARL, 2), 20);
		itemSpawner.addItem(new ItemStack(Items.TRIDENT), 10);
		itemSpawner.addItem(new ItemStack(Items.POWERED_RAIL, 16), 10);
		itemSpawner.addItem(new ItemStack(Items.DIAMOND, 4), 20);
		itemSpawner.addItem(new ItemStack(Items.CREEPER_SPAWN_EGG), 10);
		itemSpawner.addItem(new ItemStack(Items.GOLDEN_CARROT, 8), 10);
		//? if >= 1.21 {
		itemSpawner.addItem(new ItemStack(Items.WIND_CHARGE, 16), 10);
		//?}
		itemSpawner.addItem(new ItemStack(Items.SCULK_SHRIEKER, 2), 10);
		itemSpawner.addItem(new ItemStack(Items.SCULK_SENSOR, 8), 10);
		itemSpawner.addItem(new ItemStack(Items.TNT, 8), 20);
		itemSpawner.addItem(new ItemStack(Items.COBWEB, 8), 10);
		itemSpawner.addItem(new ItemStack(Items.OBSIDIAN, 8), 10);
		itemSpawner.addItem(new ItemStack(Items.PUFFERFISH_BUCKET), 10);
		itemSpawner.addItem(new ItemStack(Items.NETHERITE_CHESTPLATE), 10);
		itemSpawner.addItem(new ItemStack(Items.NETHERITE_LEGGINGS), 10);
		itemSpawner.addItem(new ItemStack(Items.NETHERITE_BOOTS), 10);
		itemSpawner.addItem(new ItemStack(Items.ARROW, 64), 10);
		itemSpawner.addItem(new ItemStack(Items.IRON_BLOCK, 2), 10);

		//? if >= 1.21 {
		ItemStack mace = new ItemStack(Items.MACE);
		ItemStackUtils.setCustomComponentBoolean(mace, "IgnoreBlacklist", true);
		ItemStackUtils.setCustomComponentBoolean(mace, "NoModifications", true);
		mace.setDamageValue(mace.getMaxDamage()-1);
		itemSpawner.addItem(mace, 5);
		//?}

		ItemStack endCrystal = new ItemStack(Items.END_CRYSTAL);
		ItemStackUtils.setCustomComponentBoolean(endCrystal, "IgnoreBlacklist", true);
		itemSpawner.addItem(endCrystal, 10);

		//? if >= 1.20.5 {
		ItemStack patat = new ItemStack(Items.POISONOUS_POTATO);
		patat.set(DataComponents.CUSTOM_NAME, Component.nullToEmpty("§6§l§nThe Sacred Patat"));
		ItemStackUtils.addLoreToItemStack(patat,
				List.of(Component.nullToEmpty("§5§oEating bot might help you. Or maybe not..."))
		);
		itemSpawner.addItem(patat, 1);
		//?}
	}
}
