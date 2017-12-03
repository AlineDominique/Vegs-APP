package modelo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Aline Dominique on 27/11/2016.
 */
public class Favorito {
    private int idFavorito;
    private Receita receita;
    private Usuario usuario;


    public Favorito(int idFavorito, Receita receita, Usuario usuario) {
        this.idFavorito = idFavorito;
        this.receita = receita;
        this.usuario = usuario;
    }


    public int getIdFavorito() {
        return idFavorito;
    }

    public void setIdFavorito(int idFavorito) {
        this.idFavorito = idFavorito;
    }

    public Receita getReceita() {
        return receita;
    }

    public void setReceita(Receita receita) {
        this.receita = receita;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public static Favorito jsonToFavorito(JSONObject objeto) throws JSONException {
        if(objeto == null){
            return null;
        }else {

            Categoria categoria = new Categoria(objeto.getInt("idCategoria"),"");

            Usuario usuario = new Usuario(objeto.getInt("idUsuario"),objeto.getString("NomeUsuario"),objeto.getString("Email"));

            Receita receita = new Receita(objeto.getInt("idReceita"),objeto.getString("Nome"),objeto.getString("TempoPreparo")
                    ,objeto.getString("Porcoes"),objeto.getString("ModoPreparo"),
                    objeto.getString("Dicas"),objeto.getString("Foto"),categoria,usuario);

            Favorito favorito = new Favorito(objeto.getInt("idFavorito"), receita, usuario);

            return favorito;
        }
    }

    public JSONObject favoritoToJson() throws JSONException {
        JSONObject objeto = new JSONObject();
        objeto.put("idFavorito",this.getIdFavorito());
        objeto.put("idReceita",this.receita.getIdReceita());
        objeto.put("Nome",this.receita.getNome());
        objeto.put("TempoPreparo",this.receita.getTempoPreparo());
        objeto.put("Porcoes",this.receita.getPorcoes());
        objeto.put("ModoPreparo",this.receita.getModoPreparo());
        objeto.put("Dicas",this.receita.getDicas());
        objeto.put("Foto",this.receita.getFoto());
        objeto.put("idCategoria",this.receita.getCategoria().getIdCategoria());;
        objeto.put("idUsuario",this.usuario.getIdUsuario());
        objeto.put("NomeUsuario",this.usuario.getNome());
        objeto.put("Email",this.usuario.getEmail());
        return objeto;
    }

}
