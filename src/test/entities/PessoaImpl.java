package entities;

import enums.Sexo;
import java.time.LocalDateTime;

// Classe de implementação concreta para testar a classe abstrata Pessoa
public class PessoaImpl extends Pessoa {

    public PessoaImpl(String nome, String cpf, String telefone, String email, String senha, Endereco endereco, LocalDateTime dataNascimento, Sexo sexo, String caminhoFoto) {
        super(nome, cpf, telefone, email, senha, endereco, dataNascimento, sexo, caminhoFoto);
    }
}
