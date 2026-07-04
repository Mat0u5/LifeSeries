plugins {
	id("mod-platform")
	id("net.fabricmc.fabric-loom")
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
}

dependencies {
	minecraft("com.mojang:minecraft:${prop("deps.minecraft")}")

	implementation(libs.fabric.loader)

	compileOnly("maven.modrinth:flashback:xs2Obe8Z")
	compileOnly("maven.modrinth:replaymod:1.21-2.6.23")
	compileOnly("maven.modrinth:appleskin:3.0.6+mc1.21")
	compileOnly("net.fabricmc.fabric-api:fabric-api:${prop("deps.fabric-api")}")
	//runtimeOnly("net.fabricmc.fabric-api:fabric-api:${prop("deps.fabric-api")}")

	if (isValidVersion(prop("deps.carpet"))) runtimeOnly("curse.maven:carpet-349239:${prop("deps.carpet")}")
	if (isValidVersion(prop("deps.carpet_bot_relog"))) runtimeOnly("maven.modrinth:carpet-bot-relog:${prop("deps.carpet_bot_relog")}")

	//Runtime and compile
	if (isValidVersion(prop("deps.voicechat"))) {
		compileOnly ("maven.modrinth:simple-voice-chat:${prop("deps.voicechat")}")
		//runtimeOnly ("maven.modrinth:simple-voice-chat:${prop("deps.voicechat")}")
	}
	else {
		compileOnly ("maven.modrinth:simple-voice-chat:fabric-2.6.11+26.1-snapshot-1")
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
