package com.vegs;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import controlador.Conexao;
import controlador.GerenciadorSharedPreferences;
import controlador.Requisicao;
import controlador.TransformacaoCirculo;
import modelo.Categoria;
import modelo.Favorito;
import modelo.Mensagem;
import modelo.Receita;
import modelo.Usuario;

/**
 * Created by Aline Dominique on 26/11/2017.
 */
public class ActPrincipal extends AppCompatActivity {

    public int tabSelecionada;
    public static Usuario usuarioLogado;
    private ProgressDialog pd;
    private ArrayList<Receita> listaReceitas = new ArrayList<>();
    private ArrayList<Favorito> listaFavoritos = new ArrayList<>();
    private ArrayList<Receita> listaReceitasPorCategoria = new ArrayList<>();
    private ArrayList<Receita> listaReceitasPorUsuario = new ArrayList<>();
    private ArrayList<Categoria> categorias = new ArrayList<>();
    private SwipeRefreshLayout scReceitas, scFavoritos, scCategorias, scConfiguracoes;
    private Spinner spCategorias;
    private Categoria categoria_escolhida;
    ArrayAdapter<Receita> adpReceitas;
    ArrayAdapter<Favorito> adpFavoritos;
    ArrayAdapter<Receita> adpReceitaPorCategoria;
    ArrayAdapter<Receita> adpConfiguracoes;
    private int processos = 0;
    Menu menu;

    private LinearLayout llconexao = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        // Procura os containers da vista do Swipe
        scReceitas = (SwipeRefreshLayout) findViewById(R.id.scReceitas);
        scCategorias = (SwipeRefreshLayout) findViewById(R.id.scCategorias);
        scFavoritos = (SwipeRefreshLayout) findViewById(R.id.scFavoritos);
        scConfiguracoes = (SwipeRefreshLayout) findViewById(R.id.scConfiguracoes);

        /**
         * Mostra o Swipe Refresh no momento em que a activity é criada
         */
        scReceitas.post(new Runnable() {
            @Override
            public void run() {

                scReceitas.setRefreshing(true);

                //Monta lista de receitas
                listaReceitas();

            }
        });

        scCategorias.post(new Runnable() {
            @Override
            public void run() {

                scCategorias.setRefreshing(true);

                //Monta lista de receitas por categoria
                listaReceitasPorCategoria();

            }
        });

        scFavoritos.post(new Runnable() {
            @Override
            public void run() {

                scFavoritos.setRefreshing(true);

                //Monta lista de receitas
                listaFavoritos();

            }
        });

        scConfiguracoes.post(new Runnable() {
            @Override
            public void run() {

                scConfiguracoes.setRefreshing(true);

                //Monta lista de receitas
                listaReceitasPorUsuario();

            }
        });

        // Seta o listener do refresh que é o gatilho de novas datas
        scFavoritos.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //Monta lista de receitas
                listaFavoritos();

            }
        });

        // Seta o listener do refresh que é o gatilho de novas datas
        scConfiguracoes.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //Monta lista de receitas
                listaReceitasPorUsuario();

            }
        });

        scReceitas.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //Monta lista de receitas
                listaReceitas();

            }
        });


        // Configuração das cores do swipe
        scReceitas.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        scFavoritos.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        scCategorias.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        scConfiguracoes.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //Verifica se o processo já está rodando, se não estiver ele é iniciado.
