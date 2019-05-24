package com.info;



import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class Login extends AppCompatActivity {
    private EditText et_username, et_password, et_email, et_confirmPassword, et_confirmRPassword;
    protected TextView tv_create, tv_forgoPass;
    protected Button btn_login;
    private Context context = this;
    private CheckBox cbox_rememberMe;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Widgetler nesnelere bağlanıyor
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        cbox_rememberMe = findViewById(R.id.cbox_rememberMe);
        tv_create = findViewById(R.id.tv_create);
        tv_forgoPass = findViewById(R.id.tv_forgotPass);
        tv_forgoPass.setTextColor(getResources().getColor(R.color.secondary_text));
        btn_login = findViewById(R.id.btn_login);

        // Beni hatırla seçimi yapmış kullanıcı var ise login ekranı geçiliyor
        preferences =  getSharedPreferences("UserInfo", MODE_PRIVATE);
        final String username = preferences.getString("username", "user not found");
        String password = preferences.getString("password", "password not found");
        Database database = new Database(context);
        boolean state = database.loginUser(username, password);
        if(state) {
            Intent next = new Intent(context, MainActivity.class);
            next.putExtra("Login User", username);
            startActivity(next);
            finish();
        }

        // Butonun listener eventi
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = Controller.loginControl(context, et_username, et_password);
                if(result){
                    //Veritabanı kulanıcı tablosundan veriler çekilerek kontrolü yapılıyor.
                    Database database = new Database(context);
                    boolean resultDb = database.loginUser(et_username.getText().toString(), et_password.getText().toString());
                    if(resultDb){
                        // Kullanıcının bir sonraki oturumda hatırlanıp hatırlanmayacağı kontrol ediliyor ve gerekli işlemler yapılıyor.
                        rememberMe(et_username.getText().toString(), et_password.getText().toString());
                        //Kullanıcı bulunursa sonraki ekrana geçiş yapılıyor.
                        Intent next = new Intent(context, MainActivity.class);
                        next.putExtra("Login User", et_username.getText().toString());
                        startActivity(next);
                        finish();
                    }else{
                        Snackbar.make(view, "Kullanıcı Bulunamadı !", 30000)
                                .setAction("TAMAM", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        et_username.setText("");
                                        et_password.setText("");
                                    }
                                }).show();
                    }
                }
            }
        });

        // Textview click eventi. Texte tıklanınca kayıt ekranı açılıyor.
        tv_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent next = new Intent(Login.this, Register.class);
                startActivity(next);
                et_username.setText("");
                et_password.setText("");
            }
        });

        // Şifremi unuttum ekranı
        tv_forgoPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View design = getLayoutInflater().inflate(R.layout.alert_reset_password, null);
                et_email = design.findViewById(R.id.et_resetEmail);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Şifre Sıfırlama");
                builder.setView(design);
                builder.setPositiveButton("SIFIRLA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                      final String email = et_email.getText().toString().trim();
                      if(!email.isEmpty()) {
                          final Database database = new Database(context);
                          boolean state = database.resetPass(email);
                          if(state){
                            // Email adresi doğruysa yeni bir alertview ile şifre güncelleme yapılıyor.
                            final View design2 = getLayoutInflater().inflate(R.layout.alert_confirm_password, null);
                            et_confirmRPassword = design2.findViewById(R.id.et_resetRPass);
                            et_confirmPassword = design2.findViewById(R.id.et_resetPass);

                            AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                            builder2.setMessage("Şifre Sıfırlama");
                            builder2.setView(design2);
                            builder2.setPositiveButton("OLUŞTUR", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                final String rePass = et_confirmRPassword.getText().toString();
                                final String pass = et_confirmPassword.getText().toString();
                                boolean res = Controller.resetPassword(context, pass, rePass);
                                    if(res){
                                        database.updatePass(email, pass );
                                        Toast.makeText(context,"Şifre Güncellendi !", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                            builder2.setNegativeButton("VAZGEÇ", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });

                            builder2.create().show();
                          }
                          else Toast.makeText(context,"Bu epostaya ait bir hesap bulunamadı !", Toast.LENGTH_LONG).show();
                      }
                    }
                });
                builder.setNegativeButton("İPTAL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) { }
                });

                builder.create().show();
            }
        });
    }

    private void rememberMe(String username, String pass){
        if(cbox_rememberMe.isChecked()) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("username", username);
            editor.putString("password", pass);
            editor.apply();
        }
    }
}
