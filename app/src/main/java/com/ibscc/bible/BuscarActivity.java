package com.ibscc.bible;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class BuscarActivity extends AppCompatActivity {

    private EditText edtBuscaAvancada;
    private ImageButton btnVoltar, btnLimpar;
    private Button btnBuscar;
    private Spinner spinnerTestamento, spinnerLivro;
    private RadioGroup radioGroupTipoBusca;
    private RecyclerView recyclerResultados;
    private TextView txtTotalResultados;
    private View layoutResultados, layoutVazio;
    
    private BuscaAvancadaHelper buscaHelper;
    private ResultadosBuscaAdapter adapter;
    private List<ResultadoBusca> resultados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);
        
        buscaHelper = new BuscaAvancadaHelper(this);
        resultados = new ArrayList<>();
        
        inicializarViews();
        configurarSpinners();
        configurarListeners();
    }

    private void inicializarViews() {
        btnVoltar = findViewById(R.id.btnVoltar);
        edtBuscaAvancada = findViewById(R.id.edtBuscaAvancada);
        btnLimpar = findViewById(R.id.btnLimpar);
        btnBuscar = findViewById(R.id.btnBuscar);
        spinnerTestamento = findViewById(R.id.spinnerTestamento);
        spinnerLivro = findViewById(R.id.spinnerLivro);
        radioGroupTipoBusca = findViewById(R.id.radioGroupTipoBusca);
        recyclerResultados = findViewById(R.id.recyclerResultados);
        txtTotalResultados = findViewById(R.id.txtTotalResultados);
        layoutResultados = findViewById(R.id.layoutResultados);
        layoutVazio = findViewById(R.id.layoutVazio);
        
        recyclerResultados.setLayoutManager(new LinearLayoutManager(this));
    }

    private void configurarSpinners() {
        // Spinner Testamento
        String[] testamentos = {"Todos", "Antigo Testamento", "Novo Testamento"};
        ArrayAdapter<String> adapterTest = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, testamentos);
        adapterTest.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTestamento.setAdapter(adapterTest);
        
        // Spinner Livros (simplificado - você pode adicionar todos)
        String[] livros = {"Todos", "Genesis", "Exodo", "Salmos", "Proverbios", "Isaias", 
                          "Mateus", "Marcos", "Lucas", "Joao", "Atos", "Romanos"};
        ArrayAdapter<String> adapterLivro = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, livros);
        adapterLivro.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLivro.setAdapter(adapterLivro);
    }

    private void configurarListeners() {
        btnVoltar.setOnClickListener(v -> finish());
        
        // Mostrar/esconder botão limpar
        edtBuscaAvancada.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnLimpar.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        btnLimpar.setOnClickListener(v -> {
            edtBuscaAvancada.setText("");
            limparResultados();
        });
        
        btnBuscar.setOnClickListener(v -> realizarBusca());
        
        // Buscar ao pressionar Enter
        edtBuscaAvancada.setOnEditorActionListener((v, actionId, event) -> {
            realizarBusca();
            return true;
        });
    }

    private void realizarBusca() {
        String termo = edtBuscaAvancada.getText().toString().trim();
        
        if (termo.isEmpty()) {
            return;
        }
        
        // Pegar filtros
        String testamento = spinnerTestamento.getSelectedItem().toString();
        String livro = spinnerLivro.getSelectedItem().toString();
        boolean fraseExata = radioGroupTipoBusca.getCheckedRadioButtonId() == R.id.radioFraseExata;
        
        // Executar busca
        resultados = buscaHelper.buscar(termo, testamento, livro, fraseExata);
        
        // Mostrar resultados
        if (resultados.isEmpty()) {
            mostrarMensagemVazia();
        } else {
            mostrarResultados(termo);
        }
    }

    private void mostrarResultados(String termo) {
        layoutVazio.setVisibility(View.GONE);
        layoutResultados.setVisibility(View.VISIBLE);
        
        txtTotalResultados.setText(resultados.size() + " resultado" + (resultados.size() != 1 ? "s" : "") + " encontrado" + (resultados.size() != 1 ? "s" : ""));
        
        adapter = new ResultadosBuscaAdapter(this, resultados, termo);
        recyclerResultados.setAdapter(adapter);
    }

    private void mostrarMensagemVazia() {
        layoutResultados.setVisibility(View.GONE);
        layoutVazio.setVisibility(View.VISIBLE);
    }

    private void limparResultados() {
        resultados.clear();
        layoutResultados.setVisibility(View.GONE);
        layoutVazio.setVisibility(View.VISIBLE);
    }
}