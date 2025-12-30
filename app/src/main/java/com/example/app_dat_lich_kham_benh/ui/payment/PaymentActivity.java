package com.example.app_dat_lich_kham_benh.ui.payment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.ui.MainActivity;
import com.google.android.material.appbar.MaterialToolbar;

public class PaymentActivity extends AppCompatActivity {

    private static final String VNPayReturnUrl = "vnpay-return";

    private WebView webView;
    private ProgressBar progressBar;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        webView = findViewById(R.id.webview_payment);
        progressBar = findViewById(R.id.progress_bar);
        toolbar = findViewById(R.id.toolbar_payment);

        toolbar.setNavigationOnClickListener(v -> finish());

        String paymentUrl = getIntent().getStringExtra("PAYMENT_URL");

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);

                if (url.contains(VNPayReturnUrl)) {
                    Uri uri = Uri.parse(url);
                    String responseCode = uri.getQueryParameter("vnp_ResponseCode");

                    if ("00".equals(responseCode)) {
                        Intent intent = new Intent(PaymentActivity.this, PaymentSuccessActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(PaymentActivity.this, "Thanh toán đã bị hủy hoặc thất bại.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            }
        });

        if (paymentUrl != null) {
            webView.loadUrl(paymentUrl);
        } else {
            Toast.makeText(this, "URL thanh toán không hợp lệ", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
