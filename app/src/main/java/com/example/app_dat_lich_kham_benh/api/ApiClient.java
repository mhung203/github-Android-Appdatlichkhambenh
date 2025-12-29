package com.example.app_dat_lich_kham_benh.api;

import com.example.app_dat_lich_kham_benh.api.service.ApiService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiClient {

    // Use 10.0.2.2 to connect to host machine's localhost from Android emulator
    private static final String BASE_URL = "http://10.0.2.2:8081/";

    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create()) // Add this first
                    .addConverterFactory(GsonConverterFactory.create())    // Then add Gson
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
