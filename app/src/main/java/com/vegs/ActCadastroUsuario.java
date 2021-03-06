package com.vegs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import controlador.GerenciadorSharedPreferences;
import controlador.Requisicao;
import modelo.Mensagem;

/**
 * Created by Aline Dominique on 18/11/2017.
 */
public class ActCadastroUsuario extends AppCompatActivity {
    private TextView etNome;
    private Button btInscrever;
    private String nome;
    private String email;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        //Recupera os parâmetros passados na chamada dessa tela
        Intent i = getIntent();
        nome = i.getStringExtra("Nome");
        email = i.getStringExtra("Email");

        //Recupera objetos da tela
        etNome = (TextView)findViewById(R.id.etNome);
        btInscrever = (Button)findViewById(R.id.btCadastrar);

        //Seta valor default para a caixa de nome baseado no nome utilizado na conta do google
        etNome.setText(nome);

        //Cadastra usuário
        btInscrever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Verifica se todas as informações foram fornecidas
                if(etNome.getText().toString().trim().equals("")){
                    Toast.makeText(getBaseContext(), "Preencha todas as informações!", Toast.LENGTH_LONG).show();
                }else{
                    try {
                        //Gera objeto para ser autenticado pela API.
                        JSONObject usuarioJson = new JSONObject();
                        usuarioJson.put("Nome", etNome.getText().toString().trim());
                        usuarioJson.put("Email", email);

                        //Insere usuário na API
                        new RequisicaoAsyncTask().execute("InserirUsuario", "0", usuarioJson.toString());

                    }catch (Exception ex){
                        Log.e("Erro", ex.getMessage());
                        Toast.makeText(ActCadastroUsuario.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    private class RequisicaoAsyncTask extends AsyncTask<String, Void, JSONArray> {

        private String metodo;

        @Override
        protected void onPreExecute() {
            //Faz algo antes de executar o procedimento assincrono
            pd = ProgressDialog.show(ActCadastroUsuario.this, "", "Por favor, aguarde...", false);
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
                Toast.makeText(ActCadastroUsuario.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }
            return resultado;
        }

        @Override
        protected void onPostExecute(JSONArray resultado) {
            pd.dismiss();

            try {
                //Verifica se foi obtido algum resultado
                if (resultado.length() == 0) {
                    Toast.makeText(ActCadastroUsuario.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                } else {
                    //Verifica se o objeto retornado é do tipo mensagem
                    JSONObject json = resultado.getJSONObject(0);
                    if (Mensagem.isMensagem(json)) {
                        Mensagem msg = Mensagem.jsonToMensagem(json);

                        //Verifica se o usuário foi inserido com sucesso
                        if (msg.getCodigo() == 4) {
                            //Salva usuário na sharedpreferences
                            GerenciadorSharedPreferences.setEmail(getBaseContext(), email);

                            //Chama tela principal
                            Intent principal = new Intent(ActCadastroUsuario.this, ActPrincipal.class);
                            startActivity(principal);
                        } else {
                            Toast.makeText(ActCadastroUsuario.this, msg.getMensagem(), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(ActCadastroUsuario.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                Log.e("Erro", e.getMessage());
                Toast.makeText(ActCadastroUsuario.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }

        }
    }

}
