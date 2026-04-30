plugins {
	alias(libs.plugins.stonecutter)
	alias(libs.plugins.dotenv)
	alias(libs.plugins.mod.publish.plugin)
	alias(libs.plugins.fabric.loom).apply(false)
	alias(libs.plugins.fabric.loom.remap).apply(false)
	alias(libs.plugins.neoforged.moddev).apply(false)
	alias(libs.plugins.minecraftforge.gradle).apply(false)
	alias(libs.plugins.minecraftforge.jarjar).apply(false)
	alias(libs.plugins.jsonlang.postprocess).apply(false)
	alias(libs.plugins.kotlin.jvm).apply(false)
	alias(libs.plugins.devtools.ksp).apply(false)
	alias(libs.plugins.fletching.table).apply(false)
	alias(libs.plugins.legacyforge.moddev).apply(false)
}

stonecutter active file(".sc_active_version")


for (version in stonecutter.versions.map { it.version }.distinct()) tasks.register("publish$version") {
	group = "publishing"
	dependsOn(stonecutter.tasks.named("publishMods") { metadata.version == version })
}

gradle.projectsEvaluated {
	val versionOrder = stonecutter.versions.map { it.version }.distinct().reversed()

	listOf("publishModrinth", "publishCurseforge").forEach { taskName ->
		val allTasks = subprojects.mapNotNull { it.tasks.findByName(taskName) }

		val sorted = allTasks.sortedWith(compareBy(
			{ task ->
				val loader = task.project.name.substringAfterLast('-')
				when {
					"fabric" in loader -> 2
					"neoforge" in loader -> 1
					else -> 0
				}
			},
			{ task ->
				val version = task.project.name.substringBeforeLast('-')
				versionOrder.indexOf(version).takeIf { it >= 0 } ?: Int.MAX_VALUE
			}
		))

		for (i in 1 until sorted.size) {
			sorted[i].dependsOn(sorted[i - 1])
		}

		sorted.forEach { task ->
			task.doFirst {
				logger.lifecycle("\n>>> Uploading ${task.project.name}...")
			}
		}
	}

	tasks.findByName("publishDiscordBody") ?.dependsOn(tasks.named("publishDiscordHeader"))

	val shouldWaitForModrinth = findProperty("publish.modrinth")?.toString() == "true"
	val shouldAnnounceDiscord = findProperty("publish.discord")?.toString() == "true"

	if (shouldAnnounceDiscord && shouldWaitForModrinth) {
		val allModrinthTasks = subprojects.mapNotNull { it.tasks.findByName("publishModrinth") }
		tasks.findByName("publishDiscordHeader")?.dependsOn(allModrinthTasks)
	}
}

tasks.register("runActiveClient") {
	group = "stonecutter"
	description = "Run client of the active Stonecutter version (always up-to-date)"

	dependsOn(stonecutter.current!!.project + ":processResources")
	dependsOn(stonecutter.current!!.project + ":classes")

	finalizedBy(stonecutter.current!!.project + ":runClient")
}

stonecutter parameters {
	constants.match(node.metadata.project.substringAfterLast('-'), "fabric", "neoforge", "forge")
	filters.include("**/*.fsh", "**/*.vsh")
	swaps["mod_version"] = "\"" + property("mod.version") + "\";"
	swaps["mod_id"] = "\"" + property("mod.id") + "\";"
	swaps["mod_name"] = "\"" + property("mod.name") + "\";"
	swaps["mod_group"] = "\"" + property("mod.group") + "\";"
	swaps["minecraft"] = "\"" + node.metadata.version + "\";"
	constants["release"] = property("mod.id") != "modtemplate"
}

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

publishMods {
	// Non-root releases are in ModPlatformPlugin
	val shouldPublishGithub = project.findProperty("publish.github")?.toString() == "true"
	val shouldAnnounceDiscord = project.findProperty("publish.discord")?.toString() == "true"

	if (shouldPublishGithub) {
		github {
			val githubAccessToken = env("GITHUB_TOKEN")
			val forcePrerelease = property("publish.github.prerelease").toString() == "true"

			val versionName = project.findProperty("mod.version")?.toString()
			val versionPrefix = project.findProperty("mod.version_prefix")?.toString()
			val versionSuffix = project.findProperty("mod.version_suffix")?.toString()
			val versionNameFull = versionPrefix+versionName+versionSuffix

			version = versionNameFull
			displayName = "Version $versionNameFull"
			changelog = rootProject.file("CHANGELOG.md").readText()
			type = when {
				forcePrerelease -> me.modmuss50.mpp.ReleaseType.BETA
				else -> me.modmuss50.mpp.ReleaseType.STABLE
			}

			accessToken = githubAccessToken
			repository = property("publish.github.target").toString().removeSuffix("/")
			commitish = property("publish.github.branch").toString().ifBlank { "main" }
			tagName = property("mod.version").toString()
			allowEmptyFiles = true
		}
	}


	if (shouldAnnounceDiscord) {
		val versionName = project.findProperty("mod.version")?.toString()
		val versionPrefix = project.findProperty("mod.version_prefix")?.toString()
		val versionSuffix = project.findProperty("mod.version_suffix")?.toString()
		val isDev = project.findProperty("publish.discord.dev")?.toString() == "true"
		val ping = project.findProperty("publish.discord.ping")?.toString() == "true"
		val version = versionPrefix+versionName+versionSuffix
		val webhook = if (isDev) env("DISCORD_WEBHOOK_DEV") else env("DISCORD_WEBHOOK")
		val changelogLink = project.findProperty("publish.changelog.link")?.toString()

		discord("publishDiscordHeader") {
			username = "Mat0u5"
			avatarUrl = "https://github.com/Mat0u5.png"
			webhookUrl = webhook
			if (ping) {
				if (!isDev) {
					content = "<@&1346438113169510421>";
				}
				else {
					content = "<@&1346434430683844658>";
				}
			}
			else {
				content = "*Ping Skipped*";
			}
			setPlatformsAllFrom()
		}
		discord("publishDiscordBody") {
			username = "Mat0u5"
			avatarUrl = "https://github.com/Mat0u5.png"
			changelog = rootProject.file("CHANGELOG.md").readText()
			webhookUrl = webhook

			if (!isDev) {
				content = changelog.map { "# [Life Series version `$version` is out!](https://modrinth.com/mod/life-series/versions)\n" +
						"### Changelog:\n" +
						"```\n$it```\n\n" +
						"[Click here to open the **full changelog**]($changelogLink)" }
			}
			else {
				content = changelog.map { "# [Life Series version `$version` is out!](https://modrinth.com/mod/life-series-dev/versions)\n" +
						"### Changelog:\n" +
						"```\n$it```\n\n" +
						"[Click here to open the **full changelog**]($changelogLink)" }
			}

			setPlatformsAllFrom()
			style {
				thumbnailUrl = if (isDev) "https://cdn.modrinth.com/data/RLDqKhd4/8ce4817238d1f4cd077dfbb515aa3da85f9e80ea_96.webp" else "https://cdn.modrinth.com/data/aLasQi8P/88e9e2637dee4707a5f9eda10e37325b6a5b292a_96.webp"
				color = if (isDev) "#B24691" else "#511A82"
				look = "MODERN"
				//link = "BUTTON"
			}
		}
	}

}
