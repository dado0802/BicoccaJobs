package com.app.bicoccajobs.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.app.bicoccajobs.R;
import com.app.bicoccajobs.ui.shop.ShopActivity;
import com.app.bicoccajobs.ui.student.StudentActivity;
import com.app.bicoccajobs.data.database_controllers.DatabaseUploader;
import com.app.bicoccajobs.databinding.ActivityRegistrationBinding;
import com.app.bicoccajobs.data.listeners.OnTaskListeners;
import com.app.bicoccajobs.models.ShopModelCLass;
import com.app.bicoccajobs.models.StudentModelCLass;
import com.google.firebase.auth.FirebaseAuth;

//Sign up form screen fro getting name, address etc from user
public class RegistrationActivity extends BaseActivity {

    ActivityRegistrationBinding binding;
    String fullName, phone, address;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
    }

    public void onRegisterClick(View view) {
        fullName = binding.edtName.getText().toString().trim();
        phone = binding.edtPhone.getText().toString().trim();
        address = binding.edtAddress.getText().toString().trim();

        if(TextUtils.isEmpty(fullName)){
            binding.edtName.setError(getString(R.string.required));
            binding.edtName.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(phone)){
            binding.edtPhone.setError(getString(R.string.required));
            binding.edtPhone.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(address)){
            binding.edtAddress.setError(getString(R.string.required));
            binding.edtAddress.requestFocus();
            return;
        }
        createAccount();
    }

    //This method store all the data fields of user to firebase...
    private void createAccount(){
        if(SelectionActivity.key.equals("Shop")){
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            ShopModelCLass model = new ShopModelCLass(userId,fullName,"",AuthenticationActivity.email,phone,address,
                    AuthenticationActivity.password,SelectionActivity.key, VerificationActivity.emailVerified);
            DatabaseUploader.setShopRecord(model, new OnTaskListeners() {
                @Override
                public void onTaskSuccess() {
                    Toast.makeText(getApplicationContext(), R.string.detail_saved, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), ShopActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                @Override
                public void onTaskFail(String e) {
                    Toast.makeText(getApplicationContext(),e, Toast.LENGTH_LONG).show();
                }
            });
        }else if(SelectionActivity.key.equals("Student")){
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StudentModelCLass model = new StudentModelCLass(userId,fullName,"",AuthenticationActivity.email,phone,address,
                    AuthenticationActivity.password,SelectionActivity.key,VerificationActivity.emailVerified);
            DatabaseUploader.setStudentRecord(model, new OnTaskListeners() {
                @Override
                public void onTaskSuccess() {
                    Toast.makeText(getApplicationContext(), R.string.detail_saved, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), StudentActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onTaskFail(String e) {
                    Toast.makeText(getApplicationContext(),e, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
