package com.example.app_dat_lich_kham_benh.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.adapter.ChatAdapter;
import com.example.app_dat_lich_kham_benh.api.dto.ChatMessageDTO;
import com.example.app_dat_lich_kham_benh.api.model.TinNhan;
import com.example.app_dat_lich_kham_benh.api.service.ApiService;
import com.google.gson.Gson;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class ChatActivity extends AppCompatActivity {

    private EditText etMessage;
    private Button btnSend;
    private RecyclerView rvChat;
    private ChatAdapter adapter;
    private Integer MY_ID;
    private Integer TARGET_ID;
    private String TARGET_NAME;

    private StompClient stompClient;
    private CompositeDisposable compositeDisposable;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        TARGET_ID = getIntent().getIntExtra("TARGET_ID", -1);
        MY_ID = getIntent().getIntExtra("MY_ID", -1);
        TARGET_NAME = getIntent().getStringExtra("TARGET_NAME");
        TextView tvTitle = findViewById(R.id.tvTitle);
        if(TARGET_NAME != null) {
            tvTitle.setText(TARGET_NAME);
        }

        if (TARGET_ID == -1 || MY_ID == -1) {
            Toast.makeText(this, "Lỗi xác định người dùng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        rvChat = findViewById(R.id.rvChat);


        adapter = new ChatAdapter(MY_ID);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(adapter);


        initWebSocket();

        loadChatHistory();


        btnSend.setOnClickListener(v -> sendMessage());

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }
    private void initWebSocket() {
        String url = "ws://10.0.2.2:8081/ws/websocket";

        Log.d("ChatDebug", "Đang thử kết nối tới: " + url);

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url);
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        compositeDisposable = new CompositeDisposable();

        stompClient.connect();
        compositeDisposable.add(stompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.d("ChatDebug", ">>> KẾT NỐI THÀNH CÔNG! (OPENED)");
                            Toast.makeText(this, "Đã kết nối Chat!", Toast.LENGTH_SHORT).show();
                            subscribeToMessages();
                            break;
                        case ERROR:
                            Log.e("ChatDebug", ">>> LỖI KẾT NỐI (ERROR):", lifecycleEvent.getException());
                            Toast.makeText(this, "Lỗi kết nối Server!", Toast.LENGTH_SHORT).show();
                            break;
                        case CLOSED:
                            Log.d("ChatDebug", ">>> ĐÃ NGẮT KẾT NỐI (CLOSED)");
                            break;
                    }
                }));
    }

    private void subscribeToMessages() {
        String topic = "/user/" + MY_ID + "/queue/messages";

        compositeDisposable.add(stompClient.topic(topic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d("Chat", "Nhận tin mới: " + topicMessage.getPayload());
                    TinNhan tinNhan = gson.fromJson(topicMessage.getPayload(), TinNhan.class);
                    adapter.addMessage(tinNhan);
                    rvChat.smoothScrollToPosition(adapter.getItemCount() - 1);
                }, throwable -> {
                    Log.e("Chat", "Lỗi nhận tin: " + throwable.getMessage());
                }));
    }

    private void sendMessage() {
        String content = etMessage.getText().toString().trim();
        Log.d("ChatDebug", "Nút Gửi được bấm. Nội dung: " + content);

        if (content.isEmpty()) {
            Log.d("ChatDebug", "Nội dung rỗng -> Không gửi.");
            return;
        }

        if (!stompClient.isConnected()) {
            Log.e("ChatDebug", "CHƯA KẾT NỐI WEBSOCKET -> Không thể gửi tin!");
            Toast.makeText(this, "Chưa kết nối đến Server", Toast.LENGTH_SHORT).show();
            return;
        }

        ChatMessageDTO chatMessage = new ChatMessageDTO(MY_ID, TARGET_ID, content);
        String jsonPayload = gson.toJson(chatMessage);

        Log.d("ChatDebug", "Đang gửi payload: " + jsonPayload);

        compositeDisposable.add(stompClient.send("/app/chat", jsonPayload)
                .subscribe(() -> {
                    Log.d("ChatDebug", ">>> Gửi tin thành công (Server đã nhận lệnh)");
                    etMessage.setText("");
                }, throwable -> {
                    Log.e("ChatDebug", ">>> Gửi tin thất bại:", throwable);
                }));
        TinNhan tinNhanAo = new TinNhan();
        tinNhanAo.setNguoiGuiId(MY_ID);
        tinNhanAo.setNguoiNhanId(TARGET_ID);
        tinNhanAo.setNoiDung(content);
        tinNhanAo.setThoiGian(new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date()));

        adapter.addMessage(tinNhanAo); // Thêm vào danh sách
        rvChat.smoothScrollToPosition(adapter.getItemCount() - 1);
        etMessage.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (stompClient != null) stompClient.disconnect();
        if (compositeDisposable != null) compositeDisposable.dispose();
    }
    private void loadChatHistory() {
        ApiService apiService = com.example.app_dat_lich_kham_benh.api.ApiClient.getApiService();
        apiService.getChatHistory(MY_ID, TARGET_ID).enqueue(new retrofit2.Callback<List<TinNhan>>() {
            @Override
            public void onResponse(retrofit2.Call<List<TinNhan>> call, retrofit2.Response<List<TinNhan>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TinNhan> list = response.body();
                    adapter.setMessages(list);
                    if (!list.isEmpty()) {
                        rvChat.scrollToPosition(list.size() - 1);
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<TinNhan>> call, Throwable t) {
                Log.e("ChatError", "Lỗi tải lịch sử chat: " + t.getMessage());
            }
        });
    }

}
