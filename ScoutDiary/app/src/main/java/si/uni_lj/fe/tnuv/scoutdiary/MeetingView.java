package si.uni_lj.fe.tnuv.scoutdiary;

import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MeetingView {

    private PreferencesUtil preferencesUtil;

    public MeetingView(PreferencesUtil preferencesUtil) {
        this.preferencesUtil = preferencesUtil;
    }

    /**
     * Saves the meeting details and attendance to preferences.
     *
     * @param date              the date of the meeting
     * @param meetingName       the name of the meeting
     * @param meetingDescription the description of the meeting
     * @param meetingImageUri   the image URI for the meeting
     * @param rating            the rating of the meeting
     * @param flags             additional flags for the meeting
     * @param attendance        attendance list for the meeting
     */
    public void saveMeeting(Date date, String meetingName, String meetingDescription, Uri meetingImageUri, float rating, List<Boolean> flags, List<Boolean> attendance) {
        String dateKey = getDateKey(date);
        preferencesUtil.saveMeetingDetails(dateKey, meetingName, meetingDescription, meetingImageUri, rating, flags);
        preferencesUtil.saveAttendance(dateKey, attendance);
    }

    /**
     * Loads the meeting details and attendance from preferences.
     *
     * @param date the date of the meeting
     * @return the loaded meeting data
     */
    public MeetingData loadMeeting(Date date) {
        String dateKey = getDateKey(date);
        String meetingName = preferencesUtil.loadMeetingName(dateKey);
        String meetingDescription = preferencesUtil.loadMeetingDescription(dateKey);
        Uri meetingImageUri = preferencesUtil.loadMeetingImageUri(dateKey);
        List<Boolean> attendance = preferencesUtil.loadAttendance(dateKey);
        float rating = preferencesUtil.loadMeetingRating(dateKey);
        List<Boolean> flags = preferencesUtil.loadMeetingFlags(dateKey);

        return new MeetingData(meetingName, meetingDescription, meetingImageUri, attendance, rating, flags);
    }

    /**
     * Generates a key string based on the given date.
     *
     * @param date the date
     * @return the key string
     */
    private String getDateKey(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(date);
    }

    public static class MeetingData {
        private final String meetingName;
        private final String meetingDescription;
        private final Uri meetingImageUri;
        private final List<Boolean> attendance;
        private final float rating;
        private final List<Boolean> flags;

        public MeetingData(String meetingName, String meetingDescription, Uri meetingImageUri, List<Boolean> attendance, float rating, List<Boolean> flags) {
            this.meetingName = meetingName;
            this.meetingDescription = meetingDescription;
            this.meetingImageUri = meetingImageUri;
            this.attendance = Collections.unmodifiableList(new ArrayList<>(attendance));
            this.rating = rating;
            this.flags = Collections.unmodifiableList(new ArrayList<>(flags));
        }

        public String getMeetingName() {
            return meetingName;
        }

        public String getMeetingDescription() {
            return meetingDescription;
        }

        public Uri getMeetingImageUri() {
            return meetingImageUri;
        }

        public List<Boolean> getAttendance() {
            return attendance;
        }

        public float getRating() {
            return rating;
        }

        public List<Boolean> getFlags() {
            return flags;
        }
    }
}



