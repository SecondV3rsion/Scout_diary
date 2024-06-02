package si.uni_lj.fe.tnuv.scoutdiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;

public class MainActivity extends AppCompatActivity {

    private ShapeableImageView btn_slika_voda;
    private TextView groupName;
    private PreferencesUtil preferencesUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        preferencesUtil = new PreferencesUtil(this);

        // Check if it's the first run and clear preferences
        clearPreferencesOnFirstRun();
    }

    private void initViews() {
        btn_slika_voda = findViewById(R.id.btn_slika_voda);
        groupName = findViewById(R.id.ime_voda);
    }

    private void clearPreferencesOnFirstRun() {
        SharedPreferences prefs = getSharedPreferences("scout_diary_prefs", Context.MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean("isFirstRun", true);

        if (isFirstRun) {
            preferencesUtil.clearPreferences();

            // Set isFirstRun to false to indicate that the app has been initialized
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isFirstRun", false);
            editor.apply();
        }
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
        }
    }

    private void loadGroupImage() {
        Uri imageUri = preferencesUtil.loadImageUri("group_img");
        if (imageUri != null) {
            btn_slika_voda.setImageURI(imageUri);
            btn_slika_voda.setScaleType(ImageView.ScaleType.CENTER_CROP);
            btn_slika_voda.setBackground(null);
            btn_slika_voda.setStrokeWidth(30);
            setupListeners(true); // Image is present
        } else {
            btn_slika_voda.setImageResource(R.drawable.add_btn_logo);
            setupListeners(false); // Image is not present, show default image
        }
    }

    private void setupListeners(boolean isImagePresent) {
        if (isImagePresent) {
            btn_slika_voda.setOnClickListener(view -> goToGroupOverview());
            btn_slika_voda.setOnLongClickListener(view -> {
                goToEditGroupActivity(true);
                return true;
            });
        } else {
            btn_slika_voda.setOnClickListener(view -> goToEditGroupActivity(false));
            btn_slika_voda.setOnLongClickListener(view -> {
                // Optionally you can handle long click differently when no image is present
                return false;
            });
        }
    }

    private void goToGroupOverview() {
        startActivity(new Intent(this, GroupOverviewActivity.class));
    }

    private void goToEditGroupActivity(boolean isEditMode) {
        Intent intent = new Intent(this, EditGroupActivity.class);
        intent.putExtra("isEditMode", isEditMode);
        startActivity(intent);
    }
}