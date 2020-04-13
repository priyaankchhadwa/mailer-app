package com.example.inclass08;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ComposeMailActivity extends AppCompatActivity {

    private ArrayList<String> user_list = new ArrayList<>();
    private ArrayList<User> all_users = new ArrayList<>();

    private ArrayAdapter<String> spinner_adapter;

    private SharedPreferences sharedPreferences;

    private Spinner spinner;

    private EditText subject_et;
    private EditText message_et;

    private Button send_btn;
    private Button cancel_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_mail);
        setTitle("Compose Email");

        sharedPreferences = getApplicationContext().getSharedPreferences("sharedPreferences", MODE_PRIVATE);

        user_list.add("Select a recipient");

        spinner = findViewById(R.id.spinner);
        subject_et = findViewById(R.id.subject_et);
        message_et = findViewById(R.id.message_et);

        send_btn = findViewById(R.id.send_btn);
        cancel_btn = findViewById(R.id.cancel_btn);

        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url(getString(R.string.USERS_API))
                .addHeader("Authorization", "BEARER " + sharedPreferences.getString("user_token", ""))
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Log.d("asdf", "Spinner Error: " + e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    JSONObject root = new JSONObject(response.body().string());
                    JSONArray usersList = root.getJSONArray("users");
                    for (int i = 0; i < usersList.length(); i++) {
                        JSONObject each = usersList.getJSONObject(i);

                        User user = new User(
                                each.getInt("id"),
                                each.getString("fname"),
                                each.getString("lname"));

                        all_users.add(user);

                        user_list.add(user.fname + " " + user.lname);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        spinner_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, user_list);
        spinner.setAdapter(spinner_adapter);

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject = subject_et.getText().toString();
                String message = message_et.getText().toString();

                subject_et.setError(null);
                subject_et.setError(null);

                int ind = spinner.getSelectedItemPosition() - 1;

                if (ind >= 0 && !subject.equals("") && !message.equals("")) {
                    Log.d("asdf", "spinner selection: " + all_users.get(ind));

                    OkHttpClient okHttpClient = new OkHttpClient();

                    RequestBody formBody = new FormBody.Builder()
                            .add("receiver_id", String.valueOf(all_users.get(ind).id))
                            .add("subject", subject)
                            .add("message", message)
                            .build();

                    Request request = new Request.Builder()
                            .url(getString(R.string.MAILTO_API))
                            .addHeader("Authorization", "BEARER " + sharedPreferences.getString("user_token", ""))
                            .post(formBody)
                            .build();

                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            Log.d("response", response.body().string());

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ComposeMailActivity.this, "Mail Sent", Toast.LENGTH_SHORT).show();
                                }
                            });

//                            Intent InboxIntent = new Intent();
//                            InboxIntent.setData(null);
                            finish();
                        }
                    });

                } else {
                    if (ind < 0) {
                        TextView errorText = (TextView) spinner.getSelectedView();
                        errorText.setError("");
                        errorText.setTextColor(Color.RED);//just to highlight that this is an error
                    }
                    if (subject.equals(""))
                        subject_et.setError("Enter a Subject");
                    if (message.equals(""))
                        message_et.setError("Enter a Message");
                }

            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
