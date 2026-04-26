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
		val inferredLoaderIsFabric = inferredLoader == "fabric"
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
		val modVersion = prop("mod.version")
		val modVersionPrefix = prop("mod.version_prefix")
		val modVersionSuffix = prop("mod.version_suffix")
		val mcVersion = prop("deps.minecraft")
		val mcRange = prop("mod.mc_range").ifBlank { "[$mcVersion]" }

		val stonecutter = extensions.getByType<StonecutterBuildExtension>()

		listOf(
			"java",
			"me.modmuss50.mod-publish-plugin",
			"idea",
		).forEach { apply(plugin = it) }

		version = "$modVersionPrefix$modVersion$modVersionSuffix+$mcVersion-$loader"

		extension.requiredJava.set(
			when {
				stonecutter.eval(stonecutter.current.version, ">=26") -> JavaVersion.VERSION_25
				stonecutter.eval(stonecutter.current.version, ">=1.20.5") -> JavaVersion.VERSION_21
				stonecutter.eval(stonecutter.current.version, ">=1.18") -> JavaVersion.VERSION_17
				stonecutter.eval(stonecutter.current.version, ">=1.17") -> JavaVersion.VERSION_16
				else -> JavaVersion.VERSION_1_8
			}
		)

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
					versionRange.set(prop("mod.loader_range").ifBlank { "*" })
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
			"$modVersionPrefix$modVersion$modVersionSuffix",
			mcVersion,
			extension,
			extension.requiredJava.get(),
			stonecutter
		)
		configureJava(stonecutter, extension.requiredJava.get())
		registerBuildAndCollectTask(extension, "$modVersionPrefix$modVersion$modVersionSuffix")
		configurePublishing(extension, loader, stonecutter,
			"$modVersionPrefix$modVersion$modVersionSuffix",
			"$loader-$modVersionPrefix$modVersion$modVersionSuffix+$mcVersion")
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
				val mixinJava = if (isForge) {
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
						exclude("META-INF/neoforge.mods.toml", "fabric.mod.json", "aw/*.accesswidener", ".cache", "pack.mcmeta")
					} else {
						filesMatching("META-INF/neoforge.mods.toml") { expand(props) }
						exclude("META-INF/mods.toml", "fabric.mod.json", "aw/*.accesswidener", ".cache", "pack.mcmeta")
					}
				}

				isForge -> {
					filesMatching("META-INF/mods.toml") { expand(props) }
					exclude("META-INF/neoforge.mods.toml", "fabric.mod.json", "aw/*.accesswidener", ".cache")
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
			into(rootProject.layout.buildDirectory.file("libs/$modVersion"))
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

		// Read release type from gradle.properties based on loader
		val releaseTypeRaw = prop("release.$loader").ifBlank { "stable" }
		val releaseType = ReleaseType.of(
			releaseTypeRaw.let { if (it == "dev") "beta" else it }
		)

		extensions.configure<ModPublishExtension>("publishMods") {
			val mrStaging = envTrue("TEST_PUBLISHING_WITH_MR_STAGING")

			val modrinthAccessToken = env("MODRINTH_API_TOKEN")
			val curseforgeAccessToken = env("CURSEFORGE_API_TOKEN")

			val modrinthProjectId = prop("publish.modrinth")
			val curseforgeProjectId = prop("publish.curseforge")

			if (!envTrue("ENABLE_PUBLISHING")) {
				dryRun = true
			}

			val isForge = loader == "forge"
			val targetName = if (isForge && stonecutter.eval(stonecutter.current.version, "<=1.20")) {
				"reobfJar"
			} else {
				ext.jarTask.get()
			}

			val jarTask = tasks.named(targetName).map { it as Jar }
			val currentVersion = stonecutter.current.version
			val deps = ext.dependencies

			file.set(jarTask.flatMap(Jar::getArchiveFile))
			type = releaseType
			version = displayVersion
			changelog.set(rootProject.file("CHANGELOG.md").readText())
			modLoaders.add(loader)
			if (loader == "fabric") {
				modLoaders.add("quilt")
			}
			if (loader == "forge" && currentVersion == "1.20" && additionalVersions.size <= 1) {
				modLoaders.add("neoforge")
			}

			val mcVersionRange = if (additionalVersions.isNotEmpty()) "$currentVersion-${additionalVersions.last()}" else currentVersion
			displayName = "${prop("mod.name")} $modVersion for ${loader.replaceFirstChar(Char::titlecase)} $mcVersionRange"

			// Check if Modrinth should be published
			if (!modrinthAccessToken.isNullOrBlank() && modrinthProjectId.isNotBlank()) {
				modrinth(deps, currentVersion, additionalVersions, mrStaging, modrinthAccessToken)
			} else {
				//logger.lifecycle("Skipping Modrinth publishing for $name: Token or Project ID is missing.")
			}

			// Check if CurseForge should be published
			if (!curseforgeAccessToken.isNullOrBlank() && curseforgeProjectId.isNotBlank()) {
				if (!mrStaging) curseforge(deps, currentVersion, additionalVersions, false, curseforgeAccessToken)
			} else {
				//logger.lifecycle("Skipping CurseForge publishing for $name: Token or Project ID is missing.")
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
		projectId = project.prop("publish.modrinth")
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
		projectId = project.prop("publish.curseforge")
		this.accessToken = accessToken
		minecraftVersions.addAll(listOf(currentVersion) + additionalVersions)

		deps.required.forEach { dep -> whenNotNull(dep.curseforge) { requires(it) } }
		deps.optional.forEach { dep -> whenNotNull(dep.curseforge) { optional(it) } }
		deps.incompatible.forEach { dep -> whenNotNull(dep.curseforge) { incompatible(it) } }
		deps.embeds.forEach { dep -> whenNotNull(dep.curseforge) { embeds(it) } }
	}
}
