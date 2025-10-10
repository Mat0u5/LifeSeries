package net.mat0u5.lifeseries.entity.triviabot;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.util.math.MatrixStack;

//? if <= 1.21 {
public class TriviaBotModel<T extends TriviaBot> extends SinglePartEntityModel<T> {
//?} else {
/*import net.minecraft.client.render.entity.model.EntityModel;
public class TriviaBotModel extends EntityModel<TriviaBotRenderState> {
*///?}

    public static final EntityModelLayer TRIVIA_BOT = new EntityModelLayer(TriviaBot.ID, "triviabot");

    //? if >= 1.21.6 {
    
    /*private final Animation glideAnimation;
    private final Animation idleAnimation;
    private final Animation walkAnimation;
    private final Animation countdownAnimation;
    private final Animation analyzingAnimation;
    private final Animation answerCorrectAnimation;
    private final Animation answerIncorrectAnimation;
    private final Animation snailTransformAnimation;
     *///?}

    private final ModelPart triviabot;
    private final ModelPart neckpivot;
    private final ModelPart main;
    private final ModelPart shell;
    private final ModelPart expressions;
    private final ModelPart mouth;
    private final ModelPart dots;
    private final ModelPart green;
    private final ModelPart yellow;
    private final ModelPart red;
    private final ModelPart clock;
    private final ModelPart clockhand;
    private final ModelPart processing;
    private final ModelPart one;
    private final ModelPart two;
    private final ModelPart three;
    private final ModelPart angry;
    private final ModelPart happy;
    private final ModelPart snail;
    private final ModelPart body;
    private final ModelPart righthand;
    private final ModelPart actualhand;
    private final ModelPart microphone;
    private final ModelPart umbrella;
    private final ModelPart top;
    private final ModelPart lefthand;
    private final ModelPart torso;
    private final ModelPart bottom;
    private final ModelPart legs;

    public TriviaBotModel(ModelPart root) {
        //? if >= 1.21.2 {
        /*super(root);
        *///?}
        this.triviabot = root.getChild("triviabot");
        this.neckpivot = this.triviabot.getChild("neckpivot");
        this.main = this.neckpivot.getChild("main");
        this.shell = this.main.getChild("shell");
        this.expressions = this.main.getChild("expressions");
        this.mouth = this.expressions.getChild("mouth");
        this.dots = this.expressions.getChild("dots");
        this.green = this.dots.getChild("green");
        this.yellow = this.dots.getChild("yellow");
        this.red = this.dots.getChild("red");
        this.clock = this.expressions.getChild("clock");
        this.clockhand = this.clock.getChild("clockhand");
        this.processing = this.expressions.getChild("processing");
        this.one = this.processing.getChild("one");
        this.two = this.processing.getChild("two");
        this.three = this.processing.getChild("three");
        this.angry = this.expressions.getChild("angry");
        this.happy = this.expressions.getChild("happy");
        this.snail = this.expressions.getChild("snail");
        this.body = this.triviabot.getChild("body");
        this.righthand = this.body.getChild("righthand");
        this.actualhand = this.righthand.getChild("actualhand");
        this.microphone = this.righthand.getChild("microphone");
        this.umbrella = this.righthand.getChild("umbrella");
        this.top = this.umbrella.getChild("top");
        this.lefthand = this.body.getChild("lefthand");
        this.torso = this.body.getChild("torso");
        this.bottom = this.torso.getChild("bottom");
        this.legs = this.body.getChild("legs");

        //? if >= 1.21.6 {
        /*glideAnimation = TriviaBotAnimations.glide.createAnimation(root);
        idleAnimation = TriviaBotAnimations.idle.createAnimation(root);
        walkAnimation = TriviaBotAnimations.walk.createAnimation(root);
        countdownAnimation = TriviaBotAnimations.countdown.createAnimation(root);
        analyzingAnimation = TriviaBotAnimations.analyzing.createAnimation(root);
        answerCorrectAnimation = TriviaBotAnimations.answer_correct.createAnimation(root);
        answerIncorrectAnimation = TriviaBotAnimations.answer_incorrect.createAnimation(root);
        snailTransformAnimation = TriviaBotAnimations.snail_transform.createAnimation(root);
        *///?}
    }
    public static TexturedModelData getTexturedModelData() {
        //? if <= 1.21.4 {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData triviabot = modelPartData.addChild("triviabot", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.9F, -0.7F));

        ModelPartData neckpivot = triviabot.addChild("neckpivot", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 5.1F, 0.2F));

