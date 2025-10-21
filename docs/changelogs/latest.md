# Full changelog from version `1.4.2` to `*`.

----------

You can no longer get new tasks when the session is finished when having red tasks or when the constant tasks config is turned on.<br>
You can no longer self fail as the boogeyman if you've already failed or been cured.<br>
Other bug fixes.

----------

Added the 'boogeyman_team_notice' global config, which shares who the other boogeymen are if you're a boogeyman.<br>
Added the 'boogeyman_kills_needed' global config, which controls how many kills you need to cure yourself as the boogeyman.<br>
Added the 'lives_system_disabled' global config, which fully disables the lives system, allowing for a custom implementation. Note that this disables literally everything. The lives command, default lives, dying removing a life all do not work for example.
