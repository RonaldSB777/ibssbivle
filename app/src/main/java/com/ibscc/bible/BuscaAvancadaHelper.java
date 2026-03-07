package com.ibscc.bible;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class BuscaAvancadaHelper {
    
    private BibleHelper bibleHelper;
    private Context context;

    public BuscaAvancadaHelper(Context context) {
        this.context = context;
        this.bibleHelper = new BibleHelper(context);
    }

    /**
     * Busca avançada com filtros
     */
    public List<ResultadoBusca> buscar(String termo, String testamento, String livro, boolean fraseExata) {
        List<ResultadoBusca> resultados = new ArrayList<>();
        
        if (termo == null || termo.trim().isEmpty()) {
            return resultados;
        }

        // Busca inicial usando o método do BibleHelper
        List<String> resultadosBrutos = bibleHelper.search(termo);
        
        // Processar e filtrar resultados
        for (String resultado : resultadosBrutos) {
            try {
                // Formato esperado: "Livro cap:vers\nTexto"
                String[] partes = resultado.split("\n", 2);
                if (partes.length < 2) continue;
                
                String referencia = partes[0]; // Ex: "Gênesis 1:1"
                String texto = partes[1];
                
                // Extrair livro, capítulo e versículo
                String[] refPartes = referencia.split(" ");
                if (refPartes.length < 2) continue;
                
                // Nome do livro (pode ter espaços, ex: "1 Samuel")
                String nomeLivro = referencia.substring(0, referencia.lastIndexOf(" "));
                
                // Capítulo:Versículo
                String capVers = refPartes[refPartes.length - 1];
                String[] cv = capVers.split(":");
                if (cv.length < 2) continue;
                
                int capitulo = Integer.parseInt(cv[0]);
                int versiculo = Integer.parseInt(cv[1]);
                
                // Aplicar filtros
                
                // Filtro de frase exata
                if (fraseExata) {
                    if (!texto.toLowerCase().contains(termo.toLowerCase())) {
                        continue;
                    }
                } else {
                    // Busca por qualquer palavra do termo
                    String[] palavras = termo.toLowerCase().split("\\s+");
                    boolean encontrou = false;
                    for (String palavra : palavras) {
                        if (texto.toLowerCase().contains(palavra)) {
                            encontrou = true;
                            break;
                        }
                    }
                    if (!encontrou) continue;
                }
                
                // Filtro de testamento
                if (!testamento.equals("Todos")) {
                    List<String> livrosTestamento = getLivrosDoTestamento(testamento);
                    boolean pertenceTestamento = false;
                    for (String livroAT_NT : livrosTestamento) {
                        if (nomeLivro.equals(bibleHelper.getBookName(livroAT_NT))) {
                            pertenceTestamento = true;
                            break;
                        }
                    }
                    if (!pertenceTestamento) continue;
                }
                
                // Filtro de livro específico
                if (!livro.equals("Todos") && !nomeLivro.equals(livro)) {
                    continue;
                }
                
                // Adicionar resultado válido
                resultados.add(new ResultadoBusca(nomeLivro, capitulo, versiculo, texto));
                
                // Limite de 500 resultados
                if (resultados.size() >= 500) break;
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return resultados;
    }
    
    private List<String> getLivrosDoTestamento(String testamento) {
        List<String> livros = new ArrayList<>();
        
        if (testamento.equals("Antigo Testamento")) {
            // Lista de livros do AT (abreviações)
            String[] at = {"gn", "ex", "lv", "nm", "dt", "js", "jz", "rt", "1sm", "2sm",
                          "1rs", "2rs", "1cr", "2cr", "ed", "ne", "et", "job", "sl", "pv",
                          "ec", "ct", "is", "jr", "lm", "ez", "dn", "os", "jl", "am",
                          "ob", "jn", "mq", "na", "hc", "sf", "ag", "zc", "ml"};
            for (String l : at) livros.add(l);
        } else if (testamento.equals("Novo Testamento")) {
            // Lista de livros do NT
            String[] nt = {"mt", "mc", "lc", "jo", "at", "rm", "1co", "2co", "gl", "ef",
                          "fp", "cl", "1ts", "2ts", "1tm", "2tm", "tt", "fm", "hb", "tg",
                          "1pe", "2pe", "1jo", "2jo", "3jo", "jd", "ap"};
            for (String l : nt) livros.add(l);
        }
        
        return livros;
    }
}