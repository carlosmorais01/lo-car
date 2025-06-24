package entities;

import enums.Sexo;
import java.io.Serializable; // Importar Serializable
import java.time.LocalDateTime;

public class Funcionario extends Pessoa implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private static int ultimoId = 0;

    public Funcionario(String nome, String cpf, String telefone, String email, String senha, Endereco endereco, LocalDateTime dataNascimento, Sexo sexo, String caminhoFoto) {
        super(nome, cpf, telefone, email, senha, endereco, dataNascimento, sexo, caminhoFoto);
        this.id = ++ultimoId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}