package com.example.app_dat_lich_kham_benh.ui.admin;

import android.app.Dialog;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.adapter.AdminKhoaAdapter;
import com.example.app_dat_lich_kham_benh.api.ApiClient;
import com.example.app_dat_lich_kham_benh.api.model.Khoa;
import com.example.app_dat_lich_kham_benh.api.service.ApiService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageDepartmentActivity extends AppCompatActivity {

    private RecyclerView rcvKhoa;
    private FloatingActionButton fabAdd;
    private ApiService apiService;
    private AdminKhoaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_department);

        apiService = ApiClient.getApiService();

        rcvKhoa = findViewById(R.id.rcvKhoa);
        fabAdd = findViewById(R.id.fabAdd);

        rcvKhoa.setLayoutManager(new LinearLayoutManager(this));
        loadListKhoa();
        fabAdd.setOnClickListener(v -> showAddDialog());
    }

    private void loadListKhoa() {
        apiService.getAllKhoa().enqueue(new Callback<List<Khoa>>() {
            @Override
            public void onResponse(Call<List<Khoa>> call, Response<List<Khoa>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new AdminKhoaAdapter(ManageDepartmentActivity.this, response.body(), id -> {
                        confirmDelete(id);
                    });
                    rcvKhoa.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<List<Khoa>> call, Throwable t) {
                Toast.makeText(ManageDepartmentActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDelete(int id) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa chuyên khoa này không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    deleteKhoa(id);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteKhoa(int id) {
        apiService.deleteKhoa(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ManageDepartmentActivity.this, "Đã xóa thành công!", Toast.LENGTH_SHORT).show();
                    loadListKhoa(); // Load lại danh sách
                } else {
                    Toast.makeText(ManageDepartmentActivity.this, "Không thể xóa (Có thể do ràng buộc dữ liệu)", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ManageDepartmentActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_khoa);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        EditText edtTen = dialog.findViewById(R.id.edtTenKhoa);
        EditText edtMoTa = dialog.findViewById(R.id.edtMoTa);
        Button btnSave = dialog.findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> {
            String ten = edtTen.getText().toString().trim();
            String mota = edtMoTa.getText().toString().trim();

            if (ten.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên khoa", Toast.LENGTH_SHORT).show();
                return;
            }

            createKhoa(ten, mota, dialog);
        });

        dialog.show();
    }

    private void createKhoa(String ten, String moTa, Dialog dialog) {
        Khoa newKhoa = new Khoa();
        newKhoa.setTenKhoa(ten);
        newKhoa.setMoTa(moTa);

        apiService.createKhoa(newKhoa).enqueue(new Callback<Khoa>() {
            @Override
            public void onResponse(Call<Khoa> call, Response<Khoa> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ManageDepartmentActivity.this, "Thêm mới thành công!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    loadListKhoa();
                } else {
                    Toast.makeText(ManageDepartmentActivity.this, "Lỗi khi thêm khoa", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Khoa> call, Throwable t) {
                Toast.makeText(ManageDepartmentActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}