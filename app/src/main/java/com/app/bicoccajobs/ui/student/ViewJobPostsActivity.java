package com.app.bicoccajobs.ui.student;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;

import com.app.bicoccajobs.R;
import com.app.bicoccajobs.data.repository.Repository;
import com.app.bicoccajobs.ui.BaseActivity;
import com.app.bicoccajobs.adapters.PostListAdapter;
import com.app.bicoccajobs.databinding.ActivityViewJobPostsBinding;
import com.app.bicoccajobs.data.listeners.OnMyPostsLoadListener;
import com.app.bicoccajobs.models.PostModelClass;

import java.util.List;

public class ViewJobPostsActivity extends BaseActivity {

    ActivityViewJobPostsBinding binding;

    PostListAdapter adapter;
    public static PostModelClass post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewJobPostsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        binding.recyclerView.setLayoutManager(gridLayoutManager);

        binding.imgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        binding.textView.setText("");
        binding.recyclerView.setAdapter(null);
        Repository.getAllPostsData(new OnMyPostsLoadListener() {
            @Override
            public void onPostsLoad(List<PostModelClass> list) {
                if(list.size()>0){
//                    Collections.reverse(list);
                    adapter = new PostListAdapter(ViewJobPostsActivity.this, list);
                    binding.recyclerView.setAdapter(adapter);
                }else {
                    binding.textView.setText(R.string.no_post_added);
                }
                hideProgressDialog();
            }

            @Override
            public void onEmpty(String message) {
                hideProgressDialog();
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String e) {
                hideProgressDialog();
                Toast.makeText(getApplicationContext(), "Error : "+e, Toast.LENGTH_SHORT).show();
            }
        });

    }
}