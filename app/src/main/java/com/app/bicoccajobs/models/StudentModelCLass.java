package com.app.bicoccajobs.models;

public class StudentModelCLass {
    private String userId;
    private String fullName;
    private String pic;
    private String email;
    private String phone;
    private String address;
    private String password;
    private String userType;
    private boolean emailVerified;

    public StudentModelCLass() { }

    public StudentModelCLass(String userId, String fullName, String pic, String email, String phone, String address, String password,
                             String userType, boolean emailVerified) {
        this.userId = userId;
        this.fullName = fullName;
        this.pic = pic;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.password = password;
        this.userType = userType;
        this.emailVerified = emailVerified;
    }

    public String getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }
    public String getPic() {
        return pic;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
    public String getAddress() {
        return address;
    }

    public String getPassword() {
        return password;
    }
    public String getUserType() {
        return userType;
    }
    public boolean isEmailVerified() {
        return emailVerified;
    }
}
