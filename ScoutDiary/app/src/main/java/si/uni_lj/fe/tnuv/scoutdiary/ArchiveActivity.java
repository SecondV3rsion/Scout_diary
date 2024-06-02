package si.uni_lj.fe.tnuv.scoutdiary;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ArchiveActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        // Instantiate MeetingView with PreferencesUtil instance
        MeetingView meetingView = new MeetingView(new PreferencesUtil(this));

        // Load meeting dates
        List<Date> meetingDates = meetingView.loadMeetingDates();

        // Sort meeting dates in ascending order
        Collections.sort(meetingDates, new Comparator<Date>() {
            @Override
            public int compare(Date date1, Date date2) {
                return date2.compareTo(date1);
            }
        });

        // Create meeting views for each saved date in the meetingDates list
        for (Date date : meetingDates) {
            createMeetingView(date);
        }
    }


    public void createMeetingView(Date date) {
        // Inflate the meeting view layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View meetingView = inflater.inflate(R.layout.meeting_view, null);

        // Get references to views in the meeting view layout
        TextView meetingName = meetingView.findViewById(R.id.meeting_name);
        TextView meetingDescription = meetingView.findViewById(R.id.meeting_description);
        ShapeableImageView meetingImage = meetingView.findViewById(R.id.meeting_image);
        RatingBar meetingRating = meetingView.findViewById(R.id.meeting_rating);
        ImageView flagRed = meetingView.findViewById(R.id.flag_red);
        ImageView flagGreen = meetingView.findViewById(R.id.flag_green);
        ImageView flagBlue = meetingView.findViewById(R.id.flag_blue);
        ImageView flagPurple = meetingView.findViewById(R.id.flag_purple);
        ImageView flagYellow = meetingView.findViewById(R.id.flag_yellow);
        TextView meetingDate = meetingView.findViewById(R.id.meeting_date);

        // Set meeting date
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d. M. yy", Locale.getDefault());
        String formattedDate = sdf.format(date);
        meetingDate.setText(formattedDate);

        // Load meeting data for the specified date
        MeetingView meetingViewHelper = new MeetingView(new PreferencesUtil(this));
        MeetingView.MeetingData meetingData = meetingViewHelper.loadMeeting(date);

        // Set meeting name and description
        meetingName.setText(meetingData.getMeetingName());
        meetingDescription.setText(meetingData.getMeetingDescription());

        // Set meeting image
        if (meetingData.getMeetingImageUri() != null) {
            meetingImage.setImageURI(meetingData.getMeetingImageUri());
        } else {
            meetingImage.setImageResource(R.drawable.ic_photo);
        }

        // Set rating bar as view-only
        meetingRating.setRating(meetingData.getRating());
        meetingRating.setIsIndicator(true);

        // Set flags
        List<Boolean> flags = meetingData.getFlags();
        flagRed.setVisibility(flags.size() > 0 && flags.get(0) ? View.VISIBLE : View.GONE);
        flagGreen.setVisibility(flags.size() > 1 && flags.get(1) ? View.VISIBLE : View.GONE);
        flagBlue.setVisibility(flags.size() > 2 && flags.get(2) ? View.VISIBLE : View.GONE);
        flagPurple.setVisibility(flags.size() > 3 && flags.get(3) ? View.VISIBLE : View.GONE);
        flagYellow.setVisibility(flags.size() > 4 && flags.get(4) ? View.VISIBLE : View.GONE);

        // Add the meeting view to the linear layout
        LinearLayout linearLayout = findViewById(R.id.linear_layout);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, 16); // Set margins if needed
        meetingView.setLayoutParams(layoutParams);
        linearLayout.addView(meetingView);
    }

}