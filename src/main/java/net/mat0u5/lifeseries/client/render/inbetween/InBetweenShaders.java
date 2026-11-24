package net.mat0u5.lifeseries.client.render.inbetween;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

//? if <= 1.21 {
/*import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.shaders.BlendMode;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
*///?} else if <= 1.21.4 {
/*import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.client.renderer.ShaderProgram;
*///?}
//? if <= 1.21.4 {
/*import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.VertexFormat;
*///?} else {
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
//?}
//? if >= 1.21.5 <= 1.21.9 {
/*import com.mojang.blaze3d.textures.AddressMode;
import net.minecraft.client.renderer.RenderStateShard;
*///?}
//? if >= 1.21.5 <= 26.1 {
/*import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.VertexFormat;
*///?}
//? if >= 1.21.5 <= 26.2 {
/*import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
*///?}
//? if = 1.21.5 {
/*import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
*///?}
//? if >= 1.21.5 <= 1.21.11 {
//import com.mojang.blaze3d.platform.DepthTestFunction;
//?}
//? if >= 1.21.6
import net.minecraft.client.renderer.texture.AbstractTexture;
//? if >= 1.21.11 {
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.TextureTransform;
//?}
//? if >= 26.1 <= 26.2 {
/*import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.platform.CompareOp;
*///?}
//? if = 26.2
//import com.mojang.blaze3d.PrimitiveTopology;
//? if >= 26.2
import net.minecraft.client.renderer.BindGroupLayouts;
//? if >= 26.3 {
import com.mojang.renderpearl.api.commands.CommandEncoder;
import com.mojang.renderpearl.api.device.GpuDevice;
import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.CompareOp;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.textures.GpuTexture;
import com.mojang.renderpearl.frontend.FrontendCommandEncoder;
import net.minecraft.client.renderer.oit.OitPipelineSet;
//?}

public class InBetweenShaders {
	public static final Identifier NOISE_TEXTURE = IdentifierHelper.lifeseries("textures/effect/in_between_noise.png");
	private static final Identifier SCENE_TEXTURE = IdentifierHelper.lifeseries("dynamic/in_between_scene");
	private static final Identifier DEPTH_TEXTURE = IdentifierHelper.lifeseries("dynamic/in_between_depth");
	private static final int BUFFER_SIZE = 1536;
	private static final Set<Object> failedShaders = new HashSet<>();
	private static final Map<Identifier, RenderType> glowTypes = new HashMap<>();

	private static Identifier id(String path) {
		return IdentifierHelper.lifeseries(path);
	}

	public static void onShadersReloaded() {
		failedShaders.clear();
	}

	private static boolean markFailed(Object shader, @Nullable Exception e) {
		if (failedShaders.add(shader)) LifeSeries.LOGGER.error("[In-Between] Shader {} failed, that effect is turned off.", shader, e);
		return false;
	}

