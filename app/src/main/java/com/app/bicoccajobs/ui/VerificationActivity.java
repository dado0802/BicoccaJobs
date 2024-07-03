package com.app.bicoccajobs.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.app.bicoccajobs.R;
import com.app.bicoccajobs.data.database_controllers.DatabaseUploader;
import com.app.bicoccajobs.databinding.ActivityVerificationBinding;
import com.app.bicoccajobs.data.listeners.OnTaskListeners;
import com.app.bicoccajobs.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//Email verification screen..
public class VerificationActivity extends AppCompatActivity {

    ActivityVerificationBinding binding;
    FirebaseAuth mAuth;
    public  static boolean emailVerified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
    }

    public void onResendClick(View view) {
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        DatabaseUploader.sendVerification(firebaseUser, new OnTaskListeners() {
            @Override
            public void onTaskSuccess() {
                Toast.makeText(getApplicationContext(), R.string.email_sent + firebaseUser.getEmail(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onTaskFail(String e) {
                Toast.makeText(getApplicationContext(),e, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onVerifyClick(View view) {
        SignInAndVerify(AuthenticationActivity.email , AuthenticationActivity.password);
    }

    //Checks user verify email using link or not
    private void SignInAndVerify(String email, String password) {
        if(Utils.checkEmailIsVerified()) {
            emailVerified = true;
            Toast.makeText(getApplicationContext(), R.string.email_verify, Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
        } else {
            emailVerified = false;
            Toast.makeText(getApplicationContext(), R.string.email_not_verify_2, Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
        }
    }
}
