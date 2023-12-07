package com.example.dgaj;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Choose_SignupType extends AppCompatActivity {

    private String type;
    private Button btn_choose_user, btn_choose_owner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_signup_type);

        btn_choose_user = findViewById(R.id.btn_choose_user);
        btn_choose_owner = findViewById(R.id.btn_choose_owner);

        btn_choose_user.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                type = "user";
                Intent intent = new Intent(Choose_SignupType.this, Signup_user.class);
                intent.putExtra("type", type);
                startActivity(intent);
            }
        });

        btn_choose_owner.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                type = "owner";
                Intent intent = new Intent(Choose_SignupType.this, Signup_owner.class);
                intent.putExtra("type", type);
                startActivity(intent);
            }
        });
    }

}

