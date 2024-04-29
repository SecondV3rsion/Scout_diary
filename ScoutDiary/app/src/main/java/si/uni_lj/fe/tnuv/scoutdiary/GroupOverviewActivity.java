package si.uni_lj.fe.tnuv.scoutdiary;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class GroupOverviewActivity extends AppCompatActivity {

    private ImageView groupImg;
    private TextView groupName;
    private ImageButton btn_meetingImg;

    // Class
    private PreferencesUtil preferencesUtil;
    private ImageSelector imageSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_overview);
        initViews();
        setupListeners();
        preferencesUtil = new PreferencesUtil(this);

        imageSelector = new ImageSelector(this, new ImageSelector.ImageSelectedCallback() {
            @Override
            public void onImageSelected(Uri uri) {
                btn_meetingImg.setImageURI(uri);
                preferencesUtil.saveImageUri("meeting_img", uri);
            }

            @Override
            public void onSelectionError(String message) {
                Toast.makeText(GroupOverviewActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        groupImg = findViewById(R.id.slika_voda);
        groupName = findViewById(R.id.ime_voda);
        btn_meetingImg = findViewById(R.id.btn_izberi_sliko_sestanka);
    }

    private void setupListeners() {
        btn_meetingImg.setOnClickListener(v -> imageSelector.openGallery());
        findViewById(R.id.btn_evidenca).setOnClickListener(v -> showAttendanceDialog());
    }

    private void showAttendanceDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_attendance);
        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerViewAttendance);
        Button btnSave = dialog.findViewById(R.id.btn_save_attendance);

        List<String> members = preferencesUtil.loadGroupMembers(); // Load member names
        List<Boolean> attendance = new ArrayList<>(Collections.nCopies(members.size(), false)); // Initial false attendance

        AttendanceAdapter adapter = new AttendanceAdapter(members, attendance);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnSave.setOnClickListener(v -> {
            // Here you could save the attendance to preferences or a database
            Toast.makeText(this, R.string.prisotnost_shranjena, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadGroupDetails();
    }

    private void loadGroupDetails() {
        groupName.setText(preferencesUtil.loadGroupName());

        Uri groupImageUri = preferencesUtil.loadImageUri("group_img");
        if (groupImageUri != null) {
            groupImg.setImageURI(groupImageUri);
        } else {
            groupImg.setImageResource(R.drawable.ic_launcher_foreground);
        }

        Uri meetingImageUri = preferencesUtil.loadImageUri("meeting_img");
        if (meetingImageUri != null) {
            btn_meetingImg.setImageURI(meetingImageUri);
        }
    }
}
