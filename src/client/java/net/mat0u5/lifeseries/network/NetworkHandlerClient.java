package net.mat0u5.lifeseries.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.LifeSeriesClient;
import net.mat0u5.lifeseries.compatibilities.CompatibilityManager;
import net.mat0u5.lifeseries.compatibilities.VoicechatClient;
import net.mat0u5.lifeseries.config.ClientConfig;
import net.mat0u5.lifeseries.config.ClientConfigGuiManager;
import net.mat0u5.lifeseries.config.ClientConfigNetwork;
import net.mat0u5.lifeseries.features.LifeSkinsClient;
import net.mat0u5.lifeseries.features.Morph;
import net.mat0u5.lifeseries.features.SnailSkinsClient;
import net.mat0u5.lifeseries.features.Trivia;
import net.mat0u5.lifeseries.gui.EmptySleepScreen;
import net.mat0u5.lifeseries.gui.other.ChooseWildcardScreen;
import net.mat0u5.lifeseries.gui.other.PastLifeChooseTwistScreen;
import net.mat0u5.lifeseries.gui.seasons.ChooseSeasonScreen;
import net.mat0u5.lifeseries.gui.seasons.SeasonInfoScreen;
import net.mat0u5.lifeseries.gui.trivia.NewQuizScreen;
import net.mat0u5.lifeseries.gui.trivia.VotingScreen;
import net.mat0u5.lifeseries.mixin.PlayerAccessor;
import net.mat0u5.lifeseries.mixin.client.GuiAccessor;
import net.mat0u5.lifeseries.network.packets.*;
import net.mat0u5.lifeseries.network.packets.simple.SimplePacket;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.registries.ParticleRegistry;
import net.mat0u5.lifeseries.render.TextHud;
import net.mat0u5.lifeseries.render.VignetteRenderer;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphComponent;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.Hunger;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.TimeDilation;
import net.mat0u5.lifeseries.seasons.session.SessionStatus;
import net.mat0u5.lifeseries.utils.ClientResourcePacks;
import net.mat0u5.lifeseries.utils.ClientSounds;
import net.mat0u5.lifeseries.utils.ClientUtils;
import net.mat0u5.lifeseries.utils.enums.HandshakeStatus;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.versions.VersionControl;
import net.mat0u5.lifeseries.utils.world.AnimationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
//? if <= 1.20.3 {
/*import net.minecraft.network.FriendlyByteBuf;
*///?}
import net.fabricmc.fabric.api.networking.v1.*;

