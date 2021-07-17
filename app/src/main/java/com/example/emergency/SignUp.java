package com.example.emergency;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

/**
 * A login screen that offers login via email/password.
 */
public class SignUp extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final String TAG = "YOUR-TAG-NAME";
    boolean netConnection;
    private FirebaseAuth mAuth;
    String emailString,name,pass, c_pass;
    EditText passwordEt, confirmPasswordEt, emailEt,nameEt;
    ProgressDialog progressDialog;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        passwordEt = findViewById(R.id.signup_pass);
        confirmPasswordEt = findViewById(R.id.signuppass2);
        emailEt = findViewById(R.id.signup_user);
       nameEt = findViewById(R.id.signup_name);

    }


    @Override
    public void onBackPressed() {
        Intent i = new Intent(SignUp.this, SignIn.class);
        startActivity(i);
        finish();

    }

    public void btn1Click(View view) {
        CheckInternetConnection();
        userRegister();


    }

    public void btnsigninclick(View view) {
        Intent i = new Intent(this, SignIn.class);
        this.finish();
        startActivity(i);
    }

    private void CheckInternetConnection() {
        netConnection = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            netConnection = true;
        } else {
            netConnection = false;
        }
    }

    private void userRegister() {
        emailString = emailEt.getText().toString().trim();
        pass = passwordEt.getText().toString().trim();
        name = nameEt.getText().toString().trim();
        c_pass = confirmPasswordEt.getText().toString();
        if (netConnection) {
            if (emailString.equals("")) {
                emailEt.setError("Email required!");
            } else if (pass.equals("")) {
                passwordEt.setError("Name Required!");
            } else if (name.equals("")) {
                nameEt.setError("Password Required!");
            }
            else if (pass.length() < 6) {
                passwordEt.setError("At least 6 character password");
            } else if ((!Patterns.EMAIL_ADDRESS.matcher(emailString).matches())) {
                emailEt.setError("Wrong format");
            } else if (!(pass.equals(c_pass))) {
                confirmPasswordEt.setError("password not match");
            } else {
                progressDialog=new ProgressDialog(SignUp.this);
                progressDialog.setTitle("Checking Credentials");
                progressDialog.show();

                mAuth.createUserWithEmailAndPassword(emailString, pass).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            CollectionReference usersRef = db.collection("users");
                            Query query = usersRef.whereEqualTo("username", emailString);
                            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        Log.d(TAG, "User do not Exist");
                                        if (passwordEt.getText().toString().equals(confirmPasswordEt.getText().toString())) {
                                            DbFunctions df = new DbFunctions();
                                            df.addUser(emailEt.getText().toString(), passwordEt.getText().toString());
                                            DocumentReference dateRef = db.collection("users").document(emailEt.getText().toString());
                                            dateRef.update("fullname", nameEt.getText().toString())
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            AlertDialog.Builder ad = new AlertDialog.Builder(SignUp.this);
                                                            ad.setTitle("Account Created");
                                                            ad.setMessage("Welcome!");
                                                            ad.setCancelable(false);
                                                            ad.setIcon(R.drawable.ic_baseline_check_24);
                                                            ad.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                    Intent i = new Intent(SignUp.this, PersonalInfo.class);
                                                                    SignUp.this.finish();
                                                                    startActivity(i);
                                                                }
                                                            });
                                                            progressDialog.dismiss();
                                                            ad.show();

                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast toast=Toast.makeText(getApplicationContext(),"Name dident get saved",Toast.LENGTH_SHORT);
                                                            toast.show();
                                                            Log.w(TAG, "Error updating document", e);
                                                        }
                                                    });



                                        }
                                        else {
                                            Toast.makeText(SignUp.this, "Something Went Wrong \n Please try again", Toast.LENGTH_SHORT).show();

                                        }
                                    }


                                }
                            });

                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(SignUp.this, "Account Already Exist", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
            }
        }
    }
}