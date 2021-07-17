package com.example.emergency;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ShowSafe extends AppCompatActivity {


    ArrayList<String> names1;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String disastersafe;
    ListView lv;

    @Override
    public void onBackPressed() {
        /*Intent i=new Intent(ShowSafe.this,RespondDisaster.class);
        startActivity(i);*/
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_safe);
        init();



        db.collection(("userdisasters")).whereEqualTo("disastername", disastersafe).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot qs2 = task.getResult();
                    if (qs2.getDocuments().size() > 0) {
                        for (int i = 0; i < qs2.getDocuments().size(); i++) {
                            DocumentSnapshot ds = qs2.getDocuments().get(i);
                            names1.add(ds.get("username").toString());
                        }

                    }else{
                        Toast.makeText(ShowSafe.this, "Size =0"+disastersafe, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(ShowSafe.this, "Error =1", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        List<String> names1 = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.disasterlist, R.id.textView, names1);

        Handler handler = new Handler();
        int delay = 1000; //milliseconds



        lv.setAdapter(arrayAdapter);

        handler.postDelayed(new Runnable(){
            public void run(){
                arrayAdapter.notifyDataSetChanged();
                //do something
                handler.postDelayed(this, delay);
            }
        }, delay);
    }
    public void init()
    {
        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        if(b!=null)
        {
            disastersafe =(String) b.get("disastername");

        }



        names1 = new ArrayList<>();
        lv= findViewById(R.id.listview);
    }
}
