package com.example.app_dat_lich_kham_benh.api.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Date;

public class LichHen implements Serializable {

    @SerializedName("lichHenId")
    private int lichHenId;

    @SerializedName("idPhong") // Added this field
    private int idPhong;

    @SerializedName("chiPhi")
    private double chiPhi;

    @SerializedName("lyDoKham")
    private String lyDoKham;

    @SerializedName("ngay")
    private String ngay;

    @SerializedName("ngayHen")
    private Date ngayHen;

    @SerializedName("timeEnd")
    private String timeEnd;

    @SerializedName("timeStart")
    private String timeStart;

    @SerializedName("trangThai")
    private String trangThai;

    @SerializedName("bacSi")
    private BacSi bacSi;

    @SerializedName("benhNhan")
    private BenhNhan benhNhan;

    // Getters and Setters

    public int getLichHenId() {
        return lichHenId;
    }

    public void setLichHenId(int lichHenId) {
        this.lichHenId = lichHenId;
    }

    public int getIdPhong() {
        return idPhong;
    }

    public void setIdPhong(int idPhong) {
        this.idPhong = idPhong;
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

    public String getNgay() {
        return ngay;
    }

    public void setNgay(String ngay) {
        this.ngay = ngay;
    }

    public Date getNgayHen() {
        return ngayHen;
    }

    public void setNgayHen(Date ngayHen) {
        this.ngayHen = ngayHen;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public BacSi getBacSi() {
        return bacSi;
    }

    public void setBacSi(BacSi bacSi) {
        this.bacSi = bacSi;
    }

    public BenhNhan getBenhNhan() {
        return benhNhan;
    }

    public void setBenhNhan(BenhNhan benhNhan) {
        this.benhNhan = benhNhan;
    }
}
