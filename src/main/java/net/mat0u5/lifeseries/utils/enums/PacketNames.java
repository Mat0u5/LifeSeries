package net.mat0u5.lifeseries.utils.enums;

public enum PacketNames {
    PACKET(""),

    CURSE_SLIDING,
    LIMITED_LIFE_TIMER("limited_life_timer__"),
    SESSION_TIMER,

    MIMICRY_COOLDOWN,
    SUPERPOWER_COOLDOWN,
    FAKE_THUNDER,
    SNAIL_AIR,
    TOGGLE_TIMER,
    PREVENT_GLIDING,
    SNAIL_TEXTURES_INFO,
    TRIVIA_SNAIL_POS,
    TRIVIA_SNAIL_PART,
    SNAIL_POS,
    SNAIL_PART,
    TRIVIA_BOT_PART,
    SEASON_INFO,
    SELECT_SEASON,
    OPEN_CONFIG,
    SELECT_WILDCARDS,
    RESET_TRIVIA,
    JUMP,
    SESSION_STATUS,
    SNAIL_SKIN,
    MORPH,
    TIME_DILATION,
    PLAYER_DISGUISE, // Also a packet ID
    SHOW_VIGNETTE,
    PLAYER_INVISIBLE("player_invisible__"),
    TABLIST_SHOW_EXACT,
    CURRENT_SEASON,
    ACTIVE_WILDCARDS,
    PLAYER_MIN_MSPT,
    TRIPLE_JUMP,
    REQUEST_SNAIL_MODEL,
    SET_SEASON,
    REQUEST_CONFIG,
    SELECTED_WILDCARD,
    TRANSCRIPT,
    SUPERPOWER_KEY,
    HOLDING_JUMP,
    TRIVIA_ANSWER,
    SHOW_TOTEM,
    PAST_LIFE_CHOOSE_TWIST,
    TAB_LIVES_CUTOFF,
    FIX_SIZECHANGING_BUGS,
    SIZESHIFTING_CHANGE;

    public static PacketNames fromName(String name) {
        for (PacketNames packet : PacketNames.values()) {
            if (packet.getName().equalsIgnoreCase(name)) {
                return packet;
            }
        }
        PacketNames returnPacket = PACKET;
        returnPacket.setName(name); // This changes the name of the PACKET forever, which isn't really a problem. In fact, it kinda acts as a sort of cache.
        return returnPacket;
    }


    private String name;
    PacketNames(String name) {
        this.name = name;
    }

    PacketNames() {
        this.name = this.toString().toLowerCase();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
