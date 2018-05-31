package com.example.administrator.demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 30/05/2018.
 */

public class SlideAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;
    public Integer level=0;
    RadioGroup rg;
    public  SlideAdapter(Context context)
    {
        this.context=context;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (RelativeLayout) object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater =(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view;
        if(position == 0) {
            view = layoutInflater.inflate(R.layout.slide_report_0_layout, container, false);
        }
        else {
            view = layoutInflater.inflate(R.layout.slide_report_1_layout, container, false);

        rg=  container.findViewById(R.id.rdgLevel);
        }
        container.addView(view);
        return view;
    }

    public String getChoose()
    {
        RadioButton rbtn = rg.findViewById(rg.getCheckedRadioButtonId());
        return rbtn.getText().toString();
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout)object);
    }
}
