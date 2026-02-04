package net.mat0u5.lifeseries.utils.enums;

public enum ConfigTypes {
    NULL(""),

    STRING("string"),
    BOOLEAN("boolean"),
    INTEGER("integer"),
    NULLABLE_INTEGER("nullable_integer"),
    DOUBLE("double"),
    TEXT("text"),

    HEARTS("hearts"),
    PERCENTAGE("percentage"),
    ITEM_LIST("itemlist"),
    BLOCK_LIST("blocklist"),
    EFFECT_LIST("effectlist"),
    ENCHANT_LIST("enchantlist"),
    BOOGEYMAN("boogeyman"),
    SECONDS("seconds"),
    MINUTES("minutes"),
    STRING_LIST("list"),

    LIVES_ENTRY("lives"),
    TEAM_ENTRY("teams"),
    EVENT_ENTRY("event"),

    SECRET_TASK("secret_task"),
    TRIVIA_QUESTION("trivia_question"),
    TRIVIA_ANSWER("trivia_answer"),

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
        return this == STRING || this == ITEM_LIST || this == BLOCK_LIST || this == EFFECT_LIST || this == ENCHANT_LIST || this == STRING_LIST || this == EVENT_ENTRY;
    }
    public boolean parentText() {
        return this == TEXT || this == TEAM_ENTRY || this == SECRET_TASK || this == TRIVIA_QUESTION || this == TRIVIA_ANSWER;
    }
    public boolean parentBoolean() {
        return this == BOOLEAN || this == BOOGEYMAN;
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
