package com.app.bicoccajobs.ui.shop;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.app.bicoccajobs.R;
import com.app.bicoccajobs.data.repository.Repository;
import com.app.bicoccajobs.ui.BaseActivity;
import com.app.bicoccajobs.ui.SelectionActivity;
import com.app.bicoccajobs.ui.student.ViewJobPostsActivity;
import com.app.bicoccajobs.databinding.ActivityShopBinding;
import com.app.bicoccajobs.data.listeners.OnShopLoadListener;
import com.app.bicoccajobs.models.ShopModelCLass;
import com.app.bicoccajobs.utils.SharedPref;
import com.google.firebase.auth.FirebaseAuth;

public class ShopActivity extends BaseActivity {
    ActivityShopBinding binding;
    public static String userId="";
    public static String fullName,pic, phone, address, userType, email, password;
    public static boolean emailVerified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShopBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        SharedPref.setShop(ShopActivity.this);
    }

    public void onAddPostClick(View view) {
        Intent intent = new Intent(getApplicationContext(), CreatePostActivity.class);
        startActivity(intent);
    }
    public void onViewMyPostClick(View view) {
        Intent intent = new Intent(getApplicationContext(), ViewMyPostsActivity.class);
        startActivity(intent);
    }
    public void onViewAllPostsClick(View view) {
        Intent intent = new Intent(getApplicationContext(), ViewJobPostsActivity.class);//ViewAllPostsActivity.class
        startActivity(intent);
    }
    public void onViewProfileClick(View view) {
        Intent intent = new Intent(getApplicationContext(), ShopProfileActivity.class);
        startActivity(intent);
    }
    public void onSignOutClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ShopActivity.this);
        builder.setTitle(R.string.confirm);
        builder.setMessage(R.string.sign_out_conf).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedPref = getSharedPreferences("shopInfo", Context.MODE_PRIVATE);
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
        Repository.getShopData(userId, new OnShopLoadListener() {
            @Override
            public void onBarberLoad(ShopModelCLass model) {
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
                    binding.tvName.setText(fullName);
                }
                hideProgressDialog();
            }

            @Override
            public void onEmpty(String message) {
                hideProgressDialog();
                Toast.makeText(ShopActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String e) {
                hideProgressDialog();
                Toast.makeText(ShopActivity.this, e, Toast.LENGTH_SHORT).show();
            }
        });
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