package com.example.emergency;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ShowSingleDisaster extends AppCompatActivity {
    private ImageView singleDisasterImage;
    private Button singleDisasterShareBtn;
    private TextView singleDisasterName,singleDisasterCity,singleDisasterDate,singleDisasterCatigory;
    String image,name,city,date,catigory;
    String currentUser;
    String fullname,picture;



    @Override
    public void onBackPressed() {
        Intent i=new Intent(ShowSingleDisaster.this,ShowDisasters.class);
        i.putExtra("email", currentUser);
        i.putExtra("name", fullname);
        i.putExtra("pic", picture);
        startActivity(i);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_single_disaster);
        init();
        intentCode();
        putIntentData();
        shareDisasterData();



    }
    public void init()
    {
        singleDisasterImage=findViewById(R.id.single_disaster_img);
        singleDisasterName=findViewById(R.id.single_disaster_name);
        singleDisasterCity=findViewById(R.id.single_disaster_city);
        singleDisasterCatigory=findViewById(R.id.single_disaster_catigoryy);
        singleDisasterDate=findViewById(R.id.single_disaster_date);
        singleDisasterShareBtn=findViewById(R.id.single_disaster_share_btn);
    }
    public void intentCode()
    {
        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        if(b!=null)
        {
            image =(String) b.get("img");
            name =(String) b.get("name");
            city =(String) b.get("city");
            date =(String) b.get("date");
            catigory =(String) b.get("catigory");
            currentUser=(String) b.get("email") ;
            fullname =(String) b.get("name");
            picture =(String) b.get("pic");



        }


    }
    public void putIntentData()
    {
        Glide.with(ShowSingleDisaster.this).load(image).into(singleDisasterImage);
        singleDisasterName.setText(name);
        singleDisasterCity.setText(city);
        singleDisasterDate.setText(date);
        singleDisasterCatigory.setText(catigory);
    }
    public void shareDisasterData()
    {
        singleDisasterShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Share share = new Share(ShowSingleDisaster.this,ShowSingleDisaster.this,image,R.layout.loader_view,R.id.custom_loader,R.drawable.loading);




            }
        });
    }


}