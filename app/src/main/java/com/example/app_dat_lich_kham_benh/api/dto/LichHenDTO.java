package com.example.app_dat_lich_kham_benh.api.dto;

import com.google.gson.annotations.SerializedName;

// This DTO is structured to match the backend's LichHenController expectations
public class LichHenDTO {

    @SerializedName("userId")
    private int userId;

    @SerializedName("bacSiId") // Corrected from idBacSi
    private int bacSiId;

    @SerializedName("idCa")
    private int idCa;

    @SerializedName("idPhong")
    private int idPhong;

    @SerializedName("ngayKham") // Corrected from ngay
    private String ngayKham;

    @SerializedName("timeStart")
    private String timeStart;

    @SerializedName("timeEnd")
    private String timeEnd;

    @SerializedName("chiPhi")
    private double chiPhi;

    @SerializedName("lyDoKham")
    private String lyDoKham;

    // Getters and Setters

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBacSiId() {
        return bacSiId;
    }

    public void setBacSiId(int bacSiId) {
        this.bacSiId = bacSiId;
    }

    public int getIdCa() {
        return idCa;
    }

    public void setIdCa(int idCa) {
        this.idCa = idCa;
    }

    public int getIdPhong() {
        return idPhong;
    }

    public void setIdPhong(int idPhong) {
        this.idPhong = idPhong;
    }

    public String getNgayKham() {
        return ngayKham;
    }

    public void setNgayKham(String ngayKham) {
        this.ngayKham = ngayKham;
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

    public double getChiPhi() {
        return chiPhi;
    }

    public void setChiPhi(double chiPhi) {
        this.chiPhi = chiPhi;
    }

    public String getLyDoKham() {
        return lyDoKham;
    }

    public void setLyDoKham(String lyDoKham) {
        this.lyDoKham = lyDoKham;
    }
}
