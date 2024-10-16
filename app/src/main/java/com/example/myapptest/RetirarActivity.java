package com.example.myapptest;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RetirarActivity extends AppCompatActivity {

    private EditText txtIDCentro, txtIDItem, txtQuantidade;
    private Button btnRetirar;
    private DatabaseItem dbItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_retirar);

        dbItem = new DatabaseItem(this);

        txtIDCentro = findViewById(R.id.txtCentroIDRetirar);
        txtIDItem = findViewById(R.id.txtIDRetirar);
        txtQuantidade = findViewById(R.id.txtQuantidadeRetirar);
        btnRetirar = findViewById(R.id.btnRetiradaRetirar);

        btnRetirar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String textIDItem = txtIDItem.getText().toString();
                String textQuantidade = txtQuantidade.getText().toString();
                String textCentroEntrada = txtQuantidade.getText().toString();

                if (textIDItem.isEmpty() || textQuantidade.isEmpty() || textCentroEntrada.isEmpty()) {
                    Toast.makeText(RetirarActivity.this, "Algum campo está vazio!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int idCentro = Integer.parseInt(txtIDCentro.getText().toString());
                int idItem = Integer.parseInt(txtIDItem.getText().toString());
                int qtd = Integer.parseInt(txtQuantidade.getText().toString());

                int itemId;
                try {
                    itemId = Integer.parseInt(txtIDItem.getText().toString());

                    int codigoStatus = dbItem.retirarItem(idCentro, idItem, qtd);

                    switch (codigoStatus) {
                        case 200:
                            Toast.makeText(RetirarActivity.this, "Item retirado com sucesso!", Toast.LENGTH_SHORT).show();
                            break;
                        case 404:
                            Toast.makeText(RetirarActivity.this, "Falha na retirada. Item não encontrado.", Toast.LENGTH_SHORT).show();
                            break;
                        case 400:
                            Toast.makeText(RetirarActivity.this, "Houve uma falha durante a retirada.", Toast.LENGTH_SHORT).show();
                            break;
                        case -1:
                            Toast.makeText(RetirarActivity.this, "Quantidade insuficiente.", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(RetirarActivity.this, "Erro desconhecido.", Toast.LENGTH_SHORT).show();
                            break;
                    }

                } catch (NumberFormatException e) {
                    Toast.makeText(RetirarActivity.this, "Por favor, insira um ID válido.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
