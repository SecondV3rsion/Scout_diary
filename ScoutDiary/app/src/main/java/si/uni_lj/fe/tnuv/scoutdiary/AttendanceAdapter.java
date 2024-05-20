package si.uni_lj.fe.tnuv.scoutdiary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {

    private List<String> members; // List of member names
    private List<Boolean> attendance; // Attendance status

    public AttendanceAdapter(List<String> members, List<Boolean> attendance) {
        this.members = members;
        this.attendance = attendance;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String member = members.get(position);
        holder.memberName.setText(member);
        holder.attendance.setChecked(attendance.get(position));
        holder.attendance.setOnCheckedChangeListener((buttonView, isChecked) -> attendance.set(position, isChecked));
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    // Method to get the list of attendance data
    public List<Boolean> getAttendanceList() {
        return attendance;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView memberName;
        CheckBox attendance;

        ViewHolder(View itemView) {
            super(itemView);
            memberName = itemView.findViewById(R.id.memberName);
            attendance = itemView.findViewById(R.id.attendanceCheckbox);
        }
    }
}
