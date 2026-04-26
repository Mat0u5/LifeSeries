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

stonecutter {
	create(rootProject) {
		fun match(version: String, vararg loaders: String) {
			loaders.forEach { loader ->
				val buildscriptName = when {
					version.startsWith("26") && loader == "fabric" -> "build.fabric26.gradle.kts"
					loader == "forge" && (!version.equals("1.20") &&(version.startsWith("1.20") || version.startsWith("1.21") || version.startsWith("26"))) -> "build.forge20.gradle.kts"
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

		match("1.19", "fabric", "forge")
		match("1.18", "fabric")

		vcsVersion = "26.1-fabric"
	}
}
