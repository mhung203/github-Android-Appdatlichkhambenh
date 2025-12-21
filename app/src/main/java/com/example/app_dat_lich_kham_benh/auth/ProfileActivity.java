package com.example.app_dat_lich_kham_benh.auth;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.api.ApiClient;
import com.example.app_dat_lich_kham_benh.api.model.User;
import com.example.app_dat_lich_kham_benh.api.service.ApiService;
import com.example.app_dat_lich_kham_benh.util.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    // Views
    private TextView tvFirstName, tvLastName, tvBirthday, tvGender, tvPhone, tvAddress;
    private EditText etEditBirthday, etEditPhone, etEditAddress;
    private Spinner spinnerEditGender;
    private ImageButton btnEditBirthday, btnSaveBirthday, btnEditGender, btnSaveGender, btnEditPhone, btnSavePhone, btnEditAddress, btnSaveAddress;

    private SessionManager sessionManager;
    private final Calendar myCalendar = Calendar.getInstance();
    private ImageButton backButton;
    private ApiService apiService;
    private User currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        sessionManager = new SessionManager(this);
        apiService = ApiClient.getApiService();

        // Ánh xạ views
        tvFirstName = findViewById(R.id.tvFirstName);
        tvLastName = findViewById(R.id.tvLastName);
        tvBirthday = findViewById(R.id.tvBirthday);
        tvGender = findViewById(R.id.tvGender);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        etEditBirthday = findViewById(R.id.etEditBirthday);
        etEditPhone = findViewById(R.id.etEditPhone);
        etEditAddress = findViewById(R.id.etEditAddress);

        spinnerEditGender = findViewById(R.id.spinnerEditGender);

        btnEditBirthday = findViewById(R.id.btnEditBirthday);
        btnSaveBirthday = findViewById(R.id.btnSaveBirthday);
        btnEditGender = findViewById(R.id.btnEditGender);
        btnSaveGender = findViewById(R.id.btnSaveGender);
        btnEditPhone = findViewById(R.id.btnEditPhone);
        btnSavePhone = findViewById(R.id.btnSavePhone);
        btnEditAddress = findViewById(R.id.btnEditAddress);
        btnSaveAddress = findViewById(R.id.btnSaveAddress);
        backButton = findViewById(R.id.backButton);

        setupClickListeners();
        setupGenderSpinner();
        loadUserProfile();
    }

    private void setupClickListeners() {
        // Back Button
        backButton.setOnClickListener(v -> finish());

        // Birthday
        btnEditBirthday.setOnClickListener(v -> toggleEditMode(tvBirthday, etEditBirthday, btnEditBirthday, btnSaveBirthday, true));
        btnSaveBirthday.setOnClickListener(v -> {
            saveField("birthday", etEditBirthday.getText().toString(), tvBirthday);
            toggleEditMode(tvBirthday, etEditBirthday, btnEditBirthday, btnSaveBirthday, false);
        });

        // Gender
        btnEditGender.setOnClickListener(v -> toggleEditMode(tvGender, spinnerEditGender, btnEditGender, btnSaveGender, true));
        btnSaveGender.setOnClickListener(v -> {
            saveField("gender", spinnerEditGender.getSelectedItem().toString(), tvGender);
            toggleEditMode(tvGender, spinnerEditGender, btnEditGender, btnSaveGender, false);
        });

        // Phone
        btnEditPhone.setOnClickListener(v -> toggleEditMode(tvPhone, etEditPhone, btnEditPhone, btnSavePhone, true));
        btnSavePhone.setOnClickListener(v -> {
            saveField("phone", etEditPhone.getText().toString(), tvPhone);
            toggleEditMode(tvPhone, etEditPhone, btnEditPhone, btnSavePhone, false);
        });

        // Address
        btnEditAddress.setOnClickListener(v -> toggleEditMode(tvAddress, etEditAddress, btnEditAddress, btnSaveAddress, true));
        btnSaveAddress.setOnClickListener(v -> {
            saveField("address", etEditAddress.getText().toString(), tvAddress);
            toggleEditMode(tvAddress, etEditAddress, btnEditAddress, btnSaveAddress, false);
        });
        
        // Date Picker Dialog
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };
        etEditBirthday.setOnClickListener(v -> new DatePickerDialog(ProfileActivity.this, date,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());
    }

    private void setupGenderSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEditGender.setAdapter(adapter);
    }

    private void loadUserProfile() {
        apiService.getUserById(sessionManager.getUserId()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentUser = response.body();
                    runOnUiThread(() -> {
                        tvFirstName.setText(currentUser.getFirstname());
                        tvLastName.setText(currentUser.getLastname());
                        tvBirthday.setText(currentUser.getBirthday() != null ? currentUser.getBirthday() : "Chưa có");
                        tvGender.setText(currentUser.getGender() != null ? currentUser.getGender() : "Chưa có");
                        tvPhone.setText(currentUser.getPhone() != null ? currentUser.getPhone() : "Chưa có");
                        tvAddress.setText(currentUser.getAddress() != null ? currentUser.getAddress() : "Chưa có");

                        // Set initial values for editing
                        etEditBirthday.setText(currentUser.getBirthday());
                        etEditPhone.setText(currentUser.getPhone());
                        etEditAddress.setText(currentUser.getAddress());
                        setSpinnerSelection(spinnerEditGender, currentUser.getGender());
                    });
                } else {
                    showToast("Không thể tải thông tin người dùng.");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "Error loading profile: " + t.getMessage());
                showToast("Lỗi kết nối.");
            }
        });
    }

    private void saveField(String fieldName, String value, TextView displayView) {
        if (currentUser == null) return;

        switch (fieldName) {
            case "birthday":
                currentUser.setBirthday(value);
                break;
            case "gender":
                currentUser.setGender(value);
                break;
            case "phone":
                currentUser.setPhone(value);
                break;
            case "address":
                currentUser.setAddress(value);
                break;
        }

        apiService.updateUser(currentUser.getUserId(), currentUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    showToast("Cập nhật thành công!");
                    displayView.setText(value);
                } else {
                    showToast("Cập nhật thất bại.");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "Error saving field: " + t.getMessage());
                showToast("Lỗi kết nối.");
            }
        });
    }

    private void toggleEditMode(View displayView, View editView, View editButton, View saveButton, boolean isEditing) {
        displayView.setVisibility(isEditing ? View.GONE : View.VISIBLE);
        editView.setVisibility(isEditing ? View.VISIBLE : View.GONE);
        editButton.setVisibility(isEditing ? View.GONE : View.VISIBLE);
        saveButton.setVisibility(isEditing ? View.VISIBLE : View.GONE);
    }

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        etEditBirthday.setText(sdf.format(myCalendar.getTime()));
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }
    
    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }
}
