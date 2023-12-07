package com.example.dgaj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class Customer_game_choose extends AppCompatActivity {

    private TextView txt_chooseGame;
    private ImageButton btn_chooseRoulette,btn_chooseBylots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_game_choose);

        txt_chooseGame = findViewById(R.id.txt_chooseGame);
        btn_chooseRoulette = findViewById(R.id.btn_chooseRoulette);
        btn_chooseBylots = findViewById(R.id.btn_chooseBylots);

        btn_chooseRoulette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Customer_game_choose.this, Customer_game_setItem.class);
                startActivity(intent);
            }
        });

        btn_chooseBylots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Customer_game_choose.this, Customer_game_byLots.class);
                startActivity(intent);
            }
        });
    }
}