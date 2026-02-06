package net.mat0u5.lifeseries.utils.enums;

import java.util.Locale;

public enum PacketNames {
    PACKET("")

    ,CURSE_SLIDING
    ,LIMITED_LIFE_TIMER("limited_life_timer__")
    ,SESSION_TIMER

    ,MIMICRY_COOLDOWN
    ,SUPERPOWER_COOLDOWN
    ,FAKE_THUNDER
    ,SNAIL_AIR
    ,TOGGLE_TIMER
    ,PREVENT_GLIDING
    ,SNAIL_TEXTURES_INFO
    ,SEASON_INFO
    ,SELECT_SEASON
    ,CLEAR_CONFIG
    ,OPEN_CONFIG
    ,SELECT_WILDCARDS
    ,RESET_TRIVIA
    ,JUMP
    ,SESSION_STATUS
    ,MORPH
    ,TIME_DILATION
    ,PLAYER_DISGUISE // Also a packet ID
    ,SHOW_VIGNETTE
    ,PLAYER_INVISIBLE("player_invisible__")
    ,TABLIST_SHOW_EXACT
    ,CURRENT_SEASON
    ,ACTIVE_WILDCARDS
    ,PLAYER_MIN_MSPT
    ,TRIPLE_JUMP
    ,SET_SEASON
    ,SELECTED_WILDCARD
    ,TRANSCRIPT
    ,SUPERPOWER_KEY
    ,HOLDING_JUMP
    ,TRIVIA_ANSWER
    ,SHOW_TOTEM
    ,PAST_LIFE_CHOOSE_TWIST
    ,TAB_LIVES_CUTOFF
    ,FIX_SIZECHANGING_BUGS
    ,SIZESHIFTING_CHANGE
    ,ANIMAL_DISGUISE_ARMOR
    ,ANIMAL_DISGUISE_HANDS
    ,SIDETITLE
    ,TRIVIA_TIMER
    ,HUNGER_NON_EDIBLE
    ,SET_LIVES
    ,SET_TEAM
    ,SNOWY_NETHER
    ,EMPTY_SCREEN
    ,HIDE_SLEEP_DARKNESS
    ,VOTING_TIME
    ,SUBMIT_VOTE
    ,SKYCOLOR
    ,FOGCOLOR
    ,CLOUDCOLOR
    ,TRIVIA_ALL_WRONG
    ,MIC_MUTED
    ,ADMIN_INFO
    ,STOP_TRIVIA_SOUNDS
    ,REMOVE_SLEEP_SCREENS
    ,CONFIG_SECRET_TASK
    ,CONFIG_TRIVIA
    ;

    public static PacketNames fromName(String name) {
        for (PacketNames packet : PacketNames.values()) {
            if (packet.getName().equalsIgnoreCase(name)) {
                return packet;
            }
        }
        PacketNames returnPacket = PACKET;
        returnPacket.setName(name.toLowerCase(Locale.ROOT));
        return returnPacket;
    }


    private String name;
    PacketNames(String name) {
        this.name = name;
    }

    PacketNames() {
        this.name = this.toString().toLowerCase(Locale.ROOT);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}