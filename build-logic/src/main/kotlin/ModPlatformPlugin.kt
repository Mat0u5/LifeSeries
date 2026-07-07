@file:Suppress("unused", "DuplicatedCode")

import dev.kikugie.fletching_table.extension.FletchingTableExtension
import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import me.modmuss50.mpp.ModPublishExtension
import me.modmuss50.mpp.ReleaseType
import org.gradle.api.JavaVersion
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Copy
import org.gradle.internal.extensions.stdlib.toDefaultLowerCase
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.*
import org.gradle.language.jvm.tasks.ProcessResources
import org.gradle.plugins.ide.idea.model.IdeaModel
import java.util.*
import javax.inject.Inject

fun Project.prop(name: String): String = (findProperty(name) ?: "") as String

fun Project.env(variable: String): String? {
	var value = providers.environmentVariable(variable).orNull
	if (value != null) return value

	val envFile = rootProject.file(".env")
	if (envFile.exists()) {
		val props = java.util.Properties()
		envFile.inputStream().use { props.load(it) }
		value = props.getProperty(variable)
		if (value != null) return value
	}

	return findProperty(variable) as? String
}

fun Project.envTrue(variable: String): Boolean = env(variable)?.toDefaultLowerCase() == "true"

fun RepositoryHandler.strictMaven(
	url: String, vararg groups: String, configure: MavenArtifactRepository.() -> Unit = {}
) = exclusiveContent {
	forRepository { maven(url) { configure() } }
	filter { groups.forEach(::includeGroup) }
}

abstract class ModPlatformPlugin @Inject constructor() : Plugin<Project> {
	override fun apply(project: Project) = with(project) {
		val inferredLoader = project.buildFile.name.substringAfter('.').replace(".gradle.kts", "")
		val inferredLoaderIsFabric = inferredLoader == "fabric-legacy"
		val inferredLoaderIsForge = inferredLoader == "forge"

		val extension = extensions.create("platform", ModPlatformExtension::class.java).apply {
			loader.convention(inferredLoader)
			jarTask.convention(
				when {
					inferredLoaderIsFabric -> "remapJar"
					inferredLoaderIsForge -> "reobfJar"
					else -> "jar"
				}
			)
			sourcesJarTask.convention(if (inferredLoaderIsFabric) "remapSourcesJar" else "sourcesJar")
		}

		listOf(
			"org.jetbrains.kotlin.jvm",
			"com.google.devtools.ksp",
			"dev.kikugie.fletching-table"
		).forEach { apply(plugin = it) }

		afterEvaluate {
			configureProject(extension)
		}
	}

	private fun Project.configureProject(extension: ModPlatformExtension) {
		val loader = extension.loader.get()
		val isFabric = loader == "fabric"
		val isNeoForge = loader == "neoforge"
		val isForge = loader == "forge"

		val modId = prop("mod.id")
		val modVersion = prop("mod.version_prefix")+prop("mod.version")+prop("mod.version_suffix")
		val mcVersion = prop("deps.minecraft")
		var mcRange = prop("mod.mc_range").ifBlank { "[$mcVersion]" }
		if (env("BUILD_UNBOUND_VERSION_RANGE") == "true") {
			mcRange = "*"
		}

		val stonecutter = extensions.getByType<StonecutterBuildExtension>()
		configureStonecutterReplacements(stonecutter)

		listOf(
			"java",
			"me.modmuss50.mod-publish-plugin",
			"idea",
		).forEach { apply(plugin = it) }

		extension.requiredJava.set(
			when {
				stonecutter.eval(stonecutter.current.version, ">=26") -> JavaVersion.VERSION_25
				stonecutter.eval(stonecutter.current.version, ">=1.20.5") -> JavaVersion.VERSION_21
				stonecutter.eval(stonecutter.current.version, ">=1.18") -> JavaVersion.VERSION_17
				stonecutter.eval(stonecutter.current.version, ">=1.17") -> JavaVersion.VERSION_16
				else -> JavaVersion.VERSION_1_8
			}
		)

		val fullVersion = "$modVersion+$mcVersion-$loader"
		val publishDisplayVersion = "$loader-$modVersion+$mcVersion"
		version = fullVersion

		extension.dependencies {
			required.maybeCreate("minecraft").apply {
				modid.set("minecraft")
				versionRange.set(mcRange)
				forgeVersionRange.set(mcRange)
			}

			if (isFabric) {
				required.maybeCreate("java").apply {
					modid.set("java")
					versionRange.set(">=${extension.requiredJava.get().majorVersion}")
				}

				required.maybeCreate("fabricloader").apply {
					modid.set("fabricloader")
					versionRange.set(">=0.18.0")
				}
			}
		}

		configureFletchingTable()
		configureJarTask(modId, loader)
		configureIdea()
		configureProcessResources(
			isFabric,
			isNeoForge,
			isForge,
			modId,
			modVersion,
			mcVersion,
			extension,
			extension.requiredJava.get(),
			stonecutter
		)
		configureJava(stonecutter, extension.requiredJava.get())
		registerBuildAndCollectTask(extension, modVersion)
		configurePublishing(extension, loader, stonecutter, modVersion, publishDisplayVersion)
	}

