package net.mat0u5.lifeseries.gui.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.config.ClientConfigNetwork;
import net.mat0u5.lifeseries.gui.config.entries.ConfigEntry;
import net.mat0u5.lifeseries.gui.config.entries.GroupConfigEntry;
import net.mat0u5.lifeseries.gui.config.entries.extra.TriviaQuestionConfigEntry;
import net.mat0u5.lifeseries.gui.config.entries.main.TextConfigEntry;
import net.mat0u5.lifeseries.render.RenderUtils;
import net.mat0u5.lifeseries.utils.TextColors;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
//? if >= 1.21.9 {
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
//?}

public class ConfigScreen extends Screen {
    private static int HEADER_HEIGHT_SMALL = 55;
    private static int HEADER_HEIGHT_LARGE = 75;
    private static final int HEADER_TITLE_Y = 10;
    private static final int HEADER_CATEGORY_GAP = 5;
    private static final int HEADER_CATEGORY_Y = 24;
    private static final int HEADER_CATEGORY_MIN_WIDTH = 130;
    private static final int HEADER_CATEGORY_HEIGHT = 20;
    private static final int HEADER_CATEGORY_NAME_OFFSET_Y = 6;

    private static final int SEARCH_BAR_WIDTH = 550;
    private static final int SEARCH_BAR_HEIGHT = 20;
    private static final int SEARCH_BAR_Y_OFFSET = -6;

    private static final int FOOTER_HEIGHT = 30;
    private static final int FOOTER_BUTTON_GAP = 4;
    private static final int FOOTER_BUTTON_WIDTH = 145;
    private static final int FOOTER_BUTTON_HEIGHT = 20;

    private static final int RESETALL_BUTTON_WIDTH = 75;
    private static final int RESETALL_BUTTON_HEIGHT = 20;

    private final Screen parent;
    private final Map<String, List<ConfigEntry>> categories;
    private final List<String> categoryNames;

    private ConfigEntry focusedEntry;
    public ConfigListWidget listWidget;
    private Button saveButton;
    private Button cancelButton;
    private Button resetAllButton;
    private EditBox searchField;
    private int selectedCategory = 0;
    private boolean hasChanges = false;
    private String currentSearchQuery = "";

    public ConfigScreen(Screen parent, Component title, Map<String, List<ConfigEntry>> categories) {
        super(title);
        this.parent = parent;
        this.categories = categories;
        this.categoryNames = Lists.newArrayList(categories.keySet());

        this.initializeConfigEntries();
    }

    private void initializeConfigEntries() {
        for (List<ConfigEntry> entries : this.categories.values()) {
            for (ConfigEntry entry : entries) {
                entry.setScreen(this);
            }
        }
        for (ConfigEntry entry : getAllEntries()) {
            if (entry.screen == null) {
                entry.setScreen(this);
            }
        }
    }

    @Override
    protected void init() {
        super.init();

        int headerHeight = this.categoryNames.size() > 1 ? HEADER_HEIGHT_LARGE : HEADER_HEIGHT_SMALL;
        int searchBarY = headerHeight - SEARCH_BAR_HEIGHT + SEARCH_BAR_Y_OFFSET;
        int listTop = headerHeight;

        int searchBarWidth = Math.min(SEARCH_BAR_WIDTH, this.width - 40);
        this.searchField = new EditBox(this.font,
                (this.width-searchBarWidth)/2, searchBarY,
                searchBarWidth, SEARCH_BAR_HEIGHT,
                Component.nullToEmpty("§7Search config..."));
        this.searchField.setHint(Component.nullToEmpty("§7Search config..."));
        this.searchField.setValue(this.currentSearchQuery);
        this.searchField.setResponder(this::onSearchChanged);
        this.addWidget(this.searchField);

        //? if <= 1.20.2 {
        /*this.listWidget = new ConfigListWidget(this.minecraft, this.width, this.height - listTop - FOOTER_HEIGHT, listTop, this.height - FOOTER_HEIGHT, ConfigEntry.PREFFERED_HEIGHT);
        *///?} else {
        this.listWidget = new ConfigListWidget(this.minecraft, this.width, this.height - listTop - FOOTER_HEIGHT, listTop, ConfigEntry.PREFFERED_HEIGHT);
        //?}
        listWidget.setScreen(this);

        this.addWidget(this.listWidget);
        this.addRenderableWidget(this.listWidget);

        this.refreshList();

        this.saveButton = Button.builder(Component.nullToEmpty("Save & Quit"), button -> this.save())
                .bounds(this.width / 2 + FOOTER_BUTTON_GAP, this.height - FOOTER_BUTTON_HEIGHT - FOOTER_BUTTON_GAP, FOOTER_BUTTON_WIDTH, FOOTER_BUTTON_HEIGHT)
                .build();
        this.addRenderableWidget(this.saveButton);

        this.cancelButton = Button.builder(Component.nullToEmpty("Discard Changes"), button -> this.onClose())
                .bounds(this.width / 2 - FOOTER_BUTTON_WIDTH - FOOTER_BUTTON_GAP, this.height - FOOTER_BUTTON_HEIGHT - FOOTER_BUTTON_GAP, FOOTER_BUTTON_WIDTH, FOOTER_BUTTON_HEIGHT)
                .build();
        this.addRenderableWidget(this.cancelButton);

        this.resetAllButton = Button.builder(Component.nullToEmpty("Reset All"), button -> this.resetAll())
                .bounds(this.width - RESETALL_BUTTON_WIDTH - 10, this.height - RESETALL_BUTTON_HEIGHT - FOOTER_BUTTON_GAP, RESETALL_BUTTON_WIDTH, RESETALL_BUTTON_HEIGHT)
                .build();
        this.addRenderableWidget(this.resetAllButton);

        this.updateButtonStates();
    }

