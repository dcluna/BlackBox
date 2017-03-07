package icechen1.com.blackbox.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import icechen1.com.blackbox.AppConstants;
import icechen1.com.blackbox.BuildConfig;

/**
 * Created by yuchen.hou on 15-07-11.
 */
public class AppUtils {

    public static long getBufferSavedTime(long start, long end, long bufferSize){
        long diff = end - start;
        long maxTime = bufferSize * 1000; //s -> ms
        return diff <= maxTime ? diff : maxTime;
    }

    private final static int DAYS_UNTIL_PROMPT = 0;
    private final static int LAUNCHES_UNTIL_PROMPT = 3;

    public static boolean isMp3Enabled(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("use_mp3", false);
    }

    public static boolean isPremium(Context context) {
        if(BuildConfig.DEBUG) {
            return true;
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(AppConstants.IAP_PREF, false);
    }

    public static boolean shouldLaunchAppRater(Context mContext) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        if (prefs.getBoolean("apprater_show", false)) { return false; }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launch_count = prefs.getLong("apprater_launch_count", 0) + 1;
        editor.putLong("apprater_launch_count", launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong("apprater_date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("apprater_date_firstlaunch", date_firstLaunch);
        }

        boolean shouldLaunch = false;

        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch +
                    (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                shouldLaunch = true;
            }
        }
        if(shouldLaunch) {
            editor.putBoolean("apprater_show", true);
        }
        editor.apply();

        return shouldLaunch;
    }
}
