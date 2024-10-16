package com.example.myapptest;

import android.annotation.SuppressLint;
import android.content.Intent;
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

    private EditText emailCadastro;
    private EditText senhaCadastro;
    private EditText repitirSenhaCadastro;

    private TextView txtPasso1;
    private TextView txtPasso2;
    private TextView txtPasso3;
    private TextView txtPasso4;
    private TextView txtPasso5;
    private TextView txtPasso6;

    private ProgressBar progressStatus;

    private Button btnRegistrar;

    private TextView txtLinkEntrar;

    private boolean temMinuscula, temMaiuscula, temNumerico, temEspecial, contemOitoCaracteres, senhasIguais;

    int successColor;
    int failColor;
    int colorPrimaryText;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        emailCadastro = findViewById(R.id.emailCadastro);
        senhaCadastro = findViewById(R.id.senhaCadastro);
        repitirSenhaCadastro = findViewById(R.id.repitirSenhaCadastro);

        txtPasso1 = findViewById(R.id.txtPasso1);
        txtPasso2 = findViewById(R.id.txtPasso2);
        txtPasso3 = findViewById(R.id.txtPasso3);
        txtPasso4 = findViewById(R.id.txtPasso4);
        txtPasso5 = findViewById(R.id.txtPasso5);
        txtPasso6 = findViewById(R.id.txtPasso6);
        txtLinkEntrar = findViewById(R.id.txtLinkEntrar);

        progressStatus = findViewById(R.id.progressStatus);
        progressStatus.setMax(100);

        btnRegistrar = findViewById(R.id.btnRegistrar);

        successColor = getResources().getColor(R.color.success);
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
                } else {
                    txtPasso6.setTextColor(failColor);
                }

                checkValidacoes(senha, repitirSenha);
                updateProgress();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });


        repitirSenhaCadastro.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String senhaRepetida = repitirSenhaCadastro.getText().toString();
                String senhaOriginal = senhaCadastro.getText().toString();

                if (senhaRepetida.isEmpty()) {
                    txtPasso6.setTextColor(colorPrimaryText);
                    return;
                }

                if (senhaRepetida.equals(senhaOriginal)) {
                    txtPasso6.setTextColor(successColor);
                    senhasIguais = true;
                } else {
                    txtPasso6.setTextColor(failColor);
                    senhasIguais = false;
                }

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

                for (Usuario u : loadUsers()) {
                    if (u.getEmail().equals(email)) {
                        Toast.makeText(RegisterActivity.this, "Já existe um usuario registrado com esse email", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (email.matches(emailRegex) && contemOitoCaracteres && senhasIguais && temEspecial && temMaiuscula && temMinuscula && temNumerico) {

                    dbHelper.addUser(email, senha);

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

    private List<Usuario> loadUsers() {
        return dbHelper.getAllUsers();
    }

    private void checkValidacoes(String senha, String repetirSenha) {
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

        senhasIguais = repetirSenha.equals(senha);
        txtPasso6.setTextColor(senhasIguais ? successColor : failColor);
    }

    private void resetValidationColors() {
        TextView[] steps = {txtPasso1, txtPasso2, txtPasso3, txtPasso4, txtPasso5, txtPasso6};
        for (TextView step : steps) {
            step.setTextColor(colorPrimaryText);
        }
    }

    private void updateProgress() {
        double progress = 0;

        if (temEspecial) progress += 16.68;
        if (temNumerico) progress += 16.68;
        if (temMaiuscula) progress += 16.68;
        if (temMinuscula) progress += 16.68;
        if (contemOitoCaracteres) progress += 16.68;
        if (senhasIguais) progress += 16.68;

        progressStatus.setProgress((int) Math.max(0, Math.min(progress, 100)));
    }
}