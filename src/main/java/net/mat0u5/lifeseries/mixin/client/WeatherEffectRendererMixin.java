package net.mat0u5.lifeseries.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.client.LifeSeriesClient;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.resources.Identifier;

//? if <= 26.1 {
/*import net.minecraft.world.level.Level;
*///?} else {
import net.minecraft.client.multiplayer.ClientLevel;
//?}

//? if >= 26.3 {
/*import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
*///?}

//? if >= 1.21.4 <= 1.21.11 {
/*import net.minecraft.client.renderer.rendertype.RenderType;
*///?}
//? if <= 1.21.9
//import net.minecraft.client.multiplayer.ClientLevel;

//? if <= 1.21 {
/*import net.minecraft.client.renderer.LevelRenderer;
@Mixin(value = LevelRenderer.class)
*///?} else {
import net.minecraft.client.renderer.WeatherEffectRenderer;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = WeatherEffectRenderer.class)
//?}
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public class WeatherEffectRendererMixin {
    private static Identifier LESS_SNOW_RESOURCE_LOCATION = IdentifierHelper.mod("textures/environment/less-snow.png");
    //? if >= 26.3 {
    /*private static AbstractTexture LESS_SNOW_RESOURCE;

    @Inject(method = "<init>", at = @At("HEAD"))
    private static void snowTexture(CallbackInfo ci) {
        LESS_SNOW_RESOURCE = Minecraft.getInstance().getTextureManager().getTexture(LESS_SNOW_RESOURCE_LOCATION);
    }
    *///?}

//? if fabric || forge {
    //? if <= 1.21 {
    /*@WrapOperation(method = "renderSnowAndRain", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/Identifier;)V"))
    public void render(int i, Identifier resourceLocation, Operation<Void> original) {
    *///?} else if <= 1.21.2 {
    /*@WrapOperation(method = "render(Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/world/phys/Vec3;IFLjava/util/List;Ljava/util/List;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/Identifier;)V"))
    public void render(int i, Identifier resourceLocation, Operation<Void> original) {
    *///?} else if <= 1.21.6 {
    /*@WrapOperation(method = "render(Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/phys/Vec3;IFLjava/util/List;Ljava/util/List;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;weather(Lnet/minecraft/resources/Identifier;Z)Lnet/minecraft/client/renderer/RenderType;"))
    public RenderType render(Identifier resourceLocation, boolean bl, Operation<RenderType> original) {
    *///?} else if <= 1.21.9 {
    /*@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;weather(Lnet/minecraft/resources/Identifier;Z)Lnet/minecraft/client/renderer/RenderType;"))
    public RenderType render(Identifier resourceLocation, boolean bl, Operation<RenderType> original) {
    *///?} else if <= 1.21.11 {
    /*@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;weather(Lnet/minecraft/resources/Identifier;Z)Lnet/minecraft/client/renderer/rendertype/RenderType;"))
    public RenderType render(Identifier resourceLocation, boolean bl, Operation<RenderType> original) {
    *///?} else if <= 26.2 {
    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureManager;getTexture(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/client/renderer/texture/AbstractTexture;"))
    public Identifier render(Identifier resourceLocation) {
    //?} else {
    /*@ModifyArg(method = "render(Lnet/minecraft/client/renderer/state/level/WeatherRenderState;Lcom/mojang/renderpearl/api/commands/RenderPass;Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WeatherEffectRenderer;renderWeather(Lcom/mojang/renderpearl/api/commands/RenderPass;Lnet/minecraft/client/renderer/texture/AbstractTexture;II)V"), index = 1)
    public AbstractTexture render(AbstractTexture texture) {
    *///?}
//?} else {
    /*//? if <= 1.21 {
    /^@WrapOperation(method = "renderSnowAndRain", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/Identifier;)V"))
    public void render(int i, Identifier resourceLocation, Operation<Void> original) {
    ^///?} else if <= 1.21.2 {
    /^@WrapOperation(method = "render(Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/world/phys/Vec3;IFLjava/util/List;Ljava/util/List;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/Identifier;)V"))
    public void render(int i, Identifier resourceLocation, Operation<Void> original) {
    ^///?} else if <= 1.21.6 {
    /^@WrapOperation(method = "render(Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/phys/Vec3;IFLjava/util/List;Ljava/util/List;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;weather(Lnet/minecraft/resources/Identifier;Z)Lnet/minecraft/client/renderer/RenderType;"))
    public RenderType render(Identifier resourceLocation, boolean bl, Operation<RenderType> original) {
    ^///?} else if <= 1.21.9 {
    /^@WrapOperation(method = "render(Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/state/WeatherRenderState;Lnet/minecraft/client/renderer/state/LevelRenderState;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;weather(Lnet/minecraft/resources/Identifier;Z)Lnet/minecraft/client/renderer/RenderType;"))
    public RenderType render(Identifier resourceLocation, boolean bl, Operation<RenderType> original) {
    ^///?} else if <= 1.21.11 {
    /^@WrapOperation(method = "render(Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/state/WeatherRenderState;Lnet/minecraft/client/renderer/state/LevelRenderState;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;weather(Lnet/minecraft/resources/Identifier;Z)Lnet/minecraft/client/renderer/rendertype/RenderType;"))
    public RenderType render(Identifier resourceLocation, boolean bl, Operation<RenderType> original) {
    ^///?} else {
    @ModifyArg(method = "render(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/state/level/WeatherRenderState;Lnet/minecraft/client/renderer/state/level/LevelRenderState;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureManager;getTexture(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/client/renderer/texture/AbstractTexture;"))
    public Identifier render(Identifier resourceLocation) {
    //?}
*///?}
        if (LifeSeriesClient.NICE_LIFE_LESS_SNOW && !LifeSeries.modDisabled() && LifeSeries.isSeason(Seasons.NICE_LIFE)) {
            //? if <= 26.2 {
            boolean isSnow = resourceLocation.getPath().contains("snow.png");
            //?} else {
            /*boolean isSnow = (texture instanceof SimpleTexture st) && st.resourceId().getPath().contains("snow.png");
            *///?}
            if (isSnow) {
                //? if <= 1.21.2 {
                /*original.call(i, LESS_SNOW_RESOURCE_LOCATION);
                return;
                *///?} else if <= 1.21.11 {
                /*return original.call(LESS_SNOW_RESOURCE_LOCATION, bl);
                *///?} else if <= 26.2 {
                return LESS_SNOW_RESOURCE_LOCATION;
                //?} else {
                /*return LESS_SNOW_RESOURCE;
                *///?}
            }
        }
        //? if <= 1.21.2 {
        /*original.call(i, resourceLocation);
        *///?} else if <= 1.21.11 {
        /*return original.call(resourceLocation, bl);
        *///?} else if <= 26.2 {
        return resourceLocation;
        //?} else {
        /*return texture;
        *///?}
    }

    //? if <= 1.21 {
    /*@WrapOperation(method = "renderSnowAndRain", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getRainLevel(F)F"))
    public float renderRain(ClientLevel instance, float v, Operation<Float> original) {
    *///?} else if <= 1.21.9 {
    /*@WrapOperation(method = "tickRainParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getRainLevel(F)F"))
    public float renderRain(ClientLevel instance, float v, Operation<Float> original) {
    *///?} else if <= 26.1 {
    /*@WrapOperation(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getRainLevel(F)F"))
    public float renderRain(Level instance, float v, Operation<Float> original) {
    *///?} else {
    @WrapOperation(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getRainLevel(F)F"))
    public float renderRain(ClientLevel instance, float v, Operation<Float> original) {
    //?}
        if (!LifeSeries.modDisabled() && LifeSeries.isSeason(Seasons.NICE_LIFE)) {
            return 1;
        }
        return original.call(instance, v);
    }
}
