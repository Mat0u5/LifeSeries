pluginManagement {
	repositories {
		mavenLocal()
		mavenCentral()
		gradlePluginPortal()
		maven("https://maven.fabricmc.net/") { name = "Fabric" }
		maven("https://maven.neoforged.net/releases/") { name = "NeoForged" }
		maven("https://maven.minecraftforge.net/") { name = "MinecraftForge" }
		maven("https://repo.spongepowered.org/repository/maven-public/") { name = "Sponge" }
		maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
		maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
		exclusiveContent {
			forRepository { maven("https://api.modrinth.com/maven") { name = "Modrinth" } }
			filter { includeGroup("maven.modrinth") }
		}
	}
	includeBuild("build-logic")
}

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
	id("dev.kikugie.stonecutter") version "0.9"
}

val settingsRootDir = rootDir

stonecutter {
	create(rootProject) {
		fun match(version: String, vararg loaders: String) {
			loaders.forEach { loader ->
				val buildscriptName = when {
					version.startsWith("26") && loader == "fabric" -> "build.fabric26.gradle.kts"
					loader == "forge" && (!version.equals("1.20") &&(version.startsWith("1.20") || version.startsWith("1.21") || version.startsWith("26"))) -> "build.forge21.gradle.kts"
					else -> "build.$loader.gradle.kts"
				}
				/*
				val buildscriptName = when {
					version.startsWith("26") && loader == "fabric" -> "build.fabric26.gradle.kts"
					else -> "build.$loader.gradle.kts"
				}
				 */

				version("$version-$loader", version).buildscript = buildscriptName
			}
		}

		fun env(variable: String): String? {
			val value = System.getenv(variable)
			if (value != null) return value

			val envFile = java.io.File(settingsRootDir, ".env")
			if (envFile.exists()) {
				val props = java.util.Properties()
				envFile.inputStream().use { props.load(it) }
				val fromFile = props.getProperty(variable)
				if (fromFile != null) return fromFile
			}

			return null
		}

		if (env("GRADLE_ONLY_IMPORTANT_FABRIC") == "true") {
			match("26.2", "fabric")
			match("26.1", "fabric")
			match("1.21.11", "fabric")
			match("1.21", "fabric")
		}
		else if (env("GRADLE_ONLY_FABRIC") == "true") {
			match("26.2", "fabric")
			match("26.1", "fabric")

			match("1.21.11", "fabric")
			match("1.21.9", "fabric")
			match("1.21.6", "fabric")
			match("1.21.5", "fabric")
			match("1.21.4", "fabric")
			match("1.21.2", "fabric")
			match("1.21", "fabric")

			match("1.20.5", "fabric")
			match("1.20.3", "fabric")
			match("1.20.2", "fabric")
			match("1.20", "fabric")
		}
		else {
			match("26.2", "fabric")
			match("26.1", "fabric", "forge", "neoforge")

			match("1.21.11", "fabric", "forge", "neoforge")
			match("1.21.9", "fabric", "forge", "neoforge")
			match("1.21.6", "fabric", "forge", "neoforge")
			match("1.21.5", "fabric", "forge", "neoforge")
			match("1.21.4", "fabric", "forge", "neoforge")
			match("1.21.2", "fabric", "forge", "neoforge")
			match("1.21", "fabric", "forge", "neoforge")

			match("1.20.5", "fabric", "forge", "neoforge")
			match("1.20.3", "fabric", "neoforge")
			match("1.20.2", "fabric")
			match("1.20", "fabric", "forge")
		}

		vcsVersion = "26.1-fabric"
	}
}
