package si.uni_lj.fe.tnuv.scoutdiary;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PreferencesUtil {

    private static final String PREFS_NAME = "scout_diary_prefs";
    private SharedPreferences prefs;
    private Gson gson;

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
}
