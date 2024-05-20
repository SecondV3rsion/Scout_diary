package si.uni_lj.fe.tnuv.scoutdiary;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GroupOverviewActivity extends AppCompatActivity {

    private ImageView groupImg;
    private TextView groupName;
    private ImageButton btn_meetingImg;
    private TextView DateView;
    private ImageButton btnPreviousDay, btnNextDay;
    private EditText etMeetingName, etMeetingDescription;
    private RatingBar ratingBar;
    private Calendar calendar;


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
                btn_meetingImg.setTag(uri.toString());
                btn_meetingImg.setImageURI(uri);
                saveDetailsForCurrentDate(); // Save the image URI when selected
            }

            @Override
            public void onSelectionError(String message) {
                Toast.makeText(GroupOverviewActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize calendar and set the current date
        calendar = Calendar.getInstance();
        setCurrentDate();
        loadDetailsForCurrentDate();
    }

    private void initViews() {
        groupImg = findViewById(R.id.slika_voda);
        groupName = findViewById(R.id.ime_voda);
        btn_meetingImg = findViewById(R.id.btn_izberi_sliko_sestanka);
        DateView = findViewById(R.id.current_date);
        btnPreviousDay = findViewById(R.id.btn_previous_day);
        btnNextDay = findViewById(R.id.btn_next_day);
        etMeetingName = findViewById(R.id.et_meeting_name);
        etMeetingDescription = findViewById(R.id.et_meeting_description);
        ratingBar = findViewById(R.id.ocena_sestanka);
    }

    private void setupListeners() {
        btn_meetingImg.setOnClickListener(v -> imageSelector.openGallery());
        findViewById(R.id.btn_evidenca).setOnClickListener(v -> showAttendanceDialog());

        btnPreviousDay.setOnClickListener(v -> {
            saveDetailsForCurrentDate();
            changeDateBy(-1);
        });

        btnNextDay.setOnClickListener(v -> {
            saveDetailsForCurrentDate();
            changeDateBy(1);
        });
    }

    private void showAttendanceDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_attendance);
        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerViewAttendance);
        Button btnSave = dialog.findViewById(R.id.btn_save_attendance);

        List<String> members = preferencesUtil.loadGroupMembers();
        String dateKey = getDateKey();
        List<Boolean> attendance = preferencesUtil.loadAttendance(dateKey);

        // Preveri, ali je seznam prisotnosti prazen in ga inicializiraj, če je potrebno
        if (attendance == null || attendance.isEmpty()) {
            attendance = new ArrayList<>(Collections.nCopies(members.size(), false));
        }

        AttendanceAdapter adapter = new AttendanceAdapter(members, attendance);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnSave.setOnClickListener(v -> {
            saveAttendance(adapter.getAttendanceList()); // Save attendance record
            Toast.makeText(this, R.string.prisotnost_shranjena, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void saveAttendance(List<Boolean> attendance) {
        String dateKey = getDateKey();
        preferencesUtil.saveAttendance(dateKey, attendance);
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

        loadDetailsForCurrentDate();
    }

    private void setCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d. M. yy", Locale.getDefault());
        String currentDate = sdf.format(calendar.getTime());
        DateView.setText(currentDate);
    }

    private void changeDateBy(int days) {
        saveDetailsForCurrentDate(); // Save details before moving to the next date
        calendar.add(Calendar.DAY_OF_MONTH, days);
        setCurrentDate();
        loadDetailsForCurrentDate();
    }

    private void saveDetailsForCurrentDate() {
        String dateKey = getDateKey();
        String meetingName = etMeetingName.getText().toString();
        String meetingDescription = etMeetingDescription.getText().toString();
        Uri meetingImageUri = btn_meetingImg.getTag() != null ? Uri.parse(btn_meetingImg.getTag().toString()) : null;
        float rating = ratingBar.getRating();

        // Save meeting details including the image URI for the current date
        preferencesUtil.saveMeetingDetails(dateKey, meetingName, meetingDescription, meetingImageUri, rating);
    }

    private void loadDetailsForCurrentDate() {
        String dateKey = getDateKey();
        etMeetingName.setText(preferencesUtil.loadMeetingName(dateKey));
        etMeetingDescription.setText(preferencesUtil.loadMeetingDescription(dateKey));

        // Load the image URI for the current date directly from PreferencesUtil
        Uri meetingImageUri = preferencesUtil.loadMeetingImageUri(dateKey);
        if (meetingImageUri != null) {
            btn_meetingImg.setTag(meetingImageUri.toString()); // Save the URI as a tag
            btn_meetingImg.setImageURI(meetingImageUri);
        } else {
            btn_meetingImg.setTag(null); // Clear the tag if there's no image
            btn_meetingImg.setImageResource(R.drawable.ic_launcher_foreground);
        }
        ratingBar.setRating(preferencesUtil.loadMeetingRating(dateKey));
    }

    private String getDateKey() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }
}