    private void resetAll() {
        for (ConfigEntry entry : getAllEntries(this.categories.get(this.categoryNames.get(selectedCategory)))) {
            entry.resetToDefault();
            entry.markChanged();
        }
    }

    private boolean canResetAll() {
        for (ConfigEntry entry : getAllEntries(this.categories.get(this.categoryNames.get(selectedCategory)))) {
            if (entry.canReset() && entry.hasResetButton()) {
                return true;
            }
        }
        return false;
    }

    private void onSearchChanged(String query) {
        this.currentSearchQuery = query;
        this.refreshList();
    }

    private boolean matchesSearch(ConfigEntry entry, String query) {
        if (entry instanceof TextConfigEntry) {
            return false;
        }

        if (query.isEmpty()) {
            return true;
        }

        String lowerQuery = query.toLowerCase(Locale.ROOT);

        if (entry.getDisplayName().getString().toLowerCase(Locale.ROOT).contains(lowerQuery)) {
            return true;
        }

        if (entry.getFieldName().toLowerCase(Locale.ROOT).contains(lowerQuery)) {
            return true;
        }

        String description = entry.getDescription();
        if (description != null && !description.isEmpty() && description.toLowerCase(Locale.ROOT).contains(lowerQuery)) {
            return true;
        }

        return false;
    }

    private List<ConfigEntry> getFilteredEntries(List<ConfigEntry> entries, String searchQuery) {
        if (searchQuery.isEmpty()) {
            return entries;
        }

        List<ConfigEntry> filteredEntries = new ArrayList<>();
        for (ConfigEntry entry : entries) {
            if (matchesSearch(entry, searchQuery)) {
                filteredEntries.add(entry);
            }
        }
        return filteredEntries;
    }

    private void refreshList() {
        this.listWidget.clearAllEntries();
        if (this.selectedCategory < this.categoryNames.size()) {
            String categoryName = this.categoryNames.get(this.selectedCategory);
            List<ConfigEntry> entries = this.categories.get(categoryName);
            if (entries != null) {
                String searchQuery = this.currentSearchQuery.trim();
                if (searchQuery.isEmpty()) {
                    for (ConfigEntry entry : entries) {
                        if (!entry.isSearchable()) continue;
                        this.listWidget.addEntry(entry);
                    }
                }
                else {
                    for (ConfigEntry entry : getFilteredEntries(getAllEntries(entries), searchQuery)) {
                        if (!entry.isSearchable()) continue;
                        this.listWidget.addEntry(entry);
                    }
                }
            }
        }
        this.updateButtonStates();
    }

    public void onEntryValueChanged() {
        this.updateButtonStates();
    }

    public List<ConfigEntry> getAllEntries() {
        List<ConfigEntry> allSurfaceEntries = new ArrayList<>();
        for (List<ConfigEntry> entries : this.categories.values()) {
            allSurfaceEntries.addAll(entries);
        }
        return getAllEntries(allSurfaceEntries);
    }

    public List<ConfigEntry> getAllEntries(List<ConfigEntry> currentEntries) {
        List<ConfigEntry> allEntries = new ArrayList<>();
        for (ConfigEntry entry : currentEntries) {
            if (entry instanceof TriviaQuestionConfigEntry triviaQuestionConfigEntry) {
                allEntries.add(triviaQuestionConfigEntry);
                allEntries.addAll(getAllEntries(triviaQuestionConfigEntry.renderAsGroup.getChildEntries()));
                allEntries.addAll(getAllEntries(List.of(triviaQuestionConfigEntry.renderAsGroup.getMainEntry())));
            }
            else if (entry instanceof GroupConfigEntry<?> groupEntry) {
                allEntries.addAll(getAllEntries(groupEntry.getChildEntries()));
                allEntries.addAll(getAllEntries(List.of(groupEntry.getMainEntry())));
            }
            else {
                allEntries.add(entry);
            }
        }
        return allEntries;
    }

