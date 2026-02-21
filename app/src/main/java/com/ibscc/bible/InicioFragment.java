package com.ibscc.bible;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class InicioFragment extends Fragment {

    private TextView txtSaudacao, txtVersiculoInicio, txtReferenciaInicio;
    private Button btnAcessoBiblia, btnAcessoBuscar, btnAcessoFavoritos, btnAcessoProgramacao;
    private Button btnVersiculoDia, btnQuiz;

    private BibleHelper bibleHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        // Inicializar helper
        bibleHelper = new BibleHelper(getContext());

        // Inicializar views
        inicializarViews(view);

        // Configurar saudação
        configurarSaudacao();

        // Configurar versículo do dia
        configurarVersiculoDoDia();

        // Configurar botões de acesso rápido
        configurarBotoes();

        return view;
    }

    private void inicializarViews(View view) {
        txtSaudacao = view.findViewById(R.id.txtSaudacao);
        txtVersiculoInicio = view.findViewById(R.id.txtVersiculoInicio);
        txtReferenciaInicio = view.findViewById(R.id.txtReferenciaInicio);

        btnAcessoBiblia = view.findViewById(R.id.btnAcessoBiblia);
        btnAcessoBuscar = view.findViewById(R.id.btnAcessoBuscar);
        btnAcessoFavoritos = view.findViewById(R.id.btnAcessoFavoritos);
        btnAcessoProgramacao = view.findViewById(R.id.btnAcessoProgramacao);
        btnVersiculoDia = view.findViewById(R.id.btnVersiculoDia);
        btnQuiz = view.findViewById(R.id.btnQuiz);
    }

    private void configurarSaudacao() {
        Calendar calendar = Calendar.getInstance();
        int hora = calendar.get(Calendar.HOUR_OF_DAY);

        String saudacao;
        String emoji;

        if (hora >= 5 && hora < 12) {
            saudacao = "Bom dia";
            emoji = "🌅";
        } else if (hora >= 12 && hora < 18) {
            saudacao = "Boa tarde";
            emoji = "☀️";
        } else {
            saudacao = "Boa noite";
            emoji = "🌙";
        }

        txtSaudacao.setText(emoji + " " + saudacao + "!");
    }

    private void configurarVersiculoDoDia() {
        try {
            List<String> livros = bibleHelper.getBookNames();
            List<String> abreviacoes = bibleHelper.getBookAbbreviations();

            if (livros == null || livros.isEmpty()) {
                txtVersiculoInicio.setText("\"O Senhor é o meu pastor; nada me faltará.\"");
                txtReferenciaInicio.setText("— Salmos 23:1");
                return;
            }

            // Usar o dia do ano como seed para ter o mesmo versículo o dia todo
            Calendar cal = Calendar.getInstance();
            int seed = cal.get(Calendar.DAY_OF_YEAR) + cal.get(Calendar.YEAR);
            Random random = new Random(seed);

            int livroIndex = random.nextInt(livros.size());
            String abrev = abreviacoes.get(livroIndex);
            String livroNome = livros.get(livroIndex);

            int totalCap = bibleHelper.getChapterCount(abrev);
            if (totalCap == 0) {
                txtVersiculoInicio.setText("\"O Senhor é o meu pastor; nada me faltará.\"");
                txtReferenciaInicio.setText("— Salmos 23:1");
                return;
            }

            int cap = random.nextInt(totalCap) + 1;
            List<Verse> verses = bibleHelper.getChapter(abrev, cap);

            if (verses == null || verses.isEmpty()) {
                txtVersiculoInicio.setText("\"O Senhor é o meu pastor; nada me faltará.\"");
                txtReferenciaInicio.setText("— Salmos 23:1");
                return;
            }

            int verseIndex = random.nextInt(verses.size());
            Verse verse = verses.get(verseIndex);

            txtVersiculoInicio.setText("\"" + verse.getText() + "\"");
            txtReferenciaInicio.setText("— " + livroNome + " " + cap + ":" + verse.getNumber());

        } catch (Exception e) {
            txtVersiculoInicio.setText("\"O Senhor é o meu pastor; nada me faltará.\"");
            txtReferenciaInicio.setText("— Salmos 23:1");
        }
    }

    private void configurarBotoes() {
        // Botão Bíblia
        btnAcessoBiblia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).abrirBiblia();
                }
            }
        });

        // Botão Buscar
        btnAcessoBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).abrirBusca();
                }
            }
        });

        // Botão Favoritos
        btnAcessoFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof MainActivity) {
                    MainActivity activity = (MainActivity) getActivity();
                    activity.esconderTodasTelas();
                    activity.layoutFavoritos.setVisibility(View.VISIBLE);
                    activity.txtToolbarTitle.setText("⭐ Favoritos");
                    activity.carregarFavoritos();
                }
            }
        });

        // Botão Programação
        btnAcessoProgramacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).abrirProgramacao();
                }
            }
        });

        // Botão Versículo do Dia
        btnVersiculoDia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof MainActivity) {
                    MainActivity activity = (MainActivity) getActivity();
                    activity.esconderTodasTelas();
                    activity.layoutVersiculoDia.setVisibility(View.VISIBLE);
                    activity.txtToolbarTitle.setText("🔔 Versículo do Dia");
                    activity.gerarVersiculoDoDia();
                }
            }
        });

        // Botão Quiz
        btnQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    android.content.Intent intent = new android.content.Intent(getActivity(), QuizMenuActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}