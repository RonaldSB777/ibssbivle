package com.ibscc.bible;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

public class VerseActivity extends Activity {

    LinearLayout rootLayout, toolbarVerse, navBar;
    TextView txtTitulo;
    ListView listVersiculos;
    Button btnAnterior, btnProximo;
    ImageButton btnVoltar, btnCompartilhar;

    String livro, abrev;
    int capitulo, totalCapitulos;

    List<Verse> verses;
    VerseAdapter adapter;

    BibleHelper bibleHelper;
    FavoritesHelper favoritesHelper;
    NotesHelper notesHelper;
    SharedPreferences prefs;

    boolean modoNoturno = false;
    int tamanhoFonte = 18;

    // CORES
    int COR_FUNDO_CLARO = Color.parseColor("#E3F2FD");
    int COR_TOOLBAR_CLARO = Color.parseColor("#1976D2");
    int COR_NAV_CLARO = Color.parseColor("#BBDEFB");
    int COR_BOTAO_CLARO = Color.parseColor("#1976D2");

    int COR_FUNDO_ESCURO = Color.parseColor("#000000");
    int COR_TOOLBAR_ESCURO = Color.parseColor("#1A1A1A");
    int COR_NAV_ESCURO = Color.parseColor("#1A1A1A");
    int COR_BOTAO_ESCURO = Color.parseColor("#333333");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verse);

        prefs = getSharedPreferences("BibliaPrefs", MODE_PRIVATE);
        modoNoturno = prefs.getBoolean("modoNoturno", false);
        tamanhoFonte = prefs.getInt("tamanhoFonte", 18);

        livro = getIntent().getStringExtra("livro");
        abrev = getIntent().getStringExtra("abrev");
        capitulo = getIntent().getIntExtra("capitulo", 1);
        totalCapitulos = getIntent().getIntExtra("totalCapitulos", 1);

        rootLayout = findViewById(R.id.rootLayout);
        toolbarVerse = findViewById(R.id.toolbarVerse);
        navBar = findViewById(R.id.navBar);
        txtTitulo = findViewById(R.id.txtTitulo);
        listVersiculos = findViewById(R.id.listVersiculos);
        btnAnterior = findViewById(R.id.btnAnterior);
        btnProximo = findViewById(R.id.btnProximo);
        btnVoltar = findViewById(R.id.btnVoltar);
        btnCompartilhar = findViewById(R.id.btnCompartilhar);

        bibleHelper = new BibleHelper(this);
        favoritesHelper = new FavoritesHelper(this);
        notesHelper = new NotesHelper(this);

        configurarBotoes();
        carregarCapitulo();
        aplicarTema();
    }

    private void configurarBotoes() {
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnCompartilhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compartilharCapitulo();
            }
        });

        btnAnterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (capitulo > 1) {
                    capitulo--;
                    carregarCapitulo();
                }
            }
        });

        btnProximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (capitulo < totalCapitulos) {
                    capitulo++;
                    carregarCapitulo();
                }
            }
        });

        listVersiculos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Verse verse = verses.get(position);
                mostrarOpcoesVersiculo(verse);
            }
        });

        listVersiculos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Verse verse = verses.get(position);
                toggleFavorito(verse);
                return true;
            }
        });
    }

    private void mostrarOpcoesVersiculo(final Verse verse) {
        boolean isFav = favoritesHelper.isFavorite(abrev, capitulo, verse.getNumber());
        String notaAtual = notesHelper.getNote(abrev, capitulo, verse.getNumber());
        boolean temNota = !notaAtual.isEmpty();
        
        String[] opcoes;
        if (isFav) {
            opcoes = new String[]{"📤 Compartilhar", "⭐ Remover dos Favoritos", temNota ? "📝 Editar Anotação" : "📝 Adicionar Anotação", "📋 Copiar"};
        } else {
            opcoes = new String[]{"📤 Compartilhar", "⭐ Adicionar aos Favoritos", temNota ? "📝 Editar Anotação" : "📝 Adicionar Anotação", "📋 Copiar"};
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(livro + " " + capitulo + ":" + verse.getNumber());
        builder.setItems(opcoes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: compartilharVersiculo(verse); break;
                    case 1: toggleFavorito(verse); break;
                    case 2: mostrarDialogoAnotacao(verse); break;
                    case 3: copiarVersiculo(verse); break;
                }
            }
        });
        builder.show();
    }

    private void toggleFavorito(Verse verse) {
        if (favoritesHelper.isFavorite(abrev, capitulo, verse.getNumber())) {
            favoritesHelper.removeFavorite(abrev, capitulo, verse.getNumber());
            Toast.makeText(this, "Removido dos Favoritos", Toast.LENGTH_SHORT).show();
        } else {
            favoritesHelper.addFavorite(livro, abrev, capitulo, verse.getNumber(), verse.getText());
            Toast.makeText(this, "⭐ Adicionado aos Favoritos!", Toast.LENGTH_SHORT).show();
        }
        adapter.atualizarDados();
    }

    private void mostrarDialogoAnotacao(final Verse verse) {
        String notaAtual = notesHelper.getNote(abrev, capitulo, verse.getNumber());
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("📝 Anotação");
        
        final EditText input = new EditText(this);
        input.setText(notaAtual);
        input.setHint("Escreva sua reflexão...");
        input.setPadding(32, 32, 32, 32);
        builder.setView(input);
        
        builder.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String texto = input.getText().toString();
                notesHelper.saveNote(abrev, capitulo, verse.getNumber(), texto);
                Toast.makeText(VerseActivity.this, "💾 Anotação salva!", Toast.LENGTH_SHORT).show();
                adapter.atualizarDados();
            }
        });
        
        builder.setNegativeButton("Cancelar", null);
        
        if (!notaAtual.isEmpty()) {
            builder.setNeutralButton("Excluir", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    notesHelper.saveNote(abrev, capitulo, verse.getNumber(), "");
                    Toast.makeText(VerseActivity.this, "Anotação removida", Toast.LENGTH_SHORT).show();
                    adapter.atualizarDados();
                }
            });
        }
        
        builder.show();
    }

    private void compartilharVersiculo(Verse verse) {
        String texto = livro + " " + capitulo + ":" + verse.getNumber() + "\n\n\"" + verse.getText() + "\"\n\n📖 Bíblia Sagrada - App";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, texto);
        startActivity(Intent.createChooser(intent, "Compartilhar via"));
    }

    private void copiarVersiculo(Verse verse) {
        String texto = livro + " " + capitulo + ":" + verse.getNumber() + " - " + verse.getText();
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Versículo", texto);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "📋 Versículo copiado!", Toast.LENGTH_SHORT).show();
    }

    private void compartilharCapitulo() {
        StringBuilder texto = new StringBuilder();
        texto.append(livro).append(" ").append(capitulo).append("\n\n");
        for (Verse verse : verses) {
            texto.append(verse.getNumber()).append(" ").append(verse.getText()).append("\n");
        }
        texto.append("\n📖 Bíblia Sagrada - App");

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, texto.toString());
        startActivity(Intent.createChooser(intent, "Compartilhar via"));
    }

    private void carregarCapitulo() {
        txtTitulo.setText(livro + " " + capitulo);
        verses = bibleHelper.getChapter(abrev, capitulo);
        adapter = new VerseAdapter(this, verses, livro, abrev, capitulo, modoNoturno, tamanhoFonte);
        listVersiculos.setAdapter(adapter);
        atualizarBotoes();
        listVersiculos.setSelection(0);
    }

    private void atualizarBotoes() {
        btnAnterior.setEnabled(capitulo > 1);
        btnAnterior.setAlpha(capitulo > 1 ? 1f : 0.5f);
        btnProximo.setEnabled(capitulo < totalCapitulos);
        btnProximo.setAlpha(capitulo < totalCapitulos ? 1f : 0.5f);
    }

    private void aplicarTema() {
        int corFundo = modoNoturno ? COR_FUNDO_ESCURO : COR_FUNDO_CLARO;
        int corToolbar = modoNoturno ? COR_TOOLBAR_ESCURO : COR_TOOLBAR_CLARO;
        int corNav = modoNoturno ? COR_NAV_ESCURO : COR_NAV_CLARO;
        int corBotao = modoNoturno ? COR_BOTAO_ESCURO : COR_BOTAO_CLARO;

        rootLayout.setBackgroundColor(corFundo);
        toolbarVerse.setBackgroundColor(corToolbar);
        navBar.setBackgroundColor(corNav);
        listVersiculos.setBackgroundColor(corFundo);
        txtTitulo.setTextColor(Color.WHITE);
        btnAnterior.setBackgroundColor(corBotao);
        btnAnterior.setTextColor(Color.WHITE);
        btnProximo.setBackgroundColor(corBotao);
        btnProximo.setTextColor(Color.WHITE);

        if (adapter != null) {
            adapter.atualizarTema(modoNoturno, tamanhoFonte);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        modoNoturno = prefs.getBoolean("modoNoturno", false);
        tamanhoFonte = prefs.getInt("tamanhoFonte", 18);
        aplicarTema();
        if (adapter != null) {
            adapter.atualizarDados();
        }
    }
}