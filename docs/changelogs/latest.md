# Full changelog from version `1.4.1` to `*`.

----------

Bug Fixes

- Minimal armor pack changes:
  - Added support for 1.21.9 (copper armor)
  - Fixed armor trims not working properly on minimal armor in 1.21.2+.
  - Added the two missing armor trims (flow and bolt) for minimal armor.

----------

Removed all dependencies from the mod (Polymer and BIL, which were used for rendering custom entities - snail and trivia bot), which will allow me to control their rendering more, and it will speed up development time, allow me to update to newer versions (and snapshots!) faster.<br>
Custom rendering for the snail and trivia bot - this should fix ALL bugs with snails / bots being invisible sometimes.<br>
The trivia bot quiz screen now shows the trivia entity in 1.21.6+.<br>
Adding snail skins no longer requires a full reload (no more reloading screen on the client yipiiee).

----------

Sub-ins now have the snail skin of the player they are subbing in.<br>
When a player stops subbing in for another player, their lives now return to what they were before.<br>
You can no longer sit on snails and trivia bots with the superpunch power.<br>
Bug fixes.