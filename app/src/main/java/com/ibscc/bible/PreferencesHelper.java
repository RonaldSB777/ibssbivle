package com.ibscc.bible;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class PreferencesHelper {

    private static final String PREFS_NAME = "BibliaPrefs";
    private static final String KEY_FAVORITES = "favorites";
    private static final String KEY_NIGHT_MODE = "night_mode";

    public static void saveFavorite(Context context, String reference) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> favorites = new HashSet<>(prefs.getStringSet(KEY_FAVORITES, new HashSet<String>()));
        favorites.add(reference);
        prefs.edit().putStringSet(KEY_FAVORITES, favorites).apply();
    }

    public static void removeFavorite(Context context, String reference) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> favorites = new HashSet<>(prefs.getStringSet(KEY_FAVORITES, new HashSet<String>()));
        favorites.remove(reference);
        prefs.edit().putStringSet(KEY_FAVORITES, favorites).apply();
    }

    public static boolean isFavorite(Context context, String reference) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> favorites = prefs.getStringSet(KEY_FAVORITES, new HashSet<String>());
        return favorites.contains(reference);
    }

    public static List<String> getFavorites(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return new ArrayList<>(prefs.getStringSet(KEY_FAVORITES, new HashSet<String>()));
    }

    public static void setNightMode(Context context, boolean enabled) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_NIGHT_MODE, enabled).apply();
    }

    public static boolean isNightMode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_NIGHT_MODE, false);
    }
}