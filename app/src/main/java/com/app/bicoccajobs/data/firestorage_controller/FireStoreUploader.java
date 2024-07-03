package com.app.bicoccajobs.data.firestorage_controller;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.app.bicoccajobs.data.listeners.OnFileUploadListeners;
import com.app.bicoccajobs.utils.Utils;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.List;


//This class is created is for accessing Firebase Storage and upload user images.
public class FireStoreUploader {
    private StorageTask mUploadTask;

    //This method upload the user selected picture to Firebase Storage by using picture url.
    public static void uploadPhoto(Uri uri, OnFileUploadListeners onFileUploadListeners, StorageReference storageReference) {

        storageReference.child(uri.getLastPathSegment()).putFile(uri).addOnSuccessListener(taskSnapshot -> onFileUploadListeners.onSuccess(taskSnapshot)).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                onFileUploadListeners.onProgress(snapshot);
            }
        }).addOnFailureListener(e -> onFileUploadListeners.onFailure(e.getMessage()));
    }

    public static void uploadPhotos(List<Uri> uriList, OnFileUploadListeners onFileUploadListeners, StorageReference storageReference) {
        for (Uri uri : uriList) {
            storageReference.child(uri.getLastPathSegment()).putFile(uri).addOnSuccessListener(taskSnapshot -> onFileUploadListeners.onSuccess(taskSnapshot)).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    onFileUploadListeners.onProgress(snapshot);
                }
            }).addOnFailureListener(e -> onFileUploadListeners.onFailure(e.getMessage()));
        }
    }



    public static void uploadImages(Uri mImageUri, Context context, OnFileUploadListeners onFileUploadListeners) {
        final StorageReference fileref = FireStorageAddresses.getJobPostRef().child(System.currentTimeMillis() + "." + Utils.getExtension(mImageUri, context));
        fileref.putFile(mImageUri).addOnSuccessListener(taskSnapshot -> onFileUploadListeners.onSuccess(taskSnapshot)).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                onFileUploadListeners.onProgress(snapshot);
            }
        }).addOnFailureListener(e -> onFileUploadListeners.onFailure(e.getMessage()));
    }
}
