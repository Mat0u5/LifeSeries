package net.mat0u5.lifeseries.config.entries;

import net.mat0u5.lifeseries.network.packets.ConfigPayload;

import java.util.List;

public class TextObject extends ConfigObject {
    public String text;
    public boolean clickable;
    public TextObject(ConfigPayload payload, String text, boolean clickable) {
        super(payload);
        this.text = text;
        this.clickable = clickable;
    }

    @Override
    public List<String> getArgs() {
        return List.of();
    }
}