	//? if <= 1.21 {
	/*private static final Shader RAYS = new Shader("in_between_rays", Blend.PREMULTIPLIED, true);
	private static final Shader FOG = new Shader("in_between_fog", Blend.TRANSLUCENT, true);
	private static final Shader FOG_DEPTH = new Shader("in_between_fog", Blend.PREMULTIPLIED, false);
	private static final Shader GLOW = new Shader("in_between_glow", Blend.ADDITIVE, true);
	private static final Shader SCREEN = new Shader("in_between_screen", Blend.TRANSLUCENT, false);
	private static final Shader SPOTLIGHT = new Shader("in_between_spotlight", Blend.ADDITIVE, true);
	private static final BlendMode OPAQUE_BLEND = new BlendMode();

	private static class Shader {
		private final String name;
		private final Blend blend;
		private final boolean depthTest;
		@Nullable
		private ShaderInstance instance;

		private Shader(String name, Blend blend, boolean depthTest) {
			this.name = name;
			this.blend = blend;
			this.depthTest = depthTest;
		}

		private void reload(ResourceProvider resources) {
			if (instance != null) instance.close();
			instance = null;
			String prefix = "shaders/core/lifeseries_" + name + ".";
			ResourceProvider provider = location -> {
				if (!location.getNamespace().equals("minecraft") || !location.getPath().startsWith(prefix)) return resources.getResource(location);
				String extension = location.getPath().substring(prefix.length());
				Optional<Resource> resource = resources.getResource(id("shaders/core/" + name + "." + extension));
				if (resource.isEmpty() || !extension.equals("json")) return resource;
				return Optional.of(new Resource(resource.get().source(), () -> {
					try (BufferedReader reader = resource.get().openAsReader()) {
						JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
						json.addProperty("vertex", "lifeseries_" + name);
						json.addProperty("fragment", "lifeseries_" + name);
						return new ByteArrayInputStream(json.toString().getBytes(StandardCharsets.UTF_8));
					}
				}));
			};
			try {
				instance = new ShaderInstance(provider, "lifeseries_" + name, DefaultVertexFormat.POSITION_TEX_COLOR);
			} catch (Exception e) {
				markFailed(name, e);
			}
		}
	}

	public static void reloadShaders(ResourceProvider resources) {
		failedShaders.clear();
		for (Shader shader : List.of(RAYS, FOG, FOG_DEPTH, GLOW, SCREEN, SPOTLIGHT)) {
			shader.reload(resources);
		}
	}

	private static boolean isAvailable(Shader shader) {
		return shader.instance != null && !failedShaders.contains(shader);
	}

	private static void setShader(Shader shader) {
		RenderSystem.setShader(() -> shader.instance);
	}
	*///?} else if <= 1.21.4 {
	/*private static final Shader RAYS = new Shader("in_between_rays", Blend.PREMULTIPLIED, true);
	private static final Shader FOG = new Shader("in_between_fog", Blend.TRANSLUCENT, true);
	private static final Shader FOG_DEPTH = new Shader("in_between_fog", Blend.PREMULTIPLIED, false);
	private static final Shader GLOW = new Shader("in_between_glow", Blend.ADDITIVE, true);
	private static final Shader SCREEN = new Shader("in_between_screen", Blend.TRANSLUCENT, false);
	private static final Shader SPOTLIGHT = new Shader("in_between_spotlight", Blend.ADDITIVE, true);

	private record Shader(ShaderProgram program, Blend blend, boolean depthTest) {
		private Shader(String name, Blend blend, boolean depthTest) {
			this(new ShaderProgram(id("core/" + name), DefaultVertexFormat.POSITION_TEX_COLOR, ShaderDefines.EMPTY), blend, depthTest);
		}
	}

	private static boolean isAvailable(Shader shader) {
		if (failedShaders.contains(shader)) return false;
		try {
			if (Minecraft.getInstance().getShaderManager().getProgram(shader.program()) != null) return true;
		} catch (Exception e) {
			return markFailed(shader, e);
		}
		return markFailed(shader, null);
	}

	private static void setShader(Shader shader) {
		RenderSystem.setShader(shader.program());
	}
	*///?} else {
	//? if <= 1.21.5 {
	//private static final BlendFunction PREMULTIPLIED = new BlendFunction(SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
	//?} else {
	private static final BlendFunction PREMULTIPLIED = BlendFunction.TRANSLUCENT_PREMULTIPLIED_ALPHA;
	//?}
	//? if >= 26.3 {
	private static final Map<RenderPipeline, OitPipelineSet> OIT = new HashMap<>();
	//?}

