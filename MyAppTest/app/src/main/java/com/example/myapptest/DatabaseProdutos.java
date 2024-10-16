package com.example.myapptest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseProdutos extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "produtos.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "produtos";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NOME = "nome";
    private static final String COLUMN_QUANTIDADE = "quantidade";
    private static final String COLUMN_STATUS_PRODUTO = "statusProduto";

    public DatabaseProdutos(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NOME + " TEXT, " +
                COLUMN_QUANTIDADE + " INTEGER, " +
                COLUMN_STATUS_PRODUTO + " BOOLEAN)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Create
    public void addProduto(String nome, int quantidade, boolean statusProduto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOME, nome);
        values.put(COLUMN_QUANTIDADE, quantidade);
        values.put(COLUMN_STATUS_PRODUTO, statusProduto);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // Read
    public List<Produto> getAllProdutos() {
        List<Produto> productsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String nome = cursor.getString(1);
                int quantidade = cursor.getInt(2); // Correção para quantidade
                boolean statusProduto = cursor.getInt(3) == 1; // Verifica se o valor é 1 (verdadeiro)

                productsList.add(new Produto(id, nome, quantidade, statusProduto));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return productsList;
    }


    // Update
    public void updateUser(int id, String nome, int quantidade, boolean statusProduto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOME, nome);
        values.put(COLUMN_QUANTIDADE, quantidade);
        values.put(COLUMN_STATUS_PRODUTO, statusProduto);
        db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Delete
    public void deleteUser(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}
