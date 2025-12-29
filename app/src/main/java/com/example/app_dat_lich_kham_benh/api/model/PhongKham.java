package com.example.app_dat_lich_kham_benh.api.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class PhongKham implements Serializable {

    @SerializedName("idPhong")
    private int idPhong;

    @SerializedName("tenPhong")
    private String tenPhong;
    @SerializedName("viTri")
    private String viTri;
    // Getters and Setters
    public int getIdPhong() {
        return idPhong;
    }

    public void setIdPhong(int idPhong) {
        this.idPhong = idPhong;
    }

    public String getTenPhong() {
        return tenPhong;
    }

    public void setTenPhong(String tenPhong) {
        this.tenPhong = tenPhong;
    }

    public String getViTri() {
        return viTri;
    }

    public void setViTri(String viTri) {
        this.viTri = viTri;
    }
}