	private fun Project.configureJarTask(modId: String, loader: String) {
		val isForge = loader == "forge"

		tasks.withType<Jar>().configureEach {
			archiveBaseName.set(modId)
			if (isForge) {
				manifest.attributes(
					"MixinConfigs" to "${modId}.mixins.json"
				)
			}
		}
	}

	private fun Project.configureProcessResources(
		isFabric: Boolean,
		isNeoForge: Boolean,
		isForge: Boolean,
		modId: String,
		modVersion: String,
		mcVersion: String,
		extension: ModPlatformExtension,
		requiredJava: JavaVersion,
		stonecutter: StonecutterBuildExtension
	) {
		tasks.named<ProcessResources>("processResources") {
			dependsOn(tasks.named("stonecutterGenerate"))
			dependsOn("kspKotlin")

			filesMatching("*.mixins.json") {
				val needsRefmap = isForge && stonecutter.eval(stonecutter.current.version, "<=1.20") // legacyForge
				if (!needsRefmap) {
					filter { line: String ->
						if (line.trimStart().startsWith("\"refmap\"")) null else line
					}
				}

				val mixinJava = if (isForge && requiredJava > JavaVersion.VERSION_17) {
					"JAVA_17"
				} else {
					"JAVA_${requiredJava.majorVersion}"
				}

				expand(
					"java" to mixinJava,
					"id" to modId
				)
			}

			var contributors = prop("mod.contributors")
			var authors = prop("mod.authors")
			var issuesUrl = prop("mod.issues_url")
			if (issuesUrl == "") issuesUrl = prop("mod.sources_url") + "/issues"

			if (isFabric) {
				contributors = contributors.replace(", ", "\", \"")
				authors = authors.replace(", ", "\", \"")
			}

			val dependencies = buildDependenciesBlock(isFabric, modId, extension.dependencies)

			val props = mapOf(
				"version" to modVersion,
				"minecraft" to mcVersion,
				"sc_version" to stonecutter.current.version,
				"id" to modId,
				"name" to prop("mod.name"),
				"group" to prop("mod.group"),
				"authors" to authors,
				"contributors" to contributors,
				"license" to prop("mod.license"),
				"description" to prop("mod.description"),
				"issues_url" to issuesUrl,
				"homepage_url" to prop("mod.homepage_url"),
				"sources_url" to prop("mod.sources_url"),
				"discord_url" to prop("mod.discord_url"),
				"dependencies" to dependencies
			)

			when {
				isFabric -> {
					filesMatching("fabric.mod.json") {
						filter { line ->
							if (line.trim() == "\"depends\": {}") "  \"depends\": {$dependencies\n  }" else line
						}
						expand(props.filterKeys { it != "dependencies" })
					}
					exclude("META-INF/mods.toml", "META-INF/neoforge.mods.toml", "aw/*.cfg", ".cache", "pack.mcmeta")
				}

				isNeoForge -> {
					val usesLegacyToml = stonecutter.eval(stonecutter.current.version, "<=1.20.3")
					if (usesLegacyToml) {
						filesMatching("META-INF/mods.toml") { expand(props) }
						exclude("META-INF/neoforge.mods.toml", "fabric.mod.json", "aw/*.accesswidener", "aw/*.classtweaker", ".cache", "pack.mcmeta")
					} else {
						filesMatching("META-INF/neoforge.mods.toml") { expand(props) }
						exclude("META-INF/mods.toml", "fabric.mod.json", "aw/*.accesswidener", "aw/*.classtweaker", ".cache", "pack.mcmeta")
					}
				}

				isForge -> {
					filesMatching("META-INF/mods.toml") { expand(props) }
					exclude("META-INF/neoforge.mods.toml", "fabric.mod.json", "aw/*.accesswidener", "aw/*.classtweaker", ".cache")
				}
			}
		}
	}

