package net.mat0u5.lifeseries.platform.neoforge;
//? if neoforge {

/*import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.entity.angrysnowman.AngrySnowman;
import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.registries.MobRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@Mod(LifeSeries.MOD_ID)
public class NeoforgeEntrypoint {

	public NeoforgeEntrypoint(IEventBus modBus) {
		LifeSeries.onInitialize();
		modBus.addListener(this::registerAttributes);
	}

	private void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(MobRegistry.SNAIL, Snail.createAttributes().build());
		event.put(MobRegistry.TRIVIA_BOT, TriviaBot.createAttributes().build());
		event.put(MobRegistry.ANGRY_SNOWMAN, AngrySnowman.createAttributes().build());
	}
}
*///?}
