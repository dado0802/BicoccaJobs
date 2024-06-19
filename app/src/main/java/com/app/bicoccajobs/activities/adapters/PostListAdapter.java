package com.app.bicoccajobs.activities.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.bicoccajobs.R;
import com.app.bicoccajobs.activities.student.PostDetailsActivity;
import com.app.bicoccajobs.activities.student.ViewJobPostsActivity;
import com.app.bicoccajobs.models.PostModelClass;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.ImageViewHolder>{
    private Context mcontext ;
    private List<PostModelClass> muploadList;

    public PostListAdapter(Context context , List<PostModelClass> uploadList ) {
        mcontext = context ;
        muploadList = uploadList ;

    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mcontext).inflate(R.layout.post_layout , parent , false);
        return (new ImageViewHolder(v));
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {

     final PostModelClass uploadCurrent = muploadList.get(position);

     Picasso.with(mcontext).load(uploadCurrent.getUrlList().get(0)).placeholder(R.drawable.holder).into(holder.imgPic);
     holder.tvPlantName.setText(uploadCurrent.getTitle());
     holder.tvPlantOwner.setText(uploadCurrent.getUserName());

     holder.itemView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             ViewJobPostsActivity.post = uploadCurrent;
             mcontext.startActivity(new Intent(mcontext, PostDetailsActivity.class));
         }
     });

    }

    @Override
    public int getItemCount() {
        return muploadList.size();
    }

    public static  class ImageViewHolder extends RecyclerView.ViewHolder{
        public ImageView imgPic;
        public TextView tvPlantName;
        public TextView tvPlantOwner;

        public ImageViewHolder(View itemView) {
            super(itemView);

            imgPic = itemView.findViewById(R.id.imgPic);
            tvPlantName = itemView.findViewById(R.id.tvPlantName);
            tvPlantOwner = itemView.findViewById(R.id.tvPlantOwner);
        }
    }

    public void filterList(List<PostModelClass> searchList) {
        this.muploadList = searchList;
        notifyDataSetChanged();
    }
}
