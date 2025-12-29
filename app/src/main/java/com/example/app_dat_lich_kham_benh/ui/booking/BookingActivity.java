package com.example.app_dat_lich_kham_benh.ui.booking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.adapter.DateAdapter;
import com.example.app_dat_lich_kham_benh.api.ApiClient;
import com.example.app_dat_lich_kham_benh.api.dto.LichHenDTO;
import com.example.app_dat_lich_kham_benh.api.model.BacSi;
import com.example.app_dat_lich_kham_benh.api.model.BenhNhan;
import com.example.app_dat_lich_kham_benh.api.model.CaKham;
import com.example.app_dat_lich_kham_benh.api.model.Khoa;
import com.example.app_dat_lich_kham_benh.api.model.LichHen;
import com.example.app_dat_lich_kham_benh.api.service.ApiService;
import com.example.app_dat_lich_kham_benh.util.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingActivity extends AppCompatActivity implements DateAdapter.OnDateClickListener {

    private static final String TAG = "BookingActivity";

    private AutoCompleteTextView actvSpecialty, actvDoctor, actvTimeSlot;
    private RecyclerView recyclerViewDates;
    private TextInputEditText etReason;
    private Button btnBookAppointment;
    private MaterialToolbar toolbar;

    private ApiService apiService;
    private SessionManager sessionManager;
    private DateAdapter dateAdapter;

    private List<Khoa> khoaList = new ArrayList<>();
    private List<BacSi> doctorList = new ArrayList<>();
    private List<CaKham> timeSlotList = new ArrayList<>();

    private Khoa selectedKhoa;
    private BacSi selectedDoctor;
    private CaKham selectedCa;
    private String selectedDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        apiService = ApiClient.getApiService();
        sessionManager = new SessionManager(this);

        // Ánh xạ View
        toolbar = findViewById(R.id.toolbar_booking);
        actvSpecialty = findViewById(R.id.actv_specialty);
        actvDoctor = findViewById(R.id.actv_doctor);
        recyclerViewDates = findViewById(R.id.recycler_view_dates);
        actvTimeSlot = findViewById(R.id.actv_time_slot);
        etReason = findViewById(R.id.et_reason);
        btnBookAppointment = findViewById(R.id.btn_book_appointment);

        // Cài đặt Toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        setupDatesRecyclerView();
        loadSpecialties();

        // Lắng nghe sự kiện
        actvSpecialty.setOnItemClickListener((parent, view, position, id) -> {
            selectedKhoa = khoaList.get(position);
            resetDoctorAndTimeSlot();
            loadDoctorsBySpecialty(selectedKhoa.getKhoaId());
        });

        actvDoctor.setOnItemClickListener((parent, view, position, id) -> {
            selectedDoctor = doctorList.get(position);
            resetTimeSlot();
            loadAvailableTimeSlots();
        });

        actvTimeSlot.setOnItemClickListener((parent, view, position, id) -> {
            selectedCa = timeSlotList.get(position);
        });

        btnBookAppointment.setOnClickListener(v -> bookAppointment());
    }

    private void setupDatesRecyclerView() {
        dateAdapter = new DateAdapter(this, this);
        recyclerViewDates.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewDates.setAdapter(dateAdapter);
        // Set initial date and load slots
        onDateClick(dateAdapter.getInitialDate());
    }

    @Override
    public void onDateClick(Calendar date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectedDate = sdf.format(date.getTime());
        resetTimeSlot();
        loadAvailableTimeSlots();
    }

    private void loadSpecialties() {
        apiService.getAllKhoa().enqueue(new Callback<List<Khoa>>() {
            @Override
            public void onResponse(Call<List<Khoa>> call, Response<List<Khoa>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    khoaList.clear();
                    khoaList.addAll(response.body());
                    List<String> khoaNames = khoaList.stream().map(Khoa::getTenKhoa).collect(Collectors.toList());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(BookingActivity.this, android.R.layout.simple_dropdown_item_1line, khoaNames);
                    actvSpecialty.setAdapter(adapter);
                } else {
                    showToast("Không thể tải danh sách chuyên khoa.");
                }
            }

            @Override
            public void onFailure(Call<List<Khoa>> call, Throwable t) {
                showToast("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void loadDoctorsBySpecialty(int khoaId) {
        apiService.getBacSiByKhoa(khoaId).enqueue(new Callback<List<BacSi>>() {
            @Override
            public void onResponse(Call<List<BacSi>> call, Response<List<BacSi>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    doctorList.clear();
                    doctorList.addAll(response.body());
                    List<String> doctorNames = doctorList.stream().map(d -> d.getUser().getFirstname() + " " + d.getUser().getLastname()).collect(Collectors.toList());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(BookingActivity.this, android.R.layout.simple_dropdown_item_1line, doctorNames);
                    actvDoctor.setAdapter(adapter);
                    actvDoctor.setEnabled(true);
                } else {
                    showToast("Không thể tải danh sách bác sĩ.");
                    actvDoctor.setEnabled(false);
                }
            }

            @Override
            public void onFailure(Call<List<BacSi>> call, Throwable t) {
                showToast("Lỗi kết nối: " + t.getMessage());
                actvDoctor.setEnabled(false);
            }
        });
    }

    private void loadAvailableTimeSlots() {
        if (selectedDoctor == null || selectedDate == null) return;

        Log.d(TAG, "Đang yêu cầu ca khám cho Bác sĩ ID: " + selectedDoctor.getBacSiId() + " vào ngày: " + selectedDate);

        apiService.getAvailableCa(selectedDoctor.getBacSiId(), selectedDate).enqueue(new Callback<List<CaKham>>() {
            @Override
            public void onResponse(Call<List<CaKham>> call, Response<List<CaKham>> response) {
                timeSlotList.clear();
                if (response.isSuccessful() && response.body() != null) {
                    timeSlotList.addAll(response.body());
                    Log.d(TAG, "API trả về " + response.body().size() + " ca khám.");

                    List<String> timeSlotStrings = timeSlotList.stream()
                            .map(ca -> ca.getTimeStart() + " - " + ca.getTimeEnd())
                            .collect(Collectors.toList());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(BookingActivity.this, android.R.layout.simple_dropdown_item_1line, timeSlotStrings);
                    actvTimeSlot.setAdapter(adapter);
                    actvTimeSlot.setEnabled(!timeSlotList.isEmpty());
                }

                if (timeSlotList.isEmpty()) {
                    actvTimeSlot.setEnabled(false);
                    Log.d(TAG, "Không có ca làm việc cho ngày đã chọn hoặc API trả về danh sách rỗng.");
                }
            }

            @Override
            public void onFailure(Call<List<CaKham>> call, Throwable t) {
                showToast("Lỗi khi tải ca làm việc: " + t.getMessage());
                actvTimeSlot.setEnabled(false);
            }
        });
    }

    private void bookAppointment() {
        if (selectedKhoa == null || selectedDoctor == null || selectedCa == null || selectedDate == null) {
            showToast("Vui lòng chọn đầy đủ thông tin.");
            return;
        }

        String reason = etReason.getText().toString().trim();
        if (reason.isEmpty()) {
            showToast("Vui lòng nhập lý do khám.");
            return;
        }

        if (!sessionManager.isLoggedIn()) {
            showToast("Vui lòng đăng nhập để đặt lịch.");
            return;
        }

        LichHenDTO lichHenDTO = new LichHenDTO();
        lichHenDTO.setUserId(sessionManager.getUserId());
        lichHenDTO.setBacSiId(selectedDoctor.getBacSiId());
        lichHenDTO.setIdCa(selectedCa.getId());
        lichHenDTO.setNgayKham(selectedDate);
        lichHenDTO.setTimeStart(selectedCa.getTimeStart());
        lichHenDTO.setTimeEnd(selectedCa.getTimeEnd());
        lichHenDTO.setLyDoKham(reason);
        lichHenDTO.setIdPhong(1); // Default value
        lichHenDTO.setChiPhi(150000.0); // Default value

        Log.d(TAG, "Gửi yêu cầu đặt lịch: " + new Gson().toJson(lichHenDTO));

        apiService.createLichHen(lichHenDTO).enqueue(new Callback<LichHen>() {
            @Override
            public void onResponse(Call<LichHen> call, Response<LichHen> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showToast("Đặt lịch thành công! Vui lòng xem lại thông tin.");

                    Intent intent = new Intent(BookingActivity.this, ConfirmationActivity.class);
                    intent.putExtra("LICH_HEN_DATA", response.body());
                    startActivity(intent);

                    finish(); // Đóng màn hình đặt lịch
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body", e);
                    }
                    showToast("Đặt lịch thất bại. Khung giờ này có thể đã được đặt.");
                    Log.e(TAG, "Booking failed. Code: " + response.code() + " Body: " + errorBody);
                }
            }

            @Override
            public void onFailure(Call<LichHen> call, Throwable t) {
                showToast("Lỗi kết nối khi đặt lịch.");
            }
        });
    }

    private void resetDoctorAndTimeSlot() {
        actvDoctor.setText("", false);
        selectedDoctor = null;
        actvDoctor.setEnabled(false);
        resetTimeSlot();
    }

    private void resetTimeSlot() {
        actvTimeSlot.setText("", false);
        selectedCa = null;
        actvTimeSlot.setEnabled(false);
        timeSlotList.clear();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
