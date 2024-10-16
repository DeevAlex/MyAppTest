package com.example.myapptest;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private EditText email, senha;
    private Button btnLogin;
    private CheckBox checkBoxLembrarSenha; // Adicionando o checkbox
    private TextView txtLinkRegistrar, txtLinkEsqueceuSenha;

    private DatabaseUsuarios dbUsuarios;

    private SignInButton btnEntrarComGoogle;

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "MyPrefs";

    List<Usuario> usuarios;

    private Context context;

    String emailUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.oauth_android)) // ID do client para o Firebase
                .requestEmail()
                .build();

        context = getApplicationContext();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        dbUsuarios = new DatabaseUsuarios(this);

        usuarios = dbUsuarios.getAllUsers();

        email = findViewById(R.id.campoUsuarioEmail);
        senha = findViewById(R.id.campoUsuarioSenha);
        btnLogin = findViewById(R.id.btnEntrar);
        checkBoxLembrarSenha = findViewById(R.id.checkBoxLembrarSenha);

        btnEntrarComGoogle = findViewById(R.id.btnEntrarComGoogle);

        Dados dadosSalvos = carregarCredenciaisSalvas();

        txtLinkRegistrar = findViewById(R.id.txtLinkRegistrar);
        txtLinkEsqueceuSenha = findViewById(R.id.txtLinkEsqueceuSenha);

        if (dadosSalvos != null) {
            email.setText(dadosSalvos.getEmail());
            senha.setText(dadosSalvos.getSenha());
            checkBoxLembrarSenha.setChecked(dadosSalvos.isLembrar());
        }

        btnEntrarComGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (NetworkUtil.isConnectedToInternet(getApplicationContext())) {
                    signIn();
                } else {
                    Toast.makeText(MainActivity.this, "Sem conexão com a internet", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("MainActivity", "Primeiro");

                String emailC = email.getText().toString();
                String senhaC = senha.getText().toString();

                String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

                if (emailC.isEmpty() || senhaC.isEmpty()) {
                    Log.e("MainActivity", "Um campo encontra-se vazio");
                    Toast.makeText(MainActivity.this, "Um campo encontra-se vazio", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!emailC.matches(emailRegex)) {
                    Log.e("MainActivity", "Não é um email valido");
                    Toast.makeText(MainActivity.this, "Formato de email é inválido", Toast.LENGTH_SHORT).show();
                    return;
                }

                Usuario u = buscaUsuarioPorEmail(emailC);

                if (u != null) {

                    if (senhaC.equals(u.getSenha())) {
                        if (((emailC.equals(u.getEmail()) && checkBoxLembrarSenha.isChecked()))) {
                            dbUsuarios.updateUser(u.getId(), u.getEmail(), u.getSenha(), true, u.getCargo());
                            salvarCredenciais(u.getEmail(), u.getSenha(), true);
                        } else if ((emailC.equals(u.getEmail()) && senhaC.equals(u.getSenha()))) {
                            dbUsuarios.updateUser(u.getId(), u.getEmail(), u.getSenha(), false, u.getCargo());
                            salvarCredenciais("", "", false);
                            email.setText("");
                            senha.setText("");
                        }
                        Intent intent = new Intent(MainActivity.this, FunctionsActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Senha Invalida", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(MainActivity.this, "Usuário não encontrado", Toast.LENGTH_SHORT).show();
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
                final String emailEnviador = getString(R.string.emailEnviador); // Obtém o email do R.string
                final String senhaEnviador = getString(R.string.senhaEnviador); // Obtém a senha do R.string

                String emailRecebedor = email.getText().toString();


                EnviadorEmailRecuperacao enviadorEmailRecuperacao = new EnviadorEmailRecuperacao(emailEnviador, senhaEnviador, context);
                if (!email.getText().toString().isEmpty()) {
                    Usuario u = buscaUsuarioPorEmail(emailRecebedor);
                    if (u != null) {
                        String codigoEnviado = enviadorEmailRecuperacao.generateRecoveryCode();
                        Intent intent = new Intent(MainActivity.this, ConfirmarCodigoActivity.class);
                        intent.putExtra("CodigoRecuperacao", codigoEnviado);
                        intent.putExtra("emailUsuario", emailRecebedor);
                        startActivity(intent);
                        enviadorEmailRecuperacao.enviaEmail(emailRecebedor, codigoEnviado);
                    } else {
                        Toast.makeText(MainActivity.this, "Usuario não existe", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Intent intent = new Intent(MainActivity.this, EsqueceuSenhaActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(MainActivity.this, "Houve um erro ao entrar", Toast.LENGTH_LONG).show();
                atualizaUI(null);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser usuario = mAuth.getCurrentUser();

                        atualizaUI(usuario);
                    } else {
                        Toast.makeText(MainActivity.this, "Houve uma falha ao entrar", Toast.LENGTH_LONG).show();
                        atualizaUI(null);
                    }
                }
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Falha na autenticação", e);
        });;
    }

    private void atualizaUI(FirebaseUser usuario) {
        if (usuario != null) {

            Usuario u = buscaUsuarioPorEmail(usuario.getEmail());

            if (u != null) {
                Intent intent = new Intent(MainActivity.this, FunctionsActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(MainActivity.this, EntrandoComGoogleActivity.class);
                intent.putExtra("email", usuario.getEmail());
                startActivity(intent);
            }

        } else {
            Log.println(Log.INFO, TAG, "signInWithCredential:Saiu da conta");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser usuarioAtual = mAuth.getCurrentUser();
        atualizaUI(usuarioAtual);
    }

    private Usuario buscaUsuarioPorEmail(String email) {
        for (Usuario u : usuarios) {
            if (email.equals(u.getEmail())) {
                return u;
            }
        }
        return null;
    }

    private Usuario buscaUsuario(String email, String senha) {
        for (Usuario u : usuarios) {
            if (email.equals(u.getEmail()) && senha.equals(u.getSenha())) {
                return u;
            }
        }
        return null;
    }

    private Dados carregarCredenciaisSalvas() {
        if (sharedPreferences != null) {
            String email = sharedPreferences.getString("email", "");
            String senha = sharedPreferences.getString("senha", "");
            boolean lembrar = sharedPreferences.getBoolean("lembrar", false);
            return new Dados(email, senha, lembrar);
        } else {
            Log.e("MainActivity", "SharedPreferences é nulo");
            return null;
        }
    }

    private void salvarCredenciais(String email, String senha, boolean lembrar) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.putString("senha", senha);
        editor.putBoolean("lembrar", lembrar);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        super.onBackPressed();
    }
}