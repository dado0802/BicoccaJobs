package com.app.bicoccajobs.data.listeners;

import com.app.bicoccajobs.models.ShopModelCLass;
import com.app.bicoccajobs.models.StudentModelCLass;

public interface OnStudentLoadListener {
    void onStudentLoad(StudentModelCLass studentModel);

    void onEmpty(String message);

    void onFailure(String e);
}