    private void updateButtonStates() {
        this.hasChanges = false;
        for (ConfigEntry entry : getAllEntries()) {
            if (entry.isModified() || entry.changedForever) {
                this.hasChanges = true;
                break;
            }
        }
        if (this.saveButton != null) this.saveButton.active = this.hasChanges && !this.hasErrors();
        if (this.resetAllButton != null) this.resetAllButton.active = canResetAll();
    }

    private boolean hasErrors() {
        for (ConfigEntry entry : getAllEntries()) {
            if (entry.hasError()) {
                return true;
            }
        }
        return false;
    }

    private void save() {
        List<ConfigEntry> allSurfaceEntriesClient = new ArrayList<>();
        List<ConfigEntry> allSurfaceEntriesServer = new ArrayList<>();
        for (Map.Entry<String, List<ConfigEntry>> category : this.categories.entrySet()) {
            if (category.getKey().equals("Server")) {
                allSurfaceEntriesServer.addAll(category.getValue());
            }
            else if (category.getKey().equals("Client")) {
                allSurfaceEntriesClient.addAll(category.getValue());
            }
        }

        for (ConfigEntry entry : getAllEntries(allSurfaceEntriesServer)) {
            // Server
            if (!entry.isModified() && !entry.changedForever) continue;
            if (!entry.sendToServer()) continue;
            entry.onSave();
        }
        for (ConfigEntry entry : getAllEntries(allSurfaceEntriesClient)) {
            // Client
            if (!entry.isModified() && !entry.changedForever) continue;
            if (entry instanceof GroupConfigEntry) continue;
            ClientConfigNetwork.onConfigSave(entry);
        }
        MainClient.reloadConfig();

        this.minecraft.setScreen(this.parent);
    }

    @Override
    public void onClose() {
        if (this.hasChanges) {
            this.minecraft.setScreen(new ConfirmScreen(
                    confirmed -> {
                        if (confirmed) {
                            this.minecraft.setScreen(this.parent);
                        } else {
                            this.minecraft.setScreen(this);
                        }
                    },
                    Component.nullToEmpty("Changes Not Saved"),
                    Component.nullToEmpty("Are you sure you want to quit editing the config? Changes will not be saved!"),
                    Component.nullToEmpty("Quit & Discard Changes"),
                    Component.nullToEmpty("Cancel")
            ));
        } else {
            this.minecraft.setScreen(this.parent);
        }
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        //?if <= 1.20.2 {
        /*//?if <= 1.20 {
        /^context.setColor(0.25F, 0.25F, 0.25F, 1.0F);
        ^///?} else {
        context.setColor(0.85F, 0.85F, 0.85F, 1.0F);
        //?}
        RenderUtils.texture(Screen.BACKGROUND_LOCATION, 0, this.height - FOOTER_HEIGHT, this.width, FOOTER_HEIGHT).textureSize(32, 32).render(context);
        context.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        *///?}
        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredString(this.font, this.title, this.width / 2, HEADER_TITLE_Y, TextColors.WHITE);

        if (this.categoryNames.size() > 1) {
            this.renderCategoryTabs(context, mouseX, mouseY);
        }

        this.searchField.render(context, mouseX, mouseY, delta);

        updateButtonStates();
        if (this.hasErrors()) {
            context.drawString(this.font, Component.nullToEmpty("Errors"), 10, HEADER_TITLE_Y, TextColors.LIGHT_RED);
        }
    }

    private void renderCategoryTabs(GuiGraphics context, int mouseX, int mouseY) {
        int tabWidth = Math.min(HEADER_CATEGORY_MIN_WIDTH, this.width / this.categoryNames.size());
        int startX = (this.width - ((tabWidth+HEADER_CATEGORY_GAP) * this.categoryNames.size())) / 2;

        for (int i = 0; i < this.categoryNames.size(); i++) {
            int tabX = startX + i * (tabWidth+HEADER_CATEGORY_GAP);
            int tabY = HEADER_CATEGORY_Y;
            int tabHeight = HEADER_CATEGORY_HEIGHT;

            boolean isSelected = i == this.selectedCategory;
            boolean isHovered = mouseX >= tabX && mouseX < tabX + tabWidth && mouseY >= tabY && mouseY < tabY + tabHeight;

            int color = isSelected ? TextColors.WHITE_A128 : (isHovered ? TextColors.WHITE_A64 : TextColors.WHITE_A32);
            context.fill(tabX, tabY, tabX + tabWidth, tabY + tabHeight, color);

            String categoryName = this.categoryNames.get(i);
            int textColor = isSelected ? TextColors.WHITE : TextColors.PASTEL_WHITE;
            context.drawCenteredString(this.font, categoryName, tabX + tabWidth / 2, tabY + HEADER_CATEGORY_NAME_OFFSET_Y, textColor);
        }
    }

