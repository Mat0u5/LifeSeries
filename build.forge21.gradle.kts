plugins {
	id("mod-platform")
	id("net.minecraftforge.gradle")
	id("net.minecraftforge.jarjar")
}

fun isValidVersion(string: String?): Boolean {
	return !string.isNullOrEmpty() && !string.equals("null", ignoreCase = true) && !string.equals("[VERSIONED]", ignoreCase = true)
}

fun prop(key: String) = project.property(key) as String

platform {
	loader = "forge"
	jarTask.set("jarJar")
	dependencies {
		required("minecraft") {
			forgeVersionRange = "[${prop("deps.minecraft")}]"
		}
		required("forge") {
			forgeVersionRange = "[1,)"
		}
	}
}

minecraft {
	mappings("official", prop("deps.minecraft"))

	val atFile = rootProject.file("src/main/resources/aw/${stonecutter.current.version}.cfg")
	if (atFile.exists()) {
		accessTransformers = files(atFile)
	}

	runs {
		configureEach {
			workingDir.convention(layout.projectDirectory.dir("run"))
			systemProperty("forge.logging.console.level", "debug")
			args("--mixin.config=${prop("mod.id")}.mixins.json")
			ideaModule = null
		}
		register("client") {
			args("--username", "Player")
		}
		register("server") {
			args("--nogui")
		}
	}
}

sourceSets.configureEach {
	val dir = layout.buildDirectory.dir("sourcesSets/$name")
	output.setResourcesDir(dir)
	java.destinationDirectory.set(dir)
}

repositories {
	minecraft.mavenizer(this)
	maven(fg.forgeMaven)
	maven(fg.minecraftLibsMaven)
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth") { name = "Modrinth" }
	strictMaven("https://www.cursemaven.com", "curse.maven") { name = "CurseForge" }
	maven ( "https://maven.maxhenkel.de/repository/public")
	mavenCentral()
}

jarJar.register()

tasks.named<Jar>("jarJar") {
	archiveClassifier.set("")
	dependsOn("jar")
}

dependencies {
	implementation(minecraft.dependency("net.minecraftforge:forge:${prop("deps.forge")}"))
	annotationProcessor("org.spongepowered:mixin:${libs.versions.mixin.get()}:processor")
	annotationProcessor("io.github.llamalad7:mixinextras-common:${libs.versions.mixinextras.get()}")

	compileOnly("io.github.llamalad7:mixinextras-common:${libs.versions.mixinextras.get()}")
	implementation("io.github.llamalad7:mixinextras-forge:${libs.versions.mixinextras.get()}")
	"jarJar"("io.github.llamalad7:mixinextras-forge:${libs.versions.mixinextras.get()}")

	implementation(libs.moulberry.mixinconstraints)
	compileOnly("maven.modrinth:appleskin:2.5.1+mc1.20.2")
	compileOnly("de.maxhenkel.voicechat:voicechat-api:2.5.27")
	if (isValidVersion(prop("deps.voicechat"))) {
		implementation ("maven.modrinth:simple-voice-chat:${prop("deps.voicechat")}")
		//compileOnly ("maven.modrinth:simple-voice-chat:${prop("deps.voicechat")}")
	}
	else {
		compileOnly ("maven.modrinth:simple-voice-chat:forge-1.20.1-2.6.16")
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.compilerArgs.addAll(listOf(
		"-Amixin.refmap.name=${prop("mod.id")}.mixins.refmap.json",
		"-AoutRefMapFile=${layout.buildDirectory.file("sourcesSets/main/${prop("mod.id")}.mixins.refmap.json").get().asFile}"
	))
}

tasks.named<Jar>("jar") {
	destinationDirectory.set(layout.buildDirectory.dir("intermediates/jar"))

	manifest {
		attributes["MixinConfigs"] = "${prop("mod.id")}.mixins.json"
	}
	from(layout.buildDirectory.file("sourcesSets/main/${prop("mod.id")}.mixins.refmap.json")) {
		into("/")
	}
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

sourceSets {
	main {
		resources.srcDir(
			"${rootDir}/versions/datagen/${stonecutter.current.version.split("-")[0]}/src/main/generated"
		)
	}
}
