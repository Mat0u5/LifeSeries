plugins {
	alias(libs.plugins.stonecutter)
	alias(libs.plugins.dotenv)
	alias(libs.plugins.fabric.loom).apply(false)
	alias(libs.plugins.fabric.loom.remap).apply(false)
	alias(libs.plugins.neoforged.moddev).apply(false)
	alias(libs.plugins.minecraftforge.gradle).apply(false)
	alias(libs.plugins.minecraftforge.jarjar).apply(false)
	alias(libs.plugins.jsonlang.postprocess).apply(false)
	alias(libs.plugins.mod.publish.plugin).apply(false)
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
			val loader = task.project.name.substringAfterLast('-')
			val delayMs = 2000L;
			task.doFirst {
				logger.lifecycle("\n>>> [WAITING] ${delayMs/1000}s before uploading ${task.project.name}...")
				Thread.sleep(delayMs)
			}
		}
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
