package net.mat0u5.lifeseries.gui.trivia;

import net.mat0u5.lifeseries.gui.EmptySleepScreen;
import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.render.RenderUtils;
import net.mat0u5.lifeseries.utils.TextColors;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

//? if >= 1.21.9 {
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
//?}

//? if <= 1.21.9 {
/*import net.minecraft.resources.ResourceLocation;
*///?} else {
import net.minecraft.resources.Identifier;
 //?}

public class VotingScreen extends Screen {
    private static final int PLAYER_ENTRY_HEIGHT = 32;
    private static final int LIST_TOP = 40;
    private static final int LIST_BOTTOM_OFFSET = 35;

    private static final float LIST_WIDTH_PERCENT = 0.33f;

    private EditBox searchBox;
    private Button submitButton;
    private List<PlayerEntry> players;
    private List<PlayerEntry> filteredPlayers;
    private int scrollOffset = 0;
    private int maxScroll = 0;
    private String selectedPlayer = null;
    private final List<String> availablePlayers;
    public int timerSeconds = 60;
    public boolean requiresSleep;
    public boolean closesWithEsc;
    public boolean showTimer;

    private int listLeft;
    private int listRight;
    private int listWidth;

    public VotingScreen(String name, boolean requiresSleep, boolean closesWithEsc, boolean showTimer, List<String> availablePlayers) {
        super(Component.literal(name));
        this.requiresSleep = requiresSleep;
        this.closesWithEsc = closesWithEsc;
        this.showTimer = showTimer;
        this.availablePlayers = availablePlayers;
    }

    @Override
    public void tick() {
        super.tick();
        if (timerSeconds <= 0) {
            this.onClose();
        }
    }

    @Override
    protected void init() {
        listWidth = (int)(width * LIST_WIDTH_PERCENT);
        listLeft = (width - listWidth) / 2;
        listRight = listLeft + listWidth;

        players = new ArrayList<>();
        filteredPlayers = new ArrayList<>();

        ClientPacketListener connection = minecraft.getConnection();
        if (connection != null) {
            for (PlayerInfo playerInfo : connection.getOnlinePlayers()) {
                String name = OtherUtils.profileName(playerInfo.getProfile());
                //? if <= 1.20 {
                /*ResourceLocation skin = playerInfo.getSkinLocation();
                *///?} else if <= 1.21.6 {
                /*ResourceLocation skin = playerInfo.getSkin().texture();
                *///?} else {
                var skin = playerInfo.getSkin().body().texturePath();
                //?}
                if (!availablePlayers.contains(name)) continue;
                players.add(new PlayerEntry(name, skin));
                filteredPlayers.add(new PlayerEntry(name, skin));
            }
        }
        filteredPlayers.sort((entry1, entry2) -> entry1.name.toLowerCase().compareTo(entry2.name.toLowerCase()));

        searchBox = new EditBox(
                font,
                listLeft,
                25,
                listWidth - 100,
                20,
                Component.literal("")
        );
        searchBox.setHint(Component.literal("§7§oSearch..."));
        searchBox.setBordered(false);
        searchBox.setResponder(this::onSearchChanged);
        addWidget(searchBox);

        submitButton = Button.builder(
                        Component.literal("Submit Vote"),
                        button -> onSubmitVote()
                )
                .bounds(width / 2 - 100, height - 23, 200, 20)
                .build();
        addRenderableWidget(submitButton);

        updateMaxScroll();
    }

    private void onSearchChanged(String search) {
        filteredPlayers.clear();
        String lowerSearch = search.toLowerCase();

        for (PlayerEntry player : players) {
            if (player.name.toLowerCase().contains(lowerSearch)) {
                filteredPlayers.add(player);
            }
        }
        filteredPlayers.sort((entry1, entry2) -> entry1.name.toLowerCase().compareTo(entry2.name.toLowerCase()));

        scrollOffset = 0;
        updateMaxScroll();
    }

    private void updateMaxScroll() {
        int listHeight = height - LIST_TOP - LIST_BOTTOM_OFFSET;
        int totalHeight = filteredPlayers.size() * (PLAYER_ENTRY_HEIGHT+ 4);
        maxScroll = Math.max(0, totalHeight - listHeight);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return closesWithEsc;
    }

    @Override
    //? if <= 1.20 {
    /*public void renderBackground(GuiGraphics context) {}
    *///?} else {
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {}
    //?}

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        RenderUtils.text(title, width / 2, 10).anchorCenter().colored(TextColors.WHITE).withShadow().render(graphics, font);
        searchBox.render(graphics, mouseX, mouseY, partialTick);

        submitButton.active = selectedPlayer != null && !selectedPlayer.isEmpty();

        // Timer
        if (showTimer) {
            long minutes = timerSeconds / 60;
            long seconds = timerSeconds - minutes * 60;
            String secondsStr = String.valueOf(seconds);
            String minutesStr = String.valueOf(minutes);
            while (secondsStr.length() < 2) secondsStr = "0" + secondsStr;
            while (minutesStr.length() < 2) minutesStr = "0" + minutesStr;
            Component timerText = TextUtils.format("{}:{}", minutesStr, secondsStr);

            RenderUtils.text(timerText, listRight, 25).anchorCenter().colored(TextColors.WHITE).withShadow().render(graphics, font);
        }

        int listTop = LIST_TOP;
        int listBottom = height - LIST_BOTTOM_OFFSET;
        int listHeight = listBottom - listTop;

