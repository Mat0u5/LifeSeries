# Full changelog from version `1.4.3` to `*`.

----------

Added seeds and leaf litter to the common items in the Hunger wildcard.<br>
Random lives rolling can no longer say "1 lives" when that amount is allowed with the config.<br>
Players no longer sometimes keep zombie hearts.

----------

All toLowerCase and toUpperCase calls now have a Locale, to prevent issues in different languages.<br>
Added the 'tick_freeze_not_in_session' global config, which automatically freezes the game when the session is paused or ended or not started. Default value is false.<br>
Session timers now display even when the game is tick frozen.