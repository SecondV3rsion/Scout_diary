package si.uni_lj.fe.tnuv.scoutdiary;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class GroupOverviewActivity extends AppCompatActivity {

    private ImageView groupImg;
    private TextView groupName;
    private ImageButton btnMeetingImg;
    private TextView dateView;
    private ImageButton btnPreviousDay, btnNextDay;
    private EditText etMeetingName, etMeetingDescription;
    private RatingBar ratingBar;
    private Calendar calendar;

    private ImageView flagRed, flagGreen, flagBlue, flagPurple, flagYellow;
    private Space flagsSpace;

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
                btnMeetingImg.setTag(uri.toString());
                btnMeetingImg.setImageURI(uri);
                saveDetailsForCurrentDate();
            }

            @Override
            public void onSelectionError(String message) {
                Toast.makeText(GroupOverviewActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        calendar = Calendar.getInstance();
        setCurrentDate();
        loadDetailsForCurrentDate();
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

        btnPreviousDay.setOnClickListener(v -> {
            saveDetailsForCurrentDate();
            changeDateBy(-1);
        });

        btnNextDay.setOnClickListener(v -> {
            saveDetailsForCurrentDate();
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
        } else {
            groupImg.setImageResource(R.drawable.ic_launcher_foreground);
        }

        loadDetailsForCurrentDate();
    }

    private void setCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d. M. yy", Locale.getDefault());
        String currentDate = sdf.format(calendar.getTime());
        dateView.setText(currentDate);
    }

    private void changeDateBy(int days) {
        saveDetailsForCurrentDate();
        calendar.add(Calendar.DAY_OF_MONTH, days);
        setCurrentDate();
        loadDetailsForCurrentDate();
    }

    private void saveDetailsForCurrentDate() {
        String dateKey = getDateKey();
        String meetingName = etMeetingName.getText().toString();
        String meetingDescription = etMeetingDescription.getText().toString();
        Uri meetingImageUri = btnMeetingImg.getTag() != null ? Uri.parse(btnMeetingImg.getTag().toString()) : null;
        float rating = ratingBar.getRating();

        List<Boolean> flags = getFlags();
        preferencesUtil.saveMeetingDetails(dateKey, meetingName, meetingDescription, meetingImageUri, rating, flags);
    }

    private void loadDetailsForCurrentDate() {
        String dateKey = getDateKey();
        etMeetingName.setText(preferencesUtil.loadMeetingName(dateKey));
        etMeetingDescription.setText(preferencesUtil.loadMeetingDescription(dateKey));

        Uri meetingImageUri = preferencesUtil.loadMeetingImageUri(dateKey);
        if (meetingImageUri != null) {
            btnMeetingImg.setTag(meetingImageUri.toString());
            btnMeetingImg.setImageURI(meetingImageUri);
        } else {
            btnMeetingImg.setTag(null);
            btnMeetingImg.setImageResource(R.drawable.ic_launcher_foreground);
        }
        ratingBar.setRating(preferencesUtil.loadMeetingRating(dateKey));
        loadFlags(dateKey);
    }

    private void loadFlags(String dateKey) {
        List<Boolean> flags = preferencesUtil.loadMeetingFlags(dateKey);
        flagRed.setVisibility(flags.size() > 0 && flags.get(0) ? View.VISIBLE : View.GONE);
        flagGreen.setVisibility(flags.size() > 1 && flags.get(1) ? View.VISIBLE : View.GONE);
        flagBlue.setVisibility(flags.size() > 2 && flags.get(2) ? View.VISIBLE : View.GONE);
        flagPurple.setVisibility(flags.size() > 3 && flags.get(3) ? View.VISIBLE : View.GONE);
        flagYellow.setVisibility(flags.size() > 4 && flags.get(4) ? View.VISIBLE : View.GONE);
    }

    private void saveFlagsForCurrentDate() {
        List<Boolean> flags = getFlags();
        preferencesUtil.saveMeetingDetails(getDateKey(), etMeetingName.getText().toString(),
                etMeetingDescription.getText().toString(),
                btnMeetingImg.getTag() != null ? Uri.parse(btnMeetingImg.getTag().toString()) : null,
                ratingBar.getRating(), flags);
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

    private String getDateKey() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }
}
