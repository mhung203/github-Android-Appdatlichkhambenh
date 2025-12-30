package com.example.app_dat_lich_kham_benh.ui.booking;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.api.ApiClient;
import com.example.app_dat_lich_kham_benh.api.model.LichHen;
import com.example.app_dat_lich_kham_benh.api.service.ApiService;
import com.example.app_dat_lich_kham_benh.ui.MainActivity;
import com.example.app_dat_lich_kham_benh.ui.payment.PaymentActivity;
import com.google.android.material.appbar.MaterialToolbar;

import java.math.BigDecimal;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmationActivity extends AppCompatActivity {

    private static final String TAG = "ConfirmationActivity";

    private TextView tvDoctorName, tvSpecialtyName, tvClinicName, tvAppointmentDate, tvAppointmentTime, tvReason, tvFee, tvStatus;
    private Button btnConfirmPayment;
    private ApiService apiService;
    private MaterialToolbar toolbar;
    private LichHen currentLichHen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        apiService = ApiClient.getApiService();

        toolbar = findViewById(R.id.toolbar_confirmation);
        tvDoctorName = findViewById(R.id.tv_doctor_name);
        tvSpecialtyName = findViewById(R.id.tv_specialty_name);
        tvClinicName = findViewById(R.id.tv_clinic_name);
        tvAppointmentDate = findViewById(R.id.tv_appointment_date);
        tvAppointmentTime = findViewById(R.id.tv_appointment_time);
        tvReason = findViewById(R.id.tv_reason);
        tvFee = findViewById(R.id.tv_fee);
        tvStatus = findViewById(R.id.tv_appointment_status);
        btnConfirmPayment = findViewById(R.id.btn_confirm_payment);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        currentLichHen = (LichHen) getIntent().getSerializableExtra("LICH_HEN_DATA");

        if (currentLichHen != null) {
            displayAppointmentInfo();
        } else {
            Toast.makeText(this, "Không có dữ liệu lịch hẹn", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnConfirmPayment.setOnClickListener(v -> {
            if ("Đã xác nhận".equalsIgnoreCase(currentLichHen.getTrangThai())) {
                goToHomeScreen();
            } else {
                createPaymentUrl();
            }
        });
    }

    private void displayAppointmentInfo() {
        if (currentLichHen.getBacSi() != null) {
            if (currentLichHen.getBacSi().getUser() != null) {
                tvDoctorName.setText("Bác sĩ: " + currentLichHen.getBacSi().getUser().getFirstname() + " " + currentLichHen.getBacSi().getUser().getLastname());
            }
            if (currentLichHen.getBacSi().getKhoa() != null) {
                tvSpecialtyName.setText("Chuyên khoa: " + currentLichHen.getBacSi().getKhoa().getTenKhoa());
            }
            if (currentLichHen.getBacSi().getPhongKham() != null) {
                tvClinicName.setText("Phòng khám: " + currentLichHen.getBacSi().getPhongKham().getTenPhong());
            } else {
                tvClinicName.setText("Phòng khám: Chưa xác định");
            }
        }

        tvAppointmentDate.setText("Ngày: " + currentLichHen.getNgay());
        tvAppointmentTime.setText("Giờ: " + currentLichHen.getTimeStart() + " - " + currentLichHen.getTimeEnd());
        tvReason.setText("Lý do khám: " + currentLichHen.getLyDoKham());
        tvFee.setText(String.format("%,.0f VND", currentLichHen.getChiPhi()));
        tvStatus.setText("Trạng thái: " + currentLichHen.getTrangThai());

        if ("Đã xác nhận".equalsIgnoreCase(currentLichHen.getTrangThai())) {
            updateUiAfterPayment();
        }
    }

    private void createPaymentUrl() {
        if (currentLichHen == null) return;

        int lichHenId = currentLichHen.getLichHenId();
        BigDecimal soTien = BigDecimal.valueOf(currentLichHen.getChiPhi());

        apiService.createPaymentUrl(lichHenId, soTien).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String paymentUrl = response.body();
                    Intent intent = new Intent(ConfirmationActivity.this, PaymentActivity.class);
                    intent.putExtra("PAYMENT_URL", paymentUrl);
                    intent.putExtra("LICH_HEN_ID", lichHenId); // Pass the ID
                    startActivity(intent);
                } else {
                    Toast.makeText(ConfirmationActivity.this, "Không thể tạo URL thanh toán.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(ConfirmationActivity.this, "Lỗi kết nối khi tạo URL thanh toán.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUiAfterPayment() {
        tvStatus.setText("Trạng thái: Đã xác nhận");
        tvStatus.setTextColor(Color.parseColor("#4CAF50"));
        btnConfirmPayment.setText("Về Trang Chủ");
        btnConfirmPayment.setBackgroundColor(Color.parseColor("#2196F3"));
    }

    private void goToHomeScreen(){
        Intent intent = new Intent(ConfirmationActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
