package com.info;

import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

public class Controller {

    //Login işlemlerinde kontrol sağlayan metod
    public static boolean loginControl(Context context, EditText username, EditText password) {
        if (TextUtils.isEmpty(username.getText()) || TextUtils.isEmpty(password.getText())) {
            Toast.makeText(context, "Bilgileri eksiksiz olarak giriniz !", Toast.LENGTH_SHORT).show();
            return false;
        } else return true;
    }

    public static boolean regControl(Context context, String username, String pass, String repass, String email) {

        //Fonksiyona gonderilen parametrelerin bos olup olmadigi kontrol ediliyor
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(repass) || TextUtils.isEmpty(email)) {
            Toast.makeText(context, "Tüm bilgileri eksiksiz olarak doldurunuz !", Toast.LENGTH_SHORT).show();
            return false;
        }

        //Sifrelerin eslesip eslesmedigi kontrol ediliyor
        else if (!pass.matches(repass)) {
            Toast.makeText(context, "Şifreler Eşleşmiyor !", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Email kontrol ediliyor
        else if (!isValidEmail(email)) {
            Toast.makeText(context, "Geçersiz Email formatı !", Toast.LENGTH_SHORT).show();
            return false;
        } else return true;
    }

    // Email formatı kontrol eden metod
    private static boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean resetPassword(Context context, String pass, String rePass) {
        //Fonksiyona gonderilen parametrelerin bos olup olmadigi kontrol ediliyor
        if (pass.isEmpty() || rePass.isEmpty()) {
            Toast.makeText(context, "Bilgileri eksiksiz olarak doldurunuz !", Toast.LENGTH_SHORT).show();
            return false;
        }

        //Sifrelerin eslesip eslesmedigi kontrol ediliyor
        else if (!pass.matches(rePass)) {
            Toast.makeText(context, "Şifreler Eşleşmiyor !", Toast.LENGTH_SHORT).show();
            return false;
        } else return true;
    }
}