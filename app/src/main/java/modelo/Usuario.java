package modelo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Aline on 20/09/2017.
 */

public class Usuario {
    private int idUsuario;
    private String nome;
    private String email;

    // Construtor  que inicializa todas as varaveis da Classe Usuario
    public Usuario(int idUsuario, String nome, String email) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.email = email;
    }
// Inicializa os Getters e Setters das variaveis privadas
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
//Final dos Getters e Setters//

    public static Usuario jsonToUsuario(JSONObject objeto) throws JSONException {
        if(objeto == null){
            return null;
        }else {
            Usuario usuario = new Usuario(objeto.getInt("idUsuario"),objeto.getString("Nome"),objeto.getString("Email"));
            return usuario;
        }
    }

    //
    public JSONObject usuarioToJson() throws JSONException {
        JSONObject objeto = new JSONObject();
        objeto.put("idUsuario", this.getIdUsuario());
        objeto.put("Nome", this.getNome());
        objeto.put("Email", this.getEmail());

        return objeto;
    }
}
