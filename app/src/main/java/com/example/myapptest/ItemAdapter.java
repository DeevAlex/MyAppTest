package com.example.myapptest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ItemAdapter extends ArrayAdapter<Item> {
    private DatabaseItem db;  // Referência ao banco de dados para buscar o nome do centro

    public ItemAdapter(Context context, List<Item> items, DatabaseItem db) {
        super(context, 0, items);
        this.db = db;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Item item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        }

        TextView centroView = convertView.findViewById(android.R.id.text1);
        TextView produtoView = convertView.findViewById(android.R.id.text2);

        String nomeCentro = db.getCentroNomeById(item.getCentroId());  // Assumindo que você tenha este método

        centroView.setText("Centro de Distribuição: " + nomeCentro);  // Nome do centro
        produtoView.setText("ID: " + item.getId() + ", ID Centro: " + item.getCentroId() + " - " + item.getNome() + " - " + item.getQuantidade() + "x");  // Nome do produto e quantidade

        return convertView;
    }
}
