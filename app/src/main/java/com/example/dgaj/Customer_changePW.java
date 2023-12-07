package com.example.dgaj;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Customer_changePW extends AppCompatActivity {

    private EditText et_existPW, et_newPW, et_checkPW;

    private Button btn_checkPW, btn_ok;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance(); //파이어베이스 인증 받고
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance(); //인스턴스 가져옴

    private boolean check = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        //Toast.makeText(Customer_changePW.this, "비밀번호 변경 클래스 들어옴", Toast.LENGTH_SHORT).show();

        this.et_existPW = findViewById(R.id.et_existPW);
        this.et_newPW = findViewById(R.id.et_newPW);
        this.et_checkPW = findViewById(R.id.et_checkPW);
        this.btn_checkPW = findViewById(R.id.btn_checkPW);
        this.btn_ok = findViewById(R.id.btn_ok);


        this.btn_checkPW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "비밀번호 확인 버튼 클릭.", Toast.LENGTH_SHORT).show();

                Log.d("ButtonClicked", "Button clicked!");

                final String exist_PW = et_existPW.getText().toString().trim();

                if (exist_PW.isEmpty()) {
                    Toast.makeText(Customer_changePW.this, "기존 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();

                } else {
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userID = user.getUid();

                    mStore.collection(FirebaseID.id_list).document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {

                                    String password = document.getString(FirebaseID.password);


                                    if (password != null) {


                                        if (password.equals(exist_PW)) {
                                            check = true;
                                            Toast.makeText(Customer_changePW.this, "기존 비밀번호가 일치합니다.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(Customer_changePW.this, "기존 비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                                        }


                                    } else {

                                        Toast.makeText(Customer_changePW.this, "기존 비밀번호가 NULL입니다.", Toast.LENGTH_SHORT).show();

                                    }
                                } else {

                                    Toast.makeText(Customer_changePW.this, "문서에 password 필드가 없습니다", Toast.LENGTH_SHORT).show();

                                }
                            } else {
                                Exception exception = task.getException();
                                if (exception != null) {
                                    Log.e("FirestoreQuery", "Query failed: " + exception.getMessage());
                                }
                            }
                        }
                    });
                }

            }
        });

        this.btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "확인 버튼 클릭", Toast.LENGTH_SHORT).show();

                Log.d("ButtonClicked", "Button clicked!");

                final String exist_PW = et_existPW.getText().toString().trim();

                final String newPW = et_newPW.getText().toString().trim();

                final String checkPW = et_checkPW.getText().toString().trim();

                if (!exist_PW.isEmpty() && check == true) {
                    if (!exist_PW.isEmpty() && !newPW.isEmpty() && !checkPW.isEmpty()) {


                        FirebaseUser user = mAuth.getCurrentUser();
                        String userID = user.getUid();

                        mStore.collection(FirebaseID.id_list).document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {

                                        String password = document.getString(FirebaseID.password);


                                        if (password != null) {

                                            if (password.equals(exist_PW)) {

                                                if (exist_PW.equals(newPW)) {
                                                    Toast.makeText(Customer_changePW.this, "기존 비밀번호가 변경할 비밀번호와 같습니다.", Toast.LENGTH_SHORT).show();
                                                } else if (newPW.equals(checkPW)) {

                                                    FirebaseUser user = mAuth.getCurrentUser();
                                                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), exist_PW);


                                                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> reauthTask) {
                                                            if (reauthTask.isSuccessful()) {
                                                                FirebaseUser user = mAuth.getCurrentUser();

                                                                user.updatePassword(newPW).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> updateTask) {
                                                                        if (updateTask.isSuccessful()) {

                                                                            Log.d(TAG, "Password updated successfully in Firebase Authentication!");

                                                                            DocumentReference userDocRef = mStore.collection(FirebaseID.id_list).document(userID); //인증 update후 store 업데이트
                                                                            Map<String, Object> updates = new HashMap<>();
                                                                            updates.put(FirebaseID.password, newPW);
                                                                            userDocRef.update(updates);
                                                                            finish();
                                                                            Toast.makeText(Customer_changePW.this, "비밀번호가 업데이트되었습니다.", Toast.LENGTH_SHORT).show();
                                                                        } else {

                                                                            Log.w(TAG, "Error updating password in Firebase Authentication", updateTask.getException());

                                                                            Toast.makeText(Customer_changePW.this, "Firebase Authentication에서 비밀번호를 업데이트하는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                            } else {

                                                                Log.w(TAG, "Error re-authenticating user", reauthTask.getException());

                                                                Toast.makeText(Customer_changePW.this, "재인증 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                } else {

                                                    Toast.makeText(Customer_changePW.this, "새로운 비밀번호가 확인 비밀번호와 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {

                                                Toast.makeText(Customer_changePW.this, "기존 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                                            }


                                        } else {

                                            Toast.makeText(Customer_changePW.this, "기존 비밀번호가 NULL입니다.", Toast.LENGTH_SHORT).show();

                                        }
                                    } else {

                                        Toast.makeText(Customer_changePW.this, "password 필드가 없습니다", Toast.LENGTH_SHORT).show();

                                    }
                                } else {

                                    Exception exception = task.getException();
                                    if (exception != null) {
                                        Log.e("FirestoreQuery", "Query failed: " + exception.getMessage());
                                    }
                                }
                            }
                        });

                    } else {
                        //  Toast.makeText(Customer_changePW.this, "기존 비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show();
                        Toast.makeText(Customer_changePW.this, "빈칸 없이 모두 입력해주세요.", Toast.LENGTH_SHORT).show();

                    }
                } else if (!exist_PW.isEmpty() && !newPW.isEmpty() && !checkPW.isEmpty() && check == false) {
                    Toast.makeText(Customer_changePW.this, "기존 비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(Customer_changePW.this, "빈칸 없이 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }
}