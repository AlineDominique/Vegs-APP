package modelo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by  Aline Dominique on 06/09/2017.
 */

public class Receita {
    private int idReceita;
    private String nome;
    private String tempoPreparo;
    private String porcoes;
    private String modoPreparo;
    private String dicas;
    private String foto;
    private Categoria categoria;
    private Usuario usuario;

    public Receita(int idReceita, String nome, String tempoPreparo, String porcoes, String modoPreparo, String dicas, String foto, Categoria categoria, Usuario usuario) {
        this.idReceita = idReceita;
        this.nome = nome;
        this.tempoPreparo = tempoPreparo;
        this.porcoes = porcoes;
        this.modoPreparo = modoPreparo;
        this.dicas = dicas;
        this.foto = foto;
        this.categoria = categoria;
        this.usuario = usuario;
    }

    public int getIdReceita() {
        return idReceita;
    }

    public void setIdReceita(int idReceita) {
        this.idReceita = idReceita;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTempoPreparo() {
        return tempoPreparo;
    }

    public void setTempoPreparo(String tempoPreparo) {
        this.tempoPreparo = tempoPreparo;
    }

    public String getPorcoes() {
        return porcoes;
    }

    public void setPorcoes(String porcoes) {
        this.porcoes = porcoes;
    }

    public String getModoPreparo() {
        return modoPreparo;
    }

    public void setModoPreparo(String modoPreparo) {
        this.modoPreparo = modoPreparo;
    }

    public String getDicas() {
        return dicas;
    }

    public void setDicas(String dicas) {
        this.dicas = dicas;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public static Receita jsonToReceita (JSONObject objeto) throws JSONException {
        if(objeto == null){
            return null;
        }
        else {
            Categoria categoria = new Categoria(objeto.getInt("idCategoria"),objeto.getString("NomeCategoria"));
            Usuario usuario = new Usuario(objeto.getInt("idUsuario"),objeto.getString("NomeUsuario"),objeto.getString("Email"));

            Receita receita = new Receita(objeto.getInt("idReceita"),objeto.getString("Nome"),objeto.getString("TempoPreparo")
                    ,objeto.getString("Porcoes"),objeto.getString("ModoPreparo"),
                    objeto.getString("Dicas"),objeto.getString("Foto"),categoria,usuario);
            return receita;
        }
    }

    public JSONObject receitaToJson() throws JSONException {
        JSONObject objeto = new JSONObject();
        objeto.put("idReceita",this.getIdReceita());
        objeto.put("Nome",this.getNome());
        objeto.put("TempoPreparo",this.getTempoPreparo());
        objeto.put("Porcoes",this.getPorcoes());
        objeto.put("ModoPreparo",this.getModoPreparo());
        objeto.put("Dicas",this.getDicas());
        objeto.put("Foto",this.getFoto());
        objeto.put("idCategoria",this.categoria.getIdCategoria());
        objeto.put("NomeCategoria",this.categoria.getNome());
        objeto.put("idUsuario",this.usuario.getIdUsuario());
        objeto.put("NomeUsuario",this.usuario.getNome());
        objeto.put("Email",this.usuario.getEmail());

        return objeto;
    }

    @Override
    public String toString() {
        return nome;
    }
}