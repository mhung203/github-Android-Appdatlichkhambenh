package com.example.app_dat_lich_kham_benh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.api.model.CaKham;
import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder> {

    private List<CaKham> timeSlots;
    private Context context;
    private int selectedPosition = -1;
    private OnTimeSlotClickListener listener;

    public interface OnTimeSlotClickListener {
        void onTimeSlotClick(CaKham ca);
    }

    public TimeSlotAdapter(List<CaKham> timeSlots, Context context, OnTimeSlotClickListener listener) {
        this.timeSlots = timeSlots;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time_slot, parent, false);
        return new TimeSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        CaKham ca = timeSlots.get(position);
        holder.tvTimeSlot.setText(ca.getTimeStart() + " - " + ca.getTimeEnd());

        holder.itemView.setSelected(selectedPosition == position);

        holder.itemView.setOnClickListener(v -> {
            if (selectedPosition >= 0) {
                notifyItemChanged(selectedPosition);
            }
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(selectedPosition);
            listener.onTimeSlotClick(ca);
        });
    }

    @Override
    public int getItemCount() {
        return timeSlots.size();
    }

    public static class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        TextView tvTimeSlot;

        public TimeSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTimeSlot = itemView.findViewById(R.id.tv_time_slot);
        }
    }
}
