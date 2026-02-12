package net.mat0u5.lifeseries.config;

import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Locale;

import static net.mat0u5.lifeseries.Main.currentSeason;

public enum ModifiableText {
    BOOGEYMAN_TEST("#7Blah Blah #cboogeyman #7:)")
    ,ARGS_RAWR("#a%1$s#a send a message (%3$s) to %4$s", List.of("Player", "Receiver", "Message"))
    ,ARGS_RAWR2("#a%s send a message", List.of("Player"))
    ,DOES_THIS_REALLY_WORK("#6Yes, yes it does %s", List.of("Mat"))
    //,NAME("")
    ;


    final String name;
    final String defaultValue;
    final List<String> args;

    ModifiableText(String defaultValue) {
        this( defaultValue, null);
    }

    ModifiableText(String defaultValue, List<String> args) {
        this.name = this.name().toLowerCase(Locale.ROOT).replace("_",".");
        this.defaultValue = defaultValue;
        this.args = args;
    }

    public Component get(Object... args) {
        return ModifiableTextManager.get(this.name, args);
    }

    public String getRegisterDefaultValue() {
        if (this == BOOGEYMAN_TEST && currentSeason.getSeason() == Seasons.THIRD_LIFE) return "#7Blah Blah boogeyman in THIRD LIFE WWWATTT";
        return this.defaultValue;
    }
    public List<String> getRegisterArgs() {
        if (this == BOOGEYMAN_TEST && currentSeason.getSeason() == Seasons.THIRD_LIFE) return List.of("TEST");
        return this.args;
    }

    public static void registerAllTexts() {
        for (ModifiableText modifiableText : ModifiableText.values()) {
            ModifiableTextManager.register(modifiableText.name, modifiableText.getRegisterDefaultValue(), modifiableText.getRegisterArgs());
        }
    }
}
