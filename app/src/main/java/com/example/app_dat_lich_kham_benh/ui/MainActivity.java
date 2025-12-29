package com.example.app_dat_lich_kham_benh.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.ui.booking.BookingActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            boolean shouldSwitchFragment = true;

            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_doctors) {
                startActivity(new Intent(MainActivity.this, BookingActivity.class));
                shouldSwitchFragment = false;
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new AccountFragment();
            }

            if (shouldSwitchFragment && selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        selectedFragment).commit();
                return true;
            }
            return !shouldSwitchFragment;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigationView.getSelectedItemId() == R.id.nav_doctors) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    public void switchToHomeTab() {
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }
}
