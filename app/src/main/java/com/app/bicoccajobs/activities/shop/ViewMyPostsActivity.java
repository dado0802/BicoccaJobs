package com.app.bicoccajobs.activities.shop;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.bicoccajobs.R;
import com.app.bicoccajobs.models.PostModelClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewMyPostsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView textView, tv;
    DatabaseReference databaseReference;
    List<PostModelClass> list;
    EventsListAdapter adapter;
    public static PostModelClass model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_posts);

        showProgressDialog();
        //Firebase and screen views initialization..
        databaseReference = FirebaseDatabase.getInstance().getReference("JobPosts");
        list = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); ;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),1);
        recyclerView.setLayoutManager(gridLayoutManager);
        textView = findViewById(R.id.textView);
        tv = findViewById(R.id.tv);

    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                textView.setText("");
                recyclerView.setAdapter(null);
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    PostModelClass model = snapshot.getValue(PostModelClass.class);
                    if(ShopActivity.userId.equals(model.getUserId())){
                        list.add(model);
                    }
                }
                if(list.size()>0){
                    Collections.reverse(list);
                    adapter = new EventsListAdapter(ViewMyPostsActivity.this, list);
                    recyclerView.setAdapter(adapter);
                }else {
                    textView.setText(R.string.no_post_added);
                }
                hideProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideProgressDialog();
                Toast.makeText(getApplicationContext(), "Error : "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.ImageViewHolder>{
        private Context mcontext ;
        private List<PostModelClass> muploadList;

        public EventsListAdapter(Context context , List<PostModelClass> uploadList ) {
            mcontext = context ;
            muploadList = uploadList ;
        }

        @Override
        public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mcontext).inflate(R.layout.post_layout, parent , false);

            return (new ImageViewHolder(v));
        }

        @Override
        public void onBindViewHolder(final ImageViewHolder holder, @SuppressLint("RecyclerView") final int position) {

            final PostModelClass post = muploadList.get(position);
            holder.tvTitle.setText(R.string.title +post.getTitle());
            holder.tvFoodSeller.setVisibility(View.GONE);
            Picasso.with(mcontext).load(post.getUrlList().get(0)).placeholder(R.drawable.holder).into(holder.imgPic);

            holder.imgDelete.setVisibility(View.VISIBLE);

            holder.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                    builder.setTitle(R.string.confirmation);
                    builder.setMessage(R.string.delete).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseReference.child(post.getId()).removeValue();
                            list.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(), R.string.item_delete, Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return muploadList.size();
        }

        public class ImageViewHolder extends RecyclerView.ViewHolder{
            public ImageView imgPic;
            public ImageView imgDelete;
            public TextView tvTitle;
            public TextView tvFoodSeller;

            public ImageViewHolder(View itemView) {
                super(itemView);

                imgPic = itemView.findViewById(R.id.imgPic);
                imgDelete = itemView.findViewById(R.id.imgDelete);
                tvTitle = itemView.findViewById(R.id.tvPlantName);
                tvFoodSeller = itemView.findViewById(R.id.tvPlantOwner);
            }
        }
    }

    private ProgressDialog mProgressDialog;
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(ViewMyPostsActivity.this);
            mProgressDialog.setMessage(getString(R.string.loading_data));
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

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }
}