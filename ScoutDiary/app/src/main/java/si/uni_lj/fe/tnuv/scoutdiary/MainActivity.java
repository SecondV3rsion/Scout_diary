package si.uni_lj.fe.tnuv.scoutdiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnNovVod = findViewById(R.id.btn_nov_vod);

        btnNovVod.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        Intent intent= new Intent(this,EditVodActivity.class);
        startActivity(intent);
    }
}