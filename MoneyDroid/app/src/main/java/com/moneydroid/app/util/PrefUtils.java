package com.moneydroid.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Utilities and Constants related to app preferences.
 */
public class PrefUtils {
    public static final String PREF_CURRENCY = "pref_currency";

    public static void setCurrency(final Context context, final String currency) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_CURRENCY, currency);
    }

    public static String getCurrency(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_CURRENCY, "INR");
    }

}
