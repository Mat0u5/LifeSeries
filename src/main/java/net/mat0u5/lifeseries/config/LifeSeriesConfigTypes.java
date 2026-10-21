package net.mat0u5.lifeseries.config;

import net.mat0u5.matlib.config.type.ConfigType;
import net.mat0u5.matlib.config.type.ConfigTypes;

import java.util.List;

public final class LifeSeriesConfigTypes {
    public static final ConfigType<Boolean> BOOGEYMAN = ConfigTypes.withId("boogeyman", ConfigTypes.BOOLEAN);
    public static final ConfigType<Boolean> BOOLEAN_REVERSE_HEADER = ConfigTypes.withId("boolean_reverse_header", ConfigTypes.BOOLEAN);

    public static final ConfigType<Integer> LIVES_ENTRY = ConfigTypes.withId("lives", ConfigTypes.NULLABLE_INTEGER);
    public static final ConfigType<String> EVENT_ENTRY = ConfigTypes.withId("event", ConfigTypes.STRING);
    public static final ConfigType<String> MODIFIABLE_TEXT = ConfigTypes.withId("modifiable_text", ConfigTypes.STRING);

    public static final ConfigType<List<String>> TEAM_ENTRY = ConfigTypes.withId("teams", ConfigTypes.STRING_LIST);
    public static final ConfigType<List<String>> SOULMATE_ENTRY = ConfigTypes.withId("soulmate", ConfigTypes.STRING_LIST);
    public static final ConfigType<List<String>> SECRET_TASK = ConfigTypes.withId("secret_task", ConfigTypes.STRING_LIST);
    public static final ConfigType<List<String>> TRIVIA_QUESTION = ConfigTypes.withId("trivia_question", ConfigTypes.STRING_LIST);
    public static final ConfigType<List<String>> TRIVIA_ANSWER = ConfigTypes.withId("trivia_answer", ConfigTypes.STRING_LIST);

    public static void init() {
        // Called to register.
    }
}
