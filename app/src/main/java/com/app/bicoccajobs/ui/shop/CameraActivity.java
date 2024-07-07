package com.app.bicoccajobs.ui.shop;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.app.bicoccajobs.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity {

    private ImageCapture imageCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        PreviewView previewView = findViewById(R.id.previewView);
        Button captureButton = findViewById(R.id.camera_capture_button);

        startCamera(previewView, captureButton);
    }

    private void startCamera(PreviewView previewView, Button captureButton) {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider, previewView, captureButton);
            } catch (ExecutionException | InterruptedException e) {
                Log.e("CameraXApp", getString(R.string.error_camera), e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider, PreviewView previewView, Button captureButton) {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        imageCapture = new ImageCapture.Builder().build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture);

        captureButton.setOnClickListener(v -> takePhoto());
    }

    private void takePhoto() {
        if (imageCapture == null) {
            return;
        }

        File photoFile = new File(getExternalFilesDir(null), System.currentTimeMillis() + ".jpg");
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Uri savedUri = Uri.fromFile(photoFile);
                Intent data = new Intent();
                data.putExtra("image_uri", savedUri.toString());
                setResult(RESULT_OK, data);
                finish();
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e("CameraXApp", getString(R.string.photo_fail) + exception.getMessage(), exception);
                Toast.makeText(CameraActivity.this, getString(R.string.photo_fail), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
