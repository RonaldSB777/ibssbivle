package com.ibscc.bible;

import com.google.android.material.navigation.NavigationView;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AlertDialog;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends FragmentActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageButton btnMenu;
    TextView txtToolbarTitle;
    LinearLayout mainContent, toolbar;

    LinearLayout layoutBiblia, layoutBuscar, layoutFavoritos, layoutVersiculoDia;
    LinearLayout layoutProgramacao;
    
    ListView listLivros, listResultados, listFavoritos;
    List<String> livros, abreviacoes;
    BibleHelper bibleHelper;
    FavoritesHelper favoritesHelper;
    
    EditText edtBusca;
    Button btnPesquisar;
    
    TextView txtVersiculoDia, txtReferenciaDia;
    Button btnCompartilharDia;

    SharedPreferences prefs;
    boolean modoNoturno = false;
    int tamanhoFonte = 18;

    // MENU SECRETO
    private int toquesTitulo = 0;
    private long ultimoToque = 0;

    int COR_FUNDO_CLARO = Color.parseColor("#E3F2FD");
    int COR_TOOLBAR_CLARO = Color.parseColor("#1976D2");
    int COR_ITEM_CLARO = Color.parseColor("#FFFFFF");
    int COR_TEXTO_CLARO = Color.parseColor("#333333");
    int COR_BOTAO_CLARO = Color.parseColor("#1976D2");

    int COR_FUNDO_ESCURO = Color.parseColor("#000000");
    int COR_TOOLBAR_ESCURO = Color.parseColor("#1A1A1A");
    int COR_ITEM_ESCURO = Color.parseColor("#1A1A1A");
    int COR_TEXTO_ESCURO = Color.parseColor("#FFFFFF");
    int COR_BOTAO_ESCURO = Color.parseColor("#333333");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("BibliaPrefs", MODE_PRIVATE);
        modoNoturno = prefs.getBoolean("modoNoturno", false);
        tamanhoFonte = prefs.getInt("tamanhoFonte", 18);

        bibleHelper = new BibleHelper(this);
        favoritesHelper = new FavoritesHelper(this);

        inicializarViews();
        configurarDrawer();
        configurarBiblia();
        configurarBusca();
        configurarVersiculoDia();
        aplicarTema();
        
        DailyNotificationHelper.setupDailyNotification(this);
    }

    private void inicializarViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnMenu = findViewById(R.id.btnMenu);
        txtToolbarTitle = findViewById(R.id.txtToolbarTitle);
        mainContent = findViewById(R.id.mainContent);
        toolbar = findViewById(R.id.toolbar);

        layoutBiblia = findViewById(R.id.layoutBiblia);
        layoutBuscar = findViewById(R.id.layoutBuscar);
        layoutFavoritos = findViewById(R.id.layoutFavoritos);
        layoutVersiculoDia = findViewById(R.id.layoutVersiculoDia);
        layoutProgramacao = findViewById(R.id.layoutProgramacao);

        listLivros = findViewById(R.id.listLivros);
        edtBusca = findViewById(R.id.edtBusca);
        btnPesquisar = findViewById(R.id.btnPesquisar);
        listResultados = findViewById(R.id.listResultados);
        listFavoritos = findViewById(R.id.listFavoritos);

        txtVersiculoDia = findViewById(R.id.txtVersiculoDia);
        txtReferenciaDia = findViewById(R.id.txtReferenciaDia);
        btnCompartilharDia = findViewById(R.id.btnCompartilharDia);

        // MENU SECRETO - 5 TOQUES NO TÍTULO
        txtToolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long agora = System.currentTimeMillis();
                
                if (agora - ultimoToque > 2000) {
                    toquesTitulo = 0;
                }
                
                ultimoToque = agora;
                toquesTitulo++;
                
                if (toquesTitulo >= 3 && toquesTitulo < 5) {
                    Toast.makeText(MainActivity.this, 
                        "Mais " + (5 - toquesTitulo) + " toques...", 
                        Toast.LENGTH_SHORT).show();
                }
                
                if (toquesTitulo >= 5) {
                    toquesTitulo = 0;
                    verificarSenhaAdmin();
                }
            }
        });
    }

    private void configurarDrawer() {
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(navigationView);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                drawerLayout.closeDrawers();
                esconderTodasTelas();

                int id = item.getItemId();

                if (id == R.id.nav_biblia) {
                    layoutBiblia.setVisibility(View.VISIBLE);
                    txtToolbarTitle.setText("📖 Bíblia Sagrada");
                } else if (id == R.id.nav_buscar) {
                    layoutBuscar.setVisibility(View.VISIBLE);
                    txtToolbarTitle.setText("🔎 Buscar");
                } else if (id == R.id.nav_favoritos) {
                    layoutFavoritos.setVisibility(View.VISIBLE);
                    txtToolbarTitle.setText("⭐ Favoritos");
                    carregarFavoritos();
                } else if (id == R.id.nav_versiculo_dia) {
                    layoutVersiculoDia.setVisibility(View.VISIBLE);
                    txtToolbarTitle.setText("🔔 Versículo do Dia");
                    gerarVersiculoDoDia();
                } else if (id == R.id.nav_programacao) {
                    layoutProgramacao.setVisibility(View.VISIBLE);
                    txtToolbarTitle.setText("📅 Programação");
                    carregarProgramacao();
                } else if (id == R.id.nav_plano_leitura) {
                    mostrarPlanoLeitura();
                } else if (id == R.id.nav_quiz) {
                    Intent intent = new Intent(MainActivity.this, QuizMenuActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_modo_noturno) {
                    modoNoturno = !modoNoturno;
                    prefs.edit().putBoolean("modoNoturno", modoNoturno).apply();
                    aplicarTema();
                    configurarBiblia();
                    Toast.makeText(MainActivity.this, 
                        modoNoturno ? "🌙 Modo Noturno Ativado" : "☀️ Modo Claro Ativado", 
                        Toast.LENGTH_SHORT).show();
                    layoutBiblia.setVisibility(View.VISIBLE);
                } else if (id == R.id.nav_tamanho_fonte) {
                    mostrarDialogoFonte();
                    layoutBiblia.setVisibility(View.VISIBLE);
                }

                return true;
            }
        });
    }

    private void esconderTodasTelas() {
        layoutBiblia.setVisibility(View.GONE);
        layoutBuscar.setVisibility(View.GONE);
        layoutFavoritos.setVisibility(View.GONE);
        layoutVersiculoDia.setVisibility(View.GONE);
        layoutProgramacao.setVisibility(View.GONE);
    }

    private void carregarProgramacao() {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.layoutProgramacao, new ProgramacaoFragment())
            .commit();
    }

    private void verificarSenhaAdmin() {
        final EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setHint("Digite a senha");

        new AlertDialog.Builder(this)
            .setTitle("🔐 Acesso Restrito")
            .setMessage("Digite a senha de administrador:")
            .setView(input)
            .setPositiveButton("Entrar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String senha = input.getText().toString();
                    
                    // ALTERE A SENHA AQUI!
                    if (senha.equals("Gato2013@R")) {
                        Intent intent = new Intent(MainActivity.this, AdminProgramacaoActivity.class);
                        startActivity(intent);
                        Toast.makeText(MainActivity.this, "✅ Acesso liberado!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "❌ Senha incorreta!", Toast.LENGTH_LONG).show();
                    }
                }
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void configurarBiblia() {
        livros = bibleHelper.getBookNames();
        abreviacoes = bibleHelper.getBookAbbreviations();

        final int corTexto = modoNoturno ? COR_TEXTO_ESCURO : COR_TEXTO_CLARO;
        final int corFundo = modoNoturno ? COR_ITEM_ESCURO : COR_ITEM_CLARO;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, livros) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(corTexto);
                text.setTextSize(tamanhoFonte);
                view.setBackgroundColor(corFundo);
                return view;
            }
        };
        listLivros.setAdapter(adapter);
        listLivros.setBackgroundColor(corFundo);

        listLivros.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ChapterActivity.class);
                intent.putExtra("livro", livros.get(position));
                intent.putExtra("abrev", abreviacoes.get(position));
                startActivity(intent);
            }
        });
    }

    private void configurarBusca() {
        btnPesquisar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = edtBusca.getText().toString().trim();
                if (query.isEmpty()) return;
                
                Toast.makeText(MainActivity.this, "Pesquisando...", Toast.LENGTH_SHORT).show();
                List<String> resultados = bibleHelper.search(query);
                
                if (resultados.isEmpty()) {
                    resultados.add("Nenhum resultado encontrado.");
                }
                
                final int corTexto = modoNoturno ? COR_TEXTO_ESCURO : COR_TEXTO_CLARO;
                final int corFundo = modoNoturno ? COR_ITEM_ESCURO : COR_ITEM_CLARO;

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, resultados) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text = (TextView) view.findViewById(android.R.id.text1);
                        text.setTextColor(corTexto);
                        view.setBackgroundColor(corFundo);
                        return view;
                    }
                };
                listResultados.setAdapter(adapter);
                listResultados.setBackgroundColor(corFundo);
            }
        });
    }

    private void carregarFavoritos() {
        List<String> favoritos = favoritesHelper.getAllFavorites();
        if (favoritos.isEmpty()) {
            favoritos.add("Você ainda não tem favoritos ⭐");
        }

        final int corTexto = modoNoturno ? COR_TEXTO_ESCURO : COR_TEXTO_CLARO;
        final int corFundo = modoNoturno ? COR_ITEM_ESCURO : COR_ITEM_CLARO;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, favoritos) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(corTexto);
                view.setBackgroundColor(corFundo);
                return view;
            }
        };
        listFavoritos.setAdapter(adapter);
        listFavoritos.setBackgroundColor(corFundo);
    }

    private void configurarVersiculoDia() {
        btnCompartilharDia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String texto = txtVersiculoDia.getText().toString();
                String ref = txtReferenciaDia.getText().toString();
                compartilharTexto(texto + "\n\n" + ref + "\n\n📖 Bíblia Sagrada - App");
            }
        });
    }

    private void gerarVersiculoDoDia() {
        Calendar cal = Calendar.getInstance();
        int seed = cal.get(Calendar.DAY_OF_YEAR) + cal.get(Calendar.YEAR);
        java.util.Random random = new java.util.Random(seed);

        int livroIndex = random.nextInt(livros.size());
        String abrev = abreviacoes.get(livroIndex);
        String livroNome = livros.get(livroIndex);

        int totalCap = bibleHelper.getChapterCount(abrev);
        if (totalCap == 0) {
            txtVersiculoDia.setText("Erro ao carregar versículo.");
            return;
        }

        int cap = random.nextInt(totalCap) + 1;
        List<Verse> verses = bibleHelper.getChapter(abrev, cap);

        if (verses.isEmpty()) {
            txtVersiculoDia.setText("Erro ao carregar versículo.");
            return;
        }

        int verseIndex = random.nextInt(verses.size());
        Verse verse = verses.get(verseIndex);

        txtVersiculoDia.setText("\"" + verse.getText() + "\"");
        txtReferenciaDia.setText("— " + livroNome + " " + cap + ":" + verse.getNumber());
    }

    private void mostrarPlanoLeitura() {
        final ReadingPlanHelper planHelper = new ReadingPlanHelper(this);
        final int diaAtual = planHelper.getCurrentDay();
        final String[] leituraHoje = planHelper.getTodayReading();
        int progresso = planHelper.getProgress();
        boolean jaLeuHoje = planHelper.isDayRead(diaAtual);
        
        StringBuilder leituraTexto = new StringBuilder();
        leituraTexto.append("📅 Dia ").append(diaAtual + 1).append(" de 365\n\n");
        leituraTexto.append("📖 Leitura de Hoje:\n\n");
        
        if (leituraHoje.length >= 2) {
            String livroVT = bibleHelper.getBookName(leituraHoje[0]);
            leituraTexto.append("• ").append(livroVT).append(" ").append(leituraHoje[1]).append("\n");
        }
        
        if (leituraHoje.length >= 4) {
            String livroNT = bibleHelper.getBookName(leituraHoje[2]);
            leituraTexto.append("• ").append(livroNT).append(" ").append(leituraHoje[3]).append("\n");
        }
        
        leituraTexto.append("\n✅ Progresso Total: ").append(progresso).append("/365 dias");
        leituraTexto.append("\n").append(jaLeuHoje ? "✓ Você já leu hoje!" : "⏰ Leitura pendente");
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("📊 Plano de Leitura Bíblica Anual");
        builder.setMessage(leituraTexto.toString());
        
        builder.setPositiveButton("Marcar como Lido", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                planHelper.markAsRead(diaAtual);
                Toast.makeText(MainActivity.this, "✅ Dia " + (diaAtual + 1) + " marcado como lido!", Toast.LENGTH_SHORT).show();
            }
        });
        
        builder.setNegativeButton("Fechar", null);
        builder.show();
    }

    private void compartilharTexto(String texto) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, texto);
        startActivity(Intent.createChooser(intent, "Compartilhar via"));
    }

    private void mostrarDialogoFonte() {
        final String[] opcoes = {"Pequena (14)", "Normal (18)", "Grande (22)", "Muito Grande (26)"};
        final int[] tamanhos = {14, 18, 22, 26};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("🔤 Tamanho da Fonte");
        builder.setItems(opcoes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tamanhoFonte = tamanhos[which];
                prefs.edit().putInt("tamanhoFonte", tamanhoFonte).apply();
                configurarBiblia();
                Toast.makeText(MainActivity.this, "Fonte alterada!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    private void aplicarTema() {
        int corFundo = modoNoturno ? COR_FUNDO_ESCURO : COR_FUNDO_CLARO;
        int corToolbar = modoNoturno ? COR_TOOLBAR_ESCURO : COR_TOOLBAR_CLARO;
        int corTexto = modoNoturno ? COR_TEXTO_ESCURO : COR_TEXTO_CLARO;
        int corBotao = modoNoturno ? COR_BOTAO_ESCURO : COR_BOTAO_CLARO;

        mainContent.setBackgroundColor(corFundo);
        toolbar.setBackgroundColor(corToolbar);
        txtToolbarTitle.setTextColor(Color.WHITE);

        layoutBiblia.setBackgroundColor(corFundo);
        layoutBuscar.setBackgroundColor(corFundo);
        layoutFavoritos.setBackgroundColor(corFundo);
        layoutVersiculoDia.setBackgroundColor(corFundo);
        layoutProgramacao.setBackgroundColor(corFundo);

        txtVersiculoDia.setTextColor(corTexto);
        txtReferenciaDia.setTextColor(modoNoturno ? Color.parseColor("#AAAAAA") : Color.parseColor("#666666"));

        edtBusca.setTextColor(corTexto);
        edtBusca.setHintTextColor(modoNoturno ? Color.parseColor("#888888") : Color.parseColor("#999999"));
        edtBusca.setBackgroundColor(modoNoturno ? Color.parseColor("#333333") : Color.WHITE);

        btnPesquisar.setBackgroundColor(corBotao);
        btnPesquisar.setTextColor(Color.WHITE);

        btnCompartilharDia.setBackgroundColor(corBotao);
        btnCompartilharDia.setTextColor(Color.WHITE);

        navigationView.setBackgroundColor(modoNoturno ? COR_FUNDO_ESCURO : Color.WHITE);
        
        int corTextoMenu = modoNoturno ? Color.WHITE : Color.parseColor("#333333");
        navigationView.setItemTextColor(ColorStateList.valueOf(corTextoMenu));
        navigationView.setItemIconTintList(ColorStateList.valueOf(corTextoMenu));

        View headerView = navigationView.getHeaderView(0);
        if (headerView != null) {
            headerView.setBackgroundColor(corToolbar);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        modoNoturno = prefs.getBoolean("modoNoturno", false);
        tamanhoFonte = prefs.getInt("tamanhoFonte", 18);
        aplicarTema();
        configurarBiblia();
        carregarFavoritos();
    }
}