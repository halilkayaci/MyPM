package com.info;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProjectActivity extends AppCompatActivity {
    protected Toolbar toolbar;
    private String projectName;
    private String username;
    private Context context = this;
    protected TextView tv_projectName;
    protected ImageView img_back;
    protected CheckBox step1, step2, step3, step4, step5, step6;
    private int [] stateStep = new int[6];
    protected TextView tv_info;

    // Back butonu override ediliyor.
    @Override
    public void onBackPressed() {
        Intent back = new Intent(context, MainActivity.class);
        back.putExtra("Login User", username);
        startActivity(back);
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        // Projects ekranından gönderilen proje adı alınıyor ve toolbarda gösteriliyor.
        projectName = getIntent().getStringExtra("Title");
        username = getIntent().getStringExtra("User");
        toolbar = findViewById(R.id.toolbar_project);
        toolbar.setTitle("");
        tv_projectName = findViewById(R.id.tv_project);
        tv_projectName.setText(projectName);
        setSupportActionBar(toolbar);

        // Geri butonunun eventi
        img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(context, MainActivity.class);
                back.putExtra("Login User", username);
                startActivity(back);
                finish();
            }
        });

        tv_info = findViewById(R.id.tv_info);

        //CheckBox neseneleri bağlanıyor
        step1 = findViewById(R.id.cbox_step1);
        step2 = findViewById(R.id.cbox_step2);
        step3 = findViewById(R.id.cbox_step3);
        step4 = findViewById(R.id.cbox_step4);
        step5 = findViewById(R.id.cbox_step5);
        step6 = findViewById(R.id.cbox_step6);

        // checkboxların listener eventleri
        step1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) stateStep[0] = 1; else stateStep[0] = 0;
            }
        });
        step2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) stateStep[1] = 1; else stateStep[1] = 0;
            }
        });
        step3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) stateStep[2] = 1; else stateStep[2] = 0;
            }
        });
        step4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) stateStep[3] = 1; else stateStep[3] = 0;
            }
        });
        step5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b) stateStep[4] = 1; else stateStep[4] = 0;
            }
        });
        step6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b) stateStep[5] = 1; else stateStep[5] = 0;
            }
        });

        // Veritabanından proje adımlarının check listesi alınıyor
        int [] returnCheck;
        Database database = new Database(context);
        returnCheck = database.stateCheck(username, projectName);

        // Veri tabanından gelen dizi durumuna göre checboxlar işaretleniyor
        if(returnCheck[0] == 0) step1.setChecked(false);
        else step1.setChecked(true);
        if(returnCheck[1] == 0) step2.setChecked(false);
        else step2.setChecked(true);
        if(returnCheck[2] == 0) step3.setChecked(false);
        else step3.setChecked(true);
        if(returnCheck[3] == 0) step4.setChecked(false);
        else step4.setChecked(true);
        if(returnCheck[4] == 0) step5.setChecked(false);
        else step5.setChecked(true);
        if(returnCheck[5] == 0) step6.setChecked(false);
        else step6.setChecked(true);
    }


    // Menu tasarımı bağlanıyor
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_project, menu);
        return true;
    }

    //Menu üzerindeki butonların click eventleri
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                //Veri tabanı güncellemesi yapılan event
                Database database = new Database(context);
                boolean result = database.updateSteps(username, projectName, stateStep[0], stateStep[1], stateStep[2], stateStep[3], stateStep[4], stateStep[5]);
                if(result) Toast.makeText(context, "Kayıt işlemi başarılı..." , Toast.LENGTH_SHORT).show();
                else Toast.makeText(context, "Kayıt işlemi başarısız !" , Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
