package com.example.administrator.demo.activities;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.administrator.demo.R;
import com.example.administrator.demo.SlideAdapter;

public class ReportTrafficJamActivity extends AppCompatActivity {
    ViewPager vpSlide;
    LinearLayout layoutDots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_traffic_jam);
        vpSlide =findViewById(R.id.vpSlide);
        layoutDots = findViewById(R.id.layoutDots);
        SlideAdapter adapter= new SlideAdapter(this);

        vpSlide.setAdapter(adapter);
        setOnPageChange();


    }

    void setOnPageChange()
    {
        vpSlide.addOnPageChangeListener( new ViewPager.OnPageChangeListener(){

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                if(position==1)
                    Toast.makeText(ReportTrafficJamActivity.this,
                            ((SlideAdapter)vpSlide.getAdapter()).getChoose(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }
}
