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
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class EntrandoComGoogleActivity extends AppCompatActivity {

    private EditText txtSenha, txtRepetirSenha;

    private Button btnSalvarSenha;

    private DatabaseUsuarios dbUsuarios;

    private TextView txtPasso1, txtPasso2, txtPasso3, txtPasso4, txtPasso5, txtEmailView;

    private boolean temMinuscula, temMaiuscula, temNumerico, temEspecial, contemOitoCaracteres;

    private ProgressBar progressStatus;

    int successColor, failColor, warnColor, colorPrimaryText;

    String emailUsuario;

    List<Usuario> usuarios;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_entrando_com_google);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(getString(R.string.oauth_android))
        .requestEmail()
        .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        dbUsuarios = new DatabaseUsuarios(this);

        txtSenha = findViewById(R.id.txtSenhaEntrandoComGoogle);
        txtRepetirSenha = findViewById(R.id.txtRepetirSenhaEntrandoComGoogle);

        btnSalvarSenha = findViewById(R.id.btnSalvarSenhaEntrandoComGoogle);

        progressStatus = findViewById(R.id.progressStatus2);
        progressStatus.setMax(100);

        emailUsuario = getIntent().getStringExtra("email");

        txtEmailView = findViewById(R.id.txtEmailView);
        txtPasso1 = findViewById(R.id.txtPassoEntrandoComGoogle1);
        txtPasso2 = findViewById(R.id.txtPassoEntrandoComGoogle2);
        txtPasso3 = findViewById(R.id.txtPassoEntrandoComGoogle3);
        txtPasso4 = findViewById(R.id.txtPassoEntrandoComGoogle4);
        txtPasso5 = findViewById(R.id.txtPassoEntrandoComGoogle5);

        txtEmailView.setText(emailUsuario);

        successColor = getResources().getColor(R.color.success);
        warnColor = getResources().getColor(R.color.warn);
        failColor = getResources().getColor(R.color.fail);

        TypedArray typedArray = getTheme().obtainStyledAttributes(new int[] { android.R.attr.textColorPrimary });
        colorPrimaryText = typedArray.getColor(0, Color.BLACK);

        btnSalvarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String campoSenha = txtSenha.getText().toString();
                String campoRepetirSenha = txtRepetirSenha.getText().toString();

                if (campoSenha.isEmpty() || campoRepetirSenha.isEmpty()) {
                    Toast.makeText(EntrandoComGoogleActivity.this, "Algum campo está vazio", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!campoSenha.equals(campoRepetirSenha)) {
                    Toast.makeText(EntrandoComGoogleActivity.this, "As senhas não são iguais!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (contemOitoCaracteres && temEspecial && temMaiuscula && temMinuscula && temNumerico) {

                    Usuario u = buscaUsuarioPorEmail(emailUsuario);

                    if (u == null) {
                        dbUsuarios.addUser(emailUsuario, campoSenha, false);

                        txtSenha.setText("");
                        txtRepetirSenha.setText("");
                        progressStatus.setProgress(0);

                        Toast.makeText(EntrandoComGoogleActivity.this, "Sua senha foi cadastrada!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(EntrandoComGoogleActivity.this, FunctionsActivity.class);
                        startActivity(intent);
                    }

                } else {
                    Toast.makeText(EntrandoComGoogleActivity.this, "Houve um problema no cadastro", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onPause() {
        signOut();
        Intent intent = new Intent(EntrandoComGoogleActivity.this, MainActivity.class);
        startActivity(intent);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        signOut();
        Intent intent = new Intent(EntrandoComGoogleActivity.this, MainActivity.class);
        startActivity(intent);
        super.onDestroy();
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
            new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    updateUI();
            }
        });
    }

    private void updateUI() {
        Intent telaPrincipal = new Intent(EntrandoComGoogleActivity.this, MainActivity.class);
        startActivity(telaPrincipal);
    }

    @Override
    public void onBackPressed() {
        signOut();
        super.onBackPressed();
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


}