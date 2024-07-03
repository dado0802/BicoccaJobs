package com.app.bicoccajobs.data.listeners;

import com.app.bicoccajobs.models.PostModelClass;
import java.util.ArrayList;
import java.util.List;

public interface OnMyPostsLoadListener {
    void onPostsLoad(List<PostModelClass> list);

    void onEmpty(String message);

    void onFailure(String e);
}