public class NetworkHandlerClient {
    public static void initializeSimplePacketReceivers() {
        //Long payload
        SimplePackets.CURSE_SLIDING.setClientReceive(payload -> LifeSeriesClient.CURSE_SLIDING = payload.number());

        //String list payload
        SimplePackets.LVL1_CLAMPED_ENCHANTS.setClientReceive(payload -> {
            LifeSeriesClient.lvl1ClampedEnchants = payload.value();
        });
        SimplePackets.LIFESKINS_PLAYER.setClientReceive(payload -> {
            UUID uuid = UUID.fromString(payload.value().get(0));
            String teamName = payload.value().get(1);
            LifeSkinsClient.setCurrentSkinId(uuid, teamName);
        });
        SimplePackets.LIMITED_LIFE_TIMER.setClientReceive(payload -> {
            LifeSeriesClient.limitedLifeTimerColor = payload.value().get(0);
            LifeSeriesClient.limitedLifeLives = Long.parseLong(payload.value().get(1));
            LifeSeriesClient.limitedLifeTimeLastUpdated = System.currentTimeMillis();
        });
        SimplePackets.SEASON_INFO.setClientReceive(payload -> {
            if (!LifeSeries.modDisabled()) {
                Seasons season = Seasons.getSeasonFromStringName(payload.value().get(0));
                String adminCommands = payload.value().get(1);
                String nonAdminCommands = payload.value().get(2);
                if (season != Seasons.UNASSIGNED) Minecraft.getInstance().ls$setScreen(new SeasonInfoScreen(season, adminCommands, nonAdminCommands));
            }
        });
        SimplePackets.MORPH.setClientReceive(payload -> {
            String morphUUIDStr = payload.value().get(0);
            UUID morphUUID = UUID.fromString(morphUUIDStr);
            String morphTypeStr = payload.value().get(1);
            EntityType<?> morphType = null;
            if (!morphTypeStr.equalsIgnoreCase("null") && !morphUUIDStr.isEmpty()) {
                //? if <= 1.21 {
                /*morphType = BuiltInRegistries.ENTITY_TYPE.get(IdentifierHelper.parse(morphTypeStr));
                 *///?} else {
                morphType = BuiltInRegistries.ENTITY_TYPE.getValue(IdentifierHelper.parse(morphTypeStr));
                //?}
            }
            if (VersionControl.isDevVersion()) LifeSeries.LOGGER.info("[PACKET_CLIENT] Received morph packet: {} ({})", morphType, morphUUID);
            MorphComponent newComponent = MorphManager.setFromPacket(morphUUID, morphType);
            Morph.clientTick(newComponent);
        });
        SimplePackets.HUNGER_NON_EDIBLE.setClientReceive(payload -> {
            Hunger.nonEdible.clear();
            for (String itemId : payload.value()) {
                if (!itemId.contains(":")) itemId = "minecraft:" + itemId;

                try {
                    var id = IdentifierHelper.parse(itemId);
                    ResourceKey<Item> key = ResourceKey.create(BuiltInRegistries.ITEM.key(), id);

                    //? if <= 1.21 {
                    /*Item item = BuiltInRegistries.ITEM.get(key);
                     *///?} else {
                    Item item = BuiltInRegistries.ITEM.getValue(key);
                    //?}
                    if (item != null) {
                        Hunger.nonEdible.add(item);
                    } else {
                        OtherUtils.throwError("[CONFIG] Invalid item: " + itemId);
                    }
                } catch (Exception e) {
                    OtherUtils.throwError("[CONFIG] Error parsing item ID: " + itemId);
                }
            }
        });
        SimplePackets.SKYCOLOR.setClientReceive(payload -> {
            LifeSeriesClient.skyColorSetMode = payload.value().get(0).equalsIgnoreCase("true");
            LifeSeriesClient.skyColor = null;
            if (payload.value().size() >= 4) {
                try {
                    double red = Double.parseDouble(payload.value().get(1)) / 255.0;
                    double green = Double.parseDouble(payload.value().get(2)) / 255.0;
                    double blue = Double.parseDouble(payload.value().get(3)) / 255.0;
                    LifeSeriesClient.skyColor = new Vec3(red, green, blue);
                }catch (Exception ignored) {}
            }
        });
        SimplePackets.FOGCOLOR.setClientReceive(payload -> {
            LifeSeriesClient.fogColorSetMode = payload.value().get(0).equalsIgnoreCase("true");
            LifeSeriesClient.fogColor = null;
            if (payload.value().size() >= 4) {
                try {
                    double red = Double.parseDouble(payload.value().get(1)) / 255.0;
                    double green = Double.parseDouble(payload.value().get(2)) / 255.0;
                    double blue = Double.parseDouble(payload.value().get(3)) / 255.0;
                    LifeSeriesClient.fogColor = new Vec3(red, green, blue);
                }catch (Exception ignored) {}
            }
        });
        SimplePackets.CLOUDCOLOR.setClientReceive(payload -> {
            LifeSeriesClient.cloudColorSetMode = payload.value().get(0).equalsIgnoreCase("true");
            LifeSeriesClient.cloudColor = null;
            if (payload.value().size() >= 4) {
                try {
                    double red = Double.parseDouble(payload.value().get(1)) / 255.0;
                    double green = Double.parseDouble(payload.value().get(2)) / 255.0;
                    double blue = Double.parseDouble(payload.value().get(3)) / 255.0;
                    LifeSeriesClient.cloudColor = new Vec3(red, green, blue);
                }catch (Exception ignored) {}
            }
        });
        SimplePackets.PLAYER_INVISIBLE.setClientReceive(payload -> {
            UUID uuid = UUID.fromString(payload.value().get(0));
            long number = Long.parseLong(payload.value().get(1));
            if (number == 0) {
                LifeSeriesClient.invisiblePlayers.remove(uuid);
            }
            else {
                LifeSeriesClient.invisiblePlayers.put(uuid, number);
            }
        });
        SimplePackets.ACTIVE_WILDCARDS.setClientReceive(payload -> {
            List<Wildcards> newList = new ArrayList<>();
            for (String wildcardStr : payload.value()) {
                newList.add(Wildcards.getFromString(wildcardStr));
            }
            if (VersionControl.isDevVersion()) LifeSeries.LOGGER.info("[PACKET_CLIENT] Updated current wildcards to {}", newList);
            LifeSeriesClient.clientActiveWildcards = newList;
        });

        //String payload
        SimplePackets.TEAM_NAME.setClientReceive(payload -> LifeSeriesClient.teamName = payload.value());
        SimplePackets.TEAM_COLOR.setClientReceive(payload -> LifeSeriesClient.teamColor = payload.value());
        SimplePackets.CURRENT_SEASON.setClientReceive(payload -> {
            if (VersionControl.isDevVersion()) LifeSeries.LOGGER.info("[PACKET_CLIENT] Updated current season to {}", payload.value());
            LifeSeriesClient.clientCurrentSeason = Seasons.getSeasonFromStringName(payload.value());
            ClientResourcePacks.checkClientPacks();
            LifeSeriesClient.reloadConfig();
        });
        SimplePackets.SESSION_STATUS.setClientReceive(payload -> {
            LifeSeriesClient.clientSessionStatus = SessionStatus.getSessionName(payload.value());
        });
        SimplePackets.SELECT_SEASON.setClientReceive(payload -> {
            if (!LifeSeries.modDisabled()) {
                Minecraft.getInstance().ls$setScreen(new ChooseSeasonScreen(!payload.value().isEmpty()));
            }
        });
        SimplePackets.SHOW_TOTEM.setClientReceive(payload -> {
            ItemStack totemItem = Items.TOTEM_OF_UNDYING.getDefaultInstance();
            if (payload.value().equalsIgnoreCase("task") || payload.value().equalsIgnoreCase("task_red")) {
                totemItem = AnimationUtils.getSecretLifeTotemItem(payload.value().equalsIgnoreCase("task_red"));
            }
            Minecraft.getInstance().gameRenderer.displayItemActivation(totemItem);
        });


        //Boolean payload
        SimplePackets.POWER_INVISIBILITY_PARTICLES.setClientReceive(payload -> LifeSeriesClient.powerInvisParticles = payload.value());
        SimplePackets.PREVENT_GLIDING.setClientReceive(payload -> LifeSeriesClient.preventGliding = payload.value());
        SimplePackets.TABLIST_SHOW_EXACT.setClientReceive(payload -> LifeSeriesClient.TAB_LIST_SHOW_EXACT_LIVES = payload.value());
        SimplePackets.FIX_SIZECHANGING_BUGS.setClientReceive(payload -> LifeSeriesClient.FIX_SIZECHANGING_BUGS = payload.value());
        SimplePackets.ANIMAL_DISGUISE_ARMOR.setClientReceive(payload -> Morph.showArmor = payload.value());
        SimplePackets.ANIMAL_DISGUISE_HANDS.setClientReceive(payload -> Morph.showHandItems = payload.value());
        SimplePackets.SNOWY_NETHER.setClientReceive(payload -> {
            boolean newValue = payload.value();
            if (LifeSeriesClient.NICELIFE_SNOWY_NETHER != newValue) {
                LifeSeriesClient.NICELIFE_SNOWY_NETHER = newValue;
                ClientResourcePacks.checkClientPacks();
            }
        });
        SimplePackets.EMPTY_SCREEN.setClientReceive(payload -> {
            if (!LifeSeries.modDisabled()) {
                if (payload.value()) {
                    Minecraft.getInstance().ls$setScreen(new EmptySleepScreen(false));
                }
                else {
                    Minecraft.getInstance().ls$setScreen(null);
                }
            }
        });
        SimplePackets.HIDE_SLEEP_DARKNESS.setClientReceive(payload -> {
            LifeSeriesClient.hideSleepDarkness = payload.value();
            LocalPlayer player = Minecraft.getInstance().player;
            if (!LifeSeriesClient.hideSleepDarkness && player != null && player instanceof PlayerAccessor accessor) {
                accessor.ls$setSleepCounter(0);
            }
        });
        SimplePackets.MIC_MUTED.setClientReceive(payload -> {
            if (CompatibilityManager.voicechatLoaded()) {
                VoicechatClient.setMuted(payload.value());
            }
        });
        SimplePackets.ADMIN_INFO.setClientReceive(payload -> LifeSeriesClient.isAdmin = payload.value());
        SimplePackets.TRIPLE_JUMP.setClientReceive(payload -> LifeSeriesClient.tripleJumpActive = payload.value());
        SimplePackets.MOD_DISABLED.setClientReceive(payload -> LifeSeriesClient.modDisabledServerSide = payload.value());

        //Number payload
        SimplePackets.PLAYER_MIN_MSPT.setClientReceive(payload -> {
            if (VersionControl.isDevVersion()) LifeSeries.LOGGER.info("[PACKET_CLIENT] Updated min. player MSPT to {}", payload.number());
            TimeDilation.MIN_PLAYER_MSPT = (float) payload.number();
        });
        SimplePackets.SIZESHIFTING_CHANGE.setClientReceive(payload -> LifeSeriesClient.SIZESHIFTING_CHANGE = (float) payload.number());

        //Integer payload
        SimplePackets.POWER_TJ_JUMPS.setClientReceive(payload -> LifeSeriesClient.powerTripleJumpCount = payload.number());
        SimplePackets.SNAIL_AIR.setClientReceive(payload -> {
            LifeSeriesClient.snailAir = payload.number();
            LifeSeriesClient.snailAirTimestamp = System.currentTimeMillis();
        });
        SimplePackets.FAKE_THUNDER.setClientReceive(payload -> {
            if (Minecraft.getInstance().level != null) {
                Minecraft.getInstance().level.setSkyFlashTime(payload.number());
            }
        });
        SimplePackets.TAB_LIST_LIVES_CUTOFF.setClientReceive(payload -> LifeSeriesClient.TAB_LIST_LIVES_CUTOFF = payload.number());
        SimplePackets.TRIVIA_TIMER.setClientReceive(payload -> Trivia.updateTicksPassed(payload.number()));
        SimplePackets.VOTING_TIME.setClientReceive(payload -> {
            if (Minecraft.getInstance().ls$getScreen() instanceof VotingScreen votingScreen) {
                votingScreen.timerSeconds = payload.number();
            }
        });

        //Long payloads
        SimplePackets.SUPERPOWER_COOLDOWN.setClientReceive(payload -> LifeSeriesClient.SUPERPOWER_COOLDOWN_TIMESTAMP = payload.number());
        SimplePackets.SHOW_VIGNETTE.setClientReceive(payload -> {
            if (VersionControl.isDevVersion()) LifeSeries.LOGGER.info("[PACKET_CLIENT] Showing vignette for {}", payload.number());
            VignetteRenderer.showVignetteFor(0.35f, payload.number());
        });
        SimplePackets.MIMICRY_COOLDOWN.setClientReceive(payload -> LifeSeriesClient.MIMICRY_COOLDOWN_TIMESTAMP = payload.number());
        SimplePackets.TIME_DILATION.setClientReceive(payload -> LifeSeriesClient.TIME_DILATION_TIMESTAMP = payload.number());
        SimplePackets.SESSION_TIMER.setClientReceive(payload -> {
            LifeSeriesClient.sessionTime = payload.number();
            LifeSeriesClient.sessionTimeLastUpdated = System.currentTimeMillis();
        });

        //Empty payloads
        SimplePackets.LIFESKINS_RELOAD_START.setClientReceive(payload -> {
            LifeSkinsClient.clearTextures();
        });
        SimplePackets.JUMP.setClientReceive(payload -> {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.jumpFromGround();
            }
        });
        SimplePackets.RESET_TRIVIA.setClientReceive(payload -> Trivia.resetTrivia());
        SimplePackets.SELECT_WILDCARDS.setClientReceive(payload -> {
            if (!LifeSeries.modDisabled()) {
                Minecraft.getInstance().ls$setScreen(new ChooseWildcardScreen());
            }
        });
        SimplePackets.CLEAR_CONFIG.setClientReceive(payload -> ClientConfigNetwork.load());
        SimplePackets.OPEN_CONFIG.setClientReceive(payload -> ClientConfigGuiManager.openConfig());
        SimplePackets.TOGGLE_TIMER.setClientReceive(payload -> {
            String key = ClientConfig.SESSION_TIMER.key;
            LifeSeriesClient.clientConfig.setProperty(key, String.valueOf(!LifeSeriesClient.SESSION_TIMER));
            LifeSeriesClient.reloadConfig();
        });
        SimplePackets.PAST_LIFE_CHOOSE_TWIST.setClientReceive(payload -> {
            if (!LifeSeries.modDisabled()) {
                Minecraft.getInstance().ls$setScreen(new PastLifeChooseTwistScreen());
            }
        });
        SimplePackets.TRIVIA_ALL_WRONG.setClientReceive(payload -> {
            ClientLevel level = Minecraft.getInstance().level;
            LocalPlayer player = Minecraft.getInstance().player;
            if (level != null && player != null) {
                level.addParticle(ParticleRegistry.TRIVIA_SPIRIT, player.getX(), player.getY(), player.getZ(), 0.0, 0.0, 0.0);
            }
        });
        SimplePackets.STOP_TRIVIA_SOUNDS.setClientReceive(payload -> ClientSounds.stopTriviaSounds());
        SimplePackets.REMOVE_SLEEP_SCREENS.setClientReceive(payload -> {
            Minecraft client = Minecraft.getInstance();
            if (client.ls$getScreen() instanceof EmptySleepScreen || client.ls$getScreen() instanceof NewQuizScreen || (client.ls$getScreen() instanceof VotingScreen votingScreen && votingScreen.requiresSleep)) {
                client.ls$setScreen(null);
            }
        });
        SimplePackets.SUPERPOWER_SHOW_COOLDOWN.setClientReceive(payload -> {
            TextHud.lastPressedSuperpowerKey = System.currentTimeMillis();
        });

