package com.app.bicoccajobs.activities.shop;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.app.bicoccajobs.R;
import com.app.bicoccajobs.activities.BaseActivity;
import com.app.bicoccajobs.activities.SelectionActivity;
import com.app.bicoccajobs.models.ShopModelCLass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//Driver Home Page Screen Activity File.
public class ShopActivity extends BaseActivity {

    Button btnAddPost, btnView, btnViewALl, btnProfile, btnSignOut;
    TextView tvName;
    public static String userId="";
    DatabaseReference databaseReference;
    public static String fullName,pic, phone, address, userType, email, password;
    public static boolean emailVerified;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);


        //Firebase realtime database initialization for driver.
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("ShopsData").child(userId);

        //Storing driver login state in SharedPreferences.
        sharedPref = getSharedPreferences("shopInfo", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putBoolean("statusShop",true);
        editor.apply();

        //Views initialization of driver screen.
        tvName = findViewById(R.id.tvName);
        btnAddPost = findViewById(R.id.btnAddPost);
        btnView = findViewById(R.id.btnView);
        btnViewALl = findViewById(R.id.btnViewALl);
        btnProfile = findViewById(R.id.btnProfile);
        btnSignOut = findViewById(R.id.btnSignOut);

        btnAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreatePostActivity.class);
                startActivity(intent);
            }
        });
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewMyPostsActivity.class);
                startActivity(intent);
            }
        });
        btnViewALl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewAllPostsActivity.class);
                startActivity(intent);
            }
        });
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ShopProfileActivity.class);
                startActivity(intent);
            }
        });

        //Sign Out button code
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShopActivity.this);
                builder.setTitle(R.string.confirm);
                builder.setMessage(R.string.sign_out_conf).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
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

    //Getting current logged in driver data from firebase realtime database.
    private void loadUserData() {

        showProgressDialog(getString(R.string.prepearing));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ShopModelCLass model = snapshot.getValue(ShopModelCLass.class);
                fullName = model.getFullName();
                pic = model.getPic();
                address = model.getAddress();
                phone = model.getPhone();
                password = model.getPassword();
                email = model.getEmail();
                userType = model.getUserType();
                emailVerified = model.isEmailVerified();

                for(DataSnapshot snapshot1 : snapshot.getChildren()){ }

                //Check if current logged in user complete his/her account by providing all the required data in profile screen or not.
                if(fullName.equals("")){
                    tvName.setText(R.string.account_not_complete);
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShopActivity.this);
                    builder.setTitle("Alert!");
                    builder.setMessage(getString(R.string.account_not_complete_popup)).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                }
            }
        }
    }

}