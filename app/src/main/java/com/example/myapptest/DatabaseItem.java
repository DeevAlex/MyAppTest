package com.example.myapptest;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseItem extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "itemsDistribuicao.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_ITEMS = "items";
    private static final String TABLE_CENTRO_DISTRIBUICAO = "centro_distribuicao";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NOME = "nome";
    private static final String COLUMN_QUANTIDADE = "quantidade";
    private static final String COLUMN_CENTRO_ID = "centro_id";  // Chave estrangeira

    private static final String COLUMN_CENTRO_ID_REF = "id";  // Chave primária
    private static final String COLUMN_CENTRO_NOME = "nome";  // Nome do centro

    public DatabaseItem(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public String getNomeDatabase(String nome) {
        return DATABASE_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableCentro = "CREATE TABLE " + TABLE_CENTRO_DISTRIBUICAO + " (" +
                COLUMN_CENTRO_ID_REF + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CENTRO_NOME + " TEXT UNIQUE)";
        db.execSQL(createTableCentro);

        ContentValues guarulhos = new ContentValues();
        guarulhos.put(COLUMN_CENTRO_NOME, "Guarulhos");

        ContentValues saoPaulo = new ContentValues();
        saoPaulo.put(COLUMN_CENTRO_NOME, "São Paulo");

        db.insert(TABLE_CENTRO_DISTRIBUICAO, null, guarulhos);
        db.insert(TABLE_CENTRO_DISTRIBUICAO, null, saoPaulo);

        String createTableItems = "CREATE TABLE " + TABLE_ITEMS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NOME + " TEXT, " +
                COLUMN_QUANTIDADE + " INTEGER, " +
                COLUMN_CENTRO_ID + " INTEGER, " +
                "FOREIGN KEY (" + COLUMN_CENTRO_ID + ") REFERENCES " + TABLE_CENTRO_DISTRIBUICAO + "(" + COLUMN_CENTRO_ID_REF + "))";
        db.execSQL(createTableItems);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CENTRO_DISTRIBUICAO);
        onCreate(db);
    }

    public String getCentroNomeById(int centroId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String nomeCentro = "";

        Cursor cursor = db.rawQuery("SELECT nome FROM centro_distribuicao WHERE id = ?", new String[]{String.valueOf(centroId)});
        if (cursor.moveToFirst()) {
            nomeCentro = cursor.getString(0);
        }

        cursor.close();
        db.close();
        return nomeCentro;
    }

    public int retirarItem(int centroId, int produtoId, int quantidade) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT quantidade FROM items WHERE id = ? AND centro_id = ?",
                    new String[]{String.valueOf(produtoId), String.valueOf(centroId)});

            if (cursor.moveToFirst()) {
                int quantidadeDisponivel = cursor.getInt(0);

                if (quantidadeDisponivel >= quantidade) {
                    int novaQuantidade = quantidadeDisponivel - quantidade;
                    ContentValues values = new ContentValues();
                    values.put("quantidade", novaQuantidade);

                    if (novaQuantidade > 0) {
                        db.update("items", values, "id = ? AND centro_id = ?",
                                new String[]{String.valueOf(produtoId), String.valueOf(centroId)});

                    } else {
                        db.delete("items", "id = ? AND centro_id = ?",
                                new String[]{String.valueOf(produtoId), String.valueOf(centroId)});

                    }

                    return 200;
                } else {
                    return -1;
                }
            } else {
                return 404;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 400;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }


    public int transferirItem(int itemId, int quantidade, int centroOrigemId, int centroDestinoId) {
        SQLiteDatabase db = null;
        Cursor cursorOrigem = null;
        Cursor cursorDestino = null;

        try {
            db = this.getWritableDatabase();

            cursorOrigem = db.rawQuery("SELECT quantidade FROM items WHERE id = ? AND centro_id = ?",
                    new String[]{String.valueOf(itemId), String.valueOf(centroOrigemId)});

            if (cursorOrigem.moveToFirst()) {
                int quantidadeDisponivel = cursorOrigem.getInt(0);

                if (quantidadeDisponivel >= quantidade) {
                    cursorDestino = db.rawQuery("SELECT quantidade FROM items WHERE id = ? AND centro_id = ?",
                            new String[]{String.valueOf(itemId), String.valueOf(centroDestinoId)});

                    if (cursorDestino.moveToFirst()) {
                        int quantidadeDestino = cursorDestino.getInt(0);
                        int novaQuantidadeDestino = quantidadeDestino + quantidade;

                        ContentValues valuesDestino = new ContentValues();
                        valuesDestino.put("quantidade", novaQuantidadeDestino);

                        db.update(TABLE_ITEMS, valuesDestino, "id = ? AND centro_id = ?",
                                new String[]{String.valueOf(itemId), String.valueOf(centroDestinoId)});
                    } else {
                        String nomeItem = getNomeItemById(itemId);

                        ContentValues valuesNovo = new ContentValues();
                        valuesNovo.put("id", itemId);
                        valuesNovo.put("nome", nomeItem);
                        valuesNovo.put("quantidade", quantidade);
                        valuesNovo.put("centro_id", centroDestinoId);

                        long result = db.insert(TABLE_ITEMS, null, valuesNovo);
                        if (result == -1) {
                            Log.e(TAG, "Erro ao inserir o item no centro de destino.");
                        } else {
                            Log.e(TAG, "Item inserido com sucesso no centro de destino.");
                        }
                    }

                    int novaQuantidadeOrigem = quantidadeDisponivel - quantidade;
                    if (novaQuantidadeOrigem > 0) {
                        ContentValues valuesOrigem = new ContentValues();
                        valuesOrigem.put("quantidade", novaQuantidadeOrigem);

                        db.update(TABLE_ITEMS, valuesOrigem, "id = ? AND centro_id = ?",
                                new String[]{String.valueOf(itemId), String.valueOf(centroOrigemId)});
                    } else {
                        db.delete(TABLE_ITEMS, "id = ? AND centro_id = ?",
                                new String[]{String.valueOf(itemId), String.valueOf(centroOrigemId)});
                    }

                    return 200;
                } else {
                    return -1;
                }
            } else {
                return 404;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            if (cursorOrigem != null) {
                cursorOrigem.close();
            }
            if (cursorDestino != null) {
                cursorDestino.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public String getNomeItemById(int itemId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String nomeItem = "";

        Cursor cursor = db.rawQuery("SELECT nome FROM items WHERE id = ?", new String[]{String.valueOf(itemId)});
        if (cursor.moveToFirst()) {
            nomeItem = cursor.getString(0);
        } else {
            Log.e(TAG, "Nome do item não encontrado.");
        }

        cursor.close();
        return nomeItem;
    }

    public void addItem(String nome, int quantidade, int centroId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOME, nome);
        values.put(COLUMN_QUANTIDADE, quantidade);
        values.put(COLUMN_CENTRO_ID, centroId);

        db.insert(TABLE_ITEMS, null, values);
        db.close();
    }

    public List<Item> getAllItems() {
        List<Item> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ITEMS, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String nome = cursor.getString(1);
                int quantidade = cursor.getInt(2);
                int centroId = cursor.getInt(3);

                itemList.add(new Item(id, nome, quantidade, centroId));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return itemList;
    }

    public List<CentroDeDistribuicao> getAllCentros() {
        List<CentroDeDistribuicao> centrosList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CENTRO_DISTRIBUICAO, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String nome = cursor.getString(1);

                centrosList.add(new CentroDeDistribuicao(id, nome));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return centrosList;
    }

    public CentroDeDistribuicao getCentroById(int centroId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CENTRO_DISTRIBUICAO + " WHERE " + COLUMN_CENTRO_ID_REF + " = ?", new String[]{String.valueOf(centroId)});

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            String nome = cursor.getString(1);
            cursor.close();
            db.close();
            return new CentroDeDistribuicao(id, nome);
        } else {
            cursor.close();
            db.close();
            return null;
        }
    }


    public List<Item> getItemsByCentroId(int centroId) {
        List<Item> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ITEMS + " WHERE " + COLUMN_CENTRO_ID + " = ?", new String[]{String.valueOf(centroId)});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String nome = cursor.getString(1);
                int quantidade = cursor.getInt(2);
                int centroIdFromDb = cursor.getInt(3);

                itemList.add(new Item(id, nome, quantidade, centroIdFromDb));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return itemList;
    }


    public void updateItem(int id, String nome, int quantidade, int centroId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOME, nome);
        values.put(COLUMN_QUANTIDADE, quantidade);
        values.put(COLUMN_CENTRO_ID, centroId);

        db.update(TABLE_ITEMS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}
