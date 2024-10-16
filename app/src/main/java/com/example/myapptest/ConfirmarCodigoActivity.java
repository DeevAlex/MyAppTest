package com.example.myapptest;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ConfirmarCodigoActivity extends AppCompatActivity {

    private EditText txtCodigo;
    private Button btnConfirmarCodigo;

    String codigoRecuperacao;
    String emailUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirmar_codigo);

        codigoRecuperacao = getIntent().getStringExtra("CodigoRecuperacao");
        emailUsuario = getIntent().getStringExtra("emailUsuario");

        txtCodigo = findViewById(R.id.txtCodigo);
        btnConfirmarCodigo = findViewById(R.id.btnConfirmarCodigo);

         btnConfirmarCodigo.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 if (txtCodigo.getText().toString().isEmpty()) {
                     Toast.makeText(ConfirmarCodigoActivity.this, "Campo do código está vazio", Toast.LENGTH_SHORT).show();
                     return;
                 }

                 if (txtCodigo.getText().toString().equals(codigoRecuperacao)) {
                     Intent telaAlterarSenha = new Intent(ConfirmarCodigoActivity.this, AlterarSenhaActivity.class);
                     telaAlterarSenha.putExtra("emailUsuario", emailUsuario);
                     codigoRecuperacao = "";
                     startActivity(telaAlterarSenha);
                 } else {
                     Toast.makeText(ConfirmarCodigoActivity.this, "Código Invalido", Toast.LENGTH_SHORT).show();
                 }
             }
         });

    }

    @Override
    public void onBackPressed() {
        if (emailUsuario.isEmpty()) {
            Intent telaPrincipal = new Intent(ConfirmarCodigoActivity.this, MainActivity.class);
            startActivity(telaPrincipal);
        } else {
            Intent telaDeEmail = new Intent(ConfirmarCodigoActivity.this, EsqueceuSenhaActivity.class);
            startActivity(telaDeEmail);
        }
        super.onBackPressed();
    }
}