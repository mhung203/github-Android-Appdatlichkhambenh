package com.example.app_dat_lich_kham_benh.api.model;

import com.google.gson.annotations.SerializedName;

public class Khoa {
    @SerializedName("khoaId")
    private Integer khoaId;

    @SerializedName("tenKhoa")
    private String tenKhoa;

    @SerializedName("moTa")
    private String moTa;

    // Getters and Setters
    public Integer getKhoaId() {
        return khoaId;
    }

    public void setKhoaId(Integer khoaId) {
        this.khoaId = khoaId;
    }

    public String getTenKhoa() {
        return tenKhoa;
    }

    public void setTenKhoa(String tenKhoa) {
        this.tenKhoa = tenKhoa;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
}
