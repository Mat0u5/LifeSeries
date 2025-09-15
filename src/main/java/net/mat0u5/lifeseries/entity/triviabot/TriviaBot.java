package net.mat0u5.lifeseries.entity.triviabot;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.api.AnimatedEntityHolder;
import de.tomalbrc.bil.api.Animator;
import de.tomalbrc.bil.core.holder.entity.EntityHolder;
import de.tomalbrc.bil.core.holder.entity.living.LivingEntityHolder;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.file.loader.BbModelLoader;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import eu.pb4.polymer.virtualentity.api.elements.VirtualElement;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.dependencies.DependencyManager;
import net.mat0u5.lifeseries.entity.AnimationHandler;
import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.entity.triviabot.goal.TriviaBotGlideGoal;
import net.mat0u5.lifeseries.entity.triviabot.goal.TriviaBotLookAtPlayerGoal;
import net.mat0u5.lifeseries.entity.triviabot.goal.TriviaBotTeleportGoal;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.registries.MobRegistry;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.SizeShifting;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaQuestion;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaWildcard;
import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.AttributeUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.AnimationUtils;
import net.mat0u5.lifeseries.utils.world.ItemSpawner;
import net.mat0u5.lifeseries.utils.world.ItemStackUtils;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.mat0u5.lifeseries.voicechat.VoicechatMain;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static net.mat0u5.lifeseries.Main.server;
//? if >= 1.21.4
/*import net.minecraft.util.Uuids;*/
//? if >= 1.21.6 {
/*import net.minecraft.util.ErrorReporter;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
*///?}

public class TriviaBot extends AmbientEntity implements AnimatedEntity {
    public static final Identifier ID = Identifier.of(Main.MOD_ID, "triviabot");
    public static final Model MODEL = BbModelLoader.load(ID);

    public static final int STATIONARY_TP_COOLDOWN = 400; // No movement for 20 seconds teleports the bot
    public static final float MOVEMENT_SPEED = 0.45f;
    public static final int MAX_DISTANCE = 100;
    public static boolean CAN_START_RIDING = true;
    public static ItemSpawner itemSpawner;
    public static int EASY_TIME = 180;
    public static int NORMAL_TIME = 240;
    public static int HARD_TIME = 300;

    public boolean gliding = false;

    public boolean interactedWith = false;
    public long interactedAt = 0;
    public int timeToComplete = 0;
    public int difficulty = 0;
    public boolean submittedAnswer = false;
    public Boolean answeredRight = null;
    public boolean ranOutOfTime = false;
    public int snailTransformation = 0;
    public TriviaQuestion question;

    public int nullPlayerChecks = 0;

    private final EntityHolder<TriviaBot> holder;
    public UUID boundPlayerUUID;

    public TriviaBot(EntityType<? extends AmbientEntity> entityType, World world) {
        super(entityType, world);
        this.holder = new LivingEntityHolder<>(this, MODEL);
        EntityAttachment.ofTicking(holder, this);
        setInvulnerable(true);
        setPersistent();
        updateNavigation();
    }

    @Override
    public AnimatedEntityHolder getHolder() {
        return holder;
    }

