package com.example.app_dat_lich_kham_benh.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.api.dto.ChatContactDTO;
import com.example.app_dat_lich_kham_benh.ui.ChatActivity; // Đảm bảo import đúng ChatActivity

import java.util.ArrayList;
import java.util.List;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {

    private List<ChatContactDTO> contacts = new ArrayList<>();
    private Context context;
    private Integer myId;

    public InboxAdapter(Context context, Integer myId) {
        this.context = context;
        this.myId = myId;
    }

    public void setData(List<ChatContactDTO> list) {
        this.contacts = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_inbox, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatContactDTO contact = contacts.get(position);

        holder.tvName.setText(contact.getFullName());
        holder.tvLastMessage.setText(contact.getLastMessage());
        String rawTime = contact.getTime();
        if(rawTime != null && rawTime.contains("T")) {
            holder.tvTime.setText(rawTime.split("T")[1].substring(0, 5));
        } else {
            holder.tvTime.setText(rawTime);
        }
        String urlAvartar = contact.getAvatar();
        if(urlAvartar != null && !urlAvartar.isEmpty()) {
            Glide.with(context)
                    .load(urlAvartar)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .circleCrop()
                    .into(holder.imgAvatar);
        }
        else {
            holder.imgAvatar.setImageResource(R.drawable.ic_launcher_background);
        }
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("TARGET_ID", contact.getUserId());
            intent.putExtra("TARGET_NAME", contact.getFullName());
            intent.putExtra("MY_ID", myId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return contacts.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvLastMessage, tvTime;
        ImageView imgAvatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
        }
    }
}