package com.vegs;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import controlador.GerenciadorSharedPreferences;
import controlador.Requisicao;
import controlador.TransformacaoCirculo;
import modelo.Favorito;
import modelo.Mensagem;
import modelo.Receita;

/**
 * Created by Aline Dominique on 02/12/2017.
 */
public class ActFavorito extends AppCompatActivity {

    private Favorito favorito;
    private String solicitante;
    private AlertDialog.Builder dialogo;
    private ProgressDialog pd;
    private int processos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorito);

        Intent i_Soc = getIntent();
        solicitante = i_Soc.getStringExtra("Solicitante");

        //Configura e carrega toolbar
        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        t.setTitleTextColor(ContextCompat.getColor(this, R.color.fontColorPrimary));
        t.setLogo(R.drawable.ic_restaurant);
        setSupportActionBar(t);

        try {
            Intent i = getIntent();
            JSONObject json = new JSONObject(i.getStringExtra("Favorito"));
            favorito = Favorito.jsonToFavorito(json);
        }catch (Exception ex){
            Log.e("Erro", ex.getMessage());
            Toast.makeText(ActFavorito.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
        }

        //Carrega informações da receita
        ImageView ivFavorito = (ImageView) findViewById(R.id.ivFavorito);
        Picasso.with(ActFavorito.this).load(favorito.getReceita().getFoto()).transform(new TransformacaoCirculo()).into(ivFavorito);

        TextView tvNome = (TextView) findViewById(R.id.tvNome);
        tvNome.setText(favorito.getReceita().getNome());

        TextView tvCategoria = (TextView) findViewById(R.id.tvCategoria);
        tvCategoria.setText(favorito.getReceita().getCategoria().getNome());

        TextView tvTempoPreparo = (TextView) findViewById(R.id.tvTempoPreparo);
        tvTempoPreparo.setText(favorito.getReceita().getTempoPreparo());

        TextView tvPorcoes = (TextView) findViewById(R.id.tvPorcoes);
        tvPorcoes.setText(favorito.getReceita().getPorcoes());

        TextView tvModoPreparo = (TextView) findViewById(R.id.tvModoPreparo);
        tvModoPreparo.setText(favorito.getReceita().getModoPreparo());

        TextView tvDicas = (TextView) findViewById(R.id.tvDicas);
        tvDicas.setText(favorito.getReceita().getDicas());

        TextView tvAutor = (TextView) findViewById(R.id.tvAutor);
        tvAutor.setText(favorito.getReceita().getUsuario().getNome());
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
                Intent intent1 = new Intent(ActFavorito.this, ActSobre.class);
                startActivity(intent1);
                return true;
            case R.id.menuSair:
                //Limpa SharedPreferences
                GerenciadorSharedPreferences.setEmail(getBaseContext(),"");
                //Chama tela de login
                Intent principal = new Intent(ActFavorito.this, ActLogin.class);
                startActivity(principal);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
