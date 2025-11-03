package net.mat0u5.lifeseries.gui.config.entries.extra;

import net.mat0u5.lifeseries.gui.config.entries.StringListPopupConfigEntry;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import java.util.ArrayList;
import java.util.List;
//? if <= 1.21
import com.mojang.blaze3d.systems.RenderSystem;

//? if >= 1.21.2 {
/*import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ARGB;
*///?}

public class EffectListConfigEntry extends StringListPopupConfigEntry<Holder<MobEffect>> {
    private static final ResourceLocation EFFECT_BACKGROUND_TEXTURE = ResourceLocation.withDefaultNamespace("hud/effect_background");

    public EffectListConfigEntry(String fieldName, String displayName, String description, String value, String defaultValue) {
        super(fieldName, displayName, description, value, defaultValue, 5, 24, 2);
        reloadEntriesRaw(value);
    }

    @Override
    protected void reloadEntries(List<String> items) {
        if (Minecraft.getInstance().level == null) return;
        if (entries != null) {
            entries.clear();
        }

        List<Holder<MobEffect>> newList = new ArrayList<>();
        boolean errors = false;

        Registry<MobEffect> effectsRegistry = Minecraft.getInstance().level.registryAccess()
        //? if <=1.21 {
        .registryOrThrow(ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("minecraft", "mob_effect")));
        //?} else
        /*.lookupOrThrow(ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("minecraft", "mob_effect")));*/

        for (String potionId : items) {
            if (potionId.isEmpty()) continue;
            if (!potionId.contains(":")) potionId = "minecraft:" + potionId;

            try {
                ResourceLocation id = ResourceLocation.parse(potionId);
                //? if <= 1.21 {
                MobEffect enchantment = effectsRegistry.get(id);
                //?} else {
                /*MobEffect enchantment = effectsRegistry.getValue(id);
                *///?}

                if (enchantment != null) {
                    newList.add(effectsRegistry.wrapAsHolder(enchantment));
                } else {
                    setError(TextUtils.formatString("Invalid effect: '{}'", potionId));
                    errors = true;
                }
            } catch (Exception e) {
                setError(TextUtils.formatString("Error parsing effect ID: '{}'", potionId));
                errors = true;
            }
        }

        entries = newList;
        if (!errors) {
            clearError();
        }
    }

    @Override
    protected void renderListEntry(GuiGraphics context, Holder<MobEffect> effectType, int x, int y, int mouseX, int mouseY, float tickDelta) {
        //? if <= 1.21 {
        MobEffectTextureManager statusEffectSpriteManager = Minecraft.getInstance().getMobEffectTextures();
        RenderSystem.enableBlend();

        context.blitSprite(EFFECT_BACKGROUND_TEXTURE, x, y, 24, 24);
        TextureAtlasSprite sprite = statusEffectSpriteManager.get(effectType);
        context.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        context.blit(x + 3, y + 3, 0, 18, 18, sprite);
        context.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        RenderSystem.disableBlend();
        //?} else if <= 1.21.5 {
        /*MobEffectTextureManager statusEffectSpriteManager = Minecraft.getInstance().getMobEffectTextures();
        context.blitSprite(RenderType::guiTextured, EFFECT_BACKGROUND_TEXTURE, x, y, 24, 24);
        TextureAtlasSprite sprite = statusEffectSpriteManager.get(effectType);
        context.blitSprite(RenderType::guiTextured, sprite, x + 3, y + 3, 18, 18, ARGB.white(1.0f));
        *///?} else {
        /*context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, EFFECT_BACKGROUND_TEXTURE, x, y, 24, 24);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, InGameHud.getEffectTexture(effectType), x + 3, y + 3, 18, 18, ColorHelper.getWhite(1.0f));
        *///?}
    }

    @Override
    public boolean hasCustomErrors() {
        return true;
    }

    @Override
    public ConfigTypes getValueType() {
        return ConfigTypes.EFFECT_LIST;
    }
}
