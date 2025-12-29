package com.example.app_dat_lich_kham_benh.api.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class BacSi implements Serializable {
    @SerializedName(value = "bacSiId", alternate = {"id"})
    private Integer bacSiId;

    @SerializedName("bangCap")
    private String bangCap;

    @SerializedName("kinhNghiem")
    private Integer kinhNghiem;

    @SerializedName("anhDaiDien")
    private String anhDaiDien;

    @SerializedName("user")
    private User user;

    @SerializedName("khoa")
    private Khoa khoa;
    @SerializedName("phongKham")
    private PhongKham phongKham;

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

    public String getAnhDaiDien() {
        return anhDaiDien;
    }

    public void setAnhDaiDien(String anhDaiDien) {
        this.anhDaiDien = anhDaiDien;
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
    public void setPhongKham(PhongKham phongKham) {
        this.phongKham = phongKham;
    }
    public PhongKham getPhongKham() {
        return phongKham;
    }

}
