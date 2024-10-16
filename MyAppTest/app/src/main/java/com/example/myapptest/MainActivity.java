package com.example.myapptest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText email;
    private EditText senha;
    private Button btnLogin;
    private CheckBox checkBoxLembrarSenha; // Adicionando o checkbox
    private TextView txtLinkRegistrar;
    private TextView txtLinkEsqueceuSenha;
    private DatabaseHelper dbHelper;

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "LoginPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        email = findViewById(R.id.campoUsuarioEmail);
        senha = findViewById(R.id.campoUsuarioSenha);
        btnLogin = findViewById(R.id.btnEntrar);
        checkBoxLembrarSenha = findViewById(R.id.checkBoxLembrarSenha); // Inicializando o checkbox

        txtLinkRegistrar = findViewById(R.id.txtLinkRegistrar);
        txtLinkEsqueceuSenha = findViewById(R.id.txtLinkEsqueceuSenha);

        carregarCredenciaisSalvas(); // Carregar credenciais se existirem

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Usuario> usuarios = dbHelper.getAllUsers();

                String emailC = email.getText().toString();
                String senhaC = senha.getText().toString();

                String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
                String emailEncontrado = null;

                if (emailC.isEmpty() || senhaC.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Um campo encontra-se vazio", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!emailC.matches(emailRegex)) {
                    Toast.makeText(MainActivity.this, "Formato de email é inválido", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (Usuario u : usuarios) {
                    if (emailC.equals(u.getEmail()) && senhaC.equals(u.getSenha())) {
                        emailEncontrado = u.getEmail();
                        break;
                    }
                }

                if (emailEncontrado == null) {
                    Toast.makeText(MainActivity.this, "Usuário não encontrado", Toast.LENGTH_SHORT).show();
                } else {
                    // Salvar credenciais se o checkbox estiver marcado
                    if (checkBoxLembrarSenha.isChecked()) {
                        salvarCredenciais(emailC, senhaC);
                    }

                    // Limpar os campos e continuar com o login
                    email.setText("");
                    senha.setText("");
                    Intent intent = new Intent(MainActivity.this, FunctionsActivity.class);
                    intent.putExtra("EMAIL", emailEncontrado);
                    startActivity(intent);
                }
            }
        });

        txtLinkRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        txtLinkEsqueceuSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    // Método para salvar as credenciais
    private void salvarCredenciais(String email, String senha) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.putString("senha", senha);
        editor.putBoolean("lembrar", true);
        editor.apply();  // Salva os dados
    }

    // Método para carregar as credenciais
    private void carregarCredenciaisSalvas() {
        boolean lembrar = sharedPreferences.getBoolean("lembrar", false);
        if (lembrar) {
            String emailSalvo = sharedPreferences.getString("email", "");
            String senhaSalva = sharedPreferences.getString("senha", "");
            email.setText(emailSalvo);
            senha.setText(senhaSalva);
            checkBoxLembrarSenha.setChecked(true);
        }
    }
}
