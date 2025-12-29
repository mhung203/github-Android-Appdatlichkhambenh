package com.example.app_dat_lich_kham_benh.api.dto;

public class BacSiDTO {
    private int userId;
    private int khoaId;
    private String bangCap;
    private String kinhNghiem;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getKhoaId() {
        return khoaId;
    }

    public void setKhoaId(int khoaId) {
        this.khoaId = khoaId;
    }

    public String getBangCap() {
        return bangCap;
    }

    public void setBangCap(String bangCap) {
        this.bangCap = bangCap;
    }

    public String getKinhNghiem() {
        return kinhNghiem;
    }

    public void setKinhNghiem(String kinhNghiem) {
        this.kinhNghiem = kinhNghiem;
    }
}
