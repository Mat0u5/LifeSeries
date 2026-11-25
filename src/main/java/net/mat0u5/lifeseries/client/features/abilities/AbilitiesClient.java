package net.mat0u5.lifeseries.client.features.abilities;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.client.events.ClientKeybinds;
import net.mat0u5.lifeseries.client.gui.other.AbilitiesScreen;
import net.mat0u5.lifeseries.client.utils.ClientTaskScheduler;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Abilities;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.matlib.client.render.RenderUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

//? if >= 1.21.9
import net.minecraft.network.chat.FontDescription;
import net.minecraft.sounds.SoundEvents;

public class AbilitiesClient {
	//? if <= 1.21.6 {
	//private static final Identifier SMALL_CAPS = IdentifierHelper.lifeseries("small_caps");
	//?} else {
	private static final FontDescription SMALL_CAPS = new FontDescription.Resource(IdentifierHelper.lifeseries("small_caps"));
	//?}

	public record ActiveAbility(Abilities ability, double hearts) {}
	public record HeartTexture(Identifier texture, int width, int height) {}

	private static List<ActiveAbility> activeAbilities = new ArrayList<>();
	public static void onActiveAbilitiesPayload(List<String> payload) {
		List<ActiveAbility> newAbilities = new ArrayList<>();
		for (String str : payload) {
			try {
				String[] split = str.split(";", 2);
				newAbilities.add(new ActiveAbility(Abilities.valueOf(split[0]), split.length > 1 ? Double.parseDouble(split[1]) : 0));
			}catch(Exception ignored) {}
		}
		activeAbilities = newAbilities;
	}

	public static void onAbilityGainPacket(String abilityStr) {
		try {
			Abilities ability = Abilities.valueOf(abilityStr);
			onAbilityGain(ability);
		}catch(Exception ignored) {}
	}

	public static void onAbilityGain(Abilities ability) {
		ClientTaskScheduler.schedulePriorityTask(100, () -> sendAbilityInfoText(ability));
	}

	public static void sendAbilityInfoText(Abilities ability) {
		Minecraft client = Minecraft.getInstance();
		var player = client.player;
		if (player == null) return;

		MutableComponent infoText = Component.empty();
		if (ability == Abilities.RESISTANCE || ability == Abilities.FIRE_RESISTANCE || ability == Abilities.KNOCKBACK_RESISTANCE || ability == Abilities.PROJECTILE_PROTECTION || ability == Abilities.BLAST_RESISTANCE) {
			infoText.append("§7You have gained ").append(smallCaps(ability.title()).withStyle(ability.choiceColor())).append("§7!");
		}
		else if (ability == Abilities.SMALLER) {
			infoText.append("§7You are now ").append(smallCaps(ability.title()).withStyle(ability.choiceColor())).append("§7!");
		}
		else if (ability == Abilities.DASH) {
			infoText.append("§7You have gained the ability to ").append(smallCaps(ability.title()).withStyle(ability.choiceColor())).append("§7!");
		}
		else {
			infoText.append("§7You have gained the ").append(smallCaps(ability.title()).withStyle(ability.choiceColor())).append(" §7ability!");
		}

		infoText.append(" §7Press ").append(ClientKeybinds.openAbilityScreen.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD)).append("§7 for more details.");

		//? if <= 1.21.11 {
		/*player.displayClientMessage(Component.empty(), false);
		player.displayClientMessage(infoText, false);
		player.displayClientMessage(Component.empty(), false);
		*///?} else {
		player.sendSystemMessage(Component.empty());
		player.sendSystemMessage(infoText);
		player.sendSystemMessage(Component.empty());
		//?}
		player.playSound(SoundEvents.NOTE_BLOCK_PLING.value());
	}

	public static void onPressOpenScreenKey() {
		if (!LifeSeries.isSeason(Seasons.FINAL_LIFE) || Minecraft.getInstance().player == null) return;
		RenderUtils.setScreen(new AbilitiesScreen());
	}

	public static boolean isActive(Abilities ability) {
		for (ActiveAbility activeAbility : activeAbilities) {
			if (activeAbility.ability() == ability) return true;
		}
		return false;
	}

	public static List<ActiveAbility> getActiveAbilities() {
		return activeAbilities;
	}

	public static void reset() {
		activeAbilities = new ArrayList<>();
	}

	// textures/gui/menu/heart_texture/hearts_<hearts>.png, null when theres none
	@Nullable
	public static HeartTexture findHeartTexture(double hearts) {
		if (hearts < 0 || hearts % 1 != 0) return null;
		Identifier texture = IdentifierHelper.lifeseries("textures/gui/menu/heart_texture/hearts_" + (int) hearts + ".png");
		Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(texture);
		if (resource.isEmpty()) return null;
		try (InputStream stream = resource.get().open()) {
			ByteBuffer header = ByteBuffer.wrap(stream.readNBytes(24));
			return new HeartTexture(texture, header.getInt(16), header.getInt(20)); // Get the width and height
		} catch (Exception e) {
			return null;
		}
	}

	public static MutableComponent smallCaps(String text) {
		return Component.literal(text.toLowerCase(Locale.ROOT)).withStyle(style -> style.withFont(SMALL_CAPS));
	}

	public static MutableComponent smallCaps(MutableComponent text) {
		return text.withStyle(style -> style.withFont(SMALL_CAPS));
	}
}
