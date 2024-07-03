package com.app.bicoccajobs.data.database_controllers;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseAddresses {



    public static DatabaseReference getSignUpReference(String ref) {
        return FirebaseDatabase.getInstance().getReference(ref);
    }
    public static DatabaseReference getJobPostReference() {
        return FirebaseDatabase.getInstance().getReference("JobPosts");
    }

}
