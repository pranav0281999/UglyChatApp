package com.example.uglychatapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uglychatapp.R;
import com.example.uglychatapp.models.ChatMessage;

import java.util.List;

public class AdapterChatMsgs extends RecyclerView.Adapter<AdapterChatMsgs.ViewHolder> {
    private List<ChatMessage> chatMessageList;

    public AdapterChatMsgs(List<ChatMessage> chatMessageList) {
        this.chatMessageList = chatMessageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View msgView = layoutInflater.inflate(R.layout.listitem_chat_msg, parent, false);

        return new ViewHolder(msgView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessageList.get(position);
        TextView tvMsg = holder.tvMsg;
        tvMsg.setText(chatMessage.getBody());

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvMsg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMsg = itemView.findViewById(R.id.listitem_chat_msg_tv_msg);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }
}