        ModelPartData main = neckpivot.addChild("main", ModelPartBuilder.create().uv(40, 36).cuboid(-5.0F, -4.5F, -2.6F, 10.0F, 9.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -5.1F, -0.2F));

        ModelPartData shell = main.addChild("shell", ModelPartBuilder.create().uv(0, 41).cuboid(-6.0F, -10.6F, 4.5F, 12.0F, 11.0F, 0.0F, new Dilation(0.0F))
                .uv(56, 69).cuboid(-6.0F, -0.6F, -3.5F, 12.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(36, 70).cuboid(-6.0F, -10.6F, -3.5F, 12.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(76, 52).cuboid(5.0F, -9.6F, -3.5F, 1.0F, 9.0F, 0.0F, new Dilation(0.0F))
                .uv(38, 64).cuboid(4.0F, -1.6F, -3.5F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(38, 66).cuboid(4.0F, -9.6F, -3.5F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(36, 69).cuboid(-5.0F, -9.6F, -3.5F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(38, 65).cuboid(-5.0F, -1.6F, -3.5F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(76, 70).cuboid(-6.0F, -9.6F, -3.5F, 1.0F, 9.0F, 0.0F, new Dilation(0.0F))
                .uv(0, 25).cuboid(-6.0F, -10.6F, -3.5F, 12.0F, 0.0F, 8.0F, new Dilation(0.0F))
                .uv(0, 33).cuboid(-6.02F, 0.4F, -3.48F, 12.0F, 0.0F, 8.0F, new Dilation(0.0F))
                .uv(24, 41).cuboid(-6.0F, -10.6F, -3.5F, 0.0F, 11.0F, 8.0F, new Dilation(0.0F))
                .uv(40, 51).cuboid(6.0F, -10.6F, -3.5F, 0.0F, 11.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 5.1F, 0.2F));

        ModelPartData expressions = main.addChild("expressions", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -3.005F));

        ModelPartData mouth = expressions.addChild("mouth", ModelPartBuilder.create().uv(28, 67).cuboid(-3.0F, 1.53F, -2.61F, 6.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 3.005F));

        ModelPartData dots = expressions.addChild("dots", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -0.5F, 0.0F));

        ModelPartData green = dots.addChild("green", ModelPartBuilder.create().uv(56, 71).cuboid(-4.0F, -1.0F, 1.705F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData yellow = dots.addChild("yellow", ModelPartBuilder.create().uv(78, 78).cuboid(-1.0F, -1.0F, 1.705F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData red = dots.addChild("red", ModelPartBuilder.create().uv(44, 79).cuboid(2.0F, -1.0F, 1.705F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData clock = expressions.addChild("clock", ModelPartBuilder.create().uv(48, 79).cuboid(-1.2228F, -1.4755F, 1.7083F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.2228F, -0.0245F, -0.0033F));

        ModelPartData green_r1 = clock.addChild("green_r1", ModelPartBuilder.create().uv(38, 59).cuboid(0.0F, -3.5F, 0.69F, 1.0F, 3.5F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.6228F, -0.5755F, 1.0083F, 0.0F, 0.0F, 0.3927F));

        ModelPartData clockhand = clock.addChild("clockhand", ModelPartBuilder.create().uv(78, 77).cuboid(0.0F, -0.4F, 1.98F, 3.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.2228F, -0.4755F, -0.2917F));

        ModelPartData processing = expressions.addChild("processing", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -0.5F, 0.0F));

        ModelPartData one = processing.addChild("one", ModelPartBuilder.create().uv(52, 79).cuboid(-1.0F, -1.0F, 1.705F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(-3.0F, 0.0F, 0.0F));

        ModelPartData two = processing.addChild("two", ModelPartBuilder.create().uv(56, 79).cuboid(-1.0F, -1.0F, 1.705F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData three = processing.addChild("three", ModelPartBuilder.create().uv(66, 79).cuboid(-1.0F, -1.0F, 1.705F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(3.0F, 0.0F, 0.0F));

        ModelPartData angry = expressions.addChild("angry", ModelPartBuilder.create().uv(56, 51).cuboid(-5.0F, -9.6F, -1.5F, 10.0F, 9.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 5.1F, 3.205F));

        ModelPartData happy = expressions.addChild("happy", ModelPartBuilder.create().uv(8, 60).cuboid(-5.0F, -4.5F, 1.4F, 10.0F, 9.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.305F));

        ModelPartData snail = expressions.addChild("snail", ModelPartBuilder.create().uv(56, 60).cuboid(-5.0F, -4.5F, -1.3F, 10.0F, 9.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 3.005F));

        ModelPartData body = triviabot.addChild("body", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 23.1F, 0.7F));

        ModelPartData righthand = body.addChild("righthand", ModelPartBuilder.create(), ModelTransform.pivot(-7.7F, -16.9F, 0.0F));

        ModelPartData actualhand = righthand.addChild("actualhand", ModelPartBuilder.create().uv(22, 69).cuboid(-1.5F, -2.0F, -2.0F, 3.0F, 10.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData microphone = righthand.addChild("microphone", ModelPartBuilder.create().uv(36, 73).cuboid(-1.05F, -0.25F, -1.0F, 2.0F, 5.0F, 2.0F, new Dilation(0.0F))
                .uv(8, 52).cuboid(-1.95F, -4.25F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.5953F, 6.4269F, -5.5088F, 1.309F, 0.1309F, 0.0F));

        ModelPartData umbrella = righthand.addChild("umbrella", ModelPartBuilder.create().uv(0, 52).cuboid(-1.05F, -18.25F, -1.0F, 2.0F, 23.0F, 2.0F, new Dilation(0.0F))
                .uv(60, 70).cuboid(-1.95F, -4.25F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(2.0953F, 5.2269F, -3.9088F, 1.6581F, 0.2182F, -0.1309F));

        ModelPartData top = umbrella.addChild("top", ModelPartBuilder.create().uv(0, 0).cuboid(-11.3333F, -1.45F, -12.3333F, 24.0F, 1.0F, 24.0F, new Dilation(0.0F))
                .uv(76, 61).cuboid(12.6667F, -0.45F, -12.3333F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(78, 74).cuboid(9.6667F, -0.45F, -12.3333F, 3.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(78, 75).cuboid(-2.3333F, -0.45F, -12.3333F, 3.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(78, 76).cuboid(-2.3333F, -0.45F, 11.6667F, 3.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(66, 78).cuboid(-11.3333F, -0.45F, -12.3333F, 3.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(60, 78).cuboid(12.6667F, -0.45F, 8.6667F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(78, 70).cuboid(9.6667F, -0.45F, 11.6667F, 3.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(76, 65).cuboid(-11.3333F, -0.45F, 8.6667F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(0, 77).cuboid(-11.3333F, -0.45F, -0.3333F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(78, 56).cuboid(12.6667F, -0.45F, -3.3333F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(78, 52).cuboid(-11.3333F, -0.45F, -12.3333F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(78, 60).cuboid(-11.3333F, -0.45F, 11.6667F, 3.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.6667F, -17.35F, 0.3333F, 0.0F, -0.7854F, 0.0F));

        ModelPartData lefthand = body.addChild("lefthand", ModelPartBuilder.create().uv(8, 69).cuboid(-1.9F, -1.5F, -2.0F, 3.0F, 10.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(8.1F, -17.4F, 0.0F));

        ModelPartData torso = body.addChild("torso", ModelPartBuilder.create().uv(40, 25).cuboid(-5.998F, -5.3F, -3.002F, 12.0F, 5.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.002F, -12.1F, 0.002F));

        ModelPartData bottom = torso.addChild("bottom", ModelPartBuilder.create().uv(36, 71).cuboid(-4.998F, -0.3F, -2.502F, 10.0F, 2.0F, 0.0F, new Dilation(0.0F))
                .uv(72, 36).cuboid(-4.998F, -0.3F, 2.498F, 10.0F, 2.0F, 0.0F, new Dilation(0.0F))
                .uv(28, 60).cuboid(-4.998F, -0.3F, -2.502F, 0.0F, 2.0F, 5.0F, new Dilation(0.0F))
                .uv(72, 42).cuboid(5.002F, -0.3F, -2.502F, 0.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData legs = body.addChild("legs", ModelPartBuilder.create().uv(72, 38).cuboid(-3.9975F, 0.6F, -2.0025F, 8.0F, 2.0F, 0.0F, new Dilation(0.0F))
                .uv(72, 40).cuboid(-3.9975F, 0.6F, 1.9975F, 8.0F, 2.0F, 0.0F, new Dilation(0.0F))
                .uv(44, 73).cuboid(-3.9975F, 0.6F, -2.0025F, 0.0F, 2.0F, 4.0F, new Dilation(0.0F))
                .uv(52, 73).cuboid(4.0025F, 0.6F, -2.0025F, 0.0F, 2.0F, 4.0F, new Dilation(0.0F))
                .uv(72, 49).cuboid(-3.9975F, 3.6F, -2.0025F, 8.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(72, 50).cuboid(-3.9975F, 3.6F, 1.9975F, 8.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(76, 25).cuboid(-3.9975F, 3.6F, -2.0025F, 0.0F, 1.0F, 4.0F, new Dilation(0.0F))
                .uv(76, 30).cuboid(4.0025F, 3.6F, -2.0025F, 0.0F, 1.0F, 4.0F, new Dilation(0.0F))
                .uv(78, 71).cuboid(4.0025F, 5.6F, -1.0025F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(76, 35).cuboid(-2.9975F, 5.6F, -2.0025F, 6.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(76, 51).cuboid(-2.9975F, 5.6F, 1.9975F, 6.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(72, 78).cuboid(-3.9975F, 5.6F, -1.0025F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.0025F, -10.5F, 0.0025F));
        return TexturedModelData.of(modelData, 128, 128);
        //?} else {
        /*ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData triviabot = modelPartData.addChild("triviabot", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 0.9F, -0.7F));

        ModelPartData neckpivot = triviabot.addChild("neckpivot", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 5.1F, 0.2F));

        ModelPartData main = neckpivot.addChild("main", ModelPartBuilder.create().uv(40, 36).cuboid(-5.0F, -4.5F, -2.6F, 10.0F, 9.0F, 6.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -5.1F, -0.2F));

        ModelPartData shell = main.addChild("shell", ModelPartBuilder.create().uv(0, 41).cuboid(-6.0F, -10.6F, 4.5F, 12.0F, 11.0F, 0.0F, new Dilation(0.0F))
                .uv(56, 69).cuboid(-6.0F, -0.6F, -3.5F, 12.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(36, 70).cuboid(-6.0F, -10.6F, -3.5F, 12.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(76, 52).cuboid(5.0F, -9.6F, -3.5F, 1.0F, 9.0F, 0.0F, new Dilation(0.0F))
                .uv(38, 64).cuboid(4.0F, -1.6F, -3.5F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(38, 66).cuboid(4.0F, -9.6F, -3.5F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(36, 69).cuboid(-5.0F, -9.6F, -3.5F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(38, 65).cuboid(-5.0F, -1.6F, -3.5F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(76, 70).cuboid(-6.0F, -9.6F, -3.5F, 1.0F, 9.0F, 0.0F, new Dilation(0.0F))
                .uv(0, 25).cuboid(-6.0F, -10.6F, -3.5F, 12.0F, 0.0F, 8.0F, new Dilation(0.0F))
                .uv(0, 33).cuboid(-6.02F, 0.4F, -3.48F, 12.0F, 0.0F, 8.0F, new Dilation(0.0F))
                .uv(24, 41).cuboid(-6.0F, -10.6F, -3.5F, 0.0F, 11.0F, 8.0F, new Dilation(0.0F))
                .uv(40, 51).cuboid(6.0F, -10.6F, -3.5F, 0.0F, 11.0F, 8.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 5.1F, 0.2F));

        ModelPartData expressions = main.addChild("expressions", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 0.0F, -3.005F));

        ModelPartData mouth = expressions.addChild("mouth", ModelPartBuilder.create().uv(28, 67).cuboid(-3.0F, 1.53F, -2.61F, 6.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 3.005F));

        ModelPartData dots = expressions.addChild("dots", ModelPartBuilder.create(), ModelTransform.origin(0.0F, -0.5F, 0.0F));

        ModelPartData green = dots.addChild("green", ModelPartBuilder.create().uv(56, 71).cuboid(-4.0F, -1.0F, 1.705F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        ModelPartData yellow = dots.addChild("yellow", ModelPartBuilder.create().uv(78, 78).cuboid(-1.0F, -1.0F, 1.705F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        ModelPartData red = dots.addChild("red", ModelPartBuilder.create().uv(44, 79).cuboid(2.0F, -1.0F, 1.705F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        ModelPartData clock = expressions.addChild("clock", ModelPartBuilder.create().uv(48, 79).cuboid(-1.2228F, -1.4755F, 1.7083F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.origin(0.2228F, -0.0245F, -0.0033F));

        ModelPartData green_r1 = clock.addChild("green_r1", ModelPartBuilder.create().uv(38, 59).cuboid(0.0F, -3.5F, 0.69F, 1.0F, 3.5F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.6228F, -0.5755F, 1.0083F, 0.0F, 0.0F, 0.3927F));

        ModelPartData clockhand = clock.addChild("clockhand", ModelPartBuilder.create().uv(78, 77).cuboid(0.0F, -0.4F, 1.98F, 3.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.origin(-0.2228F, -0.4755F, -0.2917F));

        ModelPartData processing = expressions.addChild("processing", ModelPartBuilder.create(), ModelTransform.origin(0.0F, -0.5F, 0.0F));

        ModelPartData one = processing.addChild("one", ModelPartBuilder.create().uv(52, 79).cuboid(-1.0F, -1.0F, 1.705F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.origin(-3.0F, 0.0F, 0.0F));

        ModelPartData two = processing.addChild("two", ModelPartBuilder.create().uv(56, 79).cuboid(-1.0F, -1.0F, 1.705F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        ModelPartData three = processing.addChild("three", ModelPartBuilder.create().uv(66, 79).cuboid(-1.0F, -1.0F, 1.705F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.origin(3.0F, 0.0F, 0.0F));

        ModelPartData angry = expressions.addChild("angry", ModelPartBuilder.create().uv(56, 51).cuboid(-5.0F, -9.6F, -1.5F, 10.0F, 9.0F, 0.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 5.1F, 3.205F));

        ModelPartData happy = expressions.addChild("happy", ModelPartBuilder.create().uv(8, 60).cuboid(-5.0F, -4.5F, 1.4F, 10.0F, 9.0F, 0.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.305F));

        ModelPartData snail = expressions.addChild("snail", ModelPartBuilder.create().uv(56, 60).cuboid(-5.0F, -4.5F, -1.3F, 10.0F, 9.0F, 0.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 3.005F));

        ModelPartData body = triviabot.addChild("body", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 23.1F, 0.7F));

        ModelPartData righthand = body.addChild("righthand", ModelPartBuilder.create(), ModelTransform.origin(-7.7F, -16.9F, 0.0F));

        ModelPartData actualhand = righthand.addChild("actualhand", ModelPartBuilder.create().uv(22, 69).cuboid(-1.5F, -2.0F, -2.0F, 3.0F, 10.0F, 4.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        ModelPartData microphone = righthand.addChild("microphone", ModelPartBuilder.create().uv(36, 73).cuboid(-1.05F, -0.25F, -1.0F, 2.0F, 5.0F, 2.0F, new Dilation(0.0F))
                .uv(8, 52).cuboid(-1.95F, -4.25F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.5953F, 6.4269F, -5.5088F, 1.309F, 0.1309F, 0.0F));

        ModelPartData umbrella = righthand.addChild("umbrella", ModelPartBuilder.create().uv(0, 52).cuboid(-1.05F, -18.25F, -1.0F, 2.0F, 23.0F, 2.0F, new Dilation(0.0F))
                .uv(60, 70).cuboid(-1.95F, -4.25F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(2.0953F, 5.2269F, -3.9088F, 1.6581F, 0.2182F, -0.1309F));

        ModelPartData top = umbrella.addChild("top", ModelPartBuilder.create().uv(0, 0).cuboid(-11.3333F, -1.45F, -12.3333F, 24.0F, 1.0F, 24.0F, new Dilation(0.0F))
                .uv(76, 61).cuboid(12.6667F, -0.45F, -12.3333F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(78, 74).cuboid(9.6667F, -0.45F, -12.3333F, 3.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(78, 75).cuboid(-2.3333F, -0.45F, -12.3333F, 3.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(78, 76).cuboid(-2.3333F, -0.45F, 11.6667F, 3.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(66, 78).cuboid(-11.3333F, -0.45F, -12.3333F, 3.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(60, 78).cuboid(12.6667F, -0.45F, 8.6667F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(78, 70).cuboid(9.6667F, -0.45F, 11.6667F, 3.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(76, 65).cuboid(-11.3333F, -0.45F, 8.6667F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(0, 77).cuboid(-11.3333F, -0.45F, -0.3333F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(78, 56).cuboid(12.6667F, -0.45F, -3.3333F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(78, 52).cuboid(-11.3333F, -0.45F, -12.3333F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(78, 60).cuboid(-11.3333F, -0.45F, 11.6667F, 3.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.6667F, -17.35F, 0.3333F, 0.0F, -0.7854F, 0.0F));

        ModelPartData lefthand = body.addChild("lefthand", ModelPartBuilder.create().uv(8, 69).cuboid(-1.9F, -1.5F, -2.0F, 3.0F, 10.0F, 4.0F, new Dilation(0.0F)), ModelTransform.origin(8.1F, -17.4F, 0.0F));

        ModelPartData torso = body.addChild("torso", ModelPartBuilder.create().uv(40, 25).cuboid(-5.998F, -5.3F, -3.002F, 12.0F, 5.0F, 6.0F, new Dilation(0.0F)), ModelTransform.origin(-0.002F, -12.1F, 0.002F));

        ModelPartData bottom = torso.addChild("bottom", ModelPartBuilder.create().uv(36, 71).cuboid(-4.998F, -0.3F, -2.502F, 10.0F, 2.0F, 0.0F, new Dilation(0.0F))
                .uv(72, 36).cuboid(-4.998F, -0.3F, 2.498F, 10.0F, 2.0F, 0.0F, new Dilation(0.0F))
                .uv(28, 60).cuboid(-4.998F, -0.3F, -2.502F, 0.0F, 2.0F, 5.0F, new Dilation(0.0F))
                .uv(72, 42).cuboid(5.002F, -0.3F, -2.502F, 0.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        ModelPartData legs = body.addChild("legs", ModelPartBuilder.create().uv(72, 38).cuboid(-3.9975F, 0.6F, -2.0025F, 8.0F, 2.0F, 0.0F, new Dilation(0.0F))
                .uv(72, 40).cuboid(-3.9975F, 0.6F, 1.9975F, 8.0F, 2.0F, 0.0F, new Dilation(0.0F))
                .uv(44, 73).cuboid(-3.9975F, 0.6F, -2.0025F, 0.0F, 2.0F, 4.0F, new Dilation(0.0F))
                .uv(52, 73).cuboid(4.0025F, 0.6F, -2.0025F, 0.0F, 2.0F, 4.0F, new Dilation(0.0F))
                .uv(72, 49).cuboid(-3.9975F, 3.6F, -2.0025F, 8.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(72, 50).cuboid(-3.9975F, 3.6F, 1.9975F, 8.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(76, 25).cuboid(-3.9975F, 3.6F, -2.0025F, 0.0F, 1.0F, 4.0F, new Dilation(0.0F))
                .uv(76, 30).cuboid(4.0025F, 3.6F, -2.0025F, 0.0F, 1.0F, 4.0F, new Dilation(0.0F))
                .uv(78, 71).cuboid(4.0025F, 5.6F, -1.0025F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(76, 35).cuboid(-2.9975F, 5.6F, -2.0025F, 6.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(76, 51).cuboid(-2.9975F, 5.6F, 1.9975F, 6.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(72, 78).cuboid(-3.9975F, 5.6F, -1.0025F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.origin(-0.0025F, -10.5F, 0.0025F));
        return TexturedModelData.of(modelData, 128, 128);
        *///?}
    }

    //? if <= 1.21 {
    @Override
    public void setAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        this.updateAnimation(entity.clientData.glideAnimationState, TriviaBotAnimations.glide, ageInTicks);
        this.updateAnimation(entity.clientData.idleAnimationState, TriviaBotAnimations.idle, ageInTicks);
        this.updateAnimation(entity.clientData.walkAnimationState, TriviaBotAnimations.walk, ageInTicks);
        this.updateAnimation(entity.clientData.countdownAnimationState, TriviaBotAnimations.countdown, ageInTicks);
        this.updateAnimation(entity.clientData.analyzingAnimationState, TriviaBotAnimations.analyzing, ageInTicks);
        this.updateAnimation(entity.clientData.answerCorrectAnimationState, TriviaBotAnimations.answer_correct, ageInTicks);
        this.updateAnimation(entity.clientData.answerIncorrectAnimationState, TriviaBotAnimations.answer_incorrect, ageInTicks);
        this.updateAnimation(entity.clientData.snailTransformAnimationState, TriviaBotAnimations.snail_transform, ageInTicks);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        main.render(matrices, vertexConsumer, light, overlay, color);
    }

    @Override
    public ModelPart getPart() {
        return main;
    }
    //?} else {
    /*@Override
    public void setAngles(TriviaBotRenderState state) {
        super.setAngles(state);

        //? if <= 1.21.5 {
        this.animate(state.glideAnimationState, TriviaBotAnimations.glide, state.age);
        this.animate(state.idleAnimationState, TriviaBotAnimations.idle, state.age);
        this.animate(state.walkAnimationState, TriviaBotAnimations.walk, state.age);
        this.animate(state.countdownAnimationState, TriviaBotAnimations.countdown, state.age);
        this.animate(state.analyzingAnimationState, TriviaBotAnimations.analyzing, state.age);
        this.animate(state.answerCorrectAnimationState, TriviaBotAnimations.answer_correct, state.age);
        this.animate(state.answerIncorrectAnimationState, TriviaBotAnimations.answer_incorrect, state.age);
        this.animate(state.snailTransformAnimationState, TriviaBotAnimations.snail_transform, state.age);
        //?} else {
        /^this.glideAnimation.apply(state.glideAnimationState, state.age);
        this.idleAnimation.apply(state.idleAnimationState, state.age);
        this.walkAnimation.apply(state.walkAnimationState, state.age);
        this.countdownAnimation.apply(state.countdownAnimationState, state.age);
        this.analyzingAnimation.apply(state.analyzingAnimationState, state.age);
        this.answerCorrectAnimation.apply(state.answerCorrectAnimationState, state.age);
        this.answerIncorrectAnimation.apply(state.answerIncorrectAnimationState, state.age);
        this.snailTransformAnimation.apply(state.snailTransformAnimationState, state.age);
        ^///?}
    }
    *///?}
}