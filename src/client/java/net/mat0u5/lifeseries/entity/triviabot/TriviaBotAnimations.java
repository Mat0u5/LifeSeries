package net.mat0u5.lifeseries.entity.triviabot;

import net.minecraft.client.render.entity.animation.*;

public class TriviaBotAnimations {
    public static final Animation glide = Animation.Builder.create(2.0F).looping()
            .addBoneAnimation("righthand", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.0F, AnimationHelper.createRotationalVector(-105.709F, 68.92F, -23.971F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.0F, AnimationHelper.createRotationalVector(-109.282F, 69.462F, -17.097F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.0F, AnimationHelper.createRotationalVector(-105.709F, 68.92F, -23.971F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("main", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.0F, AnimationHelper.createRotationalVector(9.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.4583F, AnimationHelper.createRotationalVector(8.0F, 0.0F, -7.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.9583F, AnimationHelper.createRotationalVector(9.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.4583F, AnimationHelper.createRotationalVector(8.0F, 0.0F, 7.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.9583F, AnimationHelper.createRotationalVector(9.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("main", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.5F, AnimationHelper.createTranslationalVector(1.8F, 1.2F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.5F, AnimationHelper.createTranslationalVector(-1.8F, 1.2F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("microphone", new Transformation(Transformation.Targets.SCALE,
                    new Keyframe(0.0F, AnimationHelper.createScalingVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("body", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.5F, AnimationHelper.createTranslationalVector(1.8F, 1.2F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.5F, AnimationHelper.createTranslationalVector(-1.8F, 1.2F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("lefthand", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.0F, AnimationHelper.createRotationalVector(-46.292F, -5.752F, -69.115F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.0833F, AnimationHelper.createRotationalVector(-46.549F, 1.501F, -62.222F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.2083F, AnimationHelper.createRotationalVector(-45.141F, -12.92F, -76.187F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.3333F, AnimationHelper.createRotationalVector(-46.549F, 1.501F, -62.222F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.4583F, AnimationHelper.createRotationalVector(-45.141F, -12.92F, -76.187F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.5833F, AnimationHelper.createRotationalVector(-46.549F, 1.501F, -62.222F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.7083F, AnimationHelper.createRotationalVector(-45.141F, -12.92F, -76.187F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.8333F, AnimationHelper.createRotationalVector(-46.549F, 1.501F, -62.222F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.9583F, AnimationHelper.createRotationalVector(-45.141F, -12.92F, -76.187F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.0833F, AnimationHelper.createRotationalVector(-46.549F, 1.501F, -62.222F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.2083F, AnimationHelper.createRotationalVector(-45.141F, -12.92F, -76.187F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.3333F, AnimationHelper.createRotationalVector(-46.549F, 1.501F, -62.222F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.4583F, AnimationHelper.createRotationalVector(-45.141F, -12.92F, -76.187F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.5833F, AnimationHelper.createRotationalVector(-46.549F, 1.501F, -62.222F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.7083F, AnimationHelper.createRotationalVector(-45.141F, -12.92F, -76.187F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.8333F, AnimationHelper.createRotationalVector(-46.549F, 1.501F, -62.222F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.9583F, AnimationHelper.createRotationalVector(-45.141F, -12.92F, -76.187F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.0F, AnimationHelper.createRotationalVector(-46.292F, -5.752F, -69.115F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("torso", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.0F, AnimationHelper.createRotationalVector(-3.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.125F, AnimationHelper.createRotationalVector(-3.0F, 0.0F, 5.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.25F, AnimationHelper.createRotationalVector(-3.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.375F, AnimationHelper.createRotationalVector(-3.0F, 0.0F, -5.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.5F, AnimationHelper.createRotationalVector(-3.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.625F, AnimationHelper.createRotationalVector(-3.0F, 0.0F, 5.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.75F, AnimationHelper.createRotationalVector(-3.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.875F, AnimationHelper.createRotationalVector(-3.0F, 0.0F, -5.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.0F, AnimationHelper.createRotationalVector(-3.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.125F, AnimationHelper.createRotationalVector(-3.0F, 0.0F, 5.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.25F, AnimationHelper.createRotationalVector(-3.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.375F, AnimationHelper.createRotationalVector(-3.0F, 0.0F, -5.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.5F, AnimationHelper.createRotationalVector(-3.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.625F, AnimationHelper.createRotationalVector(-3.0F, 0.0F, 5.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.75F, AnimationHelper.createRotationalVector(-3.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.875F, AnimationHelper.createRotationalVector(-3.0F, 0.0F, -5.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.0F, AnimationHelper.createRotationalVector(-3.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("torso", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.2F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("legs", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.0F, AnimationHelper.createRotationalVector(-13.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.125F, AnimationHelper.createRotationalVector(-13.0F, 0.0F, 5.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.25F, AnimationHelper.createRotationalVector(-13.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.375F, AnimationHelper.createRotationalVector(-13.0F, 0.0F, -5.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.5F, AnimationHelper.createRotationalVector(-13.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.625F, AnimationHelper.createRotationalVector(-13.0F, 0.0F, 5.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.75F, AnimationHelper.createRotationalVector(-13.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.875F, AnimationHelper.createRotationalVector(-13.0F, 0.0F, -5.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.0F, AnimationHelper.createRotationalVector(-13.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.125F, AnimationHelper.createRotationalVector(-13.0F, 0.0F, 5.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.25F, AnimationHelper.createRotationalVector(-13.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.375F, AnimationHelper.createRotationalVector(-13.0F, 0.0F, -5.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.5F, AnimationHelper.createRotationalVector(-13.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.625F, AnimationHelper.createRotationalVector(-13.0F, 0.0F, 5.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.75F, AnimationHelper.createRotationalVector(-13.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.875F, AnimationHelper.createRotationalVector(-13.0F, 0.0F, -5.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.0F, AnimationHelper.createRotationalVector(-13.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("legs", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.3F, 1.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, -0.9F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, 0.3F, 1.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("umbrella", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.0F, AnimationHelper.createRotationalVector(-5.0F, -8.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("dots", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -1.4F), Transformation.Interpolations.LINEAR)
            ))
            .build();

    public static final Animation idle = Animation.Builder.create(4.5F).looping()
            .addBoneAnimation("main", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.3333F, AnimationHelper.createRotationalVector(2.497F, 0.109F, -2.497F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.8333F, AnimationHelper.createRotationalVector(2.497F, -0.109F, 2.497F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.2917F, AnimationHelper.createRotationalVector(2.497F, 0.109F, -2.497F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.8333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.0F, AnimationHelper.createRotationalVector(-5.0F, 0.0F, -7.5F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.375F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.8333F, AnimationHelper.createRotationalVector(2.497F, 0.109F, -2.497F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.3333F, AnimationHelper.createRotationalVector(2.497F, -0.109F, 2.497F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.7917F, AnimationHelper.createRotationalVector(2.497F, 0.109F, -2.497F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.2083F, AnimationHelper.createRotationalVector(2.497F, -0.109F, 2.497F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.625F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("main", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.875F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, 2.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.125F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("body", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.875F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, 2.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.125F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("righthand", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.0F, AnimationHelper.createRotationalVector(-25.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.7083F, AnimationHelper.createRotationalVector(-24.916F, 2.11F, 4.533F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.2917F, AnimationHelper.createRotationalVector(-24.916F, -2.11F, -4.533F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.7917F, AnimationHelper.createRotationalVector(-25.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.8333F, AnimationHelper.createRotationalVector(-25.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.0F, AnimationHelper.createRotationalVector(-30.494F, 47.663F, 24.387F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.2917F, AnimationHelper.createRotationalVector(-25.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.625F, AnimationHelper.createRotationalVector(-24.916F, -2.11F, -4.533F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.0833F, AnimationHelper.createRotationalVector(-24.916F, 2.11F, 4.533F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.5F, AnimationHelper.createRotationalVector(-40.085F, -24.607F, 6.759F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.2083F, AnimationHelper.createRotationalVector(-40.085F, -24.607F, 6.759F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.5F, AnimationHelper.createRotationalVector(-24.916F, -2.11F, -4.533F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("umbrella", new Transformation(Transformation.Targets.SCALE,
                    new Keyframe(0.0F, AnimationHelper.createScalingVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("lefthand", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.4167F, AnimationHelper.createRotationalVector(-4.99F, -0.217F, -4.995F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.375F, AnimationHelper.createRotationalVector(2.497F, -0.109F, 2.497F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.8333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.0F, AnimationHelper.createRotationalVector(-89.444F, -79.895F, -84.958F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.25F, AnimationHelper.createRotationalVector(-61.944F, -79.895F, -84.958F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.5F, AnimationHelper.createRotationalVector(-89.444F, -79.895F, -84.958F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.75F, AnimationHelper.createRotationalVector(-61.944F, -79.895F, -84.958F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.0833F, AnimationHelper.createRotationalVector(-77.506F, -79.895F, -84.958F), Transformation.Interpolations.LINEAR),
                    new Keyframe(3.2917F, AnimationHelper.createRotationalVector(-15.28F, -19.73F, -22.71F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.5417F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -2.5F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.0F, AnimationHelper.createRotationalVector(-53.94F, 5.393F, 0.961F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.375F, AnimationHelper.createRotationalVector(2.497F, -0.109F, 2.497F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("torso", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.375F, AnimationHelper.createRotationalVector(1.0F, 0.0F, 6.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.125F, AnimationHelper.createRotationalVector(-1.0F, 0.0F, -6.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.875F, AnimationHelper.createRotationalVector(1.0F, 0.0F, 6.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.625F, AnimationHelper.createRotationalVector(-1.0F, 0.0F, -6.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.375F, AnimationHelper.createRotationalVector(1.0F, 0.0F, 6.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.125F, AnimationHelper.createRotationalVector(-1.0F, 0.0F, -6.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("legs", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.375F, AnimationHelper.createRotationalVector(1.0F, 0.0F, 6.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.125F, AnimationHelper.createRotationalVector(-1.0F, 0.0F, -6.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.875F, AnimationHelper.createRotationalVector(1.0F, 0.0F, 6.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.625F, AnimationHelper.createRotationalVector(-1.0F, 0.0F, -6.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.375F, AnimationHelper.createRotationalVector(1.0F, 0.0F, 6.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.125F, AnimationHelper.createRotationalVector(-1.0F, 0.0F, -6.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("green", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.6657F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.6667F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.4F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.999F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.4F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("yellow", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.3323F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.3333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.4F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.3323F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.4F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.3333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("red", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.999F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.4F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.6657F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.4F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.6667F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("dots", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -1.4F), Transformation.Interpolations.LINEAR)
            ))
            .build();

    public static final Animation walk = Animation.Builder.create(3.6667F).looping()
            .addBoneAnimation("main", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.0417F, AnimationHelper.createRotationalVector(2.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.6667F, AnimationHelper.createRotationalVector(2.49F, 0.217F, -4.995F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.5F, AnimationHelper.createRotationalVector(2.478F, -0.326F, 7.492F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.375F, AnimationHelper.createRotationalVector(2.49F, 0.217F, -4.995F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.125F, AnimationHelper.createRotationalVector(2.478F, -0.326F, 7.492F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.7083F, AnimationHelper.createRotationalVector(2.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("body", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("righthand", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.0F, AnimationHelper.createRotationalVector(-25.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.4583F, AnimationHelper.createRotationalVector(-39.748F, -8.953F, 0.975F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.375F, AnimationHelper.createRotationalVector(-12.313F, 4.058F, 6.789F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.2917F, AnimationHelper.createRotationalVector(-39.748F, -8.953F, 0.975F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.2083F, AnimationHelper.createRotationalVector(-12.313F, 4.058F, 6.789F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.6667F, AnimationHelper.createRotationalVector(-25.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("umbrella", new Transformation(Transformation.Targets.SCALE,
                    new Keyframe(0.0F, AnimationHelper.createScalingVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("lefthand", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.4583F, AnimationHelper.createRotationalVector(12.474F, 0.758F, -4.931F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.375F, AnimationHelper.createRotationalVector(-12.157F, 0.731F, 4.956F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.2917F, AnimationHelper.createRotationalVector(12.474F, 0.758F, -4.931F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.2083F, AnimationHelper.createRotationalVector(-12.157F, 0.731F, 4.956F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.6667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("torso", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.0F, AnimationHelper.createRotationalVector(10.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.625F, AnimationHelper.createRotationalVector(9.962F, -0.867F, 4.924F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.4583F, AnimationHelper.createRotationalVector(9.962F, 0.867F, -4.924F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.2917F, AnimationHelper.createRotationalVector(9.962F, -0.867F, 4.924F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.125F, AnimationHelper.createRotationalVector(9.962F, 0.867F, -4.924F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.6667F, AnimationHelper.createRotationalVector(10.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("legs", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.0F, AnimationHelper.createRotationalVector(7.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.625F, AnimationHelper.createRotationalVector(6.962F, -0.867F, 4.924F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.4583F, AnimationHelper.createRotationalVector(6.962F, 0.867F, -4.924F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.2917F, AnimationHelper.createRotationalVector(6.962F, -0.867F, 4.924F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.125F, AnimationHelper.createRotationalVector(6.962F, 0.867F, -4.924F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.6667F, AnimationHelper.createRotationalVector(7.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("legs", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.5F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.5417F, AnimationHelper.createTranslationalVector(0.0F, 0.3F, 0.5F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.3333F, AnimationHelper.createTranslationalVector(0.0F, -0.9F, 0.5F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.2083F, AnimationHelper.createTranslationalVector(0.0F, 0.3F, 0.5F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.0417F, AnimationHelper.createTranslationalVector(0.0F, -0.9F, 0.5F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.6667F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.5F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("yellow", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.3323F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.3333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.4F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.3323F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.4F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.3333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("green", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.6657F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.6667F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.4F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.999F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.4F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("red", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.999F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.4F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.6657F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.4F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.6667F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("dots", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -1.4F), Transformation.Interpolations.LINEAR)
            ))
            .build();

    public static final Animation countdown = Animation.Builder.create(4.0F).looping()
            .addBoneAnimation("main", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.0F, AnimationHelper.createRotationalVector(2.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.8333F, AnimationHelper.createRotationalVector(2.498F, 0.087F, -1.998F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.75F, AnimationHelper.createRotationalVector(2.497F, -0.109F, 2.497F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.5833F, AnimationHelper.createRotationalVector(2.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.2917F, AnimationHelper.createRotationalVector(11.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.9167F, AnimationHelper.createRotationalVector(2.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("clockhand", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.499F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 45.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.999F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 45.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 90.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.499F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 90.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 135.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.999F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 135.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 180.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.499F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 180.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 225.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.999F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 225.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 270.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(3.499F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 270.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(3.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 315.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("body", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("righthand", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.0F, AnimationHelper.createRotationalVector(-25.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.0833F, AnimationHelper.createRotationalVector(-29.563F, 4.163F, -0.367F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.8333F, AnimationHelper.createRotationalVector(-17.313F, -5.941F, 6.789F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.9167F, AnimationHelper.createRotationalVector(-25.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("umbrella", new Transformation(Transformation.Targets.SCALE,
                    new Keyframe(0.0F, AnimationHelper.createScalingVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("lefthand", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.0833F, AnimationHelper.createRotationalVector(4.974F, 0.758F, -4.931F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.9167F, AnimationHelper.createRotationalVector(-4.657F, 0.731F, 4.956F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.9167F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("torso", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.0F, AnimationHelper.createRotationalVector(4.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.125F, AnimationHelper.createRotationalVector(3.994F, -0.209F, 2.992F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.875F, AnimationHelper.createRotationalVector(3.993F, 0.226F, -3.242F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.9167F, AnimationHelper.createRotationalVector(4.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("legs", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.0F, AnimationHelper.createRotationalVector(4.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.125F, AnimationHelper.createRotationalVector(3.994F, -0.209F, 2.992F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.875F, AnimationHelper.createRotationalVector(3.993F, 0.226F, -3.242F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.9167F, AnimationHelper.createRotationalVector(4.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("legs", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.5F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.5417F, AnimationHelper.createTranslationalVector(0.0F, 0.3F, 0.5F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.375F, AnimationHelper.createTranslationalVector(0.0F, -0.9F, 0.5F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.3333F, AnimationHelper.createTranslationalVector(0.0F, 0.3F, 0.5F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.25F, AnimationHelper.createTranslationalVector(0.0F, -0.9F, 0.5F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.9167F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.5F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("clock", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -1.4F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("mouth", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.4F), Transformation.Interpolations.LINEAR)
            ))
            .build();

    public static final Animation analyzing = Animation.Builder.create(4.0F)
            .addBoneAnimation("main", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.0F, AnimationHelper.createRotationalVector(2.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.0F, AnimationHelper.createRotationalVector(7.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.0F, AnimationHelper.createRotationalVector(2.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("main", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 0.5F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.0F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("body", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 0.5F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.0F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("righthand", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.0F, AnimationHelper.createRotationalVector(-25.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.0833F, AnimationHelper.createRotationalVector(-28.497F, 1.987F, 0.866F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.8333F, AnimationHelper.createRotationalVector(-19.407F, -4.327F, 5.54F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.0F, AnimationHelper.createRotationalVector(-25.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("umbrella", new Transformation(Transformation.Targets.SCALE,
                    new Keyframe(0.0F, AnimationHelper.createScalingVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("lefthand", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.0833F, AnimationHelper.createRotationalVector(3.974F, 0.758F, -3.931F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.9167F, AnimationHelper.createRotationalVector(-3.657F, 0.731F, 3.956F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("legs", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.5F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.5417F, AnimationHelper.createTranslationalVector(0.0F, 0.3F, 0.5F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.375F, AnimationHelper.createTranslationalVector(0.0F, -0.6F, 0.5F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.3333F, AnimationHelper.createTranslationalVector(0.0F, 0.3F, 0.5F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.25F, AnimationHelper.createTranslationalVector(0.0F, -0.6F, 0.5F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.5F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("three", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(1.0833F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.125F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 14.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -14.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.2917F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.5417F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 14.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.6667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -14.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 14.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.875F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -14.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.9167F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(3.4583F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(3.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 14.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(3.625F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -14.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(3.7083F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 14.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(3.8333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -14.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(3.875F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("three", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.4F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.874F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.4F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.875F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.124F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.125F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.2083F, AnimationHelper.createTranslationalVector(0.0F, 1.25F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.2917F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.2073F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.2083F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.4F), Transformation.Interpolations.LINEAR),
                    new Keyframe(3.249F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.4F), Transformation.Interpolations.LINEAR),
                    new Keyframe(3.25F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(3.5F, AnimationHelper.createTranslationalVector(0.0F, 1.25F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(3.5833F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("one", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.4F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.0823F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.4F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.0833F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.3323F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.3333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.4167F, AnimationHelper.createTranslationalVector(0.0F, 1.25F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.2073F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.2083F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.4F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.4157F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.4F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.4167F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.6657F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.6667F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.75F, AnimationHelper.createTranslationalVector(0.0F, 1.25F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.8333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("two", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.4F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.4573F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.4F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.4583F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.7073F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.7083F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.7917F, AnimationHelper.createTranslationalVector(0.0F, 1.25F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.875F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.2073F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.2083F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.4F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.8323F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.4F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.8333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(3.0823F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(3.0833F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(3.1667F, AnimationHelper.createTranslationalVector(0.0F, 1.25F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(3.25F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("processing", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -1.4F), Transformation.Interpolations.LINEAR)
            ))
            .build();

    public static final Animation answer_incorrect = Animation.Builder.create(11.1667F)
            .addBoneAnimation("main", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.8333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.9583F, AnimationHelper.createRotationalVector(0.0F, -25.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.125F, AnimationHelper.createRotationalVector(0.0F, 25.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.2917F, AnimationHelper.createRotationalVector(0.0F, -25.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.4583F, AnimationHelper.createRotationalVector(0.0F, 25.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.625F, AnimationHelper.createRotationalVector(0.0F, -25.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.7917F, AnimationHelper.createRotationalVector(0.0F, 25.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.9583F, AnimationHelper.createRotationalVector(0.0F, -25.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.125F, AnimationHelper.createRotationalVector(0.0F, 25.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.2917F, AnimationHelper.createRotationalVector(0.0F, -25.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.4583F, AnimationHelper.createRotationalVector(0.0F, 25.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.625F, AnimationHelper.createRotationalVector(0.0F, -25.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.7917F, AnimationHelper.createRotationalVector(0.0F, 25.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.9583F, AnimationHelper.createRotationalVector(0.0F, -10.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(3.0833F, AnimationHelper.createRotationalVector(0.0F, 10.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(3.2083F, AnimationHelper.createRotationalVector(0.0F, -10.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(3.3333F, AnimationHelper.createRotationalVector(0.0F, 10.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(3.4583F, AnimationHelper.createRotationalVector(0.0F, -10.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(3.5833F, AnimationHelper.createRotationalVector(0.0F, 10.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(3.6667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("main", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(9.7083F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(9.874F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(9.875F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(10.625F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.5F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("angry", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0417F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(4.4573F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(4.4583F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -1.4F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("righthand", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(4.8333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(5.0417F, AnimationHelper.createRotationalVector(-191.544F, -2.301F, -22.52F), Transformation.Interpolations.LINEAR),
                    new Keyframe(5.0833F, AnimationHelper.createRotationalVector(-191.184F, -3.682F, -29.391F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.125F, AnimationHelper.createRotationalVector(-191.735F, -0.887F, -15.661F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.1667F, AnimationHelper.createRotationalVector(-191.184F, -3.682F, -29.391F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.2083F, AnimationHelper.createRotationalVector(-191.735F, -0.887F, -15.661F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.25F, AnimationHelper.createRotationalVector(-191.184F, -3.682F, -29.391F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.2917F, AnimationHelper.createRotationalVector(-191.735F, -0.887F, -15.661F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.3333F, AnimationHelper.createRotationalVector(-191.184F, -3.682F, -29.391F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.375F, AnimationHelper.createRotationalVector(-191.735F, -0.887F, -15.661F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.4167F, AnimationHelper.createRotationalVector(-191.184F, -3.682F, -29.391F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.4583F, AnimationHelper.createRotationalVector(-191.735F, -0.887F, -15.661F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.5F, AnimationHelper.createRotationalVector(-191.184F, -3.682F, -29.391F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.5417F, AnimationHelper.createRotationalVector(-191.735F, -0.887F, -15.661F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.5833F, AnimationHelper.createRotationalVector(-191.184F, -3.682F, -29.391F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.625F, AnimationHelper.createRotationalVector(-191.735F, -0.887F, -15.661F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.6667F, AnimationHelper.createRotationalVector(-191.544F, -2.301F, -22.52F), Transformation.Interpolations.LINEAR),
                    new Keyframe(5.8333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(6.0833F, AnimationHelper.createRotationalVector(-25.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(7.1667F, AnimationHelper.createRotationalVector(-28.497F, 1.987F, 0.866F), Transformation.Interpolations.CUBIC),
                    new Keyframe(8.9167F, AnimationHelper.createRotationalVector(-19.407F, -4.327F, 5.54F), Transformation.Interpolations.CUBIC),
                    new Keyframe(9.7083F, AnimationHelper.createRotationalVector(-23.1F, -1.44F, 1.73F), Transformation.Interpolations.CUBIC),
                    new Keyframe(10.625F, AnimationHelper.createRotationalVector(-25.171F, 25.039F, 6.148F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("righthand", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(4.8333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(5.0417F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(5.7083F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(5.875F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(7.3333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(8.0F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(8.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("microphone", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.2083F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.25F, AnimationHelper.createRotationalVector(10.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.4167F, AnimationHelper.createRotationalVector(2.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("microphone", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.1667F, AnimationHelper.createTranslationalVector(0.0F, -8.5F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.25F, AnimationHelper.createTranslationalVector(0.0F, -7.5F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.3333F, AnimationHelper.createTranslationalVector(0.0F, -8.5F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.1667F, AnimationHelper.createTranslationalVector(0.0F, -9.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.5833F, AnimationHelper.createTranslationalVector(0.0F, -9.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(4.1667F, AnimationHelper.createTranslationalVector(0.0F, -8.72F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(4.5833F, AnimationHelper.createTranslationalVector(0.0F, -8.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(4.75F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("umbrella", new Transformation(Transformation.Targets.SCALE,
                    new Keyframe(0.0F, AnimationHelper.createScalingVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("lefthand", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(4.8333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(5.0417F, AnimationHelper.createRotationalVector(-197.508F, 10.151F, 17.292F), Transformation.Interpolations.LINEAR),
                    new Keyframe(5.0833F, AnimationHelper.createRotationalVector(-196.183F, 12.182F, 24.12F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.125F, AnimationHelper.createRotationalVector(-198.571F, 7.98F, 10.552F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.1667F, AnimationHelper.createRotationalVector(-196.183F, 12.182F, 24.12F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.2083F, AnimationHelper.createRotationalVector(-198.571F, 7.98F, 10.552F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.25F, AnimationHelper.createRotationalVector(-196.183F, 12.182F, 24.12F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.2917F, AnimationHelper.createRotationalVector(-198.571F, 7.98F, 10.552F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.3333F, AnimationHelper.createRotationalVector(-196.183F, 12.182F, 24.12F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.375F, AnimationHelper.createRotationalVector(-198.571F, 7.98F, 10.552F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.4167F, AnimationHelper.createRotationalVector(-196.183F, 12.182F, 24.12F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.4583F, AnimationHelper.createRotationalVector(-198.571F, 7.98F, 10.552F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.5F, AnimationHelper.createRotationalVector(-196.183F, 12.182F, 24.12F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.5417F, AnimationHelper.createRotationalVector(-198.571F, 7.98F, 10.552F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.5833F, AnimationHelper.createRotationalVector(-196.183F, 12.182F, 24.12F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.625F, AnimationHelper.createRotationalVector(-198.571F, 7.98F, 10.552F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.6667F, AnimationHelper.createRotationalVector(-197.508F, 10.151F, 17.292F), Transformation.Interpolations.LINEAR),
                    new Keyframe(5.8333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(5.874F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(5.875F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.125F, AnimationHelper.createRotationalVector(-84.995F, 2.49F, 0.218F), Transformation.Interpolations.LINEAR),
                    new Keyframe(6.1667F, AnimationHelper.createRotationalVector(-84.93F, 9.462F, 0.835F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.2083F, AnimationHelper.createRotationalVector(-84.983F, -4.482F, -0.393F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.25F, AnimationHelper.createRotationalVector(-84.93F, 9.462F, 0.835F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.2917F, AnimationHelper.createRotationalVector(-84.983F, -4.482F, -0.393F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.3333F, AnimationHelper.createRotationalVector(-84.93F, 9.462F, 0.835F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.375F, AnimationHelper.createRotationalVector(-84.983F, -4.482F, -0.393F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.4167F, AnimationHelper.createRotationalVector(-84.93F, 9.462F, 0.835F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.4583F, AnimationHelper.createRotationalVector(-84.983F, -4.482F, -0.393F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.5F, AnimationHelper.createRotationalVector(-84.93F, 9.462F, 0.835F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.5417F, AnimationHelper.createRotationalVector(-84.983F, -4.482F, -0.393F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.5833F, AnimationHelper.createRotationalVector(-84.93F, 9.462F, 0.835F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.625F, AnimationHelper.createRotationalVector(-84.983F, -4.482F, -0.393F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.6667F, AnimationHelper.createRotationalVector(-84.93F, 9.462F, 0.835F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.7083F, AnimationHelper.createRotationalVector(-84.983F, -4.482F, -0.393F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.75F, AnimationHelper.createRotationalVector(-84.995F, 2.49F, 0.218F), Transformation.Interpolations.LINEAR),
                    new Keyframe(7.0417F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(8.375F, AnimationHelper.createRotationalVector(-3.729F, 0.085F, -6.022F), Transformation.Interpolations.CUBIC),
                    new Keyframe(9.7083F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(10.625F, AnimationHelper.createRotationalVector(-25.0F, -27.5F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("lefthand", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(7.3333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(8.0F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(8.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("legs", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.5417F, AnimationHelper.createTranslationalVector(0.0F, 0.3F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.375F, AnimationHelper.createTranslationalVector(0.0F, -0.6F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.3333F, AnimationHelper.createTranslationalVector(0.0F, 0.3F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.25F, AnimationHelper.createTranslationalVector(0.0F, -0.6F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.1667F, AnimationHelper.createTranslationalVector(0.0F, 0.3F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.0833F, AnimationHelper.createTranslationalVector(0.0F, -0.6F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(6.9157F, AnimationHelper.createTranslationalVector(0.0F, -0.6F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(6.9167F, AnimationHelper.createTranslationalVector(0.0F, -0.6F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(7.8333F, AnimationHelper.createTranslationalVector(0.0F, 0.3F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(8.75F, AnimationHelper.createTranslationalVector(0.0F, -0.6F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("actualhand", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.0F, AnimationHelper.createRotationalVector(-25.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.2083F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("mouth", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.1F), Transformation.Interpolations.LINEAR),
                    new Keyframe(4.7073F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.1F), Transformation.Interpolations.LINEAR),
                    new Keyframe(4.7083F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.4F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("triviabot", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.0833F, AnimationHelper.createTranslationalVector(0.0F, 0.5F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.5833F, AnimationHelper.createTranslationalVector(0.0F, -0.44F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(4.75F, AnimationHelper.createTranslationalVector(0.0F, -7.94F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.875F, AnimationHelper.createTranslationalVector(0.0F, -7.94F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(5.0407F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(5.0417F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(7.5F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(9.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(9.7083F, AnimationHelper.createTranslationalVector(0.0F, -2.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("neckpivot", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(4.1667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(4.375F, AnimationHelper.createRotationalVector(47.508F, -0.692F, -1.33F), Transformation.Interpolations.LINEAR),
                    new Keyframe(4.875F, AnimationHelper.createRotationalVector(47.508F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(5.125F, AnimationHelper.createRotationalVector(-30.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(5.7083F, AnimationHelper.createRotationalVector(-30.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(5.9583F, AnimationHelper.createRotationalVector(5.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(7.0F, AnimationHelper.createRotationalVector(5.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(7.5F, AnimationHelper.createRotationalVector(5.042F, 7.471F, 0.657F), Transformation.Interpolations.CUBIC),
                    new Keyframe(8.5F, AnimationHelper.createRotationalVector(5.042F, -7.471F, -0.657F), Transformation.Interpolations.CUBIC),
                    new Keyframe(8.9583F, AnimationHelper.createRotationalVector(5.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(9.4167F, AnimationHelper.createRotationalVector(14.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(9.875F, AnimationHelper.createRotationalVector(5.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(10.625F, AnimationHelper.createRotationalVector(-22.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .build();

    public static final Animation answer_correct = Animation.Builder.create(9.0F)
            .addBoneAnimation("main", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.2083F, AnimationHelper.createRotationalVector(5.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.4583F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.6667F, AnimationHelper.createRotationalVector(-0.173F, 0.0F, -5.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.2083F, AnimationHelper.createRotationalVector(-0.17F, 0.0F, 5.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.5F, AnimationHelper.createRotationalVector(-0.173F, 0.0F, -5.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.8333F, AnimationHelper.createRotationalVector(-0.17F, 0.0F, 5.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.1667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.3333F, AnimationHelper.createRotationalVector(17.489F, 0.601F, -1.907F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.6667F, AnimationHelper.createRotationalVector(17.489F, 0.601F, -1.907F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.8333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.5F, AnimationHelper.createRotationalVector(17.489F, 0.601F, -1.907F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.9167F, AnimationHelper.createRotationalVector(17.489F, 0.601F, -1.907F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.125F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -1.5F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.375F, AnimationHelper.createRotationalVector(-4.667F, -0.163F, -1.481F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.6667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.875F, AnimationHelper.createRotationalVector(17.489F, -1.398F, 2.092F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.2917F, AnimationHelper.createRotationalVector(17.489F, -1.398F, 2.092F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.5417F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.875F, AnimationHelper.createRotationalVector(-2.5F, 0.0F, -2.5F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.7917F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(7.5F, AnimationHelper.createRotationalVector(14.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(7.9583F, AnimationHelper.createRotationalVector(5.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(8.7083F, AnimationHelper.createRotationalVector(-22.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("main", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.6667F, AnimationHelper.createTranslationalVector(0.0F, 3.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.8333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.7907F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.7917F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.9583F, AnimationHelper.createTranslationalVector(0.0F, 3.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.125F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.1657F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.1667F, AnimationHelper.createTranslationalVector(0.0F, -0.19F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.3333F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, -1.3F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.625F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, -1.3F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.8333F, AnimationHelper.createTranslationalVector(0.0F, 1.5F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.125F, AnimationHelper.createTranslationalVector(0.0F, 1.5F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.3333F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, -1.3F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.9167F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, -1.3F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.1667F, AnimationHelper.createTranslationalVector(0.0F, 1.5F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.6667F, AnimationHelper.createTranslationalVector(0.0F, 1.5F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.9167F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, -1.3F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.4167F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, -1.3F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.6667F, AnimationHelper.createTranslationalVector(0.0F, 2.5F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.4583F, AnimationHelper.createTranslationalVector(0.0F, 2.5F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.7083F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(7.249F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(7.25F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(7.9583F, AnimationHelper.createTranslationalVector(0.0F, -2.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(8.124F, AnimationHelper.createTranslationalVector(0.0F, -2.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(8.125F, AnimationHelper.createTranslationalVector(0.0F, -2.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(8.7083F, AnimationHelper.createTranslationalVector(0.0F, -2.5F, 1.5F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("happy", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.124F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.125F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -1.4F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("body", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.9167F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.2083F, AnimationHelper.createRotationalVector(-0.134F, -0.884F, -7.418F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.7917F, AnimationHelper.createRotationalVector(-0.134F, -0.884F, -3.418F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("body", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.6667F, AnimationHelper.createTranslationalVector(0.0F, 3.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.8333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.9157F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.9167F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.2083F, AnimationHelper.createTranslationalVector(2.35F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.7907F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.7917F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.9583F, AnimationHelper.createTranslationalVector(0.0F, 3.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.125F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.3333F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.625F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.875F, AnimationHelper.createTranslationalVector(0.0F, 1.5F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.125F, AnimationHelper.createTranslationalVector(0.0F, 1.5F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.3333F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.9167F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.1667F, AnimationHelper.createTranslationalVector(0.0F, 1.5F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.6667F, AnimationHelper.createTranslationalVector(0.0F, 1.5F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.9167F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.4167F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.6667F, AnimationHelper.createTranslationalVector(0.0F, 2.5F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.4583F, AnimationHelper.createTranslationalVector(0.0F, 2.5F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.7083F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(7.25F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(7.9583F, AnimationHelper.createTranslationalVector(0.0F, -2.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("righthand", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.0F, AnimationHelper.createRotationalVector(-25.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.4583F, AnimationHelper.createRotationalVector(-10.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.625F, AnimationHelper.createRotationalVector(-157.309F, 11.26F, -25.249F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.7917F, AnimationHelper.createRotationalVector(-157.309F, 11.26F, -25.249F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.9167F, AnimationHelper.createRotationalVector(-158.311F, 13.149F, -29.986F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.0417F, AnimationHelper.createRotationalVector(-156.475F, 9.296F, -20.575F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.1667F, AnimationHelper.createRotationalVector(-158.311F, 13.149F, -29.986F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.2917F, AnimationHelper.createRotationalVector(-156.475F, 9.296F, -20.575F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.4167F, AnimationHelper.createRotationalVector(-158.311F, 13.149F, -29.986F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.5417F, AnimationHelper.createRotationalVector(-156.475F, 9.296F, -20.575F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.6667F, AnimationHelper.createRotationalVector(-158.311F, 13.149F, -29.986F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.7917F, AnimationHelper.createRotationalVector(-156.475F, 9.296F, -20.575F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.9167F, AnimationHelper.createRotationalVector(-158.311F, 13.149F, -29.986F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.0417F, AnimationHelper.createRotationalVector(-156.475F, 9.296F, -20.575F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.1667F, AnimationHelper.createRotationalVector(-158.311F, 13.149F, -29.986F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.375F, AnimationHelper.createRotationalVector(-69.559F, 16.516F, -19.073F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.625F, AnimationHelper.createRotationalVector(-69.559F, 16.516F, -19.073F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.8333F, AnimationHelper.createRotationalVector(-158.311F, 13.149F, -29.986F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.9167F, AnimationHelper.createRotationalVector(-156.475F, 9.296F, -20.575F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.0417F, AnimationHelper.createRotationalVector(-158.311F, 13.149F, -29.986F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.1667F, AnimationHelper.createRotationalVector(-156.475F, 9.296F, -20.575F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.25F, AnimationHelper.createRotationalVector(-158.311F, 13.149F, -29.986F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.5417F, AnimationHelper.createRotationalVector(-69.559F, 16.516F, -19.073F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.875F, AnimationHelper.createRotationalVector(-69.559F, 16.516F, -19.073F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.125F, AnimationHelper.createRotationalVector(-158.311F, 13.149F, -29.986F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.2083F, AnimationHelper.createRotationalVector(-156.475F, 9.296F, -20.575F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.3333F, AnimationHelper.createRotationalVector(-158.311F, 13.149F, -29.986F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.4583F, AnimationHelper.createRotationalVector(-156.475F, 9.296F, -20.575F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.5833F, AnimationHelper.createRotationalVector(-158.311F, 13.149F, -29.986F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.7083F, AnimationHelper.createRotationalVector(-156.475F, 9.296F, -20.575F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.0F, AnimationHelper.createRotationalVector(-69.559F, 16.516F, -19.073F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.3333F, AnimationHelper.createRotationalVector(-69.559F, 16.516F, -19.073F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.5833F, AnimationHelper.createRotationalVector(-158.311F, 13.149F, -29.986F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.6667F, AnimationHelper.createRotationalVector(-156.475F, 9.296F, -20.575F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.7917F, AnimationHelper.createRotationalVector(-158.311F, 13.149F, -29.986F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.9167F, AnimationHelper.createRotationalVector(-156.475F, 9.296F, -20.575F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.0417F, AnimationHelper.createRotationalVector(-158.311F, 13.149F, -29.986F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.1667F, AnimationHelper.createRotationalVector(-156.475F, 9.296F, -20.575F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.2917F, AnimationHelper.createRotationalVector(-158.311F, 13.149F, -29.986F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.4167F, AnimationHelper.createRotationalVector(-156.475F, 9.296F, -20.575F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.6667F, AnimationHelper.createRotationalVector(-69.559F, 16.516F, -19.073F), Transformation.Interpolations.CUBIC),
                    new Keyframe(7.0F, AnimationHelper.createRotationalVector(-69.559F, 16.516F, -19.073F), Transformation.Interpolations.CUBIC),
                    new Keyframe(8.125F, AnimationHelper.createRotationalVector(-55.851F, 31.765F, -14.549F), Transformation.Interpolations.CUBIC),
                    new Keyframe(9.0F, AnimationHelper.createRotationalVector(-57.071F, 44.222F, -15.459F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("righthand", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.4583F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.625F, AnimationHelper.createTranslationalVector(-0.5F, 1.5F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.2073F, AnimationHelper.createTranslationalVector(-0.5F, 1.5F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.2083F, AnimationHelper.createTranslationalVector(-0.5F, 1.5F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.4583F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.8333F, AnimationHelper.createTranslationalVector(-0.5F, 1.5F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.3333F, AnimationHelper.createTranslationalVector(-0.5F, 1.5F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.7083F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(4.1667F, AnimationHelper.createTranslationalVector(-0.5F, 1.5F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.75F, AnimationHelper.createTranslationalVector(-0.5F, 1.5F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.125F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(5.625F, AnimationHelper.createTranslationalVector(-0.5F, 1.5F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.4583F, AnimationHelper.createTranslationalVector(-0.5F, 1.5F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.8333F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("umbrella", new Transformation(Transformation.Targets.SCALE,
                    new Keyframe(0.0F, AnimationHelper.createScalingVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("lefthand", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.4583F, AnimationHelper.createRotationalVector(0.218F, -4.995F, -2.509F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.625F, AnimationHelper.createRotationalVector(-165.893F, -16.507F, 31.08F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.7083F, AnimationHelper.createRotationalVector(-165.893F, -16.507F, 31.08F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.7917F, AnimationHelper.createRotationalVector(-164.518F, -15.23F, 26.054F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.9167F, AnimationHelper.createRotationalVector(-167.389F, -17.663F, 36.169F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.0417F, AnimationHelper.createRotationalVector(-164.518F, -15.23F, 26.054F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.1667F, AnimationHelper.createRotationalVector(-167.389F, -17.663F, 36.169F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.2917F, AnimationHelper.createRotationalVector(-164.518F, -15.23F, 26.054F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.4167F, AnimationHelper.createRotationalVector(-167.389F, -17.663F, 36.169F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.5417F, AnimationHelper.createRotationalVector(-164.518F, -15.23F, 26.054F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.6667F, AnimationHelper.createRotationalVector(-167.389F, -17.663F, 36.169F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.7917F, AnimationHelper.createRotationalVector(-164.518F, -15.23F, 26.054F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.9167F, AnimationHelper.createRotationalVector(-167.389F, -17.663F, 36.169F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.0417F, AnimationHelper.createRotationalVector(-164.518F, -15.23F, 26.054F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.125F, AnimationHelper.createRotationalVector(-167.389F, -17.663F, 36.169F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.375F, AnimationHelper.createRotationalVector(-75.635F, -11.276F, 14.845F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.625F, AnimationHelper.createRotationalVector(-75.635F, -11.276F, 14.845F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.8333F, AnimationHelper.createRotationalVector(-167.389F, -17.663F, 36.169F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.9167F, AnimationHelper.createRotationalVector(-164.518F, -15.23F, 26.054F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.0417F, AnimationHelper.createRotationalVector(-167.389F, -17.663F, 36.169F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.1667F, AnimationHelper.createRotationalVector(-164.518F, -15.23F, 26.054F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.25F, AnimationHelper.createRotationalVector(-167.389F, -17.663F, 36.169F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.5417F, AnimationHelper.createRotationalVector(-75.635F, -11.276F, 14.845F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.875F, AnimationHelper.createRotationalVector(-75.635F, -11.276F, 14.845F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.125F, AnimationHelper.createRotationalVector(-167.389F, -17.663F, 36.169F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.2083F, AnimationHelper.createRotationalVector(-164.518F, -15.23F, 26.054F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.3333F, AnimationHelper.createRotationalVector(-167.389F, -17.663F, 36.169F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.4583F, AnimationHelper.createRotationalVector(-164.518F, -15.23F, 26.054F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.5833F, AnimationHelper.createRotationalVector(-167.389F, -17.663F, 36.169F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.7083F, AnimationHelper.createRotationalVector(-164.518F, -15.23F, 26.054F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.0F, AnimationHelper.createRotationalVector(-75.635F, -11.276F, 14.845F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.3333F, AnimationHelper.createRotationalVector(-75.635F, -11.276F, 14.845F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.5833F, AnimationHelper.createRotationalVector(-167.389F, -17.663F, 36.169F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.6667F, AnimationHelper.createRotationalVector(-164.518F, -15.23F, 26.054F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.7917F, AnimationHelper.createRotationalVector(-167.389F, -17.663F, 36.169F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.9167F, AnimationHelper.createRotationalVector(-164.518F, -15.23F, 26.054F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.0417F, AnimationHelper.createRotationalVector(-167.389F, -17.663F, 36.169F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.1667F, AnimationHelper.createRotationalVector(-164.518F, -15.23F, 26.054F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.2917F, AnimationHelper.createRotationalVector(-167.389F, -17.663F, 36.169F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.4167F, AnimationHelper.createRotationalVector(-164.518F, -15.23F, 26.054F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.6667F, AnimationHelper.createRotationalVector(-75.635F, -11.276F, 14.845F), Transformation.Interpolations.CUBIC),
                    new Keyframe(7.0F, AnimationHelper.createRotationalVector(-75.635F, -11.276F, 14.845F), Transformation.Interpolations.CUBIC),
                    new Keyframe(8.125F, AnimationHelper.createRotationalVector(-51.745F, -25.726F, 13.071F), Transformation.Interpolations.CUBIC),
                    new Keyframe(9.0F, AnimationHelper.createRotationalVector(-66.54F, -33.925F, 27.566F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("torso", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.4583F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.6667F, AnimationHelper.createRotationalVector(0.002F, -0.109F, 2.502F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.9157F, AnimationHelper.createRotationalVector(0.002F, -0.109F, 2.502F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.9167F, AnimationHelper.createRotationalVector(0.002F, -0.109F, 2.502F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.125F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.1657F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.1667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.3333F, AnimationHelper.createRotationalVector(2.988F, -0.261F, 2.993F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.6667F, AnimationHelper.createRotationalVector(2.988F, -0.261F, 2.993F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.8333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.0833F, AnimationHelper.createRotationalVector(-1.011F, 0.338F, -1.006F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.375F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.5417F, AnimationHelper.createRotationalVector(1.988F, -0.261F, 1.993F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.875F, AnimationHelper.createRotationalVector(1.988F, -0.261F, 1.993F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.0417F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.2917F, AnimationHelper.createRotationalVector(-2.011F, 0.738F, -1.606F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.625F, AnimationHelper.createRotationalVector(-2.011F, 0.738F, -1.606F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.9167F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.0833F, AnimationHelper.createRotationalVector(1.988F, -0.261F, 0.893F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.4167F, AnimationHelper.createRotationalVector(1.988F, -0.261F, 0.893F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.5833F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.4583F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.7083F, AnimationHelper.createRotationalVector(-2.611F, 0.738F, -1.306F), Transformation.Interpolations.CUBIC),
                    new Keyframe(7.0417F, AnimationHelper.createRotationalVector(-2.611F, 0.738F, -1.306F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("legs", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.4583F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.6667F, AnimationHelper.createRotationalVector(5.0F, 0.0F, -2.5F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.9167F, AnimationHelper.createRotationalVector(5.0F, 0.0F, -2.5F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.2083F, AnimationHelper.createRotationalVector(4.882F, -1.08F, 9.953F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.5F, AnimationHelper.createRotationalVector(5.0F, 0.0F, -2.5F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.7917F, AnimationHelper.createRotationalVector(4.882F, -1.08F, 9.953F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.1667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.4167F, AnimationHelper.createRotationalVector(-7.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.6667F, AnimationHelper.createRotationalVector(-7.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.8333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(3.125F, AnimationHelper.createRotationalVector(2.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.4583F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(3.7083F, AnimationHelper.createRotationalVector(-7.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.9583F, AnimationHelper.createRotationalVector(-7.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.125F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(4.5417F, AnimationHelper.createRotationalVector(4.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(5.0F, AnimationHelper.createRotationalVector(-7.5F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.25F, AnimationHelper.createRotationalVector(-7.5F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.4167F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("legs", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, -0.6F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.9167F, AnimationHelper.createTranslationalVector(0.0F, 0.3F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.8333F, AnimationHelper.createTranslationalVector(0.0F, -0.6F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.75F, AnimationHelper.createTranslationalVector(0.0F, 0.3F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.5833F, AnimationHelper.createTranslationalVector(0.0F, -0.6F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.3333F, AnimationHelper.createTranslationalVector(0.0F, 0.3F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.625F, AnimationHelper.createTranslationalVector(0.0F, 0.3F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.1667F, AnimationHelper.createTranslationalVector(0.0F, -0.6F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(5.8333F, AnimationHelper.createTranslationalVector(0.0F, 0.3F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.4583F, AnimationHelper.createTranslationalVector(0.0F, 0.3F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(6.9167F, AnimationHelper.createTranslationalVector(0.0F, -0.4F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("mouth", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.1F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.374F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.1F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.375F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.4F), Transformation.Interpolations.LINEAR)
            ))
            .build();

    public static final Animation snail_transform = Animation.Builder.create(3.0F)
            .addBoneAnimation("main", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(2.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.5F, AnimationHelper.createRotationalVector(0.0F, -90.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.9167F, AnimationHelper.createRotationalVector(0.0F, -90.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("main", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, -12.375F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.5F, AnimationHelper.createTranslationalVector(0.0F, -14.5F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("body", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(2.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.4167F, AnimationHelper.createRotationalVector(0.0F, -90.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.9167F, AnimationHelper.createRotationalVector(0.0F, -90.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("body", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, -12.375F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.5F, AnimationHelper.createTranslationalVector(0.0F, -14.5F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("righthand", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.0F, AnimationHelper.createRotationalVector(-25.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.0F, AnimationHelper.createRotationalVector(-180.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("righthand", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("actualhand", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(2.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.5F, AnimationHelper.createRotationalVector(-88.999F, -89.0F, 102.249F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("actualhand", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.5F, AnimationHelper.createTranslationalVector(-1.675F, -1.125F, -2.175F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("actualhand", new Transformation(Transformation.Targets.SCALE,
                    new Keyframe(2.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.5F, AnimationHelper.createScalingVector(0.6F, 1.0F, 0.6F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("microphone", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.2917F, AnimationHelper.createRotationalVector(-72.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("microphone", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.2917F, AnimationHelper.createTranslationalVector(0.0F, 2.75F, 5.25F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("microphone", new Transformation(Transformation.Targets.SCALE,
                    new Keyframe(0.5F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.0F, AnimationHelper.createScalingVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("umbrella", new Transformation(Transformation.Targets.SCALE,
                    new Keyframe(0.0F, AnimationHelper.createScalingVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("lefthand", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.9583F, AnimationHelper.createRotationalVector(-180.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.5F, AnimationHelper.createRotationalVector(-95.0F, 87.0F, 80.5F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("lefthand", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -1.25F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.25F, AnimationHelper.createTranslationalVector(-9.0F, 0.0F, -6.87F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.5F, AnimationHelper.createTranslationalVector(-17.4F, 0.925F, -1.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("lefthand", new Transformation(Transformation.Targets.SCALE,
                    new Keyframe(2.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.5F, AnimationHelper.createScalingVector(0.6F, 1.0F, 0.6F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("torso", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(2.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.5F, AnimationHelper.createRotationalVector(0.0F, 90.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.9167F, AnimationHelper.createRotationalVector(0.0F, 90.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("torso", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.5F, AnimationHelper.createTranslationalVector(-2.8F, 2.275F, 0.7F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("torso", new Transformation(Transformation.Targets.SCALE,
                    new Keyframe(2.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.5F, AnimationHelper.createScalingVector(0.6F, 0.6F, 2.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("legs", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, 8.5F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("legs", new Transformation(Transformation.Targets.SCALE,
                    new Keyframe(1.9583F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.0F, AnimationHelper.createScalingVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("bottom", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, 2.25F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("bottom", new Transformation(Transformation.Targets.SCALE,
                    new Keyframe(1.9583F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.0F, AnimationHelper.createScalingVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("snail", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -1.4F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("mouth", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.4F), Transformation.Interpolations.LINEAR)
            ))
            .build();
}
