package com.app.bicoccajobs.data.firestorage_controller;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

//The purpose of this class is to create reference with Firebase Storage Users->Profiles Reference to upload users profile pictures to Users directory in firebase storage
public class FireStorageAddresses {
    public static StorageReference getJobPostRef() {
        return FirebaseStorage.getInstance().getReference("JobPosts");
    }
}
