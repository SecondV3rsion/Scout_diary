package si.uni_lj.fe.tnuv.scoutdiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.InputStream;


public class GroupOverviewActivity extends AppCompatActivity {

    ImageView groupImg;
    TextView groupName;

    ImageButton selectIconButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_overview);

        groupImg = findViewById(R.id.slika_voda);
        groupName = findViewById(R.id.ime_voda);

        selectIconButton = findViewById(R.id.btn_izberi_sliko_sestanka);

        // TO DO: dodaj sliko "prosim izberi sliko sestanka"
        selectIconButton.setImageResource(R.drawable.ic_launcher_background);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String groupNameString = PreferencesUtil.loadGroupName(this);
        groupName.setText(groupNameString);
        Uri imageUri = PreferencesUtil.loadImageUri(this);
        groupImg.setImageURI(imageUri);
    }
}