        /*

        SimplePackets._______.setClientReceive(payload -> {
        });

        SimplePackets._______.setClientReceive(payload -> );

         */
    }

    //? if <= 1.20.3 {
    /*public static void registerClientReceiver() {
        ClientLoginNetworking.registerGlobalReceiver(IdentifierHelper.mod("preloginpacket"),
                (client, handler, buf, listenerAdder) -> {
                    FriendlyByteBuf response = PacketByteBufs.create();
                    response.writeBoolean(true);
                    return CompletableFuture.completedFuture(response);
                }
        );


        ClientPlayNetworking.registerGlobalReceiver(HandshakePayload.ID, (client, handler, buf, responseSender) -> {
            HandshakePayload payload = HandshakePayload.read(buf);
            client.execute(() -> handleHandshake(payload));
        });

        ClientPlayNetworking.registerGlobalReceiver(TriviaQuestionPayload.ID, (client, handler, buf, responseSender) -> {
            TriviaQuestionPayload payload = TriviaQuestionPayload.read(buf);
            client.execute(() -> Trivia.receiveTrivia(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(VoteScreenPayload.ID, (client, handler, buf, responseSender) -> {
            VoteScreenPayload payload = VoteScreenPayload.read(buf);
            client.execute(() -> handleVoteScreen(payload));
        });


        ClientPlayNetworking.registerGlobalReceiver(PlayerDisguisePayload.ID, (client, handler, buf, responseSender) -> {
            PlayerDisguisePayload payload = PlayerDisguisePayload.read(buf);
            client.execute(() -> handlePlayerDisguise(payload));
        });

        ClientPlayNetworking.registerGlobalReceiver(ConfigPayload.ID, (client, handler, buf, responseSender) -> {
            ConfigPayload payload = ConfigPayload.read(buf);
            client.execute(() -> handleConfigPacket(payload));
        });

        ClientPlayNetworking.registerGlobalReceiver(SidetitlePacket.ID, (client, handler, buf, responseSender) -> {
            SidetitlePacket payload = SidetitlePacket.read(buf);
            client.execute(() -> handleSidetitle(payload));
        });

        ClientPlayNetworking.registerGlobalReceiver(SnailTexturePacket.ID, (client, handler, buf, responseSender) -> {
            SnailTexturePacket payload = SnailTexturePacket.read(buf);
            client.execute(() -> {
                SnailSkinsClient.handleSnailTexture(payload.skinName(), payload.textureData());
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(LifeSkinsTexturePayload.ID, (client, handler, buf, responseSender) -> {
            LifeSkinsTexturePayload payload = LifeSkinsTexturePayload.read(buf);
            client.execute(() -> {
                LifeSkinsClient.handleTexture(payload.skinName(), payload.teamName(), payload.slim(), payload.textureData());
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(NumberPayload.ID, (client, handler, buf, responseSender) -> {
            NumberPayload payload = NumberPayload.read(buf);
            SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(payload.name());
            if (packet != null) client.execute(() -> packet.receiveClient(payload));
        });

        ClientPlayNetworking.registerGlobalReceiver(StringPayload.ID, (client, handler, buf, responseSender) -> {
            StringPayload payload = StringPayload.read(buf);
            SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(payload.name());
            if (packet != null) client.execute(() -> packet.receiveClient(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(LongPayload.ID, (client, handler, buf, responseSender) -> {
            LongPayload payload = LongPayload.read(buf);
            SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(payload.name());
            if (packet != null) client.execute(() -> packet.receiveClient(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(StringListPayload.ID, (client, handler, buf, responseSender) -> {
            StringListPayload payload = StringListPayload.read(buf);
            SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(payload.name());
            if (packet != null) client.execute(() -> packet.receiveClient(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(BooleanPayload.ID, (client, handler, buf, responseSender) -> {
            BooleanPayload payload = BooleanPayload.read(buf);
            SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(payload.name());
            if (packet != null) client.execute(() -> packet.receiveClient(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(IntPayload.ID, (client, handler, buf, responseSender) -> {
            IntPayload payload = IntPayload.read(buf);
            SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(payload.name());
            if (packet != null) client.execute(() -> packet.receiveClient(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(EmptyPayload.ID, (client, handler, buf, responseSender) -> {
            EmptyPayload payload = EmptyPayload.read(buf);
            SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(payload.name());
            if (packet != null) client.execute(() -> packet.receiveClient(payload));
        });
    }
    *///?} else {
    public static void registerClientReceiver() {
        ClientLoginNetworking.registerGlobalReceiver(IdentifierHelper.mod("preloginpacket"),
                (client, handler, buf, listenerAdder) -> {
                    return CompletableFuture.completedFuture(
                            //? if <= 1.21.11 {
                            /*PacketByteBufs.create().writeBoolean(true)
                            *///?} else {
                            FriendlyByteBufs.create().writeBoolean(true)
                            //?}
                    );
                }
        );

        ClientPlayNetworking.registerGlobalReceiver(HandshakePayload.ID, (payload, context) -> {
            Minecraft client = context.client();
            client.execute(() -> handleHandshake(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(TriviaQuestionPayload.ID, (payload, context) -> {
            Minecraft client = context.client();
            client.execute(() -> Trivia.receiveTrivia(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(VoteScreenPayload.ID, (payload, context) -> {
            Minecraft client = context.client();
            client.execute(() -> handleVoteScreen(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(PlayerDisguisePayload.ID, (payload, context) -> {
            Minecraft client = context.client();
            client.execute(() -> handlePlayerDisguise(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(ConfigPayload.ID, (payload, context) -> {
            Minecraft client = context.client();
            client.execute(() -> handleConfigPacket(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(SidetitlePacket.ID, (payload, context) -> {
            Minecraft client = context.client();
            client.execute(() -> handleSidetitle(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(SnailTexturePacket.ID, (payload, context) -> {
            context.client().execute(() -> {
                SnailSkinsClient.handleSnailTexture(payload.skinName(), payload.textureData());
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(LifeSkinsTexturePayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                LifeSkinsClient.handleTexture(payload.skinName(), payload.teamName(), payload.slim(), payload.textureData());
            });
        });

        //Simple Packets
        Minecraft client = Minecraft.getInstance();
        ClientPlayNetworking.registerGlobalReceiver(StringListPayload.ID, (payload, context) -> {
            SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(payload.name());
            if (packet != null) client.execute(() -> packet.receiveClient(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(NumberPayload.ID, (payload, context) -> {
            SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(payload.name());
            if (packet != null) client.execute(() -> packet.receiveClient(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(StringPayload.ID, (payload, context) -> {
            SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(payload.name());
            if (packet != null) client.execute(() -> packet.receiveClient(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(LongPayload.ID, (payload, context) -> {
            SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(payload.name());
            if (packet != null) client.execute(() -> packet.receiveClient(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(BooleanPayload.ID, (payload, context) -> {
            SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(payload.name());
            if (packet != null) client.execute(() -> packet.receiveClient(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(EmptyPayload.ID, (payload, context) -> {
            SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(payload.name());
            if (packet != null) client.execute(() -> packet.receiveClient(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(IntPayload.ID, (payload, context) -> {
            SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(payload.name());
            if (packet != null) client.execute(() -> packet.receiveClient(payload));
        });
    }
    //?}

    public static void handleSidetitle(SidetitlePacket payload) {
        LifeSeriesClient.sideTitle = payload.text();
        Minecraft client = Minecraft.getInstance();
        if (client.gui instanceof GuiAccessor hudAccessor) {
            TextHud.sideTitleRemainTicks = hudAccessor.ls$titleFadeInTicks() + hudAccessor.ls$titleStayTicks() + hudAccessor.ls$titleFadeOutTicks();
        }
    }

    public static void handleConfigPacket(ConfigPayload payload) {
        ClientConfigNetwork.handleConfigPacket(payload, false);
    }

    public static void handlePlayerDisguise(PlayerDisguisePayload payload) {

        String hiddenUUID = payload.hiddenUUID();
        String hiddenName = payload.hiddenName();
        String shownUUID = payload.shownUUID();
        String shownName = payload.shownName();
        if (shownName.isEmpty()) {
            LifeSeriesClient.playerDisguiseNames.remove(hiddenName);
            try {
                UUID hideUUID = UUID.fromString(hiddenUUID);
                LifeSeriesClient.playerDisguiseUUIDs.remove(hideUUID);
            }catch(Exception ignored) {}
        }
        else {
            LifeSeriesClient.playerDisguiseNames.put(hiddenName, shownName);
            try {
                UUID hideUUID = UUID.fromString(hiddenUUID);
                UUID showUUID = UUID.fromString(shownUUID);
                LifeSeriesClient.playerDisguiseUUIDs.put(hideUUID, showUUID);
            }catch(Exception ignored) {}
        }
    }
    public static void handleHandshake(HandshakePayload payload) {
        LifeSeriesClient.serverHandshake = HandshakeStatus.RECEIVED;

        String serverVersionStr = payload.modVersionStr();
        String serverCompatibilityStr = payload.compatibilityStr();
        String clientVersionStr = LifeSeries.MOD_VERSION;
        String clientCompatibilityStr = VersionControl.clientCompatibilityMin();

        if (!LifeSeries.ISOLATED_ENVIRONMENT) {
            int serverVersion = payload.modVersion();
            int serverCompatibility = payload.compatibility();
            int clientVersion = VersionControl.getModVersionInt(clientVersionStr);
            int clientCompatibility = VersionControl.getModVersionInt(clientCompatibilityStr);

            //Check if client version is compatible with the server version
            if (clientVersion < serverCompatibility) {
                Component disconnectText = Component.literal("[Life Series Mod] Client-Server version mismatch!\n" +
                        "Update the client version to at least version "+serverCompatibilityStr);
                ClientUtils.disconnect(disconnectText);
                return;
            }

            //Check if server version is compatible with the client version
            if (serverVersion < clientCompatibility) {
                Component disconnectText = Component.literal("[Life Series Mod] Server-Client version mismatch!\n" +
                        "The client version is too new for the server.\n" +
                        "Either update the server, or downgrade the client version to " + serverVersionStr);
                ClientUtils.disconnect(disconnectText);
                return;
            }
        }
        else {
            //Isolated enviroment -> mod versions must be IDENTICAL between client and server
            //Check if client version is the same as the server version
            if (!clientVersionStr.equalsIgnoreCase(serverVersionStr)) {
                Component disconnectText = Component.literal("[Life Series Mod] Client-Server version mismatch!\n" +
                        "You must join with version "+serverCompatibilityStr);
                ClientUtils.disconnect(disconnectText);
                return;
            }
        }

        LifeSeries.LOGGER.info(TextUtils.formatString("[PACKET_CLIENT] Received handshake (from server): {{}, {}}", payload.modVersionStr(), payload.modVersion()));
        sendHandshake();
    }

    public static void handleVoteScreen(VoteScreenPayload payload) {
        if (LifeSeries.modDisabled()) return;
        Minecraft.getInstance().ls$setScreen(new VotingScreen(payload.name(), payload.requiresSleep(), payload.closesWithEsc(), payload.showTimer(), payload.players()));
    }

    /*
        Sending
     */

    public static void sendHandshake() {
        String clientVersionStr = LifeSeries.MOD_VERSION;
        String clientCompatibilityStr = VersionControl.clientCompatibilityMin();

        int clientVersion = VersionControl.getModVersionInt(clientVersionStr);
        int clientCompatibility = VersionControl.getModVersionInt(clientCompatibilityStr);

        HandshakePayload sendPayload = new HandshakePayload(clientVersionStr, clientVersion, clientCompatibilityStr, clientCompatibility);
        ClientPlayNetworking.send(sendPayload);
        if (VersionControl.isDevVersion()) LifeSeries.LOGGER.info("[PACKET_CLIENT] Sent handshake");
    }

    public static void sendConfigUpdate(String configType, String id, List<String> args) {
        ConfigPayload configPacket = new ConfigPayload(configType, id, -1, "", "", args);
        ClientPlayNetworking.send(configPacket);
    }

    public static void sendTriviaAnswer(int answer) {
        if (VersionControl.isDevVersion()) LifeSeries.LOGGER.info("[PACKET_CLIENT] Sending trivia answer: {}", answer);
        SimplePackets.TRIVIA_ANSWER.sendToServer(answer);
    }

    public static void sendHoldingJumpPacket() {
        SimplePackets.HOLDING_JUMP.sendToServer();
    }

    public static void pressSuperpowerKey() {
        SimplePackets.SUPERPOWER_KEY.sendToServer();
    }
    public static void pressRunCommandKey() {
        ClientUtils.runCommand(LifeSeriesClient.RUN_COMMAND);
    }
    public static void pressOpenConfigKey() {
        ClientUtils.runCommand("/lifeseries config");
    }
}
