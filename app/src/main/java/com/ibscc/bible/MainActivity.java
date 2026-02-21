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

import com.ibscc.bible.utils.ThemeManager;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends FragmentActivity {

    // ============================================
    // VIEWS - Interface (PUBLIC para Fragments)
    // ============================================
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageButton btnMenu;
    public TextView txtToolbarTitle;
    LinearLayout mainContent;
    View toolbar;

    // Layouts de conteúdo (PUBLIC para Fragments)
    public LinearLayout layoutBiblia, layoutBuscar, layoutFavoritos;
    public View layoutVersiculoDia;
    public View layoutProgramacao;
    
    // Listas
    ListView listLivros, listResultados, listFavoritos;
    List<String> livros, abreviacoes;
    
    // Busca
    EditText edtBusca;
    Button btnPesquisar;
    
    // Versículo do Dia
    public TextView txtVersiculoDia, txtReferenciaDia;
    Button btnCompartilharDia;

    // ============================================
    // HELPERS E GERENCIADORES
    // ============================================
    BibleHelper bibleHelper;
    FavoritesHelper favoritesHelper;
    ThemeManager themeManager;
    
    // ============================================
    // PREFERÊNCIAS E CONFIGURAÇÕES
    // ============================================
    SharedPreferences prefs;
    boolean modoNoturno = false;
    int tamanhoFonte = 18;

    // Controle de toques no título (acesso admin)
    private int toquesTitulo = 0;
    private long ultimoToque = 0;

    // ============================================
    // CORES - TEMA CLARO
    // ============================================
    int COR_FUNDO_CLARO = Color.parseColor("#E3F2FD");
    int COR_TOOLBAR_CLARO = Color.parseColor("#1976D2");
    int COR_ITEM_CLARO = Color.parseColor("#FFFFFF");
    int COR_TEXTO_CLARO = Color.parseColor("#333333");
    int COR_BOTAO_CLARO = Color.parseColor("#1976D2");

    // ============================================
    // CORES - TEMA ESCURO
    // ============================================
    int COR_FUNDO_ESCURO = Color.parseColor("#000000");
    int COR_TOOLBAR_ESCURO = Color.parseColor("#1A1A1A");
    int COR_ITEM_ESCURO = Color.parseColor("#1A1A1A");
    int COR_TEXTO_ESCURO = Color.parseColor("#FFFFFF");
    int COR_BOTAO_ESCURO = Color.parseColor("#333333");

    // ============================================
    // MÉTODO PRINCIPAL - onCreate
    // ============================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            // Aplicar tema ANTES do super.onCreate()
            themeManager = new ThemeManager(this);
            themeManager.applyTheme();
            
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            // Carregar preferências
            prefs = getSharedPreferences("BibliaPrefs", MODE_PRIVATE);
            modoNoturno = prefs.getBoolean("modoNoturno", false);
            tamanhoFonte = prefs.getInt("tamanhoFonte", 18);

            // Inicializar helpers
            bibleHelper = new BibleHelper(this);
            favoritesHelper = new FavoritesHelper(this);

            // Configurar interface
            inicializarViews();
            configurarDrawer();
            configurarBiblia();
            configurarBusca();
            configurarVersiculoDia();
            aplicarTema();

            // Carregar tela inicial com Fragment
            View layoutInicio = findViewById(R.id.layoutInicio);
            if (layoutInicio != null) {
                esconderTodasTelas();
                layoutInicio.setVisibility(View.VISIBLE);
                txtToolbarTitle.setText("Biblia IBSCC");
                
                getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.layoutInicio, new InicioFragment())
                    .commit();
            } else {
                esconderTodasTelas();
                layoutBiblia.setVisibility(View.VISIBLE);
                txtToolbarTitle.setText("Biblia Sagrada");
            }
            
            // Configurar notificações diárias
            try {
                DailyNotificationHelper.setupDailyNotification(this);
            } catch (Exception e) {
                // Ignorar erro de notificação
            }
            
        } catch (Exception e) {
            new AlertDialog.Builder(this)
                .setTitle("ERRO AO INICIAR")
                .setMessage("Tipo: " + e.getClass().getSimpleName() + 
                           "\n\nMensagem: " + e.getMessage() +
                           "\n\nCausa: " + (e.getCause() != null ? e.getCause().getMessage() : "Desconhecida"))
                .setPositiveButton("OK", null)
                .show();
            e.printStackTrace();
        }
    }

    // ============================================
    // INICIALIZAÇÃO DE VIEWS
    // ============================================
    private void inicializarViews() {
        try {
            // Views principais
            drawerLayout = findViewById(R.id.drawerLayout);
            if (drawerLayout == null) throw new Exception("drawerLayout nao encontrado!");
            
            navigationView = findViewById(R.id.navigationView);
            if (navigationView == null) throw new Exception("navigationView nao encontrado!");
            
            btnMenu = findViewById(R.id.btnMenu);
            if (btnMenu == null) throw new Exception("btnMenu nao encontrado!");
            
            txtToolbarTitle = findViewById(R.id.txtToolbarTitle);
            if (txtToolbarTitle == null) throw new Exception("txtToolbarTitle nao encontrado!");
            
            mainContent = findViewById(R.id.mainContent);
            if (mainContent == null) throw new Exception("mainContent nao encontrado!");
            
            toolbar = findViewById(R.id.toolbar);
            if (toolbar == null) throw new Exception("toolbar nao encontrado!");

            // Layouts de conteúdo
            layoutBiblia = findViewById(R.id.layoutBiblia);
            if (layoutBiblia == null) throw new Exception("layoutBiblia nao encontrado!");
            
            layoutBuscar = findViewById(R.id.layoutBuscar);
            if (layoutBuscar == null) throw new Exception("layoutBuscar nao encontrado!");
            
            layoutFavoritos = findViewById(R.id.layoutFavoritos);
            if (layoutFavoritos == null) throw new Exception("layoutFavoritos nao encontrado!");
            
            layoutVersiculoDia = findViewById(R.id.layoutVersiculoDia);
            if (layoutVersiculoDia == null) throw new Exception("layoutVersiculoDia nao encontrado!");
            
            layoutProgramacao = findViewById(R.id.layoutProgramacao);
            if (layoutProgramacao == null) throw new Exception("layoutProgramacao nao encontrado!");

            // Listas
            listLivros = findViewById(R.id.listLivros);
            if (listLivros == null) throw new Exception("listLivros nao encontrado!");
            
            listResultados = findViewById(R.id.listResultados);
            if (listResultados == null) throw new Exception("listResultados nao encontrado!");
            
            listFavoritos = findViewById(R.id.listFavoritos);
            if (listFavoritos == null) throw new Exception("listFavoritos nao encontrado!");
            
            // Busca
            edtBusca = findViewById(R.id.edtBusca);
            if (edtBusca == null) throw new Exception("edtBusca nao encontrado!");
            
            btnPesquisar = findViewById(R.id.btnPesquisar);
            if (btnPesquisar == null) throw new Exception("btnPesquisar nao encontrado!");

            // Versículo do Dia
            txtVersiculoDia = findViewById(R.id.txtVersiculoDia);
            if (txtVersiculoDia == null) throw new Exception("txtVersiculoDia nao encontrado!");
            
            txtReferenciaDia = findViewById(R.id.txtReferenciaDia);
            if (txtReferenciaDia == null) throw new Exception("txtReferenciaDia nao encontrado!");
            
            btnCompartilharDia = findViewById(R.id.btnCompartilharDia);
            if (btnCompartilharDia == null) throw new Exception("btnCompartilharDia nao encontrado!");

            // Easter Egg: Toques no título para acesso admin
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
            
        } catch (Exception e) {
            throw new RuntimeException("ERRO em inicializarViews: " + e.getMessage(), e);
        }
    }

    // ============================================
    // CONFIGURAÇÃO DO DRAWER (Menu Lateral)
    // ============================================
    private void configurarDrawer() {
        try {
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

                    if (id == R.id.nav_inicio) {
                        View layoutInicio = findViewById(R.id.layoutInicio);
                        if (layoutInicio != null) {
                            layoutInicio.setVisibility(View.VISIBLE);
                            getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.layoutInicio, new InicioFragment())
                                .commit();
                        } else {
                            layoutBiblia.setVisibility(View.VISIBLE);
                        }
                        txtToolbarTitle.setText("Biblia IBSCC");
                    } 
                    else if (id == R.id.nav_biblia) {
                        layoutBiblia.setVisibility(View.VISIBLE);
                        txtToolbarTitle.setText("Biblia Sagrada");
                    } 
                    else if (id == R.id.nav_buscar) {
                        layoutBuscar.setVisibility(View.VISIBLE);
                        txtToolbarTitle.setText("Buscar");
                    } 
                    else if (id == R.id.nav_favoritos) {
                        layoutFavoritos.setVisibility(View.VISIBLE);
                        txtToolbarTitle.setText("Favoritos");
                        carregarFavoritos();
                    } 
                    else if (id == R.id.nav_versiculo_dia) {
                        layoutVersiculoDia.setVisibility(View.VISIBLE);
                        txtToolbarTitle.setText("Versiculo do Dia");
                        gerarVersiculoDoDia();
                    } 
                    else if (id == R.id.nav_programacao) {
                        layoutProgramacao.setVisibility(View.VISIBLE);
                        txtToolbarTitle.setText("Programacao");
                        carregarProgramacao();
                    } 
                    else if (id == R.id.nav_plano_leitura) {
                        mostrarPlanoLeitura();
                    } 
                    else if (id == R.id.nav_quiz) {
                        Intent intent = new Intent(MainActivity.this, QuizMenuActivity.class);
                        startActivity(intent);
                    } 
                    else if (id == R.id.nav_trocar_tema) {
                        mostrarDialogoTema();
                    } 
                    else if (id == R.id.nav_tamanho_fonte) {
                        mostrarDialogoFonte();
                        layoutBiblia.setVisibility(View.VISIBLE);
                    }

                    return true;
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("ERRO em configurarDrawer: " + e.getMessage(), e);
        }
    }

    // ============================================
    // DIÁLOGO DE ESCOLHA DE TEMA
    // ============================================
    private void mostrarDialogoTema() {
        String[] temas = {
            "Tema Claro", 
            "Tema Escuro", 
            "Tema Sepia (Leitura)"
        };
        
        int temaAtual = themeManager.getSavedTheme();
        
        new AlertDialog.Builder(this)
            .setTitle("Escolha um Tema")
            .setSingleChoiceItems(temas, temaAtual, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    themeManager.applyTheme(which);
                    modoNoturno = (which == ThemeManager.THEME_DARK);
                    prefs.edit().putBoolean("modoNoturno", modoNoturno).apply();
                    dialog.dismiss();
                    recreate();
                    Toast.makeText(MainActivity.this, "Tema: " + temas[which], Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    // ============================================
    // ESCONDER TODAS AS TELAS (PUBLIC)
    // ============================================
    public void esconderTodasTelas() {
        if (layoutBiblia != null) layoutBiblia.setVisibility(View.GONE);
        if (layoutBuscar != null) layoutBuscar.setVisibility(View.GONE);
        if (layoutFavoritos != null) layoutFavoritos.setVisibility(View.GONE);
        if (layoutVersiculoDia != null) layoutVersiculoDia.setVisibility(View.GONE);
        if (layoutProgramacao != null) layoutProgramacao.setVisibility(View.GONE);
        
        View layoutInicio = findViewById(R.id.layoutInicio);
        if (layoutInicio != null) {
            layoutInicio.setVisibility(View.GONE);
        }
    }

    // ============================================
    // CARREGAR PROGRAMAÇÃO
    // ============================================
    private void carregarProgramacao() {
        try {
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layoutProgramacao, new ProgramacaoFragment())
                .commit();
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao carregar programacao: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // ============================================
    // ACESSO ADMIN (Easter Egg)
    // ============================================
    private void verificarSenhaAdmin() {
        final EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setHint("Digite a senha");

        new AlertDialog.Builder(this)
            .setTitle("Acesso Restrito")
            .setMessage("Digite a senha de administrador:")
            .setView(input)
            .setPositiveButton("Entrar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String senha = input.getText().toString();
                    if (senha.equals("Gato2013@R")) {
                        Intent intent = new Intent(MainActivity.this, AdminProgramacaoActivity.class);
                        startActivity(intent);
                        Toast.makeText(MainActivity.this, "Acesso liberado!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Senha incorreta!", Toast.LENGTH_LONG).show();
                    }
                }
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    // ============================================
    // CONFIGURAR BÍBLIA (Lista de Livros)
    // ============================================
    private void configurarBiblia() {
        try {
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
        } catch (Exception e) {
            throw new RuntimeException("ERRO em configurarBiblia: " + e.getMessage(), e);
        }
    }

    // ============================================
    // CONFIGURAR BUSCA
    // ============================================
    private void configurarBusca() {
        try {
            btnPesquisar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String query = edtBusca.getText().toString().trim();
                    if (query.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Digite algo para buscar", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
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
        } catch (Exception e) {
            throw new RuntimeException("ERRO em configurarBusca: " + e.getMessage(), e);
        }
    }

    // ============================================
    // CARREGAR FAVORITOS (PUBLIC)
    // ============================================
    public void carregarFavoritos() {
        try {
            List<String> favoritos = favoritesHelper.getAllFavorites();
            
            if (favoritos.isEmpty()) {
                favoritos.add("Voce ainda nao tem favoritos");
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
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao carregar favoritos: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // ============================================
    // CONFIGURAR VERSÍCULO DO DIA
    // ============================================
    private void configurarVersiculoDia() {
        try {
            btnCompartilharDia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String texto = txtVersiculoDia.getText().toString();
                    String ref = txtReferenciaDia.getText().toString();
                    compartilharTexto(texto + "\n\n" + ref + "\n\nBiblia Sagrada - App IBSCC");
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("ERRO em configurarVersiculoDia: " + e.getMessage(), e);
        }
    }

    // ============================================
    // GERAR VERSÍCULO DO DIA (PUBLIC)
    // ============================================
    public void gerarVersiculoDoDia() {
        try {
            Calendar cal = Calendar.getInstance();
            int seed = cal.get(Calendar.DAY_OF_YEAR) + cal.get(Calendar.YEAR);
            java.util.Random random = new java.util.Random(seed);

            int livroIndex = random.nextInt(livros.size());
            String abrev = abreviacoes.get(livroIndex);
            String livroNome = livros.get(livroIndex);

            int totalCap = bibleHelper.getChapterCount(abrev);
            if (totalCap == 0) {
                txtVersiculoDia.setText("Erro ao carregar versiculo.");
                return;
            }

            int cap = random.nextInt(totalCap) + 1;
            List<Verse> verses = bibleHelper.getChapter(abrev, cap);

            if (verses.isEmpty()) {
                txtVersiculoDia.setText("Erro ao carregar versiculo.");
                return;
            }

            int verseIndex = random.nextInt(verses.size());
            Verse verse = verses.get(verseIndex);

            txtVersiculoDia.setText("\"" + verse.getText() + "\"");
            txtReferenciaDia.setText("- " + livroNome + " " + cap + ":" + verse.getNumber());
        } catch (Exception e) {
            if (txtVersiculoDia != null) {
                txtVersiculoDia.setText("Erro: " + e.getMessage());
            }
        }
    }

    // ============================================
    // PLANO DE LEITURA ANUAL
    // ============================================
    private void mostrarPlanoLeitura() {
        try {
            final ReadingPlanHelper planHelper = new ReadingPlanHelper(this);
            final int diaAtual = planHelper.getCurrentDay();
            final String[] leituraHoje = planHelper.getTodayReading();
            int progresso = planHelper.getProgress();
            boolean jaLeuHoje = planHelper.isDayRead(diaAtual);
            
            StringBuilder leituraTexto = new StringBuilder();
            leituraTexto.append("Dia ").append(diaAtual + 1).append(" de 365\n\n");
            leituraTexto.append("Leitura de Hoje:\n\n");
            
            if (leituraHoje.length >= 2) {
                String livroVT = bibleHelper.getBookName(leituraHoje[0]);
                leituraTexto.append("- ").append(livroVT).append(" ").append(leituraHoje[1]).append("\n");
            }
            
            if (leituraHoje.length >= 4) {
                String livroNT = bibleHelper.getBookName(leituraHoje[2]);
                leituraTexto.append("- ").append(livroNT).append(" ").append(leituraHoje[3]).append("\n");
            }
            
            leituraTexto.append("\nProgresso Total: ").append(progresso).append("/365 dias");
            leituraTexto.append("\n").append(jaLeuHoje ? "Voce ja leu hoje!" : "Leitura pendente");
            
            new AlertDialog.Builder(this)
                .setTitle("Plano de Leitura Biblica Anual")
                .setMessage(leituraTexto.toString())
                .setPositiveButton("Marcar como Lido", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        planHelper.markAsRead(diaAtual);
                        Toast.makeText(MainActivity.this, "Dia " + (diaAtual + 1) + " marcado!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Fechar", null)
                .show();
        } catch (Exception e) {
            Toast.makeText(this, "Erro no plano: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // ============================================
    // COMPARTILHAR TEXTO
    // ============================================
    private void compartilharTexto(String texto) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, texto);
        startActivity(Intent.createChooser(intent, "Compartilhar via"));
    }

    // ============================================
    // DIÁLOGO DE TAMANHO DE FONTE
    // ============================================
    private void mostrarDialogoFonte() {
        final String[] opcoes = {"Pequena (14)", "Normal (18)", "Grande (22)", "Muito Grande (26)"};
        final int[] tamanhos = {14, 18, 22, 26};

        new AlertDialog.Builder(this)
            .setTitle("Tamanho da Fonte")
            .setItems(opcoes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    tamanhoFonte = tamanhos[which];
                    prefs.edit().putInt("tamanhoFonte", tamanhoFonte).apply();
                    configurarBiblia();
                    Toast.makeText(MainActivity.this, "Fonte alterada!", Toast.LENGTH_SHORT).show();
                }
            })
            .show();
    }

    // ============================================
    // APLICAR TEMA (Cores)
    // ============================================
    private void aplicarTema() {
        try {
            int corFundo = modoNoturno ? COR_FUNDO_ESCURO : COR_FUNDO_CLARO;
            int corToolbar = modoNoturno ? COR_TOOLBAR_ESCURO : COR_TOOLBAR_CLARO;
            int corTexto = modoNoturno ? COR_TEXTO_ESCURO : COR_TEXTO_CLARO;
            int corBotao = modoNoturno ? COR_BOTAO_ESCURO : COR_BOTAO_CLARO;

            if (mainContent != null) mainContent.setBackgroundColor(corFundo);
            if (toolbar != null) toolbar.setBackgroundColor(corToolbar);
            if (txtToolbarTitle != null) txtToolbarTitle.setTextColor(Color.WHITE);

            if (layoutBiblia != null) layoutBiblia.setBackgroundColor(corFundo);
            if (layoutBuscar != null) layoutBuscar.setBackgroundColor(corFundo);
            if (layoutFavoritos != null) layoutFavoritos.setBackgroundColor(corFundo);
            if (layoutVersiculoDia != null) layoutVersiculoDia.setBackgroundColor(corFundo);
            if (layoutProgramacao != null) layoutProgramacao.setBackgroundColor(corFundo);

            if (txtVersiculoDia != null) txtVersiculoDia.setTextColor(corTexto);
            if (txtReferenciaDia != null) txtReferenciaDia.setTextColor(modoNoturno ? Color.parseColor("#AAAAAA") : Color.parseColor("#666666"));

            if (edtBusca != null) {
                edtBusca.setTextColor(corTexto);
                edtBusca.setHintTextColor(modoNoturno ? Color.parseColor("#888888") : Color.parseColor("#999999"));
                edtBusca.setBackgroundColor(modoNoturno ? Color.parseColor("#333333") : Color.WHITE);
            }

            if (btnPesquisar != null) {
                btnPesquisar.setBackgroundColor(corBotao);
                btnPesquisar.setTextColor(Color.WHITE);
            }

            if (btnCompartilharDia != null) {
                btnCompartilharDia.setBackgroundColor(corBotao);
                btnCompartilharDia.setTextColor(Color.WHITE);
            }

            if (navigationView != null) {
                navigationView.setBackgroundColor(modoNoturno ? COR_FUNDO_ESCURO : Color.WHITE);
                
                int corTextoMenu = modoNoturno ? Color.WHITE : Color.parseColor("#333333");
                navigationView.setItemTextColor(ColorStateList.valueOf(corTextoMenu));
                navigationView.setItemIconTintList(ColorStateList.valueOf(corTextoMenu));

                View headerView = navigationView.getHeaderView(0);
                if (headerView != null) {
                    headerView.setBackgroundColor(corToolbar);
                }
            }
        } catch (Exception e) {
            // Ignorar erro de tema
        }
    }

    // ============================================
    // LIFECYCLE - onResume
    // ============================================
    @Override
    protected void onResume() {
        super.onResume();
        try {
            modoNoturno = prefs.getBoolean("modoNoturno", false);
            tamanhoFonte = prefs.getInt("tamanhoFonte", 18);
            aplicarTema();
            configurarBiblia();
            carregarFavoritos();
        } catch (Exception e) {
            // Ignorar erro no onResume
        }
    }

    // ============================================
    // MÉTODOS PÚBLICOS (Para Fragments)
    // ============================================
    public void abrirBiblia() {
        esconderTodasTelas();
        layoutBiblia.setVisibility(View.VISIBLE);
        txtToolbarTitle.setText("Biblia Sagrada");
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawers();
        }
    }

    public void abrirBusca() {
        esconderTodasTelas();
        layoutBuscar.setVisibility(View.VISIBLE);
        txtToolbarTitle.setText("Buscar");
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawers();
        }
    }

    public void abrirProgramacao() {
        esconderTodasTelas();
        layoutProgramacao.setVisibility(View.VISIBLE);
        txtToolbarTitle.setText("Programacao");
        carregarProgramacao();
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawers();
        }
    }

    public void abrirFavoritos() {
        esconderTodasTelas();
        layoutFavoritos.setVisibility(View.VISIBLE);
        txtToolbarTitle.setText("Favoritos");
        carregarFavoritos();
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawers();
        }
    }

    public void abrirVersiculoDia() {
        esconderTodasTelas();
        layoutVersiculoDia.setVisibility(View.VISIBLE);
        txtToolbarTitle.setText("Versiculo do Dia");
        gerarVersiculoDoDia();
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawers();
        }
    }
}