package com.example.administrator.demo.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.demo.IFragmentManager;
import com.example.administrator.demo.R;
import com.example.administrator.demo.SlideAdapter;
import com.example.administrator.demo.models.Announce;
import com.example.administrator.demo.models.MyLatlng;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class AnnounceTrafficJamFragment extends Fragment implements IFragmentManager {
    int CAMERA_REQUEST = 100;
    ViewPager vpSlide;
    Button btnConfirm, btnBack, btnNext;
    Location myLocation;
    int level, slideIndex;
    ImageView imgvCapture;

    public AnnounceTrafficJamFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_announce_traffic_jam, container, false);
    }

    void mapping() {
        btnConfirm = getActivity().findViewById(R.id.btnConfirm);
        btnBack = getActivity().findViewById(R.id.btnBack);
        btnNext = getActivity().findViewById(R.id.btnNext);
        vpSlide = getActivity().findViewById(R.id.vpSlide);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapping();
        btnConfirm.setVisibility(View.INVISIBLE);

        SlideAdapter adapter = new SlideAdapter(getActivity());
        if (adapter.getCount() <= 1)
            btnNext.setVisibility(View.INVISIBLE);

        vpSlide.setAdapter(adapter);
        setOnPageChangeListtener();
        setBtnConfirmClick();
        setBtnBackClick();
        setBtnNextClick();
        getAllTrafficJams();
    }

    void setOnPageChangeListtener() {
        vpSlide.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                slideIndex = position;


                switch (position) {
                    case 0:
                        btnConfirm.setVisibility(View.INVISIBLE);
                        btnNext.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        String choose = ((SlideAdapter) vpSlide.getAdapter()).getChoose();

                        if (choose.equals(getString(R.string.LEVEL1)))
                            level = 1;
                        else if (choose.equals(getString(R.string.LEVEL2)))
                            level = 2;
                        else if (choose.equals(getString(R.string.LEVEL3)))
                            level = 3;
                        btnConfirm.setVisibility(View.VISIBLE);
                        btnNext.setVisibility(View.INVISIBLE);
                        if (imgvCapture == null) {
                            imgvCapture = ((SlideAdapter) vpSlide.getAdapter()).imgvCapture;
                            imgvCapture.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                                }
                            });
                        }
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            if (imgvCapture != null) {
                imgvCapture.setImageBitmap(photo);
                imgvCapture.setTag("added");
            }
        }
    }

    void setBtnConfirmClick() {
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myLocation != null) {
                    if(imgvCapture.getTag()!=null && imgvCapture.getTag().toString().equals("added"))
                        sendAnnounce(imgvCapture);
                    else
                        sendAnnounce();
                }
            }
        });
    }
    void sendAnnounce()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("announces");
        String id = myRef.push().getKey();
        String deviceId = Settings.Secure.getString(getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        myRef.child(id).setValue(new Announce(id, new MyLatlng(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())),
                level, deviceId, new Timestamp(System.currentTimeMillis()).getTime(), null))
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getActivity(), "Cám ơn bạn đã báo điểm kẹt xe", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            }
        });
    }

    void sendAnnounce(ImageView imgv) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("announces");
        final String id = myRef.push().getKey();

        StorageReference mStorageRef;
        mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference mountainsRef = mStorageRef.child(id + ".png");

        byte[] data = imgvToBytes(imgv);

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getActivity(), "upload failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                String deviceId = Settings.Secure.getString(getContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                myRef.child(id).setValue(new Announce(id, new MyLatlng(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())),
                        level, deviceId, new Timestamp(System.currentTimeMillis()).getTime(), taskSnapshot.getDownloadUrl().toString()))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity(), "Cám ơn bạn đã báo điểm kẹt xe", Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();
                    }
                });
            }
        });
    }
    public void getAllTrafficJams() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("announces");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(myLocation==null)
                    return;
                float[] result= new float[1];
                for (DataSnapshot Snapshot1 : dataSnapshot.getChildren()) {
                    Announce p = Snapshot1.getValue(Announce.class);
                    Location.distanceBetween(p.location.latitude,p.location.longitude,myLocation.getLatitude(),myLocation.getLongitude(),result);
                    if(result[0]<=p.level*15)
                    {
                        Toast.makeText(getActivity(), "Bạn đã trong vùng kẹt xe", Toast.LENGTH_LONG).show();
                        getActivity().onBackPressed();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("errrrorrrrrrrrrrrrrrr", databaseError.toString());
            }


        });

    }
    void setBtnBackClick() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (slideIndex == 0)
                    getActivity().finish();
                vpSlide.setCurrentItem(slideIndex - 1);
            }
        });
    }

    void setBtnNextClick() {
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vpSlide.setCurrentItem(slideIndex + 1);
            }
        });
    }

    @Override
    public void onDataChanged(Object data) {
      try {
          myLocation = (Location) data;

      }
      catch (Exception e){}
        //Toast.makeText(getActivity(), "onDataChanged" + ((Location) data).getLatitude() + ", " + ((Location) data).getLongitude(), Toast.LENGTH_SHORT).show();

    }
    public byte[] imgvToBytes(ImageView imgv)
    {
        imgv.setDrawingCacheEnabled(true);
        imgv.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imgv.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        return data;
    }
}
