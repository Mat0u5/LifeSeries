package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability;

import com.mojang.math.Transformation;
import net.mat0u5.lifeseries.mixin.DisplayAccessor;
import net.mat0u5.lifeseries.mixin.ItemDisplayAccessor;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.InBetweenInstance;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.InBetweenManager;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.trivia.InBetweenTriviaManager;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.matlib.utils.other.TextUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

//? if <= 26.1 {
//import net.minecraft.world.entity.EntityType;
//?} else {
import net.minecraft.world.entity.EntityTypes;
//?}

public class InBetweenChoiceManager {
	public static final String ENTITY_TAG = "lifeseries_in_between";
	private static final double CHOICE_TRIGGER_RADIUS = 1.3;
	private static final int CHOICE_EXIT_DELAY = 82; // Cloud transition takes ~4 seconds
	private static final int CHOICE_DECAY = 4;
	public static final double LABEL_HEIGHT = 2.35; // Badge center above the ring
	public static final double ICON_OFFSET = 1.2; // Icon center above the badge center
	public static final float ICON_SIZE = 0.6F;
	private final Set<UUID> labelIds = new HashSet<>();
	private final List<String> labelEntries = new ArrayList<>(); // Drawn client-side
	private int choosing = -1;
	private int choiceTicks = 0;
	public final Map<Integer, Vec3> choicePositions = new TreeMap<>();
	private final InBetweenInstance inBetween;
	public final List<InBetweenChoice> choices;

	public InBetweenChoiceManager(InBetweenInstance inBetween, List<InBetweenChoice> choices) {
		this.inBetween = inBetween;
		this.choices = new ArrayList<>(choices);
	}

	public void spawnChoiceLabels() {
		labelEntries.clear();
		List<Vec3> choiceCenters = getChoiceCenters();
		while (choices.size() > choiceCenters.size()) choices.remove(choices.size() - 1);
		while (choices.size() < choiceCenters.size()) choices.add(InBetweenChoice.empty());
		for (int i = 0; i < choices.size(); i++) {
			spawnChoiceLabel(choices.get(i), choiceCenters.get(i));
		}
	}
	public void tick(ServerPlayer player, int age) {
		if (age % 100 == 0) removeAnyTextLabels();
		checkChoices(player);
	}

	public List<Vec3> getChoiceCenters() {
		return new ArrayList<>(choicePositions.values());
	}

	private static boolean hasTag(Entity entity) {
		//? if <= 1.21.11 {
		//return entity.getTags().contains(ENTITY_TAG);
		//?} else {
		return entity.entityTags().contains(ENTITY_TAG);
		//?}
	}

	public void removeAnyTextLabels() {
		for (Entity entity : inBetween.level.getEntitiesOfClass(Entity.class, inBetween.inBetweenBuilder.getRegion(), InBetweenChoiceManager::hasTag)) {
			if (!labelIds.contains(entity.getUUID())) entity.discard();
		}
	}

	private void spawnChoiceLabel(InBetweenChoice choice, Vec3 ringCenter) {
		if (choice.isEmpty()) return;
		Vec3 anchor = ringCenter.add(0, LABEL_HEIGHT, 0);
		AbilityIcon icon = choice.icon();
		String texture = icon != null && icon.isTexture() ? String.valueOf(icon.texture()) : "-";
		labelEntries.add(String.format(Locale.ROOT, "%.4f;%.4f;%.4f;%s;%s;%s;%s",
				anchor.x, anchor.y, anchor.z, texture, choice.getHearts(),
				TextUtils.textToLegacyString(choice.title()).replace(';', ','), TextUtils.textToLegacyString(choice.subtitleFallback())));

		if (icon != null && !icon.isTexture() && !icon.itemStack().isEmpty()) {
			Display.ItemDisplay item = new Display.ItemDisplay(EntityTypes.ITEM_DISPLAY, inBetween.level);
			((ItemDisplayAccessor) item).ls$setItemStack(icon.itemStack());
			setupDisplay(item, ICON_SIZE);
			addDisplay(item, anchor.x, anchor.y + ICON_OFFSET, anchor.z);
		}
	}

	public void sendLabelsToClient(ServerPlayer player) {
		SimplePackets.IN_BETWEEN_CHOICE_LABELS.sendToClient(labelEntries, player);
	}

	public static void sendClientClearLabels(ServerPlayer player) {
		SimplePackets.IN_BETWEEN_CHOICE_LABELS.sendToClient(List.of(), player);
	}

	private void setupDisplay(Display display, float scale) {
		DisplayAccessor accessor = (DisplayAccessor) display;
		accessor.ls$setBillboardConstraints(Display.BillboardConstraints.CENTER);
		accessor.ls$setBrightnessOverride(new Brightness(15, 15));
		accessor.ls$setTransformation(new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(scale, scale, scale), new Quaternionf()));
	}

	private void addDisplay(Display display, double x, double y, double z) {
		display.setPos(x, y, z);
		display.addTag(ENTITY_TAG);
		inBetween.level.addFreshEntity(display);
		labelIds.add(display.getUUID());
	}

	public void checkChoices(ServerPlayer player) {
		int inside = InBetweenTriviaManager.blocksExit(inBetween) ? -1 : getChoiceInside(player);
		if (inside < 0) {
			if (choosing >= 0) InBetweenManager.onChoiceLeft(player);
			choosing = -1;
			choiceTicks = Math.max(0, choiceTicks - CHOICE_DECAY);
			return;
		}
		if (choosing < 0) InBetweenManager.onChoiceEntered(player);
		choosing = inside;
		if (++choiceTicks >= CHOICE_EXIT_DELAY) {
			inBetween.finished = true;
			InBetweenManager.exitInBetween(player, choices.get(inside));
		}
	}

	private int getChoiceInside(ServerPlayer player) {
		if (player.isSpectator()) return -1;
		List<Vec3> centers = getChoiceCenters();
		for (int i = 0; i < choices.size() && i < centers.size(); i++) {
			Vec3 center = centers.get(i);
			double dx = player.getX() - center.x;
			double dz = player.getZ() - center.z;
			double dy = player.getY() - center.y;
			if (dx * dx + dz * dz <= CHOICE_TRIGGER_RADIUS * CHOICE_TRIGGER_RADIUS && dy > -0.75 && dy < 2) return i;
		}
		return -1;
	}

	public void destroy() {
		labelIds.clear();
		if (choosing < 0 || inBetween.finished) return;
		ServerPlayer player = PlayerUtils.getPlayer(inBetween.playerUUID);
		if (player != null) InBetweenManager.onChoiceLeft(player);
	}
}
