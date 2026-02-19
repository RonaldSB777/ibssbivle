package com.ibscc.bible;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

public class VerseAdapter extends BaseAdapter {

    private Context context;
    private List<Verse> verses;
    private FavoritesHelper favoritesHelper;
    private String abrev;
    private int capitulo;
    private String livro;
    private boolean modoNoturno;
    private int tamanhoFonte;

    public VerseAdapter(Context context, List<Verse> verses, String livro, String abrev, int capitulo, boolean modoNoturno, int tamanhoFonte) {
        this.context = context;
        this.verses = verses;
        this.livro = livro;
        this.abrev = abrev;
        this.capitulo = capitulo;
        this.modoNoturno = modoNoturno;
        this.tamanhoFonte = tamanhoFonte;
        this.favoritesHelper = new FavoritesHelper(context);
    }

    @Override
    public int getCount() {
        return verses.size();
    }

    @Override
    public Object getItem(int position) {
        return verses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_verse, parent, false);
        }

        Verse verse = verses.get(position);

        TextView txtNumero = convertView.findViewById(R.id.txtNumero);
        TextView txtTexto = convertView.findViewById(R.id.txtTexto);
        TextView txtFavorito = convertView.findViewById(R.id.txtFavorito);

        txtNumero.setText(String.valueOf(verse.getNumber()));
        txtTexto.setText(verse.getText());
        txtTexto.setTextSize(tamanhoFonte);

        // Verificar se é favorito
        boolean isFav = favoritesHelper.isFavorite(abrev, capitulo, verse.getNumber());
        txtFavorito.setText(isFav ? "⭐" : "");

        // Aplicar cores do tema
        if (modoNoturno) {
            convertView.setBackgroundColor(Color.parseColor("#000000"));
            txtNumero.setTextColor(Color.parseColor("#64B5F6"));
            txtTexto.setTextColor(Color.WHITE);
        } else {
            convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            txtNumero.setTextColor(Color.parseColor("#1976D2"));
            txtTexto.setTextColor(Color.parseColor("#333333"));
        }

        return convertView;
    }

    public void atualizarTema(boolean modoNoturno, int tamanhoFonte) {
        this.modoNoturno = modoNoturno;
        this.tamanhoFonte = tamanhoFonte;
        notifyDataSetChanged();
    }

    public void atualizarDados() {
        notifyDataSetChanged();
    }
}