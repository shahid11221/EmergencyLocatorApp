package com.example.emergency;

import android.app.DatePickerDialog;
import android.app.Dialog;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FilteredStats extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ListView listView;
    ArrayList<DataModel> dataModels1;
    ArrayList<DataModel> dataModels;

    static EditText cityname;
    static EditText categoryname;
    static TextView dateto;
    static TextView datefrom;
    private static CustomAdapter adapter;
    private static CustomAdapter adapter1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtered_stats);

        init();



        db.collection(("disasters")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot qs2 = task.getResult();
                    if (qs2.getDocuments().size() > 0) {
                        for (int i = 0; i < qs2.getDocuments().size(); i++) {
                            DocumentSnapshot ds = qs2.getDocuments().get(i);
                            dataModels.add(new DataModel(ds.get("disastername").toString(),
                                                         ds.get("disastercity").toString(),
                                                         ds.get("dateofoccurance").toString(),
                                                         ds.get("disastercategory").toString(),
                                                         ds.get("disasterpicture").toString()));
                        }

                    }else{
                        Toast.makeText(FilteredStats.this, "Size =0", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(FilteredStats.this, "Error =1", Toast.LENGTH_SHORT).show();
                }
            }
        });
        adapter= new CustomAdapter(dataModels,getApplicationContext());
        listView.setAdapter(adapter);

    }

    public void init(){
        dateto = (TextView)findViewById(R.id.dateto);
        datefrom = (TextView)findViewById(R.id.datefrom);
        cityname = (EditText) findViewById(R.id.cityname);
        categoryname = (EditText) findViewById(R.id.categoryname);
        listView=(ListView)findViewById(R.id.listView1);
        dataModels= new ArrayList<>();
    }

    public void datefromClick(View view) {
        DialogFragment newFragment = new FilteredStats.DatePickerFragment1();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void datetoClick(View view) {
        DialogFragment newFragment = new FilteredStats.DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }



    public void filter3Click(View view)
    {
        dataModels1= new ArrayList<>();

        int count = dataModels.size();

        String filterableString ;

        for (int i = 0; i < count; i++) {
            filterableString = dataModels.get(i).getCategory();

            if (filterableString.equals(categoryname.getText().toString())) {
                dataModels1.add(dataModels.get(i));
            }
        }


        adapter1= new CustomAdapter(dataModels1,getApplicationContext());
        listView.setAdapter(adapter1);
    }

    public void filter2Click(View view)
    {
        dataModels1= new ArrayList<>();

        int count = dataModels.size();

        String filterableString ;

        for (int i = 0; i < count; i++) {
            filterableString = dataModels.get(i).getDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("mm-dd-yyyy");


            try {
                Date dateto1;
                dateto1 = dateFormat.parse(dateto.getText().toString());

            Date datefrom1;

                datefrom1 = dateFormat.parse(datefrom.getText().toString());

            Date datefilter;

                datefilter = dateFormat.parse(filterableString);

                if (datefilter.after(datefrom1) && datefilter.before(dateto1)) {
                    dataModels1.add(dataModels.get(i));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            adapter1= new CustomAdapter(dataModels1,getApplicationContext());
            listView.setAdapter(adapter1);

        }
    }

    public void filter1Click(View view)
    {
        dataModels1= new ArrayList<>();

        int count = dataModels.size();

        String filterableString ;

        for (int i = 0; i < count; i++) {
            filterableString = dataModels.get(i).getCity();

                if (filterableString.equals(cityname.getText().toString())) {
                    dataModels1.add(dataModels.get(i));
                }
                }


            adapter1= new CustomAdapter(dataModels1,getApplicationContext());
            listView.setAdapter(adapter1);

        }


    public static class DatePickerFragment1
            extends DialogFragment implements DatePickerDialog.OnDateSetListener
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
            datefrom.setText(day +"-"+(month+1)+"-"+year);
        }
    }
    public static class DatePickerFragment
            extends DialogFragment implements DatePickerDialog.OnDateSetListener
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

            dateto.setText(day +"-"+(month+1)+"-"+year);
        }
    }

}
