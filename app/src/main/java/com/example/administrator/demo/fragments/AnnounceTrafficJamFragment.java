package com.example.administrator.demo.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.administrator.demo.R;
import com.example.administrator.demo.SlideAdapter;
import com.example.administrator.demo.activities.ReportTrafficJamActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class AnnounceTrafficJamFragment extends Fragment {

    ViewPager vpSlide;
    LinearLayout layoutDots;
    public AnnounceTrafficJamFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_announce_traffic_jam, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vpSlide =getActivity().findViewById(R.id.vpSlide);
        layoutDots = getActivity().findViewById(R.id.layoutDots);
        SlideAdapter adapter= new SlideAdapter(getActivity());

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
                    Toast.makeText(getActivity(),
                            ((SlideAdapter)vpSlide.getAdapter()).getChoose(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }
}
