package si.uni_lj.fe.tnuv.scoutdiary;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
public class PreferencesUtil {

    private static final String PREFS_NAME = "my_prefs";
    private static final String PREF_KEY_IMAGE_URI = "image_uri";
    private static final String PREF_KEY_GROUP_NAME = "group_name";

    public static void saveGroupName(Context context, String groupName) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(PREF_KEY_GROUP_NAME, groupName);
        editor.apply();
    }

    public static String loadGroupName(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString(PREF_KEY_GROUP_NAME, null);  // Return null or default value if not found
    }

    public static void saveImageUri(Context context, Uri imageUri) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(PREF_KEY_IMAGE_URI, imageUri.toString());
        editor.apply();
    }

    public static Uri loadImageUri(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String uriString = sharedPref.getString(PREF_KEY_IMAGE_URI, null);
        return uriString != null ? Uri.parse(uriString) : null;
    }

}
