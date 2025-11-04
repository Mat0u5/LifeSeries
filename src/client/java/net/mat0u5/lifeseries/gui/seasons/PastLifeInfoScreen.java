package net.mat0u5.lifeseries.gui.seasons;

import net.mat0u5.lifeseries.gui.DefaultScreen;
import net.mat0u5.lifeseries.render.RenderUtils;
import net.mat0u5.lifeseries.utils.TextColors;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
//? if <= 1.21.9 {
import net.minecraft.Util;
 //?} else {
/*import net.minecraft.util.Util;
*///?}

public class PastLifeInfoScreen extends DefaultScreen {

    private static final String pastLifeInfoText =
            "§n§lIMPORTANT:§r\n"+
            "The main twist of Past Life is that every session is played on a different version of Minecraft. That, obviously, cannot be done with this one mod.\n" +
            "I've made a separate project on Modrinth which has §nonly§r the Past Life mod for each of the versions used in the original series.\n" +
            "Click the button below to go to that mod page.\n" +
            "§7§o§nKeep in mind that the setup is quite difficult for the earliest mc versions.§r\n\n"+
            "§8Past Life is still fully playable here (with this mod), it just won't have the different versions aspect.";

    protected PastLifeInfoScreen() {
        super(Component.nullToEmpty("Past Life Info"), 410, 210);
    }

    @Override
    public void init() {
        super.init();

        String buttonText = "Open Past Life Mod Page";

        this.addRenderableWidget(
                Button.builder(Component.literal(buttonText), btn -> {
                            this.onClose();
                            Util.getPlatform().openUri("https://modrinth.com/mod/past-life");
                        })
                        .pos(centerX - 90, endY - 25)
                        .size(180, 20)
                        .build()
        );
        this.addRenderableWidget(
                Button.builder(Component.literal("Close"), btn -> {
                            this.onClose();;
                        })
                        .pos(endX - 70, endY - 25)
                        .size(60, 20)
                        .build()
        );
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY) {
        RenderUtils.drawTextCenterScaled(context, this.font, Component.nullToEmpty("§0Past Life"), centerX, startY + 7, 2f, 2f);
        RenderUtils.drawTextLeftWrapLines(context, this.font, TextColors.PASTEL_RED, Component.nullToEmpty(pastLifeInfoText), startX + 12, startY + 30, BG_WIDTH-30, 6);
    }
}
