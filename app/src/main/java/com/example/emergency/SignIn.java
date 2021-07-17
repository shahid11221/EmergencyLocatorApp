package com.example.emergency;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


public class SignIn extends AppCompatActivity {
    String password;
    String fullname,picture,f_name,email;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final String TAG = "YOUR-TAG-NAME";
    private FirebaseAuth mAuth;
    EditText emailEt,passwordEt;
    boolean netConnection;
    ProgressDialog progressDialog;


    @Override
    public void onBackPressed() {
        AlertDialog.Builder ad =new AlertDialog.Builder(SignIn.this);
        ad.setTitle("Are you sure??");
        ad.setMessage("Agreeing to this message will close the app!");
        ad.setCancelable(false);
        ad.setIcon(R.drawable.ic_baseline_warning_24);
        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(DialogInterface dialog, int which) {

                finishAndRemoveTask();
            }
        });
        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        ad.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth = FirebaseAuth.getInstance();
        emailEt=findViewById(R.id.signInUser);
        passwordEt= findViewById(R.id.signInPass);


    }

    public void btnsaveonclick(View view) {
        CheckInternetConnection();
        progressDialog = new ProgressDialog(SignIn.this);
        progressDialog.setTitle(" Wait a second");
        progressDialog.setMessage("Logging You In");
        progressDialog.show();


        email=emailEt.getText().toString().trim();
        password=passwordEt.getText().toString().trim();
        if(email.isEmpty()){
            emailEt.setError("Email Required");
        }else if(password.isEmpty()){
            passwordEt.setError("password Required");
        }else if ((!Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            emailEt.setError("Wrong format");
        }else if(netConnection){


            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(SignIn.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){


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
                                        Intent splashIntent = new Intent(SignIn.this, MainActivity.class);
                                        splashIntent.putExtra("pic",picture);
                                        splashIntent.putExtra("name",fullname);
                                        splashIntent.putExtra("email",email);
                                        startActivity(splashIntent);
                                        progressDialog.dismiss();
                                        finish();


                                    }
                                }

                            }

                        });


                    }

                    else{
                        String error = task.getException().toString();
                        Toast.makeText(SignIn.this, error, Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
        else {
            Toast.makeText(SignIn.this, "No Internet found", Toast.LENGTH_SHORT).show();
        }



    }

    private void CheckInternetConnection() {
        netConnection=false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            netConnection = true;
        }
        else{
            netConnection = false;
        }
    }


    public void CreateAccount(View view) {
        Intent i=new Intent(this,SignUp.class);
        startActivity(i);
        finish();
    }
}
