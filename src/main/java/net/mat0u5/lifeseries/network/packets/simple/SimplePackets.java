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
    public static final SimpleStringListPacket PLAYER_INVISIBLE = new SimpleStringListPacket("PLAYER_INVISIBLE");//TODO test
    public static final SimpleStringListPacket SET_LIVES = new SimpleStringListPacket("SET_LIVES");
    public static final SimpleStringListPacket SET_TEAM = new SimpleStringListPacket("SET_TEAM");
    public static final SimpleStringListPacket CONFIG_SECRET_TASK = new SimpleStringListPacket("CONFIG_SECRET_TASK");
    public static final SimpleStringListPacket CONFIG_TRIVIA = new SimpleStringListPacket("CONFIG_TRIVIA");
    public static final SimpleStringListPacket ACTIVE_WILDCARDS = new SimpleStringListPacket("ACTIVE_WILDCARDS");

    public static final SimpleStringPacket CURRENT_SEASON = new SimpleStringPacket("CURRENT_SEASON");
    public static final SimpleStringPacket SESSION_STATUS = new SimpleStringPacket("SESSION_STATUS");
    public static final SimpleStringPacket SELECT_SEASON = new SimpleStringPacket("SELECT_SEASON");
    public static final SimpleStringPacket SHOW_TOTEM = new SimpleStringPacket("SHOW_TOTEM");
    public static final SimpleStringPacket SELECTED_WILDCARD = new SimpleStringPacket("SELECTED_WILDCARD");
    public static final SimpleStringPacket SET_SEASON = new SimpleStringPacket("SET_SEASON");
    public static final SimpleStringPacket SUBMIT_VOTE = new SimpleStringPacket("SUBMIT_VOTE");

    public static final SimpleBooleanPacket PREVENT_GLIDING = new SimpleBooleanPacket("PREVENT_GLIDING");
    public static final SimpleBooleanPacket TABLIST_SHOW_EXACT = new SimpleBooleanPacket("TABLIST_SHOW_EXACT");
    public static final SimpleBooleanPacket FIX_SIZECHANGING_BUGS = new SimpleBooleanPacket("FIX_SIZECHANGING_BUGS");
    public static final SimpleBooleanPacket ANIMAL_DISGUISE_ARMOR = new SimpleBooleanPacket("ANIMAL_DISGUISE_ARMOR");
    public static final SimpleBooleanPacket ANIMAL_DISGUISE_HANDS = new SimpleBooleanPacket("ANIMAL_DISGUISE_HANDS");
    public static final SimpleBooleanPacket SNOWY_NETHER = new SimpleBooleanPacket("SNOWY_NETHER");
    public static final SimpleBooleanPacket EMPTY_SCREEN = new SimpleBooleanPacket("EMPTY_SCREEN");
    public static final SimpleBooleanPacket HIDE_SLEEP_DARKNESS = new SimpleBooleanPacket("HIDE_SLEEP_DARKNESS");
    public static final SimpleBooleanPacket MIC_MUTED = new SimpleBooleanPacket("MIC_MUTED");
    public static final SimpleBooleanPacket ADMIN_INFO = new SimpleBooleanPacket("ADMIN_INFO");
    public static final SimpleBooleanPacket TRIPLE_JUMP = new SimpleBooleanPacket("TRIPLE_JUMP");
    public static final SimpleBooleanPacket MOD_DISABLED = new SimpleBooleanPacket("MOD_DISABLED");

    public static final SimpleNumberPacket PLAYER_MIN_MSPT = new SimpleNumberPacket("PLAYER_MIN_MSPT");
    public static final SimpleNumberPacket SIZESHIFTING_CHANGE = new SimpleNumberPacket("SIZESHIFTING_CHANGE");

    public static final SimpleLongPacket CURSE_SLIDING = new SimpleLongPacket("CURSE_SLIDING");
    public static final SimpleLongPacket SUPERPOWER_COOLDOWN = new SimpleLongPacket("SUPERPOWER_COOLDOWN");
    public static final SimpleLongPacket SHOW_VIGNETTE = new SimpleLongPacket("SHOW_VIGNETTE");
    public static final SimpleLongPacket MIMICRY_COOLDOWN = new SimpleLongPacket("MIMICRY_COOLDOWN");
    public static final SimpleLongPacket TIME_DILATION = new SimpleLongPacket("TIME_DILATION");
    public static final SimpleLongPacket SESSION_TIMER = new SimpleLongPacket("SESSION_TIMER");

    public static final SimpleIntegerPacket TRIVIA_ANSWER = new SimpleIntegerPacket("TRIVIA_ANSWER");
    public static final SimpleIntegerPacket SNAIL_AIR = new SimpleIntegerPacket("SNAIL_AIR");
    public static final SimpleIntegerPacket FAKE_THUNDER = new SimpleIntegerPacket("FAKE_THUNDER");
    public static final SimpleIntegerPacket TAB_LIST_LIVES_CUTOFF = new SimpleIntegerPacket("TAB_LIST_LIVES_CUTOFF");
    public static final SimpleIntegerPacket TRIVIA_TIMER = new SimpleIntegerPacket("TRIVIA_TIMER");
    public static final SimpleIntegerPacket VOTING_TIME = new SimpleIntegerPacket("VOTING_TIME");

    public static final SimpleEmptyPacket HOLDING_JUMP = new SimpleEmptyPacket("HOLDING_JUMP");
    public static final SimpleEmptyPacket SUPERPOWER_KEY = new SimpleEmptyPacket("SUPERPOWER_KEY");
    public static final SimpleEmptyPacket TRANSCRIPT = new SimpleEmptyPacket("TRANSCRIPT");
    public static final SimpleEmptyPacket JUMP = new SimpleEmptyPacket("JUMP");
    public static final SimpleEmptyPacket RESET_TRIVIA = new SimpleEmptyPacket("RESET_TRIVIA");
    public static final SimpleEmptyPacket SELECT_WILDCARDS = new SimpleEmptyPacket("SELECT_WILDCARDS");
    public static final SimpleEmptyPacket CLEAR_CONFIG = new SimpleEmptyPacket("CLEAR_CONFIG");
    public static final SimpleEmptyPacket OPEN_CONFIG = new SimpleEmptyPacket("OPEN_CONFIG");
    public static final SimpleEmptyPacket TOGGLE_TIMER = new SimpleEmptyPacket("TOGGLE_TIMER");
    public static final SimpleEmptyPacket PAST_LIFE_CHOOSE_TWIST = new SimpleEmptyPacket("PAST_LIFE_CHOOSE_TWIST");
    public static final SimpleEmptyPacket TRIVIA_ALL_WRONG = new SimpleEmptyPacket("TRIVIA_ALL_WRONG");
    public static final SimpleEmptyPacket STOP_TRIVIA_SOUNDS = new SimpleEmptyPacket("STOP_TRIVIA_SOUNDS");
    public static final SimpleEmptyPacket REMOVE_SLEEP_SCREENS = new SimpleEmptyPacket("REMOVE_SLEEP_SCREENS");


    //public static final SimpleLongPacket _______ = new SimpleLongPacket("_______");
}
