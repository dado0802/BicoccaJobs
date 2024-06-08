package com.app.bicoccajobs.activities.student;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.bicoccajobs.R;
import com.app.bicoccajobs.activities.BaseActivity;
import com.app.bicoccajobs.activities.adapters.PostListAdapter;
import com.app.bicoccajobs.models.PostModelClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewJobPostsActivity extends BaseActivity {

    RecyclerView recyclerView;
    List<PostModelClass> list;
    ImageView imgHome;
    DatabaseReference databaseReference;
    TextView textView;
    PostListAdapter adapter;
    public static PostModelClass post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_job_posts);

        databaseReference = FirebaseDatabase.getInstance().getReference("JobPosts");
        list = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(gridLayoutManager);

        textView = findViewById(R.id.textView);
        imgHome = findViewById(R.id.imgHome);

        imgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        loadData();
    }

    private void loadData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                textView.setText("");
                recyclerView.setAdapter(null);
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    PostModelClass model = snapshot.getValue(PostModelClass.class);
                    list.add(model);
                }

                if(list.size()>0){
                    adapter = new PostListAdapter(ViewJobPostsActivity.this, list);
                    recyclerView.setAdapter(adapter);
                }else {
                    textView.setText("No Post Added!");
                }
                hideProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { hideProgressDialog();}});
    }
}