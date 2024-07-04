package com.app.bicoccajobs.models;

import java.io.Serializable;
import java.util.List;

public class PostModelClass implements Serializable {
    private String id;
    private List<String> urlList;
    private String title;
    private String description;
    private String userId;
    private String userName;
    private String userPic;
    private String userEmail;
    private String userAddress;
    private String flag;

    public PostModelClass() { }

    public PostModelClass(String id, List<String> urlList, String title, String description, String userId,
                          String userName, String userPic, String userEmail, String userAddress, String flag) {
        this.id = id;
        this.urlList = urlList;
        this.title = title;
        this.description = description;
        this.userId = userId;
        this.userName = userName;
        this.userPic = userPic;
        this.userEmail = userEmail;
        this.userAddress = userAddress;
        this.flag = flag;
    }

    public String getId() {
        return id;
    }

    public List<String> getUrlList() {
        return urlList;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPic() {
        return userPic;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public String getFlag() {
        return flag;
    }
}
