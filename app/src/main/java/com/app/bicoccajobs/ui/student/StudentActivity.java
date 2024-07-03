package com.app.bicoccajobs.ui.student;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.app.bicoccajobs.R;
import com.app.bicoccajobs.data.repository.Repository;
import com.app.bicoccajobs.ui.BaseActivity;
import com.app.bicoccajobs.ui.SelectionActivity;
import com.app.bicoccajobs.databinding.ActivityStudentBinding;
import com.app.bicoccajobs.data.listeners.OnStudentLoadListener;
import com.app.bicoccajobs.models.StudentModelCLass;
import com.app.bicoccajobs.utils.SharedPref;
import com.google.firebase.auth.FirebaseAuth;

//Mechanic Home Page Screen Activity File.
public class StudentActivity extends BaseActivity {

    ActivityStudentBinding binding;
    public static String fullName, pic, phone, address, userType, email, password;
    public static boolean emailVerified;
    String userId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        SharedPref.setStudent(StudentActivity.this);
    }

    public void onViewPostClick(View view) {
        startActivity(new Intent(getApplicationContext(), ViewJobPostsActivity.class));
    }

    public void onViewProfileClick2(View view) {
        Intent intent = new Intent(getApplicationContext(), StudentProfileActivity.class);
        startActivity(intent);
    }
    public void onSignOutClick2(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(StudentActivity.this);
        builder.setTitle(R.string.confirm);
        builder.setMessage(R.string.sign_out_conf).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedPref = getSharedPreferences("studentInfo", Context.MODE_PRIVATE);
                sharedPref.edit().clear().apply();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), SelectionActivity.class));
                finish();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();


        showProgressDialog(getString(R.string.prepearing));
        Repository.getStudentData(userId, new OnStudentLoadListener() {
            @Override
            public void onStudentLoad(StudentModelCLass model) {
                fullName = model.getFullName();
                pic = model.getPic();
                address = model.getAddress();
                phone = model.getPhone();
                password = model.getPassword();
                email = model.getEmail();
                userType = model.getUserType();
                emailVerified = model.isEmailVerified();

                if(fullName.equals("")){
                    binding.tvName.setText(R.string.account_not_complete);
                    AlertDialog.Builder builder = new AlertDialog.Builder(StudentActivity.this);
                    builder.setTitle("Alert!");
                    builder.setMessage(R.string.complete_profile).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }else {
                    binding.tvName.setText(fullName);
                }
                hideProgressDialog();

            }

            @Override
            public void onEmpty(String message) {
                Toast.makeText(StudentActivity.this, message, Toast.LENGTH_SHORT).show();
                hideProgressDialog();
            }

            @Override
            public void onFailure(String e) {
                Toast.makeText(StudentActivity.this, e, Toast.LENGTH_SHORT).show();
                hideProgressDialog();
            }
        });
    }
}