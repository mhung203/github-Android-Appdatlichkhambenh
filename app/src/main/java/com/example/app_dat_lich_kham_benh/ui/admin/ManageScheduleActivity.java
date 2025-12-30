package com.example.app_dat_lich_kham_benh.ui.admin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.adapter.AdminScheduleAdapter;
import com.example.app_dat_lich_kham_benh.api.ApiClient;
import com.example.app_dat_lich_kham_benh.api.model.LichHen;
import com.example.app_dat_lich_kham_benh.api.service.ApiService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageScheduleActivity extends AppCompatActivity {

    private TextView tvSelectedDate;
    private RecyclerView rcvSchedule;
    private ImageButton btnPickDate;

    private ApiService apiService;
    private AdminScheduleAdapter adapter;
    private List<LichHen> listLichHen;

    private LocalDate currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_schedule);

        apiService = ApiClient.getApiService();
        listLichHen = new ArrayList<>();
        currentDate = LocalDate.now();
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        rcvSchedule = findViewById(R.id.rcvAdminSchedule);
        btnPickDate = findViewById(R.id.btnPickDate);
        rcvSchedule.setLayoutManager(new LinearLayoutManager(this));
        updateDateDisplay();
        loadScheduleData();

        btnPickDate.setOnClickListener(v -> showDatePicker());
        tvSelectedDate.setOnClickListener(v -> showDatePicker());
    }

    private void updateDateDisplay() {
        tvSelectedDate.setText(currentDate.toString());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    currentDate = LocalDate.of(year, month + 1, dayOfMonth);
                    updateDateDisplay();

                    loadScheduleData();
                },
                currentDate.getYear(),
                currentDate.getMonthValue() - 1,
                currentDate.getDayOfMonth());
        datePickerDialog.show();
    }

    private void loadScheduleData() {
        String dateStr = currentDate.toString();

        apiService.getLichHenAdmin(dateStr).enqueue(new Callback<List<LichHen>>() {
            @Override
            public void onResponse(Call<List<LichHen>> call, Response<List<LichHen>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listLichHen = response.body();

                    adapter = new AdminScheduleAdapter(ManageScheduleActivity.this, listLichHen, new AdminScheduleAdapter.OnActionCallback() {
                        @Override
                        public void onCancelClick(int id) {

                            confirmCancel(id);
                        }
                    });
                    rcvSchedule.setAdapter(adapter);

                    if (listLichHen.isEmpty()) {
                        Toast.makeText(ManageScheduleActivity.this, "Ngày này chưa có lịch nào", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<LichHen>> call, Throwable t) {
                Toast.makeText(ManageScheduleActivity.this, "Lỗi tải lịch: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmCancel(int id) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận hủy")
                .setMessage("Bạn có chắc chắn muốn hủy lịch hẹn này? Khách hàng sẽ nhận được thông báo.")
                .setPositiveButton("Hủy lịch", (dialog, which) -> {
                    callApiCancel(id);
                })
                .setNegativeButton("Thôi", null)
                .show();
    }

    private void callApiCancel(int id) {
        apiService.huyLichHen(id).enqueue(new Callback<LichHen>() {
            @Override
            public void onResponse(Call<LichHen> call, Response<LichHen> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ManageScheduleActivity.this, "Đã hủy lịch thành công!", Toast.LENGTH_SHORT).show();
                    loadScheduleData();
                } else {
                    Toast.makeText(ManageScheduleActivity.this, "Lỗi khi hủy lịch", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<LichHen> call, Throwable t) {
                Toast.makeText(ManageScheduleActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}