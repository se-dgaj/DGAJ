package com.example.dgaj;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Choose_Find_ID_PW extends AppCompatActivity {

    private Button btn_find_id, btn_find_pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_find_id_pw);

        btn_find_id = findViewById(R.id.btn_find_id);
        btn_find_pw = findViewById(R.id.btn_find_pw);

        btn_find_id.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Choose_Find_ID_PW.this, FindID.class);
                startActivity(intent);
            }
        });

        btn_find_pw.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Choose_Find_ID_PW.this, FindPW.class);
                startActivity(intent);
            }
        });
    }

}

