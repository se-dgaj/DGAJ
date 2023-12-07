package com.example.dgaj;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Customer_myAccount extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private Button btn_change_password, btn_change_tel, btn_delete_account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_my_account);

        btn_change_password = findViewById(R.id.btn_change_password);
        btn_change_tel = findViewById(R.id.btn_change_tel);
        btn_delete_account = findViewById(R.id.btn_delete_account);

        btn_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Customer_myAccount.this, Customer_changePW.class);
                startActivity(intent);
            }
        });


        btn_change_tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Customer_myAccount.this, Customer_changeTel.class);
                startActivity(intent);
            }
        });


        btn_delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad = new AlertDialog.Builder(Customer_myAccount.this);
                ad.setIcon(R.drawable.ic_warning);
                ad.setTitle("Warning");
                ad.setMessage("정말 회원 정보를 삭제하시겠습니까?");


                ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseUser user = mFirebaseAuth.getCurrentUser();
                        String userID = user.getUid();

                        mStore.collection(FirebaseID.id_list).document(userID).delete();
                        mFirebaseAuth.getCurrentUser().delete();

                        Intent intent = new Intent(Customer_myAccount.this, Login.class);
                        startActivity(intent);

                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "회원 정보가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

                ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                ad.show();
            }
        });
    }
}