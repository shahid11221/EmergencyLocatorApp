package com.example.emergency;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DbFunctions {
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    public String addUser(String a, String b){
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("username", a);
        user.put("password", b);


// Add a new document with a generated ID
        db.collection("users").document(a)
                .set(user);

        return "success";
    }




}
