package com.app.bicoccajobs.data.repository;

import androidx.annotation.NonNull;

import com.app.bicoccajobs.data.database_controllers.DatabaseAddresses;
import com.app.bicoccajobs.data.listeners.OnMyPostsLoadListener;
import com.app.bicoccajobs.data.listeners.OnShopLoadListener;
import com.app.bicoccajobs.data.listeners.OnStudentLoadListener;
import com.app.bicoccajobs.models.PostModelClass;
import com.app.bicoccajobs.models.ShopModelCLass;
import com.app.bicoccajobs.models.StudentModelCLass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Repository {
    public static void getShopData(String userId, OnShopLoadListener onShopLoadListener){
        DatabaseAddresses.getSignUpReference("ShopsData").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ShopModelCLass model = snapshot.getValue(ShopModelCLass.class);

                for(DataSnapshot snapshot1 : snapshot.getChildren()){ }

                onShopLoadListener.onBarberLoad(model);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void getStudentData(String userId, OnStudentLoadListener onStudentLoadListener){
        DatabaseAddresses.getSignUpReference("StudentsData").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StudentModelCLass model = snapshot.getValue(StudentModelCLass.class);

                for(DataSnapshot snapshot1 : snapshot.getChildren()){ }

                onStudentLoadListener.onStudentLoad(model);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void getMyPostsData(String userId, OnMyPostsLoadListener onMyPostsLoadListener){
        List<PostModelClass> list = new ArrayList<>();
        DatabaseAddresses.getJobPostReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    PostModelClass model = snapshot.getValue(PostModelClass.class);
                    if(userId.equals(model.getUserId())){
                        list.add(model);
                    }
                }

                onMyPostsLoadListener.onPostsLoad(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void getAllPostsData(OnMyPostsLoadListener onMyPostsLoadListener){
        List<PostModelClass> list = new ArrayList<>();
        DatabaseAddresses.getJobPostReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    PostModelClass model = snapshot.getValue(PostModelClass.class);
                    list.add(model);
                }

                onMyPostsLoadListener.onPostsLoad(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