	private static final RenderPipeline RAYS = pipeline(builder("in_between_rays", true, 1), PREMULTIPLIED, true);
	private static final RenderPipeline FOG = pipeline(builder("in_between_fog", true, 1), BlendFunction.TRANSLUCENT, true);
	//? if <= 26.2 {
	//private static final RenderPipeline FOG_DEPTH = pipeline(builder("in_between_fog", true, 2).withLocation(id("pipeline/in_between_fog_depth")), PREMULTIPLIED, false);
	//?} else {
	private static final RenderPipeline FOG_DEPTH = pipeline(builder("in_between_fog", true, 2).withShaderDefine("LS_SCENE_DEPTH").withLocation(id("pipeline/in_between_fog_depth")), PREMULTIPLIED, false);
	//?}
	private static final RenderPipeline GLOW = pipeline(builder("in_between_glow", true, 1), BlendFunction.LIGHTNING, true);
	private static final RenderPipeline SCREEN = pipeline(builder("in_between_screen", false, 1), BlendFunction.TRANSLUCENT, false);
	private static final RenderPipeline SPOTLIGHT = pipeline(builder("in_between_spotlight", true, 1), BlendFunction.LIGHTNING, true);
	//? if >= 26.3 {
	static {
		OIT.put(RAYS, OitPipelineSet.builder("lifeseries_in_between_rays", builder("in_between_rays", true, 1)).build());
		OIT.put(FOG, OitPipelineSet.builder("lifeseries_in_between_fog", builder("in_between_fog", true, 1)).build());
		OIT.put(GLOW, OitPipelineSet.builder("lifeseries_in_between_glow", builder("in_between_glow", true, 1).withShaderDefine("OIT_ADDITIVE")).build());
		OIT.put(SPOTLIGHT, OitPipelineSet.builder("lifeseries_in_between_spotlight", builder("in_between_spotlight", true, 1).withShaderDefine("OIT_ADDITIVE")).build());
	}
	//?}

