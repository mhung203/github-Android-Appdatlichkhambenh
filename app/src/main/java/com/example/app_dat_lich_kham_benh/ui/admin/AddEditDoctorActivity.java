package com.example.app_dat_lich_kham_benh.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.api.ApiClient;
import com.example.app_dat_lich_kham_benh.api.model.BacSi;
import com.example.app_dat_lich_kham_benh.api.model.Khoa;
import com.example.app_dat_lich_kham_benh.api.model.User;
import com.example.app_dat_lich_kham_benh.api.service.ApiService;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEditDoctorActivity extends AppCompatActivity {

    private static final String TAG = "AddEditDoctorActivity";

    private TextInputEditText etFirstName, etLastName, etEmail, etPassword;
    private AutoCompleteTextView actvSpecialty;
    private Button btnSave;

    private boolean isEditMode = false;
    private int doctorIdToEdit = -1;
    private ApiService apiService;
    private List<Khoa> khoaList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_doctor);

        apiService = ApiClient.getApiService();

        etFirstName = findViewById(R.id.et_doctor_firstname);
        etLastName = findViewById(R.id.et_doctor_lastname);
        etEmail = findViewById(R.id.et_doctor_email);
        etPassword = findViewById(R.id.et_doctor_password);
        actvSpecialty = findViewById(R.id.actv_doctor_specialty);
        btnSave = findViewById(R.id.btn_save_doctor);
        loadKhoaData();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("DOCTOR_ID")) {
            isEditMode = true;
            doctorIdToEdit = intent.getIntExtra("DOCTOR_ID", -1);
            setTitle("Chỉnh sửa Bác sĩ");
            btnSave.setText("Cập nhật thông tin");
            etPassword.setHint("Để trống nếu không đổi mật khẩu");

            loadDoctorDetails(doctorIdToEdit);
        } else {
            setTitle("Thêm Bác sĩ mới");
        }

        btnSave.setOnClickListener(v -> saveDoctor());
    }

    private void loadKhoaData() {
        apiService.getAllKhoa().enqueue(new Callback<List<Khoa>>() {
            @Override
            public void onResponse(Call<List<Khoa>> call, Response<List<Khoa>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    khoaList.clear();
                    khoaList.addAll(response.body());

                    List<String> specialtyNames = new ArrayList<>();
                    for (Khoa khoa : khoaList) {
                        specialtyNames.add(khoa.getTenKhoa());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            AddEditDoctorActivity.this,
                            android.R.layout.simple_dropdown_item_1line,
                            specialtyNames
                    );
                    actvSpecialty.setAdapter(adapter);

                } else {
                    Log.e(TAG, "Tải danh sách chuyên khoa thất bại. Mã lỗi: " + response.code());
                    showToast("Không thể tải danh sách chuyên khoa.");
                }
            }

            @Override
            public void onFailure(Call<List<Khoa>> call, Throwable t) {
                Log.e(TAG, "Failed to load specialties: " + t.getMessage());
                showToast("Lỗi kết nối khi tải chuyên khoa.");
            }
        });
    }

    private void loadDoctorDetails(int doctorId) {
        apiService.getBacSiById(doctorId).enqueue(new Callback<BacSi>() {
            @Override
            public void onResponse(Call<BacSi> call, Response<BacSi> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BacSi doctor = response.body();
                    if (doctor.getUser() != null) {
                        etFirstName.setText(doctor.getUser().getFirstname());
                        etLastName.setText(doctor.getUser().getLastname());
                        etEmail.setText(doctor.getUser().getEmail());
                    }
                    if (doctor.getKhoa() != null) {
                        actvSpecialty.setText(doctor.getKhoa().getTenKhoa(), false);
                    }
                } else {
                    showToast("Không thể tải thông tin bác sĩ.");
                }
            }

            @Override
            public void onFailure(Call<BacSi> call, Throwable t) {
                showToast("Lỗi kết nối.");
            }
        });
    }

    private void saveDoctor() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String specialtyName = actvSpecialty.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || specialtyName.isEmpty()) {
            showToast("Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        final Khoa selectedKhoa = khoaList.stream()
                .filter(k -> k.getTenKhoa().equals(specialtyName))
                .findFirst()
                .orElse(null);

        if (selectedKhoa == null) {
            showToast("Chuyên khoa không hợp lệ. Vui lòng chọn từ danh sách.");
            return;
        }

        if (isEditMode) {
            showToast("Chức năng cập nhật chưa được hỗ trợ.");
        } else {
            if (password.isEmpty()) {
                showToast("Vui lòng nhập mật khẩu cho tài khoản mới.");
                return;
            }

            // Step 1: Create the User object
            User newUserAccount = new User();
            newUserAccount.setFirstname(firstName);
            newUserAccount.setLastname(lastName);
            newUserAccount.setEmail(email);
            newUserAccount.setPassword(password);
            newUserAccount.setRole("doctor");

            // Step 2: Call API to create the user first
            apiService.createUser(newUserAccount).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // Step 3: If user creation is successful, create the doctor profile
                        User createdUser = response.body();
                        createDoctorProfile(createdUser, selectedKhoa);
                    } else {
                        showToast("Tạo tài khoản thất bại. Email có thể đã tồn tại.");
                        Log.e(TAG, "User creation failed. Code: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.e(TAG, "User creation API failure: " + t.getMessage());
                    showToast("Lỗi kết nối khi tạo tài khoản.");
                }
            });
        }
    }

    private void createDoctorProfile(User createdUser, Khoa selectedKhoa) {
        // Link user and khoa by their IDs
        User userToLink = new User();
        userToLink.setUserId(createdUser.getUserId());

        Khoa khoaToLink = new Khoa();
        khoaToLink.setKhoaId(selectedKhoa.getKhoaId());

        BacSi newDoctor = new BacSi();
        newDoctor.setUser(userToLink);
        newDoctor.setKhoa(khoaToLink);

        // Step 4: Call API to create the doctor profile
        apiService.createBacSi(newDoctor).enqueue(new Callback<BacSi>() {
            @Override
            public void onResponse(Call<BacSi> call, Response<BacSi> response) {
                if (response.isSuccessful()) {
                    showToast("Thêm bác sĩ thành công!");
                    finish(); // Go back to the doctor list
                } else {
                    showToast("Tạo hồ sơ bác sĩ thất bại.");
                    Log.e(TAG, "Doctor profile creation failed. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BacSi> call, Throwable t) {
                Log.e(TAG, "Doctor profile creation API failure: " + t.getMessage());
                showToast("Lỗi kết nối khi tạo hồ sơ bác sĩ.");
            }
        });
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(AddEditDoctorActivity.this, message, Toast.LENGTH_SHORT).show());
    }
}
