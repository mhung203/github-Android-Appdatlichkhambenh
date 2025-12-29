package com.example.app_dat_lich_kham_benh.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.adapter.DoctorHomeAdapter;
import com.example.app_dat_lich_kham_benh.adapter.KhoaAdapter;
import com.example.app_dat_lich_kham_benh.api.ApiClient;
import com.example.app_dat_lich_kham_benh.api.model.BacSi;
import com.example.app_dat_lich_kham_benh.api.model.Khoa;
import com.example.app_dat_lich_kham_benh.api.service.ApiService;
import com.example.app_dat_lich_kham_benh.auth.LoginActivity;
import com.example.app_dat_lich_kham_benh.util.SessionManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private RecyclerView bannerRecyclerView, recyclerViewSpecialties, recyclerViewDoctors;
    private BannerAdapter bannerAdapter;
    private KhoaAdapter khoaAdapter;
    private DoctorHomeAdapter doctorHomeAdapter;

    private List<Integer> bannerImages;
    private List<Khoa> khoaList = new ArrayList<>();
    private List<BacSi> doctorList = new ArrayList<>();

    private TextView greetingTextView, userNameTextView;
    private LinearLayout userInfoLayout;
    private SessionManager sessionManager;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sessionManager = new SessionManager(requireContext());
        apiService = ApiClient.getApiService();

        // Ánh xạ
        bannerRecyclerView = view.findViewById(R.id.banner_recyclerview);
        recyclerViewSpecialties = view.findViewById(R.id.recyclerViewSpecialties);
        recyclerViewDoctors = view.findViewById(R.id.recyclerViewDoctors);
        greetingTextView = view.findViewById(R.id.greeting_textview);
        userNameTextView = view.findViewById(R.id.user_name_textview);
        userInfoLayout = view.findViewById(R.id.user_info_layout);

        // Cập nhật giao diện người dùng
        updateGreeting();
        updateUserSession();

        // Thiết lập RecyclerViews
        setupBanner();
        setupSpecialtiesRecyclerView();
        setupDoctorsRecyclerView();

        // Tải dữ liệu
        loadSpecialties();
        loadDoctors();

        // Gán sự kiện
        userInfoLayout.setOnClickListener(v -> {
            if (!sessionManager.isLoggedIn()) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUserSession();
    }

    private void setupBanner() {
        bannerImages = new ArrayList<>();
        bannerImages.add(R.drawable.banner1);
        bannerAdapter = new BannerAdapter(bannerImages);
        bannerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        bannerRecyclerView.setAdapter(bannerAdapter);
    }

    private void setupSpecialtiesRecyclerView() {
        khoaAdapter = new KhoaAdapter(khoaList, getContext());
        recyclerViewSpecialties.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewSpecialties.setAdapter(khoaAdapter);
    }

    private void setupDoctorsRecyclerView() {
        doctorHomeAdapter = new DoctorHomeAdapter(doctorList, getContext());
        recyclerViewDoctors.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewDoctors.setAdapter(doctorHomeAdapter);
    }

    private void loadSpecialties() {
        apiService.getAllKhoa().enqueue(new Callback<List<Khoa>>() {
            @Override
            public void onResponse(Call<List<Khoa>> call, Response<List<Khoa>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    khoaList.clear();
                    khoaList.addAll(response.body());
                    khoaAdapter.notifyDataSetChanged();
                } else {
                    showToast("Không thể tải danh sách chuyên khoa.");
                }
            }

            @Override
            public void onFailure(Call<List<Khoa>> call, Throwable t) {
                Log.e(TAG, "Lỗi tải chuyên khoa: " + t.getMessage());
                showToast("Lỗi kết nối.");
            }
        });
    }

    private void loadDoctors() {
        apiService.getAllBacSi().enqueue(new Callback<List<BacSi>>() {
            @Override
            public void onResponse(Call<List<BacSi>> call, Response<List<BacSi>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    doctorList.clear();
                    doctorList.addAll(response.body());
                    doctorHomeAdapter.notifyDataSetChanged();
                } else {
                    showToast("Không thể tải danh sách bác sĩ.");
                }
            }

            @Override
            public void onFailure(Call<List<BacSi>> call, Throwable t) {
                Log.e(TAG, "Lỗi tải bác sĩ: " + t.getMessage());
                showToast("Lỗi kết nối.");
            }
        });
    }

    private void updateUserSession() {
        if (sessionManager.isLoggedIn()) {
            userNameTextView.setText(sessionManager.getUserName());
        } else {
            userNameTextView.setText("Đăng ký / Đăng nhập");
        }
    }

    private void updateGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        if (hourOfDay >= 5 && hourOfDay < 12) {
            greetingTextView.setText("Buổi sáng an lành!");
        } else if (hourOfDay >= 12 && hourOfDay < 18) {
            greetingTextView.setText("Buổi chiều an lành!");
        } else {
            greetingTextView.setText("Buổi tối an lành!");
        }
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