    //? if <= 1.21.5 {
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
    //?} else {
    /*@Override
    public void writeCustomData(WriteView nbt) {
        super.writeCustomData(nbt);
    *///?}
        try {
            if (boundPlayerUUID == null) return;
            //? if <= 1.21.4 {
            nbt.putUuid("boundPlayer", boundPlayerUUID);
            //?} else {
            /*nbt.putNullable("boundPlayer", Uuids.INT_STREAM_CODEC, boundPlayerUUID);
            *///?}
        }catch(Exception e) {}
    }

    //? if <= 1.21.5 {
    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
    //?} else {
    /*@Override
    public void readCustomData(ReadView nbt) {
        super.readCustomData(nbt);
    *///?}
        try {
            //? if <= 1.21.4 {
            UUID newUUID = nbt.getUuid("boundPlayer");
            if (newUUID != null) {
                boundPlayerUUID = newUUID;
            }
            //?} else if <= 1.21.5 {
            /*UUID newUUID = nbt.get("boundPlayer", Uuids.INT_STREAM_CODEC).orElse(null);
            if (newUUID != null) {
                boundPlayerUUID = newUUID;
            }
            *///?} else {
            /*UUID newUUID = nbt.read("boundPlayer", Uuids.INT_STREAM_CODEC).orElse(null);
            if (newUUID != null) {
                boundPlayerUUID = newUUID;
            }
            *///?}
        }catch(Exception e) {}
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        //? if <= 1.21 {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10000)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, MOVEMENT_SPEED)
                .add(EntityAttributes.GENERIC_STEP_HEIGHT, 1)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 100)
                .add(EntityAttributes.GENERIC_WATER_MOVEMENT_EFFICIENCY, 1)
                .add(EntityAttributes.GENERIC_SAFE_FALL_DISTANCE, 100)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0);
        //?} else {
        /*return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 10000)
                .add(EntityAttributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(EntityAttributes.FLYING_SPEED, MOVEMENT_SPEED)
                .add(EntityAttributes.STEP_HEIGHT, 1)
                .add(EntityAttributes.FOLLOW_RANGE, 100)
                .add(EntityAttributes.WATER_MOVEMENT_EFFICIENCY, 1)
                .add(EntityAttributes.SAFE_FALL_DISTANCE, 100)
                .add(EntityAttributes.ATTACK_DAMAGE, 0);
        *///?}
    }

    @Nullable
    public ServerPlayerEntity getBoundPlayer() {
        if (server == null) return null;
        ServerPlayerEntity player = PlayerUtils.getPlayer(boundPlayerUUID);
        if (player == null || (player.isSpectator() && !player.isAlive())) {
            nullPlayerChecks++;
            return null;
        }
        nullPlayerChecks = 0;
        if (player.isSpectator()) return null;
        if (!player.isAlive()) return null;
        return player;
    }

    @Nullable
    public ServerPlayerEntity getActualBoundPlayer() {
        if (server == null) return null;
        return PlayerUtils.getPlayer(boundPlayerUUID);
    }

    public void setBoundPlayer(ServerPlayerEntity player) {
        if (player == null) return;
        boundPlayerUUID = player.getUuid();
        //? if <= 1.21.5 {
        writeCustomDataToNbt(new NbtCompound());
        //?} else {
        /*writeCustomData(NbtWriteView.create(ErrorReporter.EMPTY));
        *///?}
        sendDisplayEntityPackets(player);
    }

    public void sendDisplayEntityPackets(ServerPlayerEntity player) {
        List<VirtualElement> elements = holder.getElements();
        for (VirtualElement element : elements) {
            if (element instanceof ItemDisplayElement itemDisplayElement) {
                NetworkHandlerServer.sendStringPacket(player, PacketNames.TRIVIA_BOT_PART, itemDisplayElement.getUuid().toString());
            }
        }
    }

    @Override
    protected void initGoals() {
        goalSelector.add(0, new TriviaBotTeleportGoal(this));
        goalSelector.add(1, new TriviaBotGlideGoal(this));
        goalSelector.add(2, new TriviaBotLookAtPlayerGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        if (age % 100 == 0) {
            if (!TriviaWildcard.bots.containsValue(this) || !WildcardManager.isActiveWildcard(Wildcards.TRIVIA)) {
                despawn();
            }
        }
        if (age % 400 == 0 && getActualBoundPlayer() != null) {
            sendDisplayEntityPackets(getActualBoundPlayer());
        }

        if (submittedAnswer && answeredRight != null) {
            if (answeredRight) {
                if (analyzing < -80) {
                    if (hasVehicle()) dismountVehicle();
                    noClip = true;
                    float velocity = Math.min(0.5f, 0.25f * Math.abs((analyzing+80) / (20.0f)));
                    setVelocity(0,velocity,0);
                    if (analyzing < -200) despawn();
                }
            }
            else {
                if (analyzing < -100) {
                    if (hasVehicle()) dismountVehicle();
                    noClip = true;
                    float velocity = Math.min(0.5f, 0.25f * Math.abs((analyzing+100) / (20.0f)));
                    setVelocity(0,velocity,0);
                    if (analyzing < -200) despawn();
                }
            }
        }
        else {
            handleHighVelocity();
            if (!interactedWith) {
                ServerPlayerEntity boundPlayer = getBoundPlayer();
                if (boundPlayer != null) {
                    if (age % 5 == 0) {
                        updateNavigationTarget();
                    }
                }
            }
            if (interactedWith && getRemainingTime() <= 0) {
                if (!ranOutOfTime) {
                    ServerPlayerEntity boundPlayer = getBoundPlayer();
                    if (boundPlayer != null) {
                        NetworkHandlerServer.sendStringPacket(boundPlayer, PacketNames.RESET_TRIVIA, "true");
                    }
                }
                ranOutOfTime = true;
            }
            if (snailTransformation > 33) {
                transformIntoSnail();
            }
        }

        if (nullPlayerChecks > 1000) {
            despawn();
        }

        if (age % 2 == 0) {
            updateAnimations();
        }


        chunkLoading();
        clearStatusEffects();
        playSounds();
    }

    public void handleHighVelocity() {
        Vec3d velocity = getVelocity();
        if (velocity.y > 0.15) {
            setVelocity(velocity.x,0.15,velocity.z);
        }
        else if (velocity.y < -0.15) {
            setVelocity(velocity.x,-0.15,velocity.z);
        }
    }

    public void chunkLoading() {
        if (getWorld() instanceof ServerWorld world) {
            addTicket(world);
        }
    }

    public void addTicket(ServerWorld world) {
        //? if <= 1.21.4 {
        world.getChunkManager().addTicket(ChunkTicketType.PORTAL, new ChunkPos(getBlockPos()), 2, getBlockPos());
         //?} else {
        /*world.getChunkManager().addTicket(ChunkTicketType.PORTAL, new ChunkPos(getBlockPos()), 2);
        *///?}
    }

    public void despawn() {
        if (boundPlayerUUID != null) {
            TriviaWildcard.bots.remove(boundPlayerUUID);
        }
        //? if <= 1.21 {
        this.kill();
        //?} else {
        /*this.kill((ServerWorld) getWorld());
         *///?}
        this.discard();
    }

    public void transformIntoSnail() {
        if (getBoundPlayer() != null) {
            Snail triviaSnail = MobRegistry.SNAIL.spawn((ServerWorld) getWorld(), this.getBlockPos(), SpawnReason.COMMAND);
            if (triviaSnail != null) {
                triviaSnail.setBoundPlayer(getBoundPlayer());
                triviaSnail.setFromTrivia();
                triviaSnail.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE.value(), 0.5f, 2);
                ServerWorld world = (ServerWorld) triviaSnail.getWorld();
                world.spawnParticles(
                        ParticleTypes.EXPLOSION,
                        this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(),
                        10, 0.5, 0.5, 0.5, 0.5
                );
                TriviaWildcard.snails.put(getBoundPlayer().getUuid(), triviaSnail);
            }
        }
        despawn();
    }

    private int analyzing = -1;
    public void updateAnimations() {
        AnimationHandler.updateHurtVariant(this, holder);
        Animator animator = holder.getAnimator();
        if (ranOutOfTime) {
            if (snailTransformation == 0) {
                pauseAllAnimations("snail_transform");
                animator.playAnimation("snail_transform", 8);
            }
            snailTransformation++;
        }
        else if (analyzing > 0) {
            analyzing--;
            pauseAllAnimations("analyzing");
        }
        else if (submittedAnswer && answeredRight != null) {
            if (analyzing == 0) {
                if (answeredRight) {
                    pauseAllAnimations("answer_correct");
                    animator.playAnimation("answer_correct", 7);
                }
                else {
                    pauseAllAnimations("answer_incorrect");
                    animator.playAnimation("answer_incorrect", 6);
                }
            }
            analyzing--;
        }
        else if (interactedWith) {
            pauseAllAnimations("countdown");
            animator.playAnimation("countdown", 4);
        }
        else if (this.gliding) {
            pauseAllAnimations("glide");
            animator.playAnimation("glide", 3);
        }
        else if (this.limbAnimator.isLimbMoving() && this.limbAnimator.getSpeed() > 0.02) {
            pauseAllAnimations("walk");
            animator.playAnimation("walk", 1);
        }
        else {
            pauseAllAnimations("idle");
            animator.playAnimation("idle", 0, true);
        }
    }

    public void playAnalyzingAnimation() {
        Animator animator = holder.getAnimator();
        pauseAllAnimations("analyzing");
        animator.playAnimation("analyzing", 5);
        analyzing = 42;
    }

    public void pauseAllAnimations(String except) {
        Animator animator = holder.getAnimator();
        if (!except.equalsIgnoreCase("glide")) animator.pauseAnimation("glide");
        if (!except.equalsIgnoreCase("walk")) animator.pauseAnimation("walk");
        if (!except.equalsIgnoreCase("idle")) animator.pauseAnimation("idle");
        if (!except.equalsIgnoreCase("countdown")) animator.pauseAnimation("countdown");
        if (!except.equalsIgnoreCase("analyzing")) animator.pauseAnimation("analyzing");
        if (!except.equalsIgnoreCase("answer_incorrect")) animator.pauseAnimation("answer_incorrect");
        if (!except.equalsIgnoreCase("answer_correct")) animator.pauseAnimation("answer_correct");
        if (!except.equalsIgnoreCase("snail_transform")) animator.pauseAnimation("snail_transform");
    }

    public void fakeTeleportToPlayer() {
        ServerPlayerEntity player = getBoundPlayer();
        if (player == null) return;
        ServerWorld playerWorld = PlayerUtils.getServerWorld(player);
        if (getWorld() instanceof ServerWorld world) {
            BlockPos tpTo = getBlockPosNearTarget(player, player.getBlockPos(),5);
            world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_TELEPORT, this.getSoundCategory(), this.getSoundVolume(), this.getSoundPitch());
            playerWorld.playSound(null, tpTo.getX(), tpTo.getY(), tpTo.getZ(), SoundEvents.ENTITY_PLAYER_TELEPORT, this.getSoundCategory(), this.getSoundVolume(), this.getSoundPitch());
            AnimationUtils.spawnTeleportParticles(world, getPos());
            AnimationUtils.spawnTeleportParticles(playerWorld, tpTo.toCenterPos());
            despawn();
            TriviaWildcard.spawnBotFor(player, tpTo);
        }
    }

    public int getRemainingTime() {
        int timeSinceStart = (int) Math.ceil((System.currentTimeMillis() - interactedAt) / 1000.0);
        return timeToComplete - timeSinceStart;
    }

    public long getRemainingTimeMs() {
        long timeSinceStart = System.currentTimeMillis() - interactedAt;
        return (timeToComplete * 1000L) - timeSinceStart;
    }

    public static BlockPos getBlockPosNearTarget(ServerPlayerEntity target, BlockPos targetPos, double distanceFromTarget) {
        if (target == null) return targetPos;
        return WorldUtils.getCloseBlockPos(PlayerUtils.getServerWorld(target), targetPos, distanceFromTarget, 2, false);
    }


    public void updateNavigation() {
        moveControl = new MoveControl(this);
        navigation = new MobNavigation(this, getWorld());
        updateNavigationTarget();
    }

    public void updateNavigationTarget() {
        if (getBoundPlayer() == null) return;
        if (this.distanceTo(getBoundPlayer()) > MAX_DISTANCE) return;
        navigation.setSpeed(MOVEMENT_SPEED);
        Path path = navigation.findPathTo(getBoundPlayer(), 3);
        if (path != null) navigation.startMovingAlong(path, MOVEMENT_SPEED);
    }

    @Nullable
    public BlockPos getGroundBlock() {
        Vec3d startPos = getPos();
        Vec3d endPos = startPos.add(0, getWorld().getBottomY(), 0);

        BlockHitResult result = getWorld().raycast(
                new RaycastContext(
                        startPos,
                        endPos,
                        RaycastContext.ShapeType.COLLIDER,
                        RaycastContext.FluidHandling.NONE,
                        this
                )
        );
        if (result.getType() == HitResult.Type.MISS) return null;
        return result.getBlockPos();
    }

    public double getDistanceToGroundBlock() {
        BlockPos belowBlock = getGroundBlock();
        if (belowBlock == null) return Double.NEGATIVE_INFINITY;
        return getY() - belowBlock.getY() - 1;
    }

    private int introSoundCooldown = 0;
    private boolean playedCountdownSound = false;
    private boolean playedCountdownEndingSound = false;
    public void playSounds() {
        if (introSoundCooldown > 0) introSoundCooldown--;

        if (introSoundCooldown == 0 && !interactedWith) {
            SoundEvent sound = SoundEvent.of(Identifier.ofVanilla("wildlife_trivia_intro"));
            PlayerUtils.playSoundWithSourceToPlayers(PlayerUtils.getAllPlayers(), this, sound, SoundCategory.NEUTRAL, 1, 1);
            introSoundCooldown = 830;
        }

        if (!playedCountdownEndingSound && interactedWith && !submittedAnswer && !ranOutOfTime && getRemainingTimeMs() <= 33800) {
            PlayerUtils.playSoundWithSourceToPlayers(
                    PlayerUtils.getAllPlayers(), this,
                    SoundEvent.of(Identifier.ofVanilla("wildlife_trivia_suspense_end")),
                    SoundCategory.NEUTRAL, 0.65f, 1);
            playedCountdownEndingSound = true;
            playedCountdownSound = true;
        }
        else if (!playedCountdownSound && interactedWith && !submittedAnswer && !ranOutOfTime) {
            PlayerUtils.playSoundWithSourceToPlayers(
                    PlayerUtils.getAllPlayers(), this,
                    SoundEvent.of(Identifier.ofVanilla("wildlife_trivia_suspense")),
                    SoundCategory.NEUTRAL, 0.65f, 1);
            playedCountdownSound = true;
        }
    }


    /*
        Trivia stuff
     */

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ServerPlayerEntity boundPlayer = getBoundPlayer();
        if (boundPlayer == null) return ActionResult.PASS;
        if (boundPlayer.getUuid() != player.getUuid()) return ActionResult.PASS;
        if (submittedAnswer) return ActionResult.PASS;
        if (interactedWith && getRemainingTime() <= 0) return ActionResult.PASS;

        if (!interactedWith || question == null) {
            interactedAt = System.currentTimeMillis();
            difficulty = 1+getRandom().nextInt(3);
            timeToComplete = difficulty * 60 + 120;
            if (difficulty == 1) timeToComplete = EASY_TIME;
            if (difficulty == 2) timeToComplete = NORMAL_TIME;
            if (difficulty == 3) timeToComplete = HARD_TIME;
            question = TriviaWildcard.getTriviaQuestion(difficulty);
        }
        NetworkHandlerServer.sendTriviaPacket(boundPlayer, question.getQuestion(), difficulty, interactedAt, timeToComplete, question.getAnswers());
        interactedWith = true;

        return ActionResult.PASS;
    }

    public void handleAnswer(int answer) {
        if (submittedAnswer) return;
        submittedAnswer = true;
        PlayerUtils.playSoundWithSourceToPlayers(
                PlayerUtils.getAllPlayers(), this,
                SoundEvent.of(Identifier.ofVanilla("wildlife_trivia_analyzing")),
                SoundCategory.NEUTRAL, 1f, 1);
        if (answer == question.getCorrectAnswerIndex()) {
            answeredCorrect();
            TaskScheduler.scheduleTask(72, () -> {
                PlayerUtils.playSoundWithSourceToPlayers(
                        PlayerUtils.getAllPlayers(), this,
                        SoundEvent.of(Identifier.ofVanilla("wildlife_trivia_correct")),
                        SoundCategory.NEUTRAL, 1f, 1);
            });
        }
        else {
            answeredIncorrect();
            TaskScheduler.scheduleTask(72, () -> {
                PlayerUtils.playSoundWithSourceToPlayers(
                        PlayerUtils.getAllPlayers(), this,
                        SoundEvent.of(Identifier.ofVanilla("wildlife_trivia_incorrect")),
                        SoundCategory.NEUTRAL, 1f, 1);
            });
        }
    }

    public void answeredCorrect() {
        answeredRight = true;
        playAnalyzingAnimation();
        TaskScheduler.scheduleTask(145, this::spawnItemForPlayer);
        TaskScheduler.scheduleTask(170, this::spawnItemForPlayer);
        TaskScheduler.scheduleTask(198, this::spawnItemForPlayer);
        TaskScheduler.scheduleTask(213, this::blessPlayer);
    }

    public void answeredIncorrect() {
        answeredRight = false;
        playAnalyzingAnimation();
        TaskScheduler.scheduleTask(210, this::cursePlayer);
    }

    public void cursePlayer() {
        ServerPlayerEntity player = getBoundPlayer();
        if (player == null) return;
        player.playSoundToPlayer(SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.MASTER, 0.2f, 1f);
        ServerWorld world = (ServerWorld) getWorld();
        Vec3d pos = getPos();

        world.spawnParticles(
                EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, 0xFFa61111),
                pos.getX(), pos.getY()+1, pos.getZ(),
                40, 0.1, 0.25, 0.1, 0.035
        );
        int numOfCurses = 9;
        if (DependencyManager.voicechatLoaded() && VoicechatMain.isConnectedToSVC(player.getUuid())) numOfCurses = 10;
        int curse = world.random.nextInt(numOfCurses);
        switch (curse) {
            default:
            case 0:
                curseHunger(player);
                break;
            case 1:
                curseRavager(player);
                break;
            case 2:
                curseInfestation(player);
                break;
            case 3:
                curseGigantification(player);
                break;
            case 4:
                curseSlipperyGround(player);
                break;
            case 5:
                curseBindingArmor(player);
                break;
            case 6:
                curseHearts(player);
                break;
            case 7:
                curseMoonjump(player);
                break;
            case 8:
                curseBeeswarm(player);
                break;
            case 9:
                curseRoboticVoice(player);
                break;
        }
    }

    private static final List<RegistryEntry<StatusEffect>> blessEffects = List.of(
            StatusEffects.SPEED,
            StatusEffects.HASTE,
            StatusEffects.STRENGTH,
            StatusEffects.JUMP_BOOST,
            StatusEffects.REGENERATION,
            StatusEffects.RESISTANCE,
            StatusEffects.FIRE_RESISTANCE,
            StatusEffects.WATER_BREATHING,
            StatusEffects.NIGHT_VISION,
            StatusEffects.HEALTH_BOOST,
            StatusEffects.ABSORPTION
    );
    public void blessPlayer() {
        ServerPlayerEntity player = getBoundPlayer();
        if (player == null) return;
        player.sendMessage(Text.empty());
        for (int i = 0; i < 3; i++) {
            RegistryEntry<StatusEffect> effect = blessEffects.get(player.getRandom().nextInt(blessEffects.size()));
            int amplifier;
            if (effect == StatusEffects.FIRE_RESISTANCE || effect == StatusEffects.WATER_BREATHING || effect == StatusEffects.NIGHT_VISION ||
                    effect == StatusEffects.REGENERATION || effect == StatusEffects.STRENGTH || effect == StatusEffects.HEALTH_BOOST || effect == StatusEffects.RESISTANCE) {
                amplifier = 0;
            }
            else {
                amplifier = player.getRandom().nextInt(4);
            }
            if (WildcardManager.isActiveWildcard(Wildcards.CALLBACK)) {
                player.addStatusEffect(new StatusEffectInstance(effect, 12000, amplifier));
            }
            else {
                player.addStatusEffect(new StatusEffectInstance(effect, 24000, amplifier));
            }

            String romanNumeral = TextUtils.toRomanNumeral(amplifier + 1);
            Text effectName = Text.translatable(effect.value().getTranslationKey());
            player.sendMessage(TextUtils.formatLoosely(" §a§l+ §7{}§6 {}", effectName, romanNumeral));
        }
        player.sendMessage(Text.empty());
    }

    public void spawnItemForPlayer() {
        if (itemSpawner == null) return;
        if (getBoundPlayer() == null) return;
        Vec3d playerPos = getBoundPlayer().getPos();
        Vec3d pos = getPos().add(0,1,0);
        Vec3d relativeTargetPos = new Vec3d(
                playerPos.getX() - pos.getX(),
                0,
                playerPos.getZ() - pos.getZ()
        );
        Vec3d vector = Vec3d.ZERO;
        if (relativeTargetPos.lengthSquared() > 0.0001) {
            vector = relativeTargetPos.normalize().multiply(0.3).add(0,0.1,0);
        }

        List<ItemStack> lootTableItems = ItemSpawner.getRandomItemsFromLootTable(server, (ServerWorld) getWorld(), getBoundPlayer(), Identifier.of("lifeseriesdynamic", "trivia_reward_loottable"));
        if (!lootTableItems.isEmpty()) {
            for (ItemStack item : lootTableItems) {
                ItemStackUtils.spawnItemForPlayerWithVelocity((ServerWorld) getWorld(), pos, item, getBoundPlayer(), vector);
            }
        }
        else {
            ItemStack randomItem = itemSpawner.getRandomItem();
            ItemStackUtils.spawnItemForPlayerWithVelocity((ServerWorld) getWorld(), pos, randomItem, getBoundPlayer(), vector);
        }
    }

    public static void initializeItemSpawner() {
        itemSpawner = new ItemSpawner();
        itemSpawner.addItem(new ItemStack(Items.GOLDEN_APPLE, 2), 20);
        itemSpawner.addItem(new ItemStack(Items.ENDER_PEARL, 2), 20);
        itemSpawner.addItem(new ItemStack(Items.TRIDENT), 10);
        itemSpawner.addItem(new ItemStack(Items.POWERED_RAIL, 16), 10);
        itemSpawner.addItem(new ItemStack(Items.DIAMOND, 4), 20);
        itemSpawner.addItem(new ItemStack(Items.CREEPER_SPAWN_EGG), 10);
        itemSpawner.addItem(new ItemStack(Items.GOLDEN_CARROT, 8), 10);
        itemSpawner.addItem(new ItemStack(Items.WIND_CHARGE, 16), 10);
        itemSpawner.addItem(new ItemStack(Items.SCULK_SHRIEKER, 2), 10);
        itemSpawner.addItem(new ItemStack(Items.SCULK_SENSOR, 8), 10);
        itemSpawner.addItem(new ItemStack(Items.TNT, 8), 20);
        itemSpawner.addItem(new ItemStack(Items.COBWEB, 8), 10);
        itemSpawner.addItem(new ItemStack(Items.OBSIDIAN, 8), 10);
        itemSpawner.addItem(new ItemStack(Items.PUFFERFISH_BUCKET), 10);
        itemSpawner.addItem(new ItemStack(Items.NETHERITE_CHESTPLATE), 10);
        itemSpawner.addItem(new ItemStack(Items.NETHERITE_LEGGINGS), 10);
        itemSpawner.addItem(new ItemStack(Items.NETHERITE_BOOTS), 10);
        itemSpawner.addItem(new ItemStack(Items.ARROW, 64), 10);
        itemSpawner.addItem(new ItemStack(Items.IRON_BLOCK, 2), 10);

        ItemStack mace = new ItemStack(Items.MACE);
        ItemStackUtils.setCustomComponentBoolean(mace, "IgnoreBlacklist", true);
        ItemStackUtils.setCustomComponentBoolean(mace, "NoModifications", true);
        mace.setDamage(mace.getMaxDamage()-1);
        itemSpawner.addItem(mace, 5);

        ItemStack endCrystal = new ItemStack(Items.END_CRYSTAL);
        ItemStackUtils.setCustomComponentBoolean(endCrystal, "IgnoreBlacklist", true);
        itemSpawner.addItem(endCrystal, 10);

        ItemStack patat = new ItemStack(Items.POISONOUS_POTATO);
        patat.set(DataComponentTypes.CUSTOM_NAME, Text.of("§6§l§nThe Sacred Patat"));
        ItemStackUtils.addLoreToItemStack(patat,
                List.of(Text.of("§5§oEating this might help you. Or maybe not..."))
        );
        itemSpawner.addItem(patat, 1);
    }

    /*
        Curses
     */

    public void curseHunger(ServerPlayerEntity player) {
        StatusEffectInstance statusEffectInstance = new StatusEffectInstance(StatusEffects.HUNGER, 18000, 2);
        player.addStatusEffect(statusEffectInstance);
    }

    public void curseRavager(ServerPlayerEntity player) {
        BlockPos spawnPos = getBlockPosNearTarget(player, getBlockPos(), 5);
        EntityType.RAVAGER.spawn(PlayerUtils.getServerWorld(player), spawnPos, SpawnReason.COMMAND);
    }

    public void curseInfestation(ServerPlayerEntity player) {
        StatusEffectInstance statusEffectInstance = new StatusEffectInstance(StatusEffects.INFESTED, 18000, 0);
        player.addStatusEffect(statusEffectInstance);
    }

    public static final List<UUID> cursedGigantificationPlayers = new ArrayList<>();
    public void curseGigantification(ServerPlayerEntity player) {
        cursedGigantificationPlayers.add(player.getUuid());
        SizeShifting.setPlayerSizeUnchecked(player, 4);
    }

    public static final List<UUID> cursedSliding = new ArrayList<>();
    public void curseSlipperyGround(ServerPlayerEntity player) {
        cursedSliding.add(player.getUuid());
    }

    public void curseBindingArmor(ServerPlayerEntity player) {
        for (ItemStack item : PlayerUtils.getArmorItems(player)) {
            ItemStackUtils.spawnItemForPlayer(PlayerUtils.getServerWorld(player), player.getPos(), item.copy(), player);
        }
        ItemStack head = Items.LEATHER_HELMET.getDefaultStack();
        ItemStack chest = Items.LEATHER_CHESTPLATE.getDefaultStack();
        ItemStack legs = Items.LEATHER_LEGGINGS.getDefaultStack();
        ItemStack boots = Items.LEATHER_BOOTS.getDefaultStack();
        head.addEnchantment(ItemStackUtils.getEnchantmentEntry(Enchantments.BINDING_CURSE), 1);
        chest.addEnchantment(ItemStackUtils.getEnchantmentEntry(Enchantments.BINDING_CURSE), 1);
        legs.addEnchantment(ItemStackUtils.getEnchantmentEntry(Enchantments.BINDING_CURSE), 1);
        boots.addEnchantment(ItemStackUtils.getEnchantmentEntry(Enchantments.BINDING_CURSE), 1);
        ItemStackUtils.setCustomComponentBoolean(head, "IgnoreBlacklist", true);
        ItemStackUtils.setCustomComponentBoolean(chest, "IgnoreBlacklist", true);
        ItemStackUtils.setCustomComponentBoolean(legs, "IgnoreBlacklist", true);
        ItemStackUtils.setCustomComponentBoolean(boots, "IgnoreBlacklist", true);
        player.equipStack(EquipmentSlot.HEAD, head);
        player.equipStack(EquipmentSlot.CHEST, chest);
        player.equipStack(EquipmentSlot.LEGS, legs);
        player.equipStack(EquipmentSlot.FEET, boots);
        player.getInventory().markDirty();
    }

    public static final List<UUID> cursedHeartPlayers = new ArrayList<>();
    public void curseHearts(ServerPlayerEntity player) {
        cursedHeartPlayers.add(player.getUuid());
        double newHealth = Math.max(player.getMaxHealth()-7, 1);
        AttributeUtils.setMaxPlayerHealth(player, newHealth);
    }

    public static final List<UUID> cursedMoonJumpPlayers = new ArrayList<>();
    public void curseMoonjump(ServerPlayerEntity player) {
        cursedMoonJumpPlayers.add(player.getUuid());
        AttributeUtils.setJumpStrength(player, 0.76);
    }

    public void curseBeeswarm(ServerPlayerEntity player) {
        BlockPos spawnPos = getBlockPosNearTarget(player, getBlockPos(), 1);
        BeeEntity bee1 = EntityType.BEE.spawn((ServerWorld) getWorld(), spawnPos, SpawnReason.COMMAND);
        BeeEntity bee2 = EntityType.BEE.spawn((ServerWorld) getWorld(), spawnPos, SpawnReason.COMMAND);
        BeeEntity bee3 = EntityType.BEE.spawn((ServerWorld) getWorld(), spawnPos, SpawnReason.COMMAND);
        BeeEntity bee4 = EntityType.BEE.spawn((ServerWorld) getWorld(), spawnPos, SpawnReason.COMMAND);
        BeeEntity bee5 = EntityType.BEE.spawn((ServerWorld) getWorld(), spawnPos, SpawnReason.COMMAND);
        if (bee1 != null) bee1.setAngryAt(player.getUuid());
        if (bee2 != null) bee2.setAngryAt(player.getUuid());
        if (bee3 != null) bee3.setAngryAt(player.getUuid());
        if (bee4 != null) bee4.setAngryAt(player.getUuid());
        if (bee5 != null) bee5.setAngryAt(player.getUuid());
        if (bee1 != null) bee1.setAngerTime(1000000);
        if (bee2 != null) bee2.setAngerTime(1000000);
        if (bee3 != null) bee3.setAngerTime(1000000);
        if (bee4 != null) bee4.setAngerTime(1000000);
        if (bee5 != null) bee5.setAngerTime(1000000);
    }

    public static final List<UUID> cursedRoboticVoicePlayers = new ArrayList<>();
    public void curseRoboticVoice(ServerPlayerEntity player) {
        cursedRoboticVoicePlayers.add(player.getUuid());
    }

    /*
        Override vanilla things
     */

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.PLAYERS;
    }
    
    @Override
    public Vec3d applyFluidMovingSpeed(double gravity, boolean falling, Vec3d motion) {
        return motion;
    }

    @Override
    //? if <= 1.21.4 {
    protected boolean shouldSwimInFluids() {
        return false;
    }
    //?} else {
    /*public boolean shouldSwimInFluids() {
        return false;
    }
    *///?}

    @Override
    public boolean isTouchingWater() {
        return false;
    }

    @Override
    public void setSwimming(boolean swimming) {
        this.setFlag(4, false);
    }

    @Override
    public boolean updateMovementInFluid(TagKey<Fluid> tag, double speed) {
        return false;
    }

    @Override
    protected boolean canStartRiding(Entity entity) {
        return CAN_START_RIDING;
    }

    @Override
    public void slowMovement(BlockState state, Vec3d multiplier) {
    }

    @Override
    public boolean isImmuneToExplosion(Explosion explosion) {
        return true;
    }

    @Override
    public boolean canUsePortals(boolean allowVehicles) {
        return false;
    }
}
