package com.example.emergency;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShowDisasters extends AppCompatActivity {
    private RecyclerView myDisasterView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
   private FirestoreRecyclerAdapter firestoreRecyclerAdapter;
    String currentUser,fullname,picture;

    @Override
    protected void onStart() {
        super.onStart();
        firestoreRecyclerAdapter.startListening();
    }

    @Override
    public void onBackPressed() {
        Intent i=new Intent(this,MainActivity.class);
        i.putExtra("email", currentUser);
        i.putExtra("name", fullname);
        i.putExtra("pic", picture);
        startActivity(i);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firestoreRecyclerAdapter.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_disasters);

        loadIntent();
        LoadData();

    }

    private void loadIntent()
    {
        Bundle b = getIntent().getExtras();
        if(b!=null)
        {   currentUser=(String) b.get("email") ;
            fullname =(String) b.get("name");
            picture =(String) b.get("pic");

        }




    }


    public void LoadData()
    {    myDisasterView=findViewById(R.id.disasters_recycler_view);
        Query query=db.collection("disasters");
        FirestoreRecyclerOptions<DisasterModel> options=new FirestoreRecyclerOptions.Builder<DisasterModel>()
                .setQuery(query,DisasterModel.class).build();
         firestoreRecyclerAdapter= new FirestoreRecyclerAdapter<DisasterModel, DisastersViewHolder>(options) {
            @NonNull
            @Override
            public DisastersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.disaster_view_template,parent,false);

                return new DisastersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull DisastersViewHolder disastersViewHolder, int i, @NonNull DisasterModel disasterModel) {
                String dName=disasterModel.getDisastername();
                String dCity=disasterModel.getDisastercity();
                String dCatigory=disasterModel.getDisastercategory();
                String dDate=disasterModel.getDateofoccurance();
                String dImage= disasterModel.getDisasterpicture();
                disastersViewHolder.disasterName.setText(disasterModel.getDisastername());
                disastersViewHolder.disasterCity.setText("Disaster City:" + disasterModel.getDisastercity());
                disastersViewHolder.disasterCatigory.setText("Disaster Catigory:" +disasterModel.getDisastercategory());
                disastersViewHolder.disasterDate.setText("Disaster Date:" +disasterModel.getDateofoccurance());
                Glide.with(ShowDisasters.this).load(disasterModel.getDisasterpicture()).into(disastersViewHolder.disasterImage);

                disastersViewHolder.markSafeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        Map<String, Object> userdisaster = new HashMap<>();
                        userdisaster.put("username", currentUser);
                        userdisaster.put("disastername", dName);
                        userdisaster.put("safe", true);


                        // Add a new document with a generated ID
                        db.collection("userdisasters").document(currentUser+"("+dName+")")
                                .set(userdisaster)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast toast = Toast.makeText(getApplicationContext(), "You were marked Safe Successfully", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast toast = Toast.makeText(getApplicationContext(), "Marking Safe Failed (Database Error)", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });
                    }
                });
                disastersViewHolder.safeUsersBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(ShowDisasters.this, ShowSafe.class);
                        i.putExtra("disastername",dName);
                        startActivity(i);}
                });
                disastersViewHolder.disasterLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i=new Intent(ShowDisasters.this,ShowSingleDisaster.class);
                        i.putExtra("img",dImage);
                        i.putExtra("name",dName);
                        i.putExtra("city",dCity);
                        i.putExtra("date",dDate);
                        i.putExtra("catigory",dCatigory);
                        i.putExtra("email",currentUser);
                        i.putExtra("name",fullname);
                        i.putExtra("pic",picture);
                        startActivity(i);
                        finish();


                    }
                });

            }
        };
        myDisasterView.setHasFixedSize(true);
        myDisasterView.setLayoutManager(new LinearLayoutManager(this));
        myDisasterView.setAdapter(firestoreRecyclerAdapter);

    }
    private class DisastersViewHolder extends RecyclerView.ViewHolder
    {
        private LinearLayout disasterLayout;
        private  ImageView disasterImage;
        private TextView disasterName,disasterDate,disasterCity,disasterCatigory;
        private Button safeUsersBtn,markSafeBtn;
        public DisastersViewHolder(@NonNull View itemView) {
            super(itemView);
            disasterLayout=itemView.findViewById(R.id.disaster_template_layout);
            disasterImage=itemView.findViewById(R.id.disaster_template_img);
            disasterName=itemView.findViewById(R.id.disaster_template_name);
            disasterDate=itemView.findViewById(R.id.disaster_template_date);
            disasterCity=itemView.findViewById(R.id.disaster_template_city);
            disasterCatigory=itemView.findViewById(R.id.disaster_template_catigory);
            safeUsersBtn=itemView.findViewById(R.id.disaster_template_safe_users);
            markSafeBtn=itemView.findViewById(R.id.disaster_template_mark_safe);
        }
    }
}