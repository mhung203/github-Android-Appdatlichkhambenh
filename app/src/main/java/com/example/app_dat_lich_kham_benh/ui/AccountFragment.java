package com.example.app_dat_lich_kham_benh.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.auth.LoginActivity;
import com.example.app_dat_lich_kham_benh.util.SessionManager;

public class AccountFragment extends Fragment {

    private SessionManager sessionManager;
    private LinearLayout loggedOutView, loggedInView;
    private Button goToLoginButton;
    private TextView logoutButton;
    private TextView userNameTextView, userEmailTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        sessionManager = new SessionManager(requireContext());
        loggedOutView = view.findViewById(R.id.logged_out_view);
        loggedInView = view.findViewById(R.id.logged_in_view);
        goToLoginButton = view.findViewById(R.id.go_to_login_button);
        logoutButton = view.findViewById(R.id.logout_button);
        userNameTextView = view.findViewById(R.id.account_user_name);
        userEmailTextView = view.findViewById(R.id.account_user_email);

        // Gán sự kiện
        goToLoginButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            // Xóa phiên đăng nhập
            sessionManager.logoutUser();

            // Yêu cầu MainActivity chuyển về tab Trang chủ
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).switchToHomeTab();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Cập nhật giao diện mỗi khi Fragment được hiển thị
        updateView();
    }

    private void updateView() {
        if (sessionManager.isLoggedIn()) {
            loggedInView.setVisibility(View.VISIBLE);
            loggedOutView.setVisibility(View.GONE);

            // Lấy và hiển thị thông tin người dùng
            userNameTextView.setText(sessionManager.getUserName());
            userEmailTextView.setText(sessionManager.getUserEmail());

        } else {
            loggedInView.setVisibility(View.GONE);
            loggedOutView.setVisibility(View.VISIBLE);
        }
    }
}
