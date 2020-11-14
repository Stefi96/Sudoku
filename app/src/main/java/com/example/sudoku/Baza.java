package com.example.sudoku;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;

public class Baza extends SQLiteOpenHelper {

    private static final String DB_NAME = "sudoku.db";
    private static final int DB_VERSION = 1;

    public Baza(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE polja (x INTEGER , y INTEGER , broj TEXT, fixed INTEGER, PRIMARY KEY (x,y) )";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS beleske");
        this.onCreate(db);
    }

    public void dodajPolje(Polje polje){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("x", polje.getX());
        values.put("y", polje.getY());
        values.put("broj", polje.getBroj());
        values.put("fixed", polje.isFixed());
        Log.e("fixed insert",polje.isFixed()+"");
        db.insert("polja", null, values);

        db.close();
    }

    public LinkedList<Polje> ucitajPolja() {

        LinkedList<Polje> lista = new LinkedList<>();

        String query = "SELECT  * FROM polja";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Polje polje = null;

        while (cursor.moveToNext()) {
            polje = new Polje();
            polje.setX(cursor.getInt(0));
            polje.setY(cursor.getInt(1));
            polje.setBroj(cursor.getString(2));
            int fixed = cursor.getInt(3);
            polje.setFixed(fixed == 1);
            lista.add(polje);

        }
        return lista;
    }

    public void clear(){

        String query = "DELETE FROM polja";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
    }
}
