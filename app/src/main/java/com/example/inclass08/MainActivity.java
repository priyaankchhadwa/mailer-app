package com.example.inclass08;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private final OkHttpClient client = new OkHttpClient();

    private EditText email_et;
    private EditText password_et;

    private Button login_btn;
    private Button signup_btn;

    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<Message> messages = new ArrayList<>();

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Mailer");

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        String user_shared_pref = sharedPreferences.getString("user_token", "");
        Log.d("asdf", "user shared pref: " + user_shared_pref);

        email_et = findViewById(R.id.email_et);
        password_et = findViewById(R.id.password_et);
        login_btn = findViewById(R.id.login_btn);
        signup_btn = findViewById(R.id.signup_btn);

        if (!user_shared_pref.equals("")) {
            Intent email_intent = new Intent(MainActivity.this, InboxActivity.class);
            startActivity(email_intent);
            finish();
        } else {
            login_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    hideSoftKeyboard(MainActivity.this);

                    String email = email_et.getText().toString();
                    String password = password_et.getText().toString();

                    if (!Pattern.matches("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", email)) {
                        email_et.setError("Invalid email");
                    } else if (password.equals("")) {
                        password_et.setError("Enter a password");
                    } else {
                        try {
                            email_et.setError(null);
                            OkHttpClient client = new OkHttpClient();

                            RequestBody formBody = new FormBody.Builder()
                                    .add("email", email)
                                    .add("password", password)
                                    .build();

                            Request request = new Request.Builder()
                                    .url(getString(R.string.LOGIN_API))
                                    .post(formBody)
                                    .build();

                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onResponse(Call call, final Response response) throws IOException {
                                    try {
                                        if (response.isSuccessful()) {
                                            JSONObject root = new JSONObject(response.body().string());
                                            String status = root.getString("status");
                                            String token = root.getString("token");
                                            String user_id = root.getString("user_id");
                                            String user_email = root.getString("user_email");
                                            String user_fname = root.getString("user_fname");
                                            String user_lname = root.getString("user_lname");

                                            Log.d("asdf", "username = " + user_email);
                                            Log.d("asdf", "user_fname + lname = " + user_fname + user_lname);
                                            Log.d("asdf", "Token = " + token);

                                            Gson gson = new Gson();
                                            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("sharedPreferences", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("user_token", token);
                                            editor.putString("user_name", user_fname + " " + user_lname);
                                            editor.apply();

                                            Intent emailIntent = new Intent(MainActivity.this, InboxActivity.class);
                                            startActivity(emailIntent);
                                            finish();
                                        } else {
                                            try {
                                                final JSONObject root = new JSONObject(response.body().string());
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            Toast.makeText(MainActivity.this, "ERROR!", Toast.LENGTH_SHORT).show();
                                                            Toast.makeText(MainActivity.this, root.getString("message"), Toast.LENGTH_SHORT).show();
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Login unsuccessful!", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }
            });

        }

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                hideSoftKeyboard(MainActivity.this);
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

    }
}

