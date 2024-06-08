package com.app.bicoccajobs.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.app.bicoccajobs.R;
import com.app.bicoccajobs.activities.shop.ShopActivity;
import com.app.bicoccajobs.activities.shop.ShopLoginActivity;
import com.app.bicoccajobs.activities.student.StudentActivity;
import com.app.bicoccajobs.activities.student.StudentLoginActivity;

//Select login choice screen as driver or mechanic.
public class SelectionActivity extends AppCompatActivity {

    Button btnShop, btnStudent;
    public static String key="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        btnShop = findViewById(R.id.btnShop);
        btnStudent = findViewById(R.id.btnStudent);

        btnShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                key="Shop";
                startActivity(new Intent(getApplicationContext(), ShopLoginActivity.class));
            }
        });
        btnStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                key="Student";
                startActivity(new Intent(getApplicationContext(), StudentLoginActivity.class));
            }
        });
    }

    //This method checks user login status..
    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sharedPref = getSharedPreferences("studentInfo", Context.MODE_PRIVATE);
        SharedPreferences sharedPref2 = getSharedPreferences("shopInfo", Context.MODE_PRIVATE);
        boolean statusStudent = sharedPref.getBoolean("statusStudent", false);
        boolean statusShop = sharedPref2.getBoolean("statusShop", false);

        if(statusStudent){
            startActivity(new Intent(getApplicationContext(), StudentActivity.class));
            finish();
        }
        if(statusShop){
            startActivity(new Intent(getApplicationContext(), ShopActivity.class));
            finish();
        }

    }

//    boolean isOnline(){
//        if(FirebaseAuth.getInstance().getCurrentUser() != null){
//            return true;
//        }
//        return false;
//    }
}