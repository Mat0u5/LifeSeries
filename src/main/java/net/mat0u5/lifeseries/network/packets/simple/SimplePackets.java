package net.mat0u5.lifeseries.network.packets.simple;

import net.mat0u5.lifeseries.network.packets.simple.instances.*;

import java.util.HashMap;
import java.util.Map;

public class SimplePackets {
    public static final Map<String, SimplePacket<?, ?>> registeredPackets = new HashMap<>();

    public static final SimpleStringListPacket LIMITED_LIFE_TIMER = new SimpleStringListPacket("LIMITED_LIFE_TIMER");//TODO test
    public static final SimpleStringListPacket SEASON_INFO = new SimpleStringListPacket("SEASON_INFO");
    public static final SimpleStringListPacket MORPH = new SimpleStringListPacket("MORPH");
    public static final SimpleStringListPacket HUNGER_NON_EDIBLE = new SimpleStringListPacket("HUNGER_NON_EDIBLE");
    public static final SimpleStringListPacket SKYCOLOR = new SimpleStringListPacket("SKYCOLOR");
    public static final SimpleStringListPacket FOGCOLOR = new SimpleStringListPacket("FOGCOLOR");
    public static final SimpleStringListPacket CLOUDCOLOR = new SimpleStringListPacket("CLOUDCOLOR");
    public static final SimpleStringListPacket PLAYER_INVISIBLE = new SimpleStringListPacket("PLAYER_INVISIBLE");
    public static final SimpleStringListPacket SET_LIVES = new SimpleStringListPacket("SET_LIVES");
    public static final SimpleStringListPacket SET_TEAM = new SimpleStringListPacket("SET_TEAM");
    public static final SimpleStringListPacket CONFIG_SECRET_TASK = new SimpleStringListPacket("CONFIG_SECRET_TASK");
    public static final SimpleStringListPacket CONFIG_TRIVIA = new SimpleStringListPacket("CONFIG_TRIVIA");

