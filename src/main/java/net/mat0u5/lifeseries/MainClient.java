package net.mat0u5.lifeseries;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MainClient implements ClientModInitializer {
    public static float MIN_MSPT = 25.0F;
    @Override
    public void onInitializeClient() {
        Main.isClient = true;
        FabricLoader.getInstance().getModContainer(Main.MOD_ID).ifPresent(container -> {
            //ResourceManagerHelper.registerBuiltinResourcePack(Identifier.of(Main.MOD_ID, "lifeseries"), container, Text.translatable("Main Life Series Resourcepack"), ResourcePackActivationType.ALWAYS_ENABLED);
            ResourceManagerHelper.registerBuiltinResourcePack(Identifier.of(Main.MOD_ID, "secretlife"), container, Text.translatable("Secret Life Resourcepack"), ResourcePackActivationType.NORMAL);
        });

        //PolymerResourcePackUtils.markAsRequired();
    }
}
