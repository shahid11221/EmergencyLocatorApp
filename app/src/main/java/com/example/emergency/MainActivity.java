package com.example.emergency;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    String currentUser,fullname,picture;
    NavigationView navigationView;
    private DrawerLayout drawer;
    ImageView profilePicture;
    TextView profileName,profileEmail;

    @Override
    public void onBackPressed() {
        AlertDialog.Builder ad =new AlertDialog.Builder(MainActivity.this);
        ad.setTitle("App will close");
        ad.setMessage("Are u sure you want to exit?");
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
        setContentView(R.layout.activity_main);

        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        if(b!=null)
        {   currentUser=(String) b.get("email") ;
            fullname =(String) b.get("name");
            picture =(String) b.get("pic");

        }
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getEmail();


        //setTitle("App Menu");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        profilePicture=(CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        profileEmail=(TextView) navigationView.getHeaderView(0).findViewById(R.id.profile_email);
        profileName=(TextView) navigationView.getHeaderView(0).findViewById(R.id.profile_name);
        Glide.with(MainActivity.this).load(picture).into(profilePicture);
        profileName.setText(fullname);
        profileEmail.setText(currentUser);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.syncState();

        if (savedInstanceState == null) {
            hideKeyboard(this);


        }


        }


    public void btndis2click(View view) {
        //EDIT THIS CODE
        Intent i = new Intent(MainActivity.this, ShowDisasters.class);
        i.putExtra("email",currentUser);
        i.putExtra("name",fullname);
        i.putExtra("pic",picture);


        startActivity(i);
        finish();
    }
    public void btndis1click(View view) {
        //EDIT THIS CODE
        Intent i = new Intent(MainActivity.this, IdentifyDisaster.class);
        i.putExtra("email",currentUser);
        i.putExtra("name",fullname);
        i.putExtra("pic",picture);
        startActivity(i);
        finish();
    }
    public void btnguard(View view) {
        Intent i = new Intent(MainActivity.this, AddContacts.class);
        i.putExtra("email",currentUser);
        i.putExtra("name",fullname);
        i.putExtra("pic",picture);
        startActivity(i);
        finish();
    }
    public void btnemclick(View view) {

        Intent i = new Intent(MainActivity.this, MapActivity.class);
        i.putExtra("email",currentUser);
        i.putExtra("name",fullname);
        i.putExtra("pic",picture);
        startActivity(i);
        finish();
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        currentUser = mAuth.getCurrentUser().getEmail();
        switch (item.getItemId()) {

            case R.id.nav_profile:
                hideKeyboard(this);
                Intent i = new Intent(MainActivity.this, PersonalInfo.class);
                i.putExtra("email",currentUser);
                startActivity(i);
                finish();


                break;
            case R.id.nav_logout:
            {
                AlertDialog.Builder ad =new AlertDialog.Builder(MainActivity.this);
                ad.setTitle("App will log you out");
                ad.setMessage("Are u sure you want to log out?");
                ad.setCancelable(false);
                ad.setIcon(R.drawable.ic_baseline_warning_24);
                ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseAuth.getInstance().signOut();
                        Intent backIntent = new Intent(MainActivity.this,SignIn.class);
                        startActivity(backIntent);
                        finish();
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

                break;
            case R.id.nav_change_pass:

                Intent ii = new Intent(MainActivity.this, ChangePassword.class);

                ii.putExtra("email",currentUser);
                ii.putExtra("name",fullname);
                ii.putExtra("pic",picture);
                startActivity(ii);
                finish();


                break;
            case R.id.nav_quit:
            {
                AlertDialog.Builder ad =new AlertDialog.Builder(MainActivity.this);
                ad.setTitle("App will close");
                ad.setMessage("Are u sure you want to exit?");
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
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View currentFocusedView = activity.getCurrentFocus();
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
