package com.vegs;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import controlador.GerenciadorSharedPreferences;
import controlador.Requisicao;
import controlador.TransformacaoCirculo;
import modelo.Receita;
import modelo.Mensagem;

/**
 * Created by Aline Dominique on 02/12/2017.
 */
public class ActReceita extends AppCompatActivity {

    private Receita receita;
    private Button btAdicionarFavorito;
    private String solicitante;
    private AlertDialog.Builder dialogo;
    private ProgressDialog pd;
    private int processos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receita);

        Intent i_Soc = getIntent();
        solicitante = i_Soc.getStringExtra("Solicitante");

        //Configura e carrega toolbar
        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        t.setTitleTextColor(ContextCompat.getColor(this, R.color.fontColorPrimary));
        t.setLogo(R.drawable.ic_restaurant);
        setSupportActionBar(t);

        try {
            Intent i = getIntent();
            JSONObject json = new JSONObject(i.getStringExtra("Receita"));
            receita = Receita.jsonToReceita(json);
        }catch (Exception ex){
            Log.e("Erro", ex.getMessage());
            Toast.makeText(ActReceita.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
        }

        //Carrega informações da receita
        ImageView ivReceita = (ImageView) findViewById(R.id.ivReceita);
        Picasso.with(ActReceita.this).load(receita.getFoto()).transform(new TransformacaoCirculo()).into(ivReceita);

        TextView tvNome = (TextView) findViewById(R.id.tvNome);
        tvNome.setText(receita.getNome());

        TextView tvCategoria = (TextView) findViewById(R.id.tvCategoria);
        tvCategoria.setText(receita.getCategoria().getNome());

        TextView tvTempoPreparo = (TextView) findViewById(R.id.tvTempoPreparo);
        tvTempoPreparo.setText(receita.getTempoPreparo());

        TextView tvPorcoes = (TextView) findViewById(R.id.tvPorcoes);
        tvPorcoes.setText(receita.getPorcoes());

        TextView tvModoPreparo = (TextView) findViewById(R.id.tvModoPreparo);
        tvModoPreparo.setText(receita.getModoPreparo());

        TextView tvDicas = (TextView) findViewById(R.id.tvDicas);
        tvDicas.setText(receita.getDicas());

        TextView tvAutor = (TextView) findViewById(R.id.tvAutor);
        tvAutor.setText(receita.getUsuario().getNome());

        //Adiciona evento de click no botão de comunicar localização
        btAdicionarFavorito = (Button) findViewById(R.id.btAdicionarFavorito);
        btAdicionarFavorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                try
                {
                    //Mostra janela de progresso
                    pd = ProgressDialog.show(ActReceita.this, "", "Por favor, aguarde...", false);
                    processos++;

                    //Monta JSON
                    JSONObject json = new JSONObject();
                    json.put("idUsuario", (ActPrincipal.usuarioLogado.getIdUsuario()));
                    json.put("idReceita", receita.getIdReceita());

                    //Salva receita como favorito
                    new RequisicaoAsyncTask().execute("InserirFavorito","0",json.toString());
                }
                catch(Exception ex)
                {
                    Log.e("Erro", ex.getMessage());
                    Toast.makeText(ActReceita.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                Intent intent1 = new Intent(ActReceita.this, ActSobre.class);
                startActivity(intent1);
                return true;
            case R.id.menuSair:
                //Limpa SharedPreferences
                GerenciadorSharedPreferences.setEmail(getBaseContext(),"");
                //Chama tela de login
                Intent principal = new Intent(ActReceita.this, ActLogin.class);
                startActivity(principal);

                return true;
            default:
                return super.onOptionsItemSelected(item);
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
                Toast.makeText(ActReceita.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }
            return resultado;
        }

        @Override
        protected void onPostExecute(JSONArray resultado) {
            try {
                //Verifica se foi obtido algum resultado
                if(resultado.length() == 0){
                    Toast.makeText(ActReceita.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                }else{

                    //Verifica se o objeto retornado foi uma mensagem ou um objeto
                    JSONObject json = resultado.getJSONObject(0);
                    if(Mensagem.isMensagem(json)){
                        Mensagem msg = Mensagem.jsonToMensagem(json);

                        if(metodo == "InserirFavorito" && msg.getCodigo() == 11){
                            Toast.makeText(ActReceita.this, msg.getMensagem(), Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(ActReceita.this, msg.getMensagem(), Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        //Se o retorno não for mensagem
                    }
                }
            }
            catch (Exception e) {
                Log.e("Erro", e.getMessage());
                Toast.makeText(ActReceita.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }

            processos--;
            if(processos == 0) {
                pd.dismiss();
            }
        }
    }

}
