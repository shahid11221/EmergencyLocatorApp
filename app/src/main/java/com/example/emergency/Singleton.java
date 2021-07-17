package com.example.emergency;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Singleton {

    private static volatile Singleton sSoleInstance = new Singleton();

    //FirebaseDatabase database;
    String spinnerItem;
    String spinnerItem1;

    String spinnerItem2;
    String spinnerItem3;

    FirebaseAuth mAuth;
    boolean readCheck;
    Boolean snackBarCheck;
    Boolean snackFlag;
    Boolean fragmentFlag;


    //private constructor.

    private Singleton(){

        mAuth = FirebaseAuth.getInstance();
        //database = FirebaseDatabase.getInstance();
        spinnerItem = null;
        spinnerItem1 = null;

        spinnerItem2 = null;
        spinnerItem3 = null;

        readCheck = false;
        snackBarCheck = false;
        snackFlag = false;

        fragmentFlag = true;
    }



    static Singleton getInstance() {

        return sSoleInstance;

    }
}
