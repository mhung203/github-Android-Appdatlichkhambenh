package com.example.app_dat_lich_kham_benh.api.service;

import com.example.app_dat_lich_kham_benh.api.dto.BacSiDTO;
import com.example.app_dat_lich_kham_benh.api.dto.ChatContactDTO;
import com.example.app_dat_lich_kham_benh.api.dto.LichHenDTO;
import com.example.app_dat_lich_kham_benh.api.dto.LoginRequestDTO;
import com.example.app_dat_lich_kham_benh.api.model.BacSi;
import com.example.app_dat_lich_kham_benh.api.model.BenhNhan;
import com.example.app_dat_lich_kham_benh.api.model.CaKham;
import com.example.app_dat_lich_kham_benh.api.model.Khoa;
import com.example.app_dat_lich_kham_benh.api.model.LichHen;
import com.example.app_dat_lich_kham_benh.api.model.PhongKham;
import com.example.app_dat_lich_kham_benh.api.model.ThongBao;
import com.example.app_dat_lich_kham_benh.api.model.TinNhan;
import com.example.app_dat_lich_kham_benh.api.model.TinTuc;
import com.example.app_dat_lich_kham_benh.api.model.User;

import java.math.BigDecimal;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // User endpoints
    @POST("api/users/register")
    Call<User> createUser(@Body User user);

    @POST("api/users/login")
    Call<User> login(@Body LoginRequestDTO loginRequest);

    @GET("api/users/{id}")
    Call<User> getUserById(@Path("id") int id);

    @GET("api/users")
    Call<List<User>> getUsers();

    @PUT("api/users/{id}")
    Call<User> updateUser(@Path("id") int id, @Body User user);

    @DELETE("api/users/{id}")
    Call<Void> deleteUser(@Path("id") int id);

    // Khoa endpoints
    @GET("api/khoa/{id}")
    Call<Khoa> getKhoaById(@Path("id") int id);

    @GET("api/khoa")
    Call<List<Khoa>> getAllKhoa();

    @GET("api/khoa/search")
    Call<List<Khoa>> searchKhoa(@Query("q") String query);

    @GET("api/bac-si/{id}")
    Call<BacSi> getBacSiById(@Path("id") int id);

    @GET("api/bac-si/user/{userId}")
    Call<BacSi> getBacSiByUserId(@Path("userId") int userId);

    @GET("api/bac-si")
    Call<List<BacSi>> getAllBacSi();

    @GET("api/bac-si/khoa/{khoaId}")
    Call<List<BacSi>> getBacSiByKhoa(@Path("khoaId") int khoaId);

    @POST("api/bac-si")
    Call<BacSi> createBacSi(@Body BacSiDTO bacSi);

    @GET("api/benh-nhan/{userId}")
    Call<BenhNhan> getBenhNhanByUserId(@Path("userId") int userId);

    @POST("api/benh-nhan")
    Call<BenhNhan> createBenhNhan(@Body BenhNhan benhNhan);

    @POST("api/lich-hen")
    Call<LichHen> createLichHen(@Body LichHenDTO lichHen);

    @GET("api/lich-hen/benh-nhan/{userId}")
    Call<List<LichHen>> getLichHenByUserId(@Path("userId") int userId);

    @GET("api/bac-si/lich-kham")
    Call<List<LichHen>> getLichKham(@Query("bacSiId") int bacSiId, @Query("date") String date);

    @GET("api/lich-hen/{id}")
    Call<LichHen> getLichHenById(@Path("id") int id);

    @GET("api/ca-kham")
    Call<List<CaKham>> getAvailableCa(@Query("bacSiId") int bacSiId, @Query("ngay") String ngay);

    @GET("api/phong-kham/{id}")
    Call<PhongKham> getPhongKhamById(@Path("id") int id);

    @GET("api/payment/create_payment")
    Call<String> createPaymentUrl(@Query("lichHenId") int lichHenId, @Query("soTien") BigDecimal soTien);
    @GET("api/chat/contacts/{myId}")
    Call<List<ChatContactDTO>> getChatContacts(@Path("myId") int myId);
    @GET("api/chat/all-doctors")
    Call<List<ChatContactDTO>> getAllDoctors();
    @GET("api/chat/all-patients")
    Call<List<ChatContactDTO>> getAllPatients();
    @GET("api/messages/{senderId}/{recipientId}")
    Call<List<TinNhan>> getChatHistory(@Path("senderId") int senderId, @Path("recipientId") int recipientId);
    @GET("api/news/latest")
    Call<List<TinTuc>> getLatestNews();
    @GET("api/notifications/{userId}")
    Call<List<ThongBao>> getNotifications(@Path("userId") int userId);
    @PUT("api/notifications/read/{id}")
    Call<Void> markAsRead(@Path("id") int id);
    @GET("api/lich-hen/admin/all")
    Call<List<LichHen>> getLichHenAdmin(@Query("date") String date);
    @PUT("api/lich-hen/huy/{id}")
    Call<LichHen> huyLichHen(@Path("id") int id);
    @POST("api/khoa")
    Call<Khoa> createKhoa(@Body Khoa khoa);

    @DELETE("api/khoa/{id}")
    Call<Void> deleteKhoa(@Path("id") int id);
}
