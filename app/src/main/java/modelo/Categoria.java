package modelo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Aline Dominique on 04/11/2017.
 */
public class Categoria {
    private int idCategoria;
    private String nome;

    public Categoria(int idCategoria, String nome) {
        this.idCategoria = idCategoria;
        this.nome = nome;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public static Categoria jsonToCategoria(JSONObject objeto) throws JSONException {
        if(objeto == null){
            return null;
        }else {
            Categoria categoria = new Categoria(objeto.getInt("idCategoria"),objeto.getString("Nome"));
            return categoria;
        }
    }

    public JSONObject CategoriaToJson() throws JSONException {
        JSONObject objeto = new JSONObject();
        objeto.put("idCategoria",this.getIdCategoria());
        objeto.put("Nome",this.getNome());
        return objeto;
    }

    @Override
    public String toString() {
        return nome;
    }
}
