package com.example.app_dat_lich_kham_benh.api.model;

import com.google.gson.annotations.SerializedName;

public class CaKham {

    @SerializedName("id")
    private int id;

    @SerializedName("tenCa")
    private String tenCa;

    @SerializedName("ngay")
    private String ngay;

    @SerializedName("timeStart")
    private String timeStart;

    @SerializedName("timeEnd")
    private String timeEnd;

    @SerializedName("trangThai")
    private String trangThai;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTenCa() {
        return tenCa;
    }

    public void setTenCa(String tenCa) {
        this.tenCa = tenCa;
    }

    public String getNgay() {
        return ngay;
    }

    public void setNgay(String ngay) {
        this.ngay = ngay;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
