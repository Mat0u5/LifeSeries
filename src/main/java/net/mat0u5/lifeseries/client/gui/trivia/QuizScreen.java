package net.mat0u5.lifeseries.client.gui.trivia;

import com.mojang.blaze3d.systems.RenderSystem;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.client.gui.DefaultScreen;
import net.mat0u5.lifeseries.client.render.RenderUtils;
import net.mat0u5.lifeseries.client.render.TextColors;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.trivia.Trivia;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
//? if >= 1.21.6 {
/*import net.minecraft.client.render.entity.state.EntityRenderState;
*///?}

public class QuizScreen extends DefaultScreen {
    private static final int[] ANSWER_COLORS = {
            TextColors.TRIVIA_BLUE, TextColors.TRIVIA_ORANGE, TextColors.TRIVIA_LIME, TextColors.TRIVIA_YELLOW, TextColors.TRIVIA_RED
    };

    private final List<List<OrderedText>> answers = new ArrayList<>();
    private String difficulty = "Difficulty: null";
    private long timerSeconds = 120;
    private final List<Rectangle> answerRects = new ArrayList<>();

    public QuizScreen() {
        super(Text.literal("Quiz Screen"));
    }

    @Override
    protected void init() {
        super.init();
        timerSeconds = Trivia.getRemainingTime();

        int fifth3 = startX + (BG_WIDTH / 5) * 3;
        int answersStartX = fifth3 + 15;
        int answersStopX = endX - 15;

        int maxWidth = answersStopX - answersStartX;

        int currentYPos = startY + 30;
        int gap = 8;
        answers.clear();
        answerRects.clear();
        for (int i = 0; i < Trivia.answers.size(); i++) {
            char answerIndex = (char) (i+65);
            MutableText label = Text.literal(answerIndex + ": ").formatted(Formatting.BOLD);
            MutableText answerText = Text.literal(Trivia.answers.get(i));
            answerText.setStyle(answerText.getStyle().withBold(false));
            Text text = label.append(answerText);
            List<OrderedText> answer = this.textRenderer.wrapLines(text, maxWidth);
            answers.add(answer);
            int answerBoxHeight = this.textRenderer.fontHeight * answer.size()+2;
            int answerBoxWidth = 0;
            for (OrderedText line : answer) {
                int lineWidth = this.textRenderer.getWidth(line);
                if (lineWidth > answerBoxWidth) answerBoxWidth = lineWidth;
            }
            answerBoxWidth += 2;

            Rectangle rect = new Rectangle(answersStartX, currentYPos, answerBoxWidth, answerBoxHeight);
            answerRects.add(rect);
            currentYPos += answerBoxHeight + gap;
        }
        switch (Trivia.difficulty) {
            case 1:
                difficulty = "Difficulty: Easy";
                break;
            case 2:
                difficulty = "Difficulty: Medium";
                break;
            case 3:
                difficulty = "Difficulty: Hard";
                break;
            default:
                difficulty = "Difficulty: null";
        }
    }

