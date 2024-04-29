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

public class MainActivity extends AppCompatActivity {

    private ImageButton btn_slika_voda;
    private TextView groupName;
    private PreferencesUtil preferencesUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setupListeners();
        preferencesUtil = new PreferencesUtil(this);
    }

    private void initViews() {
        Button btnNovVod = findViewById(R.id.btn_nov_vod);
        btn_slika_voda = findViewById(R.id.btn_slika_voda);
        groupName = findViewById(R.id.ime_voda); // Cache the TextView
    }

    private void setupListeners() {
        findViewById(R.id.btn_nov_vod).setOnClickListener(view -> goToEditGroupActivity());
        btn_slika_voda.setOnClickListener(view -> goToGroupOverview());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadGroupName();
        loadGroupImage();
    }

    private void loadGroupName() {
        String groupNameString = preferencesUtil.loadGroupName();
        if (groupNameString != null) {
            groupName.setText(groupNameString);
            groupName.setVisibility(View.VISIBLE);
        } else {
            groupName.setVisibility(View.GONE);
        }
    }

    private void loadGroupImage() {
        Uri imageUri = preferencesUtil.loadImageUri("group_img");
        if (imageUri != null) {
            btn_slika_voda.setImageURI(imageUri);
            btn_slika_voda.setVisibility(View.VISIBLE);
        } else {
            btn_slika_voda.setVisibility(View.GONE);
        }
    }


    private void goToGroupOverview() {
        startActivity(new Intent(this, GroupOverviewActivity.class));
    }

    private void goToEditGroupActivity() {
        startActivity(new Intent(this, EditGroupActivity.class));
    }
}