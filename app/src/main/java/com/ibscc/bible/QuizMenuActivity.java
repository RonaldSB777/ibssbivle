package com.ibscc.bible;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class QuizMenuActivity extends Activity {

    private Button btnFacil, btnMedio, btnDificil, btnVoltar;
    private TextView txtRecordeFacil, txtRecordeMedio, txtRecordeDificil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_menu);

        inicializarViews();
        carregarRecordes();
        
        btnFacil.setOnClickListener(v -> iniciarQuiz("facil"));
        btnMedio.setOnClickListener(v -> iniciarQuiz("medio"));
        btnDificil.setOnClickListener(v -> iniciarQuiz("dificil"));
        btnVoltar.setOnClickListener(v -> finish());
    }

    private void inicializarViews() {
        btnFacil = findViewById(R.id.btnFacil);
        btnMedio = findViewById(R.id.btnMedio);
        btnDificil = findViewById(R.id.btnDificil);
        btnVoltar = findViewById(R.id.btnVoltar);
        txtRecordeFacil = findViewById(R.id.txtRecordeFacil);
        txtRecordeMedio = findViewById(R.id.txtRecordeMedio);
        txtRecordeDificil = findViewById(R.id.txtRecordeDificil);
    }

    private void carregarRecordes() {
        SharedPreferences prefs = getSharedPreferences("QuizPrefs", MODE_PRIVATE);
        
        int recordeFacil = prefs.getInt("recorde_facil", 0);
        int recordeMedio = prefs.getInt("recorde_medio", 0);
        int recordeDificil = prefs.getInt("recorde_dificil", 0);
        
        txtRecordeFacil.setText("Recorde: " + recordeFacil + " pts");
        txtRecordeMedio.setText("Recorde: " + recordeMedio + " pts");
        txtRecordeDificil.setText("Recorde: " + recordeDificil + " pts");
    }

    private void iniciarQuiz(String nivel) {
        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra("nivel", nivel);
        startActivity(intent);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        carregarRecordes(); // Atualiza recordes ao voltar
    }
}