	private static RenderPipeline.Builder builder(String shader, boolean world, int samplers) {
		RenderPipeline.Builder builder = RenderPipeline.builder()
				.withLocation(id("pipeline/" + shader))
				.withVertexShader(id("core/" + shader))
				.withFragmentShader(id("core/" + shader))
				.withCull(false);
		//? if <= 1.21.5 {
		/*builder.withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS)
				.withUniform("ModelViewMat", UniformType.MATRIX4X4)
				.withUniform("ProjMat", UniformType.MATRIX4X4);
		if (world) {
			builder.withUniform("TextureMat", UniformType.MATRIX4X4)
					.withUniform("GameTime", UniformType.FLOAT)
					.withUniform("FogStart", UniformType.FLOAT)
					.withUniform("FogEnd", UniformType.FLOAT);
		}
		for (int i = 0; i < samplers; i++) builder.withSampler("Sampler" + i);
		*///?} else if <= 26.1 {
		/*builder.withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS)
				.withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
				.withUniform("Projection", UniformType.UNIFORM_BUFFER);
		if (world) {
			builder.withUniform("Fog", UniformType.UNIFORM_BUFFER)
					.withUniform("Globals", UniformType.UNIFORM_BUFFER);
		}
		for (int i = 0; i < samplers; i++) builder.withSampler("Sampler" + i);
		*///?} else if <= 26.2 {
		/*builder.withVertexBinding(0, DefaultVertexFormat.POSITION_TEX_COLOR)
				.withPrimitiveTopology(PrimitiveTopology.QUADS)
				.withBindGroupLayout(BindGroupLayouts.GLOBALS)
				.withBindGroupLayout(BindGroupLayouts.MATRICES_PROJECTION);
		if (world) builder.withBindGroupLayout(BindGroupLayouts.FOG);
		builder.withBindGroupLayout(samplers == 2 ? BindGroupLayouts.SAMPLER0_SAMPLER1 : BindGroupLayouts.SAMPLER0);
		*///?} else {
		builder.withVertexBinding(0, DefaultVertexFormat.POSITION_TEX_COLOR)
				.withPrimitiveTopology(PrimitiveTopology.QUADS)
				.withBindGroupLayout(BindGroupLayouts.GLOBALS)
				.withBindGroupLayout(BindGroupLayouts.PROJECTION)
				.withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS);
		if (world) builder.withBindGroupLayout(BindGroupLayouts.FOG);
		builder.withBindGroupLayout(samplers == 2 ? BindGroupLayouts.SAMPLER0_SAMPLER1 : BindGroupLayouts.SAMPLER0);
		//?}
		return builder;
	}

	private static RenderPipeline pipeline(RenderPipeline.Builder builder, BlendFunction blend, boolean depthTest) {
		//? if <= 1.21.11 {
		/*builder.withBlend(blend).withDepthWrite(false);
		if (!depthTest) builder.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST);
		*///?} else if <= 26.1 {
		/*builder.withColorTargetState(new ColorTargetState(blend));
		if (depthTest) builder.withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false));
		*///?} else {
		builder.withColorTargetState(new ColorTargetState(blend));
		if (depthTest) builder.withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false));
		//?}
		return builder.build();
	}

	private static boolean isAvailable(RenderPipeline pipeline) {
		if (failedShaders.contains(pipeline)) return false;
		try {
			//? if <= 26.2 {
			//if (RenderSystem.getDevice().precompilePipeline(pipeline).isValid()) return true;
			//?} else {
			OitPipelineSet oit = OIT.get(pipeline);
			if (RenderSystem.getCompiledPipelineNullable(pipeline) != null && (oit == null || (RenderSystem.getCompiledPipelineNullable(oit.depthBoundsPipeline()) != null
					&& RenderSystem.getCompiledPipelineNullable(oit.transmittancePipeline()) != null && RenderSystem.getCompiledPipelineNullable(oit.accumulatePipeline()) != null))) return true;
			//?}
		} catch (Exception e) {
			return markFailed(pipeline, e);
		}
		return markFailed(pipeline, null);
	}
	//?}

	public static boolean raysAvailable() {
		return isAvailable(RAYS);
	}

	public static boolean cloudsAvailable() {
		return isAvailable(FOG);
	}

	public static boolean glowAvailable() {
		return isAvailable(GLOW);
	}

	public static boolean screenAvailable() {
		return isAvailable(SCREEN);
	}

	public static boolean spotlightsAvailable() {
		return isAvailable(SPOTLIGHT);
	}

	public static boolean depthCloudsAvailable() {
		//? if >= 26.3
		if (Minecraft.getInstance().gameRenderer.useImprovedTransparency()) return false;
		if (!isAvailable(FOG_DEPTH)) return false;
		//? if <= 26.1 {
		/*return true;
		*///?} else {
		boolean copied = depthCopied;
		depthCopied = false;
		depthWanted = true;
		matchDepthCopy();
		return copied;
		//?}
	}

	public static RenderType rays(Identifier texture, Supplier<Matrix4f> frame) {
		return renderType("lifeseries_in_between_rays", RAYS, frame, texture);
	}

	public static RenderType clouds(Supplier<Matrix4f> parameters, boolean overScene) {
		if (!overScene) return renderType("lifeseries_in_between_clouds", FOG, parameters, NOISE_TEXTURE);
		//? if <= 26.1 {
		/*Supplier<Matrix4f> afterCopy = () -> {
			copySceneDepth();
			return parameters.get();
		};
		return renderType("lifeseries_in_between_clouds_depth", FOG_DEPTH, afterCopy, NOISE_TEXTURE, DEPTH_TEXTURE);
		*///?} else {
		return renderType("lifeseries_in_between_clouds_depth", FOG_DEPTH, parameters, NOISE_TEXTURE, DEPTH_TEXTURE);
		//?}
	}

	public static RenderType glow(Identifier texture) {
		return glowTypes.computeIfAbsent(texture, id -> renderType("lifeseries_in_between_glow", GLOW, null, id));
	}

	public static RenderType spotlight(Supplier<Matrix4f> beam) {
		return renderType("lifeseries_in_between_spotlight", SPOTLIGHT, beam, NOISE_TEXTURE);
	}

	//? if <= 1.21.4 {
	/*private enum Blend {ADDITIVE, TRANSLUCENT, PREMULTIPLIED}

	private static RenderType renderType(String name, Shader shader, @Nullable Supplier<Matrix4f> parameters, Identifier... textures) {
		return customType(name, shader, () -> {
			for (int i = 0; i < textures.length; i++) bindTexture(i, textures[i]);
		}, parameters);
	}

	private static RenderType customType(String name, Shader shader, Runnable bindTextures, @Nullable Supplier<Matrix4f> parameters) {
		return new RenderType(name, DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, BUFFER_SIZE, false, false, () -> {
			if (parameters != null) RenderSystem.setTextureMatrix(parameters.get());
			setShader(shader);
			bindTextures.run();
			//? if <= 1.21
			//OPAQUE_BLEND.apply();
			RenderSystem.depthMask(false);
			RenderSystem.disableCull();
			RenderSystem.enableBlend();
			switch (shader.blend) {
				case ADDITIVE -> RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
				case PREMULTIPLIED -> RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				default -> RenderSystem.defaultBlendFunc();
			}
			if (shader.depthTest) {
				RenderSystem.enableDepthTest();
				RenderSystem.depthFunc(515);
			}
			else {
				RenderSystem.disableDepthTest();
			}
		}, () -> {
			RenderSystem.resetTextureMatrix();
			RenderSystem.depthMask(true);
			RenderSystem.enableCull();
			RenderSystem.enableDepthTest();
			RenderSystem.disableBlend();
			RenderSystem.defaultBlendFunc();
		}) {};
	}
	*///?} else if <= 1.21.9 {
	/*private static RenderType renderType(String name, RenderPipeline pipeline, @Nullable Supplier<Matrix4f> parameters, Identifier... textures) {
		return RenderType.create(name, BUFFER_SIZE, pipeline, RenderType.CompositeState.builder()
				.setTextureState(new RenderStateShard.EmptyTextureStateShard(() -> {
					if (parameters != null) RenderSystem.setTextureMatrix(parameters.get());
					for (int i = 0; i < textures.length; i++) bindTexture(i, textures[i]);
				}, RenderSystem::resetTextureMatrix))
				.createCompositeState(false));
	}
	*///?} else {
	private static RenderType renderType(String name, RenderPipeline pipeline, @Nullable Supplier<Matrix4f> parameters, Identifier... textures) {
		RenderSetup.RenderSetupBuilder setup = RenderSetup.builder(pipeline);
		for (int i = 0; i < textures.length; i++) {
			setup.withTexture("Sampler" + i, textures[i]);
		}
		if (parameters != null) setup.setTextureTransform(new TextureTransform(name, () -> new Matrix4f(parameters.get())));
		//? if >= 26.3
		if (OIT.containsKey(pipeline)) setup.setOitPipelines(OIT.get(pipeline));
		return RenderType.create(name, setup.createRenderSetup());
	}
	//?}

	//? if <= 1.21.9 {
	/*private static void bindTexture(int sampler, Identifier texture) {
		//? if <= 1.21.4 {
		/^if (texture != DEPTH_TEXTURE) RenderSystem.setShaderTexture(sampler, texture);
		else if (depthTarget != null) RenderSystem.setShaderTexture(sampler, depthTarget.getDepthTextureId());
		^///?} else if <= 1.21.5 {
		/^if (texture != DEPTH_TEXTURE) RenderSystem.setShaderTexture(sampler, Minecraft.getInstance().getTextureManager().getTexture(texture).getTexture());
		else if (depthTexture != null) RenderSystem.setShaderTexture(sampler, depthTexture);
		^///?} else {
		RenderSystem.setShaderTexture(sampler, Minecraft.getInstance().getTextureManager().getTexture(texture).getTextureView());
		//?}
	}
	*///?}

	private static RenderTarget mainTarget() {
		//? if <= 26.1 {
		//return Minecraft.getInstance().getMainRenderTarget();
		//?} else {
		return Minecraft.getInstance().gameRenderer.mainRenderTarget();
		//?}
	}

	//? if >= 1.21.6 {
	private static final TargetCopy sceneCopy = new TargetCopy(SCENE_TEXTURE, "Life Series In-Between transition", GpuTexture.USAGE_COPY_DST | GpuTexture.USAGE_TEXTURE_BINDING, FilterMode.LINEAR);
	private static boolean sceneCopyRequested = false;

	private static class TargetCopy extends AbstractTexture {
		private final Identifier id;
		private final String label;
		private final int usage;
		private final FilterMode filter;
		private boolean registered = false;

		private TargetCopy(Identifier id, String label, int usage, FilterMode filter) {
			this.id = id;
			this.label = label;
			this.usage = usage;
			this.filter = filter;
		}

		private boolean matches(GpuTexture source, int width, int height) {
			return texture != null && texture.getWidth(0) == width && texture.getHeight(0) == height && texture.getFormat() == source.getFormat();
		}

		private void match(GpuTexture source, int width, int height) {
			if (!registered) {
				Minecraft.getInstance().getTextureManager().register(id, this);
				registered = true;
			}
			if (matches(source, width, height)) return;
			if (textureView != null) textureView.close();
			if (texture != null) texture.close();
			GpuDevice device = RenderSystem.getDevice();
			texture = device.createTexture(() -> label, usage, source.getFormat(), width, height, 1, 1);
			textureView = device.createTextureView(texture);
			//? if <= 1.21.9 {
			/*texture.setAddressMode(AddressMode.CLAMP_TO_EDGE);
			texture.setTextureFilter(filter, false);
			*///?} else {
			sampler = RenderSystem.getSamplerCache().getClampToEdge(filter);
			//?}
		}
	}

	public static void drawScreen(GuiGraphicsExtractor graphics, int color, float u, float v) {
		RenderTarget target = mainTarget();
		if (target.getColorTexture() == null) return;
		sceneCopy.match(target.getColorTexture(), target.width, target.height);
		sceneCopyRequested = true;
		graphics.blit(SCREEN, SCENE_TEXTURE, 0, 0, u, v, graphics.guiWidth(), graphics.guiHeight(), 0, 0, 1, 1, color);
	}

	public static void copyScene() {
		if (!sceneCopyRequested) return;
		sceneCopyRequested = false;
		RenderTarget target = mainTarget();
		GpuTexture source = target.getColorTexture();
		if (source == null || !sceneCopy.matches(source, target.width, target.height)) return;
		RenderSystem.getDevice().createCommandEncoder().copyTextureToTexture(source, sceneCopy.getTexture(), 0, 0, 0, 0, 0, target.width, target.height);
	}
	//?} else {
	/*@Nullable
	private static RenderType screenType;

	public static void drawScreen(GuiGraphicsExtractor graphics, int color, float u, float v) {
		if (!copySceneNow()) return;
		int width = graphics.guiWidth();
		int height = graphics.guiHeight();
		//? if <= 1.21 {
		/^if (screenType == null) screenType = customType("lifeseries_in_between_screen", SCREEN, () -> RenderSystem.setShaderTexture(0, sceneTarget.getColorTextureId()), null);
		InBetweenMesh quad = new InBetweenMesh(screenType, 0, false);
		quad.add(0, 0, 0, u, v, color);
		quad.add(0, height, 0, u, v, color);
		quad.add(width, height, 0, u, v, color);
		quad.add(width, 0, 0, u, v, color);
		MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
		quad.render(graphics.pose().last().pose(), bufferSource.getBuffer(screenType));
		bufferSource.endBatch(screenType);
		^///?} else if <= 1.21.4 {
		/^if (screenType == null) screenType = customType("lifeseries_in_between_screen", SCREEN, () -> RenderSystem.setShaderTexture(0, sceneTarget.getColorTextureId()), null);
		graphics.blit(texture -> screenType, SCENE_TEXTURE, 0, 0, u, v, width, height, 0, 0, 1, 1, color);
		^///?} else {
		if (screenType == null) screenType = RenderType.create("lifeseries_in_between_screen", BUFFER_SIZE, SCREEN, RenderType.CompositeState.builder()
				.setTextureState(new RenderStateShard.EmptyTextureStateShard(() -> RenderSystem.setShaderTexture(0, sceneTexture), () -> {}))
				.createCompositeState(false));
		graphics.blit(texture -> screenType, SCENE_TEXTURE, 0, 0, u, v, width, height, 0, 0, 1, 1, color);
		//?}
	}
	*///?}

	//? if <= 1.21.4 {
	/*@Nullable
	private static TextureTarget sceneTarget;

	private static boolean copySceneNow() {
		RenderTarget main = mainTarget();
		sceneTarget = matchTarget(sceneTarget, main, false);
		GlStateManager._glBindFramebuffer(36008, main.frameBufferId); // READ_FRAMEBUFFER
		GlStateManager._glBindFramebuffer(36009, sceneTarget.frameBufferId); // DRAW_FRAMEBUFFER
		GlStateManager._glBlitFrameBuffer(0, 0, main.width, main.height, 0, 0, main.width, main.height, 16384, 9728); // COLOR_BUFFER_BIT, NEAREST
		main.bindWrite(false);
		return true;
	}

	private static TextureTarget matchTarget(@Nullable TextureTarget copy, RenderTarget main, boolean depth) {
		if (copy != null && copy.width == main.width && copy.height == main.height) return copy;
		if (copy != null) copy.destroyBuffers();
		//? if <= 1.21 {
		//copy = new TextureTarget(main.width, main.height, depth, Minecraft.ON_OSX);
		//?} else {
		copy = new TextureTarget(main.width, main.height, depth);
		//?}
		copy.setFilterMode(9729); // LINEAR
		return copy;
	}
	*///?} else if <= 1.21.5 {
	/*@Nullable
	private static GpuTexture sceneTexture;

	private static boolean copySceneNow() {
		RenderTarget target = mainTarget();
		GpuTexture source = target.getColorTexture();
		if (source == null) return false;
		sceneTexture = matchTexture(sceneTexture, source, target, "Life Series In-Between transition", FilterMode.LINEAR);
		RenderSystem.getDevice().createCommandEncoder().copyTextureToTexture(source, sceneTexture, 0, 0, 0, 0, 0, target.width, target.height);
		return true;
	}

	private static GpuTexture matchTexture(@Nullable GpuTexture copy, GpuTexture source, RenderTarget target, String label, FilterMode filter) {
		if (copy != null && copy.getWidth(0) == target.width && copy.getHeight(0) == target.height && copy.getFormat() == source.getFormat()) return copy;
		if (copy != null) copy.close();
		copy = RenderSystem.getDevice().createTexture(() -> label, source.getFormat(), target.width, target.height, 1);
		copy.setAddressMode(AddressMode.CLAMP_TO_EDGE);
		copy.setTextureFilter(filter, false);
		return copy;
	}
	*///?}

	//? if <= 1.21.4 {
	/*@Nullable
	private static TextureTarget depthTarget;
	*///?} else if <= 1.21.5 {
	/*@Nullable
	private static GpuTexture depthTexture;
	*///?} else {
	private static final TargetCopy depthCopy = new TargetCopy(DEPTH_TEXTURE, "Life Series In-Between depth", GpuTexture.USAGE_COPY_DST | GpuTexture.USAGE_TEXTURE_BINDING | GpuTexture.USAGE_RENDER_ATTACHMENT, FilterMode.NEAREST);
	//?}
	//? if >= 26.2 {
	private static boolean depthWanted = false;
	private static boolean depthCopied = false;

	private static void matchDepthCopy() {
		RenderTarget target = mainTarget();
		GpuTexture source = target.getDepthTexture();
		if (source != null) depthCopy.match(source, target.width, target.height);
	}
	//?}

	public static void copySceneDepth() {
		//? if >= 26.2 {
		if (!depthWanted) return;
		depthWanted = false;
		//?}
		try {
			RenderTarget target = mainTarget();
			//? if <= 1.21.4 {
			/*int readFramebuffer = GlStateManager._getInteger(36010); // READ_FRAMEBUFFER_BINDING
			int drawFramebuffer = GlStateManager._getInteger(36006); // DRAW_FRAMEBUFFER_BINDING
			depthTarget = matchTarget(depthTarget, target, true);
			depthTarget.copyDepthFrom(target);
			GlStateManager._glBindFramebuffer(36008, readFramebuffer); // READ_FRAMEBUFFER
			GlStateManager._glBindFramebuffer(36009, drawFramebuffer); // DRAW_FRAMEBUFFER
			*///?} else {
			GpuTexture source = target.getDepthTexture();
			if (source == null) return;
			//? if <= 1.21.5 {
			/*depthTexture = matchTexture(depthTexture, source, target, "Life Series In-Between depth", FilterMode.NEAREST);
			GpuTexture destination = depthTexture;
			*///?} else {
			depthCopy.match(source, target.width, target.height);
			GpuTexture destination = depthCopy.getTexture();
			//?}
			//? if <= 26.2 {
			//RenderSystem.getDevice().createCommandEncoder().copyTextureToTexture(source, destination, 0, 0, 0, 0, 0, target.width, target.height);
			//?} else {
			CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
			if (encoder instanceof FrontendCommandEncoder frontend) {
				frontend.backend().copyTextureToTexture(source, destination, 0, 0, 0, 0, 0, target.width, target.height);
			}
			else {
				encoder.copyTextureToTexture(source, destination, 0, 0, 0, 0, 0, target.width, target.height);
			}
			//?}
			//?}
			//? if >= 26.2
			depthCopied = true;
		} catch (Exception e) {
			markFailed(FOG_DEPTH, e);
		}
	}

	// Pixel as 0xAARRGGBB
	public static int getPixel(NativeImage image, int x, int y) {
		//? if <= 1.21 {
		//return swapRedBlue(image.getPixelRGBA(x, y));
		//?} else {
		return image.getPixel(x, y);
		//?}
	}

	public static void setPixel(NativeImage image, int x, int y, int argb) {
		//? if <= 1.21 {
		//image.setPixelRGBA(x, y, swapRedBlue(argb));
		//?} else {
		image.setPixel(x, y, argb);
		//?}
	}

	// 0xAABBGGRR <-> 0xAARRGGBB
	private static int swapRedBlue(int color) {
		return (color & 0xFF00FF00) | ((color >> 16) & 0xFF) | ((color & 0xFF) << 16);
	}

	@Nullable
	public static NativeImage getPixels(Identifier texture) {
		if (Minecraft.getInstance().getTextureManager().getTexture(texture) instanceof DynamicTexture dynamic) return dynamic.getPixels();
		return null;
	}

	public static void registerLinearTexture(Identifier id, String label, NativeImage image) {
		Minecraft.getInstance().getTextureManager().register(id, new LinearTexture(label, image));
	}

	private static class LinearTexture extends DynamicTexture {
		//? if <= 1.21.9 {
		/*LinearTexture(String label, NativeImage image) {
			//? if <= 1.21.4 {
			//super(image);
			//?} else {
			super(() -> label, image);
			//?}
			setFilter(true, false);
			// Repeating would blend each edge with the opposite one
			//? if <= 1.21.2 {
			/^GlStateManager._texParameter(3553, 10242, 33071); // WRAP_S, CLAMP_TO_EDGE
			GlStateManager._texParameter(3553, 10243, 33071); // WRAP_T
			^///?} else {
			setClamp(true);
			//?}
		}

		@Override
		public void setFilter(boolean blur, boolean mipmap) {
			super.setFilter(true, false);
		}
		*///?} else {
		LinearTexture(String label, NativeImage image) {
			super(() -> label, image);
			this.sampler = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR);
		}
		//?}
	}
}
