package com.app.bicoccajobs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.app.bicoccajobs.R;
import com.app.bicoccajobs.models.ShopModelCLass;
import com.app.bicoccajobs.models.StudentModelCLass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//Sign up screen..
public class AuthenticationActivity extends BaseActivity {
    EditText edtEmail, edtPassword, edtConfirmPassword;
    Button btnRegister, btnLogRegister;
    private FirebaseAuth mAuth;
    public static String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        //All the Views of screen and firebase initialization..
        mAuth = FirebaseAuth.getInstance();

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.editPassword);
        edtConfirmPassword = findViewById(R.id.editConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogRegister = findViewById(R.id.btnLogRegister);

        //Press signUp button code..
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = edtEmail.getText().toString().trim();
                password = edtPassword.getText().toString().trim();
                if (email.isEmpty()) {
                    edtEmail.setError(getString(R.string.required));
                    edtEmail.requestFocus();
                    return;
                }
                if (password.isEmpty()) {
                    edtPassword.setError(getString(R.string.required));
                    edtPassword.requestFocus();
                    return;
                }
                if (edtConfirmPassword.getText().toString().isEmpty()) {
                    edtConfirmPassword.setError(getString(R.string.required));
                    edtConfirmPassword.requestFocus();
                    return;
                }
                if(!password.equals(edtConfirmPassword.getText().toString())){
                    edtConfirmPassword.setError(getString(R.string.no_match));
                    edtConfirmPassword.requestFocus();
                    return;
                }
                if(Patterns.EMAIL_ADDRESS.matcher(email).matches()==false){
                    edtEmail.setError(getString(R.string.invalid_email_format));
                    edtEmail.requestFocus();
                    return;
                }
                createAccount(email, password);
            }
        });

        btnLogRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    // This function create user account in firebase authentication with email and password..
    private void createAccount(final String email, final String password) {
        if (!validateForm()) {
            return;
        }

        showProgressDialog(getString(R.string.please_wait));

        //This function create user account in firebase authentication with email and password
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    sendVerification();
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
                hideProgressDialog();
            }
        });
    }

    //After storing email and password to firebase send verification email to user and move to verification screen..
    private void sendVerification() {
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    saveData();
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //Valodations on sign up form
    private boolean validateForm() {
        if (TextUtils.isEmpty(edtEmail.getText().toString())) {
            edtEmail.setError(getString(R.string.required));
            return false;
        } else if (TextUtils.isEmpty(edtPassword.getText().toString())) {
            edtPassword.setError(getString(R.string.required));
            return false;
        }else {
            edtEmail.setError(null);
            edtPassword.setError(null);
            return true;
        }
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
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ShopsData");
                        ShopModelCLass model = new ShopModelCLass(userId,"","",email,"","",password,SelectionActivity.key,false);
                        databaseReference.child(userId).setValue(model);
                    }else if(SelectionActivity.key.equals(R.string.students)){
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("StudentsData");
                        StudentModelCLass model = new StudentModelCLass(userId,"","",email,"","",password,SelectionActivity.key,false);
                        databaseReference.child(userId).setValue(model);
                    }


                    Toast.makeText(getApplicationContext(), R.string.account_created + firebaseUser.getEmail(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext() , VerificationActivity.class);
                    startActivity(intent);

                }
            }
        });
    }

}