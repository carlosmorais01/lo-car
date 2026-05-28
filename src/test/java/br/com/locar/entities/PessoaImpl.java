package br.com.locar.entities;

import br.com.locar.core.entities.enums.Sexo;
import br.com.locar.core.entities.Endereco;
import br.com.locar.core.entities.Pessoa;

import java.time.LocalDateTime;

public class PessoaImpl extends Pessoa {

    public PessoaImpl(String nome, String cpf, String telefone, String email, String senha, Endereco endereco, LocalDateTime dataNascimento, Sexo sexo, String caminhoFoto) {
        super(nome, cpf, telefone, email, senha, endereco, dataNascimento, sexo, caminhoFoto);
    }
}
