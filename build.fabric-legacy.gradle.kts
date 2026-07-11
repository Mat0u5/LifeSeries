plugins {
	id("mod-platform")
	id("net.fabricmc.fabric-loom-remap")
}

fun isValidVersion(string: String?): Boolean {
	return !string.isNullOrEmpty() && !string.equals("null", ignoreCase = true) && !string.equals("[VERSIONED]", ignoreCase = true)
}
platform {
	loader = "fabric"
	dependencies {
		required("minecraft") {
			versionRange = prop("deps.minecraft")
		}
		required("fabricloader") {
			versionRange = ">=${libs.fabric.loader.get().version}"
		}
	}
}

loom {
	accessWidenerPath = rootProject.file("src/main/resources/aw/${stonecutter.current.version}.accesswidener")
	val isJbr = System.getProperty("java.vendor")?.contains("JetBrains", ignoreCase = true) == true
	runs.named("client") {
		client()
		ideConfigGenerated(false)
		runDir = "run/"
		environment = "client"
		programArgs("--username=Player")
		configName = "Fabric Client"
		if (isJbr) vmArg("-XX:+AllowEnhancedClassRedefinition")
	}
	runs.named("server") {
		server()
		ideConfigGenerated(false)
		runDir = "run/"
		environment = "server"
		configName = "Fabric Server"
		if (isJbr) vmArg("-XX:+AllowEnhancedClassRedefinition")
	}
}

repositories {
	mavenCentral()
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth") { name = "Modrinth" }
	strictMaven("https://www.cursemaven.com", "curse.maven") { name = "CurseForge" }
	maven ( "https://maven.maxhenkel.de/repository/public")
}

dependencies {
	minecraft("com.mojang:minecraft:${prop("deps.minecraft")}")
	mappings(
		loom.layered {
			officialMojangMappings()
		})
	modImplementation(libs.fabric.loader)
	modCompileOnly("maven.modrinth:flashback:z0SX4zNw")
	modCompileOnly("maven.modrinth:replaymod:1.21-2.6.23")
	modCompileOnly("de.maxhenkel.voicechat:voicechat-api:2.5.27") //TODO this is a patch for older versions of SVC
	if (stonecutter.eval(stonecutter.current.version, "<=1.20")) {
		compileOnly("maven.modrinth:appleskin:2.5.2+mc1.20.1")
	}
	else {
		modCompileOnly("maven.modrinth:appleskin:3.0.6+mc1.21")
	}
	modCompileOnly("net.fabricmc.fabric-api:fabric-api:${prop("deps.fabric-api")}")
	//modRuntimeOnly("net.fabricmc.fabric-api:fabric-api:${prop("deps.fabric-api")}")

	if (isValidVersion(prop("deps.carpet"))) modRuntimeOnly("curse.maven:carpet-349239:${prop("deps.carpet")}")
	if (isValidVersion(prop("deps.carpet_bot_relog"))) modRuntimeOnly("maven.modrinth:carpet-bot-relog:${prop("deps.carpet_bot_relog")}")

	//Runtime and compile
	if (isValidVersion(prop("deps.voicechat"))) {
		modCompileOnly ("maven.modrinth:simple-voice-chat:${prop("deps.voicechat")}")
		//modRuntimeOnly ("maven.modrinth:simple-voice-chat:${prop("deps.voicechat")}")
	}
	else {
		modCompileOnly ("maven.modrinth:simple-voice-chat:fabric-1.21.1-2.5.35")
	}
}

project.afterEvaluate {
	val mixinJarPath = configurations.compileClasspath.get().files
		.firstOrNull { it.name.contains("sponge-mixin") || (it.name.contains("mixin") && !it.name.contains("fabric-mixin-compile-extensions")) }
		?.absolutePath

	if (mixinJarPath != null) {
		loom {
			runs.named("client") {
				vmArg("-javaagent:$mixinJarPath")
			}
			runs.named("server") {
				vmArg("-javaagent:$mixinJarPath")
			}
		}
	}
}
