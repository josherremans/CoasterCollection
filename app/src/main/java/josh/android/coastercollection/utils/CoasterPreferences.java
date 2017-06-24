package josh.android.coastercollection.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import josh.android.coastercollection.R;

/**
 * Created by Jos on 28/05/2017.
 */

public class CoasterPreferences {

    public static boolean showCoasterDetails(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        boolean show = sharedPref.getBoolean(context.getString(R.string.pref_key_show_info_details), false);

        return show;
    }

    public static boolean showCoasterDescription(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        boolean show = sharedPref.getBoolean(context.getString(R.string.pref_key_show_info_description), false);

        return show;
    }

    public static boolean showCoasterText(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        boolean show = sharedPref.getBoolean(context.getString(R.string.pref_key_show_info_text), false);

        return show;
    }

    public static boolean showCoasterQuality(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        boolean show = sharedPref.getBoolean(context.getString(R.string.pref_key_show_info_quality), false);

        return show;
    }

    public static boolean showCollector(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        boolean show = sharedPref.getBoolean(context.getString(R.string.pref_key_show_info_collector), false);

        return show;
    }

    public static boolean showCollectDate(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        boolean show = sharedPref.getBoolean(context.getString(R.string.pref_key_show_info_date), false);

        return show;
    }

    public static boolean showShape(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        boolean show = sharedPref.getBoolean(context.getString(R.string.pref_key_show_info_shape), false);

        return show;
    }

    public static boolean showSeries(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        boolean show = sharedPref.getBoolean(context.getString(R.string.pref_key_show_info_series), false);

        return show;
    }

    public static boolean showLocation(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        boolean show = sharedPref.getBoolean(context.getString(R.string.pref_key_show_info_location), false);

        return show;
    }

    public static boolean showImageBackside(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        boolean show = sharedPref.getBoolean(context.getString(R.string.pref_key_show_info_image_back), false);

        return show;
    }

    public static boolean showInReverseOrder(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        boolean show = sharedPref.getBoolean(context.getString(R.string.pref_key_sort_reverse), false);

        return show;
    }

    public static String getListViewType(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        String listViewType = sharedPref.getString(context.getString(R.string.pref_key_listview_type), context.getResources().getStringArray(R.array.pref_listview_type_values)[0]); //"CardType");

        return listViewType;
    }
}
