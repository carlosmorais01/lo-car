package entities;

import enums.Sexo;
import java.io.Serializable; // Importar Serializable
import java.time.LocalDateTime;

public class Funcionario extends Pessoa implements Serializable { // Adicionar implements Serializable
    private static final long serialVersionUID = 1L; // Adicionar serialVersionUID
    private int id;
    private static int ultimoId = 0; // Cuidado com static para serialização se o ID for persistido

    public Funcionario(String nome, String cpf, String telefone, String email, String senha, Endereco endereco, LocalDateTime dataNascimento, Sexo sexo) {
        super(nome, cpf, telefone, email, senha, endereco, dataNascimento, sexo);
        // Para garantir IDs únicos após deserialização, o ideal seria carregar ultimoId do arquivo ou de um gerador de ID persistente
        // Por simplicidade, para este projeto POO, manter assim pode ser aceitável, mas em produção geraria IDs repetidos.
        this.id = ++ultimoId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}