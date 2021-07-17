package com.example.emergency;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import static com.example.emergency.IdentifyDisaster.REQUEST_IMAGE_CAPTURE;

public class PersonalInfo extends AppCompatActivity {
    public static TextView et;
    EditText nameEt;
    EditText fnameEt;
    EditText addressTw;
    ImageView profilePicIv;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final String TAG = "YOUR-TAG-NAME";
    private StorageReference mStorageRef;
    String email,path,img_path,fullname,picture;
    ProgressDialog progressDialog;
    Button changePicBtn;


    @Override
    public void onBackPressed()
    {  DocumentReference docRef = db.collection("users").document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if(        document.contains("fullname")
                            && document.contains("profilepicture")
                            && document.contains("fathername")
                            && document.contains("address"))
                    {    email=(document.get("username").toString());
                        fullname=(document.get("fullname").toString());
                        picture=(document.get("profilepicture").toString());
                        Intent i=new Intent(PersonalInfo.this,MainActivity.class);
                        i.putExtra("email",email);
                        i.putExtra("name",fullname);
                        i.putExtra("pic",picture);
                        startActivity(i);
                        finish();

                    }
                    else
                    {
                        AlertDialog.Builder ad = new AlertDialog.Builder(PersonalInfo.this);
                        ad.setTitle("App will close");
                        ad.setMessage("Are u sure you want to exit and delete your account?");
                        ad.setCancelable(false);
                        ad.setIcon(R.drawable.ic_baseline_warning_24);
                        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().getCurrentUser().delete();
                                FirebaseFirestore.getInstance().collection("users").document(email).delete();

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

                }

            }




        });





    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);


                if (requestCode== 0 && resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    profilePicIv.setImageURI(selectedImage);

                    progressDialog=new ProgressDialog(PersonalInfo.this);
                    progressDialog.setTitle("Wait a second");
                    progressDialog.setMessage("Uploading Picture");
                    progressDialog.show();
                    mStorageRef.child("images/").child("profilePictures/")
                            .child(email+".jpg").putFile(selectedImage)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            if (taskSnapshot.getMetadata() != null) {
                                if (taskSnapshot.getMetadata().getReference() != null) {
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            img_path= uri.toString();
                                             }
                                    });

                                }else{
                                    img_path="no_image";
                                    Toast.makeText(PersonalInfo.this, "Path not selected", Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressDialog.cancel();
                        }
                    });

                }
                else  if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    profilePicIv.setImageBitmap(imageBitmap);
                    getImageUri(PersonalInfo.this,imageBitmap);
                    progressDialog=new ProgressDialog(PersonalInfo.this);
                    progressDialog.setTitle("Wait a second");
                    progressDialog.setMessage("Uploading Picture");
                    progressDialog.show();
                    mStorageRef.child("images/").child("profilePictures/")
                            .child(email+".jpg").putFile(Uri.parse(path))
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    if (taskSnapshot.getMetadata() != null) {
                                        if (taskSnapshot.getMetadata().getReference() != null) {
                                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    img_path= uri.toString();
                                                }
                                            });

                                        }else{
                                            img_path="no_image";
                                            Toast.makeText(PersonalInfo.this, "Path not selected", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    progressDialog.cancel();
                                }
                            });

                }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);


            init();
            DataSetting();
    }

    public void cancelinfoclick(View view)
    {  DocumentReference docRef = db.collection("users").document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if(        document.contains("fullname")
                            && document.contains("profilepicture")
                            && document.contains("fathername")
                            && document.contains("address"))
                    {    email=(document.get("username").toString());
                        fullname=(document.get("fullname").toString());
                        picture=(document.get("profilepicture").toString());
                        Intent i=new Intent(PersonalInfo.this,MainActivity.class);
                        i.putExtra("email",email);
                        i.putExtra("name",fullname);
                        i.putExtra("pic",picture);
                        startActivity(i);
                        finish();

                    }
                    else
                    {
                        AlertDialog.Builder ad = new AlertDialog.Builder(PersonalInfo.this);
                        ad.setTitle("App will close");
                        ad.setMessage("Are u sure you want to exit and delete your account?");
                        ad.setCancelable(false);
                        ad.setIcon(R.drawable.ic_baseline_warning_24);
                        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().getCurrentUser().delete();
                                FirebaseFirestore.getInstance().collection("users").document(email).delete();

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

                }

            }




        });





    }

    public void saveinfoclick(View view)
    {
        ProgressDialog p=new ProgressDialog(PersonalInfo.this);
        p.setMessage("Please wait");
        p.setTitle("Saving");
        p.show();

        final Boolean[] flag = {true};

        DocumentReference dateRef = db.collection("users").document(email);
        if(!img_path.equals("")){
            dateRef.update("profilepicture", img_path)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            flag[0] =false;
                            Toast toast=Toast.makeText(getApplicationContext(),"Profile Picture Change Failed (Database Error)",Toast.LENGTH_SHORT);
                            toast.show();
                            Log.w(TAG, "Error updating document", e);
                        }
                    });

        }
        if(!(nameEt.getText().toString().equals(""))){
            dateRef.update("fullname", nameEt.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            flag[0] =false;
                            Toast toast=Toast.makeText(getApplicationContext(),"Full Name Change Failed (Database Error)",Toast.LENGTH_SHORT);
                            toast.show();
                            Log.w(TAG, "Error updating document", e);
                        }
                    });

        }
        if(!(fnameEt.getText().toString().equals(""))){
            dateRef.update("fathername", fnameEt.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            flag[0] =false;
                            Toast toast=Toast.makeText(getApplicationContext(),"Father Name Change Failed (Database Error)",Toast.LENGTH_SHORT);
                            toast.show();
                            Log.w(TAG, "Error updating document", e);
                        }
                    });
        }
        if(!(addressTw.getText().toString().equals(""))){
            dateRef.update("address", addressTw.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            flag[0] =false;
                            Toast toast=Toast.makeText(getApplicationContext(),"Address Change Failed (Database Error)",Toast.LENGTH_SHORT);
                            toast.show();
                            Log.w(TAG, "Error updating document", e);
                        }
                    });
        }
        if(!(et.getText().toString().equals("Tap to Add"))){
            dateRef.update("dateofbirth", et.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            flag[0] =false;
                            Toast toast=Toast.makeText(getApplicationContext(),"DateofBirth Change Failed (Database Error)",Toast.LENGTH_SHORT);
                            toast.show();
                            Log.w(TAG, "Error updating document", e);
                        }
                    });
        }
        if(flag[0]==true){
            p.dismiss();
            Toast toast=Toast.makeText(getApplicationContext(),"Personal Information Changed Successfully",Toast.LENGTH_SHORT);
            toast.show();
        }





    }

    public void AddProfilePicture(View view)
    {
        AlertDialog.Builder ad=new AlertDialog.Builder(PersonalInfo.this);
        ad.setMessage("Do you want to take a picture or do u want a picture from gallary");
        ad.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dispatchTakePictureIntent();

            }

        });
        ad.setNegativeButton("Gallary", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,0);

            }
        });
        ad.setCancelable(false);
        ad.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        ad.show();


    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener
    {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            et.setText(day +"-"+(month+1)+"-"+year);
        }
    }
    public void showDatePickerDialog(View view)
    {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");

    }
    private void dispatchTakePictureIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    public void DataSetting()
    {       email=FirebaseAuth.getInstance().getCurrentUser().getEmail();
        DocumentReference docRef = db.collection("users").document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        if(document.contains("fullname")){
                            nameEt.setText(document.get("fullname").toString());
                        }
                        if(document.contains("profilepicture")){
                            changePicBtn.setVisibility(View.VISIBLE);
                            profilePicIv.setClickable(false);
                            img_path=document.get("profilepicture").toString();
                            Picasso.get().load(document.get("profilepicture").toString()).into(profilePicIv);


                        }
                        if(document.contains("fathername")){
                            fnameEt.setText(document.get("fathername").toString());
                        }
                        if(document.contains("dateofbirth")){
                            et.setText(document.get("dateofbirth").toString());
                        }
                        if(document.contains("address")){
                            addressTw.setText(document.get("address").toString());
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
    public void init()
    {

        et = findViewById(R.id.dateofbirth);
        nameEt = findViewById(R.id.fullname);
        fnameEt = findViewById(R.id.fathername);
        addressTw = findViewById(R.id.address);
        profilePicIv =findViewById(R.id.profile_pic);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        changePicBtn =findViewById(R.id.change_pic);
        changePicBtn.setVisibility(View.INVISIBLE);
        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        if(b!=null)
        {
            email =(String) b.get("email");

        }

    }
    }

