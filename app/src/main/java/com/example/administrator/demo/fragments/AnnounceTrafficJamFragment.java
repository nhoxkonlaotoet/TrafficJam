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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
    Button btnConfirm,btnBack,btnNext;
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
    void mapping()
    {
        btnConfirm = getActivity().findViewById(R.id.btnConfirm);
        btnBack=getActivity().findViewById(R.id.btnBack);
        btnNext=getActivity().findViewById(R.id.btnNext);
        vpSlide =getActivity().findViewById(R.id.vpSlide);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapping();
        btnConfirm.setVisibility(View.INVISIBLE);

        SlideAdapter adapter= new SlideAdapter(getActivity());
        if(adapter.getCount()<=1)
            btnNext.setVisibility(View.INVISIBLE);

        vpSlide.setAdapter(adapter);
        setOnPageChangeListtener();
        setBtnConfirmClick();
        setBtnBackClick();
        setBtnNextClick();
    }
    void setOnPageChangeListtener()
    {
        vpSlide.addOnPageChangeListener( new ViewPager.OnPageChangeListener(){

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                slideIndex=position;


                switch (position)
                {
                    case 0:
                        btnConfirm.setVisibility(View.INVISIBLE);
                        btnNext.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        String choose=((SlideAdapter) vpSlide.getAdapter()).getChoose();

                        if(choose.equals(getString(R.string.LEVEL1)))
                            level=1;
                        else
                        if(choose.equals(getString(R.string.LEVEL2)))
                            level=2;
                        else
                        if (choose.equals(getString(R.string.LEVEL3)))
                            level=3;
                        btnConfirm.setVisibility(View.VISIBLE);
                        btnNext.setVisibility(View.INVISIBLE);
                        if(imgvCapture==null) {
                            imgvCapture = ((SlideAdapter) vpSlide.getAdapter()).imgvCapture;
                            Toast.makeText(getActivity(), imgvCapture + "", Toast.LENGTH_SHORT).show();
                            imgvCapture.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                                }
                            });
                        }
                        break;
                    default:break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                if(imgvCapture!=null) {
                    imgvCapture.setImageBitmap(photo);
                    imageViewToBytes(imgvCapture);

                }
        }
    }

    void setBtnConfirmClick()
    {
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(myLocation!=null) {

                   StorageReference mStorageRef;
                   mStorageRef = FirebaseStorage.getInstance().getReference();
                   StorageReference mountainsRef = mStorageRef.child("mountains.jpg");

                   imgvCapture.setDrawingCacheEnabled(true);
                   imgvCapture.buildDrawingCache();
                   Bitmap bitmap = ((BitmapDrawable) imgvCapture.getDrawable()).getBitmap();
                   ByteArrayOutputStream baos = new ByteArrayOutputStream();
                   bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                   byte[] data = baos.toByteArray();

                   Toast.makeText(getActivity(), data.length, Toast.LENGTH_SHORT).show();
//                   UploadTask uploadTask = mountainsRef.putBytes(data);
//                   uploadTask.addOnFailureListener(new OnFailureListener() {
//                       @Override
//                       public void onFailure(@NonNull Exception exception) {
//                           // Handle unsuccessful uploads
//                           Toast.makeText(getActivity(), exception.toString(), Toast.LENGTH_SHORT).show();
//                       }
//                   }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                       @Override
//                       public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                           // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
//                           // ...
//                           Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();
//                       }
//                   });


//                   FirebaseDatabase database = FirebaseDatabase.getInstance();
//                   DatabaseReference myRef = database.getReference("announces");
//                   String id = myRef.push().getKey();
//                   String deviceId = Settings.Secure.getString(getContext().getContentResolver(),
//                           Settings.Secure.ANDROID_ID);
//
//                   myRef.child(id).setValue(new Announce(id, new MyLatlng(new LatLng(myLocation.getLatitude(),myLocation.getLongitude())),
//                           level,deviceId, new Timestamp(System.currentTimeMillis()),imageViewToBytes(imgvCapture)));
                    getActivity().finish();
               }
            }
        });
    }
    void setBtnBackClick()
    {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(slideIndex==0)
                   getActivity().finish();
               vpSlide.setCurrentItem(slideIndex-1);
            }
        });
    }
    void setBtnNextClick()
    {
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vpSlide.setCurrentItem(slideIndex+1);
            }
        });
    }
    @Override
    public void onDataChanged(Object data) {
        myLocation = (Location)data;
        Toast.makeText(getActivity(),"onDataChanged"+((Location)data).getLatitude()+", "+ ((Location)data).getLongitude(), Toast.LENGTH_SHORT).show();

    }
    public byte[] imageViewToBytes(ImageView imgv){

        BitmapDrawable drawable = (BitmapDrawable) imgv.getDrawable();
        Bitmap bmp = drawable.getBitmap();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        return byteArray;
    }
    public Bitmap bytesToBitmap(byte[] bytes)
    {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
