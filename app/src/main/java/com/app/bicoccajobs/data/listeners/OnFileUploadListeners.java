package com.app.bicoccajobs.data.listeners;

import com.google.firebase.storage.UploadTask;

public interface OnFileUploadListeners {
    void onSuccess(UploadTask.TaskSnapshot taskSnapshot);

    void onProgress(UploadTask.TaskSnapshot taskSnapshot);

    void onFailure(String e);
}
