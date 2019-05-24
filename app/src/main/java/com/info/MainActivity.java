package com.info;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private List<Projects> projects;
    protected Toolbar toolbar;
    private String username;
    private Context context = this;
    private EditText projectName;
    private boolean exitFlag = false;
    protected TextView tv_username, tv_projectsCount;
    protected FloatingActionButton fab_add;

    @Override
    public void onBackPressed() {

        // Back tuşu ile uygulamadan çıkışın kontrolü yapılıyor
        if(exitFlag) {
            super.onBackPressed();
            return;
        }
        Toast.makeText(this, "Uygulamadan Çıkmak İçin Tekrar Basın !", Toast.LENGTH_SHORT).show();
        exitFlag = true;

        // Kullanıcı üç saniye içerinde yeniden back tuşuna basmazsa uygulamadan çıkış engelleniyor
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                exitFlag = false;
            }
        }, 3000);
    }

    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar nesnesi bağlanıyor ve gösteriliyor
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Projelerim");
        toolbar.setTitleTextColor(getResources().getColor(R.color.primary_text));
        setSupportActionBar(toolbar);

        // Login ekranından gönderilen login olan kullanıcının username'i alınıyor.
        username = getIntent().getStringExtra("Login User"); // bundle.getString("Login User");
        showInfos();

        // Ekle butonunun eventi
        fab_add = findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View desing = getLayoutInflater().inflate(R.layout.alert_project_name, null);
                projectName = desing.findViewById(R.id.et_projectName);
                final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setMessage("Proje Oluştur");
                dialog.setView(desing);
                dialog.setPositiveButton("OLUŞTUR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(TextUtils.isEmpty(projectName.getText().toString()))
                            Toast.makeText(context, "Proje İsmi Boş Bırakılamaz !", Toast.LENGTH_LONG).show();
                        else{
                            Database db = new Database(context);
                            boolean result = db.addProject(username, projectName.getText().toString().trim());
                            if (!result)
                                showInfos();
                            else
                                Toast.makeText(context, "Aynı İsimde Bir Proje Zaten Mevcut !", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                dialog.setNegativeButton("İPTAL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.create().show();
            }
        });
    }

    // Cardviewler gösteriliyor
    public void showInfos(){
        //Kullanıcı adına göre projeler tablosundan veriler çekilerek bir ArrayListe aktarılıyor.
        projects = new ArrayList<>();
        Database db = new Database(context);
        projects = db.getList(username);
        // Recyclerview nesenesi bağlanıyor. Adapter sınıfından bir nesne ayağa kaldırılıyor.ArrayList recyclerview'e aktarılıtor
        RecyclerView recyclerView = findViewById(R.id.recyv_project);
        RecyclerAdapter adapter = new RecyclerAdapter(this, projects, username);
        recyclerView.setAdapter(adapter);
        // Recyclerview grid layout ve her satırda 2 cardview olacak şekilde ayarlanıyor.
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
    }

     //Toolbar menüleri ekleniyor
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // menu tasarımı ekleniyor
        getMenuInflater().inflate(R.menu.menu_tool, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)item.getActionView();
        searchView.setOnQueryTextListener(MainActivity.this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

             // Toolbar üzerindeki logout butonuna tıklanma durumunu yakalayıp oturumu sonlandıran event.
            case R.id.action_logout:
                deleteRemember();
                Intent back = new Intent(context, Login.class);
                startActivity(back);
                finish();
                return true;

            // Toolbar üzerindeki hesap bilgileri butonu tıklandığında pop up menu açılarak hesap bilgileri listeleniyor.
            case R.id.action_infos:
                final View designInfo = getLayoutInflater().inflate(R.layout.alert_user_info, null);
                tv_username = designInfo.findViewById(R.id.tv_username);
                tv_username.setText(username);

                tv_projectsCount = designInfo.findViewById(R.id.tv_projectsCount);
                tv_projectsCount.setText(String.valueOf(projects.size()));

                AlertDialog.Builder dialogInfo = new AlertDialog.Builder(context);
                dialogInfo.setMessage("Hesap Bilgileri");
                dialogInfo.setView(designInfo);
                dialogInfo.setPositiveButton("HESABI SİL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        AlertDialog.Builder dialogInfo = new AlertDialog.Builder(context);
                        dialogInfo.setMessage("Hesabı silmek istediğinizden emin misiniz ? Bu işlem geri alınamaz !");
                        dialogInfo.setPositiveButton("EVET", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Kullanıcı adına göre hesap silinip uygulama login ekranına  geri dönüyor
                                Database db = new Database(context);
                                db.deleteAccount(username);
                                deleteRemember();
                                startActivity(new Intent(context, Login.class));
                                finish();
                            }
                        });
                        dialogInfo.setNegativeButton("HAYIR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        dialogInfo.create().show();
                    }
                });

                dialogInfo.setNegativeButton("İPTAL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialogInfo.create().show();
                return true;

            default:
                return false;
        }
    }

    // Logout olan kullanıcının beni hatırla özelliği kaldırılıyor.
    private void deleteRemember(){
        SharedPreferences preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("username");
        editor.remove("password");
        editor.apply();
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        searchResult(s);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        searchResult(s);
        return false;
    }

    private void searchResult(String s){
        Database database = new Database(context);
        projects = database.searchProject(username, s);
        RecyclerView recyclerView = findViewById(R.id.recyv_project);
        RecyclerAdapter adapter = new RecyclerAdapter(this, projects, username);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
    }
}