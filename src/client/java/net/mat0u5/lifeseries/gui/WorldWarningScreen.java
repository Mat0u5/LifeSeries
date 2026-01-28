package net.mat0u5.lifeseries.gui;

import net.mat0u5.lifeseries.render.RenderUtils;
import net.mat0u5.lifeseries.utils.TextColors;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

//? if >= 1.21.9 {
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.FormattedText;
//?}

public class WorldWarningScreen extends Screen {
    private final Runnable onCancel;
    protected final WorldWarningScreen.Listener onProceed;
    private final String levelId;

    public WorldWarningScreen(
            String levelId,
            final Runnable onCancel,
            final WorldWarningScreen.Listener onProceed
    ) {
        super(Component.literal("World Modification Warning"));
        this.onCancel = onCancel;
        this.onProceed = onProceed;
        this.levelId = levelId;
    }
    @Override
    protected void init() {
        super.init();
        int yOffset = 20;
        this.addRenderableWidget(
                Button.builder(Component.literal("Confirm"), button -> this.onProceed.proceed(false))
                        .bounds(this.width / 2 - 140, this.height/2 + yOffset, 120, 20)
                        .build()
        );
        //this.addRenderableWidget(
        //        Button.builder(Component.literal("Disable Mod and Confirm"), button -> this.onProceed.proceed(true))
        //                .bounds(this.width / 2, this.height/2 + yOffset, 200, 20)
        //                .build()
        //);
        this.addRenderableWidget(
                Button.builder(Component.literal("Cancel"), button -> this.onCancel.run()).bounds(this.width / 2 + 140 - 120, this.height/2 + yOffset, 120, 20).build()
        );
    }

    @Override
    public void render(final GuiGraphics context, final int mouseX, final int mouseY, final float a) {
        super.render(context, mouseX, mouseY, a);
        RenderUtils.text(Component.literal("'"+levelId+"' has not been opened before with the Life Series mod.\nDoing so will automatically change the world border, gamerules and more.\nDo you want to proceed?"), this.width / 2, this.height / 2 - 50).anchorCenter().wrapLines(this.width, 6).colored(TextColors.WHITE).render(context, this.font);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    //? if <= 1.21.6 {
    /*public boolean keyPressed(int i, int j, int k) {
        if (i == 256) {
            this.onCancel.run();
            return true;
        } else {
            return super.keyPressed(i, j, k);
        }
    }
    *///?} else {
    @Override
    public boolean keyPressed(final KeyEvent event) {
        if (event.isEscape()) {
            this.onCancel.run();
            return true;
        } else {
            return super.keyPressed(event);
        }
    }
    //?}

    public interface Listener {
        void proceed(boolean disable);
    }
}