	private fun buildDependenciesBlock(
		isFabric: Boolean, modId: String, deps: DependenciesConfig
	): String = if (isFabric) {
		buildString {
			fun joinEntries(container: NamedDomainObjectContainer<Dependency>): List<String> =
				container.map { "    \"${it.modid.get()}\": \"${it.versionRange.get()}\"" }

			val entries = joinEntries(deps.required) + joinEntries(deps.optional)
			if (entries.isNotEmpty()) append("\n" + entries.joinToString(",\n"))
		}
	} else {
		buildString {
			fun appendBlock(container: NamedDomainObjectContainer<Dependency>, type: String) {
				container.forEach {
					appendLine(
						"""

						[[dependencies.$modId]]
						modId = "${it.modid.get()}"
						side = "${it.environment.get().uppercase(Locale.getDefault())}"
                        versionRange = "${it.forgeVersionRange.get()}"
						mandatory = ${if (type == "required") "true" else "false"}
                        type = "$type"
						""".replace("                  ", "").trimIndent()
					)
				}
			}

			appendBlock(deps.required, "required")
			appendBlock(deps.optional, "optional")
			appendBlock(deps.incompatible, "incompatible")
		}
	}

	private fun Project.configureJava(stonecutter: StonecutterBuildExtension, requiredJava: JavaVersion) {
		extensions.configure<JavaPluginExtension>("java") {
			//withSourcesJar()
			//withJavadocJar()
			sourceCompatibility = requiredJava
			targetCompatibility = requiredJava
		}
	}

	private fun Project.configureIdea() {
		extensions.configure<IdeaModel>("idea") {
			module {
				isDownloadJavadoc = true
				isDownloadSources = true
			}
		}
	}

	private fun Project.configureFletchingTable() {
		extensions.configure<FletchingTableExtension> {
			mixins.create("main").apply {
				mixin("default", "${prop("mod.id")}.mixins.json")
			}
		}
	}

	private fun Project.registerBuildAndCollectTask(extension: ModPlatformExtension, modVersion: String) {
		tasks.register<Copy>("buildAndCollect") {
			group = "build"
			from(
				tasks.named(extension.jarTask.get())
			)
			into(rootProject.file("output/$modVersion"))
			dependsOn("build")
		}
	}

