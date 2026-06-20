plugins {
	id("mod-platform")
	id("net.neoforged.moddev.legacyforge")
}

fun isValidVersion(string: String?): Boolean {
	return !string.isNullOrEmpty() && !string.equals("null", ignoreCase = true) && !string.equals("[VERSIONED]", ignoreCase = true)
}

platform {
	loader = "forge"
	dependencies {
		required("minecraft") {
			forgeVersionRange = "[${prop("deps.minecraft")}]"
		}
		required("forge") {
			forgeVersionRange = "[1,)"
		}
	}
}

legacyForge {
	version = "${property("deps.forge")}"

	validateAccessTransformers = true

	accessTransformers.from(
		rootProject.file("src/main/resources/aw/${stonecutter.current.version}.cfg")
	)

	runs {
		register("client") {
			client()
			gameDirectory = file("run/")
			ideName = "Forge Client (${stonecutter.active?.version})"
			programArgument("--username=Player")
		}
		register("server") {
			server()
			gameDirectory = file("run/")
			ideName = "Forge Server (${stonecutter.active?.version})"
		}
	}


	mods {
		register(prop("mod.id")) {
			sourceSet(sourceSets["main"])
		}
	}
}

mixin {
	add(sourceSets.main.get(), "${prop("mod.id")}.mixins.refmap.json")
	config("${prop("mod.id")}.mixins.json")
}

repositories {
	mavenCentral()
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth") { name = "Modrinth" }
	strictMaven("https://www.cursemaven.com", "curse.maven") { name = "CurseForge" }
	maven ( "https://maven.maxhenkel.de/repository/public")
}

dependencies {
	annotationProcessor("org.spongepowered:mixin:${libs.versions.mixin.get()}:processor")
	annotationProcessor("io.github.llamalad7:mixinextras-common:${libs.versions.mixinextras.get()}")

	compileOnly("io.github.llamalad7:mixinextras-common:${libs.versions.mixinextras.get()}")
	implementation("io.github.llamalad7:mixinextras-forge:${libs.versions.mixinextras.get()}")
	"jarJar"("io.github.llamalad7:mixinextras-forge:${libs.versions.mixinextras.get()}")

	implementation(libs.moulberry.mixinconstraints)
	jarJar(libs.moulberry.mixinconstraints)
	compileOnly("maven.modrinth:appleskin:2.5.1+mc1.20.2")
	compileOnly("de.maxhenkel.voicechat:voicechat-api:2.5.27")
	if (isValidVersion(prop("deps.voicechat"))) {
		implementation ("maven.modrinth:simple-voice-chat:${prop("deps.voicechat")}")
	}
	else {
		compileOnly ("maven.modrinth:simple-voice-chat:forge-1.20.1-2.6.16")
	}
}

sourceSets {
	main {
		resources.srcDir(
			"${rootDir}/versions/datagen/${stonecutter.current.version.split("-")[0]}/src/main/generated"
		)
	}
}

tasks.named("createMinecraftArtifacts") {
	dependsOn(tasks.named("stonecutterGenerate"))
}
