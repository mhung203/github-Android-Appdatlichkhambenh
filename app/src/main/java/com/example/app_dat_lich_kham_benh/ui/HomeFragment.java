package com.example.app_dat_lich_kham_benh.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.auth.LoginActivity;
import com.example.app_dat_lich_kham_benh.util.SessionManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView bannerRecyclerView;
    private BannerAdapter bannerAdapter;
    private List<Integer> bannerImages;
    private TextView greetingTextView, userNameTextView;
    private LinearLayout userInfoLayout;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sessionManager = new SessionManager(requireContext());

        // Ánh xạ
        bannerRecyclerView = view.findViewById(R.id.banner_recyclerview);
        greetingTextView = view.findViewById(R.id.greeting_textview);
        userNameTextView = view.findViewById(R.id.user_name_textview);
        userInfoLayout = view.findViewById(R.id.user_info_layout);

        // Cập nhật giao diện người dùng
        updateGreeting();
        updateUserSession();

        // Thiết lập banner
        setupBanner();

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
        // Cập nhật lại giao diện mỗi khi quay lại màn hình này
        updateUserSession();
    }

    private void setupBanner() {
        bannerImages = new ArrayList<>();
        bannerImages.add(R.drawable.banner1);
        bannerAdapter = new BannerAdapter(bannerImages);
        bannerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        bannerRecyclerView.setAdapter(bannerAdapter);
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
}
