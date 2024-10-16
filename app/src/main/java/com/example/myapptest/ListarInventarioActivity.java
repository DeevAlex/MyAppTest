package com.example.myapptest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class ListarInventarioActivity extends AppCompatActivity {

    private DatabaseItem itemsCentros;
    private ListView lista;

    ArrayList<Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listar_inventario);

        lista = findViewById(R.id.listaItems);
        itemsCentros = new DatabaseItem(this);

        Intent intent = getIntent();
        boolean checksLigados = intent.getBooleanExtra("checksLigados", false);
        boolean check1Ligado = intent.getBooleanExtra("check1Ligado", false);
        boolean check2Ligado = intent.getBooleanExtra("check2Ligado", false);

        if (checksLigados) {
            items = (ArrayList<Item>) itemsCentros.getItemsByCentroId(1);
            items.addAll(itemsCentros.getItemsByCentroId(2));
        } else if (check1Ligado) {
            items = (ArrayList<Item>) itemsCentros.getItemsByCentroId(1);
        } else if (check2Ligado) {
            items = (ArrayList<Item>) itemsCentros.getItemsByCentroId(2);
        }

        ItemAdapter adapter = new ItemAdapter(this, items, itemsCentros);
        lista.setAdapter(adapter);
    }
}
