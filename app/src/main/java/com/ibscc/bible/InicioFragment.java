package com.ibscc.bible;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.ibscc.bible.models.WeeklyEvent;
import com.ibscc.bible.services.FirebaseRESTService;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class InicioFragment extends Fragment {

    private TextView tvVersiculoDia, tvReferenciaDia;
    private TextView tvProgressoDias, tvProgressoPercentual;
    private ProgressBar progressBar;
    private LinearLayout layoutProximoEvento;
    private TextView tvProximoEventoTitulo, tvProximoEventoData;
    
    private CardView cardLerBiblia, cardBuscar, cardProgramacao, cardQuiz;
    
    private BibleHelper bibleHelper;
    private ReadingPlanHelper planHelper;
    private FirebaseRESTService firebaseService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);
        
        bibleHelper = new BibleHelper(getContext());
        planHelper = new ReadingPlanHelper(getContext());
        firebaseService = new FirebaseRESTService();
        
        initViews(view);
        carregarVersiculoDoDia();
        carregarProgresso();
        carregarProximoEvento();
        configurarBotoes();
        
        return view;
    }

    private void initViews(View view) {
        tvVersiculoDia = view.findViewById(R.id.tv_versiculo_dia);
        tvReferenciaDia = view.findViewById(R.id.tv_referencia_dia);
        tvProgressoDias = view.findViewById(R.id.tv_progresso_dias);
        tvProgressoPercentual = view.findViewById(R.id.tv_progresso_percentual);
        progressBar = view.findViewById(R.id.progress_bar_leitura);
        layoutProximoEvento = view.findViewById(R.id.layout_proximo_evento);
        tvProximoEventoTitulo = view.findViewById(R.id.tv_proximo_evento_titulo);
        tvProximoEventoData = view.findViewById(R.id.tv_proximo_evento_data);
        
        cardLerBiblia = view.findViewById(R.id.card_ler_biblia);
        cardBuscar = view.findViewById(R.id.card_buscar);
        cardProgramacao = view.findViewById(R.id.card_programacao);
        cardQuiz = view.findViewById(R.id.card_quiz);
    }

    private void carregarVersiculoDoDia() {
        Calendar cal = Calendar.getInstance();
        int seed = cal.get(Calendar.DAY_OF_YEAR) + cal.get(Calendar.YEAR);
        Random random = new Random(seed);

        List<String> livros = bibleHelper.getBookNames();
        List<String> abreviacoes = bibleHelper.getBookAbbreviations();

        int livroIndex = random.nextInt(livros.size());
        String abrev = abreviacoes.get(livroIndex);
        String livroNome = livros.get(livroIndex);

        int totalCap = bibleHelper.getChapterCount(abrev);
        if (totalCap == 0) return;

        int cap = random.nextInt(totalCap) + 1;
        List<Verse> verses = bibleHelper.getChapter(abrev, cap);

        if (verses.isEmpty()) return;

        int verseIndex = random.nextInt(verses.size());
        Verse verse = verses.get(verseIndex);

        tvVersiculoDia.setText("\"" + verse.getText() + "\"");
        tvReferenciaDia.setText("— " + livroNome + " " + cap + ":" + verse.getNumber());
    }

    private void carregarProgresso() {
        int progresso = planHelper.getProgress();
        int percentual = (progresso * 100) / 365;
        
        tvProgressoDias.setText(progresso + "/365 dias");
        tvProgressoPercentual.setText(percentual + "%");
        progressBar.setProgress(percentual);
    }

    private void carregarProximoEvento() {
        firebaseService.loadEvents(new FirebaseRESTService.OnEventsLoadedListener() {
            @Override
            public void onEventsLoaded(List<WeeklyEvent> events) {
                if (events.isEmpty()) {
                    layoutProximoEvento.setVisibility(View.GONE);
                    return;
                }
                
                // Buscar próximo evento
                long agora = System.currentTimeMillis();
                WeeklyEvent proximo = null;
                
                for (WeeklyEvent event : events) {
                    if (event.getDateTimeMillis() > agora) {
                        if (proximo == null || event.getDateTimeMillis() < proximo.getDateTimeMillis()) {
                            proximo = event;
                        }
                    }
                }
                
                if (proximo != null) {
                    layoutProximoEvento.setVisibility(View.VISIBLE);
                    tvProximoEventoTitulo.setText(proximo.getIcon() + " " + proximo.getTitle());
                    tvProximoEventoData.setText(proximo.getDayName() + " • " + proximo.getFormattedTime());
                } else {
                    layoutProximoEvento.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String error) {
                layoutProximoEvento.setVisibility(View.GONE);
            }
        });
    }

    private void configurarBotoes() {
        cardLerBiblia.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).abrirBiblia();
            }
        });

        cardBuscar.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).abrirBusca();
            }
        });

        cardProgramacao.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).abrirProgramacao();
            }
        });

        cardQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), QuizMenuActivity.class);
            startActivity(intent);
        });
    }
}