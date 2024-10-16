package com.example.myapptest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class InventarioActivity extends AppCompatActivity {


    private CheckBox checkBoxCentro1;
    private CheckBox checkBoxCentro2;

    private Button btnListar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inventario);

        checkBoxCentro1 = findViewById(R.id.checkBoxCd1);
        checkBoxCentro2 = findViewById(R.id.checkBoxCd2);

        btnListar = findViewById(R.id.btnInventarioListar);

        btnListar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent telaInventario = new Intent(InventarioActivity.this, ListarInventarioActivity.class);
                if (checkBoxCentro1.isChecked() && checkBoxCentro2.isChecked()) {
                    telaInventario.putExtra("checksLigados", true);
                } else if (checkBoxCentro1.isChecked()) {
                    telaInventario.putExtra("check1Ligado", checkBoxCentro1.isChecked());
                } else if (checkBoxCentro2.isChecked()) {
                    telaInventario.putExtra("check2Ligado", checkBoxCentro2.isChecked());
                } else {
                    Toast.makeText(InventarioActivity.this, "Selecione ao menos um centro", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(telaInventario);
            }
        });

    }
}