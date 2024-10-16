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

public class TransferirActivity extends AppCompatActivity {

    private EditText txtIDItem, txtQuantidade, txtCentroSaida, txtCentroEntrada;

    private Button btnTransferir;
    private DatabaseItem dbItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transferir);

        dbItem = new DatabaseItem(this);

        txtIDItem = findViewById(R.id.txtIDTransferir);
        txtQuantidade = findViewById(R.id.txtQuantidadeTransferir);
        txtCentroEntrada = findViewById(R.id.txtIDCentroEntrada);
        txtCentroSaida = findViewById(R.id.txtIDCentroSaida);

        btnTransferir = findViewById(R.id.btnTransferenciaTranferir);

        btnTransferir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textIDItem = txtIDItem.getText().toString();
                String textQuantidade = txtQuantidade.getText().toString();
                String textCentroEntrada = txtCentroEntrada.getText().toString();
                String textCentroSaida = txtCentroSaida.getText().toString();

                if (textIDItem.isEmpty() || textQuantidade.isEmpty() || textCentroEntrada.isEmpty() || textCentroSaida.isEmpty()) {
                    Toast.makeText(TransferirActivity.this, "Algum campo está vazio!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int idItem = Integer.parseInt(txtIDItem.getText().toString());
                int quantidade = Integer.parseInt(txtQuantidade.getText().toString());
                int centroOrigemId = Integer.parseInt(txtCentroSaida.getText().toString());
                int centroDestinoId = Integer.parseInt(txtCentroEntrada.getText().toString());

                try {

                    int statusCode = dbItem.transferirItem(idItem, quantidade, centroOrigemId, centroDestinoId);

                    switch (statusCode) {
                        case 200:
                            Toast.makeText(TransferirActivity.this, "Transferência de 'CD" + centroOrigemId + "' para 'CD" + centroDestinoId + "' enviada para analise!", Toast.LENGTH_SHORT).show();
                            break;
                        case 404:
                            Toast.makeText(TransferirActivity.this, "Falha na transferência. Item não encontrado.", Toast.LENGTH_SHORT).show();
                            break;
                        case 400:
                            Toast.makeText(TransferirActivity.this, "Houve uma falha durante o envio da analise para transferência.", Toast.LENGTH_SHORT).show();
                            break;
                        case -1:
                            Toast.makeText(TransferirActivity.this, "Quantidade insuficiente.", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(TransferirActivity.this, "Erro desconhecido.", Toast.LENGTH_SHORT).show();
                            break;
                    }

                } catch (NumberFormatException e) {
                    Toast.makeText(TransferirActivity.this, "Por favor, insira um ID válido.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
