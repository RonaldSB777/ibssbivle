package com.ibscc.bible;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class ChapterActivity extends Activity {

    String livro, abrev;
    int totalCap;
    SharedPreferences prefs;
    boolean modoNoturno = false;
    int tamanhoFonte = 18;

    // CORES
    int COR_FUNDO_CLARO = Color.parseColor("#E3F2FD");
    int COR_TOOLBAR_CLARO = Color.parseColor("#1976D2");
    int COR_ITEM_CLARO = Color.parseColor("#BBDEFB");
    int COR_TEXTO_CLARO = Color.parseColor("#333333");

    int COR_FUNDO_ESCURO = Color.parseColor("#000000");
    int COR_TOOLBAR_ESCURO = Color.parseColor("#1A1A1A");
    int COR_ITEM_ESCURO = Color.parseColor("#333333");
    int COR_TEXTO_ESCURO = Color.parseColor("#FFFFFF");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);

        prefs = getSharedPreferences("BibliaPrefs", MODE_PRIVATE);
        modoNoturno = prefs.getBoolean("modoNoturno", false);
        tamanhoFonte = prefs.getInt("tamanhoFonte", 18);

        livro = getIntent().getStringExtra("livro");
        abrev = getIntent().getStringExtra("abrev");

        TextView titulo = findViewById(R.id.txtNomeLivro);
        titulo.setText("📖 " + livro);

        BibleHelper bibleHelper = new BibleHelper(this);
        totalCap = bibleHelper.getChapterCount(abrev);

        String[] caps = new String[totalCap];
        for (int i = 0; i < totalCap; i++) {
            caps[i] = String.valueOf(i + 1);
        }

        GridView grid = findViewById(R.id.gridCapitulos);

        final int corTexto = modoNoturno ? COR_TEXTO_ESCURO : COR_TEXTO_CLARO;
        final int corFundo = modoNoturno ? COR_ITEM_ESCURO : COR_ITEM_CLARO;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, caps) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(corTexto);
                text.setTextSize(tamanhoFonte);
                text.setGravity(android.view.Gravity.CENTER);
                text.setPadding(16, 24, 16, 24);
                view.setBackgroundColor(corFundo);
                return view;
            }
        };

        grid.setAdapter(adapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ChapterActivity.this, VerseActivity.class);
                intent.putExtra("livro", livro);
                intent.putExtra("abrev", abrev);
                intent.putExtra("capitulo", position + 1);
                intent.putExtra("totalCapitulos", totalCap);
                startActivity(intent);
            }
        });

        aplicarTema(titulo, grid);
    }

    private void aplicarTema(TextView titulo, GridView grid) {
        View root = findViewById(android.R.id.content);
        
        if (modoNoturno) {
            root.setBackgroundColor(COR_FUNDO_ESCURO);
            titulo.setBackgroundColor(COR_TOOLBAR_ESCURO);
            titulo.setTextColor(COR_TEXTO_ESCURO);
            grid.setBackgroundColor(COR_FUNDO_ESCURO);
        } else {
            root.setBackgroundColor(COR_FUNDO_CLARO);
            titulo.setBackgroundColor(COR_TOOLBAR_CLARO);
            titulo.setTextColor(Color.WHITE);
            grid.setBackgroundColor(COR_FUNDO_CLARO);
        }
    }
}