package entities;

import entities.enums.Sexo;

import java.time.LocalDateTime;

public class Funcionario extends Pessoa {
    private int id;
    private static int ultimoId = 0;

    public Funcionario(String nome, String cpf, String telefone, String email, String senha, Endereco endereco, LocalDateTime dataNascimento, Sexo sexo) {
        super(nome, cpf, telefone, email, senha, endereco, dataNascimento, sexo);
        this.id = ++ultimoId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
