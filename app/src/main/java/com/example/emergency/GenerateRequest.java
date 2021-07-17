package com.example.emergency;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class GenerateRequest extends AppCompatActivity {
    private static final int PERMISSION_REQUEST = 0;
    String[] phoneNo = new String[4];
    String msg;
    int k=0;
    double lat;
    double lon;
    boolean flag1=false;
    boolean flag2=false;
    String currentUser;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    String fullname,picture;

    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    public void onBackPressed() {
        Intent i=new Intent(GenerateRequest.this,MainActivity.class);
        i.putExtra("email", currentUser);
        i.putExtra("name", fullname);
        i.putExtra("pic", picture);
        startActivity(i);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_request2);
        CheckLocationEnabled();

        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        if(b!=null)
        {
            currentUser =(String) b.get("email");
            fullname =(String) b.get("name");
            picture =(String) b.get("pic");

        }


        LocationManager mLocationManager;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
               return;
        }


        mLocationManager   = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, locationListener);
        Location l = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        final Task<QuerySnapshot> snapshotTask = db.collection(("users"))
                .whereEqualTo("username", currentUser).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot qs2 = task.getResult();
                    if (qs2.getDocuments().size() > 0) {

                        for (int i = 0; i < qs2.getDocuments().size(); i++) {
                            DocumentSnapshot ds = qs2.getDocuments().get(i);

                            if (ds.contains("contact1")) {
                                phoneNo[0] = ds.get("contact1").toString();
                            } else {
                                phoneNo[0] = "";
                            }
                            if (ds.contains("contact2")) {
                                phoneNo[1] = ds.get("contact2").toString();
                            } else {
                                phoneNo[1] = "";
                            }
                            if (ds.contains("contact3")) {

                                phoneNo[2] = ds.get("contact3").toString();
                            } else {
                                phoneNo[2] = "";
                            }
                            if(flag2){

                            }
                            flag1=true;

                        }

                    } else {
                        Toast.makeText(GenerateRequest.this, "Size =0", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(GenerateRequest.this, "Error =1", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            lat = location.getLongitude();
            lon = location.getLatitude();
            if(flag1){

            }
            flag2=true;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public void generatebuttonclick(View view) {
        CheckLocationEnabled();
        if(gps_enabled && network_enabled)
        {
            if("".equals(phoneNo[2])){
                k=0;
                sendSMSMessage();
                k=1;
                sendSMSMessage();
            }else
            if("".equals(phoneNo[1])){
                k=0;
                sendSMSMessage();
            }else
            if("".equals(phoneNo[0])){
                Toast.makeText(GenerateRequest.this, "You have not registered any contact numbers", Toast.LENGTH_SHORT).show();
            }else{
                k=0;
                sendSMSMessage();
                k=1;
                sendSMSMessage();
                k=2;
                sendSMSMessage();
        }


        }

    }
    public void generatebuttonclickPolice(View view) {
        CheckLocationEnabled();
        if(gps_enabled && network_enabled)
        {
            phoneNo[k]="03364720003";
            sendSMSMessage();
        }




    }

    public void sendSMS() {
        try {

            msg ="User " + fullname + " has an emergency. \n This is his location Click here to track: https://maps.google.com/?q="+lon+","+lat+"";
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo[k], null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    protected void sendSMSMessage() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(GenerateRequest.this,
                        new String[]{Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQUEST);
          }
        else{
            sendSMS();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                msg = "User" + fullname + " has an emergency. \n This is his location Click here to track: https://maps.google.com/?q=" + lon + "," + lat + "";

                SmsManager smsManager = SmsManager.getDefault();


                smsManager.sendTextMessage(phoneNo[k], null, msg, null, null);
                Toast.makeText(getApplicationContext(), "SMS sent.",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "SMS failed, please try again.", Toast.LENGTH_LONG).show();

            }
        }

    }
    public void CheckLocationEnabled(){
        LocationManager lm = (LocationManager)GenerateRequest.this.getSystemService(Context.LOCATION_SERVICE);


        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            new AlertDialog.Builder(GenerateRequest.this)
                    .setMessage("Location Services Disabled")
                    .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            GenerateRequest.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                            startActivity(getIntent());
                        }
                    })
                    .show();
        }
    }


    public void generateButtonMedical(View view) {
        CheckLocationEnabled();
        if(gps_enabled && network_enabled)
        {
            phoneNo[k]="03364720003";
            sendSMSMessage();
        }
    }

    public void generateButtonFire(View view) {
        CheckLocationEnabled();
        if(gps_enabled && network_enabled)
        {
            phoneNo[k]="03364720003";
            sendSMSMessage();
        }
    }
}
