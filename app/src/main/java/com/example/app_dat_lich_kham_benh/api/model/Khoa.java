package com.example.app_dat_lich_kham_benh.api.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Khoa implements Serializable {
    @SerializedName("khoaId")
    private Integer khoaId;

    @SerializedName("tenKhoa")
    private String tenKhoa;

    @SerializedName("moTa")
    private String moTa;

    @SerializedName("hinhAnh")
    private String hinhAnh;

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

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }
}
