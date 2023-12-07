package com.example.dgaj;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class Customer_game_byLots extends AppCompatActivity {

    private TextView txt_bylots, txt_printMenu;
    private EditText et_input_menu;

    private Button btn_inputMenu, btn_byLots, btn_reset, btn_home;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_game_by_lots);

        txt_bylots = findViewById(R.id.txt_bylots);
        txt_printMenu = findViewById(R.id.txt_printMenu);
        et_input_menu = findViewById(R.id.et_input_menu);
        btn_inputMenu = findViewById(R.id.btn_inputMenu);
        btn_byLots = findViewById(R.id.btn_byLots);
        btn_reset = findViewById(R.id.btn_reset);
        btn_home = findViewById(R.id.btn_home);

        ArrayList<String> menuList = new ArrayList<>();

        btn_inputMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String tmp = et_input_menu.getText().toString().trim();

                if (!tmp.isEmpty()) {
                    menuList.add(tmp); //랜덤 뽑기 위해 리스트에 저장

                    // menu = menu.concat(tmp + " "); //출력 위해 제비뽑기 요소들 하나의 string으로
                    txt_printMenu.setText(menuList.toString());

                    et_input_menu.setText(null);


                } else {
                    Toast.makeText(Customer_game_byLots.this, "메뉴를 입력하지 않았습니다.", Toast.LENGTH_SHORT).show();
                }


            }
        });

        btn_byLots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(Customer_game_byLots.this);


                if (!menuList.isEmpty()) {
                    Random random = new Random();
                    int randomIndex = random.nextInt(menuList.size());
                    dialog.setIcon(R.drawable.ic_check);
                    dialog.setTitle("제비 뽑기 결과");
                    dialog.setMessage(menuList.get(randomIndex));

                    dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                    //Toast.makeText(Customer_game_byLots.this, "제비 뽑기 결과:" + menuList.get(randomIndex), Toast.LENGTH_SHORT).show();
                } else {
                    dialog.setIcon(R.drawable.ic_warning);
                    dialog.setTitle("warning");
                    dialog.setMessage("메뉴를 입력하지 않았습니다.");

                    dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                    //Toast.makeText(Customer_game_byLots.this, "메뉴를 입력하지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(Customer_game_byLots.this);

                dialog.setIcon(R.drawable.ic_warning);
                dialog.setTitle("Warning");
                dialog.setMessage("리스트를 초기화하시겠습니까?");

                if(!menuList.isEmpty()){
                    dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            menuList.clear();
                            txt_printMenu.setText(null);
                            dialog.dismiss(); //dialog 닫기
                            Toast.makeText(Customer_game_byLots.this, "메뉴 리스트가 초기화 되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });

                    dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss(); ////dialog 닫기
                        }
                    });

                    dialog.show();
                } else {
                    dialog.setIcon(R.drawable.ic_warning);
                    dialog.setTitle("warning");
                    dialog.setMessage("입력된 메뉴가 없습니다.");

                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                    Toast.makeText(Customer_game_byLots.this, "메뉴를 입력하지 않았습니다.", Toast.LENGTH_SHORT).show();
                }




                Toast.makeText(Customer_game_byLots.this, "초기화 버튼 클릭", Toast.LENGTH_SHORT).show();
            }
        });

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Customer_game_byLots.this, Customer_searchScreen.class);
                startActivity(intent);
            }
        });
    }
}