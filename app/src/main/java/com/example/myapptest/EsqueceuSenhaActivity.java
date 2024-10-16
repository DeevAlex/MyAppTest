package com.example.myapptest;

import static android.content.ContentValues.TAG;

import android.content.Context;
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

import java.util.List;

public class EsqueceuSenhaActivity extends AppCompatActivity {

    private EditText emailConfirmado;
    private Button btnConfirmarEmail;

    private DatabaseUsuarios dbUsuarios;

    Context context;

    private List<Usuario> usuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_esqueceu_senha);

        emailConfirmado = findViewById(R.id.txtEmailConfirmado);
        btnConfirmarEmail = findViewById(R.id.btnEmailConfirmado);

        dbUsuarios = new DatabaseUsuarios(this);

        context = getApplicationContext();

        btnConfirmarEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Usuario u = buscaUsuarioPorEmail(emailConfirmado.getText().toString());

                if (u != null) {
                    String emailEnviador = getString(R.string.emailEnviador);
                    String senhaEnviador = getString(R.string.emailEnviador);
                    EnviadorEmailRecuperacao enviadorEmailRecuperacao = new EnviadorEmailRecuperacao(emailEnviador, senhaEnviador, getApplicationContext());
                    String codigoEnviado = enviadorEmailRecuperacao.generateRecoveryCode();
                    Intent intent = new Intent(EsqueceuSenhaActivity.this, ConfirmarCodigoActivity.class);
                    intent.putExtra("CodigoRecuperacao", codigoEnviado);
                    intent.putExtra("emailUsuario", u.getEmail());
                    startActivity(intent);

                    Thread backgroundThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                for (int i = 1; i <= 2; i++) {
                                    Thread.sleep(1000);
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(EsqueceuSenhaActivity.this, "Enviando Codigo...", Toast.LENGTH_SHORT).show();
                                        enviadorEmailRecuperacao.enviaEmail(emailConfirmado.getText().toString(), codigoEnviado);
                                        Toast.makeText(EsqueceuSenhaActivity.this, "Codigo enviado", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    backgroundThread.start();
                } else {
                    Toast.makeText(EsqueceuSenhaActivity.this, "Usuario não existe", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EsqueceuSenhaActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
        
    }

    private Usuario buscaUsuarioPorEmail(String email) {
        usuarios = dbUsuarios.getAllUsers();
        for (Usuario u : usuarios) {
            if (email.equals(u.getEmail())) {
                return u;
            }
        }
        return null;
    }

}