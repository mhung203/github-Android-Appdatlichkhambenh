package com.example.app_dat_lich_kham_benh.api.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class BenhNhan implements Serializable {
    @SerializedName("benhNhanId")
    private int benhNhanId;

    @SerializedName("userId")
    private Integer userId;

    @SerializedName("medicalHistory")
    private String medicalHistory;

    @SerializedName("hoTen")
    private String hoTen;

    @SerializedName("soDienThoai")
    private String soDienThoai;

    @SerializedName("gioiTinh")
    private String gioiTinh;

    @SerializedName("user")
    private User user;

    public int getBenhNhanId() {
        return benhNhanId;
    }

    public void setBenhNhanId(int benhNhanId) {
        this.benhNhanId = benhNhanId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
