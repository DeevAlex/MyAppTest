package com.example.myapptest;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class AlterarSenhaActivity extends AppCompatActivity {

    private EditText txtSenha, txtRepetirSenha;
    
    private Button btnAlterarSenha;
    
    private DatabaseUsuarios dbUsuarios;

    private TextView txtPasso1, txtPasso2, txtPasso3, txtPasso4, txtPasso5;

    private boolean temMinuscula, temMaiuscula, temNumerico, temEspecial, contemOitoCaracteres;

    private ProgressBar progressStatus;

    int successColor, failColor, colorPrimaryText, warnColor;

    List<Usuario> usuarios;

    String emailUsuario;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alterar_senha);

        dbUsuarios = new DatabaseUsuarios(this);
        
        txtSenha = findViewById(R.id.txtSenha);
        txtRepetirSenha = findViewById(R.id.txtRepetirSenha);
        btnAlterarSenha = findViewById(R.id.btnAlterarSenha);

        progressStatus = findViewById(R.id.progressStatus2);
        progressStatus.setMax(100);

        emailUsuario = getIntent().getStringExtra("emailUsuario");

        txtPasso1 = findViewById(R.id.txtPassoRe1);
        txtPasso2 = findViewById(R.id.txtPassoRe2);
        txtPasso3 = findViewById(R.id.txtPassoRe3);
        txtPasso4 = findViewById(R.id.txtPassoRe4);
        txtPasso5 = findViewById(R.id.txtPassoRe5);

        successColor = getResources().getColor(R.color.success);
        warnColor = getResources().getColor(R.color.warn);
        failColor = getResources().getColor(R.color.fail);

        TypedArray typedArray = getTheme().obtainStyledAttributes(new int[] { android.R.attr.textColorPrimary });
        colorPrimaryText = typedArray.getColor(0, Color.BLACK);

        btnAlterarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String campoSenha = txtSenha.getText().toString();
                String campoRepetirSenha = txtRepetirSenha.getText().toString();

                if (campoSenha.isEmpty() || campoRepetirSenha.isEmpty()) {
                    Toast.makeText(AlterarSenhaActivity.this, "Algum campo está vazio", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!campoSenha.equals(campoRepetirSenha)) {
                    Toast.makeText(AlterarSenhaActivity.this, "As senhas não são iguais!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (contemOitoCaracteres && temEspecial && temMaiuscula && temMinuscula && temNumerico) {

                    Usuario u = buscaUsuarioPorEmail(emailUsuario);

                    if (u != null) {

                        dbUsuarios.updateUser(u.getId(), u.getEmail(), campoSenha, u.isLembrarSenha(), u.getCargo());

                        txtSenha.setText("");
                        txtRepetirSenha.setText("");
                        progressStatus.setProgress(0);

                        Toast.makeText(AlterarSenhaActivity.this, "Sua senha alterada!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(AlterarSenhaActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(AlterarSenhaActivity.this, "Usuario não encontrado", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AlterarSenhaActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                } else {
                    Toast.makeText(AlterarSenhaActivity.this, "Houve um problema na alteração", Toast.LENGTH_SHORT).show();
                }

            }
        });

        txtSenha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String senha = txtSenha.getText().toString();

                if (senha.isEmpty()) {
                    resetValidationColors();
                    progressStatus.setProgress(0);
                    return;
                }

                checkValidacoes(senha);
                updateProgress();
            }

            @Override
            public void afterTextChanged(Editable editable) {

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

    private void checkValidacoes(String senha) {

        String regexCaractereEspecial = ".*[!@#$%^&*()\\-_=+{}\\[\\]:;\"'<>,.?/\\\\|].*";
        String regexCaractereMaiusculo = ".*[A-Z].*";
        String regexCaractereMinusculo = ".*[a-z].*";
        String regexCaractereNumerico = ".*[0-9].*";

        temEspecial = senha.matches(regexCaractereEspecial);
        txtPasso1.setTextColor(temEspecial ? successColor : failColor);

        temNumerico = senha.matches(regexCaractereNumerico);
        txtPasso2.setTextColor(temNumerico ? successColor : failColor);

        temMaiuscula = senha.matches(regexCaractereMaiusculo);
        txtPasso3.setTextColor(temMaiuscula ? successColor : failColor);

        temMinuscula = senha.matches(regexCaractereMinusculo);
        txtPasso4.setTextColor(temMinuscula ? successColor : failColor);

        contemOitoCaracteres = (senha.length() >= 8 && senha.length() <= 16);
        txtPasso5.setTextColor(contemOitoCaracteres ? successColor : failColor);

    }

    private void resetValidationColors() {
        TextView[] passos = {txtPasso1, txtPasso2, txtPasso3, txtPasso4, txtPasso5};
        for (TextView passo : passos) {
            passo.setTextColor(colorPrimaryText);
        }
    }

    private void updateProgress() {
        double progress = 0;

        if (temEspecial) progress += 20;
        if (temNumerico) progress += 20;
        if (temMaiuscula) progress += 20;
        if (temMinuscula) progress += 20;
        if (contemOitoCaracteres) progress += 20;

        progressStatus.setProgress((int) Math.max(0, Math.min(progress, 100)));

        int transparentColor = Color.parseColor("#00FFFFFF");

        if (progress <= 0) {
            progressStatus.setProgressTintList(ColorStateList.valueOf(transparentColor));
        } else if (progress >= 20 && progress <= 49) {
            progressStatus.setProgressTintList(ColorStateList.valueOf(failColor));
        } else if (progress >= 50 && progress <= 99) {
            progressStatus.setProgressTintList(ColorStateList.valueOf(warnColor));
        } else if (progress == 100) {
            progressStatus.setProgressTintList(ColorStateList.valueOf(successColor));
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AlterarSenhaActivity.this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}