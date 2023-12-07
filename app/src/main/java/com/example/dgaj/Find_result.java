package com.example.dgaj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Find_result extends AppCompatActivity {

    private TextView txt_found_id_pw;
    private EditText found_content;
    private Button btn_backToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_result);

        txt_found_id_pw = findViewById(R.id.txt_found_id_pw);
        found_content = findViewById(R.id.found_content);
        btn_backToLogin = findViewById(R.id.btn_backToLogin);


        // 인텐트에서 데이터 가져오기
        Intent intent = getIntent();
        if (intent != null) {
            String userId = intent.getStringExtra("userId");
            String userPw = intent.getStringExtra("userPw");

            // userId가 전달되었다면 '찾은 아이디'를 텍스트뷰에 표시
            if (userId != null) {
                txt_found_id_pw.setText("찾은 아이디");
                found_content.setText("    "+userId);
            }
            // userPw가 전달되었다면 '찾은 비밀번호'를 텍스트뷰에 표시
            else if (userPw != null) {
                txt_found_id_pw.setText("찾은 비밀번호");
                found_content.setText("     "+userPw);
            }
        }

        btn_backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Find_result.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}