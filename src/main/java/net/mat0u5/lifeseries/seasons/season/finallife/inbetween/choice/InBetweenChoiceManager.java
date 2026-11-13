package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.choice;

import com.mojang.math.Transformation;
import net.mat0u5.lifeseries.mixin.DisplayAccessor;
import net.mat0u5.lifeseries.mixin.ItemDisplayAccessor;
import net.mat0u5.lifeseries.mixin.TextDisplayAccessor;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.InBetweenInstance;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.InBetweenManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
	private final Set<UUID> labelIds = new HashSet<>();
	public final Map<Integer, Vec3> choicePositions = new TreeMap<>();
	private final InBetweenInstance inBetween;
	public final List<InBetweenChoice> choices;

	public InBetweenChoiceManager(ServerPlayer player, InBetweenInstance inBetween) {
		this.inBetween = inBetween;
		this.choices = createChoices(player);
	}

	public static List<InBetweenChoice> createChoices(ServerPlayer player) {
		//TODO
		return List.of(
				new InBetweenChoice("knockback_resistance", Component.literal("KNOCKBACK RESISTANCE"), Component.literal("+3 HEARTS"), new ItemStack(Items.IRON_CHESTPLATE)),
				new InBetweenChoice("feather_falling", Component.literal("FEATHER FALLING"), Component.literal("+1 HEART"), new ItemStack(Items.FEATHER))
		);
	}

	public void spawnChoiceLabels() {
		List<Vec3> choiceCenters = getChoiceCenters();
		for (int i = 0; i < choices.size() && i < choiceCenters.size(); i++) {
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
		double x = ringCenter.x;
		double z = ringCenter.z;
		double y = ringCenter.y + 2.2;

		Display.TextDisplay subtitle = createTextDisplay(
				Component.literal(" ").append(choice.subtitle()).append(" ").withStyle(ChatFormatting.WHITE),
				0xE0A3212B, 0.6F);
		addDisplay(subtitle, x, y, z);

		Display.TextDisplay title = createTextDisplay(choice.title().copy().withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD), 0, 1.0F);
		addDisplay(title, x, y + 0.25, z);

		if (!choice.icon().isEmpty()) {
			Display.ItemDisplay icon = new Display.ItemDisplay(EntityTypes.ITEM_DISPLAY, inBetween.level);
			((ItemDisplayAccessor) icon).ls$setItemStack(choice.icon().copy());
			setupDisplay(icon, 0.55F);
			addDisplay(icon, x, y + 0.95, z);
		}
	}

	private Display.TextDisplay createTextDisplay(Component text, int backgroundColor, float scale) {
		Display.TextDisplay display = new Display.TextDisplay(EntityTypes.TEXT_DISPLAY, inBetween.level);
		TextDisplayAccessor accessor = (TextDisplayAccessor) display;
		accessor.ls$setText(text);
		accessor.ls$setBackgroundColor(backgroundColor);
		accessor.ls$setLineWidth(400);
		accessor.ls$setFlags((byte) 1); // Shadow
		setupDisplay(display, scale);
		return display;
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
		//TODO do not make it instant.
		if (player.isSpectator()) return;
		List<Vec3> centers = getChoiceCenters();
		for (int i = 0; i < choices.size() && i < centers.size(); i++) {
			Vec3 center = centers.get(i);
			double dx = player.getX() - center.x;
			double dz = player.getZ() - center.z;
			double dy = player.getY() - center.y;
			if (dx * dx + dz * dz <= CHOICE_TRIGGER_RADIUS * CHOICE_TRIGGER_RADIUS && dy > -0.75 && dy < 2) {
				inBetween.finished = true;
				InBetweenManager.onMadeChoice(player, choices.get(i));
				return;
			}
		}
	}

	public void destroy() {
		labelIds.clear();
	}
}
