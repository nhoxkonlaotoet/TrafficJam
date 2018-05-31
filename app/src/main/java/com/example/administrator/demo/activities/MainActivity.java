package com.example.administrator.demo.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.method.CharacterPickerDialog;
import android.view.View;

import com.example.administrator.demo.R;

public class MainActivity extends AppCompatActivity {
    CardView cvMap, cvAnnouce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cvMap = findViewById(R.id.cvMap);
        cvAnnouce = findViewById(R.id.cvAnnouce);
        setCvMapClick();
        setCvAnnouceReportClick();
    }

    public void setCvMapClick() {
        cvMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                intent.putExtra("action", "viewmap");
                startActivity(intent);
            }
        });
    }
    public void setCvAnnouceReportClick() {
        cvAnnouce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                intent.putExtra("action", "announce");
                startActivity(intent);

            }
        });
    }
}
