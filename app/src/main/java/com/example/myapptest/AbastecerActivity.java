package com.example.myapptest;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class AbastecerActivity extends AppCompatActivity {

    private EditText txtIdCentro, txtIdItem, txtNomeItem, txtQuantidade;
    
    private Button btnAbastecer;
    
    private DatabaseItem dbItems;

    List<Item> itemsDoCentro;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_abastecer);

        dbItems = new DatabaseItem(this);

        txtIdCentro = findViewById(R.id.txtIDCentroAbastecer);
        txtIdItem = findViewById(R.id.txtIDAbastecer);
        txtNomeItem = findViewById(R.id.txtNomeAbastecer);
        txtQuantidade = findViewById(R.id.txtQuantidadeAbastecer);
        btnAbastecer = findViewById(R.id.btnAbastecimentoAbastecer);
        
        btnAbastecer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                String idCentro = txtIdCentro.getText().toString();
                String idItem = txtIdItem.getText().toString();
                String nomeItem = txtNomeItem.getText().toString();
                String edTxtQuantidade = txtQuantidade.getText().toString();

                if (idCentro.isEmpty() || edTxtQuantidade.isEmpty() || nomeItem.isEmpty()) {
                    Toast.makeText(AbastecerActivity.this, "Erro: Algum campo está vazio", Toast.LENGTH_SHORT).show();
                    return;
                }

                int centro = Integer.parseInt(idCentro);
                int quantidade = Integer.parseInt(edTxtQuantidade);

                if (centro < 0) {
                    Toast.makeText(AbastecerActivity.this, "Erro: Digite um numero positivo", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (quantidade <= 0) {
                    Toast.makeText(AbastecerActivity.this, "A quantidade não pode ser negativa ou zerada", Toast.LENGTH_SHORT).show();
                    return;
                } else if (quantidade > 5000) {
                    Toast.makeText(AbastecerActivity.this, "A quantidade não pode passar de 5000", Toast.LENGTH_SHORT).show();
                    return;
                }

                CentroDeDistribuicao _centro = dbItems.getCentroById(centro);

                if (_centro != null) {
                    itemsDoCentro = dbItems.getItemsByCentroId(_centro.getId());

                    if (idItem.isEmpty()) {
                        dbItems.addItem(nomeItem, quantidade, centro);
                        Toast.makeText(AbastecerActivity.this, "Item '" + nomeItem + "' cadastrado!", Toast.LENGTH_SHORT).show();
                    } else {

                        int item = Integer.parseInt(idItem);

                        if (item < 0) {
                            Toast.makeText(AbastecerActivity.this, "Erro: Digite um ID positivo", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Item i = buscaItemPorID(item);
                        if (i != null) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(AbastecerActivity.this);
                            builder.setTitle("Item já cadastrado");
                            builder.setMessage("Deseja atualizar o item?");

                            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dbItems.updateItem(i.getId(), nomeItem, quantidade, i.getCentroId());  // Atualizar item com nova quantidade
                                    Toast.makeText(AbastecerActivity.this, "Item '" + nomeItem + "' foi atualizado!", Toast.LENGTH_SHORT).show();
                                }
                            });

                            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();

                        }
                    }

                }
            }


        });

    }

    private Item buscaItemPorID(int id) {
        for (Item i : itemsDoCentro) {
            if (i.getId() == id) {
                return i;
            }
        }
        return null;
    }


}