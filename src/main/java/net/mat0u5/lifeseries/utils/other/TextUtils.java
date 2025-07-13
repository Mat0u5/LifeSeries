package net.mat0u5.lifeseries.utils.other;

import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//? if >= 1.21.4
/*import java.net.URI;*/

public class TextUtils {
    private static HashMap<List<String>, List<String>> emotes = new HashMap<List<String>, List<String>>();

    public static void setEmotes() {
        emotes.put(List.of("skull"),List.of("☠"));
        emotes.put(List.of("smile"),List.of("☺"));
        emotes.put(List.of("frown"),List.of("☹"));
        emotes.put(List.of("heart"),List.of("❤"));
        emotes.put(List.of("copyright"),List.of("©"));
        emotes.put(List.of("trademark","tm"),List.of("™"));
    }

    public static String replaceEmotes(String input) {
        for (Map.Entry<List<String>, List<String>> entry : emotes.entrySet()) {
            if (entry.getValue().size()==0) continue;
            String emoteValue = entry.getValue().get(0);
            for (String emote : entry.getKey()) {
                String emoteCode = ":" + emote + ":";
                input = replaceCaseInsensitive(input, emoteCode, emoteValue);
            }
            if (!input.contains(":")) return input;
        }
        return input;
    }

    public static String replaceCaseInsensitive(String input, String replaceWhat, String replaceWith) {
        Pattern pattern = Pattern.compile(replaceWhat, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(input);
        String result = matcher.replaceAll(replaceWith);
        return result;
    }

    public static String toRomanNumeral(int num) {
        String[] romanNumerals = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
        return (num > 0 && num <= romanNumerals.length) ? romanNumerals[num - 1] : String.valueOf(num);
    }

    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String textToLegacyString(Text text) {
        StringBuilder formattedString = new StringBuilder();
        Style style = text.getStyle();

        // Convert color
        if (style.getColor() != null) {
            formattedString.append(getColorCode(style.getColor()));
        }

        // Convert other formatting (bold, italic, etc.)
        if (style.isBold()) formattedString.append("§l");
        if (style.isItalic()) formattedString.append("§o");
        if (style.isUnderlined()) formattedString.append("§n");
        if (style.isStrikethrough()) formattedString.append("§m");
        if (style.isObfuscated()) formattedString.append("§k");

        // Append the raw text
        formattedString.append(text.getString());

        return formattedString.toString();
    }

    public static String getColorCode(TextColor color) {
        for (Formatting formatting : Formatting.values()) {
            if (formatting.getColorValue() == color.getRgb()) {
                return "§" + formatting.getCode();
            }
        }
        return "";
    }
    public static String removeFormattingCodes(String input) {
        return input.replaceAll("§[0-9a-fk-or]", "");
    }

    public static ClickEvent openURLClickEvent(String url) {
        //? if <= 1.21.4 {
        return new ClickEvent(ClickEvent.Action.OPEN_URL, url);
        //?} else {
        /*return new ClickEvent.OpenUrl(URI.create(url));
        *///?}
    }

    public static ClickEvent runCommandClickEvent(String command) {
        //? if <= 1.21.4 {
        return new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);
         //?} else {
        /*return new ClickEvent.RunCommand(command);
        *///?}
    }

    public static ClickEvent copyClipboardClickEvent(String copy) {
        //? if <= 1.21.4 {
        return new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, copy);
         //?} else {
        /*return new ClickEvent.CopyToClipboard(copy);
        *///?}
    }

    public static HoverEvent showTextHoverEvent(Text text) {
        //? if <= 1.21.4 {
        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, text);
         //?} else {
        /*return new HoverEvent.ShowText(text);
        *///?}
    }
}
