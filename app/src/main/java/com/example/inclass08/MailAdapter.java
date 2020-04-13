package com.example.inclass08;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MailAdapter extends RecyclerView.Adapter<MailAdapter.ViewHolder> {

    private ArrayList<Message> mData;
    private OnItemListener mOnItemListener;

    MailAdapter(ArrayList<Message> mData, OnItemListener mOnItemListener) {
        this.mData = mData;
        this.mOnItemListener = mOnItemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mail_item, parent, false);
        return new ViewHolder(view, mOnItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = mData.get(position);

        holder.subject_tv.setText(message.subject);
        holder.date_tv.setText(new SimpleDateFormat("MMM dd, yyyy", Locale.US).format(message.created_at));
        holder.delete_iv.setTag(message);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView subject_tv, date_tv;
        ImageView delete_iv;
        Message message;
        OnItemListener onItemListener;

        public ViewHolder(@NonNull final View itemView, OnItemListener onItemListener) {
            super(itemView);
            subject_tv = itemView.findViewById(R.id.subject_tv);
            date_tv = itemView.findViewById(R.id.date_tv);
            delete_iv = itemView.findViewById(R.id.delete_iv);
            this.onItemListener = onItemListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(getAdapterPosition());
        }
    }
}
