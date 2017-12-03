package modelo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Aline Dominique on 26/11/2017.
 */

public class Foto {

    private int idFoto;
    private String caminho;
    private Receita receita;

    public Foto(int idFoto, String caminho, Receita receita) {
        this.idFoto = idFoto;
        this.caminho = caminho;
        this.receita = receita;
    }

    public int getIdFoto() {
        return idFoto;
    }

    public void setIdFoto(int idFoto) {
        this.idFoto = idFoto;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public Receita getReceita() {
        return receita;
    }

    public void setReceita(Receita receita) {
        this.receita = receita;
    }

    public static Receita jsonToFoto (JSONObject objeto) throws JSONException {
        if(objeto == null){
            return null;
        }
        else {
            Categoria categoria = new Categoria(objeto.getInt("idCategoria"),objeto.getString("NomeCategoria"));
            Usuario usuario = new Usuario(objeto.getInt("idUsuario"),objeto.getString("NomeUsuario"),objeto.getString("Email"));

            Receita receita = new Receita(objeto.getInt("idReceita"),objeto.getString("NomeReceita"),objeto.getString("TempoPreparo")
                    ,objeto.getString("Porcoes"),objeto.getString("ModoPreparo"),
                    objeto.getString("Dicas"),objeto.getString("Foto"),categoria,usuario);

            Foto foto = new Foto(objeto.getInt("idFoto"),objeto.getString("Caminho"),receita);
            return receita;
        }
    }

    public JSONObject FotoToJson() throws JSONException {
        JSONObject objeto = new JSONObject();
        objeto.put("idFoto",this.getIdFoto());
        objeto.put("Caminho",this.getCaminho());
        objeto.put("idReceita",this.receita.getIdReceita());

        return objeto;
    }

}
