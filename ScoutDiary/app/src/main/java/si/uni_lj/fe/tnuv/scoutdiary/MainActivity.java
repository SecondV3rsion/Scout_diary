package si.uni_lj.fe.tnuv.scoutdiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String PREF_KEY_IMAGE_URI = "imageUri";
    private static final String PREF_KEY_GROUP_NAME = "groupName";
    ImageButton btn_slika_voda;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnNovVod = findViewById(R.id.btn_nov_vod);
        btn_slika_voda = findViewById(R.id.btn_slika_voda);

        btnNovVod.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadGroupName();
        loadGroupImage();
    }

    private void loadGroupName() {
        SharedPreferences sharedPref = getSharedPreferences("my_prefs", MODE_PRIVATE);
        String groupNameString = sharedPref.getString(PREF_KEY_GROUP_NAME, null);

        TextView groupName = findViewById(R.id.ime_voda);
        if (groupNameString != null) {
            groupName.setText(groupNameString);
        } else {
            groupName.setText(R.string.default_group_name);
        }
    }

    private void loadGroupImage() {
        SharedPreferences sharedPref = getSharedPreferences("my_prefs", MODE_PRIVATE);
        String uriString = sharedPref.getString(PREF_KEY_IMAGE_URI, null);

        if (uriString != null) {
            Uri imageUri = Uri.parse(uriString);
            try (InputStream stream = getContentResolver().openInputStream(imageUri)) {
                btn_slika_voda.setImageURI(imageUri); // Set the image to an ImageView
                Log.d("EditVodActivity", "Image set to ImageView successfully");
            } catch (FileNotFoundException e) {
                Log.e("EditVodActivity", "FileNotFoundException: " + e.getMessage());
            } catch (Exception e) {
                Log.e("EditVodActivity", "Exception in setting image URI: ", e);
            }
        } else {
            Log.d("EditVodActivity", "No URI found in SharedPreferences");
        }

    }

    @Override
    public void onClick(View v) {
        Intent intent= new Intent(this,EditVodActivity.class);
        startActivity(intent);
    }
}