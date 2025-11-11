# Full changelog from version `1.4.3` to `*`.

----------

Added seeds and leaf litter to the common items in the Hunger wildcard.<br>
Random lives rolling can no longer say "1 lives" when that amount is allowed with the config.<br>
Players no longer sometimes keep zombie hearts.

----------

All toLowerCase and toUpperCase calls now have a Locale, to prevent issues in different languages.<br>
Added the 'tick_freeze_not_in_session' global config, which automatically freezes the game when the session is paused or ended or not started. Default value is false.<br>
Session timers now display even when the game is tick frozen.

----------

You can no longer make one person the boogeyman twice by using the add command.<br>
Snails no longer crash the game when the player they are tracking logs off.<br>
Snails no longer sometimes fail to spawn for new players.<br>
Items from trivia bots can no longer be picked up by hoppers.

----------

Completely rewrote snail pathfinding, leading to massive improvements.<br>
Snails should no longer randomly stop moving on the edges of blocks.<br>
Snail mining and flying navigation no longer sometimes stops working.<br>
Reduced lag caused by snails by around 10x

----------

The Entity Culling mod is now compatible with the animal disguise superpower in 1.21.2+<br>
Fixed bugs regarding the boogeyman steal life config.

----------

Added the '/boogeyman reset <players>' command, which resets the Boogeyman fail/cure status. It does not automatically revert any punishments / rewards.<br>
Vanilla commands can now change the config. (For example changing the keep inv/show advancements / locator bar gamerule, or changing the worldborder size will change the config value)

----------

Added the 'wildcard_superpowers_zombies_health' Wild Life config. Default value is 8.<br>
Added the 'wildcard_hunger_non_edible_items' Wild Life config - It's an item list, so the same format as the item blacklist for example.

----------

Renamed '/superpower assignForRandomization' to '/superpower force'<br>
Added the '/soulmate force <player> <soulmate>' command, which forces two players to be rolled together when the soulmate randomization happens.<br>
Added the '/soulmate prevent <player> <soulmate>' command, which prevents two players from being rolled together when the soulmate randomization happens.<br>
Both of the commands don't keep that info once you close the server.