package com.example.app_dat_lich_kham_benh.api.model;

public class TinTuc {
    private Integer id;
    private String tieuDe;      // Tiêu đề
    private String hinhAnh;     // Link ảnh
    private String moTaNgan;    // Mô tả ngắn
    private String linkBaiViet; // Link gốc (để click vào)
    // private String ngayDang; // Tạm thời chưa cần hiển thị ngày

    // Constructor, Getter, Setter
    public String getTieuDe() { return tieuDe; }
    public String getHinhAnh() { return hinhAnh; }
    public String getMoTaNgan() { return moTaNgan; }
    public String getLinkBaiViet() { return linkBaiViet; }
}