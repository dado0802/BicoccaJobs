package com.app.bicoccajobs.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.app.bicoccajobs.R;
import com.app.bicoccajobs.data.database_controllers.DatabaseUploader;
import com.app.bicoccajobs.databinding.ActivityAuthBinding;
import com.app.bicoccajobs.data.listeners.OnTaskListeners;
import com.app.bicoccajobs.models.ShopModelCLass;
import com.app.bicoccajobs.models.StudentModelCLass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//Sign up screen..
public class AuthenticationActivity extends BaseActivity {
    ActivityAuthBinding binding;
    private FirebaseAuth mAuth;
    public static String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //All the Views of screen and firebase initialization..
        mAuth = FirebaseAuth.getInstance();
    }
    public void onLoginClick(View view) {
        finish();
    }
    public void onSignUpClick(View view) {
        email = binding.edtEmail.getText().toString().trim();
        password = binding.editPassword.getText().toString().trim();
        if (email.isEmpty()) {
            binding.edtEmail.setError(getString(R.string.required));
            binding.edtEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            binding.editPassword.setError(getString(R.string.required));
            binding.editPassword.requestFocus();
            return;
        }
        if (binding.editConfirmPassword.getText().toString().isEmpty()) {
            binding.editConfirmPassword.setError(getString(R.string.required));
            binding.editConfirmPassword.requestFocus();
            return;
        }
        if(!password.equals(binding.editConfirmPassword.getText().toString())){
            binding.editConfirmPassword.setError(getString(R.string.no_match));
            binding.editConfirmPassword.requestFocus();
            return;
        }
        if(Patterns.EMAIL_ADDRESS.matcher(email).matches()==false){
            binding.edtEmail.setError(getString(R.string.invalid_email_format));
            binding.edtEmail.requestFocus();
            return;
        }
        createAccount(email, password);
    }
    // This function create user account in firebase authentication with email and password..
    private void createAccount(final String email, final String password) {
        showProgressDialog(getString(R.string.please_wait));
        DatabaseUploader.setAuthToFirebase(email, password, mAuth, new OnTaskListeners() {
            @Override
            public void onTaskSuccess() {
                sendVerification();
                hideProgressDialog();
            }

            @Override
            public void onTaskFail(String e) {
                Toast.makeText(getApplicationContext(),e, Toast.LENGTH_LONG).show();
                hideProgressDialog();
            }
        });

    }

    //After storing email and password to firebase send verification email to user and move to verification screen..
    private void sendVerification() {
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        DatabaseUploader.sendVerification(firebaseUser, new OnTaskListeners() {
            @Override
            public void onTaskSuccess() {
                saveData();
            }

            @Override
            public void onTaskFail(String e) {
                Toast.makeText(getApplicationContext(),e, Toast.LENGTH_LONG).show();
            }
        });
    }

    //This function create and save user details in firebase realtime database after user successful authentication.
    private void saveData() {
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String userId = firebaseUser.getUid();

                    if(SelectionActivity.key.equals(R.string.shop)){

                        ShopModelCLass model = new ShopModelCLass(userId,"","",email,"","",password,SelectionActivity.key,false);
                        DatabaseUploader.setShopRecord(model, new OnTaskListeners() {
                            @Override
                            public void onTaskSuccess() {
                                Toast.makeText(getApplicationContext(), R.string.account_created, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext() , VerificationActivity.class);
                                startActivity(intent);
                            }
                            @Override
                            public void onTaskFail(String e) {
                                Toast.makeText(getApplicationContext(),e, Toast.LENGTH_LONG).show();
                            }
                        });
                    }else if(SelectionActivity.key.equals(R.string.students)){
                        StudentModelCLass model = new StudentModelCLass(userId,"","",email,"","",password,SelectionActivity.key,false);
                        DatabaseUploader.setStudentRecord(model, new OnTaskListeners() {
                            @Override
                            public void onTaskSuccess() {
                                Toast.makeText(getApplicationContext(), R.string.account_created, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext() , VerificationActivity.class);
                                startActivity(intent);
                            }

                            @Override
                            public void onTaskFail(String e) {
                                Toast.makeText(getApplicationContext(),e, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        });
    }
}