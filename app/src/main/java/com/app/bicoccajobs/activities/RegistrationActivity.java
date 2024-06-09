package com.app.bicoccajobs.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.bicoccajobs.R;
import com.app.bicoccajobs.activities.shop.ShopActivity;
import com.app.bicoccajobs.activities.student.StudentActivity;
import com.app.bicoccajobs.models.ShopModelCLass;
import com.app.bicoccajobs.models.StudentModelCLass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//Sign up form screen fro getting name, address etc from user
public class RegistrationActivity extends BaseActivity {

    EditText edtName, edtPhone, edtAddress;
    Button btnRegister;
    String fullName, phone, address;
    FirebaseAuth mAuth;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Firebase database and authentication initialization...

        mAuth = FirebaseAuth.getInstance();

        //All the Views of screen and firebase initialization..
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullName = edtName.getText().toString().trim();
                phone = edtPhone.getText().toString().trim();
                address = edtAddress.getText().toString().trim();

                if(TextUtils.isEmpty(fullName)){
                   edtName.setError(getString(R.string.required));
                   edtName.requestFocus();
                   return;
                }
                if(TextUtils.isEmpty(phone)){
                    edtPhone.setError(getString(R.string.required));
                    edtPhone.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(address)){
                    edtAddress.setError(getString(R.string.required));
                    edtAddress.requestFocus();
                    return;
                }
                createAccount();
            }
        });
    }

    //This method store all the data fields of user to firebase...
    private void createAccount(){
        if(SelectionActivity.key.equals(R.string.shop)){
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ShopsData");
            ShopModelCLass model = new ShopModelCLass(userId,fullName,"",AuthenticationActivity.email,phone,address,
                    AuthenticationActivity.password,SelectionActivity.key, VerificationActivity.emailVerified);
            databaseReference.child(userId).setValue(model);
            Toast.makeText(this, R.string.detail_saved, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), ShopActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }else if(SelectionActivity.key.equals(R.string.students)){
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("StudentsData");
            StudentModelCLass model = new StudentModelCLass(userId,fullName,"",AuthenticationActivity.email,phone,address,
                    AuthenticationActivity.password,SelectionActivity.key,VerificationActivity.emailVerified);
            databaseReference.child(userId).setValue(model);

            Toast.makeText(getApplicationContext(), R.string.detail_saved, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getApplicationContext(), StudentActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void getUserLocation() {

        showProgressDialog(getString(R.string.getting_position));

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }
            @Override
            public void onProviderEnabled(String provider) { }
            @Override
            public void onProviderDisabled(String provider) { }};

        if (Build.VERSION.SDK_INT < 23) {
            if (ActivityCompat.checkSelfPermission(RegistrationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RegistrationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1500, 50, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1500, 50, locationListener);
        } else {
            if (ContextCompat.checkSelfPermission(RegistrationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1500, 50, locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1500, 50, locationListener);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1500, 50, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1500, 50, locationListener);
                }
            }
        }
    }
}
