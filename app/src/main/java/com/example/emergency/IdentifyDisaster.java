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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.net.Uri;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class IdentifyDisaster extends AppCompatActivity {

    EditText et1;
    EditText et2;
    EditText et3;
    ImageView disasterPicture;
    String img_path="";
    String path;
    LinearLayout linearLayout;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static int i=0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final String TAG = "YOUR-TAG-NAME";
    public static boolean flag1 = true;
    static TextView tv ;
    ProgressDialog progressDialog;
    String currentUser,fullname,picture;
    private StorageReference mStorageRef;

    @Override
    public void onBackPressed() {
        Intent i = new Intent(IdentifyDisaster.this, MainActivity.class);
        i.putExtra("email", currentUser);
        i.putExtra("name", fullname);
        i.putExtra("pic", picture);
        startActivity(i);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                disasterPicture.setImageURI(selectedImage);
            progressDialog = new ProgressDialog(IdentifyDisaster.this);
            progressDialog.setTitle(" Wait a second");
            progressDialog.setMessage("Uploading Picture");
            progressDialog.show();

            mStorageRef.child("images/").child("disasterPictures/")
                    .child(System.currentTimeMillis() + ".jpg").putFile(selectedImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            if (taskSnapshot.getMetadata() != null) {
                                if (taskSnapshot.getMetadata().getReference() != null) {
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            img_path = uri.toString();


                                        }
                                    });

                                } else {
                                    img_path = "no_image";
                                    progressDialog.cancel();
                                    Toast.makeText(IdentifyDisaster.this, "Path not selected", Toast.LENGTH_SHORT).show();
                                }
                            }

                            progressDialog.cancel();
                        }
                    });


            }
        else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            disasterPicture.setImageBitmap(imageBitmap);
            getImageUri(IdentifyDisaster.this,imageBitmap);

            progressDialog = new ProgressDialog(IdentifyDisaster.this);
            progressDialog.setTitle(" Wait a second");
            progressDialog.setMessage("Uploading Picture");
            progressDialog.show();

            mStorageRef.child("images/").child("disasterPictures/")
                    .child(System.currentTimeMillis() + ".jpg").putFile(Uri.parse(path))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            if (taskSnapshot.getMetadata() != null) {
                                if (taskSnapshot.getMetadata().getReference() != null) {
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            img_path = uri.toString();


                                        }
                                    });

                                } else {
                                    img_path = "no_image";
                                    progressDialog.cancel();
                                    Toast.makeText(IdentifyDisaster.this, "Path not selected", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_identify_disaster);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        tv= findViewById(R.id.dateofoccurance);

        et1 = findViewById(R.id.disastername);
        et2 =findViewById(R.id.disastercity);
        et3 =  findViewById(R.id.disastercategory);
        disasterPicture =  findViewById(R.id.disasterpic);
        linearLayout=findViewById(R.id.layout);
        linearLayout.getBackground().setAlpha(60);
        Bundle b = getIntent().getExtras();
        if(b!=null)
        {   currentUser=(String) b.get("email") ;
            fullname =(String) b.get("name");
            picture =(String) b.get("pic");

        }



    }

    public void disastercancelclick(View view)
    {
        Intent i = new Intent(IdentifyDisaster.this, MainActivity.class);
        i.putExtra("email", currentUser);
        i.putExtra("name", fullname);
        i.putExtra("pic", picture);
        startActivity(i);
        finish();
    }

    public void disastersaveclick(View view)
    {
    if(img_path.equals(""))
    {
        Toast.makeText(this, "Please Select Image", Toast.LENGTH_SHORT).show();
    }
      else  if(!(et1.getText().toString().equals("")) &&
                !(et2.getText().toString().equals("")) &&
                !(et3.getText().toString().equals("")) &&
                !(tv.getText().toString().equals("Tap to Add")))

        {
            //Data Setting
            ProgressDialog p=new ProgressDialog(IdentifyDisaster.this);
            p.setMessage("Please wait");
            p.setTitle("Saving");
            p.show();
            db.collection("disasters").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot documents = task.getResult();
                        while(true){

                            i++;
                            if (documents.getDocuments().contains(("disaster"+"("+System.currentTimeMillis()+")"))) {
//                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());


                            } else {
                                Log.d(TAG, "No such document");
                                flag1=false;
                                break;
                            }}
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });


            Map<String, Object> user = new HashMap<>();
            user.put("disastername", et1.getText().toString());
            user.put("disastercity", et2.getText().toString());
            user.put("dateofoccurance",tv.getText().toString());
            user.put("disastercategory",et3.getText().toString());
            user.put("disasterpicture",img_path);


            // Add a new document with a generated ID
            db.collection("disasters").document((("disaster"+"("+System.currentTimeMillis()+")")))
                    .set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            p.cancel();
                            Toast.makeText(IdentifyDisaster.this, "Added Successfully ", Toast.LENGTH_SHORT).show();
                            Intent i=new Intent(IdentifyDisaster.this,MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast toast=Toast.makeText(getApplicationContext(),"Disaster Add Failed (Database Error)",Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });

        }

        else{
            Toast toast=Toast.makeText(getApplicationContext(),"Please fill all the fields",Toast.LENGTH_SHORT);
            toast.show();
        }


    }

    public void AddPicture(View view)
    {
        AlertDialog.Builder ad=new AlertDialog.Builder(IdentifyDisaster.this);
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
            tv.setText(day +"-"+(month+1)+"-"+year);
        }
    }


    public void showDatePickerDialog(View view)
    {
        DialogFragment newFragment = new IdentifyDisaster.DatePickerFragment();
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
}
