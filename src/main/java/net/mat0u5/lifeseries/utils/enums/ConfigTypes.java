package net.mat0u5.lifeseries.utils.enums;

public enum ConfigTypes {
    NULL(""),

    BOOGEYMAN("boogeyman"),

    LIVES_ENTRY("lives"),
    TEAM_ENTRY("teams"),
    EVENT_ENTRY("event"),
    SOULMATE_ENTRY("soulmate"),

    SECRET_TASK("secret_task"),
    TRIVIA_QUESTION("trivia_question"),
    TRIVIA_ANSWER("trivia_answer"),
    MODIFIABLE_TEXT("modifiable_text"),
    BOOLEAN_REVERSE_HEADER("boolean_reverse_header"),

    GROUP("group");

    private final String text;
    ConfigTypes(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public boolean parentString() {
        return this == STRING || this == ITEM_LIST || this == BLOCK_LIST ||
                this == EFFECT_LIST || this == ENCHANT_LIST || this == STRING_LIST
                || this == ENUM || this == EVENT_ENTRY || this == MODIFIABLE_TEXT;
    }
    public boolean parentText() {
        return this == TEXT || this == TEAM_ENTRY || this == SECRET_TASK || this == TRIVIA_QUESTION || this == TRIVIA_ANSWER || this == SOULMATE_ENTRY;
    }
    public boolean parentBoolean() {
        return this == BOOLEAN || this == BOOGEYMAN || this == BOOLEAN_REVERSE_HEADER;
    }
    public boolean parentInteger() {
        return this == INTEGER || this == HEARTS || this == SECONDS;
    }
    public boolean parentNullableInteger() {
        return this == NULLABLE_INTEGER || this == LIVES_ENTRY;
    }
    public boolean parentDouble() {
        return this == DOUBLE || this == PERCENTAGE || this == MINUTES;
    }

    public static ConfigTypes getFromString(String string) {
        for (ConfigTypes type : ConfigTypes.values()) {
            if (string.equalsIgnoreCase(type.toString())) return type;
        }
        return NULL;
    }
}
