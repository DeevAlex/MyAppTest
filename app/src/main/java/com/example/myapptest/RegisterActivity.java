package com.example.myapptest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Debug;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
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

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailCadastro, senhaCadastro, repitirSenhaCadastro;

    private TextView txtPasso1, txtPasso2, txtPasso3, txtPasso4, txtPasso5;

    private ProgressBar progressStatus;

    private Button btnRegistrar;

    private TextView txtLinkEntrar;

    private boolean temMinuscula, temMaiuscula, temNumerico, temEspecial, contemOitoCaracteres;

    int successColor, failColor, colorPrimaryText, warnColor;

    private DatabaseUsuarios dbUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        dbUsuarios = new DatabaseUsuarios(this);

        emailCadastro = findViewById(R.id.emailCadastro);
        senhaCadastro = findViewById(R.id.senhaCadastro);
        repitirSenhaCadastro = findViewById(R.id.repitirSenhaCadastro);

        txtPasso1 = findViewById(R.id.txtPasso1);
        txtPasso2 = findViewById(R.id.txtPasso2);
        txtPasso3 = findViewById(R.id.txtPasso3);
        txtPasso4 = findViewById(R.id.txtPasso4);
        txtPasso5 = findViewById(R.id.txtPasso5);
        txtLinkEntrar = findViewById(R.id.txtLinkEntrar);

        progressStatus = findViewById(R.id.progressStatus);
        progressStatus.setMax(100);

        btnRegistrar = findViewById(R.id.btnRegistrar);

        successColor = getResources().getColor(R.color.success);
        warnColor = getResources().getColor(R.color.warn);
        failColor = getResources().getColor(R.color.fail);

        TypedArray typedArray = getTheme().obtainStyledAttributes(new int[] { android.R.attr.textColorPrimary });
        colorPrimaryText = typedArray.getColor(0, Color.BLACK);

        txtLinkEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        senhaCadastro.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String senha = senhaCadastro.getText().toString();
                String repitirSenha = repitirSenhaCadastro.getText().toString();

                if (senha.isEmpty()) {
                    resetValidationColors();
                    progressStatus.setProgress(0);
                    return;
                }

                checkValidacoes(senha);
                updateProgress();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

                String email = emailCadastro.getText().toString();
                String senha = senhaCadastro.getText().toString();
                String reSenha = repitirSenhaCadastro.getText().toString();

                if (email.isEmpty() || senha.isEmpty() || reSenha.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Um campo encontra-se vazio", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!email.matches(emailRegex)) {
                    Toast.makeText(RegisterActivity.this, "Formato de email é invalido", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!senha.equals(reSenha)) {
                    Toast.makeText(RegisterActivity.this, "As senhas não são iguais!", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (Usuario u : loadUsers()) {
                    if (u.getEmail().equals(email)) {
                        Toast.makeText(RegisterActivity.this, "Já existe um usuario registrado com esse email", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (email.matches(emailRegex) && contemOitoCaracteres && temEspecial && temMaiuscula && temMinuscula && temNumerico) {

                    dbUsuarios.addUser(email, senha, false);

                    emailCadastro.setText("");
                    senhaCadastro.setText("");
                    repitirSenhaCadastro.setText("");
                    progressStatus.setProgress(0);

                    Toast.makeText(RegisterActivity.this, "Usuario registrado", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(RegisterActivity.this, "Houve um problema no cadastro", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    private List<Usuario> loadUsers() {
        return dbUsuarios.getAllUsers();
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
}