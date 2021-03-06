package com.example.uglychatapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uglychatapp.MainApplication;
import com.example.uglychatapp.R;
import com.example.uglychatapp.ui.ChatActivity;

import java.util.ArrayList;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.ViewHolder> {
    private ArrayList<String> userList;
    MainApplication globalVariable;

    public AdapterUsers(MainApplication globalVariable, ArrayList<String> userList) {
        this.userList = userList;
        this.globalVariable = globalVariable;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View msgView = layoutInflater.inflate(R.layout.listitem_user, parent, false);

        return new ViewHolder(msgView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String username = userList.get(position);
        TextView tvUsername = holder.tvUsername;
        RelativeLayout relativeLayout = holder.relativeLayout;
        tvUsername.setText(username);

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(globalVariable, ChatActivity.class);
                intent.putExtra("receiver", username);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                globalVariable.startActivity(intent);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvUsername;
        public RelativeLayout relativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUsername = itemView.findViewById(R.id.listitem_user_name);
            relativeLayout = itemView.findViewById(R.id.listitem_user_rl);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
