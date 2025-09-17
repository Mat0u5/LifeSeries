package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.mat0u5.lifeseries.Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class TriviaQuestionManager {
    private static final String DEFAULT_EASY_TRIVIA = "[\n" +
            "    {\"question\": \"Who was the first player to go yellow in Secret Life?\", \"answers\": [\"Jimmy\", \"Martyn\", \"Lizzie\"], \"correct_answer_index\": 1},\n" +
            "    {\"question\": \"Which players were part of a team called the Mounders in Secret Life?\", \"answers\": [\"Bdubs, Pearl, and Mumbo\", \"Joel, Bdubs, Pearl, and Mumbo\", \"Lizzie, Bdubs, Pearl, and Mumbo\"], \"correct_answer_index\": 1},\n" +
            "    {\"question\": \"What disc was playing while Grian, Martyn, and Scar were watching the Relation-Ship burn down?\", \"answers\": [\"Mellohi\", \"Otherside\", \"Pigstep\"], \"correct_answer_index\": 1},\n" +
            "    {\"question\": \"Which one of these is NOT a paper Scar passed around in Third Life?\", \"answers\": [\"No Kill Pass\", \"Free Sand Pass\", \"Free Kill Pass\"], \"correct_answer_index\": 2},\n" +
            "    {\"question\": \"Which of these life colors was NOT allowed to kill during Limited Life without Boogeyman?\", \"answers\": [\"Green\", \"Yellow\", \"Red\"], \"correct_answer_index\": 0},\n" +
            "    {\"question\": \"Who won Secret Life?\", \"answers\": [\"Scar\", \"Pearl\", \"Gem\"], \"correct_answer_index\": 0},\n" +
            "    {\"question\": \"At the end of Secret Life, Gem had to fight agaisnt multiple players alone. The fight was a...\", \"answers\": [\"2v1\", \"3v1\", \"4v1\"], \"correct_answer_index\": 0}\n" +
            "]";

    private static final String DEFAULT_NORMAL_TRIVIA = "[\n" +
            "    {\"question\": \"Who was the first person to be eliminated in Real Life?\", \"answers\": [\"Jimmy\", \"Joel\", \"Scar\", \"Grian\"], \"correct_answer_index\": 3},\n" +
            "    {\"question\": \"Which season(s) did Ren NOT participate in?\", \"answers\": [\"Limited, Secret, and Real Life\", \"Secret Life\", \"Limited and Secret Life\", \"Limited Life\"], \"correct_answer_index\": 3},\n" +
            "    {\"question\": \"Who was the last green in Third Life?\", \"answers\": [\"Martyn\", \"Scott\", \"Grian\", \"BigB\"], \"correct_answer_index\": 3},\n" +
            "    {\"question\": \"Who said this quote: \\\"He hasn't looked at my eyes once, he is looking at my feet!\\\"\", \"answers\": [\"Impulse\", \"Bdubs\", \"Etho\", \"Tango\"], \"correct_answer_index\": 2},\n" +
            "    {\"question\": \"Who said this quote: \\\"I'll never forget your hole BigB, that thing is huge and dangerous.\\\"\", \"answers\": [\"Scar\", \"Jimmy\", \"Martyn\", \"Grian\"], \"correct_answer_index\": 0},\n" +
            "    {\"question\": \"After a certain player's final death in Third Life, a hoard of zombies showed up at Dogwarts. Who was the player?\", \"answers\": [\"Joel\", \"Cleo\", \"Jimmy\", \"Skizz\"], \"correct_answer_index\": 1},\n" +
            "    {\"question\": \"Double Life was the season with the fewest amount of players. How many players participated?\", \"answers\": [\"10 players\", \"12 players\", \"14 players\", \"16 players\"], \"correct_answer_index\": 2},\n" +
            "    {\"question\": \"Who is the only Life Series winner to still be alive after ending their winning season?\", \"answers\": [\"Martyn\", \"Scott\", \"Cleo\", \"Scar\"], \"correct_answer_index\": 3},\n" +
            "    {\"question\": \"Who put a cake in Jimmy's house in Third Life?\", \"answers\": [\"Grian\", \"Martyn\", \"Scott\", \"Joel\"], \"correct_answer_index\": 2},\n" +
            "    {\"question\": \"How did Martyn kill Cleo in Double Life?\", \"answers\": [\"Divorce papers\", \"Rancid vibes\", \"Trust exercise\", \"Bad math\"], \"correct_answer_index\": 3},\n" +
            "    {\"question\": \"Who tried to base on top of a Pillager Outpost?\", \"answers\": [\"Cleo, Scott, Pearl, and Martyn\", \"Impulse, Bdubs, Ren, and Bigb\", \"Tango, Jimmy, Grian, and Scar\", \"Etho, Joel, Grian, and Scar\"], \"correct_answer_index\": 3},\n" +
            "    {\"question\": \"What was Plan Bubblevate?\", \"answers\": [\"Scott's plan to build a bubblevator elevator to the tower above the Scottage\", \"Joel's plan to kill Scar by building a bubblevator below Magical Mountain\", \"An emergency bubblevator exit from Ren's Shadow Tower into Cleo and BigB's castle\", \"A trap laid by Ren to kill red name players by green names under bubblevators\"], \"correct_answer_index\": 3},\n" +
            "    {\"question\": \"Who did Mumbo shove off the Ghast farm in Last Life session 7?\", \"answers\": [\"Grian\", \"Impulse\", \"Joel\", \"Jimmy\"], \"correct_answer_index\": 1},\n" +
            "    {\"question\": \"Who made the Team BEST shields?\", \"answers\": [\"Bdubs\", \"Tango\", \"Etho\", \"Skizz\"], \"correct_answer_index\": 3},\n" +
            "    {\"question\": \"Which soulmate duo in Double Life made their goal creating distrust between soulmates?\", \"answers\": [\"Martyn and Cleo\", \"Ren and BigB\", \"Etho and Joel\", \"Impulse and Bdubs\"], \"correct_answer_index\": 3},\n" +
            "    {\"question\": \"Who subbed in for Pearl during Limited Life?\", \"answers\": [\"Shelby\", \"Lizzie\", \"Gem\", \"False\"], \"correct_answer_index\": 1},\n" +
            "    {\"question\": \"Which player was eliminated from Last Life by accidentally blowing himself up?\", \"answers\": [\"Joel\", \"Skizz\", \"Mumbo\", \"Tango\"], \"correct_answer_index\": 3},\n" +
            "    {\"question\": \"Which two red names built a massive lava cast over the middle of the Last Life map?\", \"answers\": [\"Cleo and Bdubs\", \"Mumbo and Jimmy\", \"Lizzie and Joel\", \"Grian and Joel\"], \"correct_answer_index\": 2},\n" +
            "    {\"question\": \"Who named \\\"The Pufferish of Peace\\\" in Third Life?\", \"answers\": [\"Martyn\", \"Scott\", \"Jimmy\", \"Scar\"], \"correct_answer_index\": 2},\n" +
            "    {\"question\": \"Who was Grian \\\"soulbound\\\" to in session 3 of Secret Life?\", \"answers\": [\"Scar\", \"Martyn\", \"Etho\", \"Joel\"], \"correct_answer_index\": 3},\n" +
            "    {\"question\": \"What animals did Scott and Pearl keep as pets in Last Life?\", \"answers\": [\"Axolotls\", \"Wolves\", \"Sheep\", \"Cows\"], \"correct_answer_index\": 0}\n" +
            "]";

    private static final String DEFAULT_HARD_TRIVIA = "[\n" +
            "    {\"question\": \"What was the name of Skizz and Tango's first base in Last Life?\", \"answers\": [\"The Stone Maze\", \"The Roctopus\", \"The Rock Labrynth\", \"The Stonetopus\", \"The Rocksnake\"], \"correct_answer_index\": 1},\n" +
            "    {\"question\": \"Which player managed to gain more than 24 hours on their clock during Limited Life?\", \"answers\": [\"Scott\", \"Joel\", \"Cleo\", \"Grian\", \"Bdubs\"], \"correct_answer_index\": 0},\n" +
            "    {\"question\": \"Which player came up with the idea to torture their soulmate with powdered snow?\", \"answers\": [\"Scott\", \"Scar\", \"Martyn\", \"Pearl\", \"Cleo\"], \"correct_answer_index\": 1},\n" +
            "    {\"question\": \"How many players did Skizz eliminate in Third Life?\", \"answers\": [\"0\", \"1\", \"2\", \"3\", \"All of them\"], \"correct_answer_index\": 2},\n" +
            "    {\"question\": \"Who said this quote? \\\"[...] the blood is dripping into me eyes! I can't see, I've been blinded by the violence [...]\\\"\", \"answers\": [\"Skizz\", \"Scar\", \"Bdubs\", \"Ren\", \"Martyn\"], \"correct_answer_index\": 3},\n" +
            "    {\"question\": \"Who was this Double Life quote about? \\\"There is something wicked within you.\\\"\", \"answers\": [\"Cleo\", \"Ren\", \"Pearl\", \"Martyn\", \"Joel\"], \"correct_answer_index\": 2},\n" +
            "    {\"question\": \"In Secret Life, Gem, Mumbo, and Impulse were terrorized by which invisible player?\", \"answers\": [\"Lizzie\", \"Etho\", \"Tango\", \"Martyn\", \"Jimmy\"], \"correct_answer_index\": 3},\n" +
            "    {\"question\": \"Who had the fewest kills in Limited Life?\", \"answers\": [\"Bdubs\", \"Skizz\", \"Tango\", \"Jimmy\", \"BigB\"], \"correct_answer_index\": 2},\n" +
            "    {\"question\": \"Who said this quote: \\\"You gain an ally, I gain a foot, pretty good Monday for me.\\\"\", \"answers\": [\"Scar\", \"Ren\", \"Martyn\", \"Etho\", \"Bdubs\"], \"correct_answer_index\": 2},\n" +
            "    {\"question\": \"Who said this quote: \\\"I wanna hear Mumbo bark.\\\"\", \"answers\": [\"Gem\", \"Jimmy\", \"Scar\", \"Martyn\", \"Skizz\"], \"correct_answer_index\": 0},\n" +
            "    {\"question\": \"Who was the second Boogeyman in the first session of Limited Life?\", \"answers\": [\"Scott\", \"Skizz\", \"Martyn\", \"Scar\", \"Bdubs\"], \"correct_answer_index\": 4},\n" +
            "    {\"question\": \"Who was the Boogeyman in the third session of Limited Life?\", \"answers\": [\"Etho\", \"Grian\", \"Bdubs\", \"Impulse\", \"Pearl\"], \"correct_answer_index\": 3},\n" +
            "    {\"question\": \"Which player had the fewest deaths during Limited Life?\", \"answers\": [\"Bdubs\", \"Tango\", \"BigB\", \"Pearl\", \"Skizz\"], \"correct_answer_index\": 2},\n" +
            "    {\"question\": \"Which of these players had the same amount of deaths in Limited Life as Jimmy?\", \"answers\": [\"Skizz\", \"Martyn\", \"Tango\", \"Joel\", \"Scar\"], \"correct_answer_index\": 1},\n" +
            "    {\"question\": \"Who were the Blue Sword Boys?\", \"answers\": [\"Tango, Skizz, and Impulse\", \"Martyn, Ren, and Skizz\", \"BigB, Grian, and Martyn\", \"Martyn, Jimmy, and Scott\", \"Martyn, BigB, and Ren\"], \"correct_answer_index\": 2},\n" +
            "    {\"question\": \"Which was the only season where Scott did NOT place within the top 5?\", \"answers\": [\"Third Life\", \"Last Life\", \"Double Life\", \"Limited Life\", \"Secret Life\"], \"correct_answer_index\": 0},\n" +
            "    {\"question\": \"What forbidden item did Pearl find in the first session of Double Life?\", \"answers\": [\"Potion of Health\", \"Potion of Regeneration\", \"Golden apple\", \"Totem of Undying\", \"Potion of Strength\"], \"correct_answer_index\": 2},\n" +
            "    {\"question\": \"Which two players have not changed their skin for / because of the Life Series? (Not including swapping from a themed skin on another server to a \\\"default\\\" skin)\", \"answers\": [\"Tango and Mumbo\", \"Etho and Tango\", \"Mumbo and Etho\", \"Skizz and Etho\", \"Grian and Mumbo\"], \"correct_answer_index\": 0},\n" +
            "    {\"question\": \"Which phrase was NOT on the sign protecting Etho's dark oak tree in Last Life?\", \"answers\": [\"Do Not Touch\", \"Do Not Steal\", \"Do Not Sell\", \"Do Not Burn\", \"Do Not Scar\"], \"correct_answer_index\": 1},\n" +
            "    {\"question\": \"Who said this quote: \\\"The Red King dies tonight, fellas!\\\"\", \"answers\": [\"Bdub\", \"Joel\", \"Cleo\", \"Grian\", \"Scar\"], \"correct_answer_index\": 1},\n" +
            "    {\"question\": \"Who said this quote: \\\"Call the dogs of war!\\\"\", \"answers\": [\"Joel\", \"Lizzie\", \"Pearl\", \"Scar\", \"Grian\"], \"correct_answer_index\": 2},\n" +
            "    {\"question\": \"Which is the only season that Grian did NOT outlive his closest allies?\", \"answers\": [\"Last Life\", \"Double Life\", \"Limited Life\", \"Secret Life\", \"Real Life\"], \"correct_answer_index\": 4},\n" +
            "    {\"question\": \"Which of these players had at least ONE kill in Secret Life?\", \"answers\": [\"Lizzie\", \"Tango\", \"Skizz\", \"Impulse\", \"Cleo\"], \"correct_answer_index\": 2},\n" +
            "    {\"question\": \"Which two players were tied for most deaths in Limited Life?\", \"answers\": [\"Joel and Scott\", \"Skizz and Pearl\", \"Martyn and Cleo\", \"Jimmy and Etho\", \"Grian and Impulse\"], \"correct_answer_index\": 0},\n" +
            "    {\"question\": \"Which two players fought for Lizzie's heart in the first episode of Secret Life?\", \"answers\": [\"Tango and Cleo\", \"Gem and Skizz\", \"Pearl and Joel\", \"Scar and Jimmy\", \"Etho and Grian\"], \"correct_answer_index\": 2},\n" +
            "    {\"question\": \"Why was the mansion empty in the first episode of Limited Life?\", \"answers\": [\"The mansion wasn't empty\", \"They disabled all the aggressive mobs\", \"The mobs were bugging\", \"They killed all the Pillagers\", \"The game mode was set to Easy\"], \"correct_answer_index\": 4},\n" +
            "    {\"question\": \"In Last Life, Etho gave away papers falsely framing someone as the Boogeyman. Who did he frame?\", \"answers\": [\"Impulse\", \"Martyn\", \"Scar\", \"Pearl\", \"BigB\"], \"correct_answer_index\": 0},\n" +
            "    {\"question\": \"Which woodland mansion mob killed Joel and Grian in Limited Life?\", \"answers\": [\"Vex\", \"Evokers\", \"Illager\", \"Vindicators\", \"They didn't die in the mansion\"], \"correct_answer_index\": 0},\n" +
            "    {\"question\": \"Which player dropped from 4 lives to 1 in a single session of Last Life?\", \"answers\": [\"Scar\", \"Joel\", \"Pearl\", \"Tango\", \"Jimmy\"], \"correct_answer_index\": 1},\n" +
            "    {\"question\": \"Other the Clockers, what Limited Life team was Bdubs involved with?\", \"answers\": [\"Mean Gills\", \"Bad Boys\", \"Team TIES\", \"Nosey Neighbours\", \"He was loyal to the Clockers\"], \"correct_answer_index\": 2}\n" +
            "]";

    private File file;
    private File folder;

    public TriviaQuestionManager(String folder, String file) {
        this.file = new File(folder + "/" + file);
        this.folder = new File(folder);
        if (!this.folder.exists()) {
            if (!this.folder.mkdirs()) {
                Main.LOGGER.error("Failed to create folder {}", this.folder);
                return;
            }
        }
        if (!this.file.exists()) {
            try {
                if (!this.file.createNewFile()) {
                    Main.LOGGER.error("Failed to create file {}", this.file);
                    return;
                }
                if (file.startsWith("easy-")) {
                    setFileContent(DEFAULT_EASY_TRIVIA);
                } else if (file.startsWith("normal-")) {
                    setFileContent(DEFAULT_NORMAL_TRIVIA);
                } else if (file.startsWith("hard-")) {
                    setFileContent(DEFAULT_HARD_TRIVIA);
                }
            } catch (IOException ex) {
                Main.LOGGER.error(ex.getMessage());
            }
        }
    }

    private void setFileContent(String content) {
        try (FileWriter myWriter = new FileWriter(file, false)) {
            myWriter.write(content);
        } catch (IOException e) {
            Main.LOGGER.error(e.getMessage());
        }
    }

    public List<TriviaQuestion> getTriviaQuestions() throws IOException {
        String content = new String(Files.readAllBytes(file.toPath()));
        Gson gson = new Gson();
        return gson.fromJson(content, new TypeToken<List<TriviaQuestion>>() {}.getType());
    }
}