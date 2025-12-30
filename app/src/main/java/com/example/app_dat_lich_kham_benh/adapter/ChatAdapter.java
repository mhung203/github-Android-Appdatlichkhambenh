package com.example.app_dat_lich_kham_benh.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.api.model.TinNhan;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<TinNhan> messages = new ArrayList<>();
    private Integer myUserId;

    public ChatAdapter(Integer myUserId) {
        this.myUserId = myUserId;
    }

    public void setMessages(List<TinNhan> list) {
        this.messages = list;
        notifyDataSetChanged();
    }

    public void addMessage(TinNhan msg) {
        this.messages.add(msg);
        notifyItemInserted(messages.size() - 1);
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tin_nhan, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        TinNhan msg = messages.get(position);
        String formattedTime = formatTime(msg.getThoiGian());

        if (msg.getNguoiGuiId() != null && msg.getNguoiGuiId().equals(myUserId)) {
            holder.layoutRight.setVisibility(View.VISIBLE);
            holder.layoutLeft.setVisibility(View.GONE);

            holder.tvContentRight.setText(msg.getNoiDung());
            holder.tvTimeRight.setText(formattedTime);
        } else {
            holder.layoutRight.setVisibility(View.GONE);
            holder.layoutLeft.setVisibility(View.VISIBLE);

            holder.tvContentLeft.setText(msg.getNoiDung());
            holder.tvTimeLeft.setText(formattedTime);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
    private String formatTime(String rawTime) {
        if (rawTime == null || rawTime.isEmpty()) return "";
        try {
            if (rawTime.contains("T")) {
                String[] parts = rawTime.split("T");
                if (parts.length > 1) {
                    String timePart = parts[1];
                    if (timePart.length() >= 5) {
                        return timePart.substring(0, 5);
                    }
                }
            }
            return rawTime;
        } catch (Exception e) {
            return "";
        }
    }
    static class ChatViewHolder extends RecyclerView.ViewHolder {
        View layoutLeft, layoutRight;
        TextView tvContentLeft, tvContentRight;
        TextView tvTimeLeft, tvTimeRight;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutLeft = itemView.findViewById(R.id.layoutLeft);
            layoutRight = itemView.findViewById(R.id.layoutRight);

            tvContentLeft = itemView.findViewById(R.id.tvContentLeft);
            tvContentRight = itemView.findViewById(R.id.tvContentRight);
            tvTimeLeft = itemView.findViewById(R.id.tvTimeLeft);
            tvTimeRight = itemView.findViewById(R.id.tvTimeRight);
        }
    }
}