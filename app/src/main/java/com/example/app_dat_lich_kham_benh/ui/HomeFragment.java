package com.example.app_dat_lich_kham_benh.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Handler;
import android.os.Looper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.SnapHelper;

import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.adapter.DoctorHomeAdapter;
import com.example.app_dat_lich_kham_benh.adapter.KhoaAdapter;
import com.example.app_dat_lich_kham_benh.adapter.NewsAdapter;
import com.example.app_dat_lich_kham_benh.adapter.NotificationAdapter;
import com.example.app_dat_lich_kham_benh.api.ApiClient;
import com.example.app_dat_lich_kham_benh.api.model.BacSi;
import com.example.app_dat_lich_kham_benh.api.model.Khoa;
import com.example.app_dat_lich_kham_benh.api.model.ThongBao;
import com.example.app_dat_lich_kham_benh.api.service.ApiService;
import com.example.app_dat_lich_kham_benh.auth.LoginActivity;
import com.example.app_dat_lich_kham_benh.util.SessionManager;
import com.example.app_dat_lich_kham_benh.api.model.TinTuc;
import com.example.app_dat_lich_kham_benh.adapter.NewsAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private ImageView notificationIcon;
    private CardView cardNotificationMini;
    private RecyclerView rcvNotificationMini;
    private TextView tvClosePopup, tvViewAllNoti;
    private View mainContentLayout;
    private RecyclerView bannerRecyclerView, recyclerViewSpecialties, recyclerViewDoctors,rcvNews;
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
    private NewsAdapter newsAdapter;
    private Handler bannerHandler = new Handler(Looper.getMainLooper());
    private Runnable bannerRunnable = new Runnable() {
        @Override
        public void run() {
            if (bannerRecyclerView == null || bannerAdapter == null) return;
            if (bannerAdapter.getItemCount() == 0) return;

            LinearLayoutManager layoutManager = (LinearLayoutManager) bannerRecyclerView.getLayoutManager();
            if (layoutManager == null) return;

            int currentPosition = layoutManager.findFirstVisibleItemPosition();

            int nextPosition = currentPosition + 1;

            if (nextPosition >= bannerAdapter.getItemCount()) {
                nextPosition = 0;
            }

            bannerRecyclerView.smoothScrollToPosition(nextPosition);

            bannerHandler.postDelayed(this, 3000);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sessionManager = new SessionManager(requireContext());
        apiService = ApiClient.getApiService();
        bannerRecyclerView = view.findViewById(R.id.banner_recyclerview);
        recyclerViewSpecialties = view.findViewById(R.id.recyclerViewSpecialties);
        recyclerViewDoctors = view.findViewById(R.id.recyclerViewDoctors);
        greetingTextView = view.findViewById(R.id.greeting_textview);
        userNameTextView = view.findViewById(R.id.user_name_textview);
        userInfoLayout = view.findViewById(R.id.user_info_layout);
        rcvNews = view.findViewById(R.id.rcvNews);
        notificationIcon = view.findViewById(R.id.notification_icon);
        cardNotificationMini = view.findViewById(R.id.cardNotificationMini);
        rcvNotificationMini = view.findViewById(R.id.rcvNotificationMini);
        tvClosePopup = view.findViewById(R.id.tvClosePopup);
        tvViewAllNoti = view.findViewById(R.id.tvViewAllNoti);
        mainContentLayout = view.findViewById(R.id.nestedScrollView);
        rcvNotificationMini.setLayoutManager(new LinearLayoutManager(getContext()));
        updateGreeting();
        updateUserSession();
        setupBanner();
        setupSpecialtiesRecyclerView();
        setupDoctorsRecyclerView();
        setupNewsRecyclerView();
        loadSpecialties();
        loadDoctors();
        loadNews();
        userInfoLayout.setOnClickListener(v -> {
            if (!sessionManager.isLoggedIn()) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
        notificationIcon.setOnClickListener(v -> {
            if (sessionManager.isLoggedIn()) {
                togglePopup();
            } else {
                Toast.makeText(getContext(), "Vui lòng đăng nhập để xem thông báo", Toast.LENGTH_SHORT).show();
            }
        });
        tvClosePopup.setOnClickListener(v -> cardNotificationMini.setVisibility(View.GONE));
        tvViewAllNoti.setOnClickListener(v -> {
            cardNotificationMini.setVisibility(View.GONE);
            Intent intent = new Intent(getContext(), NotificationActivity.class);
            startActivity(intent);
        });
        mainContentLayout.setOnTouchListener((v, event) -> {
            if (cardNotificationMini.getVisibility() == View.VISIBLE) {
                cardNotificationMini.setVisibility(View.GONE);
                return true;
            }
            return false;
        });


        cardNotificationMini.setOnClickListener(v -> {  });
        return view;
    }
    private void togglePopup() {
        if (cardNotificationMini.getVisibility() == View.VISIBLE) {
            cardNotificationMini.setVisibility(View.GONE);
        } else {
            cardNotificationMini.setVisibility(View.VISIBLE);
            loadMiniNotifications();
        }
    }
    private void loadMiniNotifications() {
        int userId = sessionManager.getUserId();
        apiService.getNotifications(userId).enqueue(new Callback<List<ThongBao>>() {
            @Override
            public void onResponse(Call<List<ThongBao>> call, Response<List<ThongBao>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ThongBao> allList = response.body();
                    List<ThongBao> miniList = new ArrayList<>();
                    for (int i = 0; i < allList.size(); i++) {
                        if (i >= 5) break;
                        miniList.add(allList.get(i));
                    }
                    NotificationAdapter adapter = new NotificationAdapter(getContext(), miniList);
                    rcvNotificationMini.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<ThongBao>> call, Throwable t) {
                Log.e("HomeFragment", "Lỗi tải thông báo: " + t.getMessage());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUserSession();
        checkNewNotifications();
    }
    private void checkNewNotifications() {
        if (!sessionManager.isLoggedIn()) return;

        int userId = sessionManager.getUserId();

        apiService.getNotifications(userId).enqueue(new Callback<List<ThongBao>>() {
            @Override
            public void onResponse(Call<List<ThongBao>> call, Response<List<ThongBao>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ThongBao> list = response.body();
                    boolean coTinMoi = false;
                    for (ThongBao tb : list) {
                        if (!tb.isDaXem()) {
                            coTinMoi = true;
                            break;
                        }
                    }
                    if (coTinMoi) {
                        notificationIcon.setImageResource(R.drawable.ic_notifications_active);
                    } else {
                        notificationIcon.setImageResource(R.drawable.ic_notifications);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ThongBao>> call, Throwable t) {
                Log.e("HomeFragment", "Lỗi check thông báo: " + t.getMessage());
            }
        });
    }


    private void setupBanner() {
        bannerImages = new ArrayList<>();
        bannerImages.add(R.drawable.banner1);
        bannerImages.add(R.drawable.banner2);
        bannerImages.add(R.drawable.banner3);

        bannerAdapter = new BannerAdapter(bannerImages);
        bannerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        bannerRecyclerView.setAdapter(bannerAdapter);
        if (bannerRecyclerView.getOnFlingListener() == null) {
            SnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(bannerRecyclerView);
        }
        bannerHandler.postDelayed(bannerRunnable, 3000);
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
    private void setupNewsRecyclerView() {
        rcvNews.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }
    private void loadNews() {
        apiService.getLatestNews().enqueue(new Callback<List<TinTuc>>() {
            @Override
            public void onResponse(Call<List<TinTuc>> call, Response<List<TinTuc>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TinTuc> newsList = response.body();

                    newsAdapter = new NewsAdapter(getContext(), newsList);
                    rcvNews.setAdapter(newsAdapter);
                } else {
                    Log.e(TAG, "Không tải được tin tức");
                }
            }

            @Override
            public void onFailure(Call<List<TinTuc>> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối tin tức: " + t.getMessage());
            }
        });
    }
    @Override
    public void onPause() {
        super.onPause();
        bannerHandler.removeCallbacks(bannerRunnable);
    }
}
