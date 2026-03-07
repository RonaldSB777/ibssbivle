package com.ibscc.bible;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ResultadosBuscaAdapter extends RecyclerView.Adapter<ResultadosBuscaAdapter.ViewHolder> {

    private Context context;
    private List<ResultadoBusca> resultados;
    private String termoBusca;
    private BibleHelper bibleHelper;  // ADICIONADO

    public ResultadosBuscaAdapter(Context context, List<ResultadoBusca> resultados, String termoBusca) {
        this.context = context;
        this.resultados = resultados;
        this.termoBusca = termoBusca.toLowerCase();
        this.bibleHelper = new BibleHelper(context);  // ADICIONADO
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_resultado_busca, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ResultadoBusca resultado = resultados.get(position);
        
        holder.tvReferencia.setText(resultado.getReferencia());
        
        // Destacar palavra buscada
        String texto = resultado.getTexto();
        SpannableString spannableString = new SpannableString(texto);
        
        String textoLower = texto.toLowerCase();
        int startPos = 0;
        while ((startPos = textoLower.indexOf(termoBusca, startPos)) >= 0) {
            spannableString.setSpan(
                new BackgroundColorSpan(0xFFFFEB3B), // Amarelo
                startPos,
                startPos + termoBusca.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            startPos += termoBusca.length();
        }
        
        holder.tvTextoVersiculo.setText(spannableString);
        
        // Clicar = abrir versículo
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pegar o nome do livro
                String nomeLivro = resultado.getLivro();
                
                // Converter nome para abreviação
                String abreviacao = bibleHelper.getBookAbbreviation(nomeLivro);
                
                // Obter total de capítulos do livro
                int totalCapitulos = bibleHelper.getChapterCount(abreviacao);
                
                // Criar Intent com TODOS os parâmetros necessários
                Intent intent = new Intent(context, VerseActivity.class);
                intent.putExtra("livro", nomeLivro);                    // Nome completo
                intent.putExtra("abrev", abreviacao);                   // Abreviação
                intent.putExtra("capitulo", resultado.getCapitulo());   // Número do capítulo
                intent.putExtra("totalCapitulos", totalCapitulos);      // Total de capítulos
                
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return resultados.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvReferencia, tvTextoVersiculo;

        ViewHolder(View itemView) {
            super(itemView);
            tvReferencia = itemView.findViewById(R.id.tvReferencia);
            tvTextoVersiculo = itemView.findViewById(R.id.tvTextoVersiculo);
        }
    }
}