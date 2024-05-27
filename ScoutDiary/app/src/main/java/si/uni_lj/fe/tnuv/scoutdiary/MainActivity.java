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
import android.widget.ImageView;
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
        preferencesUtil = new PreferencesUtil(this);
    }

    private void initViews() {
        btn_slika_voda = findViewById(R.id.btn_slika_voda);
        groupName = findViewById(R.id.ime_voda); // Cache the TextView
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
            btn_slika_voda.setScaleType(ImageView.ScaleType.FIT_CENTER);
            setupListeners(true); // Image is present
        } else {
            btn_slika_voda.setImageResource(R.drawable.add_btn_logo);
            setupListeners(false); // Image is not present, show default image
        }
    }

    private void setupListeners(boolean isImagePresent) {
        if (isImagePresent) {
            btn_slika_voda.setOnClickListener(view -> goToGroupOverview());
        } else {
            btn_slika_voda.setOnClickListener(view -> goToEditGroupActivity());
        }
    }

    private void goToGroupOverview() {
        startActivity(new Intent(this, GroupOverviewActivity.class));
    }

    private void goToEditGroupActivity() {
        startActivity(new Intent(this, EditGroupActivity.class));
    }
}