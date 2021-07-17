package com.example.emergency;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangePassword extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final String TAG = "YOUR-TAG-NAME";
    LinearLayout passwordLayout;
    String currentUser,fullname,picture;


    @Override
    public void onBackPressed() {
        Intent i=new Intent(ChangePassword.this,MainActivity.class);

        i.putExtra("email",currentUser);
        i.putExtra("name",fullname);
        i.putExtra("pic",picture);
        startActivity(i);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        passwordLayout=findViewById(R.id.pass_layout);
        Bundle b = getIntent().getExtras();
        if(b!=null)
        {   currentUser=(String) b.get("email") ;
            fullname =(String) b.get("name");
            picture =(String) b.get("pic");

        }




    }

    public void savepassclick(View view) {
        EditText oldPassEt = findViewById(R.id.oldpass);
        EditText newPassEt = findViewById(R.id.newpass);
        EditText newCPassEt1 = findViewById(R.id.newpass1);


        String newPass1 = newCPassEt1.getText().toString();
        String newPass = newPassEt.getText().toString();
        String oldpass = oldPassEt.getText().toString();

        if(newPass.equals(newPass1))
                 {
            ProgressDialog progressDialog=new ProgressDialog(ChangePassword.this);
            progressDialog.setTitle("Examining passwords");
            progressDialog.setMessage("Sorry to make you wait !");

            progressDialog.show();

            FirebaseUser user;
            user = FirebaseAuth.getInstance().getCurrentUser();
            final String email = user.getEmail();
            AuthCredential credential = EmailAuthProvider.getCredential(email, oldpass);

            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    Snackbar snackbar_fail = Snackbar
                                            .make(passwordLayout, "Something went wrong. Please try again later", Snackbar.LENGTH_LONG);
                                    snackbar_fail.show();
                                } else {
                                    Snackbar snackbar_su = Snackbar
                                            .make(passwordLayout, "Password Successfully Modified", Snackbar.LENGTH_LONG);
                                    snackbar_su.setDuration(2000);
                                    snackbar_su.show();
                                    db.collection("users").document(email).update("password",newPass);
                                    progressDialog.cancel();
                                    Intent i=new Intent(ChangePassword.this,MainActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            }
                        });
                    } else {
                        progressDialog.cancel();
                        Snackbar snackbar_su = Snackbar
                                .make(passwordLayout, "Authentication Failed", Snackbar.LENGTH_LONG);
                        snackbar_su.show();

                    }
                }
            });
        }
        else
                {

            Snackbar snackbar_su = Snackbar
                    .make(passwordLayout, "Passwords Do not Match ", Snackbar.LENGTH_LONG);
            snackbar_su.show();


        }


    }







    public void cancelpassclick(View view) {
        Intent i=new Intent(ChangePassword.this,MainActivity.class);

        i.putExtra("email",currentUser);
        i.putExtra("name",fullname);
        i.putExtra("pic",picture);
        startActivity(i);
        finish();
    }
}
