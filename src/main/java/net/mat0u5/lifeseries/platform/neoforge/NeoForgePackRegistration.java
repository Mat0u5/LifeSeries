package net.mat0u5.lifeseries.platform.neoforge;
//? neoforge {

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.utils.other.ModBuiltInPacks;
import net.minecraft.server.packs.PackType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddPackFindersEvent;

//? if <= 1.20.3 {
/*@Mod.EventBusSubscriber(modid = LifeSeries.MOD_ID, bus = net.neoforged.fml.common.Mod.EventBusSubscriber.Bus.MOD)
 *///?} else if <= 1.21.2 {
/*@EventBusSubscriber(modid = LifeSeries.MOD_ID, bus = net.neoforged.fml.common.EventBusSubscriber.Bus.MOD)
 *///?} else {
@EventBusSubscriber(modid = LifeSeries.MOD_ID)
//?}
public class NeoForgePackRegistration {
    @SubscribeEvent
    public static void onAddPackFinders(AddPackFindersEvent event) {
        /*if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            event.addRepositorySource(ModBuiltInPacks.client());
        }
        else if (event.getPackType() == PackType.SERVER_DATA) {
            event.addRepositorySource(ModBuiltInPacks.server());
        }*/
    }
}

//?}