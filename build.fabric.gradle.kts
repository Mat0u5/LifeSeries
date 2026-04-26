plugins {
	id("mod-platform")
	id("net.fabricmc.fabric-loom-remap")
}

platform {
	loader = "fabric"
	dependencies {
		required("minecraft") {
			versionRange = prop("deps.minecraft")
		}
		required("fabric-api") {
			slug("fabric-api")
			versionRange = ">=${prop("deps.fabric-api")}"
		}
		required("fabricloader") {
			versionRange = ">=${libs.fabric.loader.get().version}"
		}
	}
}

loom {
	accessWidenerPath = rootProject.file("src/main/resources/aw/${stonecutter.current.version}.accesswidener")
	runs.named("client") {
		client()
		ideConfigGenerated(true)
		runDir = "run/"
		environment = "client"
		programArgs("--username=Player")
		configName = "Fabric Client"
	}
	runs.named("server") {
		server()
		ideConfigGenerated(true)
		runDir = "run/"
		environment = "server"
		configName = "Fabric Server"
	}
}

fabricApi {
	configureDataGeneration {
		outputDirectory = file("${rootDir}/versions/datagen/${stonecutter.current.version.split("-")[0]}/src/main/generated")
		client = true
	}
}

repositories {
	mavenCentral()
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth") { name = "Modrinth" }
}

dependencies {
	minecraft("com.mojang:minecraft:${prop("deps.minecraft")}")
	mappings(
		loom.layered {
			officialMojangMappings()
		})
	modImplementation(libs.fabric.loader)
	modImplementation("net.fabricmc.fabric-api:fabric-api:${prop("deps.fabric-api")}")
}

stonecutter {
	replacements.string(stonecutter.eval(stonecutter.current.version, ">=1.20.2"), "!renames_1_20_2") {
		replace ("net.mat0u5.lifeseries.utils.interfaces.CustomPacketPayload;", "net.minecraft.network.protocol.common.custom.CustomPacketPayload;")
	}
	replacements.string(stonecutter.eval(stonecutter.current.version, ">=1.20.3"), "!renames_1_20_3") {
		replace ("net.minecraft.world.scores.Score;", "net.minecraft.world.scores.PlayerScoreEntry;")
	}
	replacements.string(stonecutter.eval(stonecutter.current.version, ">=1.20.5"), "!renames_1_20_5") {
		replace ("BlockPathTypes", "PathType")
		replace ("Enchantments.ALL_DAMAGE_PROTECTION", "Enchantments.PROTECTION")
		replace ("Enchantments.FALL_PROTECTION", "Enchantments.FEATHER_FALLING")
		replace ("Enchantments.SILK_TOUCH", "Enchantments.SILK_TOUCH")
		replace ("Enchantments.BLOCK_FORTUNE", "Enchantments.FORTUNE")
		replace ("Enchantments.MOB_LOOTING", "Enchantments.LOOTING")
		replace ("Enchantments.BLOCK_EFFICIENCY", "Enchantments.EFFICIENCY")
	}
	replacements.string(stonecutter.eval(stonecutter.current.version, ">=1.21.2"), "!renames_1_21_2") {
		replace (".getMinBuildHeight()", ".getMinY()")
		replace (".getMaxBuildHeight()", ".getMaxY()")
		replace ("MobSpawnType", "EntitySpawnReason")
	}
	replacements.string(stonecutter.eval(stonecutter.current.version, ">=1.21.5"), "!renames_1_21_5") {
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
	replacements.string(stonecutter.eval(stonecutter.current.version, ">=1.21.6"), "renames_1_21_6_volatile") {
		replace (".popPose()", ".popMatrix()")
		replace (".pushPose()", ".pushMatrix()")
	}
	replacements.string(stonecutter.eval(stonecutter.current.version, ">=1.21.9"), "!renames_1_21_9") {
		replace ("net.minecraft.client.resources.PlayerSkin", "net.minecraft.world.entity.player.PlayerSkin")
	}
	replacements.string(stonecutter.eval(stonecutter.current.version, ">=1.21.11"), "!renames_1_21_11") {
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
	replacements.string(stonecutter.eval(stonecutter.current.version, ">=26.1"), "!renames_26_1") {
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
	replacements.string(stonecutter.eval(stonecutter.current.version, ">=26.1"), "renames_26_1_volatile") {
		replace ("render(", "extractRenderState(")
		replace ("renderListItems(", "extractListItems(")
		replace ("renderContent(", "extractContent(")
		replace ("renderBackground(", "extractBackground(")
		replace ("drawString(", "text(")
		replace ("drawCenteredString(", "centeredText(")
	}
	replacements.string(stonecutter.eval(stonecutter.current.version, ">=26.2"), "!renames_26_2") {
		replace ("net.minecraft.world.entity.EntityType", "net.minecraft.world.entity.EntityTypes")
		replace ("EntityType.", "EntityTypes.")
		replace ("EntityType.Builder", "EntityType.Builder")
	}
}
