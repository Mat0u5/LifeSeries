package net.mat0u5.lifeseries.utils.other;

import net.mat0u5.lifeseries.Main;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;
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

    public static String textToLegacyString(Component text) {
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
        for (ChatFormatting formatting : ChatFormatting.values()) {
            if (formatting.getColor() == color.getValue()) {
                return "§" + formatting.getChar();
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

    public static HoverEvent showTextHoverEvent(Component text) {
        //? if <= 1.21.4 {
        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, text);
        //?} else {
        /*return new HoverEvent.ShowText(text);
        *///?}
    }

    public static Component withHover(Component text, Component hover) {
        return text.copy().withStyle(style -> style.withHoverEvent(showTextHoverEvent(hover)));
    }
    public static Component withHover(String text, String hover) {
        return Component.literal(text).withStyle(style -> style.withHoverEvent(showTextHoverEvent(Component.literal(hover))));
    }

    public static Component selfMessageText(String message) {
        return runCommandText("/selfmsg " + message);
    }

    public static Component runCommandText(String command) {
        return hereText(runCommandClickEvent(command));
    }

    public static Component openURLText(String url) {
        return hereText(openURLClickEvent(url));
    }

    public static Component copyClipboardText(String copy) {
        return hereText(copyClipboardClickEvent(copy));
    }

    public static Component hereText(ClickEvent event) {
        return clickableText("here", event);
    }

    public static Component clickableText(String label, ClickEvent event) {
        return Component.literal(label)
                .withStyle(style -> style
                        .withColor(ChatFormatting.BLUE)
                        .withClickEvent(event)
                        .withUnderlined(true)
                );
    }


    public static MutableComponent formatPlain(String template, Object... args) {
        return Component.literal(formatString(template, args));
    }

    public static String formatString(String template, Object... args) {
        return format(template, args).getString();
    }

    public static MutableComponent format(String template, Object... args) {
        return formatStyled(false, template, args);
    }

    public static MutableComponent formatLoosely(String template, Object... args) {
        return formatStyled(true, template, args);
    }

    private static MutableComponent formatStyled(boolean looselyStyled, String template, Object... args) {
        MutableComponent result = Component.empty();
        StringBuilder resultLooselyStyled = new StringBuilder();

        int argIndex = 0;
        int lastIndex = 0;
        int placeholderIndex = template.indexOf("{}");

        if (placeholderIndex == -1) {
            Main.LOGGER.error("String ("+template+") formatting does not contain {}.");
        }
        if (args.length <= 0) {
            Main.LOGGER.error("String ("+template+") formatting does have arguments.");
        }
        if (("_"+template+"_").split("\\{\\}").length-1 != args.length) {
            Main.LOGGER.error("String ("+template+") formatting has incorrect number of arguments.");
        }

        while (placeholderIndex != -1 && argIndex < args.length) {
            if (placeholderIndex > lastIndex) {
                String textBefore = template.substring(lastIndex, placeholderIndex);
                result.append(Component.literal(textBefore));
                resultLooselyStyled.append(textBefore);
            }

            Object arg = args[argIndex];
            Component argText = getTextForArgument(arg);
            result.append(argText);
            resultLooselyStyled.append(argText.getString());

            argIndex++;
            lastIndex = placeholderIndex + 2;
            placeholderIndex = template.indexOf("{}", lastIndex);
        }

        if (lastIndex < template.length()) {
            String remainingText = template.substring(lastIndex);
            result.append(Component.literal(remainingText));
            resultLooselyStyled.append(remainingText);
        }

        if (looselyStyled) {
            return Component.literal(resultLooselyStyled.toString());
        }

        return result;
    }

    private static Component getTextForArgument(Object arg) {
        if (arg == null) {
            return Component.empty();
        }
        if (arg instanceof Component text) {
            return text;
        }
        if (arg instanceof ServerPlayer player) {
            Component name = player.getDisplayName();
            if (name == null) return Component.empty();
            return name;
        }
        if (arg instanceof List<?> list) {
            MutableComponent text = Component.empty();
            int index = 0;
            for (Object obj : list) {
                if (index != 0) {
                    text.append(Component.nullToEmpty(", "));
                }
                text.append(getTextForArgument(obj));
                index++;
            }
            return text;
        }
        return Component.nullToEmpty(arg.toString());
    }

    public static String pluralize(String text, Integer amount) {
        return pluralize(text, text+"s", amount);
    }

    public static String pluralize(String textSingular, String textPlural, Integer amount) {
        if (amount == null || Math.abs(amount) == 1) {
            return textSingular;
        }
        return textPlural;
    }

    public static String pluralize(String text, Double amount) {
        return pluralize(text, text+"s", amount);
    }

    public static String pluralize(String textSingular, String textPlural, Double amount) {
        if (amount == null || Math.abs(amount) == 1) {
            return textSingular;
        }
        return textPlural;
    }
}
