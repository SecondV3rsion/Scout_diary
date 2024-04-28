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

    ImageButton btn_slika_voda;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnNovVod = findViewById(R.id.btn_nov_vod);
        btn_slika_voda = findViewById(R.id.btn_slika_voda);

        btnNovVod.setOnClickListener(this);
        btn_slika_voda.setOnClickListener(v -> goToGroupOverview());

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadGroupName();
        loadGroupImage();
    }

    private void loadGroupName() {
        String groupNameString = PreferencesUtil.loadGroupName(this);

        TextView groupName = findViewById(R.id.ime_voda);
        if (groupNameString != null) {
            groupName.setText(groupNameString);
        } else {
            groupName.setText(R.string.default_group_name); // Make sure this string exists in your strings.xml
        }
    }

    private void loadGroupImage() {
        Uri imageUri = PreferencesUtil.loadImageUri(this);

        if (imageUri != null) {
            try {
                btn_slika_voda.setImageURI(imageUri); // Assuming btn_slika_voda is your ImageView
                Log.d("EditVodActivity", "Image set to ImageView successfully");
            } catch (Exception e) { // Typically, setImageURI does not throw an exception so try-catch can be optional depending on further use
                Log.e("EditVodActivity", "Exception in setting image URI: ", e);
            }
        } else {
            Log.d("EditVodActivity", "No URI found in SharedPreferences");
        }
    }


    public void goToGroupOverview() {
        Intent intent= new Intent(this,GroupOverviewActivity.class);
        startActivity(intent);
    }
    @Override
    public void onClick(View v) {
        Intent intent= new Intent(this,EditVodActivity.class);
        startActivity(intent);
    }
}