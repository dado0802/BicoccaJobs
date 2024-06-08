package com.app.bicoccajobs.activities.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.bicoccajobs.R;
import com.app.bicoccajobs.activities.shop.CreatePostActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ImageViewHolder>{
    private Context mcontext ;
    private List<String> muploadList;
    int position;

    public ItemListAdapter(Context context , List<String> uploadList ) {
        mcontext = context ;
        muploadList = uploadList ;


    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mcontext).inflate(R.layout.image_layout , parent , false);
        return (new ImageViewHolder(v));
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, @SuppressLint("RecyclerView") final int position) {

     final String uploadCurrent = muploadList.get(position);

     Picasso.with(mcontext).load(uploadCurrent).placeholder(R.drawable.ic_logo).into(holder.itemPic);
     Picasso.with(mcontext).load(R.drawable.ic_close).placeholder(R.drawable.ic_close).into(holder.imgCancel);

     holder.imgCancel.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             FirebaseStorage mStorage;
             mStorage = FirebaseStorage.getInstance();
             StorageReference imgRef = mStorage.getReferenceFromUrl(uploadCurrent);
             imgRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                 @Override
                 public void onSuccess(Void aVoid) {
                     CreatePostActivity.urlList.remove(position);
                     muploadList.remove(uploadCurrent);
                     notifyDataSetChanged();
                 }
             });
         }
     });

    }
    @Override
    public int getItemCount() {
        return muploadList.size();
    }

    public static  class ImageViewHolder extends RecyclerView.ViewHolder{
        public ImageView itemPic;
        public ImageView imgCancel;

        public ImageViewHolder(View itemView) {
            super(itemView);

            itemPic = itemView.findViewById(R.id.img);
            imgCancel = itemView.findViewById(R.id.imgCancel);

        }
    }

//    public void deletItems(List<Upload> user_selection) {
//        for(Upload upload : user_selection){
//            Toast.makeText(mcontext, ""+upload.getItemName(), Toast.LENGTH_SHORT).show();
//        }
//        notifyDataSetChanged();
//    }

}
