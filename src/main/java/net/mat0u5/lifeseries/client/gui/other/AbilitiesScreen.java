package net.mat0u5.lifeseries.client.gui.other;

import net.mat0u5.lifeseries.client.events.ClientKeybinds;
import net.mat0u5.lifeseries.client.features.abilities.AbilitiesClient;
import net.mat0u5.lifeseries.client.utils.ClientUtils;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Abilities;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Ability;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.AbilityIcon;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.InBetweenChoice;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.matlib.client.render.RenderUtils;
import net.mat0u5.matlib.client.utils.TextColors;
import net.mat0u5.matlib.utils.other.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

//? if >= 1.21.2 && <= 1.21.5
//import net.minecraft.client.renderer.rendertype.RenderType;
//? if >= 1.21.6
import net.minecraft.client.renderer.RenderPipelines;
//? if >= 1.21.9
import net.minecraft.client.input.KeyEvent;

public class AbilitiesScreen extends Screen {
	private static final float LIST_START = 0.4F;
	private static final float FEET_Y = 0.73F;
	private static final float PLAYER_SIZE = 0.285F;
	private static final int TITLE_Y = 10;
	private static final int LIST_TOP = 26;
	private static final int LIST_BOTTOM_MARGIN = 6;
	private static final int ROW_HEIGHT = 30;
	private static final int HOVER_HEIGHT = 28;
	private static final int TEXT_X = 30;
	private static final int TOOLTIP_WIDTH = 200;
	private static final int HEARTS_PER_ROW = 10;
	private static final int BACKGROUND_TOP = TextColors.argb(190, 15, 15, 15);
	private static final int BACKGROUND_BOTTOM = TextColors.argb(210, 15, 15, 15);
	private static final int LIST_BACKGROUND = TextColors.argb(55, 0, 0, 0);
	private static final int HOVER_COLOR = TextColors.argb(50, 255, 255, 255);
	private static final int SCROLLBAR_COLOR = TextColors.argb(35, 255, 255, 255);
	private static final int SCROLLBAR_THUMB_COLOR = TextColors.argb(135, 255, 255, 255);

	private static final int PLAYER_CLIP = 5; // Below the feet
	//? if <= 1.20
	//private static final Identifier ICONS = IdentifierHelper.vanilla("textures/gui/icons.png");

	private final List<AbilityEntry> rows = new ArrayList<>();
	@Nullable
	private List<AbilitiesClient.ActiveAbility> shownAbilities;
	private double bonusHearts;
	private int listX;
	private double scroll;

	public AbilitiesScreen() {
		super(Component.literal("Powerups"));
	}

	@Override
	protected void init() {
		listX = Math.round(width * LIST_START);
		shownAbilities = null;
	}

	private void updateRows() {
		List<AbilitiesClient.ActiveAbility> abilities = AbilitiesClient.getActiveAbilities();
		if (abilities == shownAbilities) return;
		shownAbilities = abilities;
		rows.clear();
		bonusHearts = 0;
		for (AbilitiesClient.ActiveAbility ability : abilities) {
			rows.add(new AbilityEntry(ability.ability(), ability.hearts()));
			bonusHearts += ability.hearts();
		}
	}

	private int listBottom() {
		return height - LIST_BOTTOM_MARGIN;
	}

	private int maxScroll() {
		return Math.max(0, rows.size() * ROW_HEIGHT - (ROW_HEIGHT - HOVER_HEIGHT) - (listBottom() - LIST_TOP));
	}

	//~ renames_26_1_volatile
	@Override
	//? if <= 1.20 {
	//public void extractBackground(GuiGraphicsExtractor graphics) {}
	//?} else {
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {}
	//?}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
	//~ !renames_26_1_volatile
		updateRows();
		scroll = Mth.clamp(scroll, 0, maxScroll());

		//? if <= 1.21.5 {
		/*graphics.pose().pushPose();
		graphics.pose().translate(0, 0, -500);
		*///?}
		graphics.fillGradient(0, 0, width, height, BACKGROUND_TOP, BACKGROUND_BOTTOM);
		graphics.fill(listX, 0, width, height, LIST_BACKGROUND);
		//? if <= 1.21.5
		//graphics.pose().popPose();

		LocalPlayer player = minecraft.player;
		if (player != null) {
			renderPlayer(graphics, player, mouseX, mouseY);
		}