    //? if <= 1.21.6 {
    /*@Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean leftClick = button == 0;
        if (this.searchField.mouseClicked(mouseX, mouseY, button)) {
    *///?} else {
    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        int mouseX = (int) click.x();
        int mouseY = (int) click.y();
        boolean leftClick = click.button() == 0;

        if (this.searchField.mouseClicked(click, doubled)) {
    //?}
            focusSearch();
            return true;
        }
        else {
            this.searchField.setFocused(false);
        }

        if (this.categoryNames.size() > 1 && leftClick) {
            int tabWidth = Math.min(HEADER_CATEGORY_MIN_WIDTH, this.width / this.categoryNames.size());
            int startX = (this.width - ((tabWidth+HEADER_CATEGORY_GAP) * this.categoryNames.size())) / 2;

            for (int i = 0; i < this.categoryNames.size(); i++) {
                int tabX = startX + i * (tabWidth+HEADER_CATEGORY_GAP);
                int tabY = HEADER_CATEGORY_Y;
                int tabHeight = HEADER_CATEGORY_HEIGHT;

                if (mouseX >= tabX && mouseX < tabX + tabWidth && mouseY >= tabY && mouseY < tabY + tabHeight) {
                    this.selectedCategory = i;
                    this.refreshList();
                    return true;
                }
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
        if (this.searchField.isFocused() && this.searchField.keyPressed(keyCode, scanCode, modifiers)) {
    *///?} else {
    @Override
    public boolean keyPressed(KeyEvent keyInput) {
        int keyCode = keyInput.input();
        int modifiers = keyInput.modifiers();
        if (this.searchField.isFocused() && this.searchField.keyPressed(keyInput)) {
    //?}
            return true;
        }

        // Ctrl+F to focus search
        if (keyCode == GLFW.GLFW_KEY_F && (modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
            focusSearch();
            return true;
        }

        //? if <= 1.21.6 {
        /*return super.keyPressed(keyCode, scanCode, modifiers);
        *///?} else {
        return super.keyPressed(keyInput);
        //?}
    }

    //? if <= 1.21.6 {
    /*@Override
    public boolean charTyped(char chr, int modifiers) {
        if (this.searchField.isFocused() && this.searchField.charTyped(chr, modifiers)) {
            return true;
        }
        return super.charTyped(chr, modifiers);
    }
    *///?} else {
    @Override
    public boolean charTyped(CharacterEvent charInput) {
        if (this.searchField.isFocused() && this.searchField.charTyped(charInput)) {
            return true;
        }
        return super.charTyped(charInput);
    }
    //?}

    public void focusSearch() {
        if (focusedEntry != null) {
            focusedEntry.setFocused(false);
            focusedEntry = null;
        }
        searchField.setFocused(true);
    }

    public Font getTextRenderer() {
        return this.font;
    }

    public ConfigEntry getFocusedEntry() {
        return focusedEntry;
    }

    public void setFocusedEntry(ConfigEntry entry) {
        if (!entry.canLoseFocusEasily()) return;
        if (focusedEntry == entry) return;
        searchField.setFocused(false);

        if (focusedEntry != null) {
            focusedEntry.setFocused(false);
        }
        focusedEntry = entry;
    }

    public static class Builder {
        private final Screen parent;
        private final Component title;
        private final Map<String, List<ConfigEntry>> categories = Maps.newLinkedHashMap();

        public Builder(Screen parent, Component title) {
            this.parent = parent;
            this.title = title;
        }

        public CategoryBuilder addCategory(String name) {
            this.categories.put(name, Lists.newArrayList());
            return new CategoryBuilder(this, name);
        }

        public ConfigScreen build() {
            return new ConfigScreen(this.parent, this.title, this.categories);
        }

        public static class CategoryBuilder {
            private final Builder parent;
            private final String categoryName;

            public CategoryBuilder(Builder parent, String categoryName) {
                this.parent = parent;
                this.categoryName = categoryName;
            }

            public CategoryBuilder addEntry(ConfigEntry entry) {
                this.parent.categories.get(this.categoryName).add(entry);
                return this;
            }

            public Builder endCategory() {
                return this.parent;
            }
        }
    }
}