	private fun Project.configurePublishing(
		ext: ModPlatformExtension,
		loader: String,
		stonecutter: StonecutterBuildExtension,
		modVersion: String,
		displayVersion: String,
	) {
		val additionalVersions = (findProperty("publish.additionalVersions") as String?)?.split(',')?.map(String::trim)
			?.filter(String::isNotEmpty).orEmpty()

		val releaseTypeRaw = prop("publish.release.$loader").ifBlank { "stable" }
		val releaseType = ReleaseType.of(
			releaseTypeRaw.let { if (it == "dev") "beta" else it }
		)

		extensions.configure<ModPublishExtension>("publishMods") {
			//val mrStaging = prop("publish.modrinth.staging") == "true"
			val mrStaging = false

			val modrinthAccessToken = env("MODRINTH_API_TOKEN")
			val curseforgeAccessToken = env("CURSEFORGE_API_TOKEN")
			val githubAccessToken = env("GITHUB_TOKEN")

			val modrinthProjectId = prop("publish.modrinth.id")
			val curseforgeProjectId = prop("publish.curseforge.id")

			val modrinthPublish = prop("publish.modrinth") == "true"
			val curseforgePublish = prop("publish.curseforge") == "true"
			val githubPublish = prop("publish.github") == "true"

			if (prop("publish.dryrun") == "true") {
				dryRun = true
			}

			val targetName = ext.jarTask.get()
			val jarTask = tasks.named(targetName).map { it as Jar }
			val currentVersion = prop("deps.minecraft")
			val deps = ext.dependencies

			file.set(jarTask.flatMap(Jar::getArchiveFile))
			type = releaseType
			version = displayVersion
			if (displayVersion.length > 32) {
				version = displayVersion.replace("snapshot", "snap").take(32)
			}
			val changelogFile = rootProject.file("CHANGELOG.md").readText()
			val changelogLink = prop("publish.changelog.link")
			changelog.set(changelogFile.replace("\n","\n\n")+"\n\n[Click here to open the **full changelog**]($changelogLink)")
			modLoaders.add(loader)
			if (loader == "fabric") {
				modLoaders.add("quilt")
			}
			if (loader == "forge" && stonecutter.current.version == "1.20" && additionalVersions.size <= 1) {
				modLoaders.add("neoforge")
			}

			val mcVersionRange = if (additionalVersions.isNotEmpty()) "$currentVersion-${additionalVersions.last()}" else currentVersion
			displayName = "${prop("mod.name")} $modVersion for ${loader.replaceFirstChar(Char::titlecase)} $mcVersionRange"

			if (modrinthPublish && !modrinthAccessToken.isNullOrBlank() && modrinthProjectId.isNotBlank()) {
				modrinth(deps, currentVersion, additionalVersions, mrStaging, modrinthAccessToken)
			}

			if (curseforgePublish && !curseforgeAccessToken.isNullOrBlank() && curseforgeProjectId.isNotBlank()) {
				if (!mrStaging) curseforge(deps, currentVersion, additionalVersions, false, curseforgeAccessToken)
			}

			if (githubPublish && !githubAccessToken.isNullOrBlank()) {
				github {
					accessToken = githubAccessToken
					parent(project(":").tasks.named("publishGithub"))
				}
			}
		}
	}

	fun whenNotNull(stringProp: Property<String>, action: (String) -> Unit) {
		if (!stringProp.orNull.isNullOrBlank()) action(stringProp.get())
	}

	private fun ModPublishExtension.modrinth(
		deps: DependenciesConfig,
		currentVersion: String,
		additionalVersions: List<String>,
		staging: Boolean,
		accessToken: String?
	) = modrinth {
		if (staging) apiEndpoint = "https://staging-api.modrinth.com/v2"
		projectId = project.prop("publish.modrinth.id")
		this.accessToken = accessToken
		minecraftVersions.addAll(listOf(currentVersion) + additionalVersions)

		if (!staging) {
			deps.required.forEach { dep -> whenNotNull(dep.modrinth) { requires(it) } }
			deps.optional.forEach { dep -> whenNotNull(dep.modrinth) { optional(it) } }
			deps.incompatible.forEach { dep -> whenNotNull(dep.modrinth) { incompatible(it) } }
			deps.embeds.forEach { dep -> whenNotNull(dep.modrinth) { embeds(it) } }
		}
	}

	private fun ModPublishExtension.curseforge(
		deps: DependenciesConfig,
		currentVersion: String,
		additionalVersions: List<String>,
		staging: Boolean,
		accessToken: String?
	) = curseforge {
		projectId = project.prop("publish.curseforge.id")
		this.accessToken = accessToken
		minecraftVersions.addAll(listOf(currentVersion) + additionalVersions)

		deps.required.forEach { dep -> whenNotNull(dep.curseforge) { requires(it) } }
		deps.optional.forEach { dep -> whenNotNull(dep.curseforge) { optional(it) } }
		deps.incompatible.forEach { dep -> whenNotNull(dep.curseforge) { incompatible(it) } }
		deps.embeds.forEach { dep -> whenNotNull(dep.curseforge) { embeds(it) } }
	}
	
