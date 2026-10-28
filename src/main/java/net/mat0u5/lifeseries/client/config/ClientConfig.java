package net.mat0u5.lifeseries.client.config;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.client.LifeSeriesClient;
import net.mat0u5.lifeseries.client.utils.LifeSeriesResourcePacks;
import net.mat0u5.lifeseries.client.utils.enums.MinimalArmorBehavior;
import net.mat0u5.lifeseries.config.SeasonConfig;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.versions.VersionControl;
import net.mat0u5.matlib.config.GuiConfig;
import net.mat0u5.matlib.config.entry.ConfigurableValue;
import net.minecraft.server.level.ServerPlayer;

public class ClientConfig extends GuiConfig {
    public final ConfigurableValue<MinimalArmorBehavior> MINIMAL_ARMOR = ConfigurableValue.register(this, "minimal_armor_resourcepack", MinimalArmorBehavior.MATCH_SERVER)
            .name("Minimal Armor Resourcepack")
            .description("Enables the minimal armor resourcepack.")
            .onChange((oldValue, newValue) -> LifeSeriesResourcePacks.checkClientPacks());
    public final ConfigurableValue<Boolean> SESSION_TIMER = ConfigurableValue.register(this, "session_timer", true)
            .name("Session Timer")
            .description("Enables the session timer in the bottom right of the screen.");
    public final ConfigurableValue<Boolean> COLORBLIND_SUPPORT = ConfigurableValue.register(this, "colorblind_support", false)
            .name("Colorblind Support")
            .description("Enables a feature that shows the team color name next to a players' usernames.");

    public final ConfigurableValue<Boolean> COLORED_HEARTS = ConfigurableValue.register(this, "colored_hearts", false)
            .name("Colored Hearts Based on Lives")
            .description("Makes your hearts the same color as how many lives you have.");
    public final ConfigurableValue<Boolean> HARDCORE_HEARTS_LAST_LIFE = ConfigurableValue.register(this, "hardcore_hearts_last_life", false)
            .name("Show Last Life as Hardcore")
            .description("When you are on your last life, your hearts will appear as though you are in hardcore.");
    public final ConfigurableValue<Boolean> HARDCORE_HEARTS_ALL_LIVES = ConfigurableValue.register(this, "hardcore_hearts_all_lives", false)
            .name("Show All Lives as Hardcore")
            .description("Hearts will always appear as though you are in hardcore.");

    public final ConfigurableValue<Double> TEXT_HUD_SCALE = ConfigurableValue.register(this, "text_hud_scale", 1.0)
            .name("Side Text Hud Scale")
            .description("The scale at which the side texts, like the session timer render at.");

    public final ConfigurableValue<String> RUN_COMMAND = ConfigurableValue.register(this, "run_command", "/lifeseries config")
            .name("Keybind Command")
            .description("Pressing the 'Keybind Command' keybind will run this command. Only available in dev versions.")
            .visibleIf(VersionControl::isDevVersion);
    public final ConfigurableValue<Boolean> NICE_LIFE_LESS_SNOW = ConfigurableValue.register(this, "nice_life_less_snow", true)
            .name("Less Obstructive Snow in Nice Life")
            .description("Reduces the number of snow particles when it snows.")
            .visibleIf(() -> LifeSeries.isSeason(Seasons.NICE_LIFE));
    public final ConfigurableValue<Boolean> LIMITED_LIFE_ACTIONBAR_TIME = ConfigurableValue.register(this, "limited_life_actionbar_time", false)
            .name("Move Time to Actionbar in Limited Life")
            .description("Shows the remaining time in the actionbar instead of the side title.")
            .visibleIf(() -> LifeSeries.isSeason(Seasons.LIMITED_LIFE));

    public ClientConfig() {
        super(SeasonConfig.CLIENT_CONFIG_ID, SeasonConfig.GUI_TITLE, "lifeseries/client", LifeSeries.MOD_ID+"_client.properties");
    }

    @Override
    public boolean hasPermissionsToOpen(ServerPlayer player) {
        return true;
    }

    @Override
    public void reload() {
        super.reload();
        LifeSeriesClient.reloadConfig();
    }
}
