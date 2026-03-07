package com.ibscc.bible;

public class ResultadoBusca {
    private String livro;
    private int capitulo;
    private int versiculo;
    private String texto;
    private String referencia;

    public ResultadoBusca(String livro, int capitulo, int versiculo, String texto) {
        this.livro = livro;
        this.capitulo = capitulo;
        this.versiculo = versiculo;
        this.texto = texto;
        this.referencia = livro + " " + capitulo + ":" + versiculo;
    }

    public String getLivro() { return livro; }
    public int getCapitulo() { return capitulo; }
    public int getVersiculo() { return versiculo; }
    public String getTexto() { return texto; }
    public String getReferencia() { return referencia; }
}