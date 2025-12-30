package com.example.app_dat_lich_kham_benh.api.model;

public class TinNhan {
    private Long id;
    private Integer nguoiGuiId;
    private Integer nguoiNhanId;
    private String noiDung;
    private String thoiGian;
    public Integer getNguoiGuiId() { return nguoiGuiId; }
    public String getNoiDung() { return noiDung; }
    public String getThoiGian() { return thoiGian; }
    public void setNguoiGuiId(Integer nguoiGuiId) { this.nguoiGuiId = nguoiGuiId; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
    public void setThoiGian(String thoiGian) { this.thoiGian = thoiGian; }

    public Integer getNguoiNhanId() {
        return nguoiNhanId;
    }

    public void setNguoiNhanId(Integer nguoiNhanId) {
        this.nguoiNhanId = nguoiNhanId;
    }
}