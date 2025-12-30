package com.example.app_dat_lich_kham_benh.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app_dat_lich_kham_benh.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {

    private List<Calendar> dateList;
    private Context context;
    private int selectedPosition = 0;
    private OnDateClickListener listener;
    private SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEE", new Locale("vi", "VN")); // e.g., "T2"
    private SimpleDateFormat dayOfMonthFormat = new SimpleDateFormat("d", Locale.getDefault());

    public interface OnDateClickListener {
        void onDateClick(Calendar date);
    }

    public DateAdapter(Context context, OnDateClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.dateList = generateDates();
    }

    private List<Calendar> generateDates() {
        List<Calendar> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 30; i++) {
            Calendar date = (Calendar) calendar.clone();
            dates.add(date);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        return dates;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        Calendar date = dateList.get(position);

        holder.tvDayOfWeek.setText(dayOfWeekFormat.format(date.getTime()));
        holder.tvDayOfMonth.setText(dayOfMonthFormat.format(date.getTime()));

        holder.itemView.setSelected(selectedPosition == position);

        if (selectedPosition == position) {
            holder.tvDayOfWeek.setTextColor(Color.WHITE);
            holder.tvDayOfMonth.setTextColor(Color.WHITE);
        } else {
            holder.tvDayOfWeek.setTextColor(Color.BLACK);
            holder.tvDayOfMonth.setTextColor(Color.BLACK);
        }

        holder.itemView.setOnClickListener(v -> {
            if (selectedPosition >= 0) {
                notifyItemChanged(selectedPosition);
            }
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(selectedPosition);
            listener.onDateClick(date);
        });
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public Calendar getInitialDate() {
        return dateList.get(0);
    }

    public static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayOfWeek, tvDayOfMonth;
        LinearLayout container;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayOfWeek = itemView.findViewById(R.id.tv_day_of_week);
            tvDayOfMonth = itemView.findViewById(R.id.tv_day_of_month);
            container = itemView.findViewById(R.id.date_item_container);
        }
    }
}
