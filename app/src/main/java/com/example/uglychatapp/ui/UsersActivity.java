package com.example.uglychatapp.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uglychatapp.MainApplication;
import com.example.uglychatapp.R;
import com.example.uglychatapp.adapters.AdapterUsers;
import com.example.uglychatapp.network.UserRestCalls;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class UsersActivity extends AppCompatActivity {
    private static final String TAG = "UsersActivity";

    private ProgressBar progressBar;
    private RecyclerView recyclerViewUsers;
    private MainApplication globalVariable;
    private ArrayList<String> listUsers;
    private AdapterUsers adapterUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        globalVariable = (MainApplication) getApplicationContext();
        listUsers = new ArrayList<>();

        progressBar = findViewById(R.id.activity_users_pb);
        recyclerViewUsers = findViewById(R.id.activity_users_rv_users);

        adapterUsers = new AdapterUsers(listUsers);
        recyclerViewUsers.setAdapter(adapterUsers);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

        getUsers();
    }

    public void getUsers() {
        UserRestCalls.GetAllUsers getAllUsers = new UserRestCalls.GetAllUsers(new UserRestCalls.GetAllUsersCallback() {
            @Override
            public void onTaskComplete(boolean success, JSONArray result) {
                progressBar.setVisibility(View.GONE);

                if (success) {
                    listUsers.clear();

                    for (int i = 0; i < result.length(); i++) {
                        try {
                            listUsers.add(result.getString(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    adapterUsers.notifyDataSetChanged();
                } else {
                    Toast.makeText(UsersActivity.this, R.string.couldnt_fetch_users, Toast.LENGTH_LONG).show();
                }
            }
        });
        getAllUsers.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
