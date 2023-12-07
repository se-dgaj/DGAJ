package com.example.dgaj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Customer_game_setItem extends AppCompatActivity {

    private TextView txt_menu_game, txt_menu_input;
    private EditText item1, item2, item3, item4, item5, item6;
    private Button btn_next;

    public static String[] item = new String[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_game_set_item);

        txt_menu_game = findViewById(R.id.txt_menu_game);
        txt_menu_input = findViewById(R.id.txt_menu_input);
        item1 = findViewById(R.id.item1);
        item2 = findViewById(R.id.item2);
        item3 = findViewById(R.id.item3);
        item4 = findViewById(R.id.item4);
        item5 = findViewById(R.id.item5);
        item6 = findViewById(R.id.item6);
        btn_next = findViewById(R.id.btn_next);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String i1 = item1.getText().toString().trim();
                final String i2 = item2.getText().toString().trim();
                final String i3 = item3.getText().toString().trim();
                final String i4 = item4.getText().toString().trim();
                final String i5 = item5.getText().toString().trim();
                final String i6 = item6.getText().toString().trim();

                if (!i1.isEmpty() && !i2.isEmpty() && !i3.isEmpty() && !i4.isEmpty() && !i5.isEmpty() && !i6.isEmpty()) {
                    item[0] = i1;
                    item[1] = i2;
                    item[2] = i3;
                    item[3] = i4;
                    item[4] = i5;
                    item[5] = i6;

                    Intent intent = new Intent(Customer_game_setItem.this, Customer_game_roulette.class);
                    startActivity(intent);
                    //finish();
                }
                else{
                    Toast.makeText(Customer_game_setItem.this, "6개 모두 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}