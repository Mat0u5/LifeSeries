![Banner](images/banner.png)

[![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-green.svg)](https://github.com/Mat0u5/LifeSeries/graphs/commit-activity)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)
[![Discord](https://badgen.net/discord/online-members/QWJxfb4zQZ?icon=discord&label=Discord&list=what)](https://discord.gg/QWJxfb4zQZ)

[<img alt="modrinth" height="40" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/modrinth_vector.svg">](https://modrinth.com/mod/life-series)

[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/mat0u5)


# Life Series Mod
This mod is a one-to-one recreation of [Grian](https://www.youtube.com/c/Grian)'s Life Series in minecraft fabric, with as many QOL features as possible. 
That includes automated lives tracking, automated team colors, automated session timer, automatic Boogeyman selection and kill tracking, and much more.<br>
The mod is *server-side required* and optional (**but very much recommended**) on the client-side, as that adds further QOL features (such as GUIs for most of the features, so you don't have to use the commands as much).<br>
Join the [Discord](https://discord.gg/QWJxfb4zQZ) or make an issue if you want to submit any bugs or give suggestions.
## Features

This mod contains every single season of the Life Series, so that's Third Life, Last Life, Double Life, Limited Life, Secret Life, Wild Life and Past Life.<br>
It also contains april fools seasons, Real Life and Simple Life.

| Third Life | Last Life | Double Life | Limited Life | Secret Life | **Wild Life** | Past Life |
|------------|-----------|-------------|--------------|-------------|---------------|-----------|
| Completed  | Completed | Completed   | Completed    | Completed   | Completed     | Completed |

## Wild Life
This is the *only* full recreation of Wild Life that exists publicly, and it took a lot of work to make, so enjoy!<br>
To play Wild Life, the mod must be installed on the server **and on the client** - every player needs to have the mod installed in order to play.<br>
This is because Wild Life contains features which would be impossible to do without client-side modifications.<br>
**NOTE: Wild Life modifies core mechanics of the minecraft game, and using other complex mods or modded clients (Lunar Client, etc.), might cause some of the features not to work properly!!**<br>
*It is also worth mentioning that some features in Wild Life will most probably trigger any anti-cheats, so consider disabling any anti-cheats that you have installed on the server.*

-----
## Usage
### Selecting a series
After first starting a server with this mod installed, you will need to choose the series you want.<br>
You can do this with the `/lifeseries chooseSeries` command (or with the `/lifeseries setSeries <series>` command, if you don't have the mod installed client-side)<br>
Don't worry, you don't have to remember all of this, you will be given instructions in chat ;) <br>

### Starting a session
To start a session, you must first set its length using `/session timer set <time>`. The `<time>` field is a string, which includes the hours, minutes and seconds you want the session to last - example valid values: `30m`,`1h`, `1h45m`, `2h35m20s`,...<br>
After you set your session time, you can simply run `/session start` to start the session.<br>

### The Blacklists
Every season has certain blacklisted items to prevent players from getting to overpowered, just like Grian's series, where some for example banned enchantment tables and similar OP items.<br>
Since I am creating this mod mainly for the group of players I am playing with, the Blacklists are catered to how we feel a balanced series should look like.<br>
For Example most of the seasons have maces, end crystals, strength pots,... banned.<br>
Blacklisted items are automatically deleted from inventories, and cannot even be crafted.<br>
A similar feature to the item blacklist is enchantment clamping, which limits specific (mainly combat) enchantments to level 1.<br>
On series where bookshelves are banned, the enchantment table algorithm has been modified to be able to give all the enchantments (because in vanilla its impossible to get some without bookshelves), and its also possible to get higher levels on some enchants directly from the table without bookshelves (like efficiency, unbreaking, fortune,...)
All of this is configurable, which brings me to the next point, the **Config**.

### Config
- Using `/lifeseries config` while having the Life Series mod installed client-side, along with the Cloth Config mod will open a GUI for you to edit the config from in-game.<br>
- The config files are all located in the `config/lifeseries/*` folder. There are files for every series, where you can modify the gameplay for each one of them.<br>
- Reload the config using `/lifeseries reload` or simply the vanilla `/reload`
- Currently, you can modify these (check the config files themselves for more info):
  - Modify default number of lives, the blacklists, Clamped enchants, spawn egg drop rates, if players drop items when they lose their last life ** and much, much more**.
  - Change tasks in Secret Life (`config/lifeseries/secretlife/` folder) - there are three files, where you can modify / add / remove the easy, hard and red tasks.
  - Modify rewards for completing tasks in Secret Life (`config/lifeseries/secretlife/task_reward_loottable_<version>.json`)
  - Modify rewards for answering trivia correctly in Wild Life (`config/lifeseries/wildlife/trivia_reward_loottable_<version>.json`)
  - Choose snail names and skins, trivia timers, size shifting and much more in Wild Life.
  - I can't mention it all, or we'd be here all day long. You can check the config files yourself to find what is configurable :)

## Commands & Subcommands
Unless specified otherwise (using "*No permissions required.*"), all commands can only be used by opped players (permissionLevel >= 2)

<details open>
<summary>/lifeseries</summary>

This command **always** available
- `/lifeseries series` - Shows the selected series
- `/lifeseries config` - Opens the mod's config GUI (Life Series mod on client-side required).
- `/lifeseries version` - Shows the mod's version
- `/lifeseries credits` - Shows the mod's credits
- `/lifeseries chooseSeries` - Opens the series select GUI (Life Series mod on client-side required).
- `/lifeseries setSeries <series>` - Changes the Series, only works if no series has been selected
  - `/lifeseries setSeries <series> confirm` - Changes the Series, can override the current series
- `/lifeseries disable` - Disabled the entire life series mod.
- `/lifeseries enable` - Enables the entire life series mod if it has been disabled.
</details>

<details open>
<summary>/session</summary>

*This command is only available when you have selected a Series.*
- `/session start` - Starts the session, requires a timer to be set.
- `/session stop` - Stops the session.
- `/session pause` - Pauses / Unpauses the session.
- `/session timer`
  - `/session timer set <time>` - Sets the session time to `<time>`.
  - `/session timer add <time>` - Adds `<time>` to the session time.
  - `/session timer remove <time>` - Removes `<time>` from the session time.
  - `/session timer fastforward <time>` - Skips forward `<time>` when a session is active.
  - `/session timer remaining` - Shows the remaining session time. *No permissions required.*
  - `/session timer showDisplay` - Displays a permanent session timer in the actionbar of a player. *No permissions required.*
</details>



<details open>
<summary>/lives</summary>

*This command is only available when you have selected a Series.*
- `/lives` - Shows you the amount of lives you have. *No permissions required.*
- `/lives reload` - Reloads the teams of all players. This should never be necessary to run.
- `/lives add <player> [amount]` - Adds `[amount]` lives to `<player>`. If `[amount]` is not specified, it defaults to 1.
- `/lives remove <player> [amount]` - Removes `[amount]` lives from `<player>`. If `[amount]` is not specified, it defaults to 1.
- `/lives set <player> <amount>` - Sets `<player>`'s lives to `<amount>`.
- `/lives get <player>` - Shows you how many lives `<player>` has.
- `/lives reset <player>` - Resets `<player>`'s lives.
- `/lives resetAll` - Resets the lives of all online and offline players.

*In Limited Life, all the `<amount>`'s are replaced with time arguments. Examples of valid time arguments are: `16h, 8h, 5h2m1s, 3h30m, 1h1s, 5h2s, 10s`*
</details>


<details open>
<summary>/claimkill</summary>

*This command is only available when you have selected a Series.*
- `/claimkill <player>` - Used when you'd want to claim credit for a kill. For example you kill a player as the Boogeyman with a trap, so you don't get automatically cured. Or when you gain something for killing players in a series (like Limited Life). An admin must confirm this claim for you to be given the kill rewards. *No permissions required.*
- `/claimkill validate <killer> <victim>` - This is the command the admin uses to validate a claimed kill.
</details>


<details open>
<summary>/watcher</summary>

- `/watcher info` - Shows info on what the Watchers are.
- `/watcher list` - Shows a list of all the Watchers.
- `/watcher add <player>` - Adds `<player>` from the Watchers.
- `/watcher remove <player>` - Removes `<player>` to the Watchers.
</details>

<details open>
<summary>/subin</summary>

- `/subin list` - Shows a list of all subbed in players.
- `/subin add <player> <subinfor>` - Substitues `<player>` for `<subinfor>`.
- `/subin remove <player>` - Stops `<player>`'s substitution.
</details>

<details open>
<summary>/wildcard</summary>

*This command is only available when playing Wild Life.*
- `/wildcard list` - Shows a list of all possible wildcards.
- `/wildcard listActive` - Shows a list of all **active** wildcards.
- `/wildcard activate <wildcard>` - Activates Wildcard with the name `<wildcard>`
- `/wildcard deactivate <wildcard>` - Deactivates Wildcard with the name `<wildcard>`
- `/wildcard choose` - Lets you choose the wildcard that will be used in the session via a GUI.
</details>

<details open>
<summary>/superpower</summary>

*This command is only available when playing Wild Life.*
- `/superpower set <player> <power>` - Sets `<player>`'s superpower to `<power>`.
- `/superpower get <player>` - Shows you `<player>`'s superpower.
- `/superpower reset <player>` - Resets `<player>`'s superpowers.
- `/superpower setRandom` - Assigns a random superpower to all online players.
- `/superpower resetCooldown` - Resets your superpower cooldown.
- `/superpower assignForRandomization <player> <power>` - Forces `<player>`'s superpower to be `<power>` when the next superpower randomization happens.
</details>

<details open>
<summary>/boogeyman</summary>

*This command is only available when the boogeyman has been enabled in the config.*
- `/boogeyman list` - Shows you the list of all the Boogeymen.
- `/boogeyman count` - Shows you the **amount** of chosen Boogeymen (useful for debugging and admins which are also players).
- `/boogeyman clear` - Removes all Boogeymen.
- `/boogeyman add <player>` - Makes `<player>` the Boogeyman if they aren't one already.
- `/boogeyman remove <player>` - Makes `<player>` not be the Boogeyman if they were one.
- `/boogeyman cure <player>` - Cures `<player>` of the Boogeyman curse if they were the Boogeyman.
- `/boogeyman fail <player>` - Makes `<player>` fail the Boogeyman curse if they were the Boogeyman.
- `/boogeyman chooseRandom` - Chooses the Boogeyman / Boogeymen from the list of online players.
- `/boogeyman selfFail` - Makes **you** instantly fail if you are the Boogeyman. *No permissions required.*
</details>

<details open>
<summary>Secret Society</summary>

*This command is only available when the Secret Society has been enabled in the config.*
- `/society begin` - Starts the Society with a random word (Can be changed in the config).
- `/society begin <secret_word>` - Starts the Society with a specific word you type.
- `/society end` - Ends the Secret Society.
- `/society members add <player>` - Adds `<player>` to the Society.
- `/society members remove <player>` - Removes `<player>` from the Society.
- `/society members list` - Shows you the current members of the Society.
<br><br>
- `/society success` - Society members can use this if they succeed in their task. *No permissions required.*
- `/society fail` - Society members can use this if they fail in their task. *No permissions required.*
- `/initiate` - Initiates a member of the Society. *No permissions required.*
</details>

<details open>
<summary>/health</summary>

*This command is only available when playing Secret Life.*
- `/health` - Shows you the amount of health you have (pr useless huh xD). *No permissions required.*
- `/health sync` - Synchronizes the health and max health of all players. This should never be necessary to run.
- `/health add <player> [amount]` - Adds `[amount]` health to `<player>`. If `[amount]` is not specified, it defaults to 1.
- `/health remove <player> [amount]` - Removes `[amount]` health from `<player>`. If `[amount]` is not specified, it defaults to 1.
- `/health set <player> <amount>` - Sets `<player>`'s health to `<amount>`.
- `/health get <player>` - Shows you how much health `<player>` has.
- `/health reset <player>` - Resets `<player>`'s health to the default.
</details>

<details open>
<summary>Other series specific commands</summary>

- `/givelife <player>` - Gives one of your lives to `<player>`. *This command is only available when enabled in the config.* *No permissions required.*

- `/lives rollLives [<player>]` - Rolls 2-6 lives for everyone or for the `[<player>]`, if specified. *This command is only available when playing Last Life.*

- `/gift <player>` - Gives one free heart to `<player>`. *This command is only available when playing Secret Life.* *No permissions required.*

- `/hunger randomizeFood` - Randomizes food in the Hunger wildcard. *This command is only available when playing Wild Life.*

- `/task` - *These commands are only available when playing Secret Life.*
  - `/task succeed <player>` - Succeeds `<player>`'s task.
  - `/task fail <player>` - Fails `<player>`'s task.
  - `/task reroll <player>` - Re-rolls `<player>`'s task.
  - `/task assignRandom <player/s>` - Assigns a random task to `<player/s>`.
  - `/task clearTask <player>` - Clears the task book from `<player>`.
  - `/task set <player> <difficulty> <task>` - Sets the `<player>`'s task to be `<task>` on the next randomization.
  - `/task get <player>` - Shows you if `<player>` has a task, and the task itself you want to.

- `/soulmate` - *These commands are only available when playing Double Life.*
  - `/soulmate get <player>` - Shows you who `<player>`'s soulmate is.
  - `/soulmate set <player1> <player2>` - Makes `<player1>` be `<player2>`'s soulmate.
  - `/soulmate list` - Shows you all the soulmate pairs.
  - `/soulmate reset <player>` - Resets `<player>`'s soulmate.
  - `/soulmate resetAll` - Resets all soulmates.
  - `/soulmate rollRandom` - Rolls random soulmates from the list of online players.
  - 
- `/snail` - *These commands are only available when playing Wild Life.*
  - `/snail names get <player>` - Shows you the name of `<player>`'s snail. *No permissions required.*
  - `/snail names set <player> <name>` - Sets the name of `<player>`'s snail to `<name>`.
  - `/snail names reset <player>` - Resets the name of `<player>`'s snail back to `<player>'s Snail`.
  - `/snail names request <name>` - Used when non-admin players want to request their snail name to be set to `<name>`. *No permissions required.*
  - `/snail textures list` - Shows a list of uploaded snail textures.
  - `/snail textures info` - Shows you how to create and upload snail textures.
  - `/snail textures reload` - Reloads the snail textures from the files.
</details>
