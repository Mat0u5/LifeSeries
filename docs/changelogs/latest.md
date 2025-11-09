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