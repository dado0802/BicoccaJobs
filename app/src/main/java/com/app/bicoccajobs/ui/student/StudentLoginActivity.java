package com.app.bicoccajobs.ui.student;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.app.bicoccajobs.R;
import com.app.bicoccajobs.ui.AuthenticationActivity;
import com.app.bicoccajobs.ui.BaseActivity;
import com.app.bicoccajobs.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//Login Screen of Mechanic...

public class StudentLoginActivity extends BaseActivity {

    ActivityLoginBinding binding;
    private static final String TAG = "EmailPasswordActivity";
    String email, passowrd;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        //Login button code.
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Press login button and user user credentials..
                email = binding.edtEmail.getText().toString().trim();
                passowrd = binding.editPassword.getText().toString().trim();

                if(!isConnectionAvailable(StudentLoginActivity.this)){
                    Toast.makeText(StudentLoginActivity.this, "Check your network!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(email)){
                    binding.edtEmail.setError(getString(R.string.required));
                    binding.edtEmail.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(passowrd)){
                    binding.editPassword.setError(getString(R.string.required));
                    binding.editPassword.requestFocus();
                    return;
                }

                //Call signIn method to sign in user using firebase authentication..
                signIn();
            }
        });
        binding.forgotTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Forgot password email send code for reset password..
                String email = binding.edtEmail.getText().toString();
                if(TextUtils.isEmpty(email)){
                    binding.edtEmail.setError(getString(R.string.required));
                    binding.edtEmail.requestFocus();
                    return;
                }
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), R.string.email_verify, Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getApplicationContext(), "Error : "+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

               // startActivity(new Intent(getApplicationContext(), AddPlacesDataActivity.class));
            }
        });
        binding.btnRegLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start sign up screen for creating new account..
                startActivity(new Intent(getApplicationContext(), AuthenticationActivity.class));
            }
        });
    }

    //Sign in function that checks user account credentials from firebase...
    private void signIn() {
        showProgressDialog(getString(R.string.signing));
        mAuth.signInWithEmailAndPassword(email, passowrd).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                if (task.isSuccessful()) {
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    checkUserType(userId);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.incorrect_details, Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                }

            }
        });
    }

    //This function checks mobile data connection or wifi is on or off..
    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    //This function check current logging in user type that he/she is driver or mechanic at the basis of email and passowrd.
    private void checkUserType(String userId) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("StudentsData").child(userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount()>0){
                    databaseReference.child("password").setValue(passowrd);
                    Intent intent = new Intent(getApplicationContext(), StudentActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    Toast.makeText(StudentLoginActivity.this, R.string.sign_in_succesful, Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(), R.string.incorrect_details, Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                }
                hideProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { hideProgressDialog();}});
    }
}