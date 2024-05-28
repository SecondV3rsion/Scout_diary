package si.uni_lj.fe.tnuv.scoutdiary;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class EditGroupActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MembersAdapter adapter;
    private List<String> members = new ArrayList<>();
    private PreferencesUtil preferencesUtil;
    private EditText groupNameEditText;
    private ShapeableImageView selectIconButton;
    private TextView selectIconText;
    private ImageSelector imageSelector;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);
        initViews();
        setupListeners();

        isEditMode = getIntent().getBooleanExtra("isEditMode", false);
        if (isEditMode) {
            loadGroupData();
        }
    }

    private void initViews() {
        groupNameEditText = findViewById(R.id.editTextImeVoda);
        Button addMemberButton = findViewById(R.id.buttonAddMember);
        Button saveButton = findViewById(R.id.buttonSave);
        selectIconButton = findViewById(R.id.btn_izberi_sliko_voda);
        selectIconText = findViewById(R.id.text_izberi_sliko_voda);

        recyclerView = findViewById(R.id.recyclerViewMembers);
        adapter = new MembersAdapter(this, members);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        preferencesUtil = new PreferencesUtil(this);

        // Initialize ImageSelector with the callback to handle selected images
        imageSelector = new ImageSelector(this, new ImageSelector.ImageSelectedCallback() {
            @Override
            public void onImageSelected(Uri uri) {
                selectIconText.setVisibility(View.INVISIBLE);
                selectIconButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
                selectIconButton.setImageURI(uri); // Update UI
                preferencesUtil.saveImageUri("group_img", uri); // Save URI to preferences
            }

            @Override
            public void onSelectionError(String message) {
                Toast.makeText(EditGroupActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        findViewById(R.id.buttonAddMember).setOnClickListener(v -> showAddMemberDialog());
        selectIconButton.setOnClickListener(v -> imageSelector.openGallery());
        findViewById(R.id.buttonSave).setOnClickListener(v -> saveGroup());
    }

    private void loadGroupData() {
        String groupName = preferencesUtil.loadGroupName();
        if (groupName != null) {
            groupNameEditText.setText(groupName);
        }

        Uri imageUri = preferencesUtil.loadImageUri("group_img");
        if (imageUri != null) {
            selectIconText.setVisibility(View.INVISIBLE);
            selectIconButton.setImageURI(imageUri);
            selectIconButton.setScaleType(ImageView.ScaleType.CENTER_CROP);

        }

        List<String> savedMembers = preferencesUtil.loadGroupMembers();
        if (savedMembers != null) {
            members.addAll(savedMembers);
            adapter.notifyDataSetChanged();
        }
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
                Toast.makeText(this, R.string.prosim_vpisi_ime_clana, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.cancel_text, (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void saveGroup() {
        String groupName = groupNameEditText.getText().toString().trim();
        Uri groupImageUri = preferencesUtil.loadImageUri("group_img");

        if (groupName.isEmpty()) {
            Toast.makeText(this, "prosim vpiši ime voda", Toast.LENGTH_SHORT).show();
            return;
        }

        if (groupImageUri == null) {
            Toast.makeText(this, "prosim izberi sliko", Toast.LENGTH_SHORT).show();
            return;
        }

        if (members.isEmpty()) {
            Toast.makeText(this, "prosim dodaj clana", Toast.LENGTH_SHORT).show();
            return;
        }

        preferencesUtil.saveGroupName(groupName);
        preferencesUtil.saveGroupMembers(members);
        Toast.makeText(this, R.string.saved_group_toast, Toast.LENGTH_SHORT).show();
        finishAndReturnHome();
    }

    private void finishAndReturnHome() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}

