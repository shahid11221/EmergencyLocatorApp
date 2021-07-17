package com.example.emergency;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class AddContacts extends AppCompatActivity {

    EditText etg;
    EditText etc;
    EditText etg1;
    EditText etc1;
    EditText etg2;
    EditText etc2;
    String currentUser,fullname,picture;


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final String TAG = "YOUR-TAG-NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts);

        etg= (EditText) findViewById(R.id.guardian1);
        etc= (EditText) findViewById(R.id.contact1);
        etg1= (EditText) findViewById(R.id.guardian2);
        etc1= (EditText) findViewById(R.id.contact2);
        etg2= (EditText) findViewById(R.id.guardian3);
        etc2= (EditText) findViewById(R.id.contact3);


        Bundle b = getIntent().getExtras();
        if(b!=null)
        {   currentUser=(String) b.get("email") ;
            fullname =(String) b.get("name");
            picture =(String) b.get("pic");

        }


        //Data Setting
        DocumentReference docRef = db.collection("users").document(currentUser);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        if(document.contains("guardian1")){
                            etg.setText(document.get("guardian1").toString());
                        }
                        if(document.contains("contact1")){
                            etc.setText(document.get("contact1").toString());
                        }
                        if(document.contains("guardian2")){
                            etg1.setText(document.get("guardian2").toString());
                        }
                        if(document.contains("contact2")){
                            etc1.setText(document.get("contact2").toString());
                        }
                        if(document.contains("guardian3")){
                            etg2.setText(document.get("guardian3").toString());
                        }
                        if(document.contains("contact3")){
                            etc2.setText(document.get("contact3").toString());
                        }

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        Intent i=new Intent(AddContacts.this,MainActivity.class);
        i.putExtra("email", currentUser);
        i.putExtra("name", fullname);
        i.putExtra("pic", picture);
        startActivity(i);
        finish();
    }

    public void contactsaveclick(View view) {

        final Boolean[] flag = {true};

        DocumentReference dateRef = db.collection("users").document(currentUser);

        if(!(etg.getText().toString().equals(""))){
            dateRef.update("guardian1", etg.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            flag[0] =false;
                            Toast toast=Toast.makeText(getApplicationContext(),"Guardian Change Failed (Database Error)",Toast.LENGTH_SHORT);
                            toast.show();
                            Log.w(TAG, "Error updating document", e);
                        }
                    });

        }
        if(!(etc.getText().toString().equals(""))){
            dateRef.update("contact1", etc.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            flag[0] =false;
                            Toast toast=Toast.makeText(getApplicationContext(),"Contact Change Failed (Database Error)",Toast.LENGTH_SHORT);
                            toast.show();
                            Log.w(TAG, "Error updating document", e);
                        }
                    });
        }
        if(!(etg1.getText().toString().equals(""))){
            dateRef.update("guardian2", etg1.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            flag[0] =false;
                            Toast toast=Toast.makeText(getApplicationContext(),"Guardian Change Failed (Database Error)",Toast.LENGTH_SHORT);
                            toast.show();
                            Log.w(TAG, "Error updating document", e);
                        }
                    });
        }
        if(!(etc1.getText().toString().equals(""))){
            dateRef.update("contact2", etc1.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            flag[0] =false;
                            Toast toast=Toast.makeText(getApplicationContext(),"Contact Change Failed (Database Error)",Toast.LENGTH_SHORT);
                            toast.show();
                            Log.w(TAG, "Error updating document", e);
                        }
                    });
        }
        if(!(etg2.getText().toString().equals(""))){
            dateRef.update("guardian3", etg2.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            flag[0] =false;
                            Toast toast=Toast.makeText(getApplicationContext(),"Guardian Change Failed (Database Error)",Toast.LENGTH_SHORT);
                            toast.show();
                            Log.w(TAG, "Error updating document", e);
                        }
                    });
        }
        if(!(etc2.getText().toString().equals("Tap to Add"))){
            dateRef.update("contact3", etc2.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            flag[0] =false;
                            Toast toast=Toast.makeText(getApplicationContext(),"Contact Change Failed (Database Error)",Toast.LENGTH_SHORT);
                            toast.show();
                            Log.w(TAG, "Error updating document", e);
                        }
                    });
        }
        if(flag[0]==true){
            Toast toast=Toast.makeText(getApplicationContext(),"Guardians Information Changed Successfully",Toast.LENGTH_SHORT);
            toast.show();
        }

    }


}
