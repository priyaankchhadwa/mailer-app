package com.example.inclass08;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MessageDetailsActivity extends AppCompatActivity {

    private TextView created_at_tv;
    private TextView sender_tv;
    private TextView subject_tv;
    private TextView message_tv;

    private Button back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);

        created_at_tv = findViewById(R.id.created_at_tv);
        sender_tv = findViewById(R.id.sender_tv);
        subject_tv = findViewById(R.id.subject_tv);
        message_tv = findViewById(R.id.message_tv);

        back_btn = findViewById(R.id.back_btn);

        Intent intent = getIntent();

        Message message = (Message) intent.getSerializableExtra("message");

        created_at_tv.setText(message.created_at.toString());
        sender_tv.setText(message.sender_fname + " " + message.sender_lname);
        subject_tv.setText(message.subject);
        message_tv.setText(message.message);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
