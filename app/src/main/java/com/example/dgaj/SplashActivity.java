package com.example.dgaj;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

//앱 실행 시켰을 때 로딩화면
public class SplashActivity extends AppCompatActivity {

    private ImageView imageView;
    private AnimationDrawable animationDrawable;

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_splash);

        imageView = findViewById(R.id.splash_anim);
        animationDrawable = (AnimationDrawable) imageView.getDrawable();

        animationDrawable.start(); //로딩화면 실행

        new Handler().postDelayed(new Runnable() { //일정시간동안 딜레이 후 run()안의 내용 수행
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, Login.class);
                startActivity(intent);
                animationDrawable.stop(); //로딩화면 종료
                finish();
            }
        }, 3000); //3초동안 로딩화면 실행
    }

}
