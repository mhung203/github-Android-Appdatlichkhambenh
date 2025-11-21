package com.example.app_dat_lich_kham_benh.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_FIRST_NAME = "firstName";
    private static final String KEY_LAST_NAME = "lastName";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_ROLE = "role";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void createLoginSession(int userId, String firstName, String lastName, String email, String role) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_FIRST_NAME, firstName);
        editor.putString(KEY_LAST_NAME, lastName);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ROLE, role);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1); // Trả về -1 nếu không tìm thấy
    }

    public String getUserName() {
        String firstName = sharedPreferences.getString(KEY_FIRST_NAME, "");
        String lastName = sharedPreferences.getString(KEY_LAST_NAME, "");
        return lastName + " " + firstName;
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    public String getUserRole() {
        return sharedPreferences.getString(KEY_ROLE, null);
    }

    public void logoutUser() {
        editor.clear();
        editor.apply();
    }
}
