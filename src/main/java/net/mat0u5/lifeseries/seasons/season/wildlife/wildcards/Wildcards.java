package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards;

import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.*;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.snails.Snails;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaWildcard;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public enum Wildcards {
    NULL,
    SIZE_SHIFTING,
    HUNGER,
    SNAILS,
    TIME_DILATION,
    TRIVIA,
    MOB_SWAP,
    SUPERPOWERS,
    CALLBACK;


    public Wildcard getInstance() {
        if (this == SIZE_SHIFTING) return new SizeShifting();
        if (this == HUNGER) return new Hunger();
        if (this == SNAILS) return new Snails();
        if (this == TIME_DILATION) return new TimeDilation();
        if (this == TRIVIA) return new TriviaWildcard();
        if (this == MOB_SWAP) return new MobSwap();
        if (this == SUPERPOWERS) return new SuperpowersWildcard();
        if (this == CALLBACK) return new Callback();
        return null;
    }

    public String getStringName() {
        return this.toString().toLowerCase(Locale.ROOT);
    }

    public int getIndex() {
        return this.ordinal();
    }

    public static Wildcards getFromIndex(int index) {
        for (Wildcards wildcard : Wildcards.values()) {
            if (wildcard.getIndex() == index) return wildcard;
        }
        return Wildcards.NULL;
    }

    public static Wildcards getFromString(String wildcard) {
        try {
            return Enum.valueOf(Wildcards.class, wildcard.toUpperCase(Locale.ROOT));
        } catch(Exception e) {}
        return Wildcards.NULL;
    }

    public static List<Wildcards> getWildcards() {
        List<Wildcards> wildcards = new ArrayList<>(List.of(Wildcards.values()));
        wildcards.remove(Wildcards.NULL);
        return wildcards;
    }

    public static List<String> getWildcardsStr() {
        List<String> result = new ArrayList<>();
        for (Wildcards wildcard : getWildcards()) {
            String name = wildcard.getStringName();
            result.add(name);
        }
        return result;
    }

    public static List<Wildcards> getActiveWildcards() {
        return new ArrayList<>(WildcardManager.activeWildcards.keySet());
    }

    public static List<Wildcards> getInactiveWildcards() {
        List<Wildcards> result = new ArrayList<>(getWildcards());
        result.removeAll(getActiveWildcards());
        return result;
    }

    public static List<String> getInactiveWildcardsStr() {
        List<String> result = new ArrayList<>();
        for (Wildcards wildcard : getInactiveWildcards()) {
            String name = wildcard.getStringName();
            result.add(name);
        }
        return result;
    }

    public static List<String> getActiveWildcardsStr() {
        List<String> result = new ArrayList<>();
        for (Wildcards wildcard : getActiveWildcards()) {
            String name = wildcard.getStringName();
            result.add(name);
        }
        return result;
    }

}
