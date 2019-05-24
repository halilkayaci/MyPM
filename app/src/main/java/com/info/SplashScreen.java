package com.info;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {
    protected ImageView imgSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // ImageView nesnesi bağlanıyor.
        imgSplash = findViewById(R.id.img_splash);

        // Animotion sınıfından bir nesne ayağa kaldırılarak animasyon bağlanıyor.
        Animation animation = AnimationUtils.loadAnimation(this,  R.anim.animation);
        imgSplash.startAnimation(animation);

        // Handler ile bir interface oluşturularak Intent nesnesi ile activityler arası geçiş sağlanıyor. 2,5 saniye bekleme süresi ile.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent next = new Intent(SplashScreen.this,  Login.class);
                startActivity(next);
                finish();
            }
        },  2500);
    }
}
