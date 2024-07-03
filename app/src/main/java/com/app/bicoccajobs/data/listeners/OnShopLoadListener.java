package com.app.bicoccajobs.data.listeners;

import com.app.bicoccajobs.models.ShopModelCLass;
import java.util.ArrayList;

public interface OnShopLoadListener {
    void onBarberLoad(ShopModelCLass shopModel);

    void onEmpty(String message);

    void onFailure(String e);
}