        graphics.enableScissor(listLeft, listTop, listRight, listBottom);

        int posY = listTop - scrollOffset;
        for (int i = 0; i < filteredPlayers.size(); i++) {
            PlayerEntry player = filteredPlayers.get(i);

            if (posY + PLAYER_ENTRY_HEIGHT > listTop && posY < listBottom) {
                boolean hovered = mouseX >= listLeft && mouseX <= listRight && mouseY >= posY && mouseY <= posY + PLAYER_ENTRY_HEIGHT;
                boolean selected = player.name.equals(selectedPlayer);

                drawPlayerEntry(graphics, player, listLeft, posY, listWidth, hovered, selected);
            }

            posY += PLAYER_ENTRY_HEIGHT + 4;
        }

        graphics.disableScissor();

        if (maxScroll > 0) {
            drawScrollbar(graphics, listTop, listBottom);
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void drawPlayerEntry(GuiGraphics graphics, PlayerEntry player, int x, int y, int width, boolean hovered, boolean selected) {
        int bgColor = selected ? TextColors.BLACK_A192 : (hovered ? TextColors.BLACK_A128 : TextColors.BLACK_A96);
        graphics.fill(x, y, x + width, y + PLAYER_ENTRY_HEIGHT, bgColor);

        // Player head texture
        RenderUtils.texture(player.skin, x + 4, y + 4, 8, 8).outSize(24, 24).uv(8, 8).textureSize(64, 64).render(graphics);
        RenderUtils.texture(player.skin, x + 4, y + 4, 8, 8).outSize(24, 24).uv(40, 8).textureSize(64, 64).render(graphics);

        // Player name
        RenderUtils.text(player.name, x + 32, y + 12).colored(TextColors.WHITE).withShadow().render(graphics, font);
    }

    private void drawScrollbar(GuiGraphics graphics, int listTop, int listBottom) {
        int scrollbarX = listRight + 9;
        int scrollbarHeight = listBottom - listTop;
        int thumbHeight = Math.max(20, (scrollbarHeight * scrollbarHeight) / (filteredPlayers.size() * PLAYER_ENTRY_HEIGHT));
        int thumbY = listTop + (int)((scrollbarHeight - thumbHeight) * ((float)scrollOffset / maxScroll));

        graphics.fill(scrollbarX, listTop, scrollbarX + 6, listBottom, TextColors.BLACK);
        graphics.fill(scrollbarX, thumbY, scrollbarX + 6, thumbY + thumbHeight, TextColors.LIGHT_GRAY);
        graphics.fill(scrollbarX, thumbY, scrollbarX + 5, thumbY + thumbHeight-1, TextColors.GUI_BACKGROUND);
    }

    @Override
    //? if <= 1.20 {
    /*public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
    *///?} else {
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
    //?}
        if (maxScroll > 0) {
            scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset - (int)(scrollY * PLAYER_ENTRY_HEIGHT)));
            return true;
        }
        //? if <= 1.20 {
        /*return super.mouseScrolled(mouseX, mouseY, scrollY);
        *///?} else {
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        //?}
    }

    @Override
    //? if <= 1.21.6 {
    /*public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (searchBox.mouseClicked(mouseX, mouseY, button)) {
    *///?} else {
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        int mouseX = (int) click.x();
        int mouseY = (int) click.y();
        if (searchBox.mouseClicked(click, doubled)) {
    //?}
            searchBox.setFocused(true);
            return true;
        }
        searchBox.setFocused(false);

        int listTop = LIST_TOP;
        int listBottom = height - LIST_BOTTOM_OFFSET;

        if (mouseX >= listLeft && mouseX <= listRight && mouseY >= listTop && mouseY <= listBottom) {
            int clickedIndex = (int)((mouseY - listTop + scrollOffset) / (PLAYER_ENTRY_HEIGHT+ 4));

            if (clickedIndex >= 0 && clickedIndex < filteredPlayers.size()) {
                selectedPlayer = filteredPlayers.get(clickedIndex).name;
                return true;
            }
        }

        //? if <= 1.21.6 {
        /*return super.mouseClicked(mouseX, mouseY, button);
        *///?} else {
        return super.mouseClicked(click, doubled);
        //?}
    }

    //? if <= 1.21.6 {
    /*@Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searchBox.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (searchBox.charTyped(codePoint, modifiers)) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }
    *///?} else {
    @Override
    public boolean keyPressed(KeyEvent input) {
        if (searchBox.keyPressed(input)) {
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean charTyped(CharacterEvent input) {
        if (searchBox.charTyped(input)) {
            return true;
        }
        return super.charTyped(input);
    }
    //?}

    private void onSubmitVote() {
        if (selectedPlayer != null) {
            if (requiresSleep) {
                Minecraft.getInstance().setScreen(new EmptySleepScreen(false));
            }
            else {
                Minecraft.getInstance().setScreen(null);
            }
            SimplePackets.SUBMIT_VOTE.sendToServer(selectedPlayer);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static class PlayerEntry {
        final String name;
        //? if <= 1.21.9 {
        /*final ResourceLocation skin;
        PlayerEntry(String name, ResourceLocation skin) {
            this.name = name;
            this.skin = skin;
        }
        *///?} else {
        final Identifier skin;
        PlayerEntry(String name, Identifier skin) {
            this.name = name;
            this.skin = skin;
        }
        //?}

    }
}