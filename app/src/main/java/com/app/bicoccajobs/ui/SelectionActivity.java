package com.app.bicoccajobs.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.app.bicoccajobs.R;
import com.app.bicoccajobs.ui.shop.ShopActivity;
import com.app.bicoccajobs.ui.shop.ShopLoginActivity;
import com.app.bicoccajobs.ui.student.StudentActivity;
import com.app.bicoccajobs.ui.student.StudentLoginActivity;
import com.app.bicoccajobs.utils.SharedPref;

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

        if(SharedPref.getStudent(SelectionActivity.this)){
            startActivity(new Intent(getApplicationContext(), StudentActivity.class));
            finish();
        }
        if(SharedPref.getShop(SelectionActivity.this)){
            startActivity(new Intent(getApplicationContext(), ShopActivity.class));
            finish();
        }

    }
}