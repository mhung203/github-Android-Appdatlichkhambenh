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
import com.example.app_dat_lich_kham_benh.data.DatabaseConnector;
import com.example.app_dat_lich_kham_benh.util.SessionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        sessionManager = new SessionManager(this);

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
        new Thread(() -> {
            try {
                Connection connection = DatabaseConnector.getConnection();
                String sql = "SELECT firstname, lastname, birthday, gender, phone, address FROM User WHERE userId = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, sessionManager.getUserId());

                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    String firstName = rs.getString("firstname");
                    String lastName = rs.getString("lastname");
                    String birthday = rs.getString("birthday");
                    String gender = rs.getString("gender");
                    String phone = rs.getString("phone");
                    String address = rs.getString("address");

                    runOnUiThread(() -> {
                        tvFirstName.setText(firstName);
                        tvLastName.setText(lastName);
                        tvBirthday.setText(birthday != null ? birthday : "Chưa có");
                        tvGender.setText(gender != null ? gender : "Chưa có");
                        tvPhone.setText(phone != null ? phone : "Chưa có");
                        tvAddress.setText(address != null ? address : "Chưa có");

                        // Set initial values for editing
                        etEditBirthday.setText(birthday);
                        etEditPhone.setText(phone);
                        etEditAddress.setText(address);
                        setSpinnerSelection(spinnerEditGender, gender);
                    });
                }
                rs.close();
                statement.close();
                connection.close();
            } catch (SQLException e) {
                Log.e(TAG, "Error loading profile: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    private void saveField(String fieldName, String value, TextView displayView) {
        new Thread(() -> {
            try {
                Connection connection = DatabaseConnector.getConnection();
                String sql = String.format("UPDATE User SET %s = ? WHERE userId = ?", fieldName);
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, value);
                statement.setInt(2, sessionManager.getUserId());

                int rowsAffected = statement.executeUpdate();
                runOnUiThread(() -> {
                    if (rowsAffected > 0) {
                        showToast("Cập nhật thành công!");
                        displayView.setText(value);
                    } else {
                        showToast("Cập nhật thất bại.");
                    }
                });

                statement.close();
                connection.close();
            } catch (SQLException e) {
                Log.e(TAG, "Error saving field: " + e.getMessage());
                e.printStackTrace();
                showToast("Lỗi cơ sở dữ liệu.");
            }
        }).start();
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
