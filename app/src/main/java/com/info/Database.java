package com.info;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {
    // Veritabanı özellikleri tablo isimleri  oluşturuluyor.
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "MyPM";

    private static final String DB_TABLE_NAME = "Kullanicilar";
    private static final String DB_USER_ID = "Kullanici_Id";
    private static final String DB_USER_NAME = "Kullanici_Adi";
    private static final String DB_USER_PASSWORD = "Kullanici_Sifre";
    private static final String DB_USER_MAIL= "Kullanici_Mail";

    private static final String DB_TABLE_NAME2 = "Projeler";
    private static final String DB_PROJECT_ID = "Proje_Id";
    private static final String DB_PROJECT_NAME= "Proje_Adi";
    private static final String DB_PROJECT_USER = "Proje_Sahibi";
    private static final String DB_PROJECT_STEP1 = "Planlama_Asamasi";
    private static final String DB_PROJECT_STEP2 = "Analiz_Asamasi";
    private static final String DB_PROJECT_STEP3 = "Tasarim_Asamasi";
    private static final String DB_PROJECT_STEP4 = "Gercekleme_Asamasi";
    private static final String DB_PROJECT_STEP5 = "Modul_Test_Asamasi";
    private static final String DB_PROJECT_STEP6 = "Sistem_Test_Asamasi";

    // Kontrol için durum bayrakları
    private boolean flag = false;
    private boolean flag2 = false;

    boolean isFlag() {
        return flag;
    }

    boolean isFlag2() {
        return flag2;
    }

    // Veritabanının kolonları oluşturuluyor.
    Database(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = " CREATE TABLE " + DB_TABLE_NAME + "("
                + DB_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DB_USER_NAME +" TEXT, "
                + DB_USER_PASSWORD +" TEXT, "
                + DB_USER_MAIL + " TEXT "
                + ")";
        sqLiteDatabase.execSQL(CREATE_TABLE);

        String CREATE_TABLE2 = " CREATE TABLE " + DB_TABLE_NAME2 + "("
                + DB_PROJECT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DB_PROJECT_USER + " TEXT, "
                + DB_PROJECT_NAME + " TEXT, "
                + DB_PROJECT_STEP1 + " INTEGER, "
                + DB_PROJECT_STEP2 + " INTEGER, "
                + DB_PROJECT_STEP3 + " INTEGER, "
                + DB_PROJECT_STEP4 + " INTEGER, "
                + DB_PROJECT_STEP5 + " INTEGER, "
                + DB_PROJECT_STEP6 + " INTEGER "
                + ")";
        sqLiteDatabase.execSQL(CREATE_TABLE2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(" DROP TABLE IF EXISTS " + DB_TABLE_NAME + DB_TABLE_NAME2);
        onCreate(sqLiteDatabase);
    }

    private SQLiteDatabase dbRead = getReadableDatabase();
    private SQLiteDatabase db = getWritableDatabase();
    private Cursor cursorUser = dbRead.rawQuery("SELECT * FROM Kullanicilar", null);
    private Cursor cursorProject = dbRead.rawQuery("SELECT * FROM Projeler", null);

    // Aynı kullanıcı adı veya varolan bir email ile kayıt olmayı kontrol edip herşey normalse veritabanına kullanıcı ekleyen metod.
    boolean addUser(String username, String password, String email) {
        while (cursorUser.moveToNext()){
            String user = cursorUser.getString(1);
            String mail = cursorUser.getString(3);
            if(user.matches(username)){
                flag = true;
                break;
            }
            else if(mail.matches(email)){
                flag2 = true;
                break;
            }
        }
        cursorUser.close();
        if(!flag && !flag2){
            ContentValues contentValues = new ContentValues();
            contentValues.put(DB_USER_NAME, username);
            contentValues.put(DB_USER_PASSWORD, password);
            contentValues.put(DB_USER_MAIL, email);

            db.insert(DB_TABLE_NAME, null, contentValues);
            db.close();
        }
        // Her iki kontrol bayrağından birisi  kaldırılmış ise(True) metod true return ediyor
        // Bir sorun yok ise metod false return ediyor.
        return flag || flag2;
    }

    // Projeleri veritabanına ekleyen metod
    boolean addProject(String userName, String projectName){
        while(cursorProject.moveToNext()){
            String user = cursorProject.getString(1);
            String project = cursorProject.getString(2);
            if(user.matches(userName) && project.matches(projectName)){
                flag = true;
                break;
            }
        }
        cursorProject.close();
        if(!flag){
            ContentValues values = new ContentValues();
            values.put(DB_PROJECT_USER, userName);
            values.put(DB_PROJECT_NAME, projectName);
            values.put(DB_PROJECT_STEP1, 0);
            values.put(DB_PROJECT_STEP2, 0);
            values.put(DB_PROJECT_STEP3, 0);
            values.put(DB_PROJECT_STEP4, 0);
            values.put(DB_PROJECT_STEP5, 0);
            values.put(DB_PROJECT_STEP6, 0);

            db.insert(DB_TABLE_NAME2, null, values);
            db.close();
        }
        return flag;
    }

    // Projeleri cardview için arrayList olarak return eden metod
    List<Projects> getList(String username){
        ArrayList<Projects> projects = new ArrayList<>();
        while (cursorProject.moveToNext()){
           String user = cursorProject.getString(1);
           if(user.matches(username)) {
               String projectName = cursorProject.getString(2);
               projects.add(new Projects(projectName));
           }
        }
        cursorProject.close();
        dbRead.close();
        return projects;
    }

    // Login olmak isteyen kullanıcı bilgilerini veritabanından çekip kullanıcın var olup olmadığını döndüren metod.
    boolean loginUser(String username, String passwd) {
        while (cursorUser.moveToNext()) {
            String user = cursorUser.getString(1);
            String pswd = cursorUser.getString(2);
            if (user.matches(username) && pswd.matches(passwd)) {
                flag = true;
                break;
            }
        }
        cursorUser.close();
        dbRead.close();
        return flag;
    }

    // Veritabını proje adı güncellemesi yapan metod
    boolean updateProject(String username, String oldName, String newName){
        while (cursorProject.moveToNext()){
            String user = cursorProject.getString(1);
            String project = cursorProject.getString(2);
            if (project.matches(newName) && user.matches(username)) {
                flag = true;
                break;
            }
        }
        if(!flag){
            ContentValues values = new ContentValues();
            values.put(DB_PROJECT_NAME, newName);
            db.update(DB_TABLE_NAME2, values, "Proje_Adi = ? and Proje_Sahibi = ? " , new String[] {oldName, username});
        }
        cursorProject.close();
        dbRead.close();
        db.close();
        return flag;
    }

    // Veritabanı silme işlemi
    void deleteProject(String userName, String projectName){
        dbRead.delete(DB_TABLE_NAME2,  DB_PROJECT_NAME + "= ? and " + DB_PROJECT_USER + "= ?",
                new String[] {projectName, userName});
        dbRead.close();
    }

    // Proje adımlarını veritabanında güncelleyen metod
    boolean  updateSteps(String username, String projectName, int step1, int step2, int step3, int step4, int step5, int step6){
        try {
            ContentValues values = new ContentValues();
            values.put(DB_PROJECT_STEP1, step1);
            values.put(DB_PROJECT_STEP2, step2);
            values.put(DB_PROJECT_STEP3, step3);
            values.put(DB_PROJECT_STEP4, step4);
            values.put(DB_PROJECT_STEP5, step5);
            values.put(DB_PROJECT_STEP6, step6);
            db.update(DB_TABLE_NAME2, values, DB_PROJECT_NAME + "= ? and " + DB_PROJECT_USER + "= ?",
                    new String[]{projectName, username});
            db.close();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    // Proje adımlarının durumunu veritabanından çekip bir dizi return eden metod
    int[] stateCheck (String username, String projectName){
        int [] array = new int[6];
        while (cursorProject.moveToNext()){
            String user = cursorProject.getString(1);
            String project = cursorProject.getString(2);
            if(user.equals(username) && project.equals(projectName)){
                array[0] = cursorProject.getInt(3);
                array[1] = cursorProject.getInt(4);
                array[2] = cursorProject.getInt(5);
                array[3] = cursorProject.getInt(6);
                array[4] = cursorProject.getInt(7);
                array[5] = cursorProject.getInt(8);
                break;
            }
        }
        dbRead.close();
        cursorProject.close();
        return array;
    }

    // Şifre sıfırlama için gönderilen epostanın veritabanında olup olmadığını kontrol eden metod.
    boolean resetPass(String email) {
        while (cursorUser.moveToNext()) {
            String mail = cursorUser.getString(3);
            if (mail.matches(email)) {
                flag = true;
                break;
            }
        }
        cursorUser.close();
        return flag;
    }

    // Kullanıcı şifresini güncelleyen metod.
    void updatePass(String email, String newPass){
        ContentValues values = new ContentValues();
        values.put(DB_USER_PASSWORD, newPass);
        db.update(DB_TABLE_NAME, values, DB_USER_MAIL + "= ?",
                new String[] {email});
        db.close();
    }

    // Kullanıcı adına göre hesap silen metod
    void deleteAccount(String username){
        dbRead.delete(DB_TABLE_NAME,  DB_USER_NAME + "= ? ",
                new String[] {username});
        dbRead.delete(DB_TABLE_NAME2,  DB_PROJECT_USER + "= ? ",
                new String[] {username});
        dbRead.close();
    }

    // Projeler tablosundan arama yapan metod
    List<Projects> searchProject(String username, String search){

            ArrayList<Projects> projects = new ArrayList<>();
            Cursor cursorSearch = db.rawQuery("SELECT * FROM Projeler WHERE Proje_Sahibi = ? and Proje_Adi like '%"+search+"%'", new String[]{username});
            while (cursorSearch.moveToNext()){
                String projectName = cursorSearch.getString(2);
                projects.add(new Projects(projectName));

            }
            cursorSearch.close();
            db.close();
            return projects;
    }
}