    public static final SimpleStringPacket CURRENT_SEASON = new SimpleStringPacket("CURRENT_SEASON");
    public static final SimpleStringPacket SESSION_STATUS = new SimpleStringPacket("SESSION_STATUS");
    public static final SimpleStringPacket ACTIVE_WILDCARDS = new SimpleStringPacket("ACTIVE_WILDCARDS");
    public static final SimpleStringPacket JUMP = new SimpleStringPacket("JUMP");
    public static final SimpleStringPacket RESET_TRIVIA = new SimpleStringPacket("RESET_TRIVIA");
    public static final SimpleStringPacket SELECT_WILDCARDS = new SimpleStringPacket("SELECT_WILDCARDS");
    public static final SimpleStringPacket CLEAR_CONFIG = new SimpleStringPacket("CLEAR_CONFIG");
    public static final SimpleStringPacket OPEN_CONFIG = new SimpleStringPacket("OPEN_CONFIG");
    public static final SimpleStringPacket SELECT_SEASON = new SimpleStringPacket("SELECT_SEASON");
    public static final SimpleStringPacket PREVENT_GLIDING = new SimpleStringPacket("PREVENT_GLIDING");
    public static final SimpleStringPacket TOGGLE_TIMER = new SimpleStringPacket("TOGGLE_TIMER");
    public static final SimpleStringPacket TABLIST_SHOW_EXACT = new SimpleStringPacket("TABLIST_SHOW_EXACT");
    public static final SimpleStringPacket SHOW_TOTEM = new SimpleStringPacket("SHOW_TOTEM");
    public static final SimpleStringPacket PAST_LIFE_CHOOSE_TWIST = new SimpleStringPacket("PAST_LIFE_CHOOSE_TWIST");
    public static final SimpleStringPacket FIX_SIZECHANGING_BUGS = new SimpleStringPacket("FIX_SIZECHANGING_BUGS");
    public static final SimpleStringPacket ANIMAL_DISGUISE_ARMOR = new SimpleStringPacket("ANIMAL_DISGUISE_ARMOR");
    public static final SimpleStringPacket ANIMAL_DISGUISE_HANDS = new SimpleStringPacket("ANIMAL_DISGUISE_HANDS");
    public static final SimpleStringPacket SNOWY_NETHER = new SimpleStringPacket("SNOWY_NETHER");
    public static final SimpleStringPacket EMPTY_SCREEN = new SimpleStringPacket("EMPTY_SCREEN");
    public static final SimpleStringPacket HIDE_SLEEP_DARKNESS = new SimpleStringPacket("HIDE_SLEEP_DARKNESS");
    public static final SimpleStringPacket MIC_MUTED = new SimpleStringPacket("MIC_MUTED");
    public static final SimpleStringPacket ADMIN_INFO = new SimpleStringPacket("ADMIN_INFO");
    public static final SimpleStringPacket TRIVIA_ALL_WRONG = new SimpleStringPacket("TRIVIA_ALL_WRONG");
    public static final SimpleStringPacket STOP_TRIVIA_SOUNDS = new SimpleStringPacket("STOP_TRIVIA_SOUNDS");
    public static final SimpleStringPacket REMOVE_SLEEP_SCREENS = new SimpleStringPacket("REMOVE_SLEEP_SCREENS");
    public static final SimpleStringPacket TRIPLE_JUMP = new SimpleStringPacket("TRIPLE_JUMP");
    public static final SimpleStringPacket MOD_DISABLED = new SimpleStringPacket("MOD_DISABLED");
    public static final SimpleStringPacket HOLDING_JUMP = new SimpleStringPacket("HOLDING_JUMP");
    public static final SimpleStringPacket SUPERPOWER_KEY = new SimpleStringPacket("SUPERPOWER_KEY");
    public static final SimpleStringPacket TRANSCRIPT = new SimpleStringPacket("TRANSCRIPT");
    public static final SimpleStringPacket SELECTED_WILDCARD = new SimpleStringPacket("SELECTED_WILDCARD");
    public static final SimpleStringPacket SET_SEASON = new SimpleStringPacket("SET_SEASON");
    public static final SimpleStringPacket SUBMIT_VOTE = new SimpleStringPacket("SUBMIT_VOTE");

    public static final SimpleNumberPacket PLAYER_MIN_MSPT = new SimpleNumberPacket("PLAYER_MIN_MSPT");
    public static final SimpleNumberPacket SNAIL_AIR = new SimpleNumberPacket("SNAIL_AIR");
    public static final SimpleNumberPacket FAKE_THUNDER = new SimpleNumberPacket("FAKE_THUNDER");
    public static final SimpleNumberPacket TAB_LIST_LIVES_CUTOFF = new SimpleNumberPacket("TAB_LIST_LIVES_CUTOFF");
    public static final SimpleNumberPacket SIZESHIFTING_CHANGE = new SimpleNumberPacket("SIZESHIFTING_CHANGE");
    public static final SimpleNumberPacket TRIVIA_TIMER = new SimpleNumberPacket("TRIVIA_TIMER");
    public static final SimpleNumberPacket VOTING_TIME = new SimpleNumberPacket("VOTING_TIME");
    public static final SimpleNumberPacket TRIVIA_ANSWER = new SimpleNumberPacket("TRIVIA_ANSWER");

    public static final SimpleLongPacket CURSE_SLIDING = new SimpleLongPacket("CURSE_SLIDING");
    public static final SimpleLongPacket SUPERPOWER_COOLDOWN = new SimpleLongPacket("SUPERPOWER_COOLDOWN");
    public static final SimpleLongPacket SHOW_VIGNETTE = new SimpleLongPacket("SHOW_VIGNETTE");
    public static final SimpleLongPacket MIMICRY_COOLDOWN = new SimpleLongPacket("MIMICRY_COOLDOWN");
    public static final SimpleLongPacket TIME_DILATION = new SimpleLongPacket("TIME_DILATION");
    public static final SimpleLongPacket SESSION_TIMER = new SimpleLongPacket("SESSION_TIMER");

    //public static final SimpleLongPacket _______ = new SimpleLongPacket("_______");
}
