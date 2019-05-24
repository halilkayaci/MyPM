package com.info;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyHolder> {

    private Context context;
    private List<Projects> data;
    private EditText projectName;
    private String username;

    // Sınıf contructor
    public RecyclerAdapter(Context context, List<Projects> data, String username) {
        this.context = context;
        this.data = data;
        this.username = username;
    }

    // Menu tasarımı ekleniyor
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cardview_management, viewGroup, false);

        return new MyHolder(view);
    }

    // Menu itemlerının eventleri
    @Override
    public void onBindViewHolder(@NonNull final MyHolder myHolder, int i) {
        myHolder.tv_projectName.setText(data.get(i).getProjectName());
        myHolder.card_design.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent next = new Intent(context, ProjectActivity.class);
                next.putExtra("Title", myHolder.tv_projectName.getText().toString());
                next.putExtra("User", username);
                context.startActivity(next);
                ((Activity)context).finish();

            }
        });
        myHolder.card_design.setAnimation(
                AnimationUtils.loadAnimation(
                        context,
                        R.anim.cardanim
                ));

        //Cardview nesnesine uzun süre basılınca popmenu açılıyor.
        myHolder.card_design.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                PopupMenu menu = new PopupMenu(context, myHolder.img_more);
                menu.getMenuInflater().inflate(R.menu.menu_more, menu.getMenu());
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.action_open:
                                // Proje sayfası açılyor
                                Intent next = new Intent(context.getApplicationContext(), ProjectActivity.class);
                                next.putExtra("Title", myHolder.tv_projectName.getText().toString());
                                next.putExtra("User", username);
                                context.startActivity(next);
                                ((Activity)context).finish();
                                return true;

                            case R.id.action_edit:
                                // popup menu açılıp proje adı düzenleniyor ve veritabanı update işlemi gerçekleştiriliyor
                                final View view1 = View.inflate(context, R.layout.alert_project_name, null);
                                projectName = view1.findViewById(R.id.et_projectName);
                                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                                dialog.setMessage("Proje Düzenle");
                                projectName.setText(myHolder.tv_projectName.getText());
                                dialog.setView(view1);
                                dialog.setPositiveButton("DÜZENLE", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Database db = new Database(context);
                                        if(TextUtils.isEmpty(projectName.getText().toString()))
                                            Toast.makeText(context, "Proje İsmi Boş Bırakılamaz !", Toast.LENGTH_LONG).show();
                                        else{
                                            boolean result = db.updateProject(username, myHolder.tv_projectName.getText().toString().trim(), projectName.getText().toString().trim());
                                            if (result) {
                                                Toast.makeText(context, "Aynı İsimde Bir Proje Zaten Mevcut !", Toast.LENGTH_LONG).show();
                                            }
                                            else ((MainActivity)context).showInfos();
                                        }
                                    }
                                });

                                dialog.setNegativeButton("İPTAL", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) { }
                                });
                                dialog.create().show();
                                return true;

                            case R.id.action_delete:
                                // Proje veritabanından ve ekrandan siliniyor
                                Database db = new Database(context);
                                db.deleteProject(username, myHolder.tv_projectName.getText().toString());
                                ((MainActivity)context).showInfos(); //// Sorun çözüldü :):):):)::):))::)
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                menu.show();
                return true;
            }
        });

        // Cardview üzerindeki 3 nokta simgesinin tıklama eventi.
        myHolder.img_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PopupMenu menu = new PopupMenu(context, myHolder.img_more);
                menu.getMenuInflater().inflate(R.menu.menu_more, menu.getMenu());
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.action_open:
                                // Proje sayfası açılyor
                                Intent next = new Intent(context.getApplicationContext(), ProjectActivity.class);
                                next.putExtra("Title", myHolder.tv_projectName.getText().toString());
                                next.putExtra("User", username);
                                context.startActivity(next);
                                ((Activity)context).finish();
                                return true;

                            case R.id.action_edit:
                                // popup menu açılıp proje adı düzenleniyor ve veritabanı update işlemi gerçekleştiriliyor
                                View view1 = View.inflate(context, R.layout.alert_project_name, null);
                                projectName = view1.findViewById(R.id.et_projectName);
                                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                                dialog.setMessage("Proje Düzenle");
                                projectName.setText(myHolder.tv_projectName.getText());
                                dialog.setView(view1);
                                dialog.setPositiveButton("DÜZENLE", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Database db = new Database(context);
                                        if(TextUtils.isEmpty(projectName.getText().toString()))
                                            Toast.makeText(context, "Proje İsmi Boş Bırakılamaz !", Toast.LENGTH_LONG).show();
                                        else{
                                            boolean result = db.updateProject(username, myHolder.tv_projectName.getText().toString(), projectName.getText().toString());
                                            if (result) {
                                                Toast.makeText(context, "Aynı İsimde Bir Proje Zaten Mevcut !", Toast.LENGTH_LONG).show();
                                            }
                                            else ((MainActivity)context).showInfos();
                                        }
                                    }
                                });

                                dialog.setNegativeButton("İPTAL", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) { }
                                });
                                dialog.create().show();
                                return true;

                            case R.id.action_delete:
                                // Proje veritabanından ve ekrandan siliniyor
                                Database db = new Database(context);
                                db.deleteProject(username, myHolder.tv_projectName.getText().toString());
                                ((MainActivity)context).showInfos(); //// Sorun çözüldü :):):):)::):))::)
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                menu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    // Cardviewların tutucu sınıfı. Cardview nesneleri bağlanıyor.
    public static class MyHolder extends RecyclerView.ViewHolder{
        TextView tv_projectName;
        ImageView img_more;
        CardView card_design;

        public MyHolder(View itemView){
            super(itemView);
            tv_projectName = itemView.findViewById(R.id.tv_projectName);
            img_more = itemView.findViewById(R.id.img_more);
            card_design = itemView.findViewById(R.id.cardDesign);
        }
    }
}
