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
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Objects;

import static net.mat0u5.matlib.MatLib.server;

//? if <= 1.20.5 {
/*import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
*///?}
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
	public static ItemSpawner itemSpawner1;
	public static ItemSpawner itemSpawner2;
	public static ItemSpawner itemSpawner3;
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
			ModifiableSound.FINALLIFE_TRIVIA_INTRO.stop(boundPlayer);
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
		TaskScheduler.scheduleTask(74, () -> spawnItemForPlayer(1));
		TaskScheduler.scheduleTask(99, () -> spawnItemForPlayer(2));
		TaskScheduler.scheduleTask(129, () -> {
			spawnItemForPlayer(3);
		});
		ModifiableSound.FINALLIFE_TRIVIA_CORRECT.playWithSource(PlayerUtils.getAllPlayers(), bot, SoundSource.NEUTRAL, 1f, 1);
	}

	public void answeredIncorrect() {
		super.answeredIncorrect();
		//TODO unknown if anything happens.
		ModifiableSound.FINALLIFE_TRIVIA_INCORRECT.playWithSource(PlayerUtils.getAllPlayers(), bot, SoundSource.NEUTRAL, 1f, 1);
	}

	public void spawnItemForPlayer(int index) {
		if (bot.level().isClientSide()) return;
		if (index == 1 && itemSpawner1 == null) return;
		if (index == 2 && itemSpawner2 == null) return;
		if (index == 3 && itemSpawner3 == null) return;
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
			vector = relativeTargetPos.normalize().scale(0.04).add(0,0.33,0);
		}

		List<ItemStack> lootTableItems = ItemSpawner.getRandomItemsFromLootTable(server, (ServerLevel) bot.level(), bot.serverData.getBoundPlayer(), IdentifierHelper.of("lifeseriesdynamic", "finallife_trivia_reward_loottable_drop"+index), false);
		if (!lootTableItems.isEmpty()) {
			for (ItemStack item : lootTableItems) {
				ItemStackUtils.spawnItemForPlayerWithVelocity((ServerLevel) bot.level(), pos, item, bot.serverData.getBoundPlayer(), vector);
			}
		}
		else {
			if (index == 1) {
				ItemStack randomItem = itemSpawner1.getRandomItem();
				ItemStackUtils.spawnItemForPlayerWithVelocity((ServerLevel) bot.level(), pos, randomItem, bot.serverData.getBoundPlayer(), vector);
			}
			if (index == 2) {
				ItemStack randomItem = itemSpawner2.getRandomItem();
				ItemStackUtils.spawnItemForPlayerWithVelocity((ServerLevel) bot.level(), pos, randomItem, bot.serverData.getBoundPlayer(), vector);
			}
			if (index == 3) {
				for (int i = 0; i < 5; i++) {
					ItemStack randomItem = itemSpawner3.getRandomItem();
					ItemStackUtils.spawnItemForPlayerWithVelocity((ServerLevel) bot.level(), pos, randomItem, bot.serverData.getBoundPlayer(), vector);
				}
			}
		}
	}

	public static void initializeItemSpawners() {
		initializeItemSpawner1();
		initializeItemSpawner2();
		initializeItemSpawner3();
	}
	public static void initializeItemSpawner1() {
		itemSpawner1 = new ItemSpawner();

		//? if <= 1.20.5 {
		//itemSpawner1.addItem(Objects.requireNonNull(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(Enchantments.SILK_TOUCH, 1))), 249);
		//?} else {
		itemSpawner1.addItem(Objects.requireNonNull(ItemStackUtils.createEnchantedBook(Enchantments.SILK_TOUCH, 1)), 249);
		//?}

		//? if >= 1.20.5 {
		ItemStack patat = new ItemStack(Items.POISONOUS_POTATO);
		patat.set(DataComponents.CUSTOM_NAME, Component.nullToEmpty("§6§l§nThe Sacred Patat"));
		ItemStackUtils.addLoreToItemStack(patat,
				List.of(Component.nullToEmpty("§5§oEating this might help you. Or maybe not..."))
		);
		itemSpawner1.addItem(patat, 1);
		//?}
	}

	public static void initializeItemSpawner2() {
		itemSpawner2 = new ItemSpawner();

		//? if <= 1.20.5 {
		//itemSpawner2.addItem(Objects.requireNonNull(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(Enchantments.MENDING, 1))), 249);
		//?} else {
		itemSpawner2.addItem(Objects.requireNonNull(ItemStackUtils.createEnchantedBook(Enchantments.MENDING, 1)), 249);
		//?}

		//? if >= 1.20.5 {
		ItemStack patat = new ItemStack(Items.POISONOUS_POTATO);
		patat.set(DataComponents.CUSTOM_NAME, Component.nullToEmpty("§6§l§nThe Sacred Patat"));
		ItemStackUtils.addLoreToItemStack(patat,
				List.of(Component.nullToEmpty("§5§oEating this might help you. Or maybe not..."))
		);
		itemSpawner2.addItem(patat, 1);
		//?}
	}

	public static void initializeItemSpawner3() {
		itemSpawner3 = new ItemSpawner();

		//? if <= 1.20.5 {
		/*itemSpawner3.addItem(Objects.requireNonNull(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(Enchantments.FEATHER_FALLING, 1))), 10);
		itemSpawner3.addItem(Objects.requireNonNull(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(Enchantments.UNBREAKING, 1))), 10);
		itemSpawner3.addItem(Objects.requireNonNull(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(Enchantments.FORTUNE, 1))), 10);
		*///?} else {
		itemSpawner3.addItem(Objects.requireNonNull(ItemStackUtils.createEnchantedBook(Enchantments.FEATHER_FALLING, 1)), 10);
		itemSpawner3.addItem(Objects.requireNonNull(ItemStackUtils.createEnchantedBook(Enchantments.UNBREAKING, 1)), 10);
		itemSpawner3.addItem(Objects.requireNonNull(ItemStackUtils.createEnchantedBook(Enchantments.FORTUNE, 1)), 10);
		itemSpawner3.addItem(new ItemStack(Items.WIND_CHARGE, 10), 10);
		//?}
		itemSpawner3.addItem(new ItemStack(Items.IRON_INGOT, 16), 10);
		itemSpawner3.addItem(new ItemStack(Items.GOLDEN_CARROT, 8), 10);
		itemSpawner3.addItem(new ItemStack(Items.COW_SPAWN_EGG, 8), 10);
		itemSpawner3.addItem(new ItemStack(Items.SHEEP_SPAWN_EGG, 8), 10);
		itemSpawner3.addItem(new ItemStack(Items.PIG_SPAWN_EGG, 8), 10);

	}
}