    @Override
    public void tick() {
        super.tick();
        timerSeconds = Trivia.getRemainingTime();
        if (timerSeconds <= 0) {
            this.close();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { // Left-click
            for (int i = 0; i < answerRects.size(); i++) {
                if (answerRects.get(i).contains(mouseX, mouseY)) {
                    if (this.client != null) this.client.setScreen(new ConfirmQuizAnswerScreen(this, i));
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY) {
        // X
        int fifth1 = startX + (BG_WIDTH / 5);
        int fifth2 = startX + (BG_WIDTH / 5) * 2;
        int fifth4 = startX + (BG_WIDTH / 5) * 4;
        int questionX = startX + 10;
        int questionWidth = (fifth2-10) - questionX;

        // Y
        int minY = startY + 9;
        int maxY = endY - 23;
        int questionY = startY + 30;

        /*
        testDrawX(context, 0);
        testDrawX(context, this.width-1);
        testDrawY(context, 0);
        testDrawY(context, this.height-1);
        testDrawX(context, startX);
        testDrawY(context, startY);
        testDrawX(context, endX);
        testDrawY(context, endY);
        testDrawX(context, fifth1);
        testDrawX(context, fifth2);
        testDrawX(context, fifth3);
        testDrawX(context, fifth4);
        */

        // Timer
        long minutes = timerSeconds / 60;
        long seconds = timerSeconds - minutes * 60;
        String secondsStr = String.valueOf(seconds);
        String minutesStr = String.valueOf(minutes);
        while (secondsStr.length() < 2) secondsStr = "0" + secondsStr;
        while (minutesStr.length() < 2) minutesStr = "0" + minutesStr;

        if (timerSeconds <= 5) RenderUtils.drawTextCenter(context, this.textRenderer, TextColors.TIMER_RED, Text.of(minutesStr + ":" + secondsStr), centerX, minY);
        else if (timerSeconds <= 30) RenderUtils.drawTextCenter(context, this.textRenderer, TextColors.TIMER_ORANGE, Text.of(minutesStr + ":" + secondsStr), centerX, minY);
        else RenderUtils.drawTextCenter(context, this.textRenderer, Text.of(minutesStr + ":" + secondsStr), centerX, minY);

        // Difficulty
        RenderUtils.drawTextCenter(context, this.textRenderer, Text.of(difficulty), centerX, maxY);

        // Questions
        RenderUtils.drawTextCenter(context, this.textRenderer, Text.literal("Question").formatted(Formatting.UNDERLINE), fifth1, minY);
        List<OrderedText> wrappedQuestion = this.textRenderer.wrapLines(Text.literal(Trivia.question), questionWidth);
        for (int i = 0; i < wrappedQuestion.size(); i++) {
            RenderUtils.drawOrderedTextLeft(context, this.textRenderer, DEFAULT_TEXT_COLOR, wrappedQuestion.get(i), questionX, questionY + i * this.textRenderer.fontHeight);
        }

        // Answers
        RenderUtils.drawTextCenter(context, this.textRenderer, Text.literal("Answers").formatted(Formatting.UNDERLINE), fifth4, minY);
        for (int i = 0; i < Trivia.answers.size(); i++) {
            Rectangle rect = answerRects.get(i);
            int borderColor = ANSWER_COLORS[i % ANSWER_COLORS.length];
            context.fill(rect.x - 1, rect.y - 1, rect.x + rect.width + 1, rect.y, borderColor); // top border
            context.fill(rect.x - 1, rect.y + rect.height, rect.x + rect.width + 2, rect.y + rect.height + 2, borderColor); // bottom
            context.fill(rect.x - 1, rect.y, rect.x, rect.y + rect.height, borderColor); // left
            context.fill(rect.x + rect.width, rect.y-1, rect.x + rect.width + 2, rect.y + rect.height, borderColor); // right

            // Check if the mouse is hovering over this answer
            boolean hovered = rect.contains(mouseX, mouseY);
            int textColor = hovered ? TextColors.WHITE : DEFAULT_TEXT_COLOR;

            // Draw each line
            int lineY = rect.y + 2;
            for (OrderedText line : answers.get(i)) {
                RenderUtils.drawOrderedTextLeft(context, this.textRenderer, textColor, line, rect.x+1, lineY);
                lineY += this.textRenderer.fontHeight;
            }
        }

        // Entity in the middle
        context.fill(centerX-33, centerY-55, centerX+33, centerY+55, TextColors.BLACK);
        //? if <= 1.21.5 {
        drawEntity(context, startX, startY, mouseX, mouseY, centerX, centerY - 50, 40);
        //?} else {
        /*RenderUtils.drawTextLeftWrapLines(context, client.textRenderer, TextColors.DEFAULT_LIGHTER, Text.of("Bot display temporarily disabled\nin 1.21.6+ because mojang broke it :<"), centerX-28, centerY-46, 60, 5);
        *///?}
    }

    private void drawEntity(DrawContext context, int i, int j, int mouseX, int mouseY, int x, int y, int size) {
        if (client == null) return;
        if (client.world == null) return;
        if (client.player == null) return;
        List<Entity> allEntities = new ArrayList<>();
        List<Entity> matchingBotEntities = new ArrayList<>();
        for (DisplayEntity.ItemDisplayEntity entity : client.world.getEntitiesByClass(DisplayEntity.ItemDisplayEntity.class, client.player.getBoundingBox().expand(10), entity->true)) {
            if (MainClient.triviaBotPartUUIDs.contains(entity.getUuid())) {
                matchingBotEntities.add(entity);
            }
            allEntities.add(entity);
        }
        if (matchingBotEntities.isEmpty()) matchingBotEntities = allEntities;
        for (Entity entity : matchingBotEntities) {
            drawEntity(context, x-30, y-55, x+30, y+85, size, 0.0625F, mouseX, mouseY, entity);
        }
    }

    public static void drawEntity(DrawContext context, int x1, int y1, int x2, int y2, int size, float f, float mouseX, float mouseY, Entity entity) {
        context.enableScissor(x1, y1, x2, y2);
        Quaternionf quaternionf = (new Quaternionf()).rotateZ(3.1415927F);
        Quaternionf quaternionf2 = (new Quaternionf()).rotateX(0);
        quaternionf.mul(quaternionf2);
        float originalYaw = entity.getYaw();
        float originalPitch = entity.getPitch();
        entity.setYaw(180);
        entity.setPitch(0);
        Vector3f vector3f = new Vector3f(0.0F, entity.getHeight() / 2.0F + f, 0.0F);
        //? if <= 1.21.5 {
        drawEntity(context, (x1 + x2) / 2.0F, (y1 + y2) / 2.0F, size, vector3f, quaternionf, quaternionf2, entity);
        //?} else {
        /*drawEntity(context, x1, y1, x2, y2, size, vector3f, quaternionf, quaternionf2, entity);
        *///?}
        entity.setYaw(originalYaw);
        entity.setPitch(originalPitch);
        context.disableScissor();
    }

    //? if <= 1.21.5 {
    public static void drawEntity(DrawContext context, float x, float y, float size, Vector3f vector3f, Quaternionf quaternionf, @Nullable Quaternionf quaternionf2, Entity entity) {
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 50.0);
        context.getMatrices().scale(size, size, -size);
        context.getMatrices().translate(vector3f.x, vector3f.y, vector3f.z);
        context.getMatrices().multiply(quaternionf);
        //? if <= 1.21.4 {
        DiffuseLighting.method_34742();
         //?} else {
        /*DiffuseLighting.enableGuiShaderLighting();
        *///?}
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        if (quaternionf2 != null) {
            entityRenderDispatcher.setRotation(quaternionf2.conjugate(new Quaternionf()).rotateY(3.1415927F));
        }

        entityRenderDispatcher.setRenderShadows(false);
        //? if <= 1.21 {
        RenderSystem.runAsFancy(() -> {
            entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, context.getMatrices(), context.getVertexConsumers(), 15728880);
        });
         //?} else {
        /*context.draw((vertexConsumers) -> {
            entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 1.0F, context.getMatrices(), vertexConsumers, 15728880);
        });
        *///?}
        context.draw();
        entityRenderDispatcher.setRenderShadows(true);
        context.getMatrices().pop();
        DiffuseLighting.enableGuiDepthLighting();
    }
    //?} else {
    /*public static void drawEntity(DrawContext drawer, int x1, int y1, int x2, int y2, float scale, Vector3f translation, Quaternionf rotation, @Nullable Quaternionf overrideCameraAngle, Entity entity) {
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        EntityRenderer<? super Entity, ?> entityRenderer = entityRenderDispatcher.getRenderer(entity);
        EntityRenderState entityRenderState = entityRenderer.getAndUpdateRenderState(entity, 1.0F);
        entityRenderState.hitbox = null;
        drawer.addEntity(entityRenderState, scale, translation, rotation, overrideCameraAngle, x1, y1, x2, y2);
    }
    *///?}
}
