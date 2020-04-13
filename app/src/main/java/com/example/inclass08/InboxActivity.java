package com.example.inclass08;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InboxActivity extends AppCompatActivity implements OnItemListener {

    ArrayList<Message> data = new ArrayList<>();

    private ImageView compose_mail_iv;
    private TextView user_name_tv;

    private ImageView logout_iv;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    SharedPreferences sharedPreferences;

    void getMailList() {
        data.clear();
        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url(getString(R.string.INBOX_API))
                .addHeader("Authorization", "BEARER " + sharedPreferences.getString("user_token", ""))
                .build();

        final SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-DD hh:mm:ss", Locale.US);

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    JSONObject root = new JSONObject(response.body(). string());
                    JSONArray mail = root.getJSONArray("messages");
                    for (int i = 0; i < mail.length(); i++) {
                        JSONObject each = mail.getJSONObject(i);

                        data.add(new Message(each.getString("sender_fname"),
                                each.getString("sender_lname"),
                                each.getInt("id"),
                                each.getString("message"),
                                each.getString("subject"),
                                newFormat.parse(each.getString("created_at"))));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        setTitle("Inbox");

        sharedPreferences = getApplicationContext().getSharedPreferences("sharedPreferences", MODE_PRIVATE);

        compose_mail_iv = findViewById(R.id.compose_mail_iv);
        user_name_tv = findViewById(R.id.user_name_tv);

        logout_iv = findViewById(R.id.logout_iv);

        recyclerView = findViewById(R.id.inbox_recycler);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new MailAdapter(data, this);
        recyclerView.setAdapter(mAdapter);

        user_name_tv.setText(sharedPreferences.getString("user_name", ""));

        compose_mail_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent composeIntent = new Intent(InboxActivity.this, ComposeMailActivity.class);
                startActivityForResult(composeIntent, 200);
            }
        });

        logout_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("sharedPreferences", MODE_PRIVATE);
                sharedPreferences.edit().clear().apply();

                Intent loginIntent = new Intent(InboxActivity.this, MainActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });

        getMailList();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        data.clear();
        getMailList();
    }

    @Override
    public void onItemClick(int pos) {
        Log.d("asdf", "clicked: " + data.get(pos));
        Intent intent = new Intent(InboxActivity.this, MessageDetailsActivity.class);
        intent.putExtra("message", data.get(pos));
        startActivity(intent);
    }


    public void deleteMail(View view) {
        Log.d("asdf", "delete this: " + view.getTag());
        final Message message = (Message) view.getTag();

        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url(String.format(getString(R.string.DELETE_API), message.id))
                .addHeader("Authorization", "BEARER " + sharedPreferences.getString("user_token", ""))
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                int index = data.indexOf(message);
                data.remove(index);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}
