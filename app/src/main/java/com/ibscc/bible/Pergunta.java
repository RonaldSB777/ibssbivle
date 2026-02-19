package com.ibscc.bible;

public class Pergunta {
    private String pergunta;
    private String[] opcoes;
    private int respostaCorreta; // 0, 1, 2 ou 3
    private String nivel; // "facil", "medio", "dificil"
    private String referencia; // Versículo de referência (opcional)

    public Pergunta(String pergunta, String[] opcoes, int respostaCorreta, String nivel, String referencia) {
        this.pergunta = pergunta;
        this.opcoes = opcoes;
        this.respostaCorreta = respostaCorreta;
        this.nivel = nivel;
        this.referencia = referencia;
    }

    public String getPergunta() { return pergunta; }
    public String[] getOpcoes() { return opcoes; }
    public int getRespostaCorreta() { return respostaCorreta; }
    public String getNivel() { return nivel; }
    public String getReferencia() { return referencia; }
}