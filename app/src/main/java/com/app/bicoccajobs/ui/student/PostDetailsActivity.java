package com.app.bicoccajobs.ui.student;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.app.bicoccajobs.R;
import com.app.bicoccajobs.adapters.SliderAdapter;
import com.app.bicoccajobs.databinding.ActivityPostDetailsBinding;
import com.app.bicoccajobs.models.PostModelClass;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostDetailsActivity extends AppCompatActivity {

    ActivityPostDetailsBinding binding;
    PostModelClass post;
    List<String> plants;
    public static Uri imageUri = Uri.EMPTY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        post = ViewJobPostsActivity.post;
        plants = post.getUrlList();

        imageUri = Uri.parse(plants.get(0));

        if(!post.getUserPic().isEmpty()){
            Picasso.with(this).load(post.getUserPic()).placeholder(R.drawable.holder).into(binding.imgUserPic);
        }

        binding.tvUserName.setText(post.getUserName());
        binding.tvPost.setText(post.getTitle());
        binding.tvDescription.setText(post.getDescription());
        binding.tvAddress.setText(post.getUserAddress());

        binding.viewPager.setAdapter(new SliderAdapter(PostDetailsActivity.this, plants));
        binding.indicator.setupWithViewPager(binding.viewPager, true);

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = post.getUserEmail();
                String[] recipients = new String[]{email};

                String subject = "Job Application";

                // Email body
                String body = "Hello, I am interested in "+post.getTitle()+" job at "+post.getUserName();

                // Creating an Intent
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", recipients[0], null));

                // Adding the subject and body to the Intent
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                emailIntent.putExtra(Intent.EXTRA_TEXT, body);

                // Optionally, you can add multiple recipients
                emailIntent.putExtra(Intent.EXTRA_EMAIL, recipients);

                // Verifying if there's any activity that can handle the Intent
                if (emailIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                } else {
                    // Handle case where no email app is available
                    // You can show a message to the user or take other actions
                }
            }
        });
    }

    private ProgressDialog mProgressDialog;
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Please wait..");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}