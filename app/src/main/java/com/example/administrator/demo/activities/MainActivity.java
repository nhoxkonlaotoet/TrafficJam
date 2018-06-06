package com.example.administrator.demo.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.method.CharacterPickerDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.administrator.demo.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    CardView cvMap, cvAnnounce, cvExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cvMap = findViewById(R.id.cvMap);
        cvAnnounce = findViewById(R.id.cvAnnounce);
        cvExit=findViewById(R.id.cvExit);
        setCvMapClick();
        setCvAnnouceClick();
        setCvExitClick();


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
    public void setCvAnnouceClick() {
        cvAnnounce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                intent.putExtra("action", "announce");
                startActivity(intent);

            }
        });
    }
    public void setCvExitClick()
    {
        cvExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                System.exit(0);
            }
        });
    }
}
