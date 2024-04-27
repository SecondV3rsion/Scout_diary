package si.uni_lj.fe.tnuv.scoutdiary;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
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

import java.util.ArrayList;

public class EditVodActivity extends AppCompatActivity {

    // Seznam za shranjevanje imen članov
    private ArrayAdapter<String> adapter;
    ImageView slikaVoda;

    ActivityResultLauncher<Intent> resultLauncher;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_vod);

        LinearLayout membersListView = findViewById(R.id.linearLayoutMembersList);
        Button addMemberButton = findViewById(R.id.buttonAddMember);
        ImageButton selectIconButton = findViewById(R.id.imageButtonSelectIcon);
        slikaVoda = findViewById(R.id.slika_voda);

        ListView listView = new ListView(this);
        listView.setAdapter(adapter);
        membersListView.addView(listView);

        registerResult();

        addMemberButton.setOnClickListener(v -> addMember());
        selectIconButton.setOnClickListener(v -> selectIcon());
    }

    private void selectIcon(){
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        resultLauncher.launch(intent);
    }

    private void registerResult(){
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        try {
                            Uri imageUri = result.getData().getData();
                            slikaVoda.setImageURI(imageUri);

                        }catch(Exception e){
                            Toast.makeText(EditVodActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                        }
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
                    Toast.makeText(EditVodActivity.this, "Vnesite ime člana", Toast.LENGTH_SHORT).show();
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