//        if(!Servico.processoRodando) {
//            Intent i = new Intent(this, Servico.class);
//            startService(i);
//        }

        //Configura e carrega toolbar
        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        t.setTitleTextColor(ContextCompat.getColor(this, R.color.fontColorPrimary));
        setSupportActionBar(t);

        //Adiciona as opções nas tabs
        configuraTabs();

        //Carregas Spinners
        CarregaSpinners();

        //Evento click do botão flutuante de adicionar receita
        FloatingActionButton btAdicionar = (FloatingActionButton)findViewById(R.id.btAdicionar);
        btAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tela = new Intent(ActPrincipal.this, ActCadastroReceita.class);
                tela.putExtra("Solicitante", ActPrincipal.class.toString());
                startActivity(tela);
            }
        });

        recuperaUsuario();
    }

    //Bloqueia o botão de voltar do android
    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onResume() {
        super.onResume();

        //Mostra ou esconde a mensagem de falta de conexão com a internet.
        if(llconexao == null){
            llconexao = (LinearLayout) findViewById(R.id.llconexao);
        }

        if(Conexao.verificaConexao(ActPrincipal.this)){
            llconexao.setVisibility(View.GONE);
        }else{
            llconexao.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Carrega layout do toolbar
        getMenuInflater().inflate(R.menu.toolbar_principal, menu);
        this.menu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Trata click dos menus do toolbar
        switch (item.getItemId()) {
            case R.id.menuAjuda:
                Intent intentA = new Intent();
                intentA.setAction(Intent.ACTION_VIEW);
                intentA.addCategory(Intent.CATEGORY_BROWSABLE);
                intentA.setData(Uri.parse(getString(R.string.Manual)));
                startActivity(intentA);
                return true;
            case R.id.menuSobre:
                Intent intent1 = new Intent(ActPrincipal.this, ActSobre.class);
                startActivity(intent1);
                return true;
            case R.id.menuSair:
                //Limpa SharedPreferences
                GerenciadorSharedPreferences.setEmail(getBaseContext(),"");
                //Chama tela de login
                Intent principal = new Intent(ActPrincipal.this, ActLogin.class);
                startActivity(principal);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Recupera usuário logado
    public void recuperaUsuario(){
        if(ActPrincipal.usuarioLogado == null){
            try {
                JSONObject json = new JSONObject();
                json.put("Email", GerenciadorSharedPreferences.getEmail(getBaseContext()));
                //Chama método para recuperar usuário logado
                new RequisicaoAsyncTask().execute("RecuperarUsuario", "0", json.toString());
            }catch(Exception ex){
                Log.e("Erro", ex.getMessage());
                Toast.makeText(ActPrincipal.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Monta a lista de receitas
    public void listaReceitas(){
        // método chamado para cada item do lvReceitas
        adpReceitas = new ArrayAdapter<Receita>(this, R.layout.item_receitas) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.item_receitas, null); /* obtém o objeto que está nesta posição do ArrayAdapter */

                Receita receita = getItem(position);

                ImageView ivReceita = (ImageView) convertView.findViewById(R.id.ivReceita);
                TextView tvNome = (TextView) convertView.findViewById(R.id.tvNome);
                TextView tvCategoria = (TextView) convertView.findViewById(R.id.tvCategoria);
                TextView tvPorcoes = (TextView) convertView.findViewById(R.id.tvPorcoes);


                if (receita != null){
                    //Carrega informações da receita na lista
                    Picasso.with(getContext()).load(receita.getFoto()).into(ivReceita);
                    tvNome.setText(receita.getNome());
                    tvCategoria.setText("Categoria: " + receita.getCategoria().getNome().toString());
                    tvPorcoes.setText("Porções: " + receita.getPorcoes().toString());

                    final Receita r = receita;
                    //Adiciona evento de click na foto da receita
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent intent = new Intent(ActPrincipal.this, ActReceita.class);
                                intent.putExtra("Receita", r.receitaToJson().toString());
                                startActivity(intent);
                            }catch (Exception ex){
                                Log.e("Erro", ex.getMessage());
                                Toast.makeText(ActPrincipal.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                return convertView;
            }
        };

        //Carrega lista de receitas
        listaReceitas.clear();
        new RequisicaoAsyncTask().execute("ListarReceitas", "0", "");

        ListView lvReceitas = (ListView)findViewById(R.id.lvReceitas);
        lvReceitas.setAdapter(adpReceitas);

    }

    //Monta a lista de categorias
    public void listaReceitasPorCategoria(){
        // método chamado para cada item do lvCategorias
        adpReceitaPorCategoria = new ArrayAdapter<Receita>(this, R.layout.item_receitas) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.item_receitas, null); /* obtém o objeto que está nesta posição do ArrayAdapter */

                Receita receita = getItem(position);

                ImageView ivReceita = (ImageView) convertView.findViewById(R.id.ivReceita);
                TextView tvNome = (TextView) convertView.findViewById(R.id.tvNome);
                TextView tvCategoria = (TextView) convertView.findViewById(R.id.tvCategoria);
                TextView tvPorcoes = (TextView) convertView.findViewById(R.id.tvPorcoes);


                if (receita != null){
                    //Carrega informações da receita na lista
                    Picasso.with(getContext()).load(receita.getFoto()).into(ivReceita);
                    tvNome.setText(receita.getNome());
                    tvCategoria.setText("Categoria: " + receita.getCategoria().getNome().toString());
                    tvPorcoes.setText("Porções: " + receita.getPorcoes().toString());

                    final Receita r = receita;
                    //Adiciona evento de click na foto da receita
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent intent = new Intent(ActPrincipal.this, ActReceita.class);
                                intent.putExtra("Receita", r.receitaToJson().toString());
                                startActivity(intent);
                            }catch (Exception ex){
                                Log.e("Erro", ex.getMessage());
                                Toast.makeText(ActPrincipal.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                return convertView;
            }
        };

        listaReceitasPorCategoria.clear();
        new RequisicaoAsyncTask().execute("ListarReceitasPorCategoria", String.valueOf(0), "");

        ListView lvReceitasPorCategoria = (ListView)findViewById(R.id.lvCategorias);
        lvReceitasPorCategoria.setAdapter(adpReceitaPorCategoria);

    }

    //Monta a lista de receitas
    public void listaFavoritos(){
        // método chamado para cada item do lvFavoritos
        adpFavoritos = new ArrayAdapter<Favorito>(this, R.layout.item_favoritos) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.item_favoritos, null); /* obtém o objeto que está nesta posição do ArrayAdapter */

                Favorito favorito = getItem(position);

                ImageView ivFavorito = (ImageView) convertView.findViewById(R.id.ivFavorito);
                TextView tvNome = (TextView) convertView.findViewById(R.id.tvNome);
                TextView tvCategoria = (TextView) convertView.findViewById(R.id.tvCategoria);
                TextView tvPorcoes = (TextView) convertView.findViewById(R.id.tvPorcoes);


                if (favorito != null){
                    //Carrega informações da receita na lista
                    Picasso.with(getContext()).load(favorito.getReceita().getFoto()).into(ivFavorito);
                    tvNome.setText(favorito.getReceita().getNome());
                    tvCategoria.setText("Categoria: " + favorito.getReceita().getCategoria().getNome().toString());
                    tvPorcoes.setText("Porções: " + favorito.getReceita().getPorcoes().toString());

                    final Favorito f = favorito;
                    //Adiciona evento de click na foto da receita
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent intent = new Intent(ActPrincipal.this, ActFavorito.class);
                                intent.putExtra("Favorito", f.favoritoToJson().toString());
                                startActivity(intent);
                            }catch (Exception ex){
                                Log.e("Erro", ex.getMessage());
                                Toast.makeText(ActPrincipal.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    final int index = position;

                    //Adiciona evento de click no botão de deletar favorito.
                    ImageView ivExcluirFavorito = (ImageView) convertView.findViewById(R.id.ivExcluirFavorito);
                    ivExcluirFavorito.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            final int index2 = index;

                            //Monta caixa de dialogo de confirmação de deleção.
                            AlertDialog.Builder dialogo = new AlertDialog.Builder(ActPrincipal.this);
                            dialogo.setTitle("Aviso!")
                                    .setMessage("Você tem certeza que deseja remover esta receita da lista de favoritos?")
                                    .setIcon(R.mipmap.ic_launcher)
                                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            scFavoritos.setRefreshing(true);
                                            new RequisicaoAsyncTask().execute("ExcluirFavorito", String.valueOf(adpFavoritos.getItem(index2).getIdFavorito()), "");
                                        }
                                    })
                                    .setNegativeButton("Não", null);
                            AlertDialog alerta = dialogo.create();
                            alerta.show();
                        }
                    });
                }

                return convertView;
            }
        };

        //Carrega lista de favoritos
        listaFavoritos.clear();
        try {
            JSONObject json = new JSONObject();
            json.put("Email", GerenciadorSharedPreferences.getEmail(getBaseContext()));
            new RequisicaoAsyncTask().execute("ListarFavoritoPorUsuario", "0", json.toString());
        } catch (Exception ex){
            Log.e("Erro", ex.getMessage());
            Toast.makeText(ActPrincipal.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
        }

        ListView lvFavoritos = (ListView)findViewById(R.id.lvFavoritos);
        lvFavoritos.setAdapter(adpFavoritos);

    }

    //Monta a lista de receitas
    public void listaReceitasPorUsuario(){
        // método chamado para cada item do lvReceitas
        adpConfiguracoes = new ArrayAdapter<Receita>(this, R.layout.item_configuracoes) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.item_configuracoes, null); /* obtém o objeto que está nesta posição do ArrayAdapter */

                Receita receita = getItem(position);

                ImageView ivReceita = (ImageView) convertView.findViewById(R.id.ivReceita);
                TextView tvNome = (TextView) convertView.findViewById(R.id.tvNome);
                TextView tvCategoria = (TextView) convertView.findViewById(R.id.tvCategoria);
                TextView tvPorcoes = (TextView) convertView.findViewById(R.id.tvPorcoes);


                if (receita != null){
                    //Carrega informações da receita na lista
                    Picasso.with(getContext()).load(receita.getFoto()).into(ivReceita);
                    tvNome.setText(receita.getNome());
                    tvCategoria.setText("Categoria: " + receita.getCategoria().getNome().toString());
                    tvPorcoes.setText("Porções: " + receita.getPorcoes().toString());

                    final int index = position;

                    //Adiciona evento de click no botão de deletar receita do usuario.
                    ImageView ivExcluirConfiguracao = (ImageView) convertView.findViewById(R.id.ivExcluirConfiguracao);
                    ivExcluirConfiguracao.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            final int index2 = index;

                            //Monta caixa de dialogo de confirmação de deleção.
                            AlertDialog.Builder dialogo = new AlertDialog.Builder(ActPrincipal.this);
                            dialogo.setTitle("Aviso!")
                                    .setMessage("Você tem certeza que deseja remover esta receita?")
                                    .setIcon(R.mipmap.ic_launcher)
                                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            scConfiguracoes.setRefreshing(true);
                                            new RequisicaoAsyncTask().execute("ExcluirReceita", String.valueOf(adpConfiguracoes.getItem(index2).getIdReceita()), "");
                                        }
                                    })
                                    .setNegativeButton("Não", null);
                            AlertDialog alerta = dialogo.create();
                            alerta.show();
                        }
                    });
                }

                return convertView;
            }
        };

        //Carrega lista de receitas por usuario
        listaReceitasPorUsuario.clear();
        try {
            JSONObject json = new JSONObject();
            json.put("Email", GerenciadorSharedPreferences.getEmail(getBaseContext()));
            new RequisicaoAsyncTask().execute("ListarReceitasPorUsuario", "0", json.toString());
        } catch (Exception ex) {
            Log.e("Erro", ex.getMessage());
            Toast.makeText(ActPrincipal.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
        }

        ListView lvConfiguracoes = (ListView)findViewById(R.id.lvConfiguracoes);
        lvConfiguracoes.setAdapter(adpConfiguracoes);

        //Adiciona o evento de click nos items da lista
        lvConfiguracoes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {

                    Receita receita = (Receita) parent.getItemAtPosition(position);

                    Intent atualizazao = new Intent(ActPrincipal.this, ActAtualizaReceita.class);
                    atualizazao.putExtra("Receita", receita.receitaToJson().toString());

                    startActivity(atualizazao);

                } catch (Exception ex) {
                    Log.e("Erro", ex.getMessage());
                    Toast.makeText(ActPrincipal.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //Carrega spinners na tela com os valores do banco de dados
    public void CarregaSpinners() {
        //Carrega spinner de Categorias
        categorias.clear();
        categorias.add(new Categoria(0, "Selecione uma Categoria!"));
        spCategorias = (Spinner) findViewById(R.id.spCategorias);
        ArrayAdapter adCategoria = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categorias) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                //Seta cores nos items
                if (position == 0) {
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.placeholderColor));
                } else {
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.textColor));
                }
                return v;
            }

            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    //Desabilita o primeiro item da lista.
                    //O primeiro item será usado para a dica.
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Coloca cor cinza no texto
                    tv.setTextColor(Color.GRAY);
                } else {
                    //Coloca cor preta no texto
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spCategorias.setAdapter(adCategoria);

        //Adiciona evento de item selecionado no spinner de categoria
        spCategorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    categoria_escolhida = (Categoria) spCategorias.getItemAtPosition(position);

                    if (categoria_escolhida != null) {
                        //Carrega lista de receitas
                        try {
                            listaReceitasPorCategoria.clear();
                            scCategorias.setRefreshing(true);
                            new RequisicaoAsyncTask().execute("ListarReceitasPorCategoria", String.valueOf(categoria_escolhida.getIdCategoria()), "");
                        } catch (Exception ex) {
                            Log.e("Erro", ex.getMessage());
                            Toast.makeText(ActPrincipal.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                        }
                    } else if (categoria_escolhida.getIdCategoria() == 0){
                        //Carrega lista de receitas por categoria
                        try {
                            listaReceitasPorCategoria.clear();
                            scCategorias.setRefreshing(true);
                            new RequisicaoAsyncTask().execute("ListaReceitasPorCategoria", String.valueOf(0), "");
                        } catch (Exception ex) {
                            Log.e("Erro", ex.getMessage());
                            Toast.makeText(ActPrincipal.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Carrega lista de categorias
        try {
            new RequisicaoAsyncTask().execute("ListarCategorias", "0", "");
        } catch (Exception ex) {
            Log.e("Erro", ex.getMessage());
            Toast.makeText(ActPrincipal.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
        }
    }
    //Configura as tabs da tela principal
    public void configuraTabs(){
        //Adiciona as opções nas tabs da tela principal
        TabHost abas = (TabHost) findViewById(R.id.tbPrincipal);

        abas.setup();

        TabHost.TabSpec descritor = abas.newTabSpec("Receitas");
        descritor.setContent(R.id.llReceitas);
        descritor.setIndicator("", ResourcesCompat.getDrawable(getResources(), R.drawable.ic_restaurant, getTheme()));
        abas.addTab(descritor);

        descritor = abas.newTabSpec("Categorias");
        descritor.setContent(R.id.llCategorias);
        descritor.setIndicator("", ResourcesCompat.getDrawable(getResources(), R.drawable.ic_reorder, getTheme()));
        abas.addTab(descritor);

        descritor = abas.newTabSpec("Favoritos");
        descritor.setContent(R.id.llFavoritos);
        descritor.setIndicator("", ResourcesCompat.getDrawable(getResources(), R.drawable.ic_favorite, getTheme()));
        abas.addTab(descritor);

        descritor = abas.newTabSpec("Configuracoes");
        descritor.setContent(R.id.llConfiguracoes);
        descritor.setIndicator("", ResourcesCompat.getDrawable(getResources(), R.drawable.ic_setting_dark, getTheme()));
        abas.addTab(descritor);

        //Seta o fundo da primeira tab selecionada
        tabSelecionada = abas.getCurrentTab();
        abas.getTabWidget().getChildAt(abas.getCurrentTab()).setBackgroundColor(ContextCompat.getColor(getBaseContext(),R.color.buttonColorPrimary));

        abas.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            public void onTabChanged(String arg0) {
                //Seta a cor de fundo da tab selecionada
                TabHost abas = (TabHost) findViewById(R.id.tbPrincipal);
                for (int i = 0; i < abas.getTabWidget().getChildCount(); i++) {
                    abas.getTabWidget().getChildAt(i).setBackgroundColor(ContextCompat.getColor(getBaseContext(),R.color.colorPrimary));
                }
                abas.getTabWidget().getChildAt(abas.getCurrentTab()).setBackgroundColor(ContextCompat.getColor(getBaseContext(),R.color.buttonColorPrimary));

                //Anima a transição de tabs
                View viewSelecionada = abas.getCurrentView();
                if (abas.getCurrentTab() > tabSelecionada)
                {
                    viewSelecionada.setAnimation(direita());
                }
                else
                {
                    viewSelecionada.setAnimation(esquerda());
                }
                tabSelecionada = abas.getCurrentTab();

            }

        });
    }

    //Anima a transição vinda da direita
    public Animation direita() {
        Animation direita = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        direita.setDuration(240);
        direita.setInterpolator(new AccelerateInterpolator());
        return direita;
    }

    //Anima a transição vinda da esquerda
    public Animation esquerda() {
        Animation esquerda = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        esquerda.setDuration(240);
        esquerda.setInterpolator(new AccelerateInterpolator());
        return esquerda;
    }

    private class RequisicaoAsyncTask extends AsyncTask<String, Void, JSONArray> {

        private String metodo;
        private int id;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected JSONArray doInBackground(String... params) {
            JSONArray resultado = new JSONArray();

            try {
                //Recupera parâmetros e realiza a requisição
                metodo = params[0];
                id = Integer.parseInt(params[1]);
                String conteudo = params[2];

                //Chama método da API
                resultado = Requisicao.chamaMetodo(metodo, id, conteudo);

            } catch (Exception e) {
                Log.e("Erro", e.getMessage());
                Toast.makeText(ActPrincipal.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }
            return resultado;
        }

        @Override
        protected void onPostExecute(JSONArray resultado) {
            try {
                if (resultado.length() > 0) {
                    //Verifica se o objeto retornado foi uma mensagem ou um objeto
                    JSONObject json = resultado.getJSONObject(0);
                    if (Mensagem.isMensagem(json)) {
                        Mensagem msg = Mensagem.jsonToMensagem(json);
                        Toast.makeText(ActPrincipal.this, msg.getMensagem(), Toast.LENGTH_SHORT).show();

                        //Se a exclusão foi bem sucedida remove o item da lista
                        if (metodo == "ExcluirFavorito" && msg.getCodigo() == 7) {
                            int index = 0;
                            for (int i = 0; i < listaFavoritos.size(); i++) {
                                if (id == listaFavoritos.get(i).getIdFavorito()) {
                                    index = i;
                                    break;
                                }
                            }
                            listaFavoritos.remove(index);
                            adpFavoritos.clear();
                            adpFavoritos.addAll(listaFavoritos);
                        } else //Se a exclusão foi bem sucedida remove o item da lista
                            if (metodo == "ExcluirReceita" && msg.getCodigo() == 7) {
                                int index = 0;
                                for (int i = 0; i < listaReceitasPorUsuario.size(); i++) {
                                    if (id == listaReceitasPorUsuario.get(i).getIdReceita()) {
                                        index = i;
                                        break;
                                    }
                                }
                                listaReceitasPorUsuario.remove(index);
                                adpReceitaPorCategoria.clear();
                                adpReceitaPorCategoria.addAll(listaReceitasPorUsuario);
                            }

                    } else {
                        //Verifica qual foi o método chamado
                        if (metodo == "RecuperarUsuario") {
                            //Recupera usuário retornado pela API
                            ActPrincipal.usuarioLogado = Usuario.jsonToUsuario(json);
                        } else if (metodo == "ListarReceitas") {
                            //Monta lista de receitas
                            for (int i = 0; i < resultado.length(); i++) {
                                listaReceitas.add(Receita.jsonToReceita(resultado.getJSONObject(i)));
                            }
                            adpReceitas.clear();
                            adpReceitas.addAll(listaReceitas);
                        } else if (metodo == "ListarFavoritoPorUsuario") {
                            //Monta lista de favoritos
                            for (int i = 0; i < resultado.length(); i++) {
                                listaFavoritos.add(Favorito.jsonToFavorito(resultado.getJSONObject(i)));
                            }
                            adpFavoritos.clear();
                            adpFavoritos.addAll(listaFavoritos);
                        } else if (metodo == "ListarReceitasPorCategoria") {
                            //Monta lista de receitas por categoria
                            for (int i = 0; i < resultado.length(); i++) {
                                listaReceitasPorCategoria.add(Receita.jsonToReceita(resultado.getJSONObject(i)));
                            }
                            adpReceitaPorCategoria.clear();
                            adpReceitaPorCategoria.addAll(listaReceitasPorCategoria);
                        } else if(metodo.trim().equals("ListarCategorias")) {
                            //Recupera categorias
                            for (int i = 0; i < resultado.length(); i++) {
                                categorias.add(Categoria.jsonToCategoria(resultado.getJSONObject(i)));
                            }
                        } else if(metodo.trim().equals("ListarReceitasPorUsuario")) {
                            //Recupera receitas do usuario
                            for (int i = 0; i < resultado.length(); i++) {
                                listaReceitasPorUsuario.add(Receita.jsonToReceita(resultado.getJSONObject(i)));
                                adpConfiguracoes.clear();
                                adpConfiguracoes.addAll(listaReceitasPorUsuario);
                            }
                        }

                    }
                }
            }
            catch (Exception e) {
                Log.e("Erro", e.getMessage());
                Toast.makeText(ActPrincipal.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }

            processos--;
            if(processos == 0) {
                pd.dismiss();
            }

            // Para o Swipe Refreshing
            scReceitas.setRefreshing(false);
            scFavoritos.setRefreshing(false);
            scCategorias.setRefreshing(false);
            scConfiguracoes.setRefreshing(false);

        }
    }
}

