package si.uni_lj.fe.tnuv.scoutdiary;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class PreferencesUtil {

    private static final String PREFS_NAME = "scout_diary_prefs";
    private SharedPreferences prefs;
    private Gson gson;

    public void clearPreferences() {
        prefs.edit().clear().apply();
    }

    public PreferencesUtil(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveImageUri(String key, Uri imageUri) {
        prefs.edit().putString(key, imageUri.toString()).apply();
    }

    public Uri loadImageUri(String key) {
        String uriString = prefs.getString(key, null);
        return uriString != null ? Uri.parse(uriString) : null;
    }

    public void saveGroupName(String groupName) {
        prefs.edit().putString("group_name", groupName).apply();
    }

    public String loadGroupName() {
        return prefs.getString("group_name", null);
    }

    public void saveGroupMembers(List<String> members) {
        String json = gson.toJson(members);
        prefs.edit().putString("group_members", json).apply();
    }

    public List<String> loadGroupMembers() {
        String json = prefs.getString("group_members", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return json != null ? gson.fromJson(json, type) : new ArrayList<>();
    }

    public void saveAttendance(String date, List<Boolean> attendance) {
        String json = gson.toJson(attendance);
        prefs.edit().putString(date + "_attendance", json).apply();
    }

    public List<Boolean> loadAttendance(String date) {
        String json = prefs.getString(date + "_attendance", null);
        Type type = new TypeToken<ArrayList<Boolean>>() {}.getType();
        return json != null ? gson.fromJson(json, type) : new ArrayList<>();
    }

    public void saveMeetingDetails(String date, String name, String description, Uri imageUri, float rating, List<Boolean> flags) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(date + "_name", name);
        editor.putString(date + "_description", description);
        if (imageUri != null) {
            editor.putString(date + "_imageUri", imageUri.toString());
        }
        editor.putFloat(date + "_rating", rating);
        String flagsJson = gson.toJson(flags);
        editor.putString(date + "_flags", flagsJson);
        editor.apply();
    }

    public List<Boolean> loadMeetingFlags(String date) {
        String json = prefs.getString(date + "_flags", null);
        Type type = new TypeToken<ArrayList<Boolean>>() {}.getType();
        return json != null ? gson.fromJson(json, type) : new ArrayList<>();
    }

    public Uri loadMeetingImageUri(String date) {
        String uriString = prefs.getString(date + "_imageUri", null);
        return uriString != null ? Uri.parse(uriString) : null;
    }

    public String loadMeetingName(String date) {
        return prefs.getString(date + "_name", "");
    }

    public String loadMeetingDescription(String date) {
        return prefs.getString(date + "_description", "");
    }

    public float loadMeetingRating(String date) {
        return prefs.getFloat(date + "_rating", 0f);
    }

    public Set<String> getAllMeetingDateKeys() {
        Set<String> allKeys = prefs.getAll().keySet();
        Set<String> meetingDateKeys = new HashSet<>();
        for (String key : allKeys) {
            if (key.endsWith("_name")) {
                // Extract date part from the key
                String dateKey = key.replace("_name", "");
                meetingDateKeys.add(dateKey);
            }
        }
        return meetingDateKeys;
    }

}
