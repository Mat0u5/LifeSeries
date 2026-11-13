package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.choice;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public record InBetweenChoice(String id, Component title, Component subtitle, ItemStack icon) {
	//TODO this will need to support non-item stack icons


	public void onChosen(ServerPlayer player) {
		//TODO
	}
}
