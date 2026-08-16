package net.mat0u5.lifeseries.utils.other;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;

public class ActionText {
	private final HoverEvent defaultCopyClipboardHover = TextUtils.showTextHoverEvent(Component.literal("Click to copy"));
	private final HoverEvent defaultOpenURLHover = TextUtils.showTextHoverEvent(Component.literal("Click to open"));
	//private final HoverEvent defaultRunCommandHover = TextUtils.showTextHoverEvent(Component.literal("Click to execute"));

	MutableComponent text;
	public ActionText() {
		this.text = Component.literal("here")
				.withStyle(style -> style
						.withColor(ChatFormatting.BLUE)
						.withUnderlined(true)
				);
	}

	public ActionText(String text) {
		this.text = Component.literal(text);
	}

	public ActionText(MutableComponent text) {
		this.text = text;
	}

	public ActionText styledBlueUnderline() {
		this.text.withStyle(style -> style
				.withColor(ChatFormatting.BLUE)
				.withUnderlined(true)
		);
		return this;
	}

	public ActionText withClickEvent(ClickEvent event) {
		this.text.withStyle(style ->
				style.withClickEvent(event)
		);
		return this;
	}

	public ActionText withHoverEvent(HoverEvent event) {
		this.text.withStyle(style ->
				style.withHoverEvent(event)
		);
		return this;
	}

	public ActionText defaultCopyClipboard(String copy) {
		return this.withClickEvent(TextUtils.copyClipboardClickEvent(copy)).withHoverEvent(defaultCopyClipboardHover);
	}

	/*
	public ActionText defaultRunCommand(String command) {
		return this.withClickEvent(TextUtils.runCommandClickEvent(command)).withHoverEvent(defaultRunCommandHover);
	}
	*/

	public ActionText defaultOpenURL(String url) {
		return this.withClickEvent(TextUtils.openURLClickEvent(url)).withHoverEvent(defaultOpenURLHover);
	}

	public static Component hereTextCopyClipboard(String copy) {
		return new ActionText().defaultCopyClipboard(copy).get();
	}

	/*
	public static Component hereTextRunCommand(String command) {
		return new ActionText().defaultRunCommand(command).get();
	}
	 */

	public static Component hereTextRunCommand(String hoverText, String command) {
		return new ActionText().runCommand(hoverText, command).get();
	}

	public static Component hereTextOpenURL(String url) {
		return new ActionText().defaultOpenURL(url).get();
	}

	public ActionText copyClipboard(String hoverText, String copy) {
		return this.copyClipboard(Component.literal(hoverText), copy);
	}

	public ActionText runCommand(String hoverText, String command) {
		return this.runCommand(Component.literal(hoverText), command);
	}

	public ActionText openURL(String hoverText, String url) {
		return this.openURL(Component.literal(hoverText), url);
	}

	public ActionText copyClipboard(Component hoverText, String copy) {
		return this.withClickEvent(TextUtils.copyClipboardClickEvent(copy)).withHoverEvent(TextUtils.showTextHoverEvent(hoverText));
	}

	public ActionText runCommand(Component hoverText, String command) {
		return this.withClickEvent(TextUtils.runCommandClickEvent(command)).withHoverEvent(TextUtils.showTextHoverEvent(hoverText));
	}

	public ActionText openURL(Component hoverText, String url) {
		return this.withClickEvent(TextUtils.openURLClickEvent(url)).withHoverEvent(TextUtils.showTextHoverEvent(hoverText));
	}

	public ActionText hoverText(Component hoverText) {
		return this.withHoverEvent(TextUtils.showTextHoverEvent(hoverText));
	}

	public MutableComponent get() {
		return text;
	}
}
