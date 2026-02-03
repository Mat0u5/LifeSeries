package net.mat0u5.lifeseries.gui.config.entries.extra;

import net.mat0u5.lifeseries.gui.config.entries.StringListPopupConfigEntry;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import java.util.ArrayList;
import java.util.List;
//? if <= 1.21
//import com.mojang.blaze3d.systems.RenderSystem;

//? if >= 1.21.2 {
import net.minecraft.util.ARGB;
//?}
//? if >= 1.21.2 && <= 1.21.5 {
/*import net.minecraft.client.renderer.RenderType;
*///?}

//? if <= 1.21.5 {
/*import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
*///?}
//? if >= 1.21.6 {
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderPipelines;
//?}

//? if <= 1.21.9 {
/*import net.minecraft.resources.ResourceLocation;
 *///?} else {
import net.minecraft.resources.Identifier;
//?}
//? if <= 1.20.3 {
/*public class EffectListConfigEntry extends StringListPopupConfigEntry<MobEffect> {
*///?} else {
public class EffectListConfigEntry extends StringListPopupConfigEntry<Holder<MobEffect>> {
//?}
     //? if <= 1.20 {
    /*private static final ResourceLocation EFFECT_BACKGROUND_TEXTURE = IdentifierHelper.vanilla("textures/gui/container/inventory.png");
    *///?} else if <= 1.21.9 {
    /*private static final ResourceLocation EFFECT_BACKGROUND_TEXTURE = IdentifierHelper.vanilla("hud/effect_background");
    *///?} else {
    private static final Identifier EFFECT_BACKGROUND_TEXTURE = IdentifierHelper.vanilla("hud/effect_background");
    //?}

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

        //? if <= 1.20.3 {
        /*List<MobEffect> newList = new ArrayList<>();
        *///?} else {
        List<Holder<MobEffect>> newList = new ArrayList<>();
        //?}
        boolean errors = false;

        Registry<MobEffect> effectsRegistry = Minecraft.getInstance().level.registryAccess()
        //? if <=1.21 {
        /*.registryOrThrow(ResourceKey.createRegistryKey(IdentifierHelper.vanilla("mob_effect")));
        *///?} else
        .lookupOrThrow(ResourceKey.createRegistryKey(IdentifierHelper.vanilla("mob_effect")));

        for (String potionId : items) {
            if (potionId.isEmpty()) continue;
            if (!potionId.contains(":")) potionId = "minecraft:" + potionId;

            try {
                var id = IdentifierHelper.parse(potionId);
                //? if <= 1.21 {
                /*MobEffect enchantment = effectsRegistry.get(id);
                *///?} else {
                MobEffect enchantment = effectsRegistry.getValue(id);
                //?}

                if (enchantment != null) {
                    //? if <= 1.20.3 {
                    /*newList.add(enchantment);
                    *///?} else {
                    newList.add(effectsRegistry.wrapAsHolder(enchantment));
                    //?}
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
    //? if <= 1.20.3 {
    /*protected void renderListEntry(GuiGraphics context, MobEffect effectType, int x, int y, int mouseX, int mouseY, float tickDelta) {
    *///?} else {
    protected void renderListEntry(GuiGraphics context, Holder<MobEffect> effectType, int x, int y, int mouseX, int mouseY, float tickDelta) {
    //?}
        //? if <= 1.21 {
        /*MobEffectTextureManager statusEffectSpriteManager = Minecraft.getInstance().getMobEffectTextures();
        RenderSystem.enableBlend();

        //? if <= 1.20 {
        /^context.blit(EFFECT_BACKGROUND_TEXTURE, x, y, 141, 166, 24, 24);
        ^///?} else {
        context.blitSprite(EFFECT_BACKGROUND_TEXTURE, x, y, 24, 24);
        //?}
        TextureAtlasSprite sprite = statusEffectSpriteManager.get(effectType);
        context.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        context.blit(x + 3, y + 3, 0, 18, 18, sprite);
        context.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        RenderSystem.disableBlend();
        *///?} else if <= 1.21.5 {
        /*MobEffectTextureManager statusEffectSpriteManager = Minecraft.getInstance().getMobEffectTextures();
        context.blitSprite(RenderType::guiTextured, EFFECT_BACKGROUND_TEXTURE, x, y, 24, 24);
        TextureAtlasSprite sprite = statusEffectSpriteManager.get(effectType);
        context.blitSprite(RenderType::guiTextured, sprite, x + 3, y + 3, 18, 18, ARGB.white(1.0f));
        *///?} else {
        context.blitSprite(RenderPipelines.GUI_TEXTURED, EFFECT_BACKGROUND_TEXTURE, x, y, 24, 24);
        context.blitSprite(RenderPipelines.GUI_TEXTURED, Gui.getMobEffectSprite(effectType), x + 3, y + 3, 18, 18, ARGB.white(1.0f));
        //?}
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
