package com.app.bicoccajobs.activities.student;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.app.bicoccajobs.R;
import com.app.bicoccajobs.activities.adapters.SliderAdapter;
import com.app.bicoccajobs.models.PostModelClass;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostDetailsActivity extends AppCompatActivity {

    PostModelClass post;
    List<String> plants;
    ViewPager viewPager;
    TabLayout indicator;
    ImageView imgBack, imgUserPic;
    TextView tvUserName, tvPlantName, tvAddress, tvDescription;
    Button btnEmail;
    public static Uri imageUri = Uri.EMPTY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);


        post = ViewJobPostsActivity.post;
        plants = post.getUrlList();

        imageUri = Uri.parse(plants.get(0));

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        indicator = (TabLayout) findViewById(R.id.indicator);
        imgBack = findViewById(R.id.imgBack);
        imgUserPic = findViewById(R.id.imgUserPic);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserName.setText(post.getUserName());
        tvPlantName = findViewById(R.id.tvPlantName);
        tvPlantName.setText(post.getTitle()+"  ");
        tvAddress = findViewById(R.id.tvAddress);
        tvAddress.setText(post.getUserAddress());
        tvDescription = findViewById(R.id.tvDescription);
        tvDescription.setText(post.getDescription());
        btnEmail = findViewById(R.id.btnEmail);

        if(!post.getUserPic().isEmpty()){
            Picasso.with(this).load(post.getUserPic()).placeholder(R.drawable.holder).into(imgUserPic);
        }

        viewPager.setAdapter(new SliderAdapter(PostDetailsActivity.this, plants));
        indicator.setupWithViewPager(viewPager, true);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnEmail.setOnClickListener(new View.OnClickListener() {
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