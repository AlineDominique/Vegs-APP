package com.vegs;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.wearable.internal.StorageInfoResponse;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import controlador.GerenciadorSharedPreferences;
import controlador.Imagem;
import controlador.Requisicao;
import controlador.TransformacaoCirculo;
import modelo.Categoria;
import modelo.Mensagem;

/**
 * Created by Aline Dominique on 02/12/2017.
 */
public class ActCadastroReceita extends AppCompatActivity {

    private EditText etNome;
    private EditText etTempoPreparo;
    private EditText etModoPreparo;
    private EditText etDicas;
    private EditText ing1, ing2, ing3, ing4, ing5, ing6, ing7, ing8, ing9, ing10, ing11, ing12, ing13, ing14, ing15;
    private EditText qtd1, qtd2, qtd3, qtd4, qtd5, qtd6, qtd7, qtd8, qtd9, qtd10, qtd11, qtd12, qtd13, qtd14, qtd15;
    private Spinner spPorcoes;
    private Spinner spCategorias;
    private Spinner spMed1, spMed2, spMed3, spMed4, spMed5, spMed6, spMed7, spMed8, spMed9, spMed10, spMed11, spMed12, spMed13, spMed14, spMed15;
    private ImageView ivFoto;
    private AlertDialog.Builder dialogo;
    private AlertDialog alerta;
    private static final int READ_EXTERNAL_STORAGE_PERMISSIONS_REQUEST = 1;
    private Intent selecionarImagem;
    Uri imagemSelecionada = null;
    private ProgressDialog pd;
    private ArrayList<Categoria> categorias = new ArrayList<>();
    private ArrayList<String> receitaId = new ArrayList<>();
    private int processos = 0;
    Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_receita);

        //Configura e carrega toolbar
        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        t.setTitleTextColor(ContextCompat.getColor(this, R.color.fontColorPrimary));
        t.setLogo(R.drawable.ic_restaurant);
        setSupportActionBar(t);

        //Constrói mensagem de diálogo.
        dialogo = new AlertDialog.Builder(ActCadastroReceita.this);
        dialogo.setIcon(R.mipmap.ic_launcher);
        //Apresenta mensagem de aviso ao usuário
        dialogo.setMessage("Para usar essa função é necessário que o aplicativo tenha permissão de acesso ao armazenamento de arquivos! Clique em 'OK' para ir até a tela de permissões do Vegs.");
        dialogo.setTitle("Aviso!");
        dialogo.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startInstalledAppDetailsActivity(ActCadastroReceita.this);
            }
        });
        alerta = dialogo.create();

        //Inicia variavel para seleção de imagem
        selecionarImagem = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        //Recupera objeto de foto do animal e adiciona evento de click
        ivFoto = (ImageView) findViewById(R.id.ivFoto);
        ivFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Verifica permissões somente se a API for 23 ou maior
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    verificaPermissao();
                }
            }
        });

        //Recupera nome da receita.
        etNome = (EditText) findViewById(R.id.etNome);

        //Recupera tempo de preparo da receita.
        etTempoPreparo = (EditText) findViewById(R.id.etTempoPreparo);

        //Recupera Ingredientes.
        ing1 = (EditText) findViewById(R.id.etIng1);
        ing2 = (EditText) findViewById(R.id.etIng2);
        ing3 = (EditText) findViewById(R.id.etIng3);
        ing4 = (EditText) findViewById(R.id.etIng4);
        ing5 = (EditText) findViewById(R.id.etIng5);
        ing6 = (EditText) findViewById(R.id.etIng6);
        ing7 = (EditText) findViewById(R.id.etIng7);
        ing8 = (EditText) findViewById(R.id.etIng8);
        ing9 = (EditText) findViewById(R.id.etIng9);
        ing10 = (EditText) findViewById(R.id.etIng10);
        ing11 = (EditText) findViewById(R.id.etIng11);
        ing12 = (EditText) findViewById(R.id.etIng12);
        ing13 = (EditText) findViewById(R.id.etIng13);
        ing14 = (EditText) findViewById(R.id.etIng14);
        ing15 = (EditText) findViewById(R.id.etIng15);

        //Recupera Quantidades
        qtd1 = (EditText) findViewById(R.id.etQtd1);
        qtd2 = (EditText) findViewById(R.id.etQtd2);
        qtd3 = (EditText) findViewById(R.id.etQtd3);
        qtd4 = (EditText) findViewById(R.id.etQtd4);
        qtd5 = (EditText) findViewById(R.id.etQtd5);
        qtd6 = (EditText) findViewById(R.id.etQtd6);
        qtd7 = (EditText) findViewById(R.id.etQtd7);
        qtd8 = (EditText) findViewById(R.id.etQtd8);
        qtd9 = (EditText) findViewById(R.id.etQtd9);
        qtd10 = (EditText) findViewById(R.id.etQtd10);
        qtd11 = (EditText) findViewById(R.id.etQtd11);
        qtd12 = (EditText) findViewById(R.id.etQtd12);
        qtd13 = (EditText) findViewById(R.id.etQtd13);
        qtd14 = (EditText) findViewById(R.id.etQtd14);
        qtd15 = (EditText) findViewById(R.id.etQtd15);

        //Recupera modo de preparo da receita.
        etModoPreparo = (EditText) findViewById(R.id.etModoPreparo);

        //Recupera dicas da receita.
        etDicas = (EditText) findViewById(R.id.etDicas);

        //Seta calendário na data
        setaCalendario();

        //Carrega spinners da tela com os valores
        CarregaSpinners();

        //Adiciona evento de click no botão de salvar
        Button btSalvar = (Button) findViewById(R.id.btSalvar);
        btSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CadastraReceita();
            }
        });

    }

    public static void startInstalledAppDetailsActivity(final Activity context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Carrega layout do toolbar
        getMenuInflater().inflate(R.menu.toolbar_principal, menu);
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
                Intent intent1 = new Intent(ActCadastroReceita.this, ActSobre.class);
                startActivity(intent1);
                return true;
            case R.id.menuSair:
                //Limpa SharedPreferences
                GerenciadorSharedPreferences.setEmail(getBaseContext(),"");
                //Chama tela de login
                Intent principal = new Intent(ActCadastroReceita.this, ActLogin.class);
                startActivity(principal);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setaCalendario() {
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            }

        };

        etTempoPreparo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                final TimePickerDialog mTimePicker = new TimePickerDialog(ActCadastroReceita.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String hora = "";
                        String min = "";
                        if (selectedHour >= 0 && selectedHour < 10)
                            hora = "0" + String.valueOf(selectedHour);
                        else
                            hora = String.valueOf(selectedHour);

                        if (selectedMinute >= 0 && selectedMinute < 10)
                            min = "0" + String.valueOf(selectedMinute);
                        else
                            min = String.valueOf(selectedMinute);

                        etTempoPreparo.setText(hora + ":" + min + ":00");

                    }
                }, hour, minute, true);

                TextView tvTitle = new TextView(ActCadastroReceita.this);
                tvTitle.setBackgroundColor(Color.WHITE);
                tvTitle.setText("");
                mTimePicker.setCustomTitle(tvTitle);

                mTimePicker.show();

            }
        });
    }

    //Carrega spinners na tela com os valores do banco de dados
    public void CarregaSpinners(){
        //Carrega spinner de Categorias
        categorias.clear();
        categorias.add(new Categoria(0, "Selecione uma Categoria!"));
        spCategorias = (Spinner) findViewById(R.id.spCategorias);
        ArrayAdapter adCategoria = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,categorias){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                //Seta cores nos items
                if(position == 0) {
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.placeholderColor));
                }else{
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.textColor));
                }
                return v;
            }

            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    //Desabilita o primeiro item da lista.
                    //O primeiro item será usado para a dica.
                    return false;
                }
                else
                {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Coloca cor cinza no texto
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    //Coloca cor preta no texto
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spCategorias.setAdapter(adCategoria);

        //Carrega lista de categorias
        pd = ProgressDialog.show(ActCadastroReceita.this, "", "Por favor, aguarde...", false);
        processos++;
        try {
            new RequisicaoAsyncTask().execute("ListarCategorias", "0", "");
        }catch(Exception ex){
                Log.e("Erro", ex.getMessage());
                Toast.makeText(ActCadastroReceita.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
        }

        //Carrega spinner de porcoes
        String[] porcoes = new String[]{"Selecione a quantidade de porções","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30"};
        spPorcoes = (Spinner) findViewById(R.id.spPorcoes);
        ArrayAdapter adPorcoes = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,porcoes){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                //Seta cores nos items
                if(position == 0) {
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.placeholderColor));
                }else{
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.textColor));
                }
                return v;
            }

            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    //Desabilita o primeiro item da lista.
                    //O primeiro item será usado para a dica.
                    return false;
                }
                else
                {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Coloca cor cinza no texto
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    //Coloca cor preta no texto
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spPorcoes.setAdapter(adPorcoes);

        //Carrega spinner de Medidas
        String[] medidas = new String[]{"Selecione a medida","unidade(s)", "mg", "g", "kg", "lb", "ml", "l", "oz", "colher de chá", "colher e sopa", "xícara", "copo americano", "a gosto", "ramo"};
        spMed1 = (Spinner) findViewById(R.id.spMed1);
        spMed2 = (Spinner) findViewById(R.id.spMed2);
        spMed3 = (Spinner) findViewById(R.id.spMed3);
        spMed4 = (Spinner) findViewById(R.id.spMed4);
        spMed5 = (Spinner) findViewById(R.id.spMed5);
        spMed6 = (Spinner) findViewById(R.id.spMed6);
        spMed7 = (Spinner) findViewById(R.id.spMed7);
        spMed8 = (Spinner) findViewById(R.id.spMed8);
        spMed9 = (Spinner) findViewById(R.id.spMed9);
        spMed10 = (Spinner) findViewById(R.id.spMed10);
        spMed11 = (Spinner) findViewById(R.id.spMed11);
        spMed12 = (Spinner) findViewById(R.id.spMed12);
        spMed13 = (Spinner) findViewById(R.id.spMed13);
        spMed14 = (Spinner) findViewById(R.id.spMed14);
        spMed15 = (Spinner) findViewById(R.id.spMed15);
        ArrayAdapter adMedidas = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,medidas){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                //Seta cores nos items
                if(position == 0) {
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.placeholderColor));
                }else{
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.textColor));
                }
                return v;
            }

            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    //Desabilita o primeiro item da lista.
                    //O primeiro item será usado para a dica.
                    return false;
                }
                else
                {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Coloca cor cinza no texto
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    //Coloca cor preta no texto
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spMed1.setAdapter(adMedidas);
        spMed2.setAdapter(adMedidas);
        spMed3.setAdapter(adMedidas);
        spMed4.setAdapter(adMedidas);
        spMed5.setAdapter(adMedidas);
        spMed6.setAdapter(adMedidas);
        spMed7.setAdapter(adMedidas);
        spMed8.setAdapter(adMedidas);
        spMed9.setAdapter(adMedidas);
        spMed10.setAdapter(adMedidas);
        spMed11.setAdapter(adMedidas);
        spMed12.setAdapter(adMedidas);
        spMed13.setAdapter(adMedidas);
        spMed14.setAdapter(adMedidas);
        spMed15.setAdapter(adMedidas);

    }

    //Valida informações e cadastra a receita
    public void CadastraReceita() {
        String erro = "";

        //Recupera nome da receita.
        etNome = (EditText) findViewById(R.id.etNome);

        //Recupera tempo de preparo da receita.
        etTempoPreparo = (EditText) findViewById(R.id.etTempoPreparo);

        //Recupera Ingredientes.
        ing1 = (EditText) findViewById(R.id.etIng1);
        ing2 = (EditText) findViewById(R.id.etIng2);
        ing3 = (EditText) findViewById(R.id.etIng3);
        ing4 = (EditText) findViewById(R.id.etIng4);
        ing5 = (EditText) findViewById(R.id.etIng5);
        ing6 = (EditText) findViewById(R.id.etIng6);
        ing7 = (EditText) findViewById(R.id.etIng7);
        ing8 = (EditText) findViewById(R.id.etIng8);
        ing9 = (EditText) findViewById(R.id.etIng9);
        ing10 = (EditText) findViewById(R.id.etIng10);
        ing11 = (EditText) findViewById(R.id.etIng11);
        ing12 = (EditText) findViewById(R.id.etIng12);
        ing13 = (EditText) findViewById(R.id.etIng13);
        ing14 = (EditText) findViewById(R.id.etIng14);
        ing15 = (EditText) findViewById(R.id.etIng15);

        //Recupera Quantidades
        qtd1 = (EditText) findViewById(R.id.etQtd1);
        qtd2 = (EditText) findViewById(R.id.etQtd2);
        qtd3 = (EditText) findViewById(R.id.etQtd3);
        qtd4 = (EditText) findViewById(R.id.etQtd4);
        qtd5 = (EditText) findViewById(R.id.etQtd5);
        qtd6 = (EditText) findViewById(R.id.etQtd6);
        qtd7 = (EditText) findViewById(R.id.etQtd7);
        qtd8 = (EditText) findViewById(R.id.etQtd8);
        qtd9 = (EditText) findViewById(R.id.etQtd9);
        qtd10 = (EditText) findViewById(R.id.etQtd10);
        qtd11 = (EditText) findViewById(R.id.etQtd11);
        qtd12 = (EditText) findViewById(R.id.etQtd12);
        qtd13 = (EditText) findViewById(R.id.etQtd13);
        qtd14 = (EditText) findViewById(R.id.etQtd14);
        qtd15 = (EditText) findViewById(R.id.etQtd15);

        //Recupera modo de preparo da receita.
        etModoPreparo = (EditText) findViewById(R.id.etModoPreparo);

        //Recupera dicas da receita.
        etDicas = (EditText) findViewById(R.id.etDicas);

        //Valida dados fornecidos
        if (etNome.getText().toString().trim().equals("")) {
            erro = "Preencha o nome!";
        } else {
            if (spCategorias.getSelectedItemPosition() == 0) {
                erro = "Preencha a categoria!";
            }else {
                if (etTempoPreparo.getText().toString().trim().equals("")) {
                    erro = "Preencha o tempo de preparo!";
                } else {
                    if (spPorcoes.getSelectedItemPosition() == 0) {
                        erro = "Preencha a quantidade de porções!";
                    } else {
                        if (etModoPreparo.getText().toString().trim().equals("")) {
                            erro = "Preencha o modo de preparo!";
                        } else {
                            if (imagemSelecionada == null) {
                                erro = "Selecione uma foto da sua receita!";
                            }
                        }
                    }
                }
            }
        }

        try {
            //Verifica se foi encontrado algum problema
            if (erro.equals("")) {
                JSONObject json = new JSONObject();
                json.put("Nome", etNome.getText().toString());
                json.put("TempoPreparo", etTempoPreparo.getText().toString());
                json.put("Porcoes", spPorcoes.getSelectedItem().toString());
                json.put("ModoPreparo",  etModoPreparo.getText().toString());
                json.put("Dicas", etDicas.getText().toString());
                json.put("Foto", Imagem.fotoEncode(Imagem.recuperaCaminho(imagemSelecionada, ActCadastroReceita.this)));
                json.put("idCategoria", ((Categoria)spCategorias.getSelectedItem()).getIdCategoria());
                json.put("idUsuario", (ActPrincipal.usuarioLogado.getIdUsuario()));

                JSONObject jsonA = new JSONObject();
                JSONObject jsonB = new JSONObject();
                jsonA.put("Cod", 0);

                pd = ProgressDialog.show(ActCadastroReceita.this, "", "Por favor, aguarde...", false);
                processos++;
                new RequisicaoAsyncTask().execute("InserirReceita", "0", json.toString());
                new RequisicaoAsyncTask().execute("RecuperarIdReceita", "0", jsonA.toString());

                if (!(ing1.getText().toString().trim().equals("")) && !(qtd1.getText().toString().trim().equals(""))
                        && spMed1.getSelectedItemPosition() > 0) {


                    jsonB.put("Nome", ing1.getText().toString());
                    jsonB.put("Quantidade", qtd1.getText().toString());
                    jsonB.put("UnidMedida", spMed1.getSelectedItem().toString());
                    jsonB.put("idReceita",  receitaId.get(0));

                    new RequisicaoAsyncTask().execute("InserirIngrediente", "0", jsonB.toString());

                } else {
                    if (!(ing2.getText().toString().trim().equals("")) && !(qtd2.getText().toString().trim().equals(""))
                            && spMed2.getSelectedItemPosition() > 0) {


                        jsonB.put("Nome", ing2.getText().toString());
                        jsonB.put("Quantidade", qtd2.getText().toString());
                        jsonB.put("UnidMedida", spMed2.getSelectedItem().toString());
                        jsonB.put("idReceita",  receitaId.get(0));

                        new RequisicaoAsyncTask().execute("InserirIngrediente", "0", jsonB.toString());
                    }else {
                        if (!(ing3.getText().toString().trim().equals("")) && !(qtd3.getText().toString().trim().equals(""))
                                && spMed3.getSelectedItemPosition() > 0) {


                            jsonB.put("Nome", ing3.getText().toString());
                            jsonB.put("Quantidade", qtd3.getText().toString());
                            jsonB.put("UnidMedida", spMed3.getSelectedItem().toString());
                            jsonB.put("idReceita",  receitaId);

                            new RequisicaoAsyncTask().execute("InserirIngrediente", "0", jsonB.toString());
                        } else {
                            if (!(ing4.getText().toString().trim().equals("")) && !(qtd4.getText().toString().trim().equals(""))
                                    && spMed4.getSelectedItemPosition() > 0) {


                                jsonB.put("Nome", ing4.getText().toString());
                                jsonB.put("Quantidade", qtd4.getText().toString());
                                jsonB.put("UnidMedida", spMed4.getSelectedItem().toString());
                                jsonB.put("idReceita",  receitaId);

                                new RequisicaoAsyncTask().execute("InserirIngrediente", "0", jsonB.toString());
                            } else {
                                if (!(ing5.getText().toString().trim().equals("")) && !(qtd5.getText().toString().trim().equals(""))
                                        && spMed5.getSelectedItemPosition() > 0) {


                                    jsonB.put("Nome", ing5.getText().toString());
                                    jsonB.put("Quantidade", qtd5.getText().toString());
                                    jsonB.put("UnidMedida", spMed5.getSelectedItem().toString());
                                    jsonB.put("idReceita",  receitaId);

                                    new RequisicaoAsyncTask().execute("InserirIngrediente", "0", jsonB.toString());
                                } else {
                                    if (!(ing6.getText().toString().trim().equals("")) && !(qtd6.getText().toString().trim().equals(""))
                                            && spMed6.getSelectedItemPosition() > 0) {


                                        jsonB.put("Nome", ing6.getText().toString());
                                        jsonB.put("Quantidade", qtd6.getText().toString());
                                        jsonB.put("UnidMedida", spMed6.getSelectedItem().toString());
                                        jsonB.put("idReceita",  receitaId);

                                        new RequisicaoAsyncTask().execute("InserirIngrediente", "0", jsonB.toString());
                                    } else {
                                        if (!(ing7.getText().toString().trim().equals("")) && !(qtd7.getText().toString().trim().equals(""))
                                                && spMed7.getSelectedItemPosition() > 0) {


                                            jsonB.put("Nome", ing7.getText().toString());
                                            jsonB.put("Quantidade", qtd7.getText().toString());
                                            jsonB.put("UnidMedida", spMed7.getSelectedItem().toString());
                                            jsonB.put("idReceita",  receitaId);

                                            new RequisicaoAsyncTask().execute("InserirIngrediente", "0", jsonB.toString());
                                        } else{
                                            if (!(ing8.getText().toString().trim().equals("")) && !(qtd8.getText().toString().trim().equals(""))
                                                    && spMed8.getSelectedItemPosition() > 0) {


                                                jsonB.put("Nome", ing8.getText().toString());
                                                jsonB.put("Quantidade", qtd8.getText().toString());
                                                jsonB.put("UnidMedida", spMed8.getSelectedItem().toString());
                                                jsonB.put("idReceita",  receitaId);

                                                new RequisicaoAsyncTask().execute("InserirIngrediente", "0", jsonB.toString());
                                            } else{
                                                if (!(ing9.getText().toString().trim().equals("")) && !(qtd9.getText().toString().trim().equals(""))
                                                        && spMed9.getSelectedItemPosition() > 0) {


                                                    jsonB.put("Nome", ing9.getText().toString());
                                                    jsonB.put("Quantidade", qtd9.getText().toString());
                                                    jsonB.put("UnidMedida", spMed9.getSelectedItem().toString());
                                                    jsonB.put("idReceita",  receitaId);

                                                    new RequisicaoAsyncTask().execute("InserirIngrediente", "0", jsonB.toString());
                                                } else{
                                                    if (!(ing10.getText().toString().trim().equals("")) && !(qtd10.getText().toString().trim().equals(""))
                                                            && spMed10.getSelectedItemPosition() > 0) {


                                                        jsonB.put("Nome", ing10.getText().toString());
                                                        jsonB.put("Quantidade", qtd10.getText().toString());
                                                        jsonB.put("UnidMedida", spMed10.getSelectedItem().toString());
                                                        jsonB.put("idReceita",  receitaId);

                                                        new RequisicaoAsyncTask().execute("InserirIngrediente", "0", jsonB.toString());
                                                    } else{
                                                        if (!(ing11.getText().toString().trim().equals("")) && !(qtd11.getText().toString().trim().equals(""))
                                                                && spMed11.getSelectedItemPosition() > 0) {


                                                            jsonB.put("Nome", ing11.getText().toString());
                                                            jsonB.put("Quantidade", qtd11.getText().toString());
                                                            jsonB.put("UnidMedida", spMed11.getSelectedItem().toString());
                                                            jsonB.put("idReceita",  receitaId);

                                                            new RequisicaoAsyncTask().execute("InserirIngrediente", "0", jsonB.toString());
                                                        } else {
                                                            if (!(ing12.getText().toString().trim().equals("")) && !(qtd12.getText().toString().trim().equals(""))
                                                                    && spMed12.getSelectedItemPosition() > 0) {


                                                                jsonB.put("Nome", ing12.getText().toString());
                                                                jsonB.put("Quantidade", qtd12.getText().toString());
                                                                jsonB.put("UnidMedida", spMed12.getSelectedItem().toString());
                                                                jsonB.put("idReceita",  receitaId);

                                                                new RequisicaoAsyncTask().execute("InserirIngrediente", "0", jsonB.toString());
                                                            } else {
                                                                if (!(ing13.getText().toString().trim().equals("")) && !(qtd13.getText().toString().trim().equals(""))
                                                                        && spMed13.getSelectedItemPosition() > 0) {


                                                                    jsonB.put("Nome", ing13.getText().toString());
                                                                    jsonB.put("Quantidade", qtd13.getText().toString());
                                                                    jsonB.put("UnidMedida", spMed13.getSelectedItem().toString());
                                                                    jsonB.put("idReceita",  receitaId);

                                                                    new RequisicaoAsyncTask().execute("InserirIngrediente", "0", jsonB.toString());
                                                                } else{
                                                                    if (!(ing14.getText().toString().trim().equals("")) && !(qtd14.getText().toString().trim().equals(""))
                                                                            && spMed14.getSelectedItemPosition() > 0) {


                                                                        jsonB.put("Nome", ing14.getText().toString());
                                                                        jsonB.put("Quantidade", qtd14.getText().toString());
                                                                        jsonB.put("UnidMedida", spMed14.getSelectedItem().toString());
                                                                        jsonB.put("idReceita",  receitaId);

                                                                        new RequisicaoAsyncTask().execute("InserirIngrediente", "0", jsonB.toString());
                                                                    } else{
                                                                        if (!(ing15.getText().toString().trim().equals("")) && !(qtd15.getText().toString().trim().equals(""))
                                                                                && spMed15.getSelectedItemPosition() > 0) {


                                                                            jsonB.put("Nome", ing15.getText().toString());
                                                                            jsonB.put("Quantidade", qtd15.getText().toString());
                                                                            jsonB.put("UnidMedida", spMed15.getSelectedItem().toString());
                                                                            jsonB.put("idReceita",  receitaId);

                                                                            new RequisicaoAsyncTask().execute("InserirIngrediente", "0", jsonB.toString());
                                                                        } else{
                                                                            Toast.makeText(ActCadastroReceita.this, "Preencha os Ingredientes corretamente!", Toast.LENGTH_LONG).show();
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            } else {
                Toast.makeText(ActCadastroReceita.this, erro, Toast.LENGTH_LONG).show();
            }
        }catch (Exception ex){
            Log.e("Erro", ex.getMessage());
            Toast.makeText(ActCadastroReceita.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
        }
    }

    //Verifica se o aplicativo tem permissão para acessar o armazenamento de arquivos
    @TargetApi(Build.VERSION_CODES.M)
    public void verificaPermissao(){
        if(ContextCompat.checkSelfPermission(ActCadastroReceita.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){

            //Verifica se o usuário selecionou a opções de não perguntar novamente.
            if(ActivityCompat.shouldShowRequestPermissionRationale(ActCadastroReceita.this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                alerta.show();
            }else{
                ActivityCompat.requestPermissions(ActCadastroReceita.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_EXTERNAL_STORAGE_PERMISSIONS_REQUEST);
            }
        }else{
            ActivityCompat.requestPermissions(ActCadastroReceita.this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_PERMISSIONS_REQUEST);

            //Abre tela para seleção da imagem
            startActivityForResult(selecionarImagem , 1);
        }
    }

    // Callback da requisição de permissão
    @Override
    public void onRequestPermissionsResult(int codigoRequisicao, String permissoes[], int[] resultados) {
        // Verifica se esse retorno de resposta é referente a requisição de permissão da CAMERA
        switch (codigoRequisicao) {
            case READ_EXTERNAL_STORAGE_PERMISSIONS_REQUEST: {
                if (resultados.length > 0
                        && resultados[0] == PackageManager.PERMISSION_GRANTED) {

                    //Abre tela para seleção da imagem
                    startActivityForResult(selecionarImagem, 1);

                } else {

                    alerta.show();
                }
                return;
            }
        }
    }

    //Recebe a resposta da seleção de imagem.
    @Override
    protected void onActivityResult(int codigoRequisicao, int codigoResultado, Intent imagem) {
        super.onActivityResult(codigoRequisicao, codigoResultado, imagem);
        if(codigoRequisicao == 1 && codigoResultado == RESULT_OK){
            imagemSelecionada = imagem.getData();
            Picasso.with(ActCadastroReceita.this).load(imagemSelecionada).transform(new TransformacaoCirculo()).into(ivFoto);
        }
    }

    private class RequisicaoAsyncTask extends AsyncTask<String, Void, JSONArray> {

        private String metodo;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected JSONArray doInBackground(String... params) {
            JSONArray resultado = new JSONArray();

            try {
                //Recupera parâmetros e realiza a requisição
                metodo = params[0];
                int id = Integer.parseInt(params[1]);
                String conteudo = params[2];

                //Chama método da API
                resultado = Requisicao.chamaMetodo(metodo, id, conteudo);

            } catch (Exception e) {
                Log.e("Erro", e.getMessage());
                Toast.makeText(ActCadastroReceita.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }
            return resultado;
        }

        @Override
        protected void onPostExecute(JSONArray resultado) {
            try {
                //Verifica se foi obtido algum resultado
                if(resultado.length() == 0){
                    Toast.makeText(ActCadastroReceita.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                }else{

                    //Verifica se o objeto retornado foi uma mensagem ou um objeto
                    JSONObject json = resultado.getJSONObject(0);
                    if(Mensagem.isMensagem(json)){
                        Mensagem msg = Mensagem.jsonToMensagem(json);
                        Toast.makeText(ActCadastroReceita.this, msg.getMensagem(), Toast.LENGTH_SHORT).show();

                        if(metodo.trim().equals("InserirReceita") && msg.getCodigo() == 4){
                            Intent tela = new Intent(ActCadastroReceita.this,ActPrincipal.class);
                            startActivity(tela);
                        }

                    }else{
                        //Verifica qual foi o método chamado
                        if(metodo.trim().equals("ListarCategorias")) {
                            //Recupera categorias
                            for(int i=0;i<resultado.length();i++){
                                categorias.add(Categoria.jsonToCategoria(resultado.getJSONObject(i)));
                            }
                        }else if(metodo.trim().equals("RecuperarIdReceita")){
                            //Recupera ID Receita
                            for(int i=0;i<resultado.length();i++){
                                receitaId.add(resultado.toString());
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
                Log.e("Erro", e.getMessage());
                Toast.makeText(ActCadastroReceita.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }

            processos--;
            if(processos == 0) {
                pd.dismiss();
            }
        }
    }
}
