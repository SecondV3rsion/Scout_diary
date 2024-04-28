package si.uni_lj.fe.tnuv.scoutdiary;

import static android.widget.Toast.*;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class EditVodActivity extends AppCompatActivity {

    // Seznam za shranjevanje imen članov
    private ArrayAdapter<String> adapter;

    Uri imageUri = null;
    EditText ime_voda;
    ImageButton selectIconButton;
    ActivityResultLauncher<Intent> resultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_vod);

        ime_voda = findViewById(R.id.editTextImeVoda);
        LinearLayout membersListView = findViewById(R.id.linearLayoutMembersList);
        Button addMemberButton = findViewById(R.id.buttonAddMember);
        Button saveButton = findViewById(R.id.buttonSave);
        selectIconButton = findViewById(R.id.btn_izberi_sliko_voda);

        // TO DO: dodaj sliko "prosim izberi sliko voda"
        selectIconButton.setImageResource(R.drawable.ic_launcher_background);

        ListView listView = new ListView(this);
        listView.setAdapter(adapter);
        membersListView.addView(listView);

        registerResult();

        addMemberButton.setOnClickListener(v -> addMember());
        selectIconButton.setOnClickListener(v -> selectIcon());
        saveButton.setOnClickListener(v -> saveGroup());
    }

    private void saveGroup() {
        boolean resultName = saveGroupName();
        boolean resultImg = saveGroupImage();

        if (resultImg && resultName) {
            Toast.makeText(this, R.string.saved_group_toast, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish(); // Consider calling finish() to remove this activity from the back stack.
        } else {
            // Provide feedback if something is missing
            if (!resultName) {
                Toast.makeText(this, R.string.no_name_toast, Toast.LENGTH_SHORT).show();
            }
            if (!resultImg) {
                Toast.makeText(this, R.string.no_img_toast, LENGTH_SHORT).show();
            }
        }
    }

    public boolean saveGroupImage() {
        if (imageUri != null) {
            PreferencesUtil.saveImageUri(this, imageUri);
            Log.d("EditVodActivity", "Image URI saved: " + imageUri.toString());
            return true;
        } else {
            Log.d("EditVodActivity", "saveGroup: No image URI to save");
            return false;
        }
    }

    public boolean saveGroupName() {
        if (ime_voda != null && !ime_voda.getText().toString().trim().isEmpty()) {
            String groupNameString = ime_voda.getText().toString();
            PreferencesUtil.saveGroupName(this, groupNameString);
            return true;
        } else {
            return false;
        }
    }


    private void selectIcon(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT); // Use ACTION_OPEN_DOCUMENT for persistent access
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        resultLauncher.launch(intent);
    }

    private void registerResult() {
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            try {
                                getContentResolver().takePersistableUriPermission(
                                        uri,
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                );
                                imageUri = uri; // Store the URI
                                selectIconButton.setImageURI(imageUri);
                            } catch (SecurityException e) {
                                Log.e("EditVodActivity", "SecurityException: Failed to obtain persistable URI permission", e);
                            }
                        }
                    } else {
                        makeText(EditVodActivity.this, "No image selected", LENGTH_SHORT).show();
                    }
                }
        );
    }


    private void addMember() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.vpisi_ime_clana);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton(R.string.dodaj, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String memberName = input.getText().toString().trim();
                if (!memberName.isEmpty()) {
                    // Ustvarimo novo vrstico z imenom člana in gumbom "x"
                    createMemberRow(memberName);
                } else {
                    makeText(EditVodActivity.this, "Vnesite ime člana", LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(R.string.preklici, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    private void createMemberRow(String memberName) {
        // Create a new row for the member
        LinearLayout rowLayout = new LinearLayout(EditVodActivity.this);
        rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);

        // Add TextView for member name
        TextView memberTextView = new TextView(EditVodActivity.this);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        textParams.weight = 1; 
        memberTextView.setLayoutParams(textParams);
        memberTextView.setText(memberName);
        rowLayout.addView(memberTextView);

        // Add Button for removing member
        Button removeButton = new Button(EditVodActivity.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.END;
        removeButton.setText("x");
        removeButton.setBackgroundColor(Color.WHITE);
        removeButton.setTextColor(Color.RED);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the row from the LinearLayout
                ((LinearLayout) rowLayout.getParent()).removeView(rowLayout);
            }
        });
        rowLayout.addView(removeButton);

        // Add the row to the LinearLayout
        LinearLayout membersListView = findViewById(R.id.linearLayoutMembersList);
        membersListView.addView(rowLayout);
    }
}
