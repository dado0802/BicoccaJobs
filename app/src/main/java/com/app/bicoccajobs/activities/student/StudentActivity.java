package com.app.bicoccajobs.activities.student;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.app.bicoccajobs.R;
import com.app.bicoccajobs.activities.BaseActivity;
import com.app.bicoccajobs.activities.SelectionActivity;
import com.app.bicoccajobs.models.StudentModelCLass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//Mechanic Home Page Screen Activity File.
public class StudentActivity extends BaseActivity {

    Button btnViewRequest, btnProfile, btnSignOut;
    public static String fullName, pic, phone, address, userType, email, password;
    public static boolean emailVerified;
    String userId="";
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    TextView tvName;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        //Firebase realtime database initialization for Mechanic.
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("StudentsData").child(userId);

        //Storing mechanic login state in SharedPreferences.
        sharedPref = getSharedPreferences("studentInfo", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putBoolean("statusStudent",true);
        editor.apply();

        //Views initialization of mechanic screen.
        tvName = findViewById(R.id.tvName);
        btnViewRequest = findViewById(R.id.btnViewRequest);
        btnProfile = findViewById(R.id.btnProfile);
        btnSignOut = findViewById(R.id.btnSignOut);

        //View driver's request button code.
        btnViewRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ViewJobPostsActivity.class));
            }
        });

        //Mechanic profile screen button code
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), StudentProfileActivity.class));
            }
        });

        //Sign Out button code.
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StudentActivity.this);
                builder.setTitle(R.string.confirm);
                builder.setMessage(R.string.sign_out_conf).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.clear();
                        editor.apply();
                        databaseReference.child("token").setValue("null");
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
        });
    }

    //Getting current logged in mechanic data from firebase realtime database.
    private void loadUserData() {

        showProgressDialog(getString(R.string.prepearing));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StudentModelCLass model = snapshot.getValue(StudentModelCLass.class);
                fullName = model.getFullName();
                pic = model.getPic();
                address = model.getAddress();
                phone = model.getPhone();
                password = model.getPassword();
                email = model.getEmail();
                userType = model.getUserType();
                emailVerified = model.isEmailVerified();


                for(DataSnapshot snapshot1 : snapshot.getChildren()){ }

                if(fullName.equals("")){
                    tvName.setText(R.string.account_not_complete);
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
                    tvName.setText(fullName);
                }

                hideProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { hideProgressDialog();}});
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadUserData();
    }
}