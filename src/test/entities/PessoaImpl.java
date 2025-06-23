package entities;

import enums.Sexo;
import java.time.LocalDateTime;

public class PessoaImpl extends Pessoa {

    public PessoaImpl(String nome, String cpf, String telefone, String email, String senha, Endereco endereco, LocalDateTime dataNascimento, Sexo sexo, String caminhoFoto) {
        super(nome, cpf, telefone, email, senha, endereco, dataNascimento, sexo, caminhoFoto);
    }
}
