package net.mat0u5.lifeseries.client.gui.config.entries.extra;

import net.mat0u5.lifeseries.client.gui.config.entries.main.StringConfigEntry;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;

//? if >= 1.21.9
import net.minecraft.client.input.*;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class ModifiableSoundConfigEntry extends StringConfigEntry {
    private final Button playSoundButton;
    private SoundInstance soundInstance;

    public ModifiableSoundConfigEntry(String fieldName, String displayName, String value, String defaultValue) {
        super(fieldName, displayName, "", value, defaultValue, 230, 14);
        playSoundButton = Button.builder(Component.nullToEmpty("▷"), this::onPlayClicked)
                .bounds(0, 0, 14, 14)
                .build();
    }

    public void onPlayClicked(Button button) {
        if (soundInstance == null) return;
        Minecraft.getInstance().getSoundManager().play(soundInstance);
    }

    @Override
    public void renderEntry(GuiGraphicsExtractor context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        playSoundButton.setX(textField.getX()+textField.getWidth()+4);
        playSoundButton.setY(textField.getY());
        //~ renames_26_1_volatile
        playSoundButton.extractRenderState(context, mouseX, mouseY, tickDelta);
        //~ !renames_26_1_volatile
        super.renderEntry(context, x, y, width, height, mouseX, mouseY, hovered, tickDelta);
    }

    @Override
    protected int maxFieldWidth(int labelEndX, int fieldEndX) {
        return super.maxFieldWidth(labelEndX, fieldEndX) - 19;
    }

    @Override
    protected int getTextFieldPosX(int x, int entryWidth) {
        return super.getTextFieldPosX(x, entryWidth) - 19;
    }

    @Override
    protected void onTextChanged(String text) {
        super.onTextChanged(text);
        if (text.trim().isEmpty()) {
            soundInstance = null;
            clearError();
            clearWarn();
            return;
        }
        try {
            Identifier soundId = IdentifierHelper.parse(getValue());
            clearError();
            SoundInstance instance = SimpleSoundInstance.forUI(SoundEvent.createVariableRangeEvent(soundId), 1);
            //~ if >= 26.3 '.resolve(' -> '.getOrResolve(' {
            var resolved = instance.resolve(Minecraft.getInstance().getSoundManager());
            //~}
            if (resolved == null) {
                setWarn("Unknown Sound Event");
                soundInstance = null;
            }
            else {
                clearWarn();
                soundInstance = instance;
            }
        }catch(Exception e) {
            soundInstance = null;
            setError("Invalid identifier!");
            clearWarn();
        }
    }

    @Override
    public boolean hasCustomErrors() {
        return true;
    }

    //? if <= 1.21.6 {
    /*@Override
    protected boolean mouseClickedEntry(double mouseX, double mouseY, int button) {
        if (playSoundButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClickedEntry(mouseX, mouseY, button);
    }
    *///?} else {
    @Override
    protected boolean mouseClickedEntry(MouseButtonEvent click, boolean doubled) {
        if (playSoundButton.mouseClicked(click, doubled)) {
            return true;
        }
        return super.mouseClickedEntry(click, doubled);
    }
    //?}

    @Override
    public ConfigTypes getValueType() {
        return ConfigTypes.MODIFIABLE_SOUND;
    }
}
