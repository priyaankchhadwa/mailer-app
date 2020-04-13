package com.example.inclass08;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignupActivity extends AppCompatActivity {

    private TextView first_name_et;
    private TextView last_name_et;
    private TextView email_et;
    private TextView password_et;
    private TextView repeat_password_et;

    private Button signup_btn;
    private Button cancel_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        setTitle("Sign Up");

        first_name_et = findViewById(R.id.first_name_et);
        last_name_et = findViewById(R.id.last_name_et);
        email_et = findViewById(R.id.email_et);
        password_et = findViewById(R.id.password_et);
        repeat_password_et = findViewById(R.id.repeat_password_et);
        signup_btn = findViewById(R.id.signup_btn);
        cancel_btn = findViewById(R.id.cancel_btn);


        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MainActivity.hideSoftKeyboard(SignupActivity.this);
                String first_name = first_name_et.getText().toString();
                String last_name = last_name_et.getText().toString();
                String email = email_et.getText().toString();
                String password = password_et.getText().toString();
                String repeat_password = repeat_password_et.getText().toString();
                Boolean b_email = false;
                Boolean b_pass = false;
                Boolean b_pass_len = false;

                if (!Pattern.matches("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", email)) {
                    email_et.setError("Invalid email");
                    b_email = false;
                } else {
                    email_et.setError(null);
                    b_email = true;
                }

                if (password.length() < 6) {
                    password_et.setError("Passwords has to be greater than 6 characters");
                    b_pass_len = false;
                } else {
                    password_et.setError(null);
                    b_pass_len = true;
                }

                if (!password.equals(repeat_password)) {
                    repeat_password_et.setError("Passwords do not match!");
                    b_pass = false;
                } else {
                    repeat_password_et.setError(null);
                    b_pass = true;
                }


                if (b_email && b_pass && b_pass_len) {
                    OkHttpClient client = new OkHttpClient();

                    RequestBody formBody = new FormBody.Builder()
                            .add("email", email)
                            .add("password", password)
                            .add("fname", first_name)
                            .add("lname", last_name)
                            .build();

                    Request request = new Request.Builder()
                            .url(getString(R.string.SIGNUP_API))
                            .post(formBody)
                            .build();

                    client.newCall(request).enqueue(new Callback() {

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            Log.d("asdf", response.body().toString());
                            if (response.isSuccessful()) {
                                try {
                                    Log.d("asdf", "in try");

                                    JSONObject root = new JSONObject(response.body().string());
                                    Log.d("asdf", "response = " + root);
                                    String token = root.getString("token");
                                    String user_id = root.getString("user_id");
                                    String user_fname = root.getString("user_fname");
                                    String user_lname = root.getString("user_lname");
                                    Log.d("token in signup Activity", "userid: " + user_id + " name: " + user_fname + user_lname);

                                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("sharedPreferences", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("user_token", token);
                                    editor.commit();

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(SignupActivity.this, "User created successfully!", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    Intent signupIntent = new Intent(SignupActivity.this, InboxActivity.class);
                                    startActivity(signupIntent);
                                    finish();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    final JSONObject root = new JSONObject(response.body().string());
                                    Log.d("asdf", "" + root);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                Toast.makeText(SignupActivity.this, "ERROR!", Toast.LENGTH_SHORT).show();
                                                Toast.makeText(SignupActivity.this, root.getString("message"), Toast.LENGTH_SHORT).show();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            e.printStackTrace();
                        }
                    });
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
