package net.mat0u5.lifeseries.features;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.gui.trivia.ConfirmQuizAnswerScreen;
import net.mat0u5.lifeseries.gui.trivia.QuizScreen;
import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.mat0u5.lifeseries.network.packets.TriviaQuestionPayload;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.versions.VersionControl;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

public class Trivia {
    public static String question = "";
    public static List<String> answers = new ArrayList<>();
    public static int difficulty = 0;
    public static int secondsToComplete = 0;
    public static long timestamp = 0;
    public static int ticksPassed = 0;
    public static void receiveTrivia(TriviaQuestionPayload payload) {
        question = payload.question();
        answers = payload.answers();
        difficulty = payload.difficulty();
        timestamp = payload.timestamp();
        secondsToComplete = payload.timeToComplete();
        if (VersionControl.isDevVersion()) Main.LOGGER.info(TextUtils.formatString("[PACKET_CLIENT] Received trivia question: {{}, {}, {}}", question, difficulty, answers));
        openGui();
    }

    public static void updateTicksPassed(int ticksPassed) {
        Trivia.ticksPassed = ticksPassed;
    }

    public static int getRemainingTicks() {
        return (secondsToComplete*20) - ticksPassed;
    }

    public static int getRemainingSeconds() {
        return getRemainingTicks() / 20;
    }

    public static boolean isDoingTrivia() {
        if (Trivia.secondsToComplete == 0) return false;
        int remaining = Trivia.getRemainingSeconds();
        if (remaining <= 0) return false;
        if (remaining > 1000000) return false;
        return true;
    }

    public static void openGui() {
        if (question.isEmpty() || answers.isEmpty()) return;
        MinecraftClient.getInstance().setScreen(new QuizScreen());
    }

    public static void closeGui() {
        if (MinecraftClient.getInstance().currentScreen == null) return;
        if (MinecraftClient.getInstance().currentScreen instanceof QuizScreen || MinecraftClient.getInstance().currentScreen instanceof ConfirmQuizAnswerScreen) {
            MinecraftClient.getInstance().currentScreen.close();
        }
    }

    public static void resetTrivia() {
        question = "";
        answers = new ArrayList<>();
        difficulty = 0;
        secondsToComplete = 0;
        timestamp = 0;
        ticksPassed = 0;
        closeGui();
    }

    public static void sendAnswer(int answer) {
        resetTrivia();
        NetworkHandlerClient.sendTriviaAnswer(answer);
    }
}
