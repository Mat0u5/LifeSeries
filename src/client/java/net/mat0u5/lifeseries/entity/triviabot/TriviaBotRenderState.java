package net.mat0u5.lifeseries.entity.triviabot;

//? if <= 1.21 {
public class TriviaBotRenderState {
//Empty class to prevent errors
}
//?} else {
/*import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.AnimationState;

public class TriviaBotRenderState extends LivingEntityRenderState {
    public final AnimationState glideAnimationState = new AnimationState();
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState countdownAnimationState = new AnimationState();
    public final AnimationState analyzingAnimationState = new AnimationState();
    public final AnimationState answerCorrectAnimationState = new AnimationState();
    public final AnimationState answerIncorrectAnimationState = new AnimationState();
    public final AnimationState snailTransformAnimationState = new AnimationState();

    public final AnimationState santaAnalyzingAnimationState = new AnimationState();
    public final AnimationState santaAnswerCorrectAnimationState = new AnimationState();
    public final AnimationState santaAnswerIncorrectAnimationState = new AnimationState();
    public final AnimationState santaFlyAnimationState = new AnimationState();
    public final AnimationState santaGlideAnimationState = new AnimationState();
    public final AnimationState santaIdleAnimationState = new AnimationState();
    public final AnimationState santaWaveAnimationState = new AnimationState();

    public final AnimationState faceAngryAnimationState = new AnimationState();
    public final AnimationState faceHappyAnimationState = new AnimationState();

    public boolean santaBot = false;
}
*///?}
