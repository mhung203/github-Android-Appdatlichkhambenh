package com.example.app_dat_lich_kham_benh.api.model;

public class ThongBao {
    private Integer id;
    private Integer userId;
    private String tieuDe;
    private String noiDung;
    private String thoiGian; // Backend gửi về dạng String "2025-12-31..."
    private boolean daXem;

    // Getter & Setter
    public Integer getId() { return id; }
    public boolean isDaXem() { return daXem; }
    public String getTieuDe() { return tieuDe; }
    public String getNoiDung() { return noiDung; }
    public String getThoiGian() { return thoiGian; }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public void setThoiGian(String thoiGian) {
        this.thoiGian = thoiGian;
    }

    public void setDaXem(boolean daXem) {
        this.daXem = daXem;
    }
}