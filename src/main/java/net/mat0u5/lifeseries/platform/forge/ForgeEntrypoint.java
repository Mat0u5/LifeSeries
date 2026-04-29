package net.mat0u5.lifeseries.platform.forge;

//? if forge {

/*import net.mat0u5.lifeseries.LifeSeries;
import net.minecraftforge.fml.common.Mod;
//? if <= 1.21 {
/^import net.mat0u5.lifeseries.entity.angrysnowman.AngrySnowman;
import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.registries.MobRegistry;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
^///?}

@Mod(LifeSeries.MOD_ID)
public class ForgeEntrypoint {
	//? if <= 1.21 {
	/^public ForgeEntrypoint() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		LifeSeries.onInitialize();
		modBus.addListener(this::registerAttributes);
	}

	private void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(MobRegistry.SNAIL, Snail.createAttributes().build());
		event.put(MobRegistry.TRIVIA_BOT, TriviaBot.createAttributes().build());
		event.put(MobRegistry.ANGRY_SNOWMAN, AngrySnowman.createAttributes().build());
	}
	^///?} else {
	public ForgeEntrypoint() {
		LifeSeries.onInitialize();
	}
	//?}
}
*///?}
