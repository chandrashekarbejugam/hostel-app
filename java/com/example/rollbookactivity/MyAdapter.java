package com.example.rollbookactivity;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.NotifyViewHolder> {

    private List<Notify> notifyList;

    public MyAdapter(List<Notify> notifyList) {
        this.notifyList = notifyList;
    }

    public void setNotifyList(List<Notify> notifyList) {
        this.notifyList = notifyList;
    }

    @NonNull
    @Override
    public NotifyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_layout, parent, false);
        return new NotifyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifyViewHolder holder, int position) {
        Notify notify = notifyList.get(position);
        holder.bind(notify);
    }

    @Override
    public int getItemCount() {
        return notifyList.size();
    }

    public static class NotifyViewHolder extends RecyclerView.ViewHolder {

        private TextView dateTextView;
        private TextView messageTextView;

        public NotifyViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.uploadDate);
            messageTextView = itemView.findViewById(R.id.messageText);
        }

        public void bind(Notify notify) {
            dateTextView.setText(notify.getDate());
            messageTextView.setText(notify.getMessage());
        }
    }
}

