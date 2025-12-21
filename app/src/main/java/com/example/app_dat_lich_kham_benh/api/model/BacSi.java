package com.example.app_dat_lich_kham_benh.api.model;

import com.google.gson.annotations.SerializedName;

public class BacSi {
    @SerializedName("bacSiId")
    private Integer bacSiId;

    @SerializedName("bangCap")
    private String bangCap;

    @SerializedName("kinhNghiem")
    private Integer kinhNghiem;

    @SerializedName("user")
    private User user;

    @SerializedName("khoa")
    private Khoa khoa;

    public Integer getBacSiId() {
        return bacSiId;
    }

    public void setBacSiId(Integer bacSiId) {
        this.bacSiId = bacSiId;
    }

    public String getBangCap() {
        return bangCap;
    }

    public void setBangCap(String bangCap) {
        this.bangCap = bangCap;
    }

    public Integer getKinhNghiem() {
        return kinhNghiem;
    }

    public void setKinhNghiem(Integer kinhNghiem) {
        this.kinhNghiem = kinhNghiem;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Khoa getKhoa() {
        return khoa;
    }

    public void setKhoa(Khoa khoa) {
        this.khoa = khoa;
    }
}
