package modelo;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by  Aline Dominique on 20/09/2017.
 */

public class Ingrediente {
    private int idIngrediente;
    private String nome;
    private String quantidade;
    private String unidMedida;
    private Receita receita;

    public Ingrediente(int idIngrediente, String nome, String quantidade, String unidMedida, Receita receita) {
        this.idIngrediente = idIngrediente;
        this.nome = nome;
        this.quantidade = quantidade;
        this.unidMedida = unidMedida;
        this.receita = receita;
    }

    public int getIdIngrediente() {
        return idIngrediente;
    }

    public void setIdIngrediente(int idIngrediente) {
        this.idIngrediente = idIngrediente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(String quantidade) {
        this.quantidade = quantidade;
    }

    public String getUnidMedida() {
        return unidMedida;
    }

    public void setUnidMedida(String unidMedida) {
        this.unidMedida = unidMedida;
    }

    public Receita getReceita() {
        return receita;
    }

    public void setReceita(Receita receita) {
        this.receita = receita;
    }

    public static Ingrediente jsonToIngrediente (JSONObject objeto) throws JSONException {
        if(objeto == null){
            return null;
        }
        else {
            Categoria categoria = new Categoria(objeto.getInt("idCategoria"),objeto.getString("NomeCategoria"));
            Usuario usuario = new Usuario(objeto.getInt("idUsuario"),objeto.getString("NomeUsuario"),objeto.getString("Email"));

            Receita receita = new Receita(objeto.getInt("idReceita"),objeto.getString("NomeReceita"),objeto.getString("TempoPreparo")
                    ,objeto.getString("Porcoes"),objeto.getString("ModoPreparo"),
                    objeto.getString("Dicas"),objeto.getString("Foto"),categoria,usuario);

            Ingrediente ingrediente = new Ingrediente(objeto.getInt("idIngrediente"), objeto.getString("Nome"),
                    objeto.getString("Quantidade"),objeto.getString("UnidMedida"), receita);
            return ingrediente;
        }
    }

    public JSONObject ingredienteToJson() throws JSONException {
        JSONObject objeto = new JSONObject();
        objeto.put("idIngrediente",this.getIdIngrediente());
        objeto.put("Nome",this.getNome());
        objeto.put("Quantidade",this.getQuantidade());
        objeto.put("UnidMedida",this.getUnidMedida());
        objeto.put("idReceita",this.receita.getIdReceita());
        objeto.put("NomeReceita",this.receita.getNome());
        objeto.put("TempoPreparo",this.receita.getTempoPreparo());
        objeto.put("Porcoes",this.receita.getPorcoes());
        objeto.put("ModoPreparo",this.receita.getModoPreparo());
        objeto.put("Dicas",this.receita.getDicas());
        objeto.put("Foto",this.receita.getFoto());
        objeto.put("idCategoria",this.receita.getCategoria().getIdCategoria());
        objeto.put("NomeCategoria",this.receita.getCategoria().getIdCategoria());
        objeto.put("idUsuario",this.receita.getUsuario().getIdUsuario());
        objeto.put("NomeUsuario",this.receita.getUsuario().getNome());
        objeto.put("Email",this.receita.getUsuario().getEmail());

        return objeto;
    }
}