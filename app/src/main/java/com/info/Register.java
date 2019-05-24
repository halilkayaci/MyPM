package com.info;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Register extends AppCompatActivity {
    private EditText et_username, et_pass, et_rePass, et_email;
    protected Button btn_register;
    private Context context = this;
    private String username, passwd, repasswd, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Widgetler nesnelere bağlanıyor
        et_username = findViewById(R.id.et_createUsername);
        et_pass = findViewById(R.id.et_createPassword);
        et_rePass = findViewById(R.id.et_createRepassword);
        et_email = findViewById(R.id.et_createEmail);
        btn_register = findViewById(R.id.btn_register);

        // Aynı isimde kullanıcı ekleme sonucu kullanıcı adının değişen text rengini defaultlayan event
        et_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                et_username.setTextColor(getResources().getColor(R.color.accent));
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        // Aynı mail ile kayıt yapılırsa emailin değişen text rengini defaultlayan event
        et_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                et_email.setTextColor(getResources().getColor(R.color.accent));
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        // Kayıt butonun listener eventi
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = et_username.getText().toString();
                passwd = et_pass.getText().toString();
                repasswd = et_rePass.getText().toString();
                email = et_email.getText().toString();
                boolean result = Controller.regControl(context, username, passwd, repasswd, email);
                if(result) {
                    //Veritabanına kullanıcı kaydı yapılıyor.
                    final Database database = new Database(context);
                    boolean dbResult = database.addUser(username, passwd, email);

                    if(!dbResult) {
                        //Ekrana bilgi yazısı bastırılıyor
                        Snackbar.make(view, "Kullanıcı Kaydı Başarılı", 60000)
                                .setAction("GİRİŞ YAP", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent back = new Intent(context, Login.class);
                                        startActivity(back);
                                        finish();
                                    }
                                }).show();
                    }else {
                        //Ekrana bilgi yazısı bastırılıyor
                        Snackbar.make(view, "Böyle Bir Kullanıcı Mevcut ! ", 30000)
                                .setAction("TAMAM", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(database.isFlag2())
                                            et_email.setTextColor(Color.BLUE);
                                        else if(database.isFlag())
                                            et_username.setTextColor(Color.BLUE);
                                    }
                                }).show();
                    }
                }
            }
        });
    }
}