	private fun configureStonecutterReplacements(stonecutter: StonecutterBuildExtension) {
		stonecutter.replacements.string(stonecutter.eval(stonecutter.current.version, ">=1.20.2"), "!renames_1_20_2") {
			replace ("net.mat0u5.lifeseries.utils.interfaces.CustomPacketPayload;", "net.minecraft.network.protocol.common.custom.CustomPacketPayload;")
		}
		stonecutter.replacements.string(stonecutter.eval(stonecutter.current.version, ">=1.20.3"), "!renames_1_20_3") {
			replace ("net.minecraft.world.scores.Score;", "net.minecraft.world.scores.PlayerScoreEntry;")
		}
		stonecutter.replacements.string(stonecutter.eval(stonecutter.current.version, ">=1.20.5"), "!renames_1_20_5") {
			replace ("BlockPathTypes", "PathType")
			replace ("Enchantments.ALL_DAMAGE_PROTECTION", "Enchantments.PROTECTION")
			replace ("Enchantments.FALL_PROTECTION", "Enchantments.FEATHER_FALLING")
			replace ("Enchantments.SILK_TOUCH", "Enchantments.SILK_TOUCH")
			replace ("Enchantments.BLOCK_FORTUNE", "Enchantments.FORTUNE")
			replace ("Enchantments.MOB_LOOTING", "Enchantments.LOOTING")
			replace ("Enchantments.BLOCK_EFFICIENCY", "Enchantments.EFFICIENCY")
		}
		stonecutter.replacements.string(stonecutter.eval(stonecutter.current.version, ">=1.21.2"), "!renames_1_21_2") {
			replace (".getMinBuildHeight()", ".getMinY()")
			replace (".getMaxBuildHeight()", ".getMaxY()")
			replace ("MobSpawnType", "EntitySpawnReason")
		}
		stonecutter.replacements.string(stonecutter.eval(stonecutter.current.version, ">=1.21.5"), "!renames_1_21_5") {
			replace ("MobEffects.MOVEMENT_SPEED", "MobEffects.SPEED")
			replace ("MobEffects.DIG_SPEED", "MobEffects.HASTE")
			replace ("MobEffects.DAMAGE_BOOST", "MobEffects.STRENGTH")
			replace ("MobEffects.JUMP", "MobEffects.JUMP_BOOST")
			replace ("MobEffects.DAMAGE_RESISTANCE", "MobEffects.RESISTANCE")
			replace ("MobEffects.MOVEMENT_SLOWDOWN", "MobEffects.SLOWNESS")
			replace ("MobEffects.DIG_SLOWDOWN", "MobEffects.MINING_FATIGUE")
			replace ("MobEffects.HEAL", "MobEffects.INSTANT_HEALTH")
			replace ("MobEffects.HEALTH_BOOST", "MobEffects.HEALTH_BOOST")
			replace ("MobEffects.HARM", "MobEffects.INSTANT_DAMAGE")
			replace ("MobEffects.CONFUSION", "MobEffects.NAUSEA")
		}
		stonecutter.replacements.string(stonecutter.eval(stonecutter.current.version, ">=1.21.6"), "renames_1_21_6_volatile") {
			replace (".popPose()", ".popMatrix()")
			replace (".pushPose()", ".pushMatrix()")
		}
		stonecutter.replacements.string(stonecutter.eval(stonecutter.current.version, ">=1.21.9"), "!renames_1_21_9") {
			replace ("net.minecraft.client.resources.PlayerSkin", "net.minecraft.world.entity.player.PlayerSkin")
		}
		stonecutter.replacements.string(stonecutter.eval(stonecutter.current.version, ">=1.21.11"), "!renames_1_21_11") {
			replace ("ResourceLocation", "Identifier")
			replace ("getResourceLocation", "getResourceLocation")
			replace ("getIdentifier", "getIdentifier")
			replace ("IdentifierHelper", "IdentifierHelper")
			replace ("net.minecraft.Util", "net.minecraft.util.Util")
			replace ("net.minecraft.client.renderer.RenderType;", "net.minecraft.client.renderer.rendertype.RenderType;")
			replace ("net.minecraft.world.entity.animal.SnowGolem", "net.minecraft.world.entity.animal.golem.SnowGolem")
			replace ("net.minecraft.world.entity.vehicle.MinecartTNT", "net.minecraft.world.entity.vehicle.minecart.MinecartTNT")
			replace ("net.minecraft.world.entity.projectile.AbstractThrownPotion", "net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion")
			replace ("net.minecraft.world.entity.projectile.ThrownTrident", "net.minecraft.world.entity.projectile.arrow.ThrownTrident")
			replace ("net.minecraft.world.entity.animal.Bee", "net.minecraft.world.entity.animal.bee.Bee")
			replace ("net.minecraft.world.level.GameRules;", "net.minecraft.world.level.gamerules.GameRules;")
			replace ("net.minecraft.world.entity.monster.Evoker", "net.minecraft.world.entity.monster.illager.Evoker")
			replace ("net.minecraft.world.entity.projectile.Snowball", "net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball")
			replace ("net.minecraft.world.entity.projectile.ThrownEnderpearl", "net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl")
			replace ("net.minecraft.world.entity.npc.WanderingTraderSpawner", "net.minecraft.world.entity.npc.wanderingtrader.WanderingTraderSpawner")
			replace ("net.minecraft.world.entity.monster.WitherSkeleton", "net.minecraft.world.entity.monster.skeleton.WitherSkeleton")
			replace ("net.minecraft.world.entity.monster.Zombie", "net.minecraft.world.entity.monster.zombie.Zombie")
	
			replace ("net.minecraft.world.entity.animal.horse.TraderLlama", "net.minecraft.world.entity.animal.equine.TraderLlama")
			replace ("net.minecraft.world.entity.npc.WanderingTrader;", "net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;")
	
			replace ("GameRules.RULE_DAYLIGHT", "GameRules.ADVANCE_TIME")
			replace ("GameRules.RULE_KEEPINVENTORY", "GameRules.KEEP_INVENTORY")
			replace ("GameRules.RULE_ANNOUNCE_ADVANCEMENTS", "GameRules.SHOW_ADVANCEMENT_MESSAGES")
			replace ("GameRules.RULE_LOCATOR_BAR", "GameRules.LOCATOR_BAR")
			replace ("GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE", "GameRules.PLAYERS_SLEEPING_PERCENTAGE")
			replace ("GameRules.RULE_NATURAL_REGENERATION", "GameRules.NATURAL_HEALTH_REGENERATION")
			replace ("GameRules.RULE_DOMOBLOOT", "GameRules.MOB_DROPS")
		}
		stonecutter.replacements.string(stonecutter.eval(stonecutter.current.version, ">=26.1"), "!renames_26_1") {
			replace ("GuiGraphics", "GuiGraphicsExtractor")
			replace (".renderItem(", ".item(")
			replace ("renderEntityInInventoryFollowsMouse(", "extractEntityInInventoryFollowsMouse(")
			replace ("ClientCommandManager.argument", "ClientCommands.argument")
			replace ("ClientCommandManager.literal", "ClientCommands.literal")
			replace (".registerKeyBinding(", ".registerKeyMapping(")
			replace ("KeyBindingHelper", "KeyMappingHelper")
			replace ("net.minecraft.client.renderer.state.CameraRenderState", "net.minecraft.client.renderer.state.level.CameraRenderState")
			replace ("net.minecraft.client.renderer.state.ParticleGroupRenderState", "net.minecraft.client.renderer.state.level.ParticleGroupRenderState")
			replace ("net.minecraft.client.renderer.state.SkyRenderState", "net.minecraft.client.renderer.state.level.SkyRenderState")
			replace ("net.minecraft.client.renderer.state.ParticlesRenderState", "net.minecraft.client.renderer.state.level.ParticlesRenderState")
			replace ("ParticleFactoryRegistry", "ParticleProviderRegistry")
			replace ("EntityModelLayerRegistry", "ModelLayerRegistry")
			replace (".playS2C()", ".clientboundPlay()")
			replace (".playC2S()", ".serverboundPlay()")
		}
		stonecutter.replacements.string(stonecutter.eval(stonecutter.current.version, ">=26.1"), "renames_26_1_volatile") {
			replace ("render(", "extractRenderState(")
			replace ("renderListItems(", "extractListItems(")
			replace ("renderContent(", "extractContent(")
			replace ("renderBackground(", "extractBackground(")
			replace ("drawString(", "text(")
			replace ("drawCenteredString(", "centeredText(")
		}
		stonecutter.replacements.string(stonecutter.eval(stonecutter.current.version, ">=26.2"), "!renames_26_2") {
			replace ("net.minecraft.world.entity.EntityType", "net.minecraft.world.entity.EntityTypes")
			replace ("EntityType.", "EntityTypes.")
			replace ("EntityType.Builder", "EntityType.Builder")
		}
	}
}
