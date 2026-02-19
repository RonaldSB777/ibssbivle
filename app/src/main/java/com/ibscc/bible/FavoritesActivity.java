package com.ibscc.bible;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import java.util.List;

public class FavoritesActivity extends Activity {

    ListView listFavoritos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        listFavoritos = findViewById(R.id.listFavoritos);
        carregarFavoritos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarFavoritos();
    }

    private void carregarFavoritos() {
        List<String> favoritos = PreferencesHelper.getFavorites(this);

        if (favoritos.isEmpty()) {
            favoritos.add("Nenhum favorito ainda.\n\nAbra um versículo e clique na estrela ⭐ para salvar.");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_list_item_1,
            favoritos
        );
        listFavoritos.setAdapter(adapter);
    }
}