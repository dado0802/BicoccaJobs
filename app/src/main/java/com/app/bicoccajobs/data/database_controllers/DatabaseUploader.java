package com.app.bicoccajobs.data.database_controllers;

import com.app.bicoccajobs.data.listeners.OnTaskListeners;
import com.app.bicoccajobs.models.ShopModelCLass;
import com.app.bicoccajobs.models.StudentModelCLass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class DatabaseUploader {

    FirebaseAuth mAuth;

    // used for registration
    public static void setStudentRecord(StudentModelCLass userProfile, OnTaskListeners onTaskListeners) {
        DatabaseAddresses.getSignUpReference("StudentsData").child(userProfile.getUserId()).setValue(userProfile).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                onTaskListeners.onTaskSuccess();
            } else {
                onTaskListeners.onTaskFail(task.getException().getMessage());
            }
        });
    }
    public static void setShopRecord(ShopModelCLass userProfile, OnTaskListeners onTaskListeners) {
        DatabaseAddresses.getSignUpReference("ShopsData").child(userProfile.getUserId()).setValue(userProfile).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                onTaskListeners.onTaskSuccess();
            } else {
                onTaskListeners.onTaskFail(task.getException().getMessage());
            }
        });
    }
    public static void setAuthToFirebase(String email, String password, FirebaseAuth mAuth, OnTaskListeners onTaskListeners) {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                onTaskListeners.onTaskSuccess();
            } else {
                onTaskListeners.onTaskFail(task.getException().getMessage());
            }
        });
    }

    public static void sendVerification(FirebaseUser firebaseUser, OnTaskListeners onTaskListeners) {
        firebaseUser.sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                onTaskListeners.onTaskSuccess();
            } else {
                onTaskListeners.onTaskFail(task.getException().getMessage());
            }
        });
    }

}