		RenderUtils.text(title, listX + 8, TITLE_Y).colored(TextColors.PURE_WHITE).withShadow().render(graphics, font);
		int bottom = listBottom();
		AbilityEntry hovered = null;
		graphics.enableScissor(listX, LIST_TOP, width, bottom);
		for (int i = 0; i < rows.size(); i++) {
			int y = LIST_TOP + i * ROW_HEIGHT - (int) scroll;
			if (y + HOVER_HEIGHT <= LIST_TOP || y >= bottom) continue;
			boolean isHovered = mouseX >= listX + 4 && mouseX < width - 4 && mouseY >= Math.max(y, LIST_TOP) && mouseY < Math.min(y + HOVER_HEIGHT, bottom);
			if (isHovered) hovered = rows.get(i);
			renderRow(graphics, rows.get(i), y, isHovered);
		}
		graphics.disableScissor();
		renderScrollbar(graphics, bottom);

		if (hovered != null) {
			//? if <= 1.21.5 {
			//graphics.renderTooltip(font, hovered.tooltip, DefaultTooltipPositioner.INSTANCE, mouseX, mouseY);
			//?} else {
			graphics.setTooltipForNextFrame(font, hovered.tooltip, DefaultTooltipPositioner.INSTANCE, mouseX, mouseY, false);
			//?}
		}
	}

	private void renderPlayer(GuiGraphicsExtractor graphics, LocalPlayer player, int mouseX, int mouseY) {
		int centerX = listX / 2;
		int feetY = Math.round(height * FEET_Y);
		int size = Math.round(Math.min(height * PLAYER_SIZE, listX * 0.55F));
		int centerY = Math.round(feetY - size * player.getBbHeight() / player.getScale() / 2);
		graphics.enableScissor(0, 0, listX, feetY + PLAYER_CLIP);
		//? if <= 1.20 {
		//InventoryScreen.extractEntityInInventoryFollowsMouse(graphics, centerX, feetY, size, centerX - mouseX, centerY - mouseY, player);
		//?} else {
		InventoryScreen.extractEntityInInventoryFollowsMouse(graphics, centerX - size, centerY - size * 6 / 5, centerX + size, centerY + size * 6 / 5, size, 0, mouseX, mouseY, player);
		//?}
		graphics.disableScissor();

		boolean hardcore = player.level().getLevelData().isHardcore();
		int heartsY = feetY + 7;
		int hearts = Mth.ceil(player.getMaxHealth() / 2);
		int health = Mth.ceil(player.getHealth());
		int left = centerX - (Math.min(hearts, HEARTS_PER_ROW) * 8 + 1) / 2;
		for (int i = 0; i < hearts; i++) {
			int x = left + i % HEARTS_PER_ROW * 8;
			int y = heartsY + i / HEARTS_PER_ROW * 10;
			heart(graphics, "container", hardcore, x, y);
			if (health >= i * 2 + 2) heart(graphics, "full", hardcore, x, y);
			else if (health == i * 2 + 1) heart(graphics, "half", hardcore, x, y);
		}
		if (bonusHearts == 0) return;

		Component bonus = Component.empty()
				.append(Component.literal(InBetweenChoice.formattedHearts(bonusHearts)).withStyle(ChatFormatting.WHITE))
				.append(Component.literal(" from powerups").withStyle(ChatFormatting.GRAY));
		int lineX = centerX - (12 + font.width(bonus)) / 2;
		int lineY = heartsY + (Math.max(hearts, 1) - 1) / HEARTS_PER_ROW * 10 + 14;
		heart(graphics, "container", hardcore, lineX, lineY - 1);
		heart(graphics, "full", hardcore, lineX, lineY - 1);
		RenderUtils.text(bonus, lineX + 12, lineY).colored(TextColors.PURE_WHITE).withShadow().render(graphics, font);
	}

	private void renderRow(GuiGraphicsExtractor graphics, AbilityEntry row, int y, boolean hovered) {
		if (hovered) {
			graphics.fill(listX + 4, y, width - 4, y + HOVER_HEIGHT, HOVER_COLOR);
		}
		if (row.icon.isTexture()) {
			RenderUtils.texture(row.icon.texture(), listX + 7, y + 5, 18, 18).render(graphics);
		}
		else {
			//~ renames_26_1_volatile
			graphics.item(row.item, listX + 8, y + 6);
			//~ !renames_26_1_volatile
		}
		RenderUtils.text(row.name, listX + TEXT_X, y + 4).colored(TextColors.PURE_WHITE).withShadow().render(graphics, font);
		RenderUtils.text(row.description, listX + TEXT_X, y + 15).colored(TextColors.PURE_WHITE).withShadow().render(graphics, font);
		if (row.heartTexture != null) {
			RenderUtils.texture(row.heartTexture.texture(), width - 8 - row.heartTexture.width(), y + 6, row.heartTexture.width(), row.heartTexture.height()).render(graphics);
		}
		else {
			RenderUtils.text(row.fallbackHeartText, width - 8, y + 9).anchorRight().colored(TextColors.PURE_WHITE).withShadow().render(graphics, font);
		}
	}

	private void renderScrollbar(GuiGraphicsExtractor graphics, int bottom) {
		int maxScroll = maxScroll();
		if (maxScroll <= 0) return;
		int view = bottom - LIST_TOP;
		int thumb = Math.max(10, view * view / (view + maxScroll));
		int thumbY = LIST_TOP + (int) Math.round((view - thumb) * scroll / maxScroll);
		graphics.fill(width - 3, LIST_TOP, width - 1, bottom, SCROLLBAR_COLOR);
		graphics.fill(width - 3, thumbY, width - 1, thumbY + thumb, SCROLLBAR_THUMB_COLOR);
	}

	// type = container/half/full
	private static void heart(GuiGraphicsExtractor graphics, String type, boolean hardcore, int x, int y) {
		String path = "hud/heart/" + (!hardcore ? type : type.equals("container") ? "container_hardcore" : "hardcore_" + type);
		Identifier custom = ClientUtils.customHeart(path);
		Identifier sprite = custom != null ? custom : IdentifierHelper.vanilla(path);
		//? if <= 1.20 {
		/*if (custom != null) RenderUtils.texture(custom, x, y, 9, 9).render(graphics);
		else RenderUtils.texture(ICONS, x, y, 9, 9).uv(type.equals("container") ? 16 : type.equals("full") ? 52 : 61, hardcore ? 45 : 0).textureSize(256, 256).render(graphics);
		*///?} else if <= 1.21 {
		//graphics.blitSprite(sprite, x, y, 9, 9);
		//?} else if <= 1.21.5 {
		//graphics.blitSprite(RenderType::guiTextured, sprite, x, y, 9, 9);
		//?} else {
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, 9, 9);
		//?}
	}

	@Override
	//? if <= 1.20 {
	//public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
	//?} else {
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
	//?}
		scroll = Mth.clamp(scroll - scrollY * ROW_HEIGHT / 2, 0, maxScroll());
		return true;
	}


	// Make the open keybind be able to close it
	@Override
	//? if <= 1.21.6 {
	/*public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (!ClientKeybinds.openAbilityScreen.matches(keyCode, scanCode)) return super.keyPressed(keyCode, scanCode, modifiers);
	*///?} else {
	public boolean keyPressed(KeyEvent event) {
		if (!ClientKeybinds.openAbilityScreen.matches(event)) return super.keyPressed(event);
	//?}
		onClose();
		return true;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private class AbilityEntry {
		private final AbilityIcon icon;
		private final ItemStack item;
		private final Component name;
		private final Component description;
		@Nullable
		private final AbilitiesClient.HeartTexture heartTexture;
		private final Component fallbackHeartText;
		private final List<FormattedCharSequence> tooltip = new ArrayList<>();

		private AbilityEntry(Abilities ability, double hearts) {
			icon = ability.icon();
			item = icon.itemStack();
			var abilityType = ability.abilityType();
			ChatFormatting color;
			if (abilityType == Ability.AbilityType.RED) {
				color = ChatFormatting.RED;
			}
			else if (abilityType == Ability.AbilityType.ACTIVE) {
				color = ChatFormatting.YELLOW;
			}
			else {
				color = ChatFormatting.GREEN;
			}
			name = Component.literal(ability.title()).withStyle(color);
			heartTexture = AbilitiesClient.findHeartTexture(hearts);
			fallbackHeartText = AbilitiesClient.smallCaps(InBetweenChoice.heartsText(hearts).getString()).withStyle(ChatFormatting.RED);
			int texWidth = heartTexture != null ? heartTexture.width() : font.width(fallbackHeartText);
			description = Component.literal(clip(ability.description().trim(), width - 14 - texWidth - listX - TEXT_X)).withStyle(ChatFormatting.GRAY);

			tooltip.addAll(font.split(name, TOOLTIP_WIDTH));
			tooltip.add(FormattedCharSequence.EMPTY);
			tooltip.addAll(font.split(Component.literal(ability.description().trim()), TOOLTIP_WIDTH));
			if (!ability.additionalDescription().isBlank()) {
				tooltip.add(FormattedCharSequence.EMPTY);
				String additionalDescription = ability.additionalDescription().trim();
				if (ability == Abilities.SOULMATE) {
					additionalDescription = TextUtils.formatString(additionalDescription, ClientKeybinds.triggerAbility.getTranslatedKeyMessage().getString());
				}
				tooltip.addAll(font.split(Component.literal(additionalDescription).withStyle(ChatFormatting.GRAY), TOOLTIP_WIDTH));
			}
		}

		private String clip(String text, int maxWidth) {
			if (font.width(text) <= maxWidth) return text;
			return font.plainSubstrByWidth(text, Math.max(0, maxWidth - font.width("..."))).trim() + "...";
		}
	}
}
