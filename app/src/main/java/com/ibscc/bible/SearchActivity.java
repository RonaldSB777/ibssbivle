package com.ibscc.bible;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.view.View;
import java.util.List;

public class SearchActivity extends Activity {

    EditText editBuscar;
    Button btnBuscar;
    ListView listResultados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        editBuscar = findViewById(R.id.editBuscar);
        btnBuscar = findViewById(R.id.btnBuscar);
        listResultados = findViewById(R.id.listResultados);

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = editBuscar.getText().toString().trim();
                if (!query.isEmpty()) {
                    buscar(query);
                }
            }
        });
    }

    private void buscar(String query) {
        BibleHelper helper = new BibleHelper(this);
        List<String> resultados = helper.search(query);

        if (resultados.isEmpty()) {
            resultados.add("Nenhum resultado encontrado para: " + query);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_list_item_1,
            resultados
        );
        listResultados.setAdapter(adapter);
    }
}