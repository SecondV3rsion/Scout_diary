package si.uni_lj.fe.tnuv.scoutdiary;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EditGroupActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MembersAdapter adapter;
    private List<String> members = new ArrayList<>();
    private PreferencesUtil preferencesUtil;
    private EditText groupNameEditText;
    private ImageButton selectIconButton;
    private ImageSelector imageSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);
        initViews();
        setupListeners();
    }

    private void initViews() {
        groupNameEditText = findViewById(R.id.editTextImeVoda);
        Button addMemberButton = findViewById(R.id.buttonAddMember);
        Button saveButton = findViewById(R.id.buttonSave);
        selectIconButton = findViewById(R.id.btn_izberi_sliko_voda);

        recyclerView = findViewById(R.id.recyclerViewMembers);
        adapter = new MembersAdapter(this, members);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        preferencesUtil = new PreferencesUtil(this);

        // Initialize ImageSelector with the callback to handle selected images
        imageSelector = new ImageSelector(this, new ImageSelector.ImageSelectedCallback() {
            @Override
            public void onImageSelected(Uri uri) {
                selectIconButton.setImageURI(uri); // Update UI
                preferencesUtil.saveImageUri("group_img",uri); // Save URI to preferences
            }

            @Override
            public void onSelectionError(String message) {
                Toast.makeText(EditGroupActivity.this, message, LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        findViewById(R.id.buttonAddMember).setOnClickListener(v -> showAddMemberDialog());
        selectIconButton.setOnClickListener(v -> imageSelector.openGallery());
        findViewById(R.id.buttonSave).setOnClickListener(v -> saveGroup());
    }

    private void showAddMemberDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.vpisi_ime_člana);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton(R.string.add_text, (dialog, which) -> {
            String memberName = input.getText().toString().trim();
            if (!memberName.isEmpty()) {
                members.add(memberName);
                adapter.notifyItemInserted(members.size() - 1);
            } else {
                Toast.makeText(this, R.string.prosim_vpisi_ime_clana, LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.cancel_text, (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void saveGroup() {
        String groupName = groupNameEditText.getText().toString().trim();
        preferencesUtil.saveGroupName(groupName);
        preferencesUtil.saveGroupMembers(members);
        Toast.makeText(this, R.string.saved_group_toast, LENGTH_SHORT).show();
        finishAndReturnHome();
    }

    private void finishAndReturnHome() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
