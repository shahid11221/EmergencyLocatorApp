package com.example.emergency;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2000;
    boolean netConnection;
    String fullname,picture,f_name,email;
    public static final String TAG = "YOUR-TAG-NAME";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    boolean gps_enabled = false;
    boolean network_enabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.spin_kit);
        Sprite fadingCircle = new FadingCircle();
        progressBar.setIndeterminateDrawable(fadingCircle);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        final FirebaseUser currentUser = mAuth.getCurrentUser();

        CheckInternetConnection();
        CheckLocationEnabled();
        if(gps_enabled && network_enabled)
        {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if(netConnection)
                    {
                        if(currentUser == null) {

                            Intent splashIntent = new Intent(SplashScreen.this, SignIn.class);
                            startActivity(splashIntent);
                            finish();
                        }
                        else {
                            email=currentUser.getEmail();
                            DocumentReference docRef = db.collection("users").document(email);
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful())
                                    {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            email=(document.get("username").toString());
                                            fullname=(document.get("fullname").toString());
                                            picture=(document.get("profilepicture").toString());
                                            Intent splashIntent = new Intent(SplashScreen.this, MainActivity.class);
                                            splashIntent.putExtra("pic",picture);
                                            splashIntent.putExtra("name",fullname);
                                            splashIntent.putExtra("email",email);
                                            startActivity(splashIntent);
                                            finish();


                                        }
                                    }

                                }

                            });


                        }
                    }
                    else
                    {
                        AlertDialog.Builder ad =new AlertDialog.Builder(SplashScreen.this);
                        ad.setTitle("Oops");
                        ad.setMessage("No Internet Found");
                        ad.setIcon(R.drawable.ic_baseline_wifi_off_24);
                        ad.setPositiveButton("Okay!", new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                finishAndRemoveTask();
                            }
                        });
                        ad.show();
                    }

                }
            },SPLASH_TIME_OUT);
        }






    }



    private void CheckInternetConnection() {
        netConnection=false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            netConnection = true;
        }
        else{
            netConnection = false;
        }
    }
    private void CheckLocationEnabled(){
        LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);


        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            new AlertDialog.Builder(SplashScreen.this)
                    .setMessage("Location Services Disabled")
                    .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            SplashScreen.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                            startActivity(getIntent());
                        }
                    })
                    .setNeutralButton("Exit App", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        }
    }
}