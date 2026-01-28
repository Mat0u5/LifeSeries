package net.mat0u5.lifeseries.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//? if >= 1.21.4 {
//? if <= 1.21.9 {
/*import net.minecraft.client.renderer.RenderType;
*///?} else if <= 1.21.11 {
import net.minecraft.client.renderer.rendertype.RenderType;
//?}
//?}

//? if <= 1.21.9 {
/*import net.minecraft.resources.ResourceLocation;
*///?} else {
import net.minecraft.resources.Identifier;
//?}

//? if <= 1.21 {
/*import net.minecraft.client.renderer.LevelRenderer;
@Mixin(value = LevelRenderer.class)
*///?} else {
import net.minecraft.client.renderer.WeatherEffectRenderer;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = WeatherEffectRenderer.class)
//?}
public class WeatherEffectRendererMixin {
    //? if <= 1.21.9 {
    /*private static ResourceLocation LESS_SNOW_RESOURCE_LOCATION = IdentifierHelper.mod("textures/environment/less-snow.png");
    *///?} else {
    private static Identifier LESS_SNOW_RESOURCE_LOCATION = IdentifierHelper.mod("textures/environment/less-snow.png");
    //?}

    //? if <= 1.21 {
    /*@WrapOperation(method = "renderSnowAndRain", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V"))
    public void render(int i, ResourceLocation resourceLocation, Operation<Void> original) {
    *///?} else if <= 1.21.2 {
    /*@WrapOperation(method = "render(Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/world/phys/Vec3;IFLjava/util/List;Ljava/util/List;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V"))
    public void render(int i, ResourceLocation resourceLocation, Operation<Void> original) {
    *///?} else if <= 1.21.6 {
    /*@WrapOperation(method = "render(Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/phys/Vec3;IFLjava/util/List;Ljava/util/List;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;weather(Lnet/minecraft/resources/ResourceLocation;Z)Lnet/minecraft/client/renderer/RenderType;"))
    public RenderType render(ResourceLocation resourceLocation, boolean bl, Operation<RenderType> original) {
    *///?} else if <= 1.21.9 {
    /*@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;weather(Lnet/minecraft/resources/ResourceLocation;Z)Lnet/minecraft/client/renderer/RenderType;"))
    public RenderType render(ResourceLocation resourceLocation, boolean bl, Operation<RenderType> original) {
    *///?} else if <= 1.21.11 {
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;weather(Lnet/minecraft/resources/Identifier;Z)Lnet/minecraft/client/renderer/rendertype/RenderType;"))
    public RenderType render(Identifier resourceLocation, boolean bl, Operation<RenderType> original) {
    //?} else {
    /*@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureManager;getTexture(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/client/renderer/texture/AbstractTexture;"))
    public Identifier render(Identifier resourceLocation) {
    *///?}
        if (MainClient.NICE_LIFE_LESS_SNOW && !Main.modDisabled() && MainClient.clientCurrentSeason == Seasons.NICE_LIFE) {
            if (resourceLocation.getPath().contains("snow.png")) {
                //? if <= 1.21.2 {
                /*original.call(i, LESS_SNOW_RESOURCE_LOCATION);
                return;
                *///?} else if <= 1.21.11 {
                return original.call(LESS_SNOW_RESOURCE_LOCATION, bl);
                //?} else {
                /*return LESS_SNOW_RESOURCE_LOCATION;
                *///?}
            }
        }
        //? if <= 1.21.2 {
        /*original.call(i, resourceLocation);
        *///?} else if <= 1.21.11 {
        return original.call(resourceLocation, bl);
        //?} else {
        /*return resourceLocation;
        *///?}
    }

    //? if <= 1.21 {
    /*@WrapOperation(method = "renderSnowAndRain", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getRainLevel(F)F"))
    public float renderRain(ClientLevel instance, float v, Operation<Float> original) {
    *///?} else if <= 1.21.9 {
    /*@WrapOperation(method = "tickRainParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getRainLevel(F)F"))
    public float renderRain(ClientLevel instance, float v, Operation<Float> original) {
    *///?} else {
    @WrapOperation(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getRainLevel(F)F"))
    public float renderRain(Level instance, float v, Operation<Float> original) {
    //?}
        if (!Main.modDisabled() && MainClient.clientCurrentSeason == Seasons.NICE_LIFE) {
            return 1;
        }
        return original.call(instance, v);
    }
}
