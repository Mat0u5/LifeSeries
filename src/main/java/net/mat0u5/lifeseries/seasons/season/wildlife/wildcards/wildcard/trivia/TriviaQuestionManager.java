package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.config.StringListConfig;
import net.mat0u5.matlib.utils.resource.ResourceHandler;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class TriviaQuestionManager {
    private File file;
    private File folder;
    public TriviaQuestionManager(String folder, String file) {
        this.file = new File(folder + "/" + file);
        this.folder = new File(folder);
        if (!this.folder.exists()) {
            if (!this.folder.mkdirs()) {
                LifeSeries.LOGGER.error("Failed to create folder {}", this.folder);
                return;
            }
        }
        if (!this.file.exists()) {
            ResourceHandler handler = new ResourceHandler();
            if (folder.contains("finallife")) {
                handler.copyBundledSingleFile("/files/trivia/finallife-trivia.json", this.file.toPath());
            }
            if (folder.contains("nicelife")) {
                handler.copyBundledSingleFile("/files/trivia/nicelife-trivia.json", this.file.toPath());
            }
            if (file.startsWith("easy-")) {
                handler.copyBundledSingleFile("/files/trivia/easy-trivia.json", this.file.toPath());
            }
            else if (file.startsWith("normal-")) {
                handler.copyBundledSingleFile("/files/trivia/normal-trivia.json", this.file.toPath());
            }
            else if (file.startsWith("hard-")) {
                handler.copyBundledSingleFile("/files/trivia/hard-trivia.json", this.file.toPath());
            }
        }
    }

    public static void createDefaultFiles() {
        new TriviaQuestionManager("./config/lifeseries/wildlife","easy-trivia.json");
        new TriviaQuestionManager("./config/lifeseries/wildlife","normal-trivia.json");
        new TriviaQuestionManager("./config/lifeseries/wildlife","hard-trivia.json");
        new TriviaQuestionManager("./config/lifeseries/nicelife","trivia.json");
        new TriviaQuestionManager("./config/lifeseries/finallife","trivia.json");
    }

    public void setFileContent(String content) {
        FileWriter myWriter;
        try {
            myWriter = new FileWriter(file, false);
            myWriter.write(content);
            myWriter.close();
        } catch (IOException e) {
            LifeSeries.LOGGER.error(e.getMessage());
        }
    }

    public List<TriviaQuestion> getTriviaQuestions() throws IOException {
        String content = new String(Files.readAllBytes(file.toPath()));
        Gson gson = new Gson();
        return gson.fromJson(content, new TypeToken<List<TriviaQuestion>>() {}.getType());
    }

    public List<TriviaQuestion> tryGetTriviaQuestions() {
        try {
            return getTriviaQuestions();
        }catch(Exception e) {}
        return new ArrayList<>();
    }

    // Fraction of the questions to keep as used when resetting happens.
    public static double RECENT_QUESTION_PERCENTAGE = 0.5;

    public TriviaQuestion getRandomQuestion(StringListConfig usedQuestionsConfig, Random rnd) throws IOException {
        return getRandomQuestion(usedQuestionsConfig, rnd, null);
    }

    public TriviaQuestion getRandomQuestion(StringListConfig usedQuestionsConfig, Random rnd, @Nullable String playerName) throws IOException {
        List<String> usedQuestions = usedQuestionsConfig == null ? new ArrayList<>() : usedQuestionsConfig.load();
        TriviaQuestion result = getRandomQuestion(usedQuestions, rnd, playerName);
        if (usedQuestionsConfig != null) usedQuestionsConfig.save(usedQuestions);
        return result;
    }

    public TriviaQuestion getRandomQuestion(List<String> usedQuestions, Random rnd) throws IOException {
        return getRandomQuestion(usedQuestions, rnd, null);
    }

    public TriviaQuestion getRandomQuestion(List<String> usedQuestions, Random rnd, @Nullable String playerName) throws IOException {
        List<TriviaQuestion> allQuestions = getTriviaQuestions();
        if (allQuestions == null || allQuestions.isEmpty()) return TriviaQuestion.getDefault();
        List<TriviaQuestion> pool = getQuestionPool(allQuestions, playerName);

        List<TriviaQuestion> unusedQuestions = getUnusedQuestions(pool, usedQuestions);
        if (unusedQuestions.isEmpty()) {
            resetUsedQuestions(usedQuestions, pool, RECENT_QUESTION_PERCENTAGE);
            unusedQuestions = getUnusedQuestions(pool, usedQuestions);
        }
        if (unusedQuestions.isEmpty()) {
            removeFromUsed(usedQuestions, pool);
            unusedQuestions = pool;
        }

        TriviaQuestion result = unusedQuestions.get(rnd.nextInt(unusedQuestions.size()));
        usedQuestions.add(result.getQuestion());
        return result;
    }

    private static List<TriviaQuestion> getQuestionPool(List<TriviaQuestion> allQuestions, @Nullable String playerName) {
        List<TriviaQuestion> playerQuestions = new ArrayList<>();
        List<TriviaQuestion> generalQuestions = new ArrayList<>();
        for (TriviaQuestion trivia : allQuestions) {
            if (!trivia.hasPlayer()) generalQuestions.add(trivia);
            else if (trivia.isForPlayer(playerName)) playerQuestions.add(trivia);
        }
        if (!playerQuestions.isEmpty()) return playerQuestions;
        if (!generalQuestions.isEmpty()) return generalQuestions;
        return allQuestions;
    }

    private static void removeFromUsed(List<String> usedQuestions, List<TriviaQuestion> pool) {
        Set<String> poolQuestions = new HashSet<>();
        for (TriviaQuestion trivia : pool) {
            poolQuestions.add(trivia.getQuestion());
        }
        usedQuestions.removeIf(poolQuestions::contains);
    }

    private static List<TriviaQuestion> getUnusedQuestions(List<TriviaQuestion> allQuestions, List<String> usedQuestions) {
        List<TriviaQuestion> unusedQuestions = new ArrayList<>();
        for (TriviaQuestion trivia : allQuestions) {
            if (usedQuestions.contains(trivia.getQuestion())) continue;
            unusedQuestions.add(trivia);
        }
        return unusedQuestions;
    }

    private static void resetUsedQuestions(List<String> usedQuestions, List<TriviaQuestion> allQuestions, double keepPercentage) {
        Set<String> pool = new LinkedHashSet<>();
        for (TriviaQuestion trivia : allQuestions) {
            pool.add(trivia.getQuestion());
        }

        int keepLast = (int) Math.floor(pool.size() * keepPercentage);

        List<String> usedFromPool = new ArrayList<>();
        for (String question : usedQuestions) {
            if (pool.contains(question) && !usedFromPool.contains(question)) {
                usedFromPool.add(question);
            }
        }

        int keepFrom = Math.max(0, usedFromPool.size() - keepLast);
        Set<String> toKeep = new HashSet<>(usedFromPool.subList(keepFrom, usedFromPool.size()));

        // Only reset questions from this pool, the other pools keep their used questions
        usedQuestions.removeIf(question -> pool.contains(question) && !toKeep.contains(question));
    }
}