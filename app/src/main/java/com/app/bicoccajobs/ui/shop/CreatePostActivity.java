package com.app.bicoccajobs.ui.shop;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.bicoccajobs.R;
import com.app.bicoccajobs.adapters.ItemListAdapter;
import com.app.bicoccajobs.databinding.ActivityCreatePostBinding;
import com.app.bicoccajobs.data.firestorage_controller.FireStoreUploader;
import com.app.bicoccajobs.data.listeners.OnFileUploadListeners;
import com.app.bicoccajobs.models.PostModelClass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CreatePostActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 100;
    private static final int CAMERA_PERMISSION_CODE = 104;
    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int PICK_IMAGE = 102;
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
    private ActivityCreatePostBinding binding;
    private boolean mGranted, flag;
    private AlertDialog alertDialog;
    private ProgressDialog mProgressDialog;
    private Uri ImageUri;
    private static List<String> urlList;
    private DatabaseReference databaseReference;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreatePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (allPermissionsGranted()) {
            // Permessi concessi, possiamo procedere
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("JobPosts");
        mStorageRef = FirebaseStorage.getInstance().getReference("JobPosts/");
        urlList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(linearLayoutManager);

        binding.imgCancel.setOnClickListener(v -> finish());

        binding.layoutSelectPic.setOnClickListener(v -> {
            CharSequence arr[] = new CharSequence[]{
                    "TAKE A PHOTO",
                    getString(R.string.library),
                    getString(R.string.cancel)
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(CreatePostActivity.this);
            builder.setTitle(R.string.select);
            builder.setItems(arr, (dialogInterface, i) -> {
                if (i == 0) {
                    Intent intent = new Intent(CreatePostActivity.this, CameraActivity.class);
                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
                }
                if (i == 1) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!mGranted) {
                            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                                return;
                            }
                        }
                    }

                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(intent, PICK_IMAGE);
                }
                if (i == 2) {
                    alertDialog.dismiss();
                }
            });
            alertDialog = builder.create();
            alertDialog.show();
        });

        binding.btnPost.setOnClickListener(v -> {
            String Title = binding.edtTitle.getText().toString().trim();
            String Desc = binding.edtDescription.getText().toString().trim();

            if (urlList.size() < 1) {
                Toast.makeText(CreatePostActivity.this, R.string.toast_info, Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(Title)) {
                binding.edtTitle.setError(getString(R.string.missing_title));
                binding.edtTitle.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(Desc)) {
                binding.edtDescription.setError(getString(R.string.missing_des));
                binding.edtDescription.requestFocus();
                return;
            }

            String id = databaseReference.push().getKey();
            PostModelClass model = new PostModelClass(id, urlList, Title, Desc, ShopActivity.userId, ShopActivity.fullName, ShopActivity.pic, ShopActivity.email, ShopActivity.address, "");
            databaseReference.child(id).setValue(model);

            // Passa l'oggetto model tramite l'Intent
            Intent intent = new Intent(getApplicationContext(), PostSuccessActivity.class);
            intent.putExtra("model", model);
            startActivity(intent);

            Toast.makeText(CreatePostActivity.this, R.string.toast_upload, Toast.LENGTH_SHORT).show();

            binding.edtTitle.setText("");
            binding.edtDescription.setText("");
            hideProgressDialog();
            binding.recyclerView.setAdapter(null);
        });
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                // Permessi concessi, possiamo procedere
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String imageUriString = data.getStringExtra("image_uri");
            if (imageUriString != null) {
                Uri imageUri = Uri.parse(imageUriString);
                uploadImageToStorage(imageUri);
            }
        }

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                int countClipData = data.getClipData().getItemCount();
                int currentImageSelect = 0;

                while (currentImageSelect < countClipData) {
                    ImageUri = data.getClipData().getItemAt(currentImageSelect).getUri();
                    binding.recyclerView.setAdapter(null);
                    showProgressDialog();
                    FireStoreUploader.uploadImages(ImageUri, CreatePostActivity.this, new OnFileUploadListeners() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    try {
                                        urlList.add(uri.toString());
                                        hideProgressDialog();
                                        updateRecyclerView();
                                    } catch (Exception ex) {
                                        Toast.makeText(getApplicationContext(), "Error: " + ex.toString(), Toast.LENGTH_LONG).show();
                                        hideProgressDialog();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {}

                        @Override
                        public void onFailure(String e) {
                            Toast.makeText(CreatePostActivity.this, e, Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                        }
                    });
                    currentImageSelect = currentImageSelect + 1;
                }
            } else {
                ImageUri = data.getData();
                binding.recyclerView.setAdapter(null);
                showProgressDialog();
                FireStoreUploader.uploadImages(ImageUri, CreatePostActivity.this, new OnFileUploadListeners() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                try {
                                    urlList.add(uri.toString());
                                    hideProgressDialog();
                                    updateRecyclerView();
                                } catch (Exception ex) {
                                    Toast.makeText(getApplicationContext(), "Error: " + ex.toString(), Toast.LENGTH_LONG).show();
                                    hideProgressDialog();
                                }
                            }
                        });
                    }

                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {}

                    @Override
                    public void onFailure(String e) {
                        Toast.makeText(CreatePostActivity.this, e, Toast.LENGTH_SHORT).show();
                        hideProgressDialog();
                    }
                });
            }
        }
    }

    private void uploadImageToStorage(Uri imageUri) {
        showProgressDialog();
        FireStoreUploader.uploadImages(imageUri, this, new OnFileUploadListeners() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                task.addOnSuccessListener(uri -> {
                    try {
                        urlList.add(uri.toString());
                        hideProgressDialog();
                        updateRecyclerView();
                    } catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), "Error: " + ex.toString(), Toast.LENGTH_LONG).show();
                        hideProgressDialog();
                    }
                });
            }

            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {}

            @Override
            public void onFailure(String e) {
                Toast.makeText(CreatePostActivity.this, e, Toast.LENGTH_SHORT).show();
                hideProgressDialog();
            }
        });
    }

    private void updateRecyclerView() {
        ItemListAdapter imageAdapter = new ItemListAdapter(CreatePostActivity.this, urlList);
        binding.recyclerView.setAdapter(imageAdapter);
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.waiting_data));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public static List<String> getUrlList() {
        return urlList;
    }

    public static void setUrlList(List<String> urlList) {
        CreatePostActivity.urlList = urlList;
    }
}
