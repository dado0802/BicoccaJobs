package com.app.bicoccajobs.ui.student;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.app.bicoccajobs.R;
import com.app.bicoccajobs.ui.BaseActivity;
import com.app.bicoccajobs.data.database_controllers.DatabaseAddresses;
import com.app.bicoccajobs.databinding.ActivityStudentProfileBinding;
import com.app.bicoccajobs.models.StudentModelCLass;
import com.app.bicoccajobs.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

//Mechanic Profile Screen..
public class StudentProfileActivity extends BaseActivity {
    ActivityStudentProfileBinding binding;
    private static final int PICK_FILE_REQUEST = 101;
    private static final int STORAGE_PERMISSION_CODE = 100;
    String userId, userName,address, userPhone, url, password;
    StorageReference mStorageRef;
    StorageTask mUploadTask;
    boolean mGranted;
    Uri fileUri = Uri.EMPTY;
    int count = 0;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //Get mechanic data from mechanic home screen..
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userName = StudentActivity.fullName;
        userPhone = StudentActivity.phone;
        url = StudentActivity.pic;
        password = StudentActivity.password;
        address = StudentActivity.address;

        mStorageRef = FirebaseStorage.getInstance().getReference("StudentsData/");

        binding.edtName.setText(userName);
        binding.edtPhone.setText(userPhone);
        binding.edtAddress.setText(address);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        binding.edtEmail.setText(firebaseUser.getEmail());
        binding.editPassword.setText(password);

        if(!url.isEmpty()){
            Picasso.with(this).load(url).placeholder(R.drawable.profile).into(binding.imgUserPic);
        }

        binding.edtName.setEnabled(false);
        binding.edtPhone.setEnabled(false);
        binding.edtAddress.setEnabled(false);
        binding.edtEmail.setEnabled(false);
        binding.editPassword.setEnabled(false);

        //Check email verification status and resend verification email to user on his/her email
        binding.tvEmailStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Utils.checkEmailIsVerified()){
                    Utils.sendVerificationEmail(StudentProfileActivity.this);
                }
            }
        });

        //Edit sign clicks code
        binding.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                if(count%2 != 0) {
                    binding.edtName.setEnabled(true);
                    binding.edtName.requestFocus();
                    binding.edtPhone.setEnabled(true);
                    binding.edtAddress.setEnabled(true);
                }else {
                    binding.edtName.setEnabled(false);
                    binding.edtPhone.setEnabled(false);
                    binding.edtAddress.setEnabled(false);
                }
            }
        });

        //Select user picture code
        binding.selectpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!mGranted) {
                        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                            return;
                        }
                    }
                }

                //Move user to gallery of phone
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), PICK_FILE_REQUEST);
            }
        });

        //Update button click code
        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = binding.edtName.getText().toString().trim();
                userPhone = binding.edtPhone.getText().toString().trim();
                address = binding.edtAddress.getText().toString().trim();

                //Validations to all data fileds..
                if(TextUtils.isEmpty(userName)){
                    binding.edtName.setError(getString(R.string.required));
                    binding.edtName.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(userPhone)){
                    binding.edtPhone.setError(getString(R.string.required));
                    binding.edtPhone.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(address)){
                    binding.edtAddress.setError(getString(R.string.required));
                    binding.edtAddress.requestFocus();
                    return;
                }

                updateAccount();
            }
        });

        //Send update password email code..
        binding.btnUpdatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), R.string.email_verify, Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getApplicationContext(), "Error : "+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    //Update account details function..
    private void updateAccount() {
        StudentModelCLass model = new StudentModelCLass(userId,userName,url, StudentActivity.email,userPhone,address,password,StudentActivity.userType,StudentActivity.emailVerified);
        DatabaseAddresses.getSignUpReference("StudentsData").child(userId).setValue(model);
        binding.edtName.setEnabled(false);
        binding.edtPhone.setEnabled(false);
        binding.edtAddress.setEnabled(false);
        binding.editPassword.setEnabled(false);

        Toast.makeText(this, R.string.update_succesfully, Toast.LENGTH_SHORT).show();

    }

    //Check permissions of read and write storage of mobile phone
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                mGranted = true;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), PICK_FILE_REQUEST);
            } else {
                Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 100, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 100, locationListener);
                }
            }
        }
    }
    @Override

    //This function checks which file user select from his mobile gallery or file explorer..
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST && data != null) {
            fileUri = data.getData();
            Picasso.with(this).load(fileUri).into(binding.imgUserPic);
            updatePic();
        }
    }

    private  String getExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver() ;
        MimeTypeMap mime = MimeTypeMap.getSingleton() ;
        return  mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    //This function update user profile pic and store it to firebase storage..
    private void updatePic() {
        if(fileUri !=null){
            showProgressDialog(getString(R.string.uploading_picture));
            final StorageReference fileref = mStorageRef.child(System.currentTimeMillis() + "." + getExtension(fileUri));
            mUploadTask =   fileref.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            hideProgressDialog();
                            try {
                                DatabaseAddresses.getSignUpReference("StudentsData").child(userId).child("pic").setValue(uri.toString());
                                Toast.makeText(StudentProfileActivity.this, R.string.updated_picture, Toast.LENGTH_SHORT).show();
                                fileUri = Uri.EMPTY;
                            } catch (Exception ex ){
                                Toast.makeText(getApplicationContext()  , "Error : " + ex.toString() , Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    hideProgressDialog();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                }
            });
        }
    }

    //Check current logged in email verification status on the start of profile screen.
    @Override
    protected void onStart() {
        super.onStart();

        if(!Utils.checkEmailIsVerified()){
            binding.tvEmailStatus.setText(R.string.email_not_verify);
        }else {
            binding.tvEmailStatus.setText(R.string.email_verify);
        }
    }
}
