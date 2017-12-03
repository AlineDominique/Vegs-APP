package com.vegs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import controlador.GerenciadorSharedPreferences;

/**
 * Created by Aline Dominique on 18/11/2017.
 */
public class ActSobre extends AppCompatActivity
{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);

        //Configura e carrega toolbar
        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        t.setTitleTextColor(ContextCompat.getColor(this, R.color.fontColorPrimary));
        setSupportActionBar(t);
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
                intentA.setData(Uri.parse(getString(R.string.Manual)));//Mudar para o arquivo de ajuda do APP Vegs
                startActivity(intentA);
                return true;
            case R.id.menuSobre:
                return true;
            case R.id.menuSair:
                //Limpa SharedPreferences
                GerenciadorSharedPreferences.setEmail(getBaseContext(), "");

                //Chama tela de login
                Intent principal = new Intent(ActSobre.this, ActLogin.class);
                startActivity(principal);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
