package com.example.myapptest;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class FunctionsActivity extends AppCompatActivity {

    private Button btnAbastecer, btnRetirar, btnTransferir, btnInventario, btnSobre;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_functions);

        btnAbastecer = findViewById(R.id.btnAbastecer);
        btnRetirar = findViewById(R.id.btnRetirar);
        btnTransferir = findViewById(R.id.btnTransferir);
        btnInventario = findViewById(R.id.btnInventario);
        btnSobre = findViewById(R.id.btnSobre);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.oauth_android)) // ID do client para o Firebase
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnAbastecer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent telaAbastecer = new Intent(FunctionsActivity.this, AbastecerActivity.class);
                startActivity(telaAbastecer);
            }
        });

        btnRetirar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent telaAbastecer = new Intent(FunctionsActivity.this, RetirarActivity.class);
                startActivity(telaAbastecer);
            }
        });

        btnTransferir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent telaAbastecer = new Intent(FunctionsActivity.this, TransferirActivity.class);
                startActivity(telaAbastecer);
            }
        });

        btnInventario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent telaAbastecer = new Intent(FunctionsActivity.this, InventarioActivity.class);
                startActivity(telaAbastecer);
            }
        });

        btnSobre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent telaSobre = new Intent(FunctionsActivity.this, SobreActivity.class);
                startActivity(telaSobre);
            }
        });

    }

    @Override
    public void onBackPressed() {

        signOut();

        super.onBackPressed();
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
        Intent intent = new Intent(FunctionsActivity.this, MainActivity.class);
        startActivity(intent);
    }

}