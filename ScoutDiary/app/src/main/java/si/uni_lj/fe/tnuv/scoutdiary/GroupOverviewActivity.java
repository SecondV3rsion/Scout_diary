package si.uni_lj.fe.tnuv.scoutdiary;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GroupOverviewActivity extends AppCompatActivity {

    private ImageView groupImg;
    private TextView groupName;
    private ShapeableImageView btnMeetingImg;
    private TextView dateView;
    private ImageButton btnPreviousDay, btnNextDay;
    private EditText etMeetingName, etMeetingDescription;
    private RatingBar ratingBar;
    private Calendar calendar;

    private ImageView flagRed, flagGreen, flagBlue, flagPurple, flagYellow;

    private PreferencesUtil preferencesUtil;
    private MeetingView meetingView;

    private ImageSelector imageSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_overview);
        initViews();
        setupListeners();
        preferencesUtil = new PreferencesUtil(this);
        meetingView = new MeetingView(preferencesUtil);

        imageSelector = new ImageSelector(this, new ImageSelector.ImageSelectedCallback() {
            @Override
            public void onImageSelected(Uri uri) {
                btnMeetingImg.setTag(uri.toString());
                btnMeetingImg.setImageURI(uri);
                saveMeetingForCurrentDate();
            }

            @Override
            public void onSelectionError(String message) {
                Toast.makeText(GroupOverviewActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        calendar = Calendar.getInstance();
        setCurrentDate();
        loadMeetingForCurrentDate();
    }

    private void initViews() {
        groupImg = findViewById(R.id.slika_voda);
        groupName = findViewById(R.id.ime_voda);
        btnMeetingImg = findViewById(R.id.btn_izberi_sliko_sestanka);
        dateView = findViewById(R.id.current_date);
        btnPreviousDay = findViewById(R.id.btn_previous_day);
        btnNextDay = findViewById(R.id.btn_next_day);
        etMeetingName = findViewById(R.id.et_meeting_name);
        etMeetingDescription = findViewById(R.id.et_meeting_description);
        ratingBar = findViewById(R.id.ocena_sestanka);

        flagRed = findViewById(R.id.flag_red);
        flagGreen = findViewById(R.id.flag_green);
        flagBlue = findViewById(R.id.flag_blue);
        flagPurple = findViewById(R.id.flag_purple);
        flagYellow = findViewById(R.id.flag_yellow);
    }

    private void setupListeners() {
        btnMeetingImg.setOnClickListener(v -> imageSelector.openGallery());
        findViewById(R.id.btn_evidenca).setOnClickListener(v -> showAttendanceDialog());
        findViewById(R.id.btn_select_flags).setOnClickListener(v -> showFlagSelectionDialog());


        findViewById(R.id.btn_zbrisi).setOnClickListener(v -> {
            deleteDataForCurrentDate();
            Toast.makeText(GroupOverviewActivity.this, R.string.meeting_deleted, Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btn_shrani).setOnClickListener(v -> {
            saveMeetingForCurrentDate();
            Toast.makeText(GroupOverviewActivity.this, R.string.meeting_saved, Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.btn_arhiv).setOnClickListener(v -> {
            saveMeetingForCurrentDate();
            Intent intent = new Intent(GroupOverviewActivity.this, ArchiveActivity.class);
            startActivity(intent);
        });

        btnPreviousDay.setOnClickListener(v -> {
            saveMeetingForCurrentDate();
            changeDateBy(-1);
        });

        btnNextDay.setOnClickListener(v -> {
            saveMeetingForCurrentDate();
            changeDateBy(1);
        });
    }


    private void showFlagSelectionDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_flags_selection);

        CheckBox checkboxRed = dialog.findViewById(R.id.checkbox_red);
        CheckBox checkboxGreen = dialog.findViewById(R.id.checkbox_green);
        CheckBox checkboxBlue = dialog.findViewById(R.id.checkbox_blue);
        CheckBox checkboxPurple = dialog.findViewById(R.id.checkbox_purple);
        CheckBox checkboxYellow = dialog.findViewById(R.id.checkbox_yellow);
        Button btnSaveFlags = dialog.findViewById(R.id.btn_save_flags);

        checkboxRed.setChecked(flagRed.getVisibility() == View.VISIBLE);
        checkboxGreen.setChecked(flagGreen.getVisibility() == View.VISIBLE);
        checkboxBlue.setChecked(flagBlue.getVisibility() == View.VISIBLE);
        checkboxPurple.setChecked(flagPurple.getVisibility() == View.VISIBLE);
        checkboxYellow.setChecked(flagYellow.getVisibility() == View.VISIBLE);

        btnSaveFlags.setOnClickListener(v -> {
            flagRed.setVisibility(checkboxRed.isChecked() ? View.VISIBLE : View.GONE);
            flagGreen.setVisibility(checkboxGreen.isChecked() ? View.VISIBLE : View.GONE);
            flagBlue.setVisibility(checkboxBlue.isChecked() ? View.VISIBLE : View.GONE);
            flagPurple.setVisibility(checkboxPurple.isChecked() ? View.VISIBLE : View.GONE);
            flagYellow.setVisibility(checkboxYellow.isChecked() ? View.VISIBLE : View.GONE);

            saveFlagsForCurrentDate();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showAttendanceDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_attendance);
        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerViewAttendance);
        Button btnSave = dialog.findViewById(R.id.btn_save_attendance);

        List<String> members = preferencesUtil.loadGroupMembers();
        String dateKey = getDateKey();
        List<Boolean> attendance = preferencesUtil.loadAttendance(dateKey);

        if (attendance == null || attendance.isEmpty()) {
            attendance = new ArrayList<>(Collections.nCopies(members.size(), false));
        }

        AttendanceAdapter adapter = new AttendanceAdapter(members, attendance);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnSave.setOnClickListener(v -> {
            saveAttendance(adapter.getAttendanceList());
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
            groupImg.setBackground(null);
        } else {
            groupImg.setImageResource(R.drawable.ic_photo);
        }

        loadMeetingForCurrentDate();
    }

    private void setCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d. M. yy", Locale.getDefault());
        String currentDate = sdf.format(calendar.getTime());
        dateView.setText(currentDate);
    }

    private void changeDateBy(int days) {
        saveMeetingForCurrentDate();
        calendar.add(Calendar.DAY_OF_MONTH, days);
        setCurrentDate();
        loadMeetingForCurrentDate();
    }

    private void deleteDataForCurrentDate() {
        Date currentDate = calendar.getTime();
        meetingView.deleteMeeting(currentDate);

        etMeetingName.setText("");
        etMeetingDescription.setText("");
        btnMeetingImg.setTag(null);
        btnMeetingImg.setImageResource(R.drawable.ic_photo);
        ratingBar.setRating(0);
        flagRed.setVisibility(View.GONE);
        flagGreen.setVisibility(View.GONE);
        flagBlue.setVisibility(View.GONE);
        flagPurple.setVisibility(View.GONE);
        flagYellow.setVisibility(View.GONE);
    }

    private void saveMeetingForCurrentDate() {
        Date currentDate = calendar.getTime();
        String meetingName = etMeetingName.getText().toString();
        String meetingDescription = etMeetingDescription.getText().toString();
        Uri meetingImageUri = btnMeetingImg.getTag() != null ? Uri.parse(btnMeetingImg.getTag().toString()) : null;
        float rating = ratingBar.getRating();
        List<Boolean> flags = getFlags();
        List<Boolean> attendance = preferencesUtil.loadAttendance(getDateKey());

        // Check if meeting name, description, and image are not empty
        if (!meetingName.isEmpty() || !meetingDescription.isEmpty() || meetingImageUri != null) {
            // Save the meeting
            meetingView.saveMeeting(currentDate, meetingName, meetingDescription, meetingImageUri, rating, flags, attendance);
        }
    }

    private void loadMeetingForCurrentDate() {
        Date currentDate = calendar.getTime();
        MeetingView.MeetingData meetingData = meetingView.loadMeeting(currentDate);

        // Set meeting name and description
        etMeetingName.setText(meetingData.getMeetingName());
        etMeetingDescription.setText(meetingData.getMeetingDescription());

        // Set meeting image
        if (meetingData.getMeetingImageUri() != null) {
            btnMeetingImg.setTag(meetingData.getMeetingImageUri().toString());
            btnMeetingImg.setImageURI(meetingData.getMeetingImageUri());
            btnMeetingImg.setBackground(null);
        } else {
            btnMeetingImg.setTag(null);
            btnMeetingImg.setImageResource(R.drawable.ic_photo);
        }

        // Set rating bar
        ratingBar.setRating(meetingData.getRating());

        // Set flags
        List<Boolean> flags = meetingData.getFlags();
        flagRed.setVisibility(flags.size() > 0 && flags.get(0) ? View.VISIBLE : View.GONE);
        flagGreen.setVisibility(flags.size() > 1 && flags.get(1) ? View.VISIBLE : View.GONE);
        flagBlue.setVisibility(flags.size() > 2 && flags.get(2) ? View.VISIBLE : View.GONE);
        flagPurple.setVisibility(flags.size() > 3 && flags.get(3) ? View.VISIBLE : View.GONE);
        flagYellow.setVisibility(flags.size() > 4 && flags.get(4) ? View.VISIBLE : View.GONE);
    }

    private String getDateKey() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    private void saveFlagsForCurrentDate() {
        List<Boolean> flags = getFlags();
        String dateKey = getDateKey();
        String meetingName = etMeetingName.getText().toString();
        String meetingDescription = etMeetingDescription.getText().toString();
        Uri meetingImageUri = btnMeetingImg.getTag() != null ? Uri.parse(btnMeetingImg.getTag().toString()) : null;
        float rating = ratingBar.getRating();
        List<Boolean> attendance = preferencesUtil.loadAttendance(dateKey);

        meetingView.saveMeeting(calendar.getTime(), meetingName, meetingDescription, meetingImageUri, rating, flags, attendance);
    }

    private List<Boolean> getFlags() {
        List<Boolean> flags = new ArrayList<>();
        flags.add(flagRed.getVisibility() == View.VISIBLE);
        flags.add(flagGreen.getVisibility() == View.VISIBLE);
        flags.add(flagBlue.getVisibility() == View.VISIBLE);
        flags.add(flagPurple.getVisibility() == View.VISIBLE);
        flags.add(flagYellow.getVisibility() == View.VISIBLE);
        return flags;
    }
}

