package com.ibscc.bible;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

public class QuizActivity extends Activity {

    private TextView txtPergunta, txtPontuacao, txtProgresso, txtNivel;
    private Button btnOpcao1, btnOpcao2, btnOpcao3, btnOpcao4, btnVoltar;
    private ProgressBar progressBar;
    private LinearLayout layoutQuiz;
    
    private QuizDatabase quizDB;
    private List<Pergunta> perguntas;
    private int perguntaAtual = 0;
    private int pontuacao = 0;
    private String nivelAtual = "facil";
    private boolean podeResponder = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Receber nível escolhido
        Intent intent = getIntent();
        nivelAtual = intent.getStringExtra("nivel");
        if (nivelAtual == null) nivelAtual = "facil";

        inicializarViews();
        configurarCores();
        
        quizDB = new QuizDatabase();
        carregarPerguntas();
        
        if (perguntas != null && !perguntas.isEmpty()) {
            mostrarPergunta();
        } else {
            Toast.makeText(this, "Erro ao carregar perguntas!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void inicializarViews() {
        txtPergunta = findViewById(R.id.txtPergunta);
        txtPontuacao = findViewById(R.id.txtPontuacao);
        txtProgresso = findViewById(R.id.txtProgresso);
        txtNivel = findViewById(R.id.txtNivel);
        btnOpcao1 = findViewById(R.id.btnOpcao1);
        btnOpcao2 = findViewById(R.id.btnOpcao2);
        btnOpcao3 = findViewById(R.id.btnOpcao3);
        btnOpcao4 = findViewById(R.id.btnOpcao4);
        btnVoltar = findViewById(R.id.btnVoltar);
        progressBar = findViewById(R.id.progressBar);
        layoutQuiz = findViewById(R.id.layoutQuiz);

        btnOpcao1.setOnClickListener(v -> verificarResposta(0));
        btnOpcao2.setOnClickListener(v -> verificarResposta(1));
        btnOpcao3.setOnClickListener(v -> verificarResposta(2));
        btnOpcao4.setOnClickListener(v -> verificarResposta(3));
        
        btnVoltar.setOnClickListener(v -> finish());
    }

    private void configurarCores() {
        // Configurar cor baseada no nível
        String corNivel;
        switch(nivelAtual) {
            case "facil":
                corNivel = "#4CAF50"; // Verde
                txtNivel.setText("Nível: Fácil 🟢");
                break;
            case "medio":
                corNivel = "#FF9800"; // Laranja
                txtNivel.setText("Nível: Médio 🟠");
                break;
            case "dificil":
                corNivel = "#F44336"; // Vermelho
                txtNivel.setText("Nível: Difícil 🔴");
                break;
            default:
                corNivel = "#4CAF50";
        }
        
        progressBar.getProgressDrawable().setColorFilter(
            Color.parseColor(corNivel), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    private void carregarPerguntas() {
        // Carregar 10 perguntas aleatórias do nível escolhido
        perguntas = quizDB.getPerguntasPorNivel(nivelAtual, 10);
        progressBar.setMax(perguntas.size());
    }

    private void mostrarPergunta() {
        if (perguntaAtual >= perguntas.size()) {
            mostrarResultadoFinal();
            return;
        }

        podeResponder = true;
        Pergunta p = perguntas.get(perguntaAtual);
        
        // Atualizar UI
        txtPergunta.setText((perguntaAtual + 1) + ". " + p.getPergunta());
        txtProgresso.setText((perguntaAtual + 1) + "/" + perguntas.size());
        progressBar.setProgress(perguntaAtual + 1);
        txtPontuacao.setText("Pontos: " + pontuacao);

        // Resetar cores dos botões
        resetarBotoes();

        // Definir textos das opções
        String[] opcoes = p.getOpcoes();
        btnOpcao1.setText("A) " + opcoes[0]);
        btnOpcao2.setText("B) " + opcoes[1]);
        btnOpcao3.setText("C) " + opcoes[2]);
        btnOpcao4.setText("D) " + opcoes[3]);
    }

    private void resetarBotoes() {
        int corPadrao = Color.parseColor("#1976D2"); // Azul
        
        btnOpcao1.setBackgroundColor(corPadrao);
        btnOpcao2.setBackgroundColor(corPadrao);
        btnOpcao3.setBackgroundColor(corPadrao);
        btnOpcao4.setBackgroundColor(corPadrao);
        
        btnOpcao1.setEnabled(true);
        btnOpcao2.setEnabled(true);
        btnOpcao3.setEnabled(true);
        btnOpcao4.setEnabled(true);
    }

    private void verificarResposta(int respostaEscolhida) {
        if (!podeResponder) return;
        podeResponder = false;

        Pergunta p = perguntas.get(perguntaAtual);
        int correta = p.getRespostaCorreta();
        
        Button btnEscolhido = getBotaoPorIndice(respostaEscolhida);
        Button btnCorreto = getBotaoPorIndice(correta);

        if (respostaEscolhida == correta) {
            // Acertou!
            pontuacao += 10;
            btnEscolhido.setBackgroundColor(Color.parseColor("#4CAF50")); // Verde
            Toast.makeText(this, "✅ Correto! +10 pontos", Toast.LENGTH_SHORT).show();
        } else {
            // Errou!
            btnEscolhido.setBackgroundColor(Color.parseColor("#F44336")); // Vermelho
            btnCorreto.setBackgroundColor(Color.parseColor("#4CAF50")); // Mostra correto
            Toast.makeText(this, "❌ Errado! Resposta: " + p.getOpcoes()[correta], Toast.LENGTH_LONG).show();
        }

        // Desabilitar todos os botões
        btnOpcao1.setEnabled(false);
        btnOpcao2.setEnabled(false);
        btnOpcao3.setEnabled(false);
        btnOpcao4.setEnabled(false);

        // Próxima pergunta após 1.5 segundos
        btnEscolhido.postDelayed(new Runnable() {
            @Override
            public void run() {
                perguntaAtual++;
                mostrarPergunta();
            }
        }, 1500);
    }

    private Button getBotaoPorIndice(int indice) {
        switch(indice) {
            case 0: return btnOpcao1;
            case 1: return btnOpcao2;
            case 2: return btnOpcao3;
            case 3: return btnOpcao4;
            default: return btnOpcao1;
        }
    }

        private void mostrarResultadoFinal() {
        // Salvar pontuação máxima
        SharedPreferences prefs = getSharedPreferences("QuizPrefs", MODE_PRIVATE);
        int recorde = prefs.getInt("recorde_" + nivelAtual, 0);
        
        if (pontuacao > recorde) {
            prefs.edit().putInt("recorde_" + nivelAtual, pontuacao).apply();
        }

        // Calcular porcentagem
        int maximo = perguntas.size() * 10;
        double porcentagem = (pontuacao * 100.0) / maximo;
        
        String mensagem;
        if (porcentagem >= 80) {
            mensagem = "🏆 Excelente! Você é um mestre da Bíblia!";
        } else if (porcentagem >= 60) {
            mensagem = "👍 Muito bom! Continue estudando!";
        } else if (porcentagem >= 40) {
            mensagem = "📖 Você está no caminho! Leia mais a Palavra.";
        } else {
            mensagem = "💪 Continue tentando! A prática leva à perfeição.";
        }
        
        if (pontuacao > recorde) {
            mensagem += "\n\n🎉 NOVO RECORDE!";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quiz Finalizado!");
        builder.setMessage("Sua pontuação: " + pontuacao + "/" + maximo + "\n" +
                          "Acertos: " + (pontuacao/10) + "/" + perguntas.size() + "\n\n" +
                          mensagem + "\n\nRecorde anterior: " + recorde);
        
        builder.setPositiveButton("Jogar Novamente", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                recreate();
            }
        });
        
        builder.setNegativeButton("Voltar ao Menu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        
        builder.setCancelable(false);
        builder.show();
    }
}