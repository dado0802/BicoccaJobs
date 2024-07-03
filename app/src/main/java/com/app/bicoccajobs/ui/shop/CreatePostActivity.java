package com.app.bicoccajobs.ui.shop;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class CreatePostActivity extends AppCompatActivity{

    ActivityCreatePostBinding binding;
    private static final int STORAGE_PERMISSION_CODE = 100;
    private static final int CAMERA_PERMISSION_CODE = 104;
    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int PICK_IMAGE = 102;
    boolean mGranted, flag;
    AlertDialog alertDialog;
    private Uri ImageUri;
    ProgressDialog mProgressDialog;
    DatabaseReference databaseReference;
    StorageReference mStorageRef;

    public static List<String> urlList;
    String Title="", Desc="";
    public static PostModelClass model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreatePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        databaseReference = FirebaseDatabase.getInstance().getReference("JobPosts");
        mStorageRef = FirebaseStorage.getInstance().getReference("JobPosts/") ;

        urlList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(linearLayoutManager);


        binding.imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.layoutSelectPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence arr[] = new CharSequence[]{
                        //"TAKE A PHOTO",
                        getString(R.string.library),
                        getString(R.string.cancel)
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(CreatePostActivity.this);
                builder.setTitle(R.string.select);
                builder.setItems(arr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /*if(i==0){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (!flag) {
                                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                        requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                                        return;
                                    }
                                }
                            }

                            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, CAMERA_REQUEST_CODE);
                        }*/
                        if(i==0) {
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
                        if(i == 1){
                            alertDialog.dismiss();
                        }
                    }
                });
                alertDialog = builder.create();
                alertDialog.show();
            }
        });

        binding.btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Title = binding.edtTitle.getText().toString().trim();
                Desc = binding.edtDescription.getText().toString().trim();

                if(urlList.size()<1){
                    Toast.makeText(CreatePostActivity.this, R.string.toast_info, Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(Title)){
                    binding.edtTitle.setError(getString(R.string.missing_title));
                    binding.edtTitle.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(Desc)){
                    binding.edtDescription.setError(getString(R.string.missing_des));
                    binding.edtDescription.requestFocus();
                    return;
                }

                String id = databaseReference.push().getKey();

                model = new PostModelClass(id,urlList,Title,Desc,ShopActivity.userId,ShopActivity.fullName,ShopActivity.pic,ShopActivity.email,ShopActivity.address,"");
                databaseReference.child(id).setValue(model);

                Toast.makeText(CreatePostActivity.this, R.string.toast_upload, Toast.LENGTH_SHORT).show();

                binding.edtTitle.setText("");
                binding.edtDescription.setText("");
                hideProgressDialog();
                binding.recyclerView.setAdapter(null);

                startActivity(new Intent(getApplicationContext(), PostSuccessActivity.class));

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), R.string.toast_storage, Toast.LENGTH_SHORT).show();
                mGranted = true;

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, PICK_IMAGE);
            } else {
                Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == CAMERA_PERMISSION_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                flag = true;

                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            } else {
                Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            binding.recyclerView.setAdapter(null);
            uploadBitmap(photo);
        }

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {

                if (data.getClipData() != null) {

                    int countClipData = data.getClipData().getItemCount();
                    int currentImageSlect = 0;

                    while (currentImageSlect < countClipData) {
                        ImageUri = data.getClipData().getItemAt(currentImageSlect).getUri();
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
                                            abc();
                                        } catch (Exception ex ){
                                            Toast.makeText(getApplicationContext()  , "err" + ex.toString() , Toast.LENGTH_LONG).show();
                                            hideProgressDialog();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            }

                            @Override
                            public void onFailure(String e) {
                                Toast.makeText(CreatePostActivity.this, e, Toast.LENGTH_SHORT).show();
                                hideProgressDialog();
                            }
                        });
                        currentImageSlect = currentImageSlect + 1;
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
                                        abc();
                                    } catch (Exception ex ){
                                        Toast.makeText(getApplicationContext()  , "err" + ex.toString() , Toast.LENGTH_LONG).show();
                                        hideProgressDialog();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        }

                        @Override
                        public void onFailure(String e) {
                            Toast.makeText(CreatePostActivity.this, e, Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                        }
                    });
                }
        }
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

    private void uploadBitmap(Bitmap bitmap) {
        showProgressDialog();
        byte[] data;
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, boas);
        data = boas.toByteArray();

        final StorageReference fileref = mStorageRef.child(System.currentTimeMillis()+"");
        UploadTask uploadTask = fileref.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        urlList.add(uri.toString());
                        hideProgressDialog();
                        abc();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                hideProgressDialog();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
            }
        });
    }

    public void abc(){
        ItemListAdapter imageAdapter = new ItemListAdapter(CreatePostActivity.this, urlList);
        binding.recyclerView.setAdapter(imageAdapter);
    }
}