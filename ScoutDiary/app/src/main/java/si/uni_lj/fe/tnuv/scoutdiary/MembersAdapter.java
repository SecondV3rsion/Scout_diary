package si.uni_lj.fe.tnuv.scoutdiary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MemberViewHolder> {

    private List<String> members;
    private Context context;

    public MembersAdapter(Context context, List<String> members) {
        this.context = context;
        this.members = members;
    }

    @Override
    public MemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.member_item, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MemberViewHolder holder, int position) {
        String member = members.get(position);
        holder.memberTextView.setText(member);
        holder.removeButton.setOnClickListener(v -> {
            members.remove(position);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView memberTextView;
        Button removeButton;

        public MemberViewHolder(View itemView) {
            super(itemView);
            memberTextView = itemView.findViewById(R.id.memberName);
            removeButton = itemView.findViewById(R.id.removeMemberButton);
        }
    